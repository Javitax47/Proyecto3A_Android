package com.example.usuario_upv.proyecto3a;

import android.content.Intent;
import android.os.Bundle;
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

public class userConfig extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_config);

        // Inicializa los elementos de la interfaz
        usernameEditText = findViewById(R.id.username);
        firstNameEditText = findViewById(R.id.firstName);
        lastNameEditText = findViewById(R.id.lastName);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        confirmPasswordEditText = findViewById(R.id.confirmPassword);
        saveButton = findViewById(R.id.saveButton);

        // Configura el botón para guardar datos
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleFormSubmission();
            }
        });
    }

    private void handleFormSubmission() {
        String username = usernameEditText.getText().toString().trim();
        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();

        // Verifica que los campos no estén vacíos
        if (TextUtils.isEmpty(username)) {
            usernameEditText.setError("El nombre de usuario es obligatorio");
            usernameEditText.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(firstName)) {
            firstNameEditText.setError("El nombre es obligatorio");
            firstNameEditText.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(lastName)) {
            lastNameEditText.setError("El apellido es obligatorio");
            lastNameEditText.requestFocus();
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

        // Si todas las validaciones pasan
        Toast.makeText(this, "Datos guardados correctamente.", Toast.LENGTH_SHORT).show();

        Intent i = new Intent(this, Tab4.class);
        startActivity(i);
    }
}
