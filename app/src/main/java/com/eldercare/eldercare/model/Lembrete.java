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
    private String Key;

    public void guardar(){

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseRef();
        FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        String idUtilizador = Base64Custom.codificarBase64(autenticacao.getCurrentUser().getEmail());

        firebaseRef.child("lembretes")
                .child(idUtilizador)
                .push()
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