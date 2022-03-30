package com.eldercare.eldercare.notificacao;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.eldercare.eldercare.R;
import com.eldercare.eldercare.activity.NotificacoesActivity;
import com.eldercare.eldercare.activity.PrincipalActivity;
import com.eldercare.eldercare.config.ConfiguracaoFirebase;
import com.eldercare.eldercare.helper.Base64Custom;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseRef();
    private FirebaseAuth autenticacao;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage notificacao) {

        if(notificacao.getNotification() != null){

            String titulo = notificacao.getNotification().getTitle();
            String corpo = notificacao.getNotification().getBody();

            Log.i("notificação", "titulo: " + titulo + "\ncorpo: " + corpo);

            enviarNotificacao(titulo, corpo);
        }

    }

    private void enviarNotificacao(String titulo, String corpo){

        //configurações notificação
        String canal = getString(R.string.default_notification_channel_id);
        Uri uriSom = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Intent intent = new Intent(this, NotificacoesActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        //criar notificação
        NotificationCompat.Builder notificacao = new NotificationCompat.Builder(this, canal)
                .setContentTitle(titulo)
                .setContentText(corpo)
                .setSmallIcon(R.drawable.ic_menu_calendario)
                .setSound(uriSom)
                .setAutoCancel(true)//ao clicar desaparece
                .setContentIntent(pendingIntent);

        //Recupera notificationManager
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //verifica se a versão de android é compativel (apartir da versão oreo)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(canal, "canal", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        //envia a notificação
        notificationManager.notify(0, notificacao.build());
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseRef();
        FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        String idUtilizador = Base64Custom.codificarBase64(autenticacao.getCurrentUser().getEmail());

        firebaseRef.child("utilizadores")
                .child(idUtilizador)
                .child("token")
                .setValue(s);

    }
}
