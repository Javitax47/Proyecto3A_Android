package com.example.usuario_upv.proyecto3a;

public enum Alertas {
    BEACON_NO_ENVIANDO(1003, "Alerta: No se envían datos."),

    TEMPERATURA_BAJA(101, "Alerta: Temperatura por debajo del rango permitido."),
    TEMPERATURA_ALTA(102, "Alerta: Temperatura por encima del rango permitido."),

    OZONO_BAJO(201, "Alerta: Ozono por debajo del rango permitido."),
    OZONO_ALTO(202, "Alerta: Ozono por encima del rango permitido.");


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

