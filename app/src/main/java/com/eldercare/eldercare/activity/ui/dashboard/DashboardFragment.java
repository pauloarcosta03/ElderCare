package com.eldercare.eldercare.activity.ui.dashboard;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eldercare.eldercare.R;
import com.eldercare.eldercare.adapter.calendario.AdapterCalendario;
import com.eldercare.eldercare.adapter.lembretes.AdapterLembretes;
import com.eldercare.eldercare.config.ConfiguracaoFirebase;
import com.eldercare.eldercare.helper.Base64Custom;
import com.eldercare.eldercare.model.Evento;
import com.eldercare.eldercare.model.Lembrete;
import com.eldercare.eldercare.model.Utilizador;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class DashboardFragment extends Fragment {

    private TextView textSaudacao, textVazioEventos, textVazioLembretes;
    private RecyclerView recyclerEventos, recyclerLembretes;

    private List<Evento> eventos = new ArrayList<>();
    private List<Lembrete> lembretes = new ArrayList<>();

    private String dataAtual;

    private AdapterCalendario adapterEventos;
    private AdapterLembretes adapterLembretes;

    private DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseRef();
    private DatabaseReference eventosRef;
    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private ValueEventListener valueEventListenerEventos;

    private DatabaseReference lembretesRef;
    private ValueEventListener valueEventListenerLembretes;

    private DatabaseReference utilizadoresRef;
    private ValueEventListener valueEventListenerUtilizadores;

    public DashboardFragment() {
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
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /*Dizer o nome do utilizador*/
        textSaudacao = view.findViewById(R.id.textSaudacao);

        String emailUtilizador = autenticacao.getCurrentUser().getEmail();
        String idUtilizador = Base64Custom.codificarBase64(emailUtilizador);

        utilizadoresRef = firebaseRef.child("utilizadores")
                .child(idUtilizador);

        valueEventListenerUtilizadores = utilizadoresRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Utilizador utilizador = snapshot.getValue(Utilizador.class);

                textSaudacao.setText(Html.fromHtml("Bom dia, <b>" + utilizador.getNome() + "</b>"));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        textVazioEventos = view.findViewById(R.id.textVazioEventos);
        textVazioLembretes = view.findViewById(R.id.textVazioLembretes);

            /*Inicio da configuração do recyclerEventos*/
            recyclerEventos = view.findViewById(R.id.recyclerEventos);

            //configura adapter
            adapterEventos = new AdapterCalendario(eventos, getContext());

            //configura layout manager
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
            recyclerEventos.setLayoutManager(layoutManager);
            recyclerEventos.setHasFixedSize(true);
            recyclerEventos.setAdapter(adapterEventos);

            /*fim da configuração do recyclerEventos*/

            /*Inicio da configuração do recyclerLembretes*/
            recyclerLembretes = view.findViewById(R.id.recyclerLembretes);

            //configura adapter
            adapterLembretes = new AdapterLembretes(lembretes, getContext());

            //configura layout manager
            RecyclerView.LayoutManager layoutManager1 = new LinearLayoutManager(getContext());
            recyclerLembretes.setLayoutManager(layoutManager1);
            recyclerLembretes.setHasFixedSize(true);
            recyclerLembretes.setAdapter(adapterLembretes);

            /*Fim da configuração do recyclerLembretes*/

        //Vai buscar o código do dia atual para buscar valores ao firebase
        dataAtual = new SimpleDateFormat("ddMMyyyy", Locale.getDefault()).format(new Date());

    }

    public void recuperarEventos(){
        String emailUtilizador = autenticacao.getCurrentUser().getEmail();
        String idUtilizador = Base64Custom.codificarBase64(emailUtilizador);

        eventosRef = firebaseRef.child("eventos")
                .child(idUtilizador)
                .child(dataAtual);

        //O orderByChild("tempo") serve para ordenar pelo child tempo
        valueEventListenerEventos = eventosRef.orderByChild("tempo").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                eventos.clear();

                for (DataSnapshot dados: snapshot.getChildren()){

                    Evento evento = dados.getValue( Evento.class );
                    evento.setKey(dados.getKey());
                    eventos.add(evento);

                }

                //verificar se existem contactos e mostra uma mensagem
                if(eventos.isEmpty()){
                    textVazioEventos.setText("Ainda não tem nenhum evento adicionada.");
                }else{
                    textVazioEventos.setText("");
                }

                //diz ao adapter que os dados foram atualizados
                adapterEventos.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void recuperarLembretes(){

        String emailUtilizador = autenticacao.getCurrentUser().getEmail();
        String idUtilizador = Base64Custom.codificarBase64(emailUtilizador);

        lembretesRef = firebaseRef.child("lembretes")
                .child(idUtilizador);

        //O orderByChild("tempo") serve para ordenar pelo child tempo
        valueEventListenerLembretes = lembretesRef.orderByChild("tempo")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        lembretes.clear();

                        for(DataSnapshot dados: snapshot.getChildren()){

                            Lembrete lembrete = dados.getValue( Lembrete.class);
                            lembrete.setKey(dados.getKey());
                            lembretes.add(lembrete);

                        }

                        //verificar se existem notas e mostra uma mensagem
                        if(lembretes.isEmpty()){
                            textVazioLembretes.setText("Ainda não tem nenhum lembrete.");
                        }else{
                            textVazioLembretes.setText("");
                        }

                        Log.i("lembretes", "" + lembretes.size());

                        //diz ao adapter que os dados foram atualizados
                        adapterLembretes.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarEventos();
        recuperarLembretes();
    }
}