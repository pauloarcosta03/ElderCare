package com.eldercare.eldercare.activity.ui.pressao;

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
import com.eldercare.eldercare.activity.ui.calendario.AdicionarEventosActivity;
import com.eldercare.eldercare.model.Pressao;
import com.github.clans.fab.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AdicionarPressaoActivity extends AppCompatActivity {

    private Calendar calendario = Calendar.getInstance();
    private EditText editData;
    private EditText editHoras;
    private EditText editPressao;
    private EditText editPaciente;
    private FloatingActionButton fab;

    private Pressao pressao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_pressao);

        editData = findViewById(R.id.editData);
        editHoras = findViewById(R.id.editHoras);
        editPressao = findViewById(R.id.editPressao);
        editPaciente = findViewById(R.id.editPaciente);
        fab = findViewById(R.id.fabAddPressao);

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

    public void guardar(){

        String textoPressao = editPressao.getText().toString();
        String textoPaciente = editPaciente.getText().toString();
        String textoHoras = editHoras.getText().toString();
        String textoData = editData.getText().toString();

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

                                pressao.guardar();

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