package com.eldercare.eldercare.model;

import com.eldercare.eldercare.config.ConfiguracaoFirebase;
import com.eldercare.eldercare.helper.Base64Custom;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.io.Serializable;

public class Glicemia implements Serializable {

    private String glicose;
    private String data;
    private String dataAnterior;
    private String horas;
    private String minutos;
    private String tempo;
    private String paciente;
    private String key;

    public Glicemia() {
    }

    public void editar(){

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseRef();
        FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        String idUtilizador = Base64Custom.codificarBase64(autenticacao.getCurrentUser().getEmail());

        //fazer o id do dia
        String[] dataSplit = this.data.split("/");

        String idDia = dataSplit[0] + dataSplit[1] + dataSplit[2];

        firebaseRef.child("glicemia")
                .child(idUtilizador)
                .child(idDia)
                .child(this.getKey())
                .setValue(this);

        if(!(this.getData()).equals(this.getDataAnterior())){
            //fazer o id do dia anterior
            String[] dataAnteriorSplit = this.dataAnterior.split("/");

            String idDiaAnterior = dataAnteriorSplit[0] + dataAnteriorSplit[1] + dataAnteriorSplit[2];

            firebaseRef.child("glicemia").child(idUtilizador)
                    .child(idDiaAnterior)
                    .child(this.getKey()).removeValue();
        }

    }

    public void guardar(){

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseRef();
        FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        String idUtilizador = Base64Custom.codificarBase64(autenticacao.getCurrentUser().getEmail());

        //fazer o id do dia
        String[] dataSplit = this.data.split("/");

        String idDia = dataSplit[0] + dataSplit[1] + dataSplit[2];

        firebaseRef.child("glicemia")
                .child(idUtilizador)
                .child(idDia)
                .push()
                .setValue(this);

    }

    public String getGlicose() {
        return glicose;
    }

    public void setGlicose(String glicose) {
        this.glicose = glicose;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Exclude
    public String getDataAnterior() {
        return dataAnterior;
    }

    public void setDataAnterior(String dataAnterior) {
        this.dataAnterior = dataAnterior;
    }

    public String getHoras() {
        return horas;
    }

    public void setHoras(String horas) {
        this.horas = horas;
    }

    public String getMinutos() {
        return minutos;
    }

    public void setMinutos(String minutos) {
        this.minutos = minutos;
    }

    public String getTempo() {
        return tempo;
    }

    public void setTempo(String tempo) {
        this.tempo = tempo;
    }

    public String getPaciente() {
        return paciente;
    }

    public void setPaciente(String paciente) {
        this.paciente = paciente;
    }

    @Exclude
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
