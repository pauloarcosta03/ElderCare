package com.eldercare.eldercare.activity.ui.glicemia;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.eldercare.eldercare.R;
import com.eldercare.eldercare.activity.ui.pressao.AdicionarPressaoActivity;
import com.eldercare.eldercare.adapter.glicemia.AdapterGlicemia;
import com.eldercare.eldercare.config.ConfiguracaoFirebase;
import com.eldercare.eldercare.helper.Base64Custom;
import com.eldercare.eldercare.helper.RecyclerItemClickListener;
import com.eldercare.eldercare.model.Glicemia;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

public class GlicemiaFragment extends Fragment {

    private MaterialCalendarView calendario;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private TextView textVazio;

    private CalendarDay dataAtual;
    private String dataSelecionada;

    private AdapterGlicemia adapterGlicemia;
    private List<Glicemia> glicemias = new ArrayList<>();

    private DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseRef();
    private DatabaseReference glicemiaRef;
    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private ValueEventListener valueEventListenerGlicemia;

    public GlicemiaFragment() {
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
        return inflater.inflate(R.layout.fragment_glicemia, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        calendario = view.findViewById(R.id.calendarView);
        fab = view.findViewById(R.id.fabGlicemia);
        textVazio = view.findViewById(R.id.textVazio);

        recyclerView = view.findViewById(R.id.recyclerGlicemia);

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(),
                recyclerView,
                new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                Glicemia glicemia = glicemias.get(position);

                Intent intent = new Intent(getContext(), AdicionarGlicemiaActivity.class);
                intent.putExtra("glicemia", glicemia);
                startActivity(intent);

            }

            @Override
            public void onLongItemClick(View view, int position) {

                Glicemia glicemia = glicemias.get(position);

                final String[] opcoes = {"Editar Glicemia", "Duplicar Glicemia", "Eliminar Glicemia"};

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                alertDialog.setTitle("Opções de Glicemia");

                alertDialog.setItems(opcoes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if("Editar Glicemia".equals(opcoes[which])){

                            Intent intent = new Intent(getContext(), AdicionarGlicemiaActivity.class);
                            intent.putExtra("glicemia", glicemia);
                            startActivity(intent);

                        }else if("Duplicar Glicemia".equals(opcoes[which])){

                            glicemia.guardar();

                        }else if("Eliminar Glicemia".equals(opcoes[which])){

                            AlertDialog.Builder eliminarDialog = new AlertDialog.Builder(getContext());
                            eliminarDialog.setTitle("Eliminar Registo Glicemia");
                            eliminarDialog.setMessage("Deseja mesmo eliminar este registo?");

                            eliminarDialog.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    String emailUtilizador = autenticacao.getCurrentUser().getEmail();
                                    String idUtilizador = Base64Custom.codificarBase64(emailUtilizador);

                                    glicemiaRef = firebaseRef
                                            .child("glicemia")
                                            .child(idUtilizador)
                                            .child(dataSelecionada);

                                    glicemiaRef.child(glicemia.getKey()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()){
                                                Toast.makeText(getContext(),
                                                        "Registo de glicemia eliminado.",
                                                        Toast.LENGTH_LONG).show();
                                            }else{
                                                Toast.makeText(getContext(),
                                                        "Erro ao eliminar registo de glicemia.",
                                                        Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });

                                }
                            });

                            eliminarDialog.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });

                            eliminarDialog.show();

                        }

                    }
                });

                alertDialog.show();

            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        }));

        //Configuração do adapter
        adapterGlicemia = new AdapterGlicemia(glicemias, getContext());

        //Configuração do layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapterGlicemia);

        configuracaoCalendario();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AdicionarGlicemiaActivity.class));
            }
        });

    }

    public void recuperarGlicemia(){

        String emailUtilizador = autenticacao.getCurrentUser().getEmail();
        String idUtilizador = Base64Custom.codificarBase64(emailUtilizador);

        glicemiaRef = firebaseRef.child("glicemia")
                .child(idUtilizador)
                .child(dataSelecionada);

        valueEventListenerGlicemia = glicemiaRef.orderByChild("tempo")
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                glicemias.clear();

                for(DataSnapshot dados: snapshot.getChildren()){
                    Glicemia glicemia = dados.getValue(Glicemia.class);
                    glicemia.setKey(dados.getKey());

                    glicemias.add(glicemia);
                }

                //verificar se existem registos e mostra uma mensagem
                if(glicemias.isEmpty()){
                    textVazio.setText("Ainda não tem nenhum registo adicionada.");
                }else{
                    textVazio.setText("");
                }

                adapterGlicemia.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void configuracaoCalendario(){
        //Definir os meses para português
        CharSequence meses[] = {"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};
        calendario.setTitleMonths(meses);

        //Definir os dias para português
        CharSequence dias[] = {"seg", "ter", "qua", "qui", "sex", "sab", "dom"};
        calendario.setWeekDayLabels(dias);

        //Vai buscar a data atual para exibir os registos de hoje
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

                recuperarGlicemia();
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarGlicemia();
    }
}