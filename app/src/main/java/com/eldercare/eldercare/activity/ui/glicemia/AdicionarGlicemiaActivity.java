package com.eldercare.eldercare.activity.ui.glicemia;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.eldercare.eldercare.R;
import com.eldercare.eldercare.activity.ui.pressao.AdicionarPressaoActivity;
import com.eldercare.eldercare.model.Glicemia;
import com.github.clans.fab.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AdicionarGlicemiaActivity extends AppCompatActivity {

    private Calendar calendario = Calendar.getInstance();
    private EditText editData;
    private EditText editHoras;
    private EditText editGlicemia;
    private EditText editPaciente;
    private FloatingActionButton fab;

    private Glicemia glicemiaAtual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_glicemia);

        editData = findViewById(R.id.editData);
        editHoras = findViewById(R.id.editHoras);
        editGlicemia = findViewById(R.id.editGlicemia);
        editPaciente = findViewById(R.id.editPaciente);
        fab = findViewById(R.id.fabAddGlicemia);

        glicemiaAtual = (Glicemia) getIntent().getSerializableExtra("glicemia");

        if(glicemiaAtual!=null){

            editGlicemia.setText(glicemiaAtual.getGlicose());
            editPaciente.setText(glicemiaAtual.getPaciente());
            editHoras.setText(glicemiaAtual.getHoras() + ":" + glicemiaAtual.getMinutos());
            editData.setText(glicemiaAtual.getData());

        }

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

    public void guardar(){

        String textoGlicemia = editGlicemia.getText().toString();
        String textoPaciente = editPaciente.getText().toString();
        String textoData = editData.getText().toString();
        String textoHoras = editHoras.getText().toString();

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
                        glicemia.setPaciente(textoPaciente);
                        glicemia.setTempo(horas + minutos);
                        glicemia.setHoras(horas);
                        glicemia.setMinutos(minutos);
                        glicemia.setData(textoData);

                        if(glicemiaAtual!=null){

                            glicemia.setKey(glicemiaAtual.getKey());
                            glicemia.setDataAnterior(glicemiaAtual.getData());
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