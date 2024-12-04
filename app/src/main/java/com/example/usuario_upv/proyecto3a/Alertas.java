package com.example.usuario_upv.proyecto3a;

public enum Alertas {
    BEACON_NO_ENVIANDO(1003, "Error, no se estan recibiendo datos del sensor."),

    TEMPERATURA_BAJA(101, "Se ha detectado una temperatura muy baja en tu zona"),
    TEMPERATURA_ALTA(102, "Se ha detectado una temperatura excesiva en tu zona"),

    OZONO_BAJO(201, "Se ha detectado un exceso en la concentración de X gas en tu zona"),
    OZONO_ALTO(202, "Se ha detectado una escasa concentración de X gas en tu zona");


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
}

