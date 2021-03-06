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

import com.eldercare.eldercare.R;
import com.eldercare.eldercare.activity.MainActivity;
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

    private Button ButtonRemConta;
    private Button ButtonEditarPass;
    private Button ButtonGerirPacientes;
    private Button ButtonLogout;
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

        ButtonRemConta = view.findViewById(R.id.ButtonRemConta);
        ButtonEditarPass = view.findViewById(R.id.ButtonMudarPass);
        ButtonGerirPacientes = view.findViewById(R.id.ButtonGerirPacientes);
        ButtonLogout = view.findViewById(R.id.buttonLogOut);
        textTipo = view.findViewById(R.id.textTipo);

        String emailUtilizador = autenticacao.getCurrentUser().getEmail();
        String idUtilizador = Base64Custom.codificarBase64(emailUtilizador);

        utilizadorRef = firebaseRef.child("utilizadores")
                .child(idUtilizador);

        //but??o de gest??o de pacientes
        ButtonGerirPacientes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), GestaoPacientesActivity.class));
            }
        });
        //but??o de Logout
        ButtonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                alertDialog.setTitle("Sair desta conta?");

                alertDialog.setMessage("Deseja encerrar a sess??o desta conta?");

                alertDialog.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        logOut();
                    }
                });

                alertDialog.setNegativeButton("N??o", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                alertDialog.show();


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

                            alertDialog.setNegativeButton("N??o", new DialogInterface.OnClickListener() {
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


        //Abrir activity editar password
        ButtonEditarPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), EditarPassActivity.class));
            }
        });

    }

    public void logOut(){

        String emailUtilizador = autenticacao.getCurrentUser().getEmail();
        String idUtilizador = Base64Custom.codificarBase64(emailUtilizador);

        //elimina o token para este dispositivo n??o receber notifica????es
        firebaseRef.child("utilizadores")
                .child(idUtilizador)
                .child("token").setValue("");

        autenticacao.signOut();
        getActivity().finish();
    }

    @Override
    public void onStart() {
        super.onStart();
        verificarPermiss??es();
    }

    public void verificarPermiss??es(){
        //Ver se o utilizador tem permi????es
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
                        ButtonGerirPacientes.setVisibility(View.GONE);
                        ButtonRemConta.setVisibility(View.GONE);
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