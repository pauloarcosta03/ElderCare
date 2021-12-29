package com.eldercare.eldercare.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.eldercare.eldercare.R;
import com.eldercare.eldercare.config.ConfiguracaoFirebase;
import com.eldercare.eldercare.helper.Base64Custom;
import com.eldercare.eldercare.model.Utilizador;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class RegistoActivity extends AppCompatActivity {

    //variáveis do layout
    private EditText editNome, editEmail, editPassword;
    private Button botaoRegisto;

    //classes
    private Utilizador utilizador;

    //variáveis firebase
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registo);

        editNome = findViewById(R.id.editNomeSignup);
        editEmail = findViewById(R.id.editEmailSignup);
        editPassword = findViewById(R.id.editPasswordSignup);

        botaoRegisto = findViewById(R.id.botaoSignup);

        botaoRegisto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String textoNome = editNome.getText().toString();
                String textoEmail = editEmail.getText().toString();
                String textoPassword = editPassword.getText().toString();

                //Verificação do preenchimento dos campos
                if (!textoNome.isEmpty()){
                    if (!textoEmail.isEmpty()){
                        if (!textoPassword.isEmpty()){

                            utilizador = new Utilizador();
                            utilizador.setNome(textoNome);
                            utilizador.setEmail(textoEmail);
                            utilizador.setPassword(textoPassword);

                            registarUtilizador();

                        }else{
                            Toast.makeText(getApplicationContext(),
                                    "Por favor preencha a palavra-passe.",
                                    Toast.LENGTH_LONG).show();
                        }
                    }else{
                        Toast.makeText(getApplicationContext(),
                                "Por favor preencha o e-mail.",
                                Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(),
                            "Por favor preencha o nome.",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //classe para registar o user
    public void registarUtilizador(){

        autenticacao = ConfiguracaoFirebase.getFirebaseAutorizacao();
        autenticacao.createUserWithEmailAndPassword(
                utilizador.getEmail(),
                utilizador.getPassword())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            //Se o registo realizou-se com sucesso
                            String idUtilizador = Base64Custom.codificarBase64(utilizador.getEmail());
                            utilizador.setIdUtilizador(idUtilizador);
                            utilizador.guardarNome();
                            finish();
                        }else{

                            //mostra o porquê de a autenticação não ter sido um sucesso
                            String excecao = "";
                            try{
                                throw task.getException();
                            }catch(FirebaseAuthWeakPasswordException e){
                                excecao = "Introduza uma password mais forte!";
                                Log.e("FirebaseAuthError => ", e.getMessage());
                            }catch (FirebaseAuthInvalidCredentialsException e){
                                excecao = "Introduza um e-mail válido";
                            }catch (FirebaseAuthUserCollisionException e){
                                excecao = "Essa conta já existe";
                            }catch (Exception e){
                                excecao = "Erro ao criar utilizador: " + e.getMessage();
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