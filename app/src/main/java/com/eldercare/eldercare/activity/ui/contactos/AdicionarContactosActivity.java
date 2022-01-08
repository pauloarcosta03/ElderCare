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
    private Contacto contactoAtual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_contactos);

        fab = findViewById(R.id.fabAddContacto);

        editNome = findViewById(R.id.editNome);
        editNumero = findViewById(R.id.editNumero);
        editCategoria = findViewById(R.id.editCategoria);

        contactoAtual = (Contacto) getIntent().getSerializableExtra("contacto");

        //Caso o utilizador queira editar uma nota, o código preenche os campos automáticamente
        if (contactoAtual != null){
            editNome.setText(contactoAtual.getNome());
            editNumero.setText(contactoAtual.getNumero());
            editCategoria.setText(contactoAtual.getCategoria());
        }

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

                //Caso o utilizador queira editar uma nota, em vez de criar uma nota com id novo, ela reutiliza o id
                if (contactoAtual != null){

                    contacto.setNome(textoNome);
                    contacto.setNumero(textoNumero);
                    contacto.setCategoria(textoCategoria);
                    //preciso da key para saber onde atualizarw
                    contacto.setKey(contactoAtual.getKey());

                    contacto.editar();
                    finish();

                }else{

                    contacto.setNome(textoNome);
                    contacto.setNumero(textoNumero);
                    contacto.setCategoria(textoCategoria);

                    contacto.guardar();
                    finish();

                }

            }else {
                Toast.makeText(this, "Preencha o número primeiro.", Toast.LENGTH_LONG).show();
            }
        }else {
            Toast.makeText(this, "Preencha o nome primeiro.", Toast.LENGTH_LONG).show();
        }


    }
}