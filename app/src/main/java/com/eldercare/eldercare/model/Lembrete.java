package com.eldercare.eldercare.model;

import com.eldercare.eldercare.config.ConfiguracaoFirebase;
import com.eldercare.eldercare.helper.Base64Custom;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.io.Serializable;

public class Lembrete implements Serializable {

    private String Titulo;
    private String Descricao;
    private String Horas;
    private String Minutos;
    private String Tempo;
    private String paciente;
    private String idPaciente;
    private String idPacienteAnterior;
    private String Key;

    public void guardar(){

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseRef();
        FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        String idUtilizador = Base64Custom.codificarBase64(autenticacao.getCurrentUser().getEmail());

        String idLembrete = firebaseRef.push().getKey();

        firebaseRef.child("lembretes")
                .child(idUtilizador)
                .child(idLembrete)
                .setValue(this);

        firebaseRef.child("lembretes")
                .child(this.idPaciente)
                .child(idLembrete)
                .setValue(this);

    }

    public void editar(){

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseRef();
        FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        String idUtilizador = Base64Custom.codificarBase64(autenticacao.getCurrentUser().getEmail());

        firebaseRef.child("lembretes")
                .child(idUtilizador)
                .child(this.getKey())
                .setValue(this);

        firebaseRef.child("lembretes")
                .child(this.idPaciente)
                .child(this.getKey())
                .setValue(this);

        if(!(this.getIdPaciente().equals(this.getIdPacienteAnterior()))){

            firebaseRef.child("lembretes")
                    .child(this.idPacienteAnterior)
                    .child(this.getKey()).removeValue();

        }

    }

    public Lembrete() {
    }

    @Exclude
    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }

    public String getPaciente() {
        return paciente;
    }

    public void setPaciente(String paciente) {
        this.paciente = paciente;
    }

    @Exclude
    public String getIdPacienteAnterior() {
        return idPacienteAnterior;
    }

    public void setIdPacienteAnterior(String idPacienteAnterior) {
        this.idPacienteAnterior = idPacienteAnterior;
    }

    public String getIdPaciente() {
        return idPaciente;
    }

    public void setIdPaciente(String idPaciente) {
        this.idPaciente = idPaciente;
    }

    public String getTitulo() {
        return Titulo;
    }

    public void setTitulo(String titulo) {
        Titulo = titulo;
    }

    public String getDescricao() {
        return Descricao;
    }

    public void setDescricao(String descricao) {
        Descricao = descricao;
    }

    public String getHoras() {
        return Horas;
    }

    public void setHoras(String horas) {
        Horas = horas;
    }

    public String getMinutos() {
        return Minutos;
    }

    public void setMinutos(String minutos) {
        Minutos = minutos;
    }

    public String getTempo() {
        return Tempo;
    }

    public void setTempo(String tempo) {
        Tempo = tempo;
    }
}
