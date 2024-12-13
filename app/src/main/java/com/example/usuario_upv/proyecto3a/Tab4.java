package com.example.usuario_upv.proyecto3a;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;

public class Tab4 extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflar el layout del fragmento
        View view = inflater.inflate(R.layout.ejemplo_tab4, container, false);

        // Configurar clics para cada sección
        View campoInformacion = view.findViewById(R.id.campoInformacion);
        View campoNotificaciones = view.findViewById(R.id.campoNotificaciones);
        View campoAcercaDe = view.findViewById(R.id.campoAcercaDe);
        View campoAsistente = view.findViewById(R.id.campoAsistente);

        // Redirigir a la actividad correspondiente al hacer clic
        campoInformacion.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), UserConfig.class);
            startActivity(intent);
        });

        campoNotificaciones.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AlertActivity.class);
            startActivity(intent);
        });

        campoAcercaDe.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AcercaDeActivity.class);
            startActivity(intent);
        });

        campoAsistente.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), Asistente.class);
            startActivity(intent);
        });

        // Configurar el botón de cerrar sesión
        MaterialButton logoutButton = view.findViewById(R.id.botonCerrarSesion2);
        logoutButton.setOnClickListener(v -> {
            // Limpiar SharedPreferences para cerrar sesión
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear(); // Elimina todas las preferencias guardadas
            editor.apply();

            // Regresar a la pantalla de inicio de sesión
            Intent intent = new Intent(getActivity(), LandingPageActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        return view;
    }
}
