package com.eldercare.eldercare.activity.ui.definicoes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class CriarPacienteActivity extends AppCompatActivity {

    private EditText editNome, editEmail, editPassword;
    private Button botaoCriar;
    private Button buttonVerPass;
    boolean visivelPass = false;

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_paciente);

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

        //permite fazer alterações à toolbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Criar Novo Paciente");

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

    //em vez de dar reset à activity anterior, dá finish
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void registarPaciente(){

        String textoNome = editNome.getText().toString();
        String textoEmail = editEmail.getText().toString();
        String textoPassword = editPassword.getText().toString();

        //buscar o id do cuidador do paciente
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseRef();
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        String idCuidador = Base64Custom.codificarBase64(autenticacao.getCurrentUser().getEmail());

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
                            utilizador.setCuidador(idCuidador);
                            utilizador.setPassword(Base64Custom.codificarBase64(textoPassword));
                            utilizador.guardarNome();

                            //dar login de novo ao cuidador
                            DatabaseReference dadosRef = firebaseRef
                                    .child("utilizadores")
                                    .child(idCuidador);

                            dadosRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    Utilizador cuidador = snapshot.getValue(Utilizador.class);

                                    autenticacao.signInWithEmailAndPassword(
                                            Base64Custom.descodificarBase64(idCuidador),
                                            Base64Custom.descodificarBase64(cuidador.getPassword()));
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

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