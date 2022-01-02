package com.eldercare.eldercare.adapter.notas;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eldercare.eldercare.R;
import com.eldercare.eldercare.model.Nota;

import java.util.List;

public class AdapterNotas extends RecyclerView.Adapter<AdapterNotas.MyViewHolder> {

    List<Nota> notas;
    Context context;

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
        Nota nota = notas.get(position);

        holder.titulo.setText(nota.getTitulo());
        holder.descricao.setText(nota.getDescricao());

    }

    @Override
    public int getItemCount() {
        return notas.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView titulo;
        TextView descricao;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            titulo = itemView.findViewById(R.id.textTitulo);
            descricao = itemView.findViewById(R.id.textDescricao);

        }
    }

}
