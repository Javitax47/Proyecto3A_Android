package com.example.usuario_upv.proyecto3a;

/**
 * @class User
 * @brief Clase que representa un usuario.
 *
 * Esta clase contiene información sobre un usuario, incluyendo su nombre de usuario, correo electrónico, contraseña,
 * ID de actividad, token de verificación y estado de verificación.
 */
public class User {
    private String username; /**< Nombre de usuario */
    private String email; /**< Correo electrónico del usuario */
    private String password; /**< Contraseña del usuario */
    private int actividad_id; /**< ID de la actividad asociada al usuario */
    private String verification_token; /**< Token de verificación del usuario */
    private boolean is_verified; /**< Estado de verificación del usuario */

    /**
     * @brief Constructor de la clase User.
     *
     * @param username Nombre de usuario.
     * @param email Correo electrónico del usuario.
     * @param password Contraseña del usuario.
     * @param actividad_id ID de la actividad asociada al usuario.
     * @param verification_token Token de verificación del usuario.
     * @param is_verified Estado de verificación del usuario.
     */
    public User(String username, String email, String password, int actividad_id, String verification_token, boolean is_verified) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.actividad_id = actividad_id;
        this.verification_token = verification_token;
        this.is_verified = is_verified;
    }

    /**
     * @brief Obtiene el nombre de usuario.
     * @return Nombre de usuario.
     */
    public String getUsername() {
        return username;
    }

    /**
     * @brief Establece el nombre de usuario.
     * @param username Nombre de usuario.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @brief Obtiene el correo electrónico del usuario.
     * @return Correo electrónico del usuario.
     */
    public String getEmail() {
        return email;
    }

    /**
     * @brief Establece el correo electrónico del usuario.
     * @param email Correo electrónico del usuario.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @brief Obtiene el estado de verificación del usuario.
     * @return Estado de verificación del usuario.
     */
    public boolean isVerified() {
        return is_verified;
    }

    /**
     * @brief Establece el estado de verificación del usuario.
     * @param verified Estado de verificación del usuario.
     */
    public void setVerified(boolean verified) {
        is_verified = verified;
    }

    /**
     * @brief Sobrecarga el método toString para imprimir las propiedades del objeto.
     * @return Cadena de texto con las propiedades del objeto.
     */
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