package com.eldercare.eldercare.activity.ui.definicoes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.eldercare.eldercare.R;
import com.eldercare.eldercare.config.ConfiguracaoFirebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class EditarPassActivity extends AppCompatActivity {

    private Button buttonVerAtual;
    private EditText textPassAtual;
    private Button buttonVerNovo1;
    private EditText textPassNova1;
    private Button buttonVerNovo2;
    private EditText textPassNova2;

    private Button buttonMudarPass;

    boolean visivelPassAtual = false;
    boolean visivelPassNova1 = false;
    boolean visivelPassNova2 = false;

    String passwordAtual;
    String passwordNova1;
    String passwordNova2;

    FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    FirebaseUser utilizador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_pass);

        getSupportActionBar().setTitle("Editar Password");

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        buttonMudarPass = findViewById(R.id.ButtonMudarPass);

        buttonVerAtual = findViewById(R.id.buttonVerAtual);
        textPassAtual = findViewById(R.id.textPassAtual);

        buttonVerNovo1 = findViewById(R.id.buttonVerNovo1);
        textPassNova1 = findViewById(R.id.textPassNova1);

        buttonVerNovo2 = findViewById(R.id.buttonVerNovo2);
        textPassNova2 = findViewById(R.id.textPassNova2);

        //Mudar pass atual de invisivel para visivel
        buttonVerAtual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(visivelPassAtual){
                    visivelPassAtual = false;
                }else{
                    visivelPassAtual = true;
                }

                if(visivelPassAtual){
                    textPassAtual.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }else{
                    textPassAtual.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });

        //Mudar pass nova de invisivel para visivel
        buttonVerNovo1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(visivelPassNova1){
                    visivelPassNova1 = false;
                }else{
                    visivelPassNova1 = true;
                }

                if(visivelPassNova1){
                    textPassNova1.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }else{
                    textPassNova1.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });

        //Mudar confirmação de nova pass de invisivel para visivel
        buttonVerNovo2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(visivelPassNova2){
                    visivelPassNova2 = false;
                }else{
                    visivelPassNova2 = true;
                }

                if(visivelPassNova2){
                    textPassNova2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }else{
                    textPassNova2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });

        //mudar a password
        buttonMudarPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                passwordAtual = textPassAtual.getText().toString();
                passwordNova1 = textPassNova1.getText().toString();
                passwordNova2 = textPassNova2.getText().toString();

                if(!passwordAtual.isEmpty())
                {
                    if(!passwordNova1.isEmpty())
                    {
                        if(!passwordNova2.isEmpty())
                        {
                            //verificar se as 2 passwords coincidem
                            if(passwordNova1.equals(passwordNova2))
                            {
                                mudarPass();
                            }else{
                                Toast.makeText(getApplicationContext(), "As passwords novas não coincidem", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(getApplicationContext(), "Confirme a password", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(getApplicationContext(), "Introduza a password nova", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "Introduza a password atual", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    public void mudarPass(){

        String email = autenticacao.getCurrentUser().getEmail();

        autenticacao.signInWithEmailAndPassword(email, passwordAtual)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(!task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "Password atual está errada", Toast.LENGTH_SHORT).show();
                }else{
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    user.updatePassword(passwordNova1)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(getApplicationContext(), "Password alterada com sucesso.", Toast.LENGTH_LONG).show();
                                        finish();
                                    }else{
                                        Toast.makeText(getApplicationContext(), "Não foi possivel alterar a password.", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            }
        });

    }
}