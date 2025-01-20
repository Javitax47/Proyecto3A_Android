/**
 * @file AlertasAdapter.java
 * @brief Adaptador para gestionar y mostrar una lista de alertas en un RecyclerView.
 *
 * Este adaptador enlaza los datos de las alertas con las vistas de cada elemento
 * en el RecyclerView, permitiendo también manejar eventos como la eliminación de alertas.
 */
package com.example.usuario_upv.proyecto3a;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * @class AlertasAdapter
 * @brief Adaptador para el RecyclerView que maneja la lista de alertas.
 *
 * Facilita la vinculación entre los datos de las alertas y las vistas de la interfaz,
 * proporcionando funcionalidades para mostrar y eliminar alertas.
 */
public class AlertasAdapter extends RecyclerView.Adapter<AlertasAdapter.AlertasViewHolder> {

    /**
     * @interface OnAlertasListener
     * @brief Interfaz para manejar eventos de interacción con las alertas.
     *
     * Define un método para manejar la eliminación de alertas por posición.
     */
    public interface OnAlertasListener {
        /**
         * @brief Maneja la eliminación de una alerta.
         *
         * @param position Posición de la alerta a eliminar en la lista.
         */
        void onEliminarAlerta(int position);
    }

    private final List<Alertas> alertasList; /**< Lista de alertas a mostrar. */
    private final OnAlertasListener listener; /**< Listener para manejar eventos de interacción. */

    /**
     * @brief Constructor de AlertasAdapter.
     *
     * Inicializa el adaptador con la lista de alertas y el listener para manejar eventos.
     *
     * @param alertasList Lista de alertas.
     * @param listener Listener para manejar eventos de interacción.
     */
    public AlertasAdapter(List<Alertas> alertasList, OnAlertasListener listener) {
        this.alertasList = alertasList;
        this.listener = listener;
    }

    /**
     * @brief Crea un nuevo ViewHolder para un elemento del RecyclerView.
     *
     * @param parent Vista padre donde se incluirá el nuevo elemento.
     * @param viewType Tipo de vista (no utilizado en este caso).
     * @return Un nuevo objeto AlertasViewHolder.
     */
    @Override
    public AlertasViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alerta, parent, false);
        return new AlertasViewHolder(itemView);
    }

    /**
     * @brief Vincula los datos de una alerta a una vista.
     *
     * @param holder ViewHolder que contiene la vista del elemento.
     * @param position Posición del elemento en la lista.
     */
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

    /**
     * @brief Devuelve el número de elementos en la lista de alertas.
     *
     * @return Cantidad de alertas en la lista.
     */
    @Override
    public int getItemCount() {
        return alertasList.size();
    }

    /**
     * @class AlertasViewHolder
     * @brief Clase ViewHolder que contiene las vistas de un elemento del RecyclerView.
     *
     * Mantiene referencias a las vistas necesarias para mostrar los datos de una alerta.
     */
    public static class AlertasViewHolder extends RecyclerView.ViewHolder {
        public final TextView titulo; /**< Título de la alerta. */
        public final ImageButton borraralerta; /**< Botón para borrar la alerta. */

        /**
         * @brief Constructor de AlertasViewHolder.
         *
         * Inicializa las vistas necesarias para mostrar una alerta.
         *
         * @param itemView Vista correspondiente al elemento.
         */
        public AlertasViewHolder(View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.textoAlerta);
            borraralerta = itemView.findViewById(R.id.borraralerta);
        }
    }
}
