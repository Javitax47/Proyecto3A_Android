package com.example.usuario_upv.proyecto3a;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends AppCompatActivity {

    private Handler handler= new Handler();

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


    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(runnable, 200); // splash después de 2segundos
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

}