package com.eldercare.eldercare.activity.ui.calendario;

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
import com.eldercare.eldercare.config.ConfiguracaoFirebase;
import com.eldercare.eldercare.helper.Base64Custom;
import com.eldercare.eldercare.model.Evento;
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

public class AdicionarEventosActivity extends AppCompatActivity {

    private Calendar calendario = Calendar.getInstance();
    private EditText editData;
    private EditText editHoras;
    private EditText editTitulo;
    private EditText editDescricao;
    private EditText editPaciente;
    private EditText editId;
    private FloatingActionButton fab;

    private Evento evento;
    private Evento eventoAtual;

    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseRef();
    private DatabaseReference utilizadorRef;

    private Paciente paciente;
    List<String> pacientesNome = new ArrayList<String>();
    List<String> pacientesId = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_eventos);

        //permite fazer alterações à toolbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Adicionar Evento");

        editData = findViewById(R.id.editData);
        editHoras = findViewById(R.id.editHoras);
        editTitulo = findViewById(R.id.editTitulo);
        editDescricao = findViewById(R.id.editDescricao);
        fab = findViewById(R.id.fabAddEvento);

        editPaciente = findViewById(R.id.editPaciente);
        editId = findViewById(R.id.editId);

        //Caso o utilizador queira editar uma nota, o código preenche os campos automáticamente
        eventoAtual = (Evento) getIntent().getSerializableExtra("evento");

        if(eventoAtual != null){

            getSupportActionBar().setTitle("Editar evento");

            editTitulo.setText(eventoAtual.getTitulo());
            editDescricao.setText(eventoAtual.getDescricao());
            editData.setText(eventoAtual.getData());
            editHoras.setText(eventoAtual.getHoras() + ":" + eventoAtual.getMinutos());
            editPaciente.setText(eventoAtual.getPaciente());
            editId.setText(eventoAtual.getIdPaciente());
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

                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(AdicionarEventosActivity.this);
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
                        editHoras.setText( selectedHour + ":" + String.format("%02d", selectedMinute));
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

        evento = new Evento();

        String textoTitulo = editTitulo.getText().toString();
        String textoDescricao = editDescricao.getText().toString();
        String textoData = editData.getText().toString();
        String textoHoras = editHoras.getText().toString();
        String textoPaciente = editPaciente.getText().toString();
        String textoIdPaciente = editId.getText().toString();

        //verificação dos campos obrigatórios
        if(!textoTitulo.isEmpty()){
            if(!textoData.isEmpty()){
                if(!textoHoras.isEmpty()){
                    if(!textoPaciente.isEmpty()) {

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
                        evento.setPaciente(textoPaciente);
                        evento.setIdPaciente(textoIdPaciente);

                        if (eventoAtual != null) {

                            evento.setDataAnterior(eventoAtual.getData());
                            evento.setKey(eventoAtual.getKey());
                            evento.setIdPacienteAnterior(eventoAtual.getIdPaciente());
                            evento.editarEvento();
                            finish();

                        } else {

                            evento.guardarEvento();
                            finish();

                        }
                    }else{
                        Toast.makeText(getApplicationContext(), "Introduza um paciente por favor.", Toast.LENGTH_LONG)
                                .show();
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