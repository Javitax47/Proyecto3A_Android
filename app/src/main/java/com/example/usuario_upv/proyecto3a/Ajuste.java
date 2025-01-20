/**
 * @file Ajuste.java
 * @brief Clase que representa un elemento de ajuste dentro de la aplicación.
 *
 * Esta clase se utiliza para almacenar información sobre un ajuste,
 * incluyendo un texto descriptivo y una imagen asociada.
 */
package com.example.usuario_upv.proyecto3a;

/**
 * @class Ajuste
 * @brief Clase que modela un ajuste con un texto descriptivo y una imagen.
 *
 * Proporciona métodos para acceder al texto y la imagen del ajuste.
 */
public class Ajuste {
    private String texto;  /**< Texto descriptivo del ajuste. */
    private int imagen;    /**< ID del recurso de la imagen asociada al ajuste. */

    /**
     * @brief Constructor de la clase Ajuste.
     *
     * Inicializa un nuevo objeto Ajuste con el texto y la imagen proporcionados.
     *
     * @param texto Texto descriptivo del ajuste.
     * @param imagen ID del recurso de la imagen asociada.
     */
    public Ajuste(String texto, int imagen) {
        this.texto = texto;
        this.imagen = imagen;
    }

    /**
     * @brief Obtiene el texto descriptivo del ajuste.
     *
     * @return Texto descriptivo del ajuste.
     */
    public String getTexto() {
        return texto;
    }

    /**
     * @brief Obtiene el ID de la imagen asociada al ajuste.
     *
     * @return ID del recurso de la imagen.
     */
    public int getImagen() {
        return imagen;
    }
}
