package com.example.usuario_upv.proyecto3a;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class AlertActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.avisos);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);  // Establecer el Toolbar como ActionBar
        getSupportActionBar().setTitle("Notificaciones");

        // Habilitar el botón Atrás
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    // Inflar el menú
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_alertas, menu); // Asegúrate de que el XML del menú esté correcto
        return true;
    }

    // Manejar la selección del botón en el menú
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        // Manejo de otros botones si los tienes
        switch (item.getItemId()) {
                // Acción al presionar el botón del menú
                // Por ejemplo, puedes mostrar un mensaje o realizar alguna otra acción
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
