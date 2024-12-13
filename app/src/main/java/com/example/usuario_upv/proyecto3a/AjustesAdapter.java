package com.example.usuario_upv.proyecto3a;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AjustesAdapter extends RecyclerView.Adapter<AjustesAdapter.AjustesViewHolder> {
    private List<Ajuste> ajustesList;
    private Context context;
    private OnItemClickListener onItemClickListener;

    // Constructor modificado para aceptar la lista, contexto y listener
    public AjustesAdapter(List<Ajuste> ajustesList, Context context, OnItemClickListener onItemClickListener) {
        this.ajustesList = ajustesList;
        this.context = context;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public AjustesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Infla el layout de cada item en el RecyclerView
        View view = LayoutInflater.from(context).inflate(R.layout.ajustes_item, parent, false);
        return new AjustesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AjustesViewHolder holder, int position) {
        Ajuste ajuste = ajustesList.get(position);
        holder.texto.setText(ajuste.getTexto());
        holder.imagen.setImageResource(ajuste.getImagen());

        // Configura el listener para el clic en el item
        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(position));
    }

    @Override
    public int getItemCount() {
        return ajustesList.size();
    }

    // Interfaz para manejar el clic
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    // ViewHolder para los elementos del RecyclerView
    public static class AjustesViewHolder extends RecyclerView.ViewHolder {
        TextView texto;
        ImageView imagen;

        public AjustesViewHolder(View itemView) {
            super(itemView);
            texto = itemView.findViewById(R.id.settingText);
            imagen = itemView.findViewById(R.id.leftIcon);
        }
    }
}

