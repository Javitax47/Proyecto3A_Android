package com.example.usuario_upv.proyecto3a;

public class AlertaElemento {
    private int codigo;
    private String mensaje;
    private boolean isMedidaErronea;

    // Constructor, getters y setters

    public AlertaElemento(int codigo, String mensaje, boolean isMedidaErronea) {
        this.codigo = codigo;
        this.mensaje = mensaje;
        this.isMedidaErronea = isMedidaErronea;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public boolean isMedidaErronea() {
        return isMedidaErronea;
    }
}

