package com.eldercare.eldercare.adapter.pressao;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eldercare.eldercare.R;
import com.eldercare.eldercare.model.Pressao;

import java.util.List;

public class AdapterPressao extends RecyclerView.Adapter<AdapterPressao.MyViewHolder> {

    List<Pressao> pressoes;
    Context context;

    public AdapterPressao(List<Pressao> pressoes, Context context) {
        this.pressoes = pressoes;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemPressao = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_pressao, parent, false);

        return new AdapterPressao.MyViewHolder(itemPressao);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Pressao pressao = pressoes.get(position);

        Integer sistolica = Integer.valueOf(pressao.getSistolica());
        Integer diastolica = Integer.valueOf(pressao.getDiastolica());

        holder.textPressao.setText(pressao.getSistolica() + "/" + pressao.getDiastolica() + " mm Hg");
        holder.textHoras.setText(pressao.getHoras() + ":" + pressao.getMinutos());
        holder.textPaciente.setText(pressao.getPaciente());

        //pressao normal
        if(sistolica < 120 || diastolica < 80){

            holder.textPressao.setTextColor(context.getResources().getColor(R.color.colorPressaoNormal));

        }else if((sistolica >= 120 && sistolica < 140) || (diastolica >= 80 && diastolica < 90)){

            holder.textPressao.setTextColor(context.getResources().getColor(R.color.colorPressaoPreHipertencao));

        }else if((sistolica >= 140 && sistolica < 160) || (diastolica >= 90 && diastolica < 100)){

            holder.textPressao.setTextColor(context.getResources().getColor(R.color.colorPressaoHipertencao1));

        }else if((sistolica >= 160 && sistolica < 180) || (diastolica >= 100 && diastolica < 110)){

            holder.textPressao.setTextColor(context.getResources().getColor(R.color.colorPressaoHipertencao2));

        }else if(sistolica >= 180 || diastolica >= 110){

            holder.textPressao.setTextColor(context.getResources().getColor(R.color.colorPressaoAlta));

        }

    }

    @Override
    public int getItemCount() {
        return pressoes.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView textPressao;
        TextView textPaciente;
        TextView textHoras;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            textPressao = itemView.findViewById(R.id.textPressao);
            textPaciente = itemView.findViewById(R.id.textPaciente);
            textHoras = itemView.findViewById(R.id.textHoras);

        }
    }

}
