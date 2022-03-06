package com.eldercare.eldercare.activity.ui.definicoes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.eldercare.eldercare.R;
import com.eldercare.eldercare.activity.ui.calendario.AdicionarEventosActivity;
import com.eldercare.eldercare.activity.ui.contactos.AdicionarContactosActivity;
import com.eldercare.eldercare.adapter.definicoes.AdapterGestaoPacientes;
import com.eldercare.eldercare.adapter.lembretes.AdapterLembretes;
import com.eldercare.eldercare.config.ConfiguracaoFirebase;
import com.eldercare.eldercare.helper.Base64Custom;
import com.eldercare.eldercare.helper.RecyclerItemClickListener;
import com.eldercare.eldercare.model.Evento;
import com.eldercare.eldercare.model.Lembrete;
import com.eldercare.eldercare.model.Paciente;
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

public class GestaoPacientesActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseRef();
    private DatabaseReference pacientesRef;
    private ValueEventListener valueEventListenerPacientes;

    private AdapterGestaoPacientes adapterPacientes;
    private List<String> nomePacientes = new ArrayList<>();
    private List<String> idPacientes = new ArrayList<>();

    private RecyclerView recyclerPacientes;
    private FloatingActionButton fab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestao_pacientes);

        recuperarPacientes();

        //permite fazer alterações à toolbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Gerir Pacientes");

        fab = findViewById(R.id.fabAddPaciente);
        recyclerPacientes = findViewById(R.id.recyclerPacientes);

        //configuração adapter
        adapterPacientes = new AdapterGestaoPacientes(nomePacientes, getApplicationContext());

        //Configuração RecyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerPacientes.setLayoutManager(layoutManager);
        recyclerPacientes.setHasFixedSize(true);
        recyclerPacientes.setAdapter(adapterPacientes);

        touchListener();

        //botão para adicionar paciente
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), CriarPacienteActivity.class));
            }
        });
    }

    //em vez de dar reset à activity anterior, dá finish
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void recuperarPacientes(){

        //Buscar o id do utilizador
        String emailUtilizador = autenticacao.getCurrentUser().getEmail();
        String idUtilizador = Base64Custom.codificarBase64(emailUtilizador);

        pacientesRef = firebaseRef.child("utilizadores")
                .child(idUtilizador)
                .child("paciente");

        valueEventListenerPacientes = pacientesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                nomePacientes.clear();
                idPacientes.clear();

                for (DataSnapshot dados: snapshot.getChildren()){

                    nomePacientes.add(dados.child("nome").getValue().toString());
                    idPacientes.add(dados.child("idPaciente").getValue().toString());

                }

                //diz ao adapter que os dados foram atualizados
                adapterPacientes.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void touchListener(){
        //item click do recyclerView
        recyclerPacientes.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getApplicationContext(),
                        recyclerPacientes,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Paciente paciente = new Paciente();
                                paciente.setNome(nomePacientes.get(position));
                                paciente.setIdPaciente(idPacientes.get(position));

                                Intent intent = new Intent(getApplicationContext(), EditarPerfilPacienteActivity.class);
                                intent.putExtra("id", paciente.getIdPaciente());
                                startActivity(intent);

                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                                Paciente paciente = new Paciente();
                                paciente.setNome(nomePacientes.get(position));
                                paciente.setIdPaciente(idPacientes.get(position));

                                final String[] opcoes = {"Editar Dados", "Eliminar Paciente"};

                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(GestaoPacientesActivity.this);
                                alertDialog.setTitle("Opções de evento");

                                alertDialog.setItems(opcoes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if("Editar Paciente".equals(opcoes[which])){

                                        }else if("Eliminar Paciente".equals(opcoes[which])) {

                                            AlertDialog.Builder confirmarDialog = new AlertDialog.Builder(GestaoPacientesActivity.this);
                                            confirmarDialog.setTitle("Eliminar paciente");
                                            confirmarDialog.setMessage("Deseja mesmo eliminar o paciente " +
                                                    paciente.getNome() + "?");

                                            confirmarDialog.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    paciente.eliminarPaciente();
                                                }
                                            });

                                            confirmarDialog.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                }
                                            });

                                            confirmarDialog.show();
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
}