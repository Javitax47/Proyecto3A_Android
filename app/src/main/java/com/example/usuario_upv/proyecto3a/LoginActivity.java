package com.example.usuario_upv.proyecto3a;

import android.net.Uri;

import android.os.Bundle;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


import java.util.List;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText editTextEmail, editTextPassword;
    private Button buttonLogin;
    private ImageView imageViewFingerprint;
    private LogicaFake api;
    private static final String ETIQUETA_LOG = "LoginActivity";
    private Uri uri;
    String param;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ejemplo_login);

        uri = getIntent().getData();

        if (uri != null) {
            List<String> parameters = uri.getPathSegments();
            param = parameters.get(parameters.size() - 1);

            Log.d(ETIQUETA_LOG, "Parámetro Uri: " + param);
        }

        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.passw);
        buttonLogin = findViewById(R.id.buttonLogin);
        //imageViewFingerprint = findViewById(R.id.imageViewFingerprint);

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
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });



        // Configurar Retrofit
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request original = chain.request();
                    HttpUrl url = original.url().newBuilder().build();
                    Request request = original.newBuilder().url(url).build();
                    return chain.proceed(request);
                })
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(LogicaFake.class);

        buttonLogin.setOnClickListener(view -> {
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                editTextEmail.setError("Introducir email");
                return;
            }
            if (TextUtils.isEmpty(password)) {
                editTextPassword.setError("Introducir contraseña");
                return;
            }

            try {
                email = URLEncoder.encode(email, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            verificarToken(email, password);
        });
    }

    private void verificarToken(String email, String password) {
        Call<Void> call = api.autenticarUsuario(param);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {
                if (response.isSuccessful()) {
                    loginUser(email, password);
                } else {
                    Toast.makeText(LoginActivity.this, "Error de autenticación", Toast.LENGTH_SHORT).show();
                    Log.d(ETIQUETA_LOG, "Error de autenticación: " + response);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(ETIQUETA_LOG, "Error de conexión", t);
                Toast.makeText(LoginActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loginUser(String email, String password) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Iniciando sesión...");
        progressDialog.show();

        Call<User> call = api.getUserData(email, password);
        Log.d(ETIQUETA_LOG, "URL llamada: " + call.request().url());

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, retrofit2.Response<User> response) {
                progressDialog.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    Log.d(ETIQUETA_LOG, "Contenido de la respuesta: " + user);

                    if (user.isVerified()) {
                        Toast.makeText(LoginActivity.this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
                        saveUserDataToPrefs(user.getEmail(), user.getUsername());
                        saveUserPasswordToPrefs(password);

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Por favor, verifica tu cuenta antes de iniciar sesión", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Correo o contraseña erróneos", Toast.LENGTH_SHORT).show();
                    Log.d(ETIQUETA_LOG, "Correo o contraseña erróneos: " + response);
                }
            }


            @Override
            public void onFailure(Call<User> call, Throwable t) {
                progressDialog.dismiss();
                Log.e(ETIQUETA_LOG, "Error de conexión", t);
                Toast.makeText(LoginActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
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
    private void saveUserPasswordToPrefs(String password) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userPassword", password);
        editor.apply();
    }
}
