package com.eldercare.eldercare.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import com.eldercare.eldercare.R;
import com.eldercare.eldercare.adapter.calendario.AdapterCalendario;
import com.eldercare.eldercare.adapter.notificacao.AdapterNotificacao;
import com.eldercare.eldercare.config.ConfiguracaoFirebase;
import com.eldercare.eldercare.helper.Base64Custom;
import com.eldercare.eldercare.model.DisplayNotificacao;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotificacoesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView textVazio;
    private AdapterNotificacao adapterNotificacao;

    private List<DisplayNotificacao> notificacoes = new ArrayList<>();

    private DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseRef();
    private DatabaseReference notificacoesRef;
    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private ValueEventListener valueEventListenerNotificacoes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificacoes);

        //permite fazer alterações à toolbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Notificações");

        recyclerView = findViewById(R.id.recyclerNotificacao);
        textVazio = findViewById(R.id.textVazio);

        //Configuraçao do adapter
        adapterNotificacao = new AdapterNotificacao(notificacoes, getApplicationContext());

        //Configuração do layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapterNotificacao);

    }

    public void recuperarNotificacoes(){

        String emailUtilizador = autenticacao.getCurrentUser().getEmail();
        String idUtilizador = Base64Custom.codificarBase64(emailUtilizador);

        notificacoesRef = firebaseRef.child("notificacoes")
                .child(idUtilizador);

        //O orderByChild("tempo") serve para ordenar pelo child tempo
        valueEventListenerNotificacoes = notificacoesRef.orderByChild("tempo")
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                notificacoes.clear();

                for (DataSnapshot dados: snapshot.getChildren()){
                    DisplayNotificacao notificacao = dados.getValue(DisplayNotificacao.class);
                    notificacoes.add(notificacao);
                }

                //para aparecer da ordem de mais recente para mais antigo
                Collections.reverse(notificacoes);

                //verificar se existem notificacoes e mostra uma mensagem
                if(notificacoes.isEmpty()){
                    textVazio.setText("Ainda não recebeu nenhuma notificação.");
                }else{
                    textVazio.setText("");
                }

                //diz ao adapter que os dados foram atualizados
                adapterNotificacao.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperarNotificacoes();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}