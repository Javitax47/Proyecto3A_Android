package com.example.usuario_upv.proyecto3a;

/**
 * @class User
 * @brief Clase que representa un usuario.
 *
 * Esta clase contiene información sobre un usuario, incluyendo su nombre de usuario.
 */
public class User {
    private String username;  ///< Nombre de usuario.

    /**
     * @brief Constructor que crea una instancia de User.
     *
     * @param username Nombre de usuario.
     */
    public User(String username) {
        this.username = username;
    }

    // -------------------------------------------------------------------------------
    // Métodos Getters y Setters
    // -------------------------------------------------------------------------------

    /**
     * @brief Obtiene el nombre de usuario.
     *
     * @return Nombre de usuario.
     */
    public String getUsername() {
        return username;
    }

    /**
     * @brief Establece el nombre de usuario.
     *
     * @param username Nombre de usuario a establecer.
     */
    public void setUsername(String username) {
        this.username = username;
    }
} // class User

