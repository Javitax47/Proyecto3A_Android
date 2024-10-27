package com.example.usuario_upv.proyecto3a;

import android.graphics.Point;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Sensor {
    private String email;
    private String uuid;
    public Sensor(String uuid, String email) {
        this.email = email;
        this.uuid = uuid;
    }

    // Getters y setters

    public String getUuid() { return uuid; }

    public void setUuid(String uuid) { this.uuid = uuid; }

    public String getemail() { return email; }

    public void setemail(String email) { this.email = email; }

}