package com.example.usuario_upv.proyecto3a;

import android.os.Handler;

/**
 * @brief Clase que representa un temporizador.
 *
 * Esta clase permite ejecutar una tarea periódicamente en un intervalo especificado.
 */
public class Timer {
    /**
     * @brief Manejador para programar y ejecutar tareas.
     */
    private final Handler handler = new Handler();

    /**
     * @brief Tarea envuelta que se ejecuta periódicamente.
     */
    private final Runnable wrappedRunnable;

    /**
     * @brief Intervalo de tiempo en milisegundos entre ejecuciones de la tarea.
     */
    private final long interval;

    /**
     * @brief Indica si el temporizador está en ejecución.
     */
    private boolean isRunning = false;

    /**
     * @brief Constructor de la clase Timer.
     *
     * @param interval Intervalo de tiempo en milisegundos entre ejecuciones de la tarea.
     * @param runnable Tarea a ejecutar periódicamente.
     */

    public Timer(long interval, Runnable runnable) {
        this.interval = interval;

        // Definir wrappedRunnable para que se ejecute en intervalos periódicos
        this.wrappedRunnable = new Runnable() {
            @Override
            public void run() {
                if (isRunning) {
                    runnable.run(); // Ejecutar la tarea del usuario
                    handler.postDelayed(this, interval); // Reprogramar para el próximo intervalo
                }
            }
        };
    }

    /**
     * @brief Inicia el temporizador.
     *
     * Si el temporizador no está en ejecución, comienza a ejecutar la tarea periódicamente.
     */
    public void start() {
        if (!isRunning) {
            isRunning = true;
            handler.postDelayed(wrappedRunnable, interval);
        }
    }

    /**
     * @brief Detiene el temporizador.
     *
     * Si el temporizador está en ejecución, detiene la ejecución periódica de la tarea.
     */
    public void stop() {
        if (isRunning) {
            isRunning = false;
            handler.removeCallbacks(wrappedRunnable);
        }
    }
}

