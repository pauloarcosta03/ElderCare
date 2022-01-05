package com.eldercare.eldercare.activity.ui.contactos;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.eldercare.eldercare.R;
import com.eldercare.eldercare.model.Contacto;
import com.github.clans.fab.FloatingActionButton;

public class AdicionarContactosActivity extends AppCompatActivity {

    private EditText editNome, editNumero, editCategoria;
    private FloatingActionButton fab;
    private Contacto contacto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_contactos);

        fab = findViewById(R.id.fabAddContacto);

        editNome = findViewById(R.id.editNome);
        editNumero = findViewById(R.id.editNumero);
        editCategoria = findViewById(R.id.editCategoria);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarContacto();
            }
        });

    }

    public void guardarContacto(){
        contacto = new Contacto();

        String textoNome = editNome.getText().toString();
        String textoNumero = String.valueOf(editNumero.getText());
        String textoCategoria = editCategoria.getText().toString();

        //Verifica se os campos estão todos preenchidos
        //Exceto a categoria(não é obrigatória)
        if (!textoNome.isEmpty()){
            if (!textoNumero.isEmpty()){
                contacto.setNome(textoNome);
                contacto.setNumero(textoNumero);
                contacto.setCategoria(textoCategoria);

                contacto.guardar();
                finish();
            }else {
                Toast.makeText(this, "Preencha o número primeiro.", Toast.LENGTH_LONG).show();
            }
        }else {
            Toast.makeText(this, "Preencha o nome primeiro.", Toast.LENGTH_LONG).show();
        }


    }
}