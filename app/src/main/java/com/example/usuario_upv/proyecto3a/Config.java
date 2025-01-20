/**
 * @file Config.java
 * @brief Configuración de la aplicación.
 *
 * Esta clase contiene la configuración global de la aplicación, incluyendo
 * la URL base para las llamadas a la API y una lista de UUIDs.
 */
package com.example.usuario_upv.proyecto3a;

import java.util.ArrayList;

/**
 * @class Config
 * @brief Clase de configuración de la aplicación.
 *
 * La clase Config proporciona parámetros de configuración globales para la
 * aplicación, como la URL base para las llamadas a la API y una lista de UUIDs.
 */
public class Config {
    /**
     * @brief URL base para las llamadas a la API.
     */
    public static String BASE_URL = "http://airmonitor.ddns.net:13000/";

    /**
     * @brief Lista de UUIDs utilizados en la aplicación.
     */
    public static ArrayList<String> UUIDs = new ArrayList<>();
}