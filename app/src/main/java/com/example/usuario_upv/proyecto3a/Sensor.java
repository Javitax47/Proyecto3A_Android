package com.example.usuario_upv.proyecto3a;

import android.graphics.Point;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @class Sensor
 * @brief Clase que representa un sensor con un UUID y un email asociado.
 *
 * Esta clase proporciona métodos para obtener y establecer el UUID y el email
 * del sensor. Se utiliza para identificar y gestionar sensores en la aplicación.
 */
public class Sensor {
    private String email; /**< Email asociado al sensor. */
    private String uuid; /**< UUID del sensor. */

    /**
     * @brief Constructor de la clase Sensor.
     * @param uuid UUID del sensor.
     * @param email Email asociado al sensor.
     */
    public Sensor(String uuid, String email) {
        this.email = email;
        this.uuid = uuid;
    }

    /**
     * @brief Obtiene el UUID del sensor.
     * @return El UUID del sensor.
     */
    public String getUuid() { return uuid; }

    /**
     * @brief Establece el UUID del sensor.
     * @param uuid El nuevo UUID del sensor.
     */
    public void setUuid(String uuid) { this.uuid = uuid; }

    /**
     * @brief Obtiene el email asociado al sensor.
     * @return El email asociado al sensor.
     */
    public String getemail() { return email; }

    /**
     * @brief Establece el email asociado al sensor.
     * @param email El nuevo email asociado al sensor.
     */
    public void setemail(String email) { this.email = email; }
}