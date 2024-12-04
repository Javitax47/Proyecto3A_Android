package com.example.usuario_upv.proyecto3a;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import com.google.android.material.button.MaterialButton;

public class Tab4 extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflar el layout del fragmento
        View view = inflater.inflate(R.layout.tab4, container, false);

        // Obtener referencias a los botones
        MaterialButton saveButton = view.findViewById(R.id.botonActualizar);
        MaterialButton logoutButton = view.findViewById(R.id.botonCerrarSesion);

        // Configurar el evento de clic para el botón de actualizar
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Iniciar la nueva actividad
                Intent intent = new Intent(getActivity(), UserConfig.class);
                startActivity(intent);
            }
        });

        // Configurar el evento de clic para el botón de cerrar sesión
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Limpiar SharedPreferences para cerrar sesión
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear(); // Elimina todas las preferencias guardadas
                editor.apply();

                // Regresar a la pantalla de inicio de sesión
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        return view;
    }
}
