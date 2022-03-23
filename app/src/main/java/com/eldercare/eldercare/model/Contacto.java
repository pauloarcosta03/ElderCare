package com.eldercare.eldercare.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.eldercare.eldercare.api.NotificacaoService;
import com.eldercare.eldercare.config.ConfiguracaoFirebase;
import com.eldercare.eldercare.helper.Base64Custom;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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

        /*---------------------------------
        ---------Mandar notificacao--------
        ---------------------------------*/

        String baseUrl = "https://fcm.googleapis.com/fcm/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //montar o objeto de notificacao

        DatabaseReference tokenRef = firebaseRef
                .child("utilizadores")
                .child(this.idPaciente);

        tokenRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String token = snapshot.child("token").getValue().toString();
                String to = token;
                Notificacao notificacao = new Notificacao("ElderCare", "O cuidador acabou de editar um contacto!");
                NotificacaoDados notificacaoDados = new NotificacaoDados(to, notificacao);

                NotificacaoService service = retrofit.create(NotificacaoService.class);
                Call<NotificacaoDados> call = service.guardarNotificacao(notificacaoDados);
                call.enqueue(new Callback<NotificacaoDados>() {
                    @Override
                    public void onResponse(Call<NotificacaoDados> call, Response<NotificacaoDados> response) {
                        if(response.isSuccessful()){

                            Log.i("codigo", "codigo: " + response.code());

                        }
                    }

                    @Override
                    public void onFailure(Call<NotificacaoDados> call, Throwable t) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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

        /*---------------------------------
        ---------Mandar notificacao--------
        ---------------------------------*/

        String baseUrl = "https://fcm.googleapis.com/fcm/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //montar o objeto de notificacao

        DatabaseReference tokenRef = firebaseRef
                .child("utilizadores")
                .child(this.idPaciente);

        tokenRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String token = snapshot.child("token").getValue().toString();
                String to = token;
                Notificacao notificacao = new Notificacao("ElderCare", "O cuidador acabou de adicionar um contacto!");
                NotificacaoDados notificacaoDados = new NotificacaoDados(to, notificacao);

                NotificacaoService service = retrofit.create(NotificacaoService.class);
                Call<NotificacaoDados> call = service.guardarNotificacao(notificacaoDados);
                call.enqueue(new Callback<NotificacaoDados>() {
                    @Override
                    public void onResponse(Call<NotificacaoDados> call, Response<NotificacaoDados> response) {
                        if(response.isSuccessful()){

                            Log.i("codigo", "codigo: " + response.code());

                        }
                    }

                    @Override
                    public void onFailure(Call<NotificacaoDados> call, Throwable t) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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
