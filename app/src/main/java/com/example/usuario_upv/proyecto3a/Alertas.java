package com.example.usuario_upv.proyecto3a;

import android.os.Parcel;
import android.os.Parcelable;

public enum Alertas implements Parcelable {
    BEACON_NO_ENVIANDO(1003, "No se están recibiendo datos del sensor"),
    TEMPERATURA_BAJA(101, "Temperatura muy baja"),
    TEMPERATURA_ALTA(102, "Temperatura excesiva"),
    OZONO_BAJO(201, "Concentración baja de ozono"),
    OZONO_ALTO(202, "Concentración alta de ozono");

    private final int codigo;
    private final String mensaje;

    Alertas(int codigo, String mensaje) {
        this.codigo = codigo;
        this.mensaje = mensaje;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getMensaje() {
        return mensaje;
    }

    // Métodos necesarios para Parcelable
    Alertas(Parcel in) {
        // Reconstruimos usando el ordinal
        this.codigo = values()[in.readInt()].codigo;
        this.mensaje = values()[in.readInt()].mensaje;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // Guardamos el ordinal del enum
        dest.writeInt(ordinal());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Alertas> CREATOR = new Parcelable.Creator<Alertas>() {
        @Override
        public Alertas createFromParcel(Parcel in) {
            // Reconstruimos el enum usando su ordinal
            return Alertas.values()[in.readInt()];
        }

        @Override
        public Alertas[] newArray(int size) {
            return new Alertas[size];
        }
    };
}
