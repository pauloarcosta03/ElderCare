package com.eldercare.eldercare.activity.ui.definicoes;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.eldercare.eldercare.model.Utilizador;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;


public class DefinicoesFragment extends Fragment {

    private Button ButtonAddPaciente;
    private Button ButtonRemConta;
    private TextView textTipo;

    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseRef();
    private DatabaseReference utilizadorRef;
    private String permicao;

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

                            utilizador.EliminarConta();

                            //autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
                            autenticacao.signOut();
                            getActivity().startActivity(new Intent(getContext(), MainActivity.class));
                            getActivity().finish();
                        }


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