package com.eldercare.eldercare.activity.ui.lembretes;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.eldercare.eldercare.R;
import com.eldercare.eldercare.adapter.contactos.AdapterContactos;
import com.eldercare.eldercare.adapter.lembretes.AdapterLembretes;
import com.eldercare.eldercare.config.ConfiguracaoFirebase;
import com.eldercare.eldercare.helper.Base64Custom;
import com.eldercare.eldercare.helper.RecyclerItemClickListener;
import com.eldercare.eldercare.model.Lembrete;
import com.eldercare.eldercare.model.Utilizador;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LembretesFragment extends Fragment {

    private FloatingActionButton fab;
    private TextView textVazio;
    private RecyclerView recyclerLembretes;

    private AdapterLembretes adapterLembretes;
    private List<Lembrete> lembretes = new ArrayList<>();

    private DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseRef();
    private DatabaseReference lembretesRef;
    private DatabaseReference utilizadorRef;
    private Utilizador utilizador;
    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private ValueEventListener valueEventListenerLembretes;


    public LembretesFragment() {
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
        return inflater.inflate(R.layout.fragment_lembretes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fab = view.findViewById(R.id.fabLembretes);
        textVazio = view.findViewById(R.id.textVazio);

        //recyclerView
        recyclerLembretes = view.findViewById(R.id.recyclerLembretes);

        //Verificar as permições da conta
        String emailUtilizador = autenticacao.getCurrentUser().getEmail();
        String idUtilizador = Base64Custom.codificarBase64(emailUtilizador);

        utilizadorRef = firebaseRef.child("utilizadores")
                .child(idUtilizador);

        utilizadorRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                utilizador = snapshot.getValue(Utilizador.class);

                if(utilizador.getTipo().equals("p")){
                    fab.setVisibility(View.INVISIBLE);
                }else{
                    touchListener();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //configuração adapter
        adapterLembretes = new AdapterLembretes(lembretes, getContext());

        //Configuração RecyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerLembretes.setLayoutManager(layoutManager);
        recyclerLembretes.setHasFixedSize(true);
        recyclerLembretes.setAdapter(adapterLembretes);

        //listener do fab
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AdicionarLembretesActivity.class));
            }
        });

    }

    public void touchListener(){
        recyclerLembretes.addOnItemTouchListener(new RecyclerItemClickListener(getContext(),
                recyclerLembretes,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Lembrete lembrete = lembretes.get(position);

                        Intent intent = new Intent(getContext(), AdicionarLembretesActivity.class);
                        intent.putExtra("lembrete", lembrete);
                        startActivity(intent);

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        Lembrete lembrete = lembretes.get(position);

                        final String[] opcoes = {"Editar Lembrete", "Duplicar Lembrete", "Eliminar Lembrete"};

                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                        alertDialog.setTitle("Opções de Lembrete");
                        alertDialog.setItems(opcoes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if("Editar Lembrete".equals(opcoes[which])){

                                    Intent intent = new Intent(getContext(), AdicionarLembretesActivity.class);
                                    intent.putExtra("lembrete", lembrete);
                                    startActivity(intent);

                                }else if("Duplicar Lembrete".equals(opcoes[which])){

                                    lembrete.guardar();
                                    Toast.makeText(getContext(), "Lembrete "
                                                    + lembrete.getTitulo() +
                                                    " duplicado com sucesso!",
                                            Toast.LENGTH_SHORT).show();

                                }else if("Eliminar Lembrete".equals(opcoes[which])){

                                    AlertDialog.Builder eliminarDialog = new AlertDialog.Builder(getContext());
                                    eliminarDialog.setTitle("Eliminar Lembrete");
                                    eliminarDialog.setMessage("Deseja mesmo eliminar esta lembrete?\n"
                                            + lembrete.getTitulo());

                                    eliminarDialog.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            String emailUtilizador = autenticacao.getCurrentUser().getEmail();
                                            String idUtilizador = Base64Custom.codificarBase64(emailUtilizador);

                                            lembretesRef = firebaseRef.child("lembretes")
                                                    .child(idUtilizador);

                                            lembretesRef.child(lembrete.getKey()).removeValue()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {

                                                            if (task.isSuccessful()){
                                                                Toast.makeText(getContext(), "Lembrete "
                                                                                + lembrete.getTitulo() +
                                                                                " removido com sucesso!",
                                                                        Toast.LENGTH_SHORT).show();
                                                            }else{
                                                                Toast.makeText(getContext(),
                                                                        "Erro ao eliminar lembrete",
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
                    textVazio.setText("Ainda não tem nenhum lembrete.");
                }else{
                    textVazio.setText("");
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
        recuperarLembretes();
    }

    @Override
    public void onStop() {
        super.onStop();
        //para o value event listener para não consumir recursos quando a app é fechada
        lembretesRef.removeEventListener( valueEventListenerLembretes );
    }
}