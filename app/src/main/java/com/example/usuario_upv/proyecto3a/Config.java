package com.example.usuario_upv.proyecto3a;

import java.util.ArrayList;

public class Config {
    public static String BASE_URL = "http://airmonitor.ddns.net:13000/";
    public static ArrayList<String> UUIDs = new ArrayList<>();

    public static void addUUID(String uuid) {
        UUIDs.add(uuid);
    }
}
