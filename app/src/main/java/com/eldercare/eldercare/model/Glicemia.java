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

public class Glicemia implements Serializable {

    private String glicose;
    private String data;
    private String dataAnterior;
    private String horas;
    private String minutos;
    private String tempo;
    private String paciente;
    private String idPaciente;
    private String idPacienteAnterior;
    private String key;

    //notificacao
    private Retrofit retrofit;
    private String baseUrl;
    private String token;

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

        firebaseRef.child("glicemia")
                .child(this.idPaciente)
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

            firebaseRef.child("glicemia").child(this.idPaciente)
                    .child(idDiaAnterior)
                    .child(this.getKey()).removeValue();

            //para facilitar a atualização de paciente
            idDia = idDiaAnterior;
        }

        if(!(this.getIdPaciente().equals(this.getIdPacienteAnterior()))){

            firebaseRef.child("glicemia")
                    .child(this.idPacienteAnterior)
                    .child(idDia)
                    .child(this.getKey()).removeValue();

        }

        /*---------------------------------
        ---------Mandar notificacao--------
        ---------------------------------*/

        baseUrl = "https://fcm.googleapis.com/fcm/";
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //montar o objeto de notificacao

        DatabaseReference tokenRef = firebaseRef
                .child("utilizadores")
                .child(this.idPaciente);

        //guardar dados da notificação
        DisplayNotificacao displayNotificacao = new DisplayNotificacao();

        displayNotificacao.setTitulo("Glicemia");
        displayNotificacao.setTempo(String.format("%02d", LocalDateTime.now().getHour()) + ":" + String.format("%02d", LocalDateTime.now().getMinute()));
        displayNotificacao.setDescricao("Editaram um valor de glicemia.");

        tokenRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                token = snapshot.child("token").getValue().toString();
                String to = token;
                Notificacao notificacao = new Notificacao("ElderCare", "O cuidador acabou de editar uma medição de glicemia!");
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

    public void guardar(){

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseRef();
        FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        String idUtilizador = Base64Custom.codificarBase64(autenticacao.getCurrentUser().getEmail());

        String idGlicemia = firebaseRef.push().getKey();

        //fazer o id do dia
        String[] dataSplit = this.data.split("/");

        String idDia = dataSplit[0] + dataSplit[1] + dataSplit[2];

        firebaseRef.child("glicemia")
                .child(idUtilizador)
                .child(idDia)
                .child(idGlicemia)
                .setValue(this);

        firebaseRef.child("glicemia")
                .child(this.idPaciente)
                .child(idDia)
                .child(idGlicemia)
                .setValue(this);

        /*---------------------------------
        ---------Mandar notificacao--------
        ---------------------------------*/

        baseUrl = "https://fcm.googleapis.com/fcm/";
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //montar o objeto de notificacao

        DatabaseReference tokenRef = firebaseRef
                .child("utilizadores")
                .child(this.idPaciente);

        //guardar dados da notificação
        DisplayNotificacao displayNotificacao = new DisplayNotificacao();

        displayNotificacao.setTitulo("Glicemia");
        displayNotificacao.setTempo(String.format("%02d", LocalDateTime.now().getHour()) + ":" + String.format("%02d", LocalDateTime.now().getMinute()));
        displayNotificacao.setDescricao("Adicionaram um valor de Glicemia.");

        tokenRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                token = snapshot.child("token").getValue().toString();
                String to = token;
                Notificacao notificacao = new Notificacao("ElderCare", "O cuidador acabou de adicionar uma medição de glicemia!");
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
