package com.example.usuario_upv.proyecto3a;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

/**
 * @class SensorAdapter
 * @brief Adaptador para mostrar una lista de sensores en un RecyclerView.
 *
 * Esta clase extiende RecyclerView.Adapter y proporciona un adaptador
 * personalizado para mostrar una lista de sensores en un RecyclerView.
 */
public class SensorAdapter extends RecyclerView.Adapter<SensorAdapter.SensorViewHolder> {
    private List<String> sensorList; /**< Lista de sensores. */

    /**
     * @brief Constructor de la clase SensorAdapter.
     * @param sensorList Lista de sensores a mostrar.
     */
    public SensorAdapter(List<String> sensorList) {
        this.sensorList = sensorList;
    }

    /**
     * @brief Crea nuevos ViewHolder cuando no hay suficientes ViewHolder existentes.
     * @param parent El ViewGroup al que se añadirá el nuevo View.
     * @param viewType El tipo de vista del nuevo View.
     * @return Un nuevo SensorViewHolder.
     */
    @Override
    public SensorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sensor_item, parent, false);
        return new SensorViewHolder(view);
    }

    /**
     * @brief Vincula los datos del sensor a un ViewHolder.
     * @param holder El ViewHolder que debe ser actualizado.
     * @param position La posición del sensor en la lista.
     */
    @Override
    public void onBindViewHolder(SensorViewHolder holder, int position) {
        String sensor = sensorList.get(position);
        holder.nameTextView.setText(sensor);
        holder.typeTextView.setText("Sparkfun");
        holder.statusTextView.setText("Detectado");
    }

    /**
     * @brief Obtiene el número de sensores en la lista.
     * @return El número de sensores en la lista.
     */
    @Override
    public int getItemCount() {
        return sensorList.size();
    }

    /**
     * @class SensorViewHolder
     * @brief ViewHolder para los elementos del sensor.
     *
     * Esta clase proporciona una referencia a las vistas para cada elemento de la lista.
     */
    static class SensorViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView; /**< TextView para el nombre del sensor. */
        TextView typeTextView; /**< TextView para el tipo del sensor. */
        TextView statusTextView; /**< TextView para el estado del sensor. */

        /**
         * @brief Constructor de la clase SensorViewHolder.
         * @param itemView La vista del elemento del sensor.
         */
        SensorViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.sensorNameTextView);
            typeTextView = itemView.findViewById(R.id.sensorTypeTextView);
            statusTextView = itemView.findViewById(R.id.sensorStatusTextView);
        }
    }
}