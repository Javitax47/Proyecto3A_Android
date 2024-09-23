package com.example.usuario_upv.proyecto3a;

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
    private String type;      ///< Tipo de medición (ej. "CO2", "temperatura").
    private float value;      ///< Valor de la medición.
    private String timestamp;  ///< Marca de tiempo en formato ISO 8601.
    private int userId;      ///< ID del usuario asociado a la medición.

    /**
     * @brief Constructor de la clase SensorData.
     *
     * @param type Tipo de medición (ej. "CO2", "temperatura").
     * @param value Valor de la medición.
     * @param userId ID del usuario que realiza la medición.
     */
    public SensorData(String type, float value, int userId) {
        this.type = type;
        this.value = value;
        this.userId = userId;
        this.timestamp = getCurrentTimestamp();
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
    public String getType() { return type; }

    /**
     * @param type Tipo de medición.
     */
    public void setType(String type) { this.type = type; }

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

    /**
     * @return ID del usuario asociado a la medición.
     */
    public int getUserId() { return userId; }

    /**
     * @param userId ID del usuario asociado a la medición.
     */
    public void setUserId(int userId) { this.userId = userId; }
}
