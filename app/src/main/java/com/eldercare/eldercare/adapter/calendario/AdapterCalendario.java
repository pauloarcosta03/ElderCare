package com.eldercare.eldercare.adapter.calendario;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eldercare.eldercare.R;
import com.eldercare.eldercare.model.Evento;

import java.util.List;

public class AdapterCalendario extends RecyclerView.Adapter<AdapterCalendario.MyViewHolder> {

    List<Evento> eventos;
    Context context;

    public AdapterCalendario(List<Evento> eventos, Context context) {

        this.eventos = eventos;
        this.context = context;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemCalendario = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_calendario, parent, false);

        return new AdapterCalendario.MyViewHolder(itemCalendario);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Evento evento = eventos.get(position);

        holder.textTitulo.setText(evento.getTitulo());
        holder.textDescricao.setText(evento.getDescricao());
        holder.textHoras.setText(evento.getHoras() + ":" + evento.getMinutos());
        holder.textPaciente.setText(evento.getPaciente());

    }

    @Override
    public int getItemCount() {
        return eventos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView textTitulo;
        TextView textDescricao;
        TextView textHoras;
        TextView textPaciente;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitulo = itemView.findViewById(R.id.textTituloCal);
            textDescricao = itemView.findViewById(R.id.textDescCal);
            textHoras = itemView.findViewById(R.id.textHorasCal);
            textPaciente = itemView.findViewById(R.id.textPaciente);
        }
    }

}
