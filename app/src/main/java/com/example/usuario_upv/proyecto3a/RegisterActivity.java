package com.example.usuario_upv.proyecto3a;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import java.util.concurrent.Executor;

public class RegisterActivity extends AppCompatActivity {
    private TextInputEditText editTextEmail, editTextPassword, editTextName;
    private TextView passwordRequirementsTextView;
    private Button buttonReg;
    private LogicaFake logicaFake;
    private LogicaFake api;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    private String savedEmail, savedName;
    private CheckBox checkBoxTerms;

    boolean isValid = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ejemplo_register);

        editTextEmail = findViewById(R.id.username);
        editTextPassword = findViewById(R.id.email);
        editTextName = findViewById(R.id.password);
        buttonReg = findViewById(R.id.saveButton);
        checkBoxTerms = findViewById(R.id.checkbox_terms);
        // HAY QUE ARREGLAR ESTO
        // passwordRequirementsTextView = findViewById(R.id.requirementsRegisterPass);



        // BOTÓN ATRÁS
        View imageBack = findViewById(R.id.imageBack);
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finaliza la actividad para regresar
                onBackPressed();
            }
        });



        // BOTÓN REGISTRO
        TextView botonRegistro = findViewById(R.id.botonRegistro);
        botonRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Iniciar RegisterActivity
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });


        editTextPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validatePasswordRequirements(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });


        // Inicializar Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        logicaFake = retrofit.create(LogicaFake.class);

        // Configuración de BiometricPrompt
        Executor executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(RegisterActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                saveUserDataToPrefs(savedEmail, savedName);
                Toast.makeText(RegisterActivity.this, "Huella registrada exitosamente.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(RegisterActivity.this, "Error de autenticación: " + errString, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(RegisterActivity.this, "Autenticación fallida.", Toast.LENGTH_SHORT).show();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Registrar Huella Digital")
                .setSubtitle("Usa tu huella para proteger tus datos")
                .setNegativeButtonText("Cancelar")
                .build();

        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = String.valueOf(editTextEmail.getText());
                String password = String.valueOf(editTextPassword.getText());
                String nombre = String.valueOf(editTextName.getText());

                // Validación de campos
                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(nombre)) {
                    Toast.makeText(RegisterActivity.this, "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!isValidEmail(email)) {
                    Toast.makeText(RegisterActivity.this, "El formato de correo no es correcto", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(isValid){
                    Toast.makeText(RegisterActivity.this, "Usa una contraseña válida", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Verificar si el checkbox está marcado
                if (!checkBoxTerms.isChecked()) {
                    Toast.makeText(RegisterActivity.this, "Debes aceptar los términos y condiciones.", Toast.LENGTH_SHORT).show();
                    return;
                }

                api = RetrofitClient.getClient(Config.BASE_URL).create(LogicaFake.class);

                // Crear objeto de datos de usuario
                UserData userData = new UserData(nombre, email, password);
                Call<Void> call = api.createUserData(userData);

                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Registro exitoso.", Toast.LENGTH_SHORT).show();

                            // Guardar datos temporalmente para la autenticación biométrica
                            savedEmail = email;
                            savedName = nombre;

                            // Mostrar el prompt biométrico
                            biometricPrompt.authenticate(promptInfo);

                        } else {
                            Toast.makeText(RegisterActivity.this, "Error al registrar", Toast.LENGTH_SHORT).show();
                            Log.d("RegisterActivity", "Error al registrar: " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(RegisterActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            private boolean isValidEmail(String email) {
                return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
            }
        });
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

    public void privacidad(View v){
        Intent intent = new Intent(RegisterActivity.this, Privacidad.class);
        startActivity(intent);
    }

    private void saveUserDataToPrefs(String email, String username) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userEmail", email);
        editor.putString("userName", username);
        editor.apply();
    }
}
