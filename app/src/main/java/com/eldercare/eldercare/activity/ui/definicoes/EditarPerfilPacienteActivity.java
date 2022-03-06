package com.eldercare.eldercare.activity.ui.definicoes;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.eldercare.eldercare.R;
import com.eldercare.eldercare.activity.ui.calendario.AdicionarEventosActivity;
import com.eldercare.eldercare.config.ConfiguracaoFirebase;
import com.eldercare.eldercare.model.Paciente;
import com.eldercare.eldercare.model.Perfil;
import com.eldercare.eldercare.model.Utilizador;
import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Locale;

public class EditarPerfilPacienteActivity extends AppCompatActivity {

    private TextView textNome;
    private TextView textEmail;

    private LinearLayout layoutPeso;
    private TextView textPeso;

    private LinearLayout layoutAltura;
    private TextView textAltura;

    private LinearLayout layoutSexo;
    private TextView textSexo;

    private LinearLayout layoutIdade;
    private TextView textIdade;
    private TextView textData;
    private Calendar calendario = Calendar.getInstance();

    private FloatingActionButton fab;

    private String perfilAtual;
    private DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseRef();
    private DatabaseReference perfilRef;
    private DatabaseReference utilizadorRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil_paciente);

        //permite fazer alterações à toolbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Editar Perfil");

        textNome = findViewById(R.id.textNome);
        textEmail = findViewById(R.id.textEmail);

        perfilAtual = (String) getIntent().getSerializableExtra("id");

        utilizadorRef = firebaseRef.child("utilizadores").child(perfilAtual);

        utilizadorRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Utilizador utilizador = snapshot.getValue(Utilizador.class);

                textNome.setText(utilizador.getNome());
                textEmail.setText(utilizador.getEmail());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        perfilRef = firebaseRef.child("perfis").child(perfilAtual);

        perfilRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    Perfil perfil = snapshot.getValue(Perfil.class);

                    textSexo.setText(perfil.getSexo());
                    textAltura.setText(perfil.getAltura());
                    textPeso.setText(perfil.getPeso());
                    textData.setText(perfil.getDataNasc());
                    calculoAnos(perfil.getDataNasc());
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //escolher data de nascimento/idade
        layoutIdade = findViewById(R.id.layoutIdade);
        textIdade = findViewById(R.id.textIdade);
        textData = findViewById(R.id.textData);

        DatePickerDialog.OnDateSetListener data =new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                calendario.set(Calendar.YEAR, year);
                calendario.set(Calendar.MONTH,month);
                calendario.set(Calendar.DAY_OF_MONTH,day);
                atualizarData();
            }
        };
        layoutIdade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(EditarPerfilPacienteActivity.this,
                        data,
                        calendario.get(Calendar.YEAR),
                        calendario.get(Calendar.MONTH),
                        calendario.get(Calendar.DAY_OF_MONTH))
                        .show();
            }
        });

        //escolher o peso
        layoutPeso = findViewById(R.id.layoutPeso);
        textPeso = findViewById(R.id.textPeso);

        layoutPeso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final NumberPicker numberPicker = new NumberPicker(getApplicationContext());
                numberPicker.setMaxValue(300);
                numberPicker.setMinValue(0);
                numberPicker.setValue(60);

                AlertDialog.Builder escolhaPeso = new AlertDialog.Builder(EditarPerfilPacienteActivity.this);
                escolhaPeso.setTitle("Escolha o peso em kg:");
                escolhaPeso.setView(numberPicker);

                escolhaPeso.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        textPeso.setText(String.valueOf(numberPicker.getValue()) + " kg");
                    }
                });

                escolhaPeso.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                escolhaPeso.create();
                escolhaPeso.show();

            }
        });

        //escolher a altura
        layoutAltura = findViewById(R.id.layoutAltura);
        textAltura = findViewById(R.id.textAltura);

        layoutAltura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final NumberPicker numberPicker = new NumberPicker(getApplicationContext());
                numberPicker.setMaxValue(250);
                numberPicker.setMinValue(0);
                numberPicker.setValue(160);

                AlertDialog.Builder escolhaAltura = new AlertDialog.Builder(EditarPerfilPacienteActivity.this);
                escolhaAltura.setTitle("Escolha a altura em cm:");
                escolhaAltura.setView(numberPicker);

                escolhaAltura.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        textAltura.setText(String.valueOf(numberPicker.getValue()) + " cm");
                    }
                });

                escolhaAltura.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                escolhaAltura.create();
                escolhaAltura.show();

            }
        });

        //escolher o sexo
        layoutSexo = findViewById(R.id.layoutSexo);
        textSexo = findViewById(R.id.textSexo);

        layoutSexo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder escolhaSexo = new AlertDialog.Builder(EditarPerfilPacienteActivity.this);
                escolhaSexo.setTitle("Escolha o género:");

                String[] generos = {"Masculino", "Feminino"};
                escolhaSexo.setItems(generos, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for(int i = 0; i< generos.length; i++) {

                            if (generos[i].equals(generos[which])) {

                                textSexo.setText(generos[i]);
                            }
                        }
                    }
                });

                escolhaSexo.show();

            }
        });

        fab = findViewById(R.id.fabEditPerfil);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarPerfil();
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void atualizarData(){
        String myFormat="dd/MM/yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.getDefault());
        textData.setText(dateFormat.format(calendario.getTime()));

        String myFormat2="d/M/y";
        SimpleDateFormat dateFormat2 = new SimpleDateFormat(myFormat2, Locale.getDefault());

        String data = dateFormat2.format(calendario.getTime());

        String[] dataSplit = data.split("/");

        LocalDate start = LocalDate.of(Integer.parseInt(dataSplit[2]), Integer.parseInt(dataSplit[1]), Integer.parseInt(dataSplit[0]));
        LocalDate end = LocalDate.now(); // use for age-calculation: LocalDate.now()
        long years = ChronoUnit.YEARS.between(start, end);
        textIdade.setText(String.valueOf(years) + " anos");
    }

    public void calculoAnos(String data){
        String[] dataSplit = data.split("/");
        LocalDate start = LocalDate.of(Integer.parseInt(dataSplit[2]), Integer.parseInt(dataSplit[1]), Integer.parseInt(dataSplit[0]));
        LocalDate end = LocalDate.now(); // use for age-calculation: LocalDate.now()
        long years = ChronoUnit.YEARS.between(start, end);
        textIdade.setText(String.valueOf(years) + " anos");
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

    public void guardarPerfil(){
        String sexo = textSexo.getText().toString();
        String dataNasc = textData.getText().toString();
        String altura = textAltura.getText().toString();
        String peso = textPeso.getText().toString();

        if(!sexo.isEmpty()){
            if(!dataNasc.isEmpty()){
                if(!altura.isEmpty()){
                    if(!peso.isEmpty()){

                        Perfil perfil = new Perfil();

                        perfil.setId(perfilAtual);
                        perfil.setSexo(sexo);
                        perfil.setDataNasc(dataNasc);
                        perfil.setAltura(altura);
                        perfil.setPeso(peso);

                        perfil.guardarPerfil();

                        Toast.makeText(getApplicationContext(), "Perfil Atualizado", Toast.LENGTH_SHORT).show();

                    }else{
                        Toast.makeText(getApplicationContext(), "Por favor introduza o peso.", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "Por favor introduza a altura.", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(getApplicationContext(), "Por favor introduza a idade.", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(getApplicationContext(), "Por favor introduza o género.", Toast.LENGTH_SHORT).show();
        }
    }
}