package com.eldercare.eldercare.api;

import com.eldercare.eldercare.model.NotificacaoDados;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface NotificacaoService {

    @Headers(
            {"Authorization:key=AAAAnicdCfY:APA91bHsYANGoOeAanO0LsY46Wzky6JmMMhPSH-RM0CL90fKFp_CWMZUlBaGaG6Ngtm6NGYZIDXXfbAg9CiNWwJbiZdDfc7yuxXbd8ELqAt7v0ni6rLeAH4m3PKdYHsNisxdkLPTyIQ3",
            "Content-Type:application/json"})
    @POST("send")
    Call<NotificacaoDados> guardarNotificacao(@Body NotificacaoDados notificacaoDados);
}
