package com.example.usuario_upv.proyecto3a;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @class RetrofitClient
 * @brief Clase para gestionar la instancia de Retrofit.
 *
 * Esta clase proporciona un método para obtener una instancia de Retrofit
 * configurada con una URL base y un convertidor de JSON a objetos Java.
 */
public class RetrofitClient {

    private static Retrofit retrofit = null;

    /**
     * @brief Obtiene la instancia de Retrofit.
     *
     * Este método verifica si la instancia de Retrofit ya existe o si la URL base
     * ha cambiado. Si es así, crea una nueva instancia de Retrofit.
     *
     * @param baseUrl La URL base que se utilizará para las solicitudes de la API.
     * @return La instancia de Retrofit configurada.
     */
    public static Retrofit getClient(String baseUrl) {
        if (retrofit == null || !retrofit.baseUrl().toString().equals(baseUrl)) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}

