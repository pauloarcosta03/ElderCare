package com.eldercare.eldercare.activity.ui.notas;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.eldercare.eldercare.R;
import com.eldercare.eldercare.activity.ui.contactos.AdicionarContactosActivity;
import com.eldercare.eldercare.adapter.notas.AdapterNotas;
import com.eldercare.eldercare.config.ConfiguracaoFirebase;
import com.eldercare.eldercare.helper.Base64Custom;
import com.eldercare.eldercare.helper.RecyclerItemClickListener;
import com.eldercare.eldercare.model.Contacto;
import com.eldercare.eldercare.model.Nota;
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


public class NotasFragment extends Fragment {

    private FloatingActionButton fab;
    private RecyclerView recyclerView;
    private List<Nota> notas = new ArrayList<>();
    private AdapterNotas adapterNotas;

    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private DatabaseReference notasRef;
    private DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseRef();
    private ValueEventListener valueEventListenerNotas;


    public NotasFragment() {
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
        return inflater.inflate(R.layout.fragment_notas, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fab = view.findViewById(R.id.fabNotas);

        //RecyclerView
        recyclerView = view.findViewById(R.id.recyclerNotas);

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getContext(),
                        recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        Nota nota = notas.get(position);

                        final String[] opcoes = {"Editar nota", "Duplicar Nota", "Eliminar Nota"};

                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                        alertDialog.setTitle("Opções de nota");

                        alertDialog.setItems(opcoes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if ("Editar nota".equals(opcoes[which])){
                                    Intent intent = new Intent(getContext(), AdicionarNotasActivity.class);
                                    intent.putExtra("nota", nota);
                                    startActivity(intent);
                                }else if ("Duplicar Nota".equals(opcoes[which])){

                                    nota.guardar();

                                }else if ("Eliminar Nota".equals(opcoes[which])){

                                    AlertDialog.Builder eliminarDialog = new AlertDialog.Builder(getContext());
                                    eliminarDialog.setTitle("Eliminar Nota");
                                    eliminarDialog.setMessage("Deseja mesmo eliminar esta nota?\n"
                                            + nota.getTitulo());

                                    eliminarDialog.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            String emailUtilizador = autenticacao.getCurrentUser().getEmail();
                                            String idUtilizador = Base64Custom.codificarBase64(emailUtilizador);

                                            notasRef = firebaseRef.child("notas").child(idUtilizador);

                                            notasRef.child(nota.getKey()).removeValue()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {

                                                            if (task.isSuccessful()){
                                                                Toast.makeText(getContext(), "Nota "
                                                                                + nota.getTitulo() +
                                                                                " removida com sucesso!",
                                                                        Toast.LENGTH_SHORT).show();
                                                            }else{
                                                                Toast.makeText(getContext(),
                                                                        "Erro ao eliminar",
                                                                        Toast.LENGTH_LONG).show();
                                                            }
                                                        }
                                                    });

                                        }
                                    });

                                    eliminarDialog.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Toast.makeText(getContext(), "Cancelado", Toast.LENGTH_SHORT).show();
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

        //Configuração adapter
        adapterNotas = new AdapterNotas(notas, getContext());

        //Configuração RecyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter( adapterNotas );

        //Floating Action Button
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AdicionarNotasActivity.class));
            }
        });

    }

    public void recuperarNotas(){

        String emailUtilizador = autenticacao.getCurrentUser().getEmail();
        String idUtilizador = Base64Custom.codificarBase64(emailUtilizador);

        notasRef = firebaseRef.child("notas")
                .child(idUtilizador);

        valueEventListenerNotas = notasRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                notas.clear();

                for (DataSnapshot dados: snapshot.getChildren()){

                    Nota nota = dados.getValue( Nota.class );
                    nota.setKey(dados.getKey());
                    notas.add(nota);

                }

                //diz ao adapter que os dados foram atualizados
                adapterNotas.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarNotas();
    }

    @Override
    public void onStop() {
        super.onStop();
        //para o value event listener para não consumir recursos quando a app é fechada
        notasRef.removeEventListener( valueEventListenerNotas );
    }
}