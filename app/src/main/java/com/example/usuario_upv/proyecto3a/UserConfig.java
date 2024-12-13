package com.example.usuario_upv.proyecto3a;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.util.Patterns;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserConfig extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText emailEditText;
    private EditText passwordActualEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private TextView passwordRequirementsTextView;
    private Button saveButton;
    boolean isValid = false;

    private LogicaFake api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ejemplo_user_config);


        // Inicializa los elementos de la interfaz
        usernameEditText = findViewById(R.id.email);
        emailEditText = findViewById(R.id.passw);
        passwordActualEditText = findViewById(R.id.actualpassword);
        passwordEditText = findViewById(R.id.password);
        confirmPasswordEditText = findViewById(R.id.confirmPassword);
        saveButton = findViewById(R.id.buttonLogin);
        // passwordRequirementsTextView = findViewById(R.id.requirements);




        // BOTÓN ATRÁS
        View imageBack = findViewById(R.id.imageBack);
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finaliza la actividad para regresar
                onBackPressed();
            }
        });



        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validatePasswordRequirements(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Cargar datos del usuario desde SharedPreferences
        loadUserDataFromPrefs();

        // Inicializar Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(LogicaFake.class);

        // Configurar botón para guardar cambios
        saveButton.setOnClickListener(v -> handleFormSubmission());
    }

    // Método para validar los requisitos de la contraseña
    private void validatePasswordRequirements(String password) {
        StringBuilder feedback = new StringBuilder("Requisitos de la contraseña:\n");
        boolean isValid = true;

        if (password.length() < 8) {
            feedback.append("- Mínimo 8 caracteres.\n");
            isValid = false;
        }
        if (!password.matches(".*[a-z].*")) {
            feedback.append("- Al menos una letra minúscula.\n");
            isValid = false;
        }
        if (!password.matches(".*[A-Z].*")) {
            feedback.append("- Al menos una letra mayúscula.\n");
            isValid = false;
        }
        if (!password.matches(".*\\d.*")) {
            feedback.append("- Al menos un número.\n");
            isValid = false;
        }
        if (!password.matches(".*[@$!%*?&].*")) {
            feedback.append("- Al menos un carácter especial (@, $, !, %, *, ?, &).\n");
            isValid = false;
        }

        if (isValid) {
            feedback.setLength(0); // Limpiar el mensaje
            feedback.append("¡La contraseña cumple todos los requisitos!");
        }

        // Mostrar el feedback en un TextView (por ejemplo, passwordRequirementsTextView)
        passwordRequirementsTextView.setText(feedback.toString());
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
        String passwordActual = passwordActualEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();

        // Verificar campos vacíos
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

        if(!isValid){
            Toast.makeText(UserConfig.this, "Usa una contraseña válida", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(passwordActual)) {
            passwordActualEditText.setError("Introduce tu contraseña actual");
            passwordActualEditText.requestFocus();
            return;
        }

        // Verificar contraseñas
        if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Las contraseñas no coinciden");
            confirmPasswordEditText.requestFocus();
            return;
        }

        // Verificar la contraseña actual
        verifyCurrentPassword(email, passwordActual, username, password);
    }

    private void verifyCurrentPassword(String email, String passwordActual, String username, String newPassword) {
        UserData userData = new UserData(username, email, passwordActual);
        api.verifyPassword(userData).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    updateUser(username, email, newPassword);
                } else {
                    Toast.makeText(UserConfig.this, "La contraseña actual es incorrecta", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(UserConfig.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUser(String username, String email, String password) {
        UserData userData = new UserData(username, email, password);

        api.updateUser(userData).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    saveUserDataToPrefs(email, username);
                    Toast.makeText(UserConfig.this, "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(UserConfig.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    Toast.makeText(UserConfig.this, "Error al actualizar el perfil", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(UserConfig.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserDataToPrefs(String email, String username) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userEmail", email);
        editor.putString("userName", username);
        editor.apply();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();  // Volver a la actividad anterior
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
