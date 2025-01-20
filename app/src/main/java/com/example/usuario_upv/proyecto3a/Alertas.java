/**
 * @file Alertas.java
 * @brief Enum que representa diferentes tipos de alertas en la aplicación.
 *
 * Cada alerta contiene un código único y un mensaje descriptivo. Además, implementa
 * la interfaz Parcelable para poder ser transmitida entre componentes de Android.
 */
package com.example.usuario_upv.proyecto3a;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @enum Alertas
 * @brief Enum que define los tipos de alertas disponibles.
 *
 * Las alertas incluyen información sobre eventos como fallos en el sensor,
 * temperaturas fuera de rango y niveles de ozono inusuales.
 */
public enum Alertas implements Parcelable {
    BEACON_NO_ENVIANDO(1003, "No se están recibiendo datos del sensor"),
    TEMPERATURA_BAJA(101, "Temperatura muy baja"),
    TEMPERATURA_ALTA(102, "Temperatura excesiva"),
    OZONO_BAJO(201, "Concentración baja de ozono"),
    OZONO_ALTO(202, "Concentración alta de ozono");

    private final int codigo; /**< Código único de la alerta. */
    private final String mensaje; /**< Mensaje descriptivo de la alerta. */

    /**
     * @brief Constructor del enum Alertas.
     *
     * @param codigo Código único de la alerta.
     * @param mensaje Mensaje descriptivo de la alerta.
     */
    Alertas(int codigo, String mensaje) {
        this.codigo = codigo;
        this.mensaje = mensaje;
    }

    /**
     * @brief Obtiene el código de la alerta.
     *
     * @return Código único de la alerta.
     */
    public int getCodigo() {
        return codigo;
    }

    /**
     * @brief Obtiene el mensaje descriptivo de la alerta.
     *
     * @return Mensaje descriptivo de la alerta.
     */
    public String getMensaje() {
        return mensaje;
    }

    /**
     * @brief Constructor del enum para recrear a partir de un Parcel.
     *
     * @param in Objeto Parcel que contiene los datos de la alerta.
     */
    Alertas(Parcel in) {
        this.codigo = values()[in.readInt()].codigo;
        this.mensaje = values()[in.readInt()].mensaje;
    }

    /**
     * @brief Escribe el objeto Alertas en un Parcel.
     *
     * @param dest Objeto Parcel donde se guardará la alerta.
     * @param flags Indicadores adicionales sobre cómo escribir el objeto.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(ordinal());
    }

    /**
     * @brief Describe el contenido del Parcelable.
     *
     * @return Un entero que describe el tipo de objeto Parcelable.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * @brief Creador para reconstruir objetos Alertas desde un Parcel.
     */
    public static final Parcelable.Creator<Alertas> CREATOR = new Parcelable.Creator<Alertas>() {
        @Override
        public Alertas createFromParcel(Parcel in) {
            return Alertas.values()[in.readInt()];
        }

        @Override
        public Alertas[] newArray(int size) {
            return new Alertas[size];
        }
    };
}
