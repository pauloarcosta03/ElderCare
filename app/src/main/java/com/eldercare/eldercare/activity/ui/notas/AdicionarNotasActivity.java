package com.eldercare.eldercare.activity.ui.notas;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.eldercare.eldercare.R;
import com.eldercare.eldercare.model.Nota;
import com.github.clans.fab.FloatingActionButton;

public class AdicionarNotasActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private EditText editTitulo, editDescricao;
    private Nota nota;

    private Nota notaAtual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_notas);

        editTitulo = findViewById(R.id.editTitulo);
        editDescricao = findViewById(R.id.editDescricao);
        fab = findViewById(R.id.fabAddNotas);

        //Caso o utilizador queira editar uma nota, o código preenche os campos automáticamente
        notaAtual = (Nota) getIntent().getSerializableExtra("nota");

        if (notaAtual != null){

            editTitulo.setText(notaAtual.getTitulo());
            editDescricao.setText(notaAtual.getDescricao());

        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarNota();
            }
        });

    }

    public void guardarNota(){
        nota = new Nota();

        String textoTitulo = editTitulo.getText().toString();
        String textoDescricao = editDescricao.getText().toString();

        //Verifica se os campos estão todos preenchidos
        if (!textoTitulo.isEmpty()){
            if (!textoDescricao.isEmpty()){

                //Caso o utilizador queira editar uma nota, em vez de criar uma nota com id novo, ela reutiliza o id
                if (notaAtual != null){

                    nota.setTitulo(textoTitulo);
                    nota.setDescricao(textoDescricao);
                    nota.setKey(notaAtual.getKey());

                    nota.editar();
                    finish();

                }else{
                    nota.setTitulo(textoTitulo);
                    nota.setDescricao(textoDescricao);

                    nota.guardar();
                    finish();
                }


            }else{
                Toast.makeText(this, "Preencha o conteúdo primeiro.", Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(this, "Preencha o título primeiro.", Toast.LENGTH_LONG).show();
        }


    }

}