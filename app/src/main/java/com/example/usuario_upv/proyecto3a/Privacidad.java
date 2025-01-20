/**
 * @file Privacidad.java
 * @brief Actividad para mostrar la política de privacidad.
 *
 * Esta clase representa una actividad que muestra la política de privacidad de la aplicación.
 */
package com.example.usuario_upv.proyecto3a;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * @class Privacidad
 * @brief Actividad para la política de privacidad.
 *
 * Esta actividad carga y muestra el contenido de la política de privacidad.
 */
public class Privacidad extends AppCompatActivity {
    /**
     * @brief Método llamado cuando se crea la actividad.
     *
     * Este método configura la vista de la actividad con el layout correspondiente.
     *
     * @param savedInstanceState Si la actividad se está re-inicializando después de
     *        haber sido previamente cerrada, este Bundle contiene los datos más recientes
     *        suministrados en onSaveInstanceState(Bundle). De lo contrario, está nulo.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.privacidad);
    }
}