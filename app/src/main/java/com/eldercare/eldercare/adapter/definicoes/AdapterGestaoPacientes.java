package com.eldercare.eldercare.adapter.definicoes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eldercare.eldercare.R;
import com.eldercare.eldercare.model.Paciente;

import java.util.List;

public class AdapterGestaoPacientes extends RecyclerView.Adapter<AdapterGestaoPacientes.MyViewHolder>{

    List<String> nomePacientes;
    Context context;

    public AdapterGestaoPacientes(List<String> nomePacientes, Context context) {

        this.nomePacientes = nomePacientes;
        this.context = context;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_gestao_pacientes, parent, false);

        return new AdapterGestaoPacientes.MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.textPaciente.setText(nomePacientes.get(position));

    }

    @Override
    public int getItemCount() {
        return nomePacientes.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView textPaciente;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        textPaciente = itemView.findViewById(R.id.textPaciente);
    }
}

}
