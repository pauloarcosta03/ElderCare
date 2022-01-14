package com.eldercare.eldercare.activity.ui.calendario;

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
import com.eldercare.eldercare.model.Evento;
import com.eldercare.eldercare.model.Nota;
import com.github.clans.fab.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AdicionarEventosActivity extends AppCompatActivity {

    private Calendar calendario = Calendar.getInstance();
    private EditText editData;
    private EditText editHoras;
    private EditText editTitulo;
    private EditText editDescricao;
    private FloatingActionButton fab;

    private Evento evento;
    private Evento eventoAtual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_eventos);

        editData = findViewById(R.id.editData);
        editHoras = findViewById(R.id.editHoras);
        editTitulo = findViewById(R.id.editTitulo);
        editDescricao = findViewById(R.id.editDescricao);
        fab = findViewById(R.id.fabAddEvento);

        //Caso o utilizador queira editar uma nota, o código preenche os campos automáticamente
        eventoAtual = (Evento) getIntent().getSerializableExtra("evento");

        if(eventoAtual != null){

            editTitulo.setText(eventoAtual.getTitulo());
            editDescricao.setText(eventoAtual.getDescricao());
            editData.setText(eventoAtual.getData());
            editHoras.setText(eventoAtual.getHoras() + ":" + eventoAtual.getMinutos());

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
                new DatePickerDialog(AdicionarEventosActivity.this,
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
                mTimePicker = new TimePickerDialog(AdicionarEventosActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        editHoras.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);
                mTimePicker.setTitle("Escolha as horas");
                mTimePicker.show();

            }
        });

        //Evento de clique do fab
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardar();
            }
        });

    }

    public void guardar(){

        evento = new Evento();

        String textoTitulo = editTitulo.getText().toString();
        String textoDescricao = editDescricao.getText().toString();
        String textoData = editData.getText().toString();
        String textoHoras = editHoras.getText().toString();

        //verificação dos campos obrigatórios
        if(!textoTitulo.isEmpty()){
            if(!textoData.isEmpty()){
                if(!textoHoras.isEmpty()){

                    //Dividir as horas em horas e minutos
                    String[] horasdiv = textoHoras.split(":");
                    String horas = horasdiv[0];
                    String minutos = horasdiv[1];

                    evento.setTitulo(textoTitulo);
                    evento.setDescricao(textoDescricao);
                    evento.setData(textoData);
                    evento.setHoras(horas);
                    evento.setMinutos(minutos);
                    //O Tempo serve para ordenar os eventos por ordem do tempo especificado
                    evento.setTempo(horas + minutos);

                    if(eventoAtual != null){

                        evento.setKey(eventoAtual.getKey());
                        evento.editarEvento();
                        finish();

                    }else{

                        evento.guardarEvento();
                        finish();

                    }
                }else{
                    Toast.makeText(getApplicationContext(), "Introduza uma hora por favor.", Toast.LENGTH_LONG)
                            .show();
                }
            }else{
                Toast.makeText(getApplicationContext(), "Introduza uma data por favor.", Toast.LENGTH_LONG)
                        .show();
            }
        }else{
            Toast.makeText(getApplicationContext(), "Preencha o título por favor.", Toast.LENGTH_LONG)
                    .show();
        }

    }

    public void atualizarData(){
        String myFormat="dd/MM/yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.getDefault());
        editData.setText(dateFormat.format(calendario.getTime()));
    }
}