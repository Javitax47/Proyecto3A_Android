package com.example.usuario_upv.proyecto3a;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AlertasAdapter extends RecyclerView.Adapter<AlertasAdapter.AlertasViewHolder> {

    public interface OnAlertasListener {
        void onEliminarAlerta(int position);
    }

    private final List<Alertas> alertasList;
    private final OnAlertasListener listener;

    public AlertasAdapter(List<Alertas> alertasList, OnAlertasListener listener) {
        this.alertasList = alertasList;
        this.listener = listener;
    }

    @Override
    public AlertasViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alerta, parent, false);
        return new AlertasViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AlertasViewHolder holder, int position) {
        Alertas alerta = alertasList.get(position);
        holder.titulo.setText(alerta.getMensaje());

        holder.borraralerta.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                listener.onEliminarAlerta(adapterPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return alertasList.size();
    }

    public static class AlertasViewHolder extends RecyclerView.ViewHolder {
        public final TextView titulo;
        public final ImageButton borraralerta;

        public AlertasViewHolder(View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.textoAlerta);
            borraralerta = itemView.findViewById(R.id.borraralerta);
        }
    }
}
