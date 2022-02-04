package com.eldercare.eldercare.activity.ui.lembretes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.eldercare.eldercare.R;
import com.eldercare.eldercare.config.ConfiguracaoFirebase;
import com.eldercare.eldercare.helper.Base64Custom;
import com.eldercare.eldercare.model.Lembrete;
import com.eldercare.eldercare.model.Paciente;
import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AdicionarLembretesActivity extends AppCompatActivity {

    private TextView editTitulo, editDescricao, editHoras, editPaciente, editId;
    private FloatingActionButton fab;

    private Lembrete lembrete;
    private Lembrete lembreteAtual;

    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseRef();
    private DatabaseReference utilizadorRef;

    private Paciente paciente;
    List<String> pacientesNome = new ArrayList<String>();
    List<String> pacientesId = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_lembretes);

        editTitulo = findViewById(R.id.editTitulo);
        editDescricao = findViewById(R.id.editDescricao);
        editHoras = findViewById(R.id.editHoras);
        fab = findViewById(R.id.fabAddLembrete);

        //editText de pacientes
        editPaciente = findViewById(R.id.editPaciente);
        editId = findViewById(R.id.editId);

        //Caso o utilizador queira editar um lembrete, o código preenche os campos automáticamente
        lembreteAtual = (Lembrete) getIntent().getSerializableExtra("lembrete");

        if (lembreteAtual != null){

            editTitulo.setText(lembreteAtual.getTitulo());
            editDescricao.setText(lembreteAtual.getDescricao());
            editHoras.setText(lembreteAtual.getTempo());
            editPaciente.setText(lembreteAtual.getPaciente());
            editId.setText(lembreteAtual.getIdPaciente());

        }

        //escolha de paciente
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

                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(AdicionarLembretesActivity.this);
                        alertDialog.setTitle("Escolha o paciente.");

                        for (DataSnapshot dados: snapshot.getChildren()) {
                            paciente = snapshot.getValue(Paciente.class);

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

        //escolha de horas
        editHoras.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);

                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(AdicionarLembretesActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        editHoras.setText( selectedHour + ":" + String.format("%02d", selectedMinute));
                    }
                }, hour, minute, true);
                mTimePicker.setTitle("Escolha as horas");
                mTimePicker.show();

            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardar();
            }
        });
    }

    public void guardar(){

        String textoTitulo = editTitulo.getText().toString();
        String textoDescricao = editDescricao.getText().toString();
        String textoHoras = editHoras.getText().toString();
        String textoPaciente = editPaciente.getText().toString();
        String textoId = editId.getText().toString();

        //Verifica se os campos estão todos preenchidos
        if(!textoTitulo.isEmpty()){
            if(!textoHoras.isEmpty()){
                if(!textoHoras.isEmpty()) {

                    //Dividir as horas em horas e minutos
                    String[] horasdiv = textoHoras.split(":");
                    String horas = horasdiv[0];
                    String minutos = horasdiv[1];

                    lembrete = new Lembrete();

                    lembrete.setTitulo(textoTitulo);
                    lembrete.setDescricao(textoDescricao);
                    lembrete.setHoras(horas);
                    lembrete.setMinutos(minutos);
                    lembrete.setTempo(textoHoras);
                    lembrete.setPaciente(textoPaciente);
                    lembrete.setIdPaciente(textoId);

                    if (lembreteAtual != null) {

                        lembrete.setKey(lembreteAtual.getKey());
                        lembrete.setIdPacienteAnterior(lembreteAtual.getIdPaciente());
                        lembrete.editar();

                    } else {

                        lembrete.guardar();

                    }

                    finish();
                }else {
                    Toast.makeText(getApplicationContext(), "Introduza um paciente por favor.", Toast.LENGTH_LONG)
                            .show();
                }
            }else{
                Toast.makeText(getApplicationContext(), "Preencha as horas por favor.", Toast.LENGTH_LONG)
                        .show();
            }
        }else{
            Toast.makeText(getApplicationContext(), "Preencha o título por favor.", Toast.LENGTH_LONG)
                    .show();
        }

    }

}