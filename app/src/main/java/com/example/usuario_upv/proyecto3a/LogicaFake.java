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
 * @interface LogicaFake
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
     * @brief Obtiene las mediciones de la base de datos para una fecha específica.
     *
     * @param fecha Fecha para la cual se desean obtener las mediciones.
     * @return Un objeto Call que contiene una lista de datos de sensores.
     */
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

    /**
     * @brief Crea un nuevo usuario en la base de datos.
     *
     * @param userData El objeto UserData que contiene la información del usuario.
     * @return Un objeto Call que contiene la respuesta de la solicitud.
     */
    @POST("/usuarios")
    Call<Void> createUserData(@Body UserData userData);

    /**
     * @brief Verifica la contraseña de un usuario.
     *
     * @param userData El objeto UserData que contiene la información del usuario.
     * @return Un objeto Call que contiene la respuesta de la solicitud.
     */
    @POST("/usuarios/verify-password")
    Call<Void> verifyPassword(@Body UserData userData);

    /**
     * @brief Autentica un usuario mediante un token.
     *
     * @param token El token de autenticación.
     * @return Un objeto Call que contiene la respuesta de la solicitud.
     */
    @GET("/token/{token}")
    Call<Void> autenticarUsuario(@Path(value = "token", encoded = true) String token);

    /**
     * @brief Obtiene los datos de un usuario mediante su email y contraseña.
     *
     * @param email El email del usuario.
     * @param password La contraseña del usuario.
     * @return Un objeto Call que contiene los datos del usuario.
     */
    @GET("/usuarios/login/{email}/{password}")
    Call<User> getUserData(@Path(value = "email", encoded = true) String email, @Path("password") String password);

    /**
     * @brief Obtiene los sensores de un usuario mediante su email.
     *
     * @param email El email del usuario.
     * @return Un objeto Call que contiene la respuesta de la solicitud.
     */
    @GET("/usuarios/{email}/sensores")
    Call<ResponseBody> getUserSensors(@Path(value = "email", encoded = true) String email);

    /**
     * @brief Crea un nuevo sensor en la base de datos.
     *
     * @param sensor El objeto Sensor que contiene la información del sensor.
     * @return Un objeto Call que contiene la respuesta de la solicitud.
     */
    @POST("/sensores")
    Call<Void> createSensor(@Body Sensor sensor);

    /**
     * @brief Actualiza la información de un usuario.
     *
     * @param userData El objeto UserData que contiene la información del usuario.
     * @return Un objeto Call que contiene la respuesta de la solicitud.
     */
    @PUT("/users/update")
    Call<Void> updateUser(@Body UserData userData);

    /**
     * @brief Obtiene las alertas de un usuario mediante su email.
     *
     * @param email El email del usuario.
     * @return Un objeto Call que contiene una lista de datos de alertas.
     */
    @GET("/alertas/{email}")
    Call<List<AlertaData>> getUserAlerts(@Path(value = "email", encoded = true) String email);

    /**
     * @brief Elimina una alerta específica de un usuario.
     *
     * @param email El email del usuario.
     * @param alertaId El ID de la alerta a eliminar.
     * @return Un objeto Call que contiene la respuesta de la solicitud.
     */
    @DELETE("/alertas/{email}/{alertaId}")
    Call<ResponseBody> deleteAlert(@Path("email") String email, @Path("alertaId") int alertaId);
}