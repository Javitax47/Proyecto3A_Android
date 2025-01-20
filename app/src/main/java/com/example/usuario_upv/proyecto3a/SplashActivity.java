package com.example.usuario_upv.proyecto3a;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

/**
 * @file SplashActivity.java
 * @brief Actividad de pantalla de bienvenida.
 *
 * Esta actividad muestra una pantalla de bienvenida durante un breve período
 * antes de iniciar la actividad principal de la aplicación.
 */
public class SplashActivity extends AppCompatActivity {

    private Handler handler= new Handler(); /**< Manejador para ejecutar el retraso. */

    /**
     * @brief Método llamado cuando se crea la actividad.
     * @param savedInstanceState Estado previamente guardado de la actividad.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private Runnable runnable= new Runnable() {
        @Override
        public void run() {
            if(!isFinishing()){
                startActivity(new Intent(getApplicationContext(), LandingPageActivity.class));
            }
        }
    };

    /**
     * @brief Método llamado cuando la actividad se reanuda.
     */
    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(runnable, 200); // splash después de 2 segundos
    }

    /**
     * @brief Método llamado cuando la actividad se pausa.
     */
    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }
}