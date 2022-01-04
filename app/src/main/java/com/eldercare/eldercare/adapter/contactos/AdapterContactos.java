package com.eldercare.eldercare.adapter.contactos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eldercare.eldercare.R;
import com.eldercare.eldercare.model.Contacto;
import com.eldercare.eldercare.model.Nota;

import java.util.List;

public class AdapterContactos extends RecyclerView.Adapter<AdapterContactos.MyViewHolder> {

    List<Contacto> contactos;
    Context context;

    public AdapterContactos(List<Contacto> contactos, Context context) {

        this.contactos = contactos;
        this.context = context;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemLista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_contactos, parent, false);

        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Contacto contacto = contactos.get(position);

        String categoria = contacto.getCategoria();

        holder.textNome.setText(contacto.getNome());
        holder.textCategoria.setText(categoria);
        holder.textNumero.setText(contacto.getNumero());

        if (contacto.getCategoria().equals("urgência")){
            holder.textNumero.setTextColor(context.getResources().getColor(R.color.colorUrgencia));
        }

    }

    @Override
    public int getItemCount() {
        return contactos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView textNome;
        TextView textNumero;
        TextView textCategoria;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textNome = itemView.findViewById(R.id.textNome);
            textNumero = itemView.findViewById(R.id.textNumero);
            textCategoria = itemView.findViewById(R.id.textCategoria);

        }
    }

}
