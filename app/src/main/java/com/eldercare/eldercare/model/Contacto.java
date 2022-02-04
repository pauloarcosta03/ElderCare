package com.eldercare.eldercare.model;

import androidx.annotation.NonNull;

import com.eldercare.eldercare.config.ConfiguracaoFirebase;
import com.eldercare.eldercare.helper.Base64Custom;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;

//implements Serializable serve para passar um contato de uma activity para a outra
public class Contacto implements Serializable {

    private String nome;
    private String categoria;
    private String numero;
    private String paciente;
    private String idPaciente;
    private String idPacienteAnterior;
    private String key;

    public Contacto() {
    }

    public void editar(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseRef();
        FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        String idUtilizador = Base64Custom.codificarBase64(autenticacao.getCurrentUser().getEmail());

        firebaseRef.child("contactos")
                .child(idUtilizador)
                .child(this.getKey())
                .setValue(this);

        firebaseRef.child("contactos")
                .child(this.idPaciente)
                .child(this.getKey())
                .setValue(this);

        if(!(this.getIdPaciente().equals(this.getIdPacienteAnterior()))){

            firebaseRef.child("contactos")
                    .child(this.idPacienteAnterior)
                    .child(this.getKey()).removeValue();

        }

    }

    public void guardar(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseRef();
        FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        String idUtilizador = Base64Custom.codificarBase64(autenticacao.getCurrentUser().getEmail());

        String idContacto = firebaseRef.push().getKey();

        firebaseRef.child("contactos")
                .child(idUtilizador)
                .child(idContacto)
                .setValue(this);

        firebaseRef.child("contactos")
                .child(this.idPaciente)
                .child(idContacto)
                .setValue(this);

    }

    @Exclude//para não guardar a key
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = String.valueOf(numero);
    }
}
