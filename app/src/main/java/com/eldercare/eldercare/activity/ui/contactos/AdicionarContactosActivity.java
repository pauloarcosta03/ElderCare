package com.eldercare.eldercare.activity.ui.contactos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.eldercare.eldercare.R;
import com.eldercare.eldercare.config.ConfiguracaoFirebase;
import com.eldercare.eldercare.helper.Base64Custom;
import com.eldercare.eldercare.model.Contacto;
import com.eldercare.eldercare.model.Paciente;
import com.eldercare.eldercare.model.Paciente1;
import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdicionarContactosActivity extends AppCompatActivity {

    private EditText editNome, editNumero, editCategoria, editPaciente, editId;
    private FloatingActionButton fab;
    private Contacto contacto;
    private Contacto contactoAtual;

    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseRef();
    private DatabaseReference utilizadorRef;

    private Paciente1 paciente;
    List<String> pacientesNome = new ArrayList<String>();
    List<String> pacientesId = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_contactos);

        fab = findViewById(R.id.fabAddContacto);

        editNome = findViewById(R.id.editPassword);
        editNumero = findViewById(R.id.editNumero);
        editCategoria = findViewById(R.id.editCategoria);
        editPaciente = findViewById(R.id.editPaciente);
        editId = findViewById(R.id.editId);

        contactoAtual = (Contacto) getIntent().getSerializableExtra("contacto");

        //Caso o utilizador queira editar uma nota, o código preenche os campos automáticamente
        if (contactoAtual != null){
            editNome.setText(contactoAtual.getNome());
            editNumero.setText(contactoAtual.getNumero());
            editCategoria.setText(contactoAtual.getCategoria());
            editPaciente.setText(contactoAtual.getPaciente());
            editId.setText(contactoAtual.getIdPaciente());
        }

        //Clicar para escolher o paciente
        editPaciente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Verificar as permições da conta
                String emailUtilizador = autenticacao.getCurrentUser().getEmail();
                String idUtilizador = Base64Custom.codificarBase64(emailUtilizador);

                utilizadorRef = firebaseRef.child("utilizadores")
                        .child(idUtilizador)
                        .child("paciente");

                utilizadorRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        pacientesNome.clear();
                        pacientesId.clear();

                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(AdicionarContactosActivity.this);
                        alertDialog.setTitle("Escolha o paciente.");

                        for (DataSnapshot dados: snapshot.getChildren()) {
                            paciente = snapshot.getValue(Paciente1.class);

                            pacientesNome.add(dados.child("nome").getValue().toString());
                            pacientesId.add(dados.child("idPaciente").getValue().toString());
                        }

                        //define as opções de todos os nomes no alert dialog
                        String[] nomes = pacientesNome.toArray(new String[pacientesNome.size()]);
                        String[] ids = pacientesId.toArray(new String[pacientesId.size()]);

                        alertDialog.setItems(nomes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                for(int i = 0; i< nomes.length; i++) {

                                    if (nomes[i].equals(nomes[which])) {

                                        editPaciente.setText(nomes[i]);
                                        //para facilitar o guardar id
                                        editId.setText(ids[i]);
                                    }

                                }
                            }
                        });

                        alertDialog.show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

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
        String textoPaciente = editPaciente.getText().toString();
        String textoId = editId.getText().toString();

        //Verifica se os campos estão todos preenchidos
        //Exceto a categoria(não é obrigatória)
        if (!textoNome.isEmpty()){
            if (!textoPaciente.isEmpty()) {
                if (!textoNumero.isEmpty()) {

                    //Caso o utilizador queira editar uma nota, em vez de criar uma nota com id novo, ela reutiliza o id
                    if (contactoAtual != null) {

                        contacto.setNome(textoNome);
                        contacto.setNumero(textoNumero);
                        contacto.setCategoria(textoCategoria);
                        contacto.setPaciente(textoPaciente);
                        contacto.setIdPaciente(textoId);
                        //preciso da key para saber onde atualizar
                        contacto.setKey(contactoAtual.getKey());

                        contacto.editar();
                        finish();

                    } else {

                        contacto.setNome(textoNome);
                        contacto.setNumero(textoNumero);
                        contacto.setCategoria(textoCategoria);
                        contacto.setPaciente(textoPaciente);
                        contacto.setIdPaciente(textoId);

                        contacto.guardar();
                        finish();

                    }

                } else {
                    Toast.makeText(this, "Preencha o número primeiro.", Toast.LENGTH_LONG).show();
                }
            }else {
                Toast.makeText(this, "Introduza um paciente.", Toast.LENGTH_LONG).show();
            }
        }else {
            Toast.makeText(this, "Preencha o nome primeiro.", Toast.LENGTH_LONG).show();
        }


    }
}