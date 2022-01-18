package com.eldercare.eldercare.adapter.glicemia;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eldercare.eldercare.R;
import com.eldercare.eldercare.model.Glicemia;

import java.util.List;

public class AdapterGlicemia extends RecyclerView.Adapter<AdapterGlicemia.MyViewHolder> {

    List<Glicemia> glicemias;
    Context context;

    public AdapterGlicemia(List<Glicemia> glicemias, Context context) {
        this.glicemias = glicemias;
        this.context = context;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemGlicemia = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_glicemia, parent, false);

        return new AdapterGlicemia.MyViewHolder(itemGlicemia);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Glicemia glicemia = glicemias.get(position);

        Integer glicose = Integer.valueOf(glicemia.getGlicose());

        holder.textGlicose.setText(glicemia.getGlicose() + " mg/dl");
        holder.textHoras.setText(glicemia.getHoras() + ":" + glicemia.getMinutos());
        holder.textPaciente.setText(glicemia.getPaciente());

        if( glicose <= 70){
            holder.textGlicose.setTextColor(context.getResources().getColor(R.color.colorGlicoseHipoglicemia));
        }else if( glicose > 70 && glicose <= 100 ){
            holder.textGlicose.setTextColor(context.getResources().getColor(R.color.colorGlicoseNormal));
        }else if( glicose > 100 && glicose <= 126 ){
            holder.textGlicose.setTextColor(context.getResources().getColor(R.color.colorGlicosePreDiabetes));
        }else if( glicose > 126){
            holder.textGlicose.setTextColor(context.getResources().getColor(R.color.colorGlicoseDiabetes));
        }

    }

    @Override
    public int getItemCount() {
        return glicemias.size() ;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView textGlicose;
        TextView textHoras;
        TextView textPaciente;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            textGlicose = itemView.findViewById(R.id.textGlicose);
            textHoras = itemView.findViewById(R.id.textHoras);
            textPaciente = itemView.findViewById(R.id.textPaciente);

        }
    }

}
