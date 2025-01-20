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
    private String sensorId; /**< ID del sensor. */
    private int tipo; /**< Tipo de sensor. */
    private float valor; /**< Valor de la medición del sensor. */
    private Location location; /**< Ubicación del sensor. */
    private String timestamp; /**< Marca de tiempo de la medición. */

    /**
     * @brief Constructor de la clase SensorData.
     * @param sensorId ID del sensor.
     * @param valor Valor de la medición del sensor.
     * @param tipo Tipo de sensor.
     * @param location Ubicación del sensor.
     * @param timestamp Marca de tiempo de la medición.
     */
    public SensorData(String sensorId, float valor, int tipo, Location location, String timestamp) {
        this.sensorId = sensorId;
        this.tipo = tipo;
        this.valor = valor;
        this.location = location;
        this.timestamp = timestamp;
    }

    /**
     * @brief Obtiene la latitud de la ubicación del sensor.
     * @return La latitud de la ubicación del sensor.
     */
    public double getLatitude() {
        return location.getY();
    }

    /**
     * @brief Obtiene la longitud de la ubicación del sensor.
     * @return La longitud de la ubicación del sensor.
     */
    public double getLongitude() {
        return location.getX();
    }

    // Getters y setters

    /**
     * @brief Obtiene el ID del sensor.
     * @return El ID del sensor.
     */
    public String getSensorId() { return sensorId; }

    /**
     * @brief Obtiene el tipo de sensor.
     * @return El tipo de sensor.
     */
    public int getTipo() { return tipo; }

    /**
     * @brief Obtiene el valor de la medición del sensor.
     * @return El valor de la medición del sensor.
     */
    public float getValor() { return valor; }

    /**
     * @brief Obtiene la marca de tiempo de la medición.
     * @return La marca de tiempo de la medición.
     */
    public String getTimestamp() { return timestamp; }

    /**
     * @brief Obtiene la ubicación del sensor.
     * @return La ubicación del sensor.
     */
    public Location getLocation() { return location; }

    /**
     * @brief Obtiene la marca de tiempo actual en formato ISO 8601.
     * @return La marca de tiempo actual.
     */
    static String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        return sdf.format(new Date());
    }

    /**
     * @class Location
     * @brief Clase que representa la ubicación de un sensor.
     *
     * Esta clase se utiliza para almacenar las coordenadas de ubicación
     * de un sensor, incluyendo la longitud y la latitud.
     */
    public static class Location {
        private double x; /**< Longitud de la ubicación del sensor. */
        private double y; /**< Latitud de la ubicación del sensor. */

        /**
         * @brief Constructor de la clase Location.
         * @param x Longitud de la ubicación del sensor.
         * @param y Latitud de la ubicación del sensor.
         */
        public Location(double x, double y) {
            this.x = x;
            this.y = y;
        }

        /**
         * @brief Obtiene la longitud de la ubicación del sensor.
         * @return La longitud de la ubicación del sensor.
         */
        public double getX() {
            return x;
        }

        /**
         * @brief Obtiene la latitud de la ubicación del sensor.
         * @return La latitud de la ubicación del sensor.
         */
        public double getY() {
            return y;
        }
    }
}