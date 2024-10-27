package com.example.usuario_upv.proyecto3a;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.SignInButton;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LandingPageActivity extends AppCompatActivity {

    private String ip = Config.BASE_URL;

    /**
     * @brief EditText donde el usuario ingresa la IP para la conexión.
     */
    private EditText ipInput;

    LogicaFake api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);

        // Comprobar si el usuario ya ha iniciado sesión
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String userEmail = sharedPreferences.getString("userEmail", null);

        ipInput = findViewById(R.id.serverIP);

        if (userEmail != null) {
            // El usuario ya ha iniciado sesión, puedes continuar a la actividad principal
            Toast.makeText(this, "Bienvenido de nuevo", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    /**
     * @brief Actualiza la dirección IP del servidor para la conexión con la API.
     *
     * Este método es llamado cuando el usuario introduce una IP y presiona el botón para actualizarla.
     * Valida que la IP no esté vacía, construye la URL base para las peticiones HTTP mediante Retrofit
     * y actualiza la instancia de la API. Muestra un mensaje en pantalla indicando la nueva IP del servidor.
     *
     * @param view La vista que desencadena este método, generalmente el botón de envío de la IP.
     */
    public void actualizarIP(View view) {
        // Obtener la IP ingresada por el usuario
        ip = ipInput.getText().toString().trim();

        // Validar que la IP no esté vacía
        if (ip.isEmpty()) {
            Toast.makeText(LandingPageActivity.this, "Please enter a valid IP", Toast.LENGTH_SHORT).show();
            return;
        }

        // Configurar la URL base para Retrofit
        String baseUrl = "http://" + ip + ":13000/";
        Config.BASE_URL = baseUrl;
        api = RetrofitClient.getClient(baseUrl).create(LogicaFake.class);


        // Mostrar un mensaje con la IP configurada
        Toast.makeText(LandingPageActivity.this, "Server IP set to: " + baseUrl, Toast.LENGTH_SHORT).show();

        // Verificar la conexión con el servidor
        checkConnection();
    }

    // Método para comprobar la conexión con el servidor
    /**
     * @brief Verifica la conexión con el servidor mediante una llamada a la API.
     *
     * Este método realiza una petición al servidor para verificar si la conexión es exitosa.
     * Si la conexión es exitosa, muestra un mensaje indicando que la conexión ha sido establecida.
     * Si falla, muestra un mensaje de error indicando el problema.
     *
     * Utiliza Retrofit para realizar la llamada asíncrona al servidor.
     */
    private void checkConnection() {
        // Llamada al endpoint 'setup' para verificar la conexión
        Call<Void> call = api.checkConnection();
        call.enqueue(new Callback<Void>() {

            /**
             * @brief Maneja la respuesta del servidor.
             *
             * Si la respuesta es exitosa, muestra un mensaje indicando que la conexión fue exitosa.
             * En caso contrario, muestra un mensaje de fallo en la conexión.
             *
             * @param call Llamada realizada a la API.
             * @param response Respuesta recibida del servidor.
             */
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(LandingPageActivity.this, "Connected to server", Toast.LENGTH_SHORT).show();
                    // Aquí puedes realizar otras operaciones como insertar datos o usuarios
                } else {
                    Toast.makeText(LandingPageActivity.this, "Failed to connect to server", Toast.LENGTH_SHORT).show();
                }
            }

            /**
             * @brief Maneja los errores en la conexión con el servidor.
             *
             * Si la conexión falla debido a un error de red u otro problema, muestra un mensaje de error
             * con la descripción del problema.
             *
             * @param call Llamada realizada a la API.
             * @param t Excepción o error que causó la falla en la conexión.
             */
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(LandingPageActivity.this, "Connection error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void lanzarLogin(View view) {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
    }

    public void lanzarRegister(View view) {
        Intent i = new Intent(this, RegisterActivity.class);
        startActivity(i);
    }

    public void accederAnonimo(View view) {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }
}