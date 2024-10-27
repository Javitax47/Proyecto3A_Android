package com.example.usuario_upv.proyecto3a;

import android.graphics.Point;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @class SensorData
 * @brief Clase que representa los datos de un sensor.
 *
 * Esta clase se utiliza para almacenar la información de las mediciones
 * de los sensores, incluyendo el tipo, el valor, la marca de tiempo
 * y el ID del usuario asociado.
 */
public class SensorData {
    private int type;      ///< Tipo de medición (ej. "ozono", "temperatura").
    private float value;      ///< Valor de la medición.
    private String timestamp;  ///< Marca de tiempo en formato ISO 8601.
    private Point location;
    private String sensorId;



    /**
     * @brief Constructor de la clase SensorData.
     *
     * @param type Tipo de medición (ej. "ozono", "temperatura").
     * @param value Valor de la medición.
     */
    public SensorData(String sensorId, float value,  int type, Point location) {
        this.sensorId = sensorId;
        this.value = value;
        this.type = type;
        this.timestamp = getCurrentTimestamp();
        this.location = location;
    }

    /**
     * @brief Obtiene la marca de tiempo actual en formato ISO 8601.
     *
     * @return La marca de tiempo como una cadena.
     */
    private String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        return sdf.format(new Date()); // Formato ISO 8601
    }

    // Getters y setters

    /**
     * @return Tipo de medición.
     */
    public int getType() { return type; }

    /**
     * @param type Tipo de medición.
     */
    public void setType(int type) { this.type = type; }

    /**
     * @return Valor de la medición.
     */
    public float getValue() { return value; }

    /**
     * @param value Valor de la medición.
     */
    public void setValue(float value) { this.value = value; }

    /**
     * @return Marca de tiempo de la medición.
     */
    public String getTimestamp() { return timestamp; }

    /**
     * @param timestamp Marca de tiempo de la medición.
     */
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }


    public Point getLocation() {
        return location;
    }

    public void setLocation(Point location) {
        this.location = location;
    }

    public String getSensorId() {
        return sensorId;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }
}
