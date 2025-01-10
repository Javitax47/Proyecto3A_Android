package com.example.usuario_upv.proyecto3a;

import android.graphics.PointF;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @class SensorData
 * @brief Clase que representa los datos de un sensor.
 *
 * Esta clase se utiliza para almacenar la información de las mediciones
 * de los sensores, incluyendo el sensor_id, el valor, la marca de tiempo,
 * el ID del usuario asociado, y el tipo de sensor.
 */
public class SensorData {
    private String sensorId;
    private int tipo;
    private float valor;
    private String location;
    private String timestamp;

    // Constructor
    public SensorData(String sensorId, float valor, int tipo, String location, String timestamp) {
        this.sensorId = sensorId;
        this.tipo = tipo;
        this.valor = valor;
        this.location = location;
        this.timestamp = timestamp;
    }

    public double getLatitude() {
        // Extrae la coordenada y (latitud) de la cadena "location"
        String[] coordinates = location.replace("(", "").replace(")", "").split(", ");
        return Double.parseDouble(coordinates[1]); // Latitud (y)
    }

    public double getLongitude() {
        // Extrae la coordenada x (longitud) de la cadena "location"
        String[] coordinates = location.replace("(", "").replace(")", "").split(", ");
        return Double.parseDouble(coordinates[0]); // Longitud (x)
    }


    // Other getters and setters

    public String getSensorId() { return sensorId; }
    public int getTipo() { return tipo; }
    public float getValor() { return valor; }
    public String getTimestamp() { return timestamp; }
    public String getLocation() { return location; }

    static String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        return sdf.format(new Date());
    }
}
