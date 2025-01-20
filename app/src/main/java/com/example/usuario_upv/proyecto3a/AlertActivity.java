/**
 * @file AlertActivity.java
 * @brief Clase que gestiona la actividad de alertas en la aplicación.
 *
 * Esta clase muestra las alertas en un RecyclerView, las recibe a través de un BroadcastReceiver
 * y permite gestionarlas mediante opciones de menú.
 */
package com.example.usuario_upv.proyecto3a;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * @class AlertActivity
 * @brief Clase que implementa la actividad para mostrar y gestionar alertas.
 *
 * La actividad utiliza un RecyclerView para mostrar alertas que se reciben mediante
 * un BroadcastReceiver. También incluye funcionalidades para borrar alertas y gestionar el menú.
 */
public class AlertActivity extends AppCompatActivity implements AlertasAdapter.OnAlertasListener {

    private List<Alertas> alertas; /**< Lista de objetos Alertas para almacenar las alertas. */
    private AlertasAdapter adapter; /**< Adaptador para gestionar las alertas en el RecyclerView. */

    /**
     * @brief Método que se llama al crear la actividad.
     *
     * Inicializa el RecyclerView, el adaptador, y registra un BroadcastReceiver para recibir alertas.
     *
     * @param savedInstanceState Estado guardado de la actividad.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ejemplo_alertas);

        View imageBack = findViewById(R.id.imageBack2);
        imageBack.setOnClickListener(v -> onBackPressed());

        RecyclerView recyclerView = findViewById(R.id.recyclerAlertas);
        alertas = new ArrayList<>();
        adapter = new AlertasAdapter(alertas, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        IntentFilter filter = new IntentFilter("com.example.usuario_upv.proyecto3a.ALERTA_RECIBIDA");
        registerReceiver(alertReceiver, filter);
    }

    /**
     * @brief BroadcastReceiver para recibir alertas.
     */
    private final BroadcastReceiver alertReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int alertaCodigo = intent.getIntExtra("alertaCodigo", -1);
            Log.d("AlertActivity", "Código de alerta recibido: " + alertaCodigo);

            if (alertaCodigo != -1) {
                Alertas alerta = getAlertByCodigo(alertaCodigo);

                if (alerta != null) {
                    alertas.add(alerta);
                    adapter.notifyItemInserted(alertas.size() - 1);

                    RecyclerView recyclerView = findViewById(R.id.recyclerAlertas);
                    TextView mensajeAlerta = findViewById(R.id.mensajeAlerta);

                    if (alertas.isEmpty()) {
                        mensajeAlerta.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.INVISIBLE);
                    } else {
                        mensajeAlerta.setVisibility(View.INVISIBLE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                } else {
                    Log.e("AlertActivity", "No se encontró una alerta con el código: " + alertaCodigo);
                }
            }
        }
    };

    /**
     * @brief Obtiene una alerta por su código.
     *
     * @param codigo Código de la alerta.
     * @return Objeto Alertas correspondiente al código, o null si no se encuentra.
     */
    private Alertas getAlertByCodigo(int codigo) {
        for (Alertas alerta : Alertas.values()) {
            if (alerta.getCodigo() == codigo) {
                return alerta;
            }
        }
        return null;
    }

    /**
     * @brief Método que se llama cuando se destruye la actividad.
     *
     * Desregistra el BroadcastReceiver para evitar fugas de memoria.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(alertReceiver);
    }

    /**
     * @brief Infla el menú de opciones.
     *
     * @param menu Objeto Menu a inflar.
     * @return true si el menú se infla correctamente.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_alertas, menu);
        return true;
    }

    /**
     * @brief Maneja las opciones seleccionadas en el menú.
     *
     * @param item Elemento del menú seleccionado.
     * @return true si se maneja la acción correctamente.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        if (item.getItemId() == R.id.papelera) {
            clearAllAlertas();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * @brief Borra todas las alertas de la lista y actualiza la interfaz.
     */
    private void clearAllAlertas() {
        alertas.clear();
        adapter.notifyDataSetChanged();

        RecyclerView recyclerView = findViewById(R.id.recyclerAlertas);
        TextView mensajeAlerta = findViewById(R.id.mensajeAlerta);

        if (alertas.isEmpty()) {
            mensajeAlerta.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
        } else {
            mensajeAlerta.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * @brief Maneja la eliminación de una alerta por posición.
     *
     * @param position Posición de la alerta a eliminar.
     */
    @Override
    public void onEliminarAlerta(int position) {
        if (position >= 0 && position < alertas.size()) {
            alertas.remove(position);
            adapter.notifyItemRemoved(position);
        }
    }
}
