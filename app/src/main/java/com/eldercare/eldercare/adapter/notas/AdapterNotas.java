package com.eldercare.eldercare.adapter.notas;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.eldercare.eldercare.R;
import com.eldercare.eldercare.config.ConfiguracaoFirebase;
import com.eldercare.eldercare.helper.Base64Custom;
import com.eldercare.eldercare.model.Nota;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class AdapterNotas extends RecyclerView.Adapter<AdapterNotas.MyViewHolder> {

    List<Nota> notas;
    Context context;

    private DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseRef();
    private DatabaseReference notasRef;
    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutorizacao();
    private Nota nota;

//save the context recievied via constructor in a local variable

    public AdapterNotas(List<Nota> notas, Context context) {

        this.notas = notas;
        this.context = context;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemNota = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_notas, parent, false);

        return new MyViewHolder(itemNota);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        int posicao = position;
        Nota nota = notas.get(posicao);

        holder.titulo.setText(nota.getTitulo());
        holder.descricao.setText(nota.getDescricao());

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eliminarNota(posicao, nota.getTitulo());
            }
        });

    }

    @Override
    public int getItemCount() {
        return notas.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView titulo;
        TextView descricao;
        Button deleteButton;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            titulo = itemView.findViewById(R.id.textTitulo);
            descricao = itemView.findViewById(R.id.textDescricao);
            deleteButton = itemView.findViewById(R.id.deleteButton);

        }
    }

    public void eliminarNota(int position, String tituloNota){

        nota = notas.get(position);
        String emailUtilizador = autenticacao.getCurrentUser().getEmail();
        String idUtilizador = Base64Custom.codificarBase64(emailUtilizador);

        notasRef = firebaseRef.child("notas")
                .child(idUtilizador);

        notasRef.child(nota.getKey()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(context.getApplicationContext(),
                            "Nota: " + tituloNota + " eliminada com sucesso",
                            Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(context.getApplicationContext(),
                            "Erro ao eliminar " + tituloNota,
                            Toast.LENGTH_LONG).show();
                }
            }
        });


        /*AlertDialog.Builder alertDialog = new AlertDialog.Builder(context.getApplicationContext());

        alertDialog.setTitle("Deseja eliminar nota?");
        alertDialog.setMessage("Tem mesmo certeza que quer eliminar esta nota?\n"
                + tituloNota);

        alertDialog.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(context.getApplicationContext(),
                        "Eliminado",
                        Toast.LENGTH_LONG).show();
            }
        });

        alertDialog.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(context.getApplicationContext(),
                        "Cancelado",
                        Toast.LENGTH_LONG).show();
            }
        });

        AlertDialog alert = alertDialog.create();
        alert.show();*/

    }

}
