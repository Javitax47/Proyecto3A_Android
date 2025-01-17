package com.example.usuario_upv.proyecto3a;

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
    private Location location; // Cambiado a objeto Location
    private String timestamp;

    // Constructor
    public SensorData(String sensorId, float valor, int tipo, Location location, String timestamp) {
        this.sensorId = sensorId;
        this.tipo = tipo;
        this.valor = valor;
        this.location = location;
        this.timestamp = timestamp;
    }

    public double getLatitude() {
        return location.getY();
    }

    public double getLongitude() {
        return location.getX();
    }

    // Getters y setters
    public String getSensorId() { return sensorId; }
    public int getTipo() { return tipo; }
    public float getValor() { return valor; }
    public String getTimestamp() { return timestamp; }
    public Location getLocation() { return location; }

    static String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        return sdf.format(new Date());
    }

    public static class Location {
        private double x; // Longitud
        private double y; // Latitud

        public Location(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }
    }
}
