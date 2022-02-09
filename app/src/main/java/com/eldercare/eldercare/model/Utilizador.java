package com.eldercare.eldercare.model;

import androidx.annotation.NonNull;

import com.eldercare.eldercare.config.ConfiguracaoFirebase;
import com.eldercare.eldercare.helper.Base64Custom;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.ValueEventListener;

public class Utilizador {

    private String nome = "";
    private String email;
    private String password;
    private String tipo;
    private String idUtilizador;
    private String cuidador;
    private DatabaseReference firebaseRef;

    public Utilizador() {
    }

    public void EliminarConta(){

        firebaseRef = ConfiguracaoFirebase.getFirebaseRef();
        DatabaseReference utilizadorRef;
        DatabaseReference eliminarRef;
        FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        String idUtilizador = Base64Custom.codificarBase64(autenticacao.getCurrentUser().getEmail());

        utilizadorRef = firebaseRef
                .child("utilizadores")
                .child(idUtilizador);

        utilizadorRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()) {

                    Utilizador utilizador = snapshot.getValue(Utilizador.class);


                    if (utilizador.getTipo().equals("c")) {

                        //ao eliminar o cuidador elimita também os pacientes
                        DatabaseReference pacientes = utilizadorRef
                                .child("paciente");

                        pacientes.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dados : snapshot.getChildren()) {
                                    Paciente paciente = new Paciente();

                                    paciente.setIdPaciente(dados.child("idPaciente").getValue().toString());
                                    paciente.setNome(dados.child("nome").getValue().toString());

                                    paciente.eliminarPaciente();

                                }

                                FirebaseUser utilizadoAuth = autenticacao.getCurrentUser();

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

                                //utilizadoAuth.delete();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    } else {

                        String cuidador = utilizador.getCuidador();

                        //remove o paciente no seu cuidador
                        firebaseRef.child("utilizadores")
                                .child(cuidador)
                                .child("paciente")
                                .child(idUtilizador)
                                .removeValue();

                        FirebaseUser utilizadoAuth = autenticacao.getCurrentUser();

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

                        utilizadoAuth.delete();

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void guardarNome(){
        firebaseRef = ConfiguracaoFirebase.getFirebaseRef();

        firebaseRef.child("utilizadores")
                .child(this.idUtilizador)
                .setValue(this);
        
        if(this.getTipo().equals("p")){

            firebaseRef.child("utilizadores")
                    .child(this.idUtilizador)
                    .child("cuidador")
                    .setValue(this.getCuidador());

            //guardar pass encriptada (para conseguir eliminar)

            firebaseRef.child("utilizadores")
                    .child(this.idUtilizador)
                    .child("password")
                    .setValue(this.getPassword());

            Paciente paciente = new Paciente();
            paciente.setNome(this.nome);
            paciente.setIdPaciente(this.idUtilizador);

            firebaseRef.child("utilizadores")
                    .child(this.cuidador)
                    .child("paciente")
                    .child(this.idUtilizador)
                    .setValue(paciente);

        }

        //Adicionar o 112 aos contactos
        firebaseRef.child("contactos").child(this.idUtilizador).child("112").child("nome").setValue("112");
        firebaseRef.child("contactos").child(this.idUtilizador).child("112").child("numero").setValue("112");
        firebaseRef.child("contactos").child(this.idUtilizador).child("112").child("categoria").setValue("urgência");

    }

    @Exclude
    public String getIdUtilizador() {
        return idUtilizador;
    }

    public void setIdUtilizador(String idUtilizador) {
        this.idUtilizador = idUtilizador;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getCuidador() {
        return cuidador;
    }

    public void setCuidador(String cuidador) {
        this.cuidador = cuidador;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
