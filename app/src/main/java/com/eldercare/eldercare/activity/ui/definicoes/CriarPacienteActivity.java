package com.eldercare.eldercare.activity.ui.definicoes;

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

public class CriarPacienteActivity extends AppCompatActivity {

    private EditText editNome, editEmail, editPassword;
    private Button botaoCriar;

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_paciente);

        editNome = findViewById(R.id.editNome);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);

        botaoCriar = findViewById(R.id.botaoCriar);

        String textoNome = editNome.getText().toString();
        String textoEmail = editEmail.getText().toString();
        String textoPassword = editPassword.getText().toString();

        botaoCriar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!editNome.getText().toString().isEmpty()){
                    if(!editEmail.getText().toString().isEmpty()){
                        if(!editPassword.getText().toString().isEmpty()){

                            registarPaciente();

                        }else{
                            Toast.makeText(getApplicationContext(), "Introduz um nome.", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(getApplicationContext(), "Introduz um nome.", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "Introduz um nome.", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }

    public void registarPaciente(){

        String textoNome = editNome.getText().toString();
        String textoEmail = editEmail.getText().toString();
        String textoPassword = editPassword.getText().toString();

        Utilizador utilizador = new Utilizador();
        utilizador.setNome(textoNome);
        utilizador.setEmail(textoEmail);
        utilizador.setPassword(textoPassword);
        utilizador.setTipo("p");

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        //O registo é criado no firebase
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