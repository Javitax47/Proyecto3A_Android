package com.example.usuario_upv.proyecto3a;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import android.os.Handler;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BLEService extends Service {

    private static final String TAG = "BLEService";
    private static final String CHANNEL_ID = "BLEChannel";
    private BluetoothAdapter bluetoothAdapter;
    private Timer noDataTimer;
    private boolean dataReceived = false;

    private int alertaId;

    private String savedEmail;


    private void loadUserDataFromPrefs() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        savedEmail = sharedPreferences.getString("userEmail", "");
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "BLEService creado");

        // Inicializar el BluetoothManager
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        loadUserDataFromPrefs();

        // Crear el canal de notificación para API 26+
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

        // Inicializar el Timer
        noDataTimer = new Timer(5000, new Runnable() {
            @Override
            public void run() {
                if (!dataReceived) {
                    mostrarNotificacionNoDatos();
                    obtenerAlertasUsuario(savedEmail);// Llamar a la API de alertas aquí
                }
                // Reiniciar el estado de recepción de datos
                dataReceived = false;
            }
        });
    }

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

    public void recibirDatosDeSensor(int majorValue, int minorValue) {
        Log.d(TAG, "Datos del sensor recibidos: Major - " + majorValue + ", Minor - " + minorValue);
        dataReceived = true; // Se han recibido datos

        // Reiniciar el temporizador cuando se reciben datos
        reiniciarTemporizador();
    }

    private void reiniciarTemporizador() {
        noDataTimer.stop(); // Detener el temporizador
        noDataTimer.start(); // Iniciar nuevamente
    }

    private void mostrarNotificacionNoDatos() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        // Utilizamos el mensaje de la alerta correspondiente
        String mensaje = Alertas.BEACON_NO_ENVIANDO.getMensaje();

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Alerta de Datos")
                .setContentText(mensaje) // Usar el mensaje de la alerta
                .setSmallIcon(android.R.drawable.ic_dialog_info) // Cambia esto por tu ícono de notificación o usa uno predeterminado
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH) // Mayor prioridad para que sea visible
                .setAutoCancel(true)
                .build();

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        if (notificationManager != null) {
            notificationManager.notify(Alertas.BEACON_NO_ENVIANDO.getCodigo(), notification); // Usar el código de la alerta como ID
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null; // Este servicio no está diseñado para ser vinculado
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        noDataTimer.stop(); // Detener el temporizador
        Log.d(TAG, "BLEService destruido");
    }



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

    private void eliminarNotificacion(int alertaId) {
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        if (notificationManager != null) {
            notificationManager.cancel(alertaId);
        }
    }


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

    private void mostrarNotificacionAlerta(Alertas alerta) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Alerta de Sistema")
                .setContentText(alerta.getMensaje()) // Mensaje dinámico
                .setSmallIcon(android.R.drawable.ic_dialog_info) // Cambia por un ícono personalizado
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build();

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        if (notificationManager != null) {
            notificationManager.notify(alerta.getCodigo(), notification); // Código único de alerta
        }
    }


}
