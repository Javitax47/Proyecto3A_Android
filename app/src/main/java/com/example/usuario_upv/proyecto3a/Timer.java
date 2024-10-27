package com.example.usuario_upv.proyecto3a;

import android.os.Handler;




public class Timer {
    private final Handler handler = new Handler();
    private final Runnable runnable;
    private final long interval;
    private boolean isRunning = false;

    public Timer(long interval, Runnable runnable) {
        this.interval = interval;
        this.runnable = runnable;
    }


    public void start() {
        if (!isRunning) {
            isRunning = true;
            handler.postDelayed(runnable, interval);
        }
    }

    public void stop() {
        if (isRunning) {
            isRunning = false;
            handler.removeCallbacks(runnable);
        }
    }
}

