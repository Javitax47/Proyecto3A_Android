/**
 * @file AjustesAdapter.java
 * @brief Adaptador para gestionar y mostrar una lista de ajustes en un RecyclerView.
 *
 * Este adaptador enlaza una lista de objetos de tipo Ajuste con un RecyclerView,
 * proporcionando una vista personalizada para cada elemento y manejando eventos de clic.
 */
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

/**
 * @class AjustesAdapter
 * @brief Adaptador para manejar la lista de ajustes en el RecyclerView.
 *
 * Este adaptador facilita la visualización de cada elemento en la lista de ajustes,
 * inflando un layout personalizado y gestionando eventos de clic.
 */
public class AjustesAdapter extends RecyclerView.Adapter<AjustesAdapter.AjustesViewHolder> {
    private List<Ajuste> ajustesList; /**< Lista de objetos Ajuste a mostrar. */
    private Context context; /**< Contexto de la aplicación. */
    private OnItemClickListener onItemClickListener; /**< Listener para manejar clics en los elementos. */

    /**
     * @brief Constructor de AjustesAdapter.
     *
     * Inicializa el adaptador con la lista de ajustes, el contexto y el listener de clics.
     *
     * @param ajustesList Lista de ajustes.
     * @param context Contexto de la aplicación.
     * @param onItemClickListener Listener para manejar los eventos de clic.
     */
    public AjustesAdapter(List<Ajuste> ajustesList, Context context, OnItemClickListener onItemClickListener) {
        this.ajustesList = ajustesList;
        this.context = context;
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * @brief Crea un nuevo ViewHolder para un elemento del RecyclerView.
     *
     * @param parent Vista padre donde se incluirá el nuevo elemento.
     * @param viewType Tipo de vista (no utilizado en este caso).
     * @return Un nuevo objeto AjustesViewHolder.
     */
    @NonNull
    @Override
    public AjustesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.ajustes_item, parent, false);
        return new AjustesViewHolder(view);
    }

    /**
     * @brief Vincula los datos de un ajuste a una vista.
     *
     * @param holder ViewHolder que contiene la vista del elemento.
     * @param position Posición del elemento en la lista.
     */
    @Override
    public void onBindViewHolder(@NonNull AjustesViewHolder holder, int position) {
        Ajuste ajuste = ajustesList.get(position);
        holder.texto.setText(ajuste.getTexto());
        holder.imagen.setImageResource(ajuste.getImagen());

        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(position));
    }

    /**
     * @brief Devuelve el número de elementos en la lista.
     *
     * @return Cantidad de elementos en la lista de ajustes.
     */
    @Override
    public int getItemCount() {
        return ajustesList.size();
    }

    /**
     * @interface OnItemClickListener
     * @brief Interfaz para manejar eventos de clic en los elementos.
     */
    public interface OnItemClickListener {
        /**
         * @brief Manejador para el clic en un elemento.
         *
         * @param position Posición del elemento clicado en la lista.
         */
        void onItemClick(int position);
    }

    /**
     * @class AjustesViewHolder
     * @brief Clase ViewHolder que contiene las vistas de un elemento.
     *
     * Mantiene referencias a las vistas del texto y la imagen para un elemento del RecyclerView.
     */
    public static class AjustesViewHolder extends RecyclerView.ViewHolder {
        TextView texto; /**< Texto descriptivo del ajuste. */
        ImageView imagen; /**< Imagen asociada al ajuste. */

        /**
         * @brief Constructor de AjustesViewHolder.
         *
         * Inicializa las vistas del elemento.
         *
         * @param itemView Vista correspondiente al elemento.
         */
        public AjustesViewHolder(View itemView) {
            super(itemView);
            texto = itemView.findViewById(R.id.settingText);
            imagen = itemView.findViewById(R.id.leftIcon);
        }
    }
}
