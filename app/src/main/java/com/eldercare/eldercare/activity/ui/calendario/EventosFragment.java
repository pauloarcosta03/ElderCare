package com.eldercare.eldercare.activity.ui.calendario;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eldercare.eldercare.R;
import com.eldercare.eldercare.activity.ui.notas.AdicionarNotasActivity;
import com.eldercare.eldercare.adapter.calendario.AdapterCalendario;
import com.eldercare.eldercare.config.ConfiguracaoFirebase;
import com.eldercare.eldercare.helper.Base64Custom;
import com.eldercare.eldercare.model.Evento;
import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.util.ArrayList;
import java.util.List;


public class EventosFragment extends Fragment {

    private FloatingActionButton fab;
    private MaterialCalendarView calendario;
    private RecyclerView recyclerCalendario;
    private AdapterCalendario adapterCalendario;
    private CalendarDay dataAtual;

    private List<Evento> eventos = new ArrayList<>();
    private String dataSelecionada;

    private DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseRef();
    private DatabaseReference eventosRef;
    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private ValueEventListener valueEventListenerEventos;

    public EventosFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_eventos, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fab = view.findViewById(R.id.fabEventos);
        calendario = view.findViewById(R.id.calendarView);

        recyclerCalendario = view.findViewById(R.id.recyclerCalendario);

        //Configuraçao do adapter
        adapterCalendario = new AdapterCalendario(eventos, getContext());

        //Configuração do layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerCalendario.setLayoutManager(layoutManager);
        recyclerCalendario.setHasFixedSize(true);
        recyclerCalendario.setAdapter(adapterCalendario);

        configuracaoCalendarView();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AdicionarEventosActivity.class));
            }
        });

    }

    public void configuracaoCalendarView(){
        //Definir os meses para português
        CharSequence meses[] = {"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};
        calendario.setTitleMonths(meses);

        //Definir os dias para português
        CharSequence dias[] = {"seg", "ter", "qua", "qui", "sex", "sab", "dom"};
        calendario.setWeekDayLabels(dias);

        //Vai buscar a data atual para exibir os eventos de hoje
        dataAtual = CalendarDay.today();
        calendario.setDateSelected(dataAtual,true);

        //Estas 2 variaveis servem para incrementar um zero quando o dia/mês só tem um digito
        String diaSelecionado = String.format("%02d", dataAtual.getDay());
        String mesSelecionado = String.format("%02d", dataAtual.getMonth());
        dataSelecionada = String.valueOf( diaSelecionado + mesSelecionado + dataAtual.getYear());

        calendario.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {

                String diaSelecionado = String.format("%02d", date.getDay());
                String mesSelecionado = String.format("%02d", date.getMonth());
                dataSelecionada = String.valueOf(diaSelecionado + mesSelecionado + date.getYear());

                eventosRef.removeEventListener(valueEventListenerEventos);
                recuperarEventos();
            }
        });
    }

    public void recuperarEventos(){
        String emailUtilizador = autenticacao.getCurrentUser().getEmail();
        String idUtilizador = Base64Custom.codificarBase64(emailUtilizador);

        eventosRef = firebaseRef.child("eventos")
                .child(idUtilizador)
                .child(dataSelecionada);

        valueEventListenerEventos = eventosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                eventos.clear();

                for (DataSnapshot dados: snapshot.getChildren()){

                    Evento evento = dados.getValue( Evento.class );
                    evento.setKey(dados.getKey());
                    eventos.add(evento);

                }

                //diz ao adapter que os dados foram atualizados
                adapterCalendario.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        configuracaoCalendarView();
        recuperarEventos();
    }
}