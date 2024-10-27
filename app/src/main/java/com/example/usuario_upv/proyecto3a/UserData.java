package com.example.usuario_upv.proyecto3a;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UserData {
    private String username;
    private String email;
    private String password;

    public UserData(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public String toString() {
        return "UserData{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    // Getters y setters
    public String getusername() { return username; }

    public void setusername(String username) { this.username = username; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }
}