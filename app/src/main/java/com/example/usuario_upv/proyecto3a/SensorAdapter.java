package com.example.usuario_upv.proyecto3a;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class SensorAdapter extends RecyclerView.Adapter<SensorAdapter.SensorViewHolder> {
    private List<String> sensorList;

    public SensorAdapter(List<String> sensorList) {
        this.sensorList = sensorList;
    }

    @Override
    public SensorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Change this line to inflate the correct layout
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sensor_item, parent, false);
        return new SensorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SensorViewHolder holder, int position) {
        String sensor = sensorList.get(position);
        holder.nameTextView.setText(sensor);
        holder.typeTextView.setText("Sparkfun");
        holder.statusTextView.setText("Detectado");
    }

    @Override
    public int getItemCount() {
        return sensorList.size();
    }

    static class SensorViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView typeTextView;
        TextView statusTextView;

        SensorViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.sensorNameTextView);
            typeTextView = itemView.findViewById(R.id.sensorTypeTextView);
            statusTextView = itemView.findViewById(R.id.sensorStatusTextView);
        }
    }
}