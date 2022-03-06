package com.eldercare.eldercare.model;

import androidx.annotation.NonNull;

import com.eldercare.eldercare.config.ConfiguracaoFirebase;
import com.eldercare.eldercare.helper.Base64Custom;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;

public class Paciente implements Serializable {

    private String idPaciente;
    private String nome;

    public Paciente() {

    }

    public void eliminarPaciente(){

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseRef();
        DatabaseReference utilizadorRef;
        FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        String idUtilizador = this.idPaciente;

        utilizadorRef = firebaseRef
                .child("utilizadores")
                .child(idUtilizador);

        utilizadorRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //if(snapshot.exists()) {

                    String cuidador = String.valueOf(snapshot.child("cuidador").getValue());

                    //remove o paciente no seu cuidador
                    firebaseRef.child("utilizadores")
                            .child(cuidador)
                            .child("paciente")
                            .child(idUtilizador)
                            .removeValue();

                    //FirebaseUser utilizadoAuth = autenticacao.getCurrentUser();

                    firebaseRef.child("contactos")
                            .child(idUtilizador)
                            .removeValue();

                    firebaseRef.child("eventos")
                            .child(idUtilizador)
                            .removeValue();

                    firebaseRef.child("glicemia")
                            .child(idUtilizador)
                            .removeValue();

                    firebaseRef.child("lembretes")
                            .child(idUtilizador)
                            .removeValue();

                    firebaseRef.child("notas")
                            .child(idUtilizador)
                            .removeValue();

                    firebaseRef.child("pressao")
                            .child(idUtilizador)
                            .removeValue();

                    firebaseRef.child("utilizadores")
                            .child(idUtilizador)
                            .removeValue();

                    //buscar uid
            /*String password = utilizadorRef.child("password").toString();
            String email = utilizadorRef.child("email").toString();

            autenticacao.signInWithEmailAndPassword(email, Base64Custom.descodificarBase64(password));

            utilizadoAuth.delete();*/
                //}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    public String getIdPaciente() {
        return idPaciente;
    }

    public void setIdPaciente(String idPaciente) {
        this.idPaciente = idPaciente;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

}
