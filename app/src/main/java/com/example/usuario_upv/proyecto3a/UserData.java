/**
 * @file UserData.java
 * @brief This file contains the UserData class which represents user information.
 */

package com.example.usuario_upv.proyecto3a;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @class UserData
 * @brief A class to represent user data including username, email, and password.
 */
public class UserData {
    private String username; /**< The username of the user */
    private String email;    /**< The email of the user */
    private String password; /**< The password of the user */

    /**
     * @brief Constructor to initialize UserData with username, email, and password.
     * @param username The username of the user.
     * @param email The email of the user.
     * @param password The password of the user.
     */
    public UserData(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    /**
     * @brief Converts the UserData object to a string representation.
     * @return A string representation of the UserData object.
     */
    public String toString() {
        return "UserData{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    /**
     * @brief Gets the username of the user.
     * @return The username of the user.
     */
    public String getusername() { return username; }

    /**
     * @brief Sets the username of the user.
     * @param username The new username of the user.
     */
    public void setusername(String username) { this.username = username; }

    /**
     * @brief Gets the email of the user.
     * @return The email of the user.
     */
    public String getEmail() { return email; }

    /**
     * @brief Sets the email of the user.
     * @param email The new email of the user.
     */
    public void setEmail(String email) { this.email = email; }

    /**
     * @brief Gets the password of the user.
     * @return The password of the user.
     */
    public String getPassword() { return password; }

    /**
     * @brief Sets the password of the user.
     * @param password The new password of the user.
     */
    public void setPassword(String password) { this.password = password; }
}