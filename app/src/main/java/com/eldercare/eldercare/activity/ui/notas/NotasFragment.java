package com.eldercare.eldercare.activity.ui.notas;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eldercare.eldercare.R;
import com.eldercare.eldercare.adapter.notas.AdapterNotas;
import com.eldercare.eldercare.config.ConfiguracaoFirebase;
import com.eldercare.eldercare.helper.Base64Custom;
import com.eldercare.eldercare.model.Nota;
import com.github.clans.fab.FloatingActionButton;
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