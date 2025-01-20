/**
 * @file AcercaDeActivity.java
 * @brief Clase que representa la actividad "Acerca de" en la aplicación.
 *
 * Esta actividad muestra información relacionada con la aplicación,
 * como créditos, información de contacto o detalles del proyecto.
 */
package com.example.usuario_upv.proyecto3a;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

/**
 * @class AcercaDeActivity
 * @brief Clase que implementa la actividad "Acerca de".
 *
 * Esta clase extiende AppCompatActivity y se utiliza para mostrar una
 * pantalla informativa dentro de la aplicación.
 */
public class AcercaDeActivity extends AppCompatActivity {

    /**
     * @brief Método que se llama al crear la actividad.
     *
     * Este método inicializa la interfaz de usuario y establece el diseño
     * correspondiente a la actividad "Acerca de".
     *
     * @param savedInstanceState Estado guardado de la actividad en ejecuciones anteriores.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acercade);
    }
}
