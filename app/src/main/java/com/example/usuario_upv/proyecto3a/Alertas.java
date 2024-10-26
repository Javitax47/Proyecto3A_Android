package com.example.usuario_upv.proyecto3a;

public enum Alertas {
    SENSOR_DANADO(1001, "Alerta: Sensor dañado."),
    LECTURAS_ERRONEAS(1002, "Alerta: Lecturas erróneas."),
    BEACON_NO_ENVIANDO(1003, "Alerta: Beacon no enviando datos.");

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

