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

/**
 * @interface SensorApi
 * @brief Interfaz para definir los endpoints de la API de sensores.
 *
 * Esta interfaz contiene métodos para interactuar con el servidor que gestiona
 * los datos de los sensores y los usuarios.
 */
public interface SensorApi {

    /**
     * @brief Verifica la conexión con el servidor.
     *
     * Este método envía una solicitud GET al servidor para comprobar si está
     * disponible.
     *
     * @return Un objeto Call que contiene la respuesta de la solicitud.
     */
    @GET("/")
    Call<Void> checkConnection();

    /**
     * @brief Crea las tablas necesarias en la base de datos.
     *
     * Este método envía una solicitud GET al endpoint de configuración para
     * crear las tablas requeridas.
     *
     * @return Un objeto Call que contiene la respuesta de la solicitud.
     */
    @GET("setup")
    Call<Void> setupTables();

    /**
     * @brief Inserta un nuevo usuario en la base de datos.
     *
     * Este método envía una solicitud POST con la información del usuario
     * en el cuerpo de la solicitud.
     *
     * @param user El objeto User que contiene la información del nuevo usuario.
     * @return Un objeto Call que contiene la respuesta de la solicitud.
     */
    @POST("users")
    Call<Void> createUser(@Body User user);

    /**
     * @brief Inserta una medición de sensor en la base de datos.
     *
     * Este método envía una solicitud POST con la información de la medición
     * en el cuerpo de la solicitud.
     *
     * @param sensorData El objeto SensorData que contiene la información de la medición.
     * @return Un objeto Call que contiene la respuesta de la solicitud.
     */
    @POST("/")
    Call<Void> createSensorData(@Body SensorData sensorData);

    /**
     * @brief Elimina un usuario de la base de datos por su ID.
     *
     * Este método envía una solicitud DELETE al endpoint correspondiente para
     * eliminar al usuario especificado.
     *
     * @param userId El ID del usuario que se desea eliminar.
     * @return Un objeto Call que contiene la respuesta de la solicitud.
     */
    @DELETE("users/{id}")
    Call<Void> deleteUser(@Path("id") int userId);

    /**
     * @brief Resetea las tablas en la base de datos.
     *
     * Este método envía una solicitud DELETE al endpoint para reiniciar
     * las tablas.
     *
     * @return Un objeto Call que contiene la respuesta de la solicitud.
     */
    @DELETE("reset")
    Call<Void> resetTables();
}
