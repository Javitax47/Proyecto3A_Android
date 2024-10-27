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
import android.content.Context;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.ArrayList;
import java.util.List;

public class BLEService extends Service {
    private static final String TAG = "BLEService";
    private static final String CHANNEL_ID = "BLEServiceChannel";

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothManager bluetoothManager;
    private BluetoothLeScanner bluetoothLeScanner;

    private long lastReadingTimestamp = 0; // Variable para almacenar el tiempo de la última lectura
    private boolean sensorDamagedAlertSent = false;
    private boolean erroneousReadingAlertSent = false;
    private boolean beaconNotSendingAlertSent = false;

    private Timer timer;



    private BluetoothDevice lastDevice;
    private ScanResult lastResult;

    @Override
    public void onCreate() {
        super.onCreate();

        long interval = 5000; // 5000 ms = 5 segundos


        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
            bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
            startBLEScan();
        } else {
            Log.e(TAG, "Bluetooth no está habilitado o no es compatible");
        }


        timer = new Timer(interval, new Runnable() {
            @Override
            public void run() {
                List<Integer> alertasActivas = new ArrayList<>();

                // Llama a `checkSensorStatus` con el último dispositivo y resultado
                if (lastDevice != null && lastResult != null) {
                    checkSensorStatus(lastDevice, lastResult);

                    // Verifica el estado de las alertas
                    if (sensorDamagedAlertSent) alertasActivas.add(Alertas.SENSOR_DANADO.getCodigo());
                    if (erroneousReadingAlertSent) alertasActivas.add(Alertas.LECTURAS_ERRONEAS.getCodigo());
                    if (beaconNotSendingAlertSent) alertasActivas.add(Alertas.BEACON_NO_ENVIANDO.getCodigo());
                }


                // Vuelve a iniciar el timer
                timer.start();
            }
        });


    }

    private void startBLEScan() {
        Log.d(TAG, "Iniciando escaneo BLE en segundo plano...");
        bluetoothLeScanner.startScan(scanCallback);
    }

    private final ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            BluetoothDevice device = result.getDevice();
            Log.d(TAG, "Dispositivo encontrado: " + device.getName());


            lastDevice = device;
            lastResult = result;

            checkSensorStatus(device, result);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            for (ScanResult result : results) {
                BluetoothDevice device = result.getDevice();
                Log.d(TAG, "Dispositivo encontrado en batch: " + device.getName());
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Log.e(TAG, "Error en el escaneo: " + errorCode);
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Escaneo de dispositivos BLE")
                .setContentText("Escaneando dispositivos y enviando datos al servidor")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentIntent(pendingIntent)
                .build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC);
        } else {
            startForeground(1, notification);
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (bluetoothLeScanner != null) {
            bluetoothLeScanner.stopScan(scanCallback);
        }

        // Detiene el timer cuando el servicio es destruido
        if (timer != null) {
            timer.stop();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(CHANNEL_ID, "Canal de Servicio BLE", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    private void checkSensorStatus(BluetoothDevice device, ScanResult result) {
        // Lógica para verificar el estado del sensor
        if (!checkIfBeaconNotSending(result)) {
            lastReadingTimestamp = System.currentTimeMillis(); // Actualiza la última lectura
        }

        boolean isSensorDamaged = checkIfSensorIsDamaged(result);
        boolean hasErroneousReadings = checkIfReadingsAreErroneous(result);
        boolean isBeaconNotSending = checkIfBeaconNotSending(result);

        long currentTime = System.currentTimeMillis();
        long timeSinceLastReading = currentTime - lastReadingTimestamp;
        long timeoutThreshold = 5000; // 5 segundos

        if (timeSinceLastReading > timeoutThreshold && !beaconNotSendingAlertSent) {
            sendAlertNotification(Alertas.BEACON_NO_ENVIANDO);
            beaconNotSendingAlertSent = true;
        }

        if (isSensorDamaged && !sensorDamagedAlertSent) {
            sendAlertNotification(Alertas.SENSOR_DANADO);
            sensorDamagedAlertSent = true;
        } else if (hasErroneousReadings && !erroneousReadingAlertSent) {
            sendAlertNotification(Alertas.LECTURAS_ERRONEAS);
            erroneousReadingAlertSent = true;
        }


        // Resetear alertas si el sensor está en estado correcto
        if (!isSensorDamaged) sensorDamagedAlertSent = false;
        if (!hasErroneousReadings) erroneousReadingAlertSent = false;
        if (!isBeaconNotSending) beaconNotSendingAlertSent = false;
    }


    private boolean checkIfSensorIsDamaged(ScanResult result) {
        return checkIfBeaconNotSending(result);
    }

    private boolean checkIfReadingsAreErroneous(ScanResult result) {
        byte[] scanRecord = result.getScanRecord().getBytes();

        if (scanRecord.length < 2) {
            return false;
        }

        int temperature = scanRecord[0];
        int co2Level = scanRecord[1];

        boolean isTemperatureErroneous = temperature < 0 || temperature > 100;
        boolean isCO2LevelErroneous = co2Level < 400 || co2Level > 5000;

        return isTemperatureErroneous || isCO2LevelErroneous;
    }

    private boolean checkIfBeaconNotSending(ScanResult result) {
        byte[] scanRecord = result.getScanRecord().getBytes();

        if (scanRecord == null || scanRecord.length == 0) {
            Log.d(TAG, "El registro de escaneo está vacío, el sensor no está enviando datos.");
            return true;
        }
        return false;
    }

    private void sendAlertNotification(Alertas alerta) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Alerta de Sensor")
                .setContentText(alerta.getMensaje())
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(alerta.getCodigo(), notification); // Usa el código de alerta como ID
    }






}

