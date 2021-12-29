package com.eldercare.eldercare.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
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

    private Utilizador utilizador;

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editEmail = findViewById(R.id.editEmailLogin);
        editPassword = findViewById(R.id.editPasswordLogin);

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

    //classe para fazer login
    public void fazerLogin(){
        autenticacao = ConfiguracaoFirebase.getFirebaseAutorizacao();

        autenticacao.signInWithEmailAndPassword(
                utilizador.getEmail(),
                utilizador.getPassword()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(getApplicationContext(),
                            "Login feito com sucesso.",
                            Toast.LENGTH_LONG).show();
                    finish();
                }else{
                    //cria a mensagem de erro
                    String excecao = "";
                    try{
                        throw task.getException();
                    }catch (FirebaseAuthInvalidUserException e){
                        excecao ="Esse e-mail não existe.";
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        excecao = "E-mail e password não coincidem.";
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