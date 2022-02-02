package com.eldercare.eldercare.adapter.lembretes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eldercare.eldercare.R;
import com.eldercare.eldercare.adapter.contactos.AdapterContactos;
import com.eldercare.eldercare.model.Lembrete;

import java.util.List;

public class AdapterLembretes extends RecyclerView.Adapter<AdapterLembretes.MyViewHolder> {

    List<Lembrete> lembretes;
    Context context;

    public AdapterLembretes(List<Lembrete> lembretes, Context context) {

        this.lembretes = lembretes;
        this.context = context;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_lembretes, parent, false);

        return new AdapterLembretes.MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Lembrete lembrete = lembretes.get(position);

        holder.textTitulo.setText(lembrete.getTitulo());
        holder.textDescricao.setText(lembrete.getDescricao());
        holder.textHoras.setText(lembrete.getHoras() + ":" + lembrete.getMinutos());
        holder.textPaciente.setText(lembrete.getPaciente());

    }

    @Override
    public int getItemCount() {
        return lembretes.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView textTitulo;
        TextView textDescricao;
        TextView textHoras;
        TextView textPaciente;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            textTitulo = itemView.findViewById(R.id.textTituloLembretes);
            textDescricao = itemView.findViewById(R.id.textDescLembretes);
            textHoras = itemView.findViewById(R.id.textHorasLembretes);
            textPaciente = itemView.findViewById(R.id.textPaciente);

        }
    }

}
