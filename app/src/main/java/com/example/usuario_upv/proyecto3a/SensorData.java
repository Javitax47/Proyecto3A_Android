package com.example.usuario_upv.proyecto3a;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SensorData {
    private String type;
    private float value;
    private String timestamp;
    private int userId;

    public SensorData(String type, float value, int userId) {
        this.type = type;
        this.value = value;
        this.userId = userId;
        this.timestamp = getCurrentTimestamp();
    }

    private String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        return sdf.format(new Date()); // Formato ISO 8601
    }

    // Getters y setters
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public float getValue() { return value; }
    public void setValue(float value) { this.value = value; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
}
