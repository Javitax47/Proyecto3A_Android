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
    private String password;
    private int actividad_id;
    private String verification_token;
    private boolean is_verified;
    public User(String username, String email, String password, int actividad_id, String verification_token, boolean is_verified) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.actividad_id = actividad_id;
        this.verification_token = verification_token;
        this.is_verified = is_verified;
    }

    // Getters y setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isVerified() {
        return is_verified;
    }

    public void setVerified(boolean verified) {
        is_verified = verified;
    }

    // Sobrecargar el método toString para imprimir las propiedades del objeto
    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", actividad='" + actividad_id + '\'' +
                ", token='" + verification_token + '\'' +
                ", is_verified=" + is_verified +
                '}';
    }
}
