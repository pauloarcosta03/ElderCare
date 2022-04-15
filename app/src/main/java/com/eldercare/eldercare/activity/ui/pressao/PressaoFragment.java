package com.eldercare.eldercare.activity.ui.pressao;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.eldercare.eldercare.R;
import com.eldercare.eldercare.adapter.pressao.AdapterPressao;
import com.eldercare.eldercare.config.ConfiguracaoFirebase;
import com.eldercare.eldercare.helper.Base64Custom;
import com.eldercare.eldercare.helper.RecyclerItemClickListener;
import com.eldercare.eldercare.model.Pressao;
import com.eldercare.eldercare.model.Utilizador;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.util.ArrayList;
import java.util.List;

public class PressaoFragment extends Fragment {

    private TextView textVazio;
    private FloatingActionButton fab;
    private MaterialCalendarView calendario;
    private RecyclerView recyclerView;

    private AdapterPressao adapterPressao;
    private List<Pressao> pressoes = new ArrayList<>();

    private String dataSelecionada;
    private CalendarDay dataAtual;

    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseRef();
    private DatabaseReference pressoesRef;
    private DatabaseReference utilizadorRef;
    private Utilizador utilizador;

    private ValueEventListener valueEventListenerPressoes;

    public PressaoFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pressao, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textVazio = view.findViewById(R.id.textVazio);
        calendario = view.findViewById(R.id.calendarView);
        fab = view.findViewById(R.id.fabPressao);

        recyclerView = view.findViewById(R.id.recyclerPressao);

        //Verificar as permições da conta
        String emailUtilizador = autenticacao.getCurrentUser().getEmail();
        String idUtilizador = Base64Custom.codificarBase64(emailUtilizador);

        utilizadorRef = firebaseRef.child("utilizadores")
                .child(idUtilizador);

        utilizadorRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                utilizador = snapshot.getValue(Utilizador.class);

                if(utilizador.getTipo().equals("p")){
                    fab.setVisibility(View.INVISIBLE);
                }else{
                    touchListener();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Configuração adapter
        adapterPressao = new AdapterPressao(pressoes, getContext());

        //Configuração do layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapterPressao);

        configuracaoCalendario();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AdicionarPressaoActivity.class));
            }
        });

    }

    public void touchListener(){

        //item click do recyclerView
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(),
                        recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        Pressao pressao = pressoes.get(position);

                        Intent intent = new Intent(getContext(), AdicionarPressaoActivity.class);
                        intent.putExtra("pressao", pressao);
                        startActivity(intent);

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                        Pressao pressao = pressoes.get(position);

                        final String[] opcoes = {"Editar Pressão", "Duplicar Pressão", "Eliminar Pressão"};

                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                        alertDialog.setTitle("Opções de Pressão");

                        alertDialog.setItems(opcoes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if("Editar Pressão".equals(opcoes[which])){

                                    Intent intent = new Intent(getContext(), AdicionarPressaoActivity.class);
                                    intent.putExtra("pressao", pressao);
                                    startActivity(intent);

                                }else if("Duplicar Pressão".equals(opcoes[which])){

                                    pressao.guardar();
                                    Toast.makeText(getContext(),
                                            "Pressão duplicada com sucesso!",
                                            Toast.LENGTH_SHORT).show();

                                }else if("Eliminar Pressão".equals(opcoes[which])){

                                    AlertDialog.Builder eliminarDialog = new AlertDialog.Builder(getContext());
                                    eliminarDialog.setTitle("Eliminar Pressão");
                                    eliminarDialog.setMessage("Deseja mesmo eliminar este registo?");

                                    eliminarDialog.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            String emailUtilizador = autenticacao.getCurrentUser().getEmail();
                                            String idUtilizador = Base64Custom.codificarBase64(emailUtilizador);

                                            pressoesRef = firebaseRef
                                                    .child("pressao")
                                                    .child(idUtilizador)
                                                    .child(dataSelecionada);

                                            pressoesRef.child(pressao.getKey()).removeValue()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {

                                                            if(task.isSuccessful()){
                                                                Toast.makeText(getContext(),
                                                                        "Registo de pressão arterial eliminado.",
                                                                        Toast.LENGTH_LONG).show();
                                                            }else{
                                                                Toast.makeText(getContext(),
                                                                        "Erro ao eliminar registo de pressão arterial",
                                                                        Toast.LENGTH_LONG).show();
                                                            }

                                                        }
                                                    });

                                        }
                                    });

                                    eliminarDialog.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    });

                                    eliminarDialog.show();

                                }
                            }
                        });

                        alertDialog.show();

                    }

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                }));

    }

    public void recuperarPressoes(){
        String emailUtilizador = autenticacao.getCurrentUser().getEmail();
        String idUtilizador = Base64Custom.codificarBase64(emailUtilizador);

        pressoesRef = firebaseRef.child("pressao")
                .child(idUtilizador)
                .child(dataSelecionada);

        valueEventListenerPressoes = pressoesRef.orderByChild("tempo").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                pressoes.clear();

                for (DataSnapshot dados: snapshot.getChildren()){
                    Pressao pressao = dados.getValue(Pressao.class);
                    pressao.setKey(dados.getKey());

                    pressoes.add(pressao);
                }

                //verificar se existem pressoes e mostra uma mensagem
                if(pressoes.isEmpty()){
                    textVazio.setText("Ainda não tem nenhum registo adicionada.");
                }else{
                    textVazio.setText("");
                }

                //diz ao adapter que os dados foram atualizados
                adapterPressao.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void configuracaoCalendario(){

        //Definir os meses para português
        CharSequence meses[] = {"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};
        calendario.setTitleMonths(meses);

        //Definir os dias para português
        CharSequence dias[] = {"seg", "ter", "qua", "qui", "sex", "sab", "dom"};
        calendario.setWeekDayLabels(dias);

        //Vai buscar a data atual para exibir os registos de hoje
        dataAtual = CalendarDay.today();
        calendario.setDateSelected(dataAtual,true);

        //Estas 2 variaveis servem para incrementar um zero quando o dia/mês só tem um digito
        String diaSelecionado = String.format("%02d", dataAtual.getDay());
        String mesSelecionado = String.format("%02d", dataAtual.getMonth());
        dataSelecionada = String.valueOf( diaSelecionado + mesSelecionado + dataAtual.getYear());

        calendario.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {

                String diaSelecionado = String.format("%02d", date.getDay());
                String mesSelecionado = String.format("%02d", date.getMonth());
                dataSelecionada = String.valueOf(diaSelecionado + mesSelecionado + date.getYear());

                //pressoesRef.removeEventListener(valueEventListenerPressoes);
                recuperarPressoes();
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarPressoes();
    }
}