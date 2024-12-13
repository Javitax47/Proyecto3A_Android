package com.example.usuario_upv.proyecto3a;

public class Ajuste {
    private String texto;  // Texto del apartado
    private int imagen;    // ID de la imagen

    // Constructor para inicializar los valores
    public Ajuste(String texto, int imagen) {
        this.texto = texto;
        this.imagen = imagen;
    }

    // Getter para obtener el texto
    public String getTexto() {
        return texto;
    }

    // Getter para obtener la imagen
    public int getImagen() {
        return imagen;
    }
}

