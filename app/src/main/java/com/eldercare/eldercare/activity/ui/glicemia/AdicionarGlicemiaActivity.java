package com.eldercare.eldercare.activity.ui.glicemia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.eldercare.eldercare.R;
import com.eldercare.eldercare.activity.ui.calendario.AdicionarEventosActivity;
import com.eldercare.eldercare.activity.ui.pressao.AdicionarPressaoActivity;
import com.eldercare.eldercare.config.ConfiguracaoFirebase;
import com.eldercare.eldercare.helper.Base64Custom;
import com.eldercare.eldercare.model.Glicemia;
import com.eldercare.eldercare.model.Paciente;
import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdicionarGlicemiaActivity extends AppCompatActivity {

    private Calendar calendario = Calendar.getInstance();
    private EditText editData;
    private EditText editHoras;
    private EditText editGlicemia;
    private EditText editPaciente;
    private EditText editId;
    private FloatingActionButton fab;

    private Glicemia glicemiaAtual;

    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseRef();
    private DatabaseReference utilizadorRef;

    private Paciente paciente;
    List<String> pacientesNome = new ArrayList<String>();
    List<String> pacientesId = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_glicemia);

        //permite fazer alterações à toolbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Adicionar Nivel de Glicemia");

        editData = findViewById(R.id.editData);
        editHoras = findViewById(R.id.editHoras);
        editGlicemia = findViewById(R.id.editGlicemia);
        editPaciente = findViewById(R.id.editPaciente);
        fab = findViewById(R.id.fabAddGlicemia);

        editPaciente = findViewById(R.id.editPaciente);
        editId = findViewById(R.id.editId);

        glicemiaAtual = (Glicemia) getIntent().getSerializableExtra("glicemia");

        if(glicemiaAtual!=null){

            editGlicemia.setText(glicemiaAtual.getGlicose());
            editHoras.setText(glicemiaAtual.getHoras() + ":" + glicemiaAtual.getMinutos());
            editPaciente.setText(glicemiaAtual.getPaciente());
            editId.setText(glicemiaAtual.getIdPaciente());
            editData.setText(glicemiaAtual.getData());

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

                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(AdicionarGlicemiaActivity.this);
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

        //Escolha de data
        DatePickerDialog.OnDateSetListener data =new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                calendario.set(Calendar.YEAR, year);
                calendario.set(Calendar.MONTH,month);
                calendario.set(Calendar.DAY_OF_MONTH,day);
                atualizarData();
            }
        };
        editData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(AdicionarGlicemiaActivity.this,
                        data,
                        calendario.get(Calendar.YEAR),
                        calendario.get(Calendar.MONTH),
                        calendario.get(Calendar.DAY_OF_MONTH))
                        .show();
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
                mTimePicker = new TimePickerDialog(AdicionarGlicemiaActivity.this, new TimePickerDialog.OnTimeSetListener() {
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

    public void guardar(){

        String textoGlicemia = editGlicemia.getText().toString();
        String textoData = editData.getText().toString();
        String textoHoras = editHoras.getText().toString();
        String textoPaciente = editPaciente.getText().toString();
        String textoId = editId.getText().toString();

        if(!textoGlicemia.isEmpty()){
            if(!textoPaciente.isEmpty()){
                if(!textoData.isEmpty()){
                    if(!textoHoras.isEmpty()){

                        //Dividir as horas em horas e minutos
                        String[] horasdiv = textoHoras.split(":");
                        String horas = horasdiv[0];
                        String minutos = horasdiv[1];

                        Glicemia glicemia = new Glicemia();
                        glicemia.setGlicose(textoGlicemia);
                        glicemia.setTempo(horas + minutos);
                        glicemia.setHoras(horas);
                        glicemia.setMinutos(minutos);
                        glicemia.setData(textoData);
                        glicemia.setPaciente(textoPaciente);
                        glicemia.setIdPaciente(textoId);

                        if(glicemiaAtual!=null){

                            glicemia.setKey(glicemiaAtual.getKey());
                            glicemia.setDataAnterior(glicemiaAtual.getData());
                            glicemia.setIdPacienteAnterior(glicemiaAtual.getIdPaciente());
                            glicemia.editar();
                            finish();

                        }else {
                            glicemia.guardar();
                            finish();
                        }



                    }else{
                        Toast.makeText(getApplicationContext(), "Introduza as horas primeiro.", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "Introduza a data primeiro.", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(getApplicationContext(), "Introduza o paciente primeiro.", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(getApplicationContext(), "Introduza um valor primeiro.", Toast.LENGTH_SHORT).show();
        }

    }

    public void atualizarData(){
        String myFormat="dd/MM/yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.getDefault());
        editData.setText(dateFormat.format(calendario.getTime()));
    }
}