package com.example.usuario_upv.proyecto3a;

/**
 * @class User
 * @brief Clase que representa un usuario.
 *
 * Esta clase contiene información sobre un usuario, incluyendo su nombre de usuario.
 */
public class User {
    private String username;
    private String email;

    // Getters y setters
    public String getUsername() {
        return username;
    }
    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
