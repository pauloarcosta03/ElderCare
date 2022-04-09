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
import java.time.LocalDateTime;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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

        //guardar dados da notificação
        DisplayNotificacao displayNotificacao = new DisplayNotificacao();

        displayNotificacao.setTitulo(this.Titulo);
        displayNotificacao.setTempo(String.format("%02d", LocalDateTime.now().getHour()) + ":" + String.format("%02d", LocalDateTime.now().getMinute()));
        displayNotificacao.setDescricao("Adicionaram este lembrete.");

        tokenRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String token = snapshot.child("token").getValue().toString();
                String to = token;
                Notificacao notificacao = new Notificacao("ElderCare", "O cuidador acabou de adicionar um lembrete!");
                NotificacaoDados notificacaoDados = new NotificacaoDados(to, notificacao);

                NotificacaoService service = retrofit.create(NotificacaoService.class);
                Call<NotificacaoDados> call = service.guardarNotificacao(notificacaoDados);
                call.enqueue(new Callback<NotificacaoDados>() {
                    @Override
                    public void onResponse(Call<NotificacaoDados> call, Response<NotificacaoDados> response) {
                        if(response.isSuccessful()){

                            Log.i("codigo", "codigo: " + response.code());

                            String idNotificacao = firebaseRef.push().getKey();

                            firebaseRef.child("notificacoes")
                                    .child(idPaciente)
                                    .child(idNotificacao)
                                    .setValue(displayNotificacao);

                            firebaseRef.child("notificacoes")
                                    .child(idUtilizador)
                                    .child(idNotificacao)
                                    .setValue(displayNotificacao);

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

        //guardar dados da notificação
        DisplayNotificacao displayNotificacao = new DisplayNotificacao();

        displayNotificacao.setTitulo(this.Titulo);
        displayNotificacao.setTempo(String.format("%02d", LocalDateTime.now().getHour()) + ":" + String.format("%02d", LocalDateTime.now().getMinute()));
        displayNotificacao.setDescricao("Editaram este lembrete.");

        tokenRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String token = snapshot.child("token").getValue().toString();
                String to = token;
                Notificacao notificacao = new Notificacao("ElderCare", "O cuidador acabou de editar um lembrete!");
                NotificacaoDados notificacaoDados = new NotificacaoDados(to, notificacao);

                NotificacaoService service = retrofit.create(NotificacaoService.class);
                Call<NotificacaoDados> call = service.guardarNotificacao(notificacaoDados);
                call.enqueue(new Callback<NotificacaoDados>() {
                    @Override
                    public void onResponse(Call<NotificacaoDados> call, Response<NotificacaoDados> response) {
                        if(response.isSuccessful()){

                            Log.i("codigo", "codigo: " + response.code());

                            String idNotificacao = firebaseRef.push().getKey();

                            firebaseRef.child("notificacoes")
                                    .child(idPaciente)
                                    .child(idNotificacao)
                                    .setValue(displayNotificacao);

                            firebaseRef.child("notificacoes")
                                    .child(idUtilizador)
                                    .child(idNotificacao)
                                    .setValue(displayNotificacao);

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
