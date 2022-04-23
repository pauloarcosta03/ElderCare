package com.eldercare.eldercare.activity.ui.pressao;

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
import com.eldercare.eldercare.config.ConfiguracaoFirebase;
import com.eldercare.eldercare.helper.Base64Custom;
import com.eldercare.eldercare.model.Paciente;
import com.eldercare.eldercare.model.Pressao;
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

public class AdicionarPressaoActivity extends AppCompatActivity {

    private Calendar calendario = Calendar.getInstance();
    private EditText editData;
    private EditText editHoras;
    private EditText editPressao;
    private FloatingActionButton fab;
    private EditText editPaciente;
    private EditText editId;

    private Pressao pressao;
    private Pressao pressaoAtual;

    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseRef();
    private DatabaseReference utilizadorRef;

    private Paciente paciente;
    List<String> pacientesNome = new ArrayList<String>();
    List<String> pacientesId = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_pressao);

        //permite fazer alterações à toolbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Adicionar Pressão Arterial");

        editData = findViewById(R.id.editData);
        editHoras = findViewById(R.id.editHoras);
        editPressao = findViewById(R.id.editPressao);
        fab = findViewById(R.id.fabAddPressao);

        pressaoAtual = (Pressao) getIntent().getSerializableExtra("pressao");

        editPaciente = findViewById(R.id.editPaciente);
        editId = findViewById(R.id.editId);

        if(pressaoAtual != null){

            actionBar.setTitle("Editar Pressão Arterial");

            editPressao.setText(pressaoAtual.getSistolica() + "/" + pressaoAtual.getDiastolica());
            editData.setText(pressaoAtual.getData());
            editHoras.setText(pressaoAtual.getHoras() + ":" + pressaoAtual.getMinutos());
            editPaciente.setText(pressaoAtual.getPaciente());
            editId.setText(pressaoAtual.getIdPaciente());

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

                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(AdicionarPressaoActivity.this);
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
                new DatePickerDialog(AdicionarPressaoActivity.this,
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
                mTimePicker = new TimePickerDialog(AdicionarPressaoActivity.this, new TimePickerDialog.OnTimeSetListener() {
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
                finish();
            }
        });

    }

    //em vez de dar reset à activity anterior, dá finish quando se clica no botão para trás
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

        String textoPressao = editPressao.getText().toString();
        String textoHoras = editHoras.getText().toString();
        String textoData = editData.getText().toString();

        String textoPaciente = editPaciente.getText().toString();
        String textoId = editId.getText().toString();

        boolean separador = textoPressao.contains("/") ;

        if(!textoPressao.isEmpty()){
            if(separador) {
                if (!textoPaciente.isEmpty()) {
                    if (!textoData.isEmpty()) {
                        if (!textoHoras.isEmpty()) {
                            pressao = new Pressao();

                            //Dividir as horas em horas e minutos
                            String[] horasdiv = textoHoras.split(":");
                            String horas = horasdiv[0];
                            String minutos = horasdiv[1];

                            //Dividir a medicao em sistolica e diastolica
                            String[] pressaodiv = textoPressao.split("/");
                            String sistolica = pressaodiv[0];
                            String diastolica = pressaodiv[1];

                            boolean sistolicaNum = temNumero(sistolica);
                            boolean diastolicaNum = temNumero(diastolica);

                            if(sistolicaNum && diastolicaNum){

                                pressao.setData(textoData);
                                pressao.setHoras(horas);
                                pressao.setMinutos(minutos);
                                pressao.setTempo(horas + minutos);
                                pressao.setPaciente(textoPaciente);
                                pressao.setSistolica(sistolica);
                                pressao.setDiastolica(diastolica);
                                pressao.setIdPaciente(textoId);

                                if(pressaoAtual != null){

                                    pressao.setDataAnterior(pressaoAtual.getData());
                                    pressao.setKey(pressaoAtual.getKey());
                                    pressao.setIdPacienteAnterior(pressaoAtual.getIdPaciente());
                                    pressao.editar();
                                    finish();

                                }else{
                                    pressao.guardar();
                                    finish();
                                }

                            }else{
                                Toast.makeText(getApplicationContext(), "Escreva uma pressão válida por favor.", Toast.LENGTH_LONG)
                                        .show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Introduza as horas por favor.", Toast.LENGTH_LONG)
                                    .show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Introduza uma data por favor.", Toast.LENGTH_LONG)
                                .show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Introduza um paciente por favor.", Toast.LENGTH_LONG)
                            .show();
                }
            }else{
                Toast.makeText(getApplicationContext(), "Escreva uma pressão válida por favor.", Toast.LENGTH_LONG)
                        .show();
            }
        }else{
            Toast.makeText(getApplicationContext(), "Escreva uma pressão por favor.", Toast.LENGTH_LONG)
                    .show();
        }

    }

    //verifica se o valor é numero
    public static boolean temNumero(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            int i = Integer.parseInt(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public void atualizarData(){
        String myFormat="dd/MM/yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.getDefault());
        editData.setText(dateFormat.format(calendario.getTime()));
    }

}