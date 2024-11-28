package com.example.usuario_upv.proyecto3a;

import android.os.Handler;

public class Timer {
    private final Handler handler = new Handler();
    private final Runnable wrappedRunnable;
    private final long interval;
    private boolean isRunning = false;

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

    public void start() {
        if (!isRunning) {
            isRunning = true;
            handler.postDelayed(wrappedRunnable, interval);
        }
    }

    public void stop() {
        if (isRunning) {
            isRunning = false;
            handler.removeCallbacks(wrappedRunnable);
        }
    }
}

