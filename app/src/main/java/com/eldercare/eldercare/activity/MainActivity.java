package com.eldercare.eldercare.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatDelegate;

import com.eldercare.eldercare.R;
import com.eldercare.eldercare.config.ConfiguracaoFirebase;
import com.google.firebase.auth.FirebaseAuth;
import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide;

public class MainActivity extends IntroActivity {

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        //Não permite darkmode na aplicação toda
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        //Para não aparecerem butões nas intros
        setButtonBackVisible(false);
        setButtonNextVisible(false);

        //Criar slides da intro
        addSlide( new FragmentSlide.Builder()
                .background(R.color.colorPrimaryDark)
                .fragment(R.layout.intro_1)
                .build());

        addSlide( new FragmentSlide.Builder()
                .background(R.color.colorPrimaryDark)
                .fragment(R.layout.intro_2)
                .build());

        addSlide( new FragmentSlide.Builder()
                .background(R.color.colorPrimaryDark)
                .fragment(R.layout.intro_login)
                .canGoForward(false)
                .build());

    }

    @Override
    protected void onStart() {
        super.onStart();
        verificarLogin();
    }

    public void verificarLogin(){

        autenticacao = ConfiguracaoFirebase.getFirebaseAutorizacao();

        //autenticacao.signOut();
        if (autenticacao.getCurrentUser() != null){

            abrirActivityPrincipal();
        }

    }

    public void abrirActivityPrincipal(){
        startActivity(new Intent(this, PrincipalActivity.class));
    }

    public void abrirLogin(View view){
        startActivity(new Intent(this, LoginActivity.class));
    }

    public void abrirSignin(View view){
        startActivity(new Intent(this, RegistoActivity.class));
    }

}
