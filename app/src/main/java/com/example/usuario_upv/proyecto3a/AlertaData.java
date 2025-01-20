/**
 * @file AlertaData.java
 * @brief Clase que representa los datos de una alerta en la aplicación.
 *
 * Esta clase almacena información relacionada con una alerta, como su código,
 * marca de tiempo, ubicación, y un identificador único.
 */
package com.example.usuario_upv.proyecto3a;

/**
 * @class AlertaData
 * @brief Clase que modela los datos de una alerta.
 *
 * Contiene información como el código de la alerta, marca de tiempo, ubicación
 * y un identificador único.
 */
public class AlertaData {
    private int codigo; /**< Código de la alerta. */
    private String timestamp; /**< Marca de tiempo de la alerta. */
    private Location location; /**< Ubicación de la alerta. */
    private static int id; /**< Identificador único de la alerta. */

    /**
     * @brief Obtiene el código de la alerta.
     *
     * @return Código de la alerta.
     */
    public int getCodigo() {
        return codigo;
    }

    /**
     * @brief Establece el código de la alerta.
     *
     * @param codigo Código de la alerta.
     */
    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    /**
     * @brief Obtiene la marca de tiempo de la alerta.
     *
     * @return Marca de tiempo de la alerta.
     */
    public String getTimestamp() {
        return timestamp;
    }

    /**
     * @brief Establece la marca de tiempo de la alerta.
     *
     * @param timestamp Marca de tiempo de la alerta.
     */
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * @brief Obtiene la ubicación de la alerta.
     *
     * @return Objeto Location que representa la ubicación de la alerta.
     */
    public Location getLocation() {
        return location;
    }

    /**
     * @brief Establece la ubicación de la alerta.
     *
     * @param location Objeto Location que representa la ubicación de la alerta.
     */
    public void setLocation(Location location) {
        this.location = location;
    }

    /**
     * @brief Obtiene el identificador único de la alerta.
     *
     * @return Identificador único de la alerta.
     */
    public static int getId() {
        return id;
    }

    /**
     * @brief Establece el identificador único de la alerta.
     *
     * @param id Identificador único de la alerta.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @class Location
     * @brief Clase interna que representa la ubicación de una alerta.
     *
     * Contiene información sobre la latitud y longitud de la alerta.
     */
    public static class Location {
        private double latitude; /**< Latitud de la ubicación. */
        private double longitude; /**< Longitud de la ubicación. */

        /**
         * @brief Obtiene la latitud de la ubicación.
         *
         * @return Latitud de la ubicación.
         */
        public double getLatitude() {
            return latitude;
        }

        /**
         * @brief Establece la latitud de la ubicación.
         *
         * @param latitude Latitud de la ubicación.
         */
        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        /**
         * @brief Obtiene la longitud de la ubicación.
         *
         * @return Longitud de la ubicación.
         */
        public double getLongitude() {
            return longitude;
        }

        /**
         * @brief Establece la longitud de la ubicación.
         *
         * @param longitude Longitud de la ubicación.
         */
        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }
    }
}
