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

public class Pressao implements Serializable {

    private String data;
    private String dataAnterior;
    private String horas;
    private String minutos;
    private String tempo;
    private String sistolica;
    private String diastolica;
    private String paciente;
    private String idPaciente;
    private String idPacienteAnterior;
    private String key;

    public void editar(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseRef();
        FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        String idUtilizador = Base64Custom.codificarBase64(autenticacao.getCurrentUser().getEmail());

        //fazer o id do dia
        String[] dataSplit = this.data.split("/");

        String idDia = dataSplit[0] + dataSplit[1] + dataSplit[2];

        firebaseRef.child("pressao")
                .child(idUtilizador)
                .child(idDia)
                .child(this.getKey())
                .setValue(this);

        firebaseRef.child("pressao")
                .child(this.getIdPaciente())
                .child(idDia)
                .child(this.getKey())
                .setValue(this);

        if(!(this.getData()).equals(this.getDataAnterior())){
            //fazer o id do dia anterior
            String[] dataAnteriorSplit = this.dataAnterior.split("/");

            String idDiaAnterior = dataAnteriorSplit[0] + dataAnteriorSplit[1] + dataAnteriorSplit[2];

            firebaseRef.child("pressao")
                    .child(idUtilizador)
                    .child(idDiaAnterior)
                    .child(this.getKey()).removeValue();

            firebaseRef.child("pressao")
                    .child(this.getIdPaciente())
                    .child(idDiaAnterior)
                    .child(this.getKey()).removeValue();

            //para facilitar a atualiza????o de paciente
            idDia = idDiaAnterior;
        }

        if(!(this.getIdPaciente().equals(this.getIdPacienteAnterior()))){

            firebaseRef.child("pressao")
                    .child(this.idPacienteAnterior)
                    .child(idDia)
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

        //guardar dados da notifica????o
        DisplayNotificacao displayNotificacao = new DisplayNotificacao();

        displayNotificacao.setTitulo("Press??o Arterial");
        displayNotificacao.setTempo(String.format("%02d", LocalDateTime.now().getHour()) + ":" + String.format("%02d", LocalDateTime.now().getMinute()));
        displayNotificacao.setDescricao("Editaram um valor de press??o arterial.");

        tokenRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String token = snapshot.child("token").getValue().toString();
                String to = token;
                Notificacao notificacao = new Notificacao("ElderCare", "O cuidador acabou de editar uma medi????o de press??o arterial!");
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

        //fazer o id do dia
        String[] dataSplit = this.data.split("/");

        String idDia = dataSplit[0] + dataSplit[1] + dataSplit[2];

        String idPressao = firebaseRef.push().getKey();

        firebaseRef.child("pressao")
                .child(idUtilizador)
                .child(idDia)
                .child(idPressao)
                .setValue(this);

        firebaseRef.child("pressao")
                .child(this.getIdPaciente())
                .child(idDia)
                .child(idPressao)
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

        //guardar dados da notifica????o
        DisplayNotificacao displayNotificacao = new DisplayNotificacao();

        displayNotificacao.setTitulo("Press??o Arterial");
        displayNotificacao.setTempo(String.format("%02d", LocalDateTime.now().getHour()) + ":" + String.format("%02d", LocalDateTime.now().getMinute()));
        displayNotificacao.setDescricao("Adicionaram um valor de press??o arterial.");

        tokenRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String token = snapshot.child("token").getValue().toString();
                String to = token;
                Notificacao notificacao = new Notificacao("ElderCare", "O cuidador acabou de adicionar uma medi????o de press??o arterial!");
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

    public Pressao() {
    }

    @Exclude
    public String getDataAnterior() {
        return dataAnterior;
    }

    public void setDataAnterior(String dataAnterior) {
        this.dataAnterior = dataAnterior;
    }

    @Exclude
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
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

    public String getIdPaciente() {
        return idPaciente;
    }

    public void setIdPaciente(String idPaciente) {
        this.idPaciente = idPaciente;
    }

    @Exclude
    public String getIdPacienteAnterior() {
        return idPacienteAnterior;
    }

    public void setIdPacienteAnterior(String idPacienteAnterior) {
        this.idPacienteAnterior = idPacienteAnterior;
    }

    public String getPaciente() {
        return paciente;
    }

    public void setPaciente(String paciente) {
        this.paciente = paciente;
    }

    public String getSistolica() {
        return sistolica;
    }

    public void setSistolica(String sistolica) {
        this.sistolica = sistolica;
    }

    public String getDiastolica() {
        return diastolica;
    }

    public void setDiastolica(String diastolica) {
        this.diastolica = diastolica;
    }
}
