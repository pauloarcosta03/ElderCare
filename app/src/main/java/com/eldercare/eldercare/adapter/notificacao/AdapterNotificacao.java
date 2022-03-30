package com.eldercare.eldercare.adapter.notificacao;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eldercare.eldercare.R;
import com.eldercare.eldercare.adapter.contactos.AdapterContactos;
import com.eldercare.eldercare.model.DisplayNotificacao;
import com.eldercare.eldercare.model.NotificacaoDados;

import java.util.List;

public class AdapterNotificacao extends RecyclerView.Adapter<AdapterNotificacao.MyViewHolder> {

    List<DisplayNotificacao> notificacoes;
    Context context;

    public AdapterNotificacao(List<DisplayNotificacao> notificacoes, Context context) {
        this.notificacoes = notificacoes;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemLista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_notificacao, parent, false);

        return new AdapterNotificacao.MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        DisplayNotificacao notificacao = notificacoes.get(position);

        holder.textTitulo.setText(notificacao.getTitulo());
        holder.textDescricao.setText(notificacao.getDescricao());
        holder.textHoras.setText(notificacao.getTempo());

    }

    @Override
    public int getItemCount() {
        return notificacoes.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView textTitulo;
        TextView textDescricao;
        TextView textHoras;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitulo = itemView.findViewById(R.id.textTitulo);
            textDescricao = itemView.findViewById(R.id.textDescricao);
            textHoras = itemView.findViewById(R.id.textHoras);
        }
    }
}
