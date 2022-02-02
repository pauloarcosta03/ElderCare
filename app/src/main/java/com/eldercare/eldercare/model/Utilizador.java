package com.eldercare.eldercare.model;

import com.eldercare.eldercare.config.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

public class Utilizador {

    private String nome;
    private String email;
    private String password;
    private String tipo;
    private String idUtilizador;
    private String cuidador;
    private DatabaseReference firebaseRef;

    public Utilizador() {
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

            Paciente paciente = new Paciente();
            paciente.setNome(this.nome);
            paciente.setIdPaciente(this.idUtilizador);

            firebaseRef.child("utilizadores")
                    .child(this.cuidador)
                    .child("paciente")
                    .push()
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

    @Exclude
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
