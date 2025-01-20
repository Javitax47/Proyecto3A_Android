/**
 * @file BLEService.java
 * @brief Servicio encargado de gestionar la comunicación Bluetooth Low Energy (BLE).
 *
 * Este servicio monitorea dispositivos BLE, gestiona alertas basadas en eventos
 * de conectividad y datos, y utiliza Retrofit para interactuar con un servidor remoto.
 */
package com.example.usuario_upv.proyecto3a;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @class BLEService
 * @brief Servicio encargado de gestionar la conectividad BLE y alertas asociadas.
 *
 * BLEService implementa la gestión de dispositivos BLE, el envío de notificaciones
 * relacionadas con eventos y la comunicación con un servidor remoto utilizando Retrofit.
 */
public class BLEService extends Service {

    private static final String TAG = "BLEService"; /**< Etiqueta para los logs del servicio. */
    private static final String CHANNEL_ID = "BLEChannel"; /**< ID del canal de notificaciones. */
    private BluetoothAdapter bluetoothAdapter; /**< Adaptador Bluetooth del dispositivo. */
    private Timer noDataTimer; /**< Temporizador para detectar la falta de datos. */
    private boolean dataReceived = false; /**< Indica si se han recibido datos del sensor. */

    private int alertaId; /**< ID de la última alerta procesada. */
    private String savedEmail; /**< Email del usuario cargado desde preferencias. */

    private boolean alertasDeDatosActivas = true; /**< Controla si las alertas de datos están activas. */
    private boolean alertasDeMedidasErroneasActivas = true; /**< Controla si las alertas de medidas erróneas están activas. */

    /**
     * @brief Carga los datos del usuario desde las preferencias compartidas.
     */
    private void loadUserDataFromPrefs() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        savedEmail = sharedPreferences.getString("userEmail", "");
    }

    /**
     * @brief Método llamado cuando el servicio es creado.
     *
     * Inicializa el adaptador Bluetooth, configura el canal de notificaciones y
     * prepara el temporizador para manejar la detección de datos faltantes.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "BLEService creado");

        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        loadUserDataFromPrefs();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "BLE Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
            }
        }

        noDataTimer = new Timer(5000, new Runnable() {
            @Override
            public void run() {
                if (!dataReceived && alertasDeDatosActivas) {
                    mostrarNotificacionNoDatos();
                    obtenerAlertasUsuario(savedEmail);
                }
                dataReceived = false;
            }
        });

        IntentFilter filter = new IntentFilter("com.example.usuario_upv.proyecto3a.NOTIFICATION_DELETED");
        registerReceiver(notificationReceiver, filter);
    }

    /**
     * @brief BroadcastReceiver para manejar la eliminación de notificaciones.
     */
    private final BroadcastReceiver notificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && "com.example.usuario_upv.proyecto3a.NOTIFICATION_DELETED".equals(intent.getAction())) {
                int alertaCodigo = intent.getIntExtra("alertaCodigo", -1);
                if (alertaCodigo == Alertas.BEACON_NO_ENVIANDO.getCodigo()) {
                    desactivarAlertasDeDatos();
                } else if (esMedidaErronea(alertaCodigo)) {
                    desactivarAlertasDeMedidasErroneas();
                }
            }
        }
    };

    /**
     * @brief Verifica si un código de alerta corresponde a una medida errónea.
     *
     * @param codigo Código de la alerta.
     * @return true si el código corresponde a una medida errónea, false en caso contrario.
     */
    private boolean esMedidaErronea(int codigo) {
        return codigo == Alertas.TEMPERATURA_BAJA.getCodigo() ||
                codigo == Alertas.TEMPERATURA_ALTA.getCodigo() ||
                codigo == Alertas.OZONO_BAJO.getCodigo() ||
                codigo == Alertas.OZONO_ALTO.getCodigo();
    }

    /**
     * @brief Desactiva las alertas relacionadas con la falta de datos.
     */
    private void desactivarAlertasDeDatos() {
        alertasDeDatosActivas = false;
        if (noDataTimer != null) {
            noDataTimer.stop();
        }
    }

    /**
     * @brief Desactiva las alertas relacionadas con medidas erróneas.
     */
    private void desactivarAlertasDeMedidasErroneas() {
        Log.d(TAG, "Desactivando alertas de medidas erróneas...");
        alertasDeMedidasErroneasActivas = false;
    }
    /**
     * @brief Método llamado cuando se inicia el servicio.
     *
     * Muestra una notificación persistente, inicia el temporizador para detectar
     * la falta de datos y procesa datos del Intent recibido.
     *
     * @param intent Intent que inicia el servicio.
     * @param flags Indicadores sobre cómo iniciar el servicio.
     * @param startId ID único del inicio del servicio.
     * @return Indica cómo debe comportarse el sistema si el servicio es eliminado.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "BLEService iniciado");

        // Mostrar notificación
        mostrarNotificacion();

        // Iniciar el temporizador para detectar la pérdida de datos
        noDataTimer.start();

        // Procesar datos del Intent si es necesario
        if (intent != null) {
            int majorValue = intent.getIntExtra("majorValue", -1);
            int minorValue = intent.getIntExtra("minorValue", -1);
            recibirDatosDeSensor(majorValue, minorValue);
        }

        // Si el sistema mata el servicio, reiniciarlo con la última intención
        return START_STICKY;
    }

    /**
     * @brief Muestra una notificación persistente para el servicio.
     */
    private void mostrarNotificacion() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("BLE Service")
                .setContentText("El servicio BLE está en funcionamiento")
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();

        startForeground(1, notification);
    }

    /**
     * @brief Maneja la recepción de datos del sensor.
     *
     * Reinicia el temporizador cuando se reciben nuevos datos.
     *
     * @param majorValue Valor mayor recibido del sensor.
     * @param minorValue Valor menor recibido del sensor.
     */
    public void recibirDatosDeSensor(int majorValue, int minorValue) {
        Log.d(TAG, "Datos del sensor recibidos: Major - " + majorValue + ", Minor - " + minorValue);
        dataReceived = true; // Se han recibido datos

        // Reiniciar el temporizador cuando se reciben datos
        reiniciarTemporizador();
    }

    /**
     * @brief Reinicia el temporizador utilizado para detectar la falta de datos.
     */
    private void reiniciarTemporizador() {
        noDataTimer.stop(); // Detener el temporizador
        noDataTimer.start(); // Iniciar nuevamente
    }

    /**
     * @brief Muestra una notificación cuando no se reciben datos del sensor.
     */
    private void mostrarNotificacionNoDatos() {
        Alertas alerta = Alertas.BEACON_NO_ENVIANDO;

        // Intent para abrir MainActivity al tocar la notificación
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        // Intent para manejar la eliminación de la notificación
        Intent deleteIntent = new Intent("com.example.usuario_upv.proyecto3a.NOTIFICATION_DELETED");
        deleteIntent.putExtra("alertaCodigo", Alertas.BEACON_NO_ENVIANDO.getCodigo());
        PendingIntent deletePendingIntent = PendingIntent.getBroadcast(this, 0, deleteIntent, PendingIntent.FLAG_IMMUTABLE);

        // Construcción de la notificación
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Error de recepción de datos")
                .setContentText(Alertas.BEACON_NO_ENVIANDO.getMensaje())
                .setSmallIcon(R.drawable.elverdaderologonoti) // Cambia por el ícono deseado
                .setContentIntent(pendingIntent) // Acción al tocar la notificación
                .setDeleteIntent(deletePendingIntent) // Acción al eliminar la notificación
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build();

        // Mostrar la notificación
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        if (notificationManager != null) {
            notificationManager.notify(Alertas.BEACON_NO_ENVIANDO.getCodigo(), notification);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null; // Este servicio no está diseñado para ser vinculado
    }

    /**
     * @brief Método llamado cuando el servicio es destruido.
     *
     * Detiene el temporizador y realiza tareas de limpieza.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        noDataTimer.stop(); // Detener el temporizador
        Log.d(TAG, "BLEService destruido");
    }


    /**
     * @brief Obtiene las alertas del usuario desde el servidor.
     *
     * Realiza una llamada a la API para obtener las alertas del usuario y muestra
     * notificaciones basadas en las alertas recibidas.
     *
     * @param email Email del usuario para obtener las alertas.
     */
    private void obtenerAlertasUsuario(String email) {
        // Configurar Retrofit
        LogicaFake api = RetrofitClient.getClient(Config.BASE_URL).create(LogicaFake.class);

        // Realizar la llamada al endpoint de alertas
        Call<List<AlertaData>> call = api.getUserAlerts(email);

        Log.d(TAG, "URL llamada: " + call.request().url());

        call.enqueue(new Callback<List<AlertaData>>() {
            @Override
            public void onResponse(Call<List<AlertaData>> call, Response<List<AlertaData>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<AlertaData> alertas = response.body();
                    for (AlertaData alertaData : alertas) {
                        for (Alertas alerta : Alertas.values()) {
                            if (alerta.getCodigo() == alertaData.getCodigo()) {
                                mostrarNotificacionAlerta(alerta);
                                alertaId = AlertaData.getId();
                                //eliminarAlerta(email, alertaId);
                                eliminarNotificacion(alertaId);
                            }
                        }
                    }
                } else {
                    Log.d(TAG, "Error en la respuesta: " + response.code());
                }
            }


            @Override
            public void onFailure(Call<List<AlertaData>> call, Throwable t) {
                Log.d(TAG, "Error en la respuesta: " + t);
            }
        });
    }
    /**
     * @brief Elimina una notificación específica.
     *
     * @param alertaId ID de la alerta a eliminar.
     */
    private void eliminarNotificacion(int alertaId) {
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        if (notificationManager != null) {
            notificationManager.cancel(alertaId);
        }
    }

    /**
     * @brief Elimina una alerta del servidor.
     *
     * Realiza una llamada a la API para eliminar una alerta específica del usuario.
     *
     * @param email Email del usuario.
     * @param id ID de la alerta a eliminar.
     */
    private void eliminarAlerta(String email, int id){
        LogicaFake api = RetrofitClient.getClient(Config.BASE_URL).create(LogicaFake.class);
        Call<ResponseBody> call2 = api.deleteAlert(email, id);

        call2.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Alerta eliminada: " + response.body());
                } else {
                    Log.d(TAG, "Error en la respuesta: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "Error en la respuesta: " + t);
            }
        });
    }
    /**
     * @brief Muestra una notificación para una alerta específica.
     *
     * @param alerta Alerta a mostrar en la notificación.
     */
    private void mostrarNotificacionAlerta(Alertas alerta) {
        if (!alertasDeMedidasErroneasActivas) {
            Log.d(TAG, "Las alertas de medidas erróneas están desactivadas. No se enviará notificación.");
            return; // No mostrar notificación si están desactivadas
        }

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        Intent deleteIntent = new Intent("com.example.usuario_upv.proyecto3a.NOTIFICATION_DELETED");
        deleteIntent.putExtra("alertaCodigo", alerta.getCodigo());
        PendingIntent deletePendingIntent = PendingIntent.getBroadcast(this, 0, deleteIntent, PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Alerta Medidas Erróneas")
                .setContentText(alerta.getMensaje())
                .setSmallIcon(R.drawable.elverdaderologonoti)
                .setContentIntent(pendingIntent)
                .setDeleteIntent(deletePendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build();

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        if (notificationManager != null) {
            notificationManager.notify(alerta.getCodigo(), notification);
        }
    }


    /**
     * @brief Envía un broadcast con una alerta específica.
     *
     * @param alerta Alerta a enviar en el broadcast.
     */
    private void enviarAlertaBroadcast(Alertas alerta) {
        Intent intent = new Intent("com.example.usuario_upv.proyecto3a.NEW_ALERT");
        intent.putExtra("alerta", (Parcelable) alerta); // Forzamos el uso de Parcelable
        sendBroadcast(intent);
        Log.d(TAG, "Broadcast enviado con la alerta: " + alerta.getMensaje());
    }

}
