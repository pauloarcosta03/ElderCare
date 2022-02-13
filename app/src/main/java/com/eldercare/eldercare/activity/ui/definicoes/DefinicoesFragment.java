package com.eldercare.eldercare.activity.ui.definicoes;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.eldercare.eldercare.R;
import com.eldercare.eldercare.activity.MainActivity;
import com.eldercare.eldercare.activity.ui.pressao.AdicionarPressaoActivity;
import com.eldercare.eldercare.config.ConfiguracaoFirebase;
import com.eldercare.eldercare.helper.Base64Custom;
import com.eldercare.eldercare.model.Paciente;
import com.eldercare.eldercare.model.Utilizador;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class DefinicoesFragment extends Fragment {

    private Button ButtonAddPaciente;
    private Button ButtonRemConta;
    private Button ButtonRemPacientes;
    private TextView textTipo;

    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseRef();
    private DatabaseReference utilizadorRef;

    private Paciente paciente;
    List<String> pacientesNome = new ArrayList<String>();
    List<String> pacientesId = new ArrayList<String>();

    public DefinicoesFragment() {
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
        return inflater.inflate(R.layout.fragment_definicoes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButtonAddPaciente = view.findViewById(R.id.ButtonAddPaciente);
        ButtonRemConta = view.findViewById(R.id.ButtonRemConta);
        ButtonRemPacientes = view.findViewById(R.id.ButtonRemPacientes);
        textTipo = view.findViewById(R.id.textTipo);

        String emailUtilizador = autenticacao.getCurrentUser().getEmail();
        String idUtilizador = Base64Custom.codificarBase64(emailUtilizador);

        utilizadorRef = firebaseRef.child("utilizadores")
                .child(idUtilizador);

        ButtonAddPaciente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), CriarPacienteActivity.class));
            }
        });

        ButtonRemConta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                utilizadorRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            Utilizador utilizador = snapshot.getValue(Utilizador.class);

                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                            alertDialog.setTitle("Eliminar conta");

                            if(utilizador.getTipo().equals("c")){
                                alertDialog.setMessage("Deseja eliminar a conta atual e " +
                                        "a conta de todos os pacientes?");
                            }else {
                                alertDialog.setMessage("Deseja eliminar a conta atual?");
                            }

                            alertDialog.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    utilizador.EliminarConta();

                                    //autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
                                    autenticacao.signOut();
                                    getActivity().startActivity(new Intent(getContext(), MainActivity.class));
                                    getActivity().finish();
                                }
                            });

                            alertDialog.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });

                            alertDialog.show();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

        ButtonRemPacientes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Verificar as permições da conta
                String emailUtilizador = autenticacao.getCurrentUser().getEmail();
                String idUtilizador = Base64Custom.codificarBase64(emailUtilizador);

                utilizadorRef = firebaseRef.child("utilizadores")
                        .child(idUtilizador)
                        .child("paciente");

                utilizadorRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        pacientesNome.clear();
                        pacientesId.clear();

                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                        alertDialog.setTitle("Eliminar paciente");


                        for (DataSnapshot dados: snapshot.getChildren()) {
                            paciente = snapshot.getValue(Paciente.class);

                            pacientesNome.add(dados.child("nome").getValue().toString());
                            pacientesId.add(dados.child("idPaciente").getValue().toString());
                        }

                        //define as opções de todos os nomes no alert dialog
                        String[] nomes = pacientesNome.toArray(new String[pacientesNome.size()]);
                        String[] ids = pacientesId.toArray(new String[pacientesId.size()]);

                        if(nomes.length!=0){
                            alertDialog.setItems(nomes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    for(int i = 0; i< nomes.length; i++) {

                                        if (nomes[i].equals(nomes[which])) {
                                            AlertDialog.Builder confirmarDialog = new AlertDialog.Builder(getContext());
                                            confirmarDialog.setTitle("Eliminar paciente");
                                            confirmarDialog.setMessage("Deseja mesmo eliminar o paciente " +
                                                    nomes[i] + "?");

                                            int idArray = i;

                                            confirmarDialog.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Paciente paciente = new Paciente();

                                                    paciente.setNome(nomes[idArray]);
                                                    paciente.setIdPaciente(ids[idArray]);
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
                                }
                            });
                        }else{
                            alertDialog.setMessage("Ainda não adicionou nenhum paciente.");
                        }


                        alertDialog.show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        verificarPermissões();
    }

    public void verificarPermissões(){
        //Ver se o utilizador tem permições
        String emailUtilizador = autenticacao.getCurrentUser().getEmail();
        String idUtilizador = Base64Custom.codificarBase64(emailUtilizador);

        utilizadorRef = firebaseRef.child("utilizadores")
                .child(idUtilizador);

        utilizadorRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(!snapshot.exists()) {
                    //autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
                    //autenticacao.signOut();
                    //getActivity().finish();

                }else {

                    Utilizador utilizador = snapshot.getValue(Utilizador.class);

                    if (utilizador.getTipo().equals("p")) {
                        ButtonAddPaciente.setVisibility(View.INVISIBLE);
                        ButtonRemConta.setVisibility(View.INVISIBLE);
                        textTipo.setText("p");
                    } else {
                        textTipo.setText("c");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}