package com.eldercare.eldercare.activity.ui.contactos;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.eldercare.eldercare.R;
import com.eldercare.eldercare.adapter.contactos.AdapterContactos;
import com.eldercare.eldercare.config.ConfiguracaoFirebase;
import com.eldercare.eldercare.helper.Base64Custom;
import com.eldercare.eldercare.helper.RecyclerItemClickListener;
import com.eldercare.eldercare.model.Contacto;
import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ContactosFragment extends Fragment {

    private FloatingActionButton fab;
    private RecyclerView recyclerView;
    private AdapterContactos adapterContactos;

    private List<Contacto> contactos = new ArrayList<>();

    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseRef();
    private DatabaseReference contactosRef;
    private ValueEventListener valueEventListenerContactos;

    public ContactosFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contactos, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fab = view.findViewById(R.id.fabContactos);

        //RecyclerView
        recyclerView = view.findViewById(R.id.recyclerContactos);

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(
                getContext(),
                recyclerView,
                new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                Contacto contacto = contactos.get(position);

                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + contacto.getNumero()));
                startActivity( intent );
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        }));

        //configuração adapter
        adapterContactos = new AdapterContactos(contactos, getContext());

        //Configuração RecyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapterContactos);

        //para criar novo contacto
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AdicionarContactosActivity.class));
            }
        });
    }

    public void recuperarContactos(){

        //Buscar o id do utilizador
        String emailUtilizador = autenticacao.getCurrentUser().getEmail();
        String idUtilizador = Base64Custom.codificarBase64(emailUtilizador);

        contactosRef = firebaseRef.child("contactos")
                .child(idUtilizador);

        valueEventListenerContactos = contactosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                contactos.clear();

                for (DataSnapshot dados: snapshot.getChildren()){

                    Contacto contacto = dados.getValue( Contacto.class );
                    contacto.setKey(dados.getKey());
                    contactos.add(contacto);

                }

                //diz ao adapter que os dados foram atualizados
                adapterContactos.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarContactos();
    }

    @Override
    public void onStop() {
        super.onStop();
        contactosRef.removeEventListener(valueEventListenerContactos);
    }
}