package com.example.usuario_upv.proyecto3a;

import static com.example.usuario_upv.proyecto3a.Tab2.ETIQUETA_LOG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class userConfig extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private Button saveButton;
    private LogicaFake logicaFake;
    LogicaFake api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_config);

        // Inicializa los elementos de la interfaz
        usernameEditText = findViewById(R.id.username);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        confirmPasswordEditText = findViewById(R.id.confirmPassword);
        saveButton = findViewById(R.id.saveButton);

        // Cargar datos del usuario desde SharedPreferences
        loadUserDataFromPrefs();

        // Inicializar Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        logicaFake = retrofit.create(LogicaFake.class);

        // Configura el botón para guardar datos
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleFormSubmission();
            }
        });
    }

    private void loadUserDataFromPrefs() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String savedUsername = sharedPreferences.getString("userName", "");
        String savedEmail = sharedPreferences.getString("userEmail", "");

        // Establecer los valores por defecto en los EditText
        usernameEditText.setText(savedUsername);
        emailEditText.setText(savedEmail);
    }

    private void handleFormSubmission() {
        String username = usernameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();

        // Verifica que los campos no estén vacíos
        if (TextUtils.isEmpty(username)) {
            usernameEditText.setError("El nombre de usuario es obligatorio");
            usernameEditText.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Correo electrónico inválido");
            emailEditText.requestFocus();
            return;
        }

        // Verifica que la contraseña tenga al menos 8 caracteres, incluyendo una letra y un número
        if (TextUtils.isEmpty(password) || password.length() < 8 || !password.matches(".*[a-zA-Z].*") || !password.matches(".*\\d.*")) {
            passwordEditText.setError("La contraseña debe tener al menos 8 caracteres, incluyendo una letra y un número");
            passwordEditText.requestFocus();
            return;
        }

        // Verifica que las contraseñas coincidan
        if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Las contraseñas no coinciden");
            confirmPasswordEditText.requestFocus();
            return;
        }

        api = RetrofitClient.getClient(Config.BASE_URL).create(LogicaFake.class);

        UserData userData = new UserData(username, email, password);

        // Crear objeto de datos de usuario
        Call<Void> call = api.updateUser(userData);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {
                if (response.isSuccessful()) {
                    saveUserDataToPrefs(email, username);

                    // Si todas las validaciones pasan
                    Toast.makeText(userConfig.this, "Datos guardados correctamente.", Toast.LENGTH_SHORT).show();
                    Log.d(ETIQUETA_LOG, "Datos guardados correctamente.");

                    // Establecer los valores por defecto en los EditText
                    usernameEditText.setText(username);
                    emailEditText.setText(email);

                    Intent i = new Intent(userConfig.this, MainActivity.class);
                    startActivity(i);
                } else {
                    Toast.makeText(userConfig.this, "Error al actualizar", Toast.LENGTH_SHORT).show();
                    Log.d(ETIQUETA_LOG, "Error al actualizar: " + response.message());
                    Log.d(ETIQUETA_LOG, userData.toString());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(userConfig.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserDataToPrefs(String email, String username) {
        Log.d(ETIQUETA_LOG, "userEmail " + email + "userName " + username);
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userEmail", email);
        editor.putString("userName", username);
        editor.apply();
    }
}