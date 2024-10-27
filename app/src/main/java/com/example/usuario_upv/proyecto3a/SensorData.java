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
    private String sensorId;     ///< ID del sensor asociado.
    private int type;      ///< Tipo de medición (ej. "ozono", "temperatura").
    private float value;      ///< Valor de la medición.
    private String timestamp; ///< Marca de tiempo en formato ISO 8601.
    private int userId;       ///< ID del usuario asociado a la medición.

    /**
     * @brief Constructor de la clase SensorData.
     *
     * Este constructor inicializa una nueva instancia de SensorData con el tipo de medición,
     * el valor de la medición, el ID del usuario, el ID del sensor y la marca de tiempo actual.
     *
     * @param sensorId ID del sensor.
     * @param type Tipo de medición (ej. "ozono", "temperatura").
     * @param value Valor de la medición.
     * @param userId ID del usuario que realiza la medición.
     */
    public SensorData(String sensorId, int type, float value, int userId) {
        this.sensorId = sensorId;
        this.type = type;
        this.value = value;
        this.userId = userId;
        this.timestamp = getCurrentTimestamp();
    }

    /**
     * @brief Obtiene la marca de tiempo actual en formato ISO 8601.
     *
     * Este método genera la marca de tiempo en el formato ISO 8601, lo cual permite que los datos
     * tengan una referencia de tiempo estándar para su almacenamiento.
     *
     * @return La marca de tiempo como una cadena.
     */
    private String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        return sdf.format(new Date()); // Formato ISO 8601
    }

    // Getters y setters

    /**
     * @brief Obtiene el ID del sensor.
     *
     * Este método devuelve el ID del sensor almacenado en esta instancia.
     *
     * @return ID del sensor.
     */
    public String getSensorId() { return sensorId; }

    /**
     * @brief Establece el ID del sensor.
     *
     * Este método establece el ID del sensor para esta instancia.
     *
     * @param sensorId ID del sensor.
     */
    public void setSensorId(String sensorId) { this.sensorId = sensorId; }

    /**
     * @brief Obtiene el tipo de medición.
     *
     * Este método devuelve el tipo de medición almacenado en esta instancia.
     *
     * @return Tipo de medición.
     */
    public int getType() { return type; }

    /**
     * @brief Establece el tipo de medición.
     *
     * Este método establece el tipo de medición para esta instancia.
     *
     * @param type Tipo de medición.
     */
    public void setType(int type) { this.type = type; }

    /**
     * @brief Obtiene el valor de la medición.
     *
     * Este método devuelve el valor de la medición almacenado en esta instancia.
     *
     * @return Valor de la medición.
     */
    public float getValue() { return value; }

    /**
     * @brief Establece el valor de la medición.
     *
     * Este método establece el valor de la medición para esta instancia.
     *
     * @param value Valor de la medición.
     */
    public void setValue(float value) { this.value = value; }

    /**
     * @brief Obtiene la marca de tiempo de la medición.
     *
     * Este método devuelve la marca de tiempo asociada a la medición en formato ISO 8601.
     *
     * @return Marca de tiempo de la medición.
     */
    public String getTimestamp() { return timestamp; }

    /**
     * @brief Establece la marca de tiempo de la medición.
     *
     * Este método permite establecer manualmente la marca de tiempo para esta instancia.
     *
     * @param timestamp Marca de tiempo de la medición.
     */
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    /**
     * @brief Obtiene el ID del usuario asociado a la medición.
     *
     * Este método devuelve el ID del usuario que está asociado con la medición.
     *
     * @return ID del usuario asociado a la medición.
     */
    public int getUserId() { return userId; }

    /**
     * @brief Establece el ID del usuario asociado a la medición.
     *
     * Este método establece el ID del usuario para la medición.
     *
     * @param userId ID del usuario asociado a la medición.
     */
    public void setUserId(int userId) { this.userId = userId; }
}