package com.example.usuario_upv.proyecto3a;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface SensorApi {

    // Recibir la info de las tablas
    @GET("/")
    Call<Void> checkConnection();

    // Endpoint para crear tablas
    @GET("setup")
    Call<Void> setupTables();

    // Insertar un nuevo usuario
    @POST("users")
    Call<Void> createUser(@Body User user);

    // Insertar una medición de sensor
    @POST("/")
    Call<Void> createSensorData(@Body SensorData sensorData);

    // Eliminar un usuario por ID
    @DELETE("users/{id}")
    Call<Void> deleteUser(@Path("id") int userId);

    // Resetear las tablas
    @DELETE("reset")
    Call<Void> resetTables();
}
