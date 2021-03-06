package com.eldercare.eldercare.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.eldercare.eldercare.R;
import com.eldercare.eldercare.config.ConfiguracaoFirebase;
import com.eldercare.eldercare.model.Utilizador;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class LoginActivity extends AppCompatActivity {

    private EditText editEmail, editPassword;
    private Button botaoLogin;
    private Button buttonVerPass;
    boolean visivelPass = false;

    private Utilizador utilizador;

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Login");

        editEmail = findViewById(R.id.editEmailLogin);
        editPassword = findViewById(R.id.editPasswordLogin);
        buttonVerPass = findViewById(R.id.buttonVerPass);

        //Mudar pass de invisivel para visivel
        buttonVerPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(visivelPass){
                    visivelPass = false;
                }else{
                    visivelPass = true;
                }

                if(visivelPass){
                    editPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }else{
                    editPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });

        botaoLogin = findViewById(R.id.botaoLogin);

        botaoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String textoEmail = editEmail.getText().toString();
                String textoPassword = editPassword.getText().toString();

                if (!textoEmail.isEmpty()){
                    if (!textoPassword.isEmpty()){

                        utilizador = new Utilizador();
                        utilizador.setEmail(textoEmail);
                        utilizador.setPassword(textoPassword);

                        //chama a classe para o login
                        fazerLogin();

                    }else {
                        Toast.makeText(getApplicationContext(),
                                "Por favor preencha a palavra-passe.",
                                Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(getApplicationContext(),
                            "Por favor preencha o e-mail.",
                            Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    //em vez de dar reset ?? activity anterior, d?? finish
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //classe para fazer login
    public void fazerLogin(){
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        //faz login com o e-mail e com a password que o utilizador introduziram
        autenticacao.signInWithEmailAndPassword(
                utilizador.getEmail(),
                utilizador.getPassword()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    finish();
                }else{
                    //cria a mensagem de erro
                    String excecao = "";
                    try{
                        throw task.getException();
                    }catch (FirebaseAuthInvalidUserException e){
                        excecao ="Esse e-mail n??o existe.";
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        excecao = "E-mail e password n??o coincidem.";
                    }catch (Exception e){
                        excecao = "Erro ao fazer login: " + e.getMessage();
                        e.printStackTrace();
                    }

                    Toast.makeText(getApplicationContext(),
                            excecao,
                            Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}