package com.example.usuario_upv.proyecto3a;

import retrofit2.Call;
import retrofit2.http.Body;
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
public interface LogicaFake {

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
     * @brief Inserta una medición de sensor en la base de datos.
     *
     * Este método envía una solicitud POST con la información de la medición
     * en el cuerpo de la solicitud.
     *
     * @param sensorData El objeto SensorData que contiene la información de la medición.
     * @return Un objeto Call que contiene la respuesta de la solicitud.
     */
    @POST("/mediciones")
    Call<Void> createSensorData(@Body SensorData sensorData);

    @POST("/users")
    Call<Void> createUserData(@Body UserData userData);

    @GET("users/login/{email}/{password}")
    Call<LoginResponse> getUserData(
            @Path(value = "email", encoded = true) String email,
            @Path("password") String password
    );
}