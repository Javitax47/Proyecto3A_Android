package com.example.usuario_upv.proyecto3a;

import java.util.ArrayList;

public class Config {
    public static String BASE_URL = "http://192.168.212.140:13000/";
    public static ArrayList<String> UUIDs = new ArrayList<>();

    public static void addUUID(String uuid) {
        UUIDs.add(uuid);
    }
}
