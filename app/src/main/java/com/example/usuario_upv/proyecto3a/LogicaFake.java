package com.example.usuario_upv.proyecto3a;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

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

    @GET("/mediciones/{fecha}")
    Call<List<SensorData>> medicionesbbdd(@Path(value = "fecha", encoded = true) String fecha);

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

    @POST("/usuarios")
    Call<Void> createUserData(@Body UserData userData);

    @POST("/usuarios/verify-password")
    Call<Void> verifyPassword(@Body UserData userData);

    @GET("/token/{token}")
    Call<Void> autenticarUsuario(@Path(value = "token", encoded = true) String token);

    @GET("/usuarios/login/{email}/{password}")
    Call<User> getUserData(@Path(value = "email", encoded = true) String email, @Path("password") String password);

    @GET("/usuarios/{email}/sensores")
    Call<ResponseBody> getUserSensors(
            @Path(value = "email", encoded = true) String email
    );

    @POST("/sensores")
    Call<Void> createSensor(@Body Sensor sensor);

    @PUT("/users/update")
    Call<Void> updateUser(@Body UserData userData);

    @GET("/alertas/{email}")
    Call<List<AlertaData>> getUserAlerts(@Path(value = "email", encoded = true) String email);

    @DELETE("/alertas/{email}/{alertaId}")
    Call<ResponseBody> deleteAlert(@Path("email") String email, @Path("alertaId") int alertaId);
}