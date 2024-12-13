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

public class AlertActivity extends AppCompatActivity implements AlertasAdapter.OnAlertasListener {

    private List<Alertas> alertas; // Lista mutable para almacenar objetos Alertas
    private AlertasAdapter adapter; // Adaptador del RecyclerView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ejemplo_alertas);









        // BOTÓN ATRÁS
        View imageBack = findViewById(R.id.imageBack2);
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finaliza la actividad para regresar
                onBackPressed();
            }
        });






        // Configuración del RecyclerView y el TextView
        RecyclerView recyclerView = findViewById(R.id.recyclerAlertas);
        //TextView mensajeAlerta = findViewById(R.id.mensajeAlerta);

        // Inicializamos la lista de alertas
        alertas = new ArrayList<>();

        // Configuramos el adaptador con la lista mutable y el listener
        adapter = new AlertasAdapter(alertas, this);  // Pasamos 'this' como listener
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Registra el BroadcastReceiver
        IntentFilter filter = new IntentFilter("com.example.usuario_upv.proyecto3a.ALERTA_RECIBIDA");
        registerReceiver(alertReceiver, filter); // Registra el receiver

        // Enviar el broadcast
        Intent intent = new Intent(this, AlertActivity.class);  // 'this' es el contexto de la Activity
        intent.putExtra("alertaCodigo", Alertas.BEACON_NO_ENVIANDO.getCodigo());
        sendBroadcast(intent);  // Enviar el broadcast
    }



    private final BroadcastReceiver alertReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int alertaCodigo = intent.getIntExtra("alertaCodigo", -1);  // Definimos la variable dentro de onReceive

            Log.d("AlertActivity", "Código de alerta recibido: " + alertaCodigo);  // Aquí
            Log.d("AlertActivity", "Broadcast recibido");

            alertaCodigo = intent.getIntExtra("alertaCodigo", -1);

            // Verificamos que el código sea válido
            if (alertaCodigo != -1) {
                // Obtener la alerta por código
                Alertas alerta = getAlertByCodigo(alertaCodigo);

                // Verificamos si se ha encontrado una alerta válida
                if (alerta != null) {
                    // Agregar la alerta a la lista y notificar al adaptador
                    alertas.add(alerta);
                    adapter.notifyItemInserted(alertas.size() - 1);  // Notificar que se ha agregado una nueva alerta

                    // Imprimir el mensaje de la alerta para depuración
                    Log.d("AlertActivity", "Alerta agregada: " + alerta.getMensaje());

                    // Actualizar la visibilidad del RecyclerView
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
                    // En caso de que no se encuentre una alerta válida
                    Log.e("AlertActivity", "No se encontró una alerta con el código: " + alertaCodigo);
                }
                Log.e("AlertActivity", "No se encontró una alerta con el código: " + alertaCodigo);
            }
        }
    };

    // Método para obtener una alerta del enum Alertas por código
    private Alertas getAlertByCodigo(int codigo) {
        for (Alertas alerta : Alertas.values()) {
            if (alerta.getCodigo() == codigo) {
                return alerta;  // Devuelve la alerta correspondiente por el código
            }
        }
        return null;  // Si no se encuentra una alerta con el código, retornamos null
    }

    // No olvides anular el receiver cuando la actividad se destruya
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(alertReceiver);  // Desregistrar el BroadcastReceiver
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

        // Verificamos si el ítem seleccionado es el que borra todas las alertas
        if (item.getItemId() == R.id.papelera) {
            clearAllAlertas();  // Llamamos al método que borra las alertas
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Método para obtener las alertas de la clase Alertas
    private void obtenerAlertas() {
        for (Alertas alerta : Alertas.values()) {
            alertas.add(alerta);
        }
    }

    // Método para borrar todas las alertas
    private void clearAllAlertas() {
        alertas.clear();  // Borramos todas las alertas
        adapter.notifyDataSetChanged();  // Actualizamos el RecyclerView

        // Comprobamos si la lista de alertas está vacía y actualizamos la visibilidad
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

    // Implementación del método de eliminación de alerta
    @Override
    public void onEliminarAlerta(int position) {
        // Eliminar alerta de la lista
        if (position >= 0 && position < alertas.size()) {
            alertas.remove(position);
            adapter.notifyItemRemoved(position);
        }
    }
}