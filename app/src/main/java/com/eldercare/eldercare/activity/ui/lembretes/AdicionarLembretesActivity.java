package com.eldercare.eldercare.activity.ui.lembretes;

import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.eldercare.eldercare.R;
import com.eldercare.eldercare.activity.ui.calendario.AdicionarEventosActivity;
import com.eldercare.eldercare.model.Lembrete;
import com.github.clans.fab.FloatingActionButton;

import java.util.Calendar;

public class AdicionarLembretesActivity extends AppCompatActivity {

    private TextView editTitulo, editDescricao, editHoras;
    private FloatingActionButton fab;

    private Lembrete lembrete;
    private Lembrete lembreteAtual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_lembretes);

        editTitulo = findViewById(R.id.editTitulo);
        editDescricao = findViewById(R.id.editDescricao);
        editHoras = findViewById(R.id.editHoras);
        fab = findViewById(R.id.fabAddLembrete);

        //Caso o utilizador queira editar um lembrete, o código preenche os campos automáticamente
        lembreteAtual = (Lembrete) getIntent().getSerializableExtra("lembrete");

        if (lembreteAtual != null){

            editTitulo.setText(lembreteAtual.getTitulo());
            editDescricao.setText(lembreteAtual.getDescricao());
            editHoras.setText(lembreteAtual.getTempo());

        }

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

        //Verifica se os campos estão todos preenchidos
        if(!textoTitulo.isEmpty()){
            if(!textoHoras.isEmpty()){

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
                lembrete.setKey(lembreteAtual.getKey());

                if(lembreteAtual != null){

                    lembrete.editar();

                }else{

                    lembrete.guardar();

                }

                finish();

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