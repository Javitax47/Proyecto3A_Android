package com.example.usuario_upv.proyecto3a;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NotificationReceiver extends BroadcastReceiver {
    private static final String TAG = "NotificationReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && intent.getAction() != null) {
            if ("com.example.usuario_upv.proyecto3a.NOTIFICATION_DELETED".equals(intent.getAction())) {
                int alertaCodigo = intent.getIntExtra("alertaCodigo", -1);
                Log.d(TAG, "Notificación eliminada. Código de alerta: " + alertaCodigo);

                // Aquí puedes realizar cualquier acción necesaria, como desactivar alertas
                Alertas alerta = getAlertByCodigo(alertaCodigo);
                if (alerta != null) {
                    Log.d(TAG, "Alerta manejada: " + alerta.getMensaje());
                    // Agrega lógica adicional si es necesario, como actualizar la UI o el estado.
                } else {
                    Log.e(TAG, "No se encontró una alerta válida con el código: " + alertaCodigo);
                }
            }
        }
    }

    // Método para obtener una alerta del enum Alertas por su código
    private Alertas getAlertByCodigo(int codigo) {
        for (Alertas alerta : Alertas.values()) {
            if (alerta.getCodigo() == codigo) {
                return alerta;
            }
        }
        return null;
    }
}