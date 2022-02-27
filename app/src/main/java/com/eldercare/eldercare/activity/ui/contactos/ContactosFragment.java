package com.eldercare.eldercare.activity.ui.contactos;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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
import com.eldercare.eldercare.adapter.contactos.AdapterContactos;
import com.eldercare.eldercare.config.ConfiguracaoFirebase;
import com.eldercare.eldercare.helper.Base64Custom;
import com.eldercare.eldercare.helper.RecyclerItemClickListener;
import com.eldercare.eldercare.model.Contacto;
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

public class ContactosFragment extends Fragment {

    private FloatingActionButton fab;
    private RecyclerView recyclerView;
    private TextView textVazio;
    private AdapterContactos adapterContactos;

    private List<Contacto> contactos = new ArrayList<>();

    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseRef();
    private DatabaseReference contactosRef;
    private DatabaseReference utilizadorRef;
    private Utilizador utilizador;
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
        textVazio = view.findViewById(R.id.textVazio);

        //RecyclerView
        recyclerView = view.findViewById(R.id.recyclerContactos);

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
                    touchListenerPacientes();
                }else{
                    touchListener();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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

    public void touchListener(){

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

                        Contacto contacto = contactos.get(position);

                        String opcaoLigar = "Ligar a " + contacto.getNome();

                        final String[] opcoes = {"Contactar", "Editar Contacto", "Eliminar Contacto"};

                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                        alertDialog.setTitle("Opções de contacto");

                        alertDialog.setItems(opcoes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if ("Contactar".equals(opcoes[which])) {

                                    Intent intent = new Intent(Intent.ACTION_DIAL,
                                            Uri.parse("tel:" + contacto.getNumero()));
                                    startActivity( intent );

                                }else if ("Editar Contacto".equals(opcoes[which])){

                                    Intent intent = new Intent(getContext(), AdicionarContactosActivity.class);
                                    intent.putExtra("contacto", contacto);
                                    startActivity(intent);

                                }else if ("Eliminar Contacto".equals(opcoes[which])){
                                    AlertDialog.Builder eliminarDialog = new AlertDialog.Builder(getContext());
                                    eliminarDialog.setTitle("Eliminar Contacto");
                                    eliminarDialog.setMessage("Deseja mesmo eliminar este contacto?\n"
                                            + contacto.getNome() + "(" + contacto.getNumero() + ")");

                                    eliminarDialog.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            String emailUtilizador = autenticacao.getCurrentUser().getEmail();
                                            String idUtilizador = Base64Custom.codificarBase64(emailUtilizador);

                                            //Eliminar para o cuidador
                                            contactosRef = firebaseRef.child("contactos").child(idUtilizador);

                                            contactosRef.child(contacto.getKey()).removeValue()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {

                                                            if (task.isSuccessful()){
                                                                Toast.makeText(getContext(), "Contacto "
                                                                                + contacto.getNome() +
                                                                                " removido com sucesso!",
                                                                        Toast.LENGTH_SHORT).show();
                                                            }else{
                                                                Toast.makeText(getContext(),
                                                                        "Erro ao eliminar",
                                                                        Toast.LENGTH_LONG).show();
                                                            }
                                                        }
                                                    });

                                            //Eliminar para o paciente
                                            contactosRef = firebaseRef.child("contactos").child(contacto.getIdPaciente());

                                            contactosRef.child(contacto.getKey()).removeValue()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {

                                                            if (!task.isSuccessful()){
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

    }

    //Para os pacientes clicarem para ligar
    public void touchListenerPacientes(){

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

                //verificar se existem contactos e mostra uma mensagem
                if(contactos.isEmpty()){
                    textVazio.setText("Ainda não tem nenhum contacto adicionada.");
                }else{
                    textVazio.setText("");
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