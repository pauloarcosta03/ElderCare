package com.eldercare.eldercare.activity.ui.notas;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.eldercare.eldercare.R;
import com.eldercare.eldercare.model.Nota;
import com.github.clans.fab.FloatingActionButton;

public class AdicionarNotasActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private EditText editTitulo, editDescricao;
    private Nota nota;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_notas);

        editTitulo = findViewById(R.id.editTitulo);
        editDescricao = findViewById(R.id.editDescricao);

    }

    public void guardarNota(View view){
        nota = new Nota();

        String textoTitulo = editTitulo.getText().toString();
        String textoDescricao = editDescricao.getText().toString();

        nota.setTitulo(textoTitulo);
        nota.setDescricao(textoDescricao);

        nota.guardar();
        finish();
    }

}