
package com.example.usuario_upv.proyecto3a;
// ------------------------------------------------------------------
// ------------------------------------------------------------------

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

// ------------------------------------------------------------------
// ------------------------------------------------------------------

public class MainActivity extends AppCompatActivity {

    // --------------------------------------------------------------
    // --------------------------------------------------------------
    private static final String ETIQUETA_LOG = ">>>>";

    private static final int CODIGO_PETICION_PERMISOS = 11223344;

    // --------------------------------------------------------------
    // --------------------------------------------------------------
    private BluetoothLeScanner elEscanner;

    private ScanCallback callbackDelEscaneo = null;

    private TextView dato1;
    private TextView dato2;
    private ImageView dato_image;
    private ImageView dato2_image;
    private ImageView image3;
    private ImageView image4;

    private boolean beaconCO2Activo = false;
    private boolean beaconTemperaturaActivo = false;

    private LinearLayout contenedorBeacons;

    // Mapa que almacena las vistas correspondientes a cada dispositivo detectado
    private Map<String, View> vistasDispositivosDetectados = new HashMap<>();

    // Mapa que almacena los temporizadores para eliminar las vistas
    private Map<String, Handler> temporizadoresDispositivos = new HashMap<>();

    // --------------------------------------------------------------
    // --------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(ETIQUETA_LOG, " onCreate(): empieza ");

        inicializarBlueTooth();

        Log.d(ETIQUETA_LOG, " onCreate(): termina ");

        // Inicializa el TextView
        dato1 = findViewById(R.id.valor1);
        dato2 = findViewById(R.id.valor2);

        // Inicializa el ImageView
        dato_image = findViewById(R.id.valor1_image);
        dato2_image = findViewById(R.id.valor2_image);
        image3 = findViewById(R.id.image_buscarDispositivos);
        image4 = findViewById(R.id.image_buscarNuestro);

        // Inicializar el contenedor de los beacons
        contenedorBeacons = findViewById(R.id.contenedorBeacons);

    } // onCreate()

    // --------------------------------------------------------------
    // --------------------------------------------------------------
    private void buscarTodosLosDispositivosBTLE() {
        Log.d(ETIQUETA_LOG, "buscarTodosLosDispositivosBTLE(): empieza");

        this.callbackDelEscaneo = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult resultado) {
                super.onScanResult(callbackType, resultado);
                Log.d(ETIQUETA_LOG, "buscarTodosLosDispositivosBTLE(): onScanResult()");

                mostrarInformacionDispositivoBTLE(resultado, null);
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);
                Log.d(ETIQUETA_LOG, "buscarTodosLosDispositivosBTLE(): onBatchScanResults()");
            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
                Log.d(ETIQUETA_LOG, "buscarTodosLosDispositivosBTLE(): onScanFailed()");
            }
        };

        ScanSettings scanSettings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build();

        Log.d(ETIQUETA_LOG, "buscarTodosLosDispositivosBTLE(): empezamos a escanear todos los dispositivos");

        this.elEscanner.startScan(null, scanSettings, this.callbackDelEscaneo);
    }


    // --------------------------------------------------------------
    // --------------------------------------------------------------
    private void mostrarInformacionDispositivoBTLE(ScanResult resultado, UUID dispositivoBuscado) {
        BluetoothDevice bluetoothDevice = resultado.getDevice();
        byte[] bytes = resultado.getScanRecord().getBytes();

        TramaIBeacon tib = new TramaIBeacon(bytes);
        String uuid = Utilidades.bytesToString(tib.getUUID());

        // Reiniciar o crear el temporizador para este dispositivo
        int majorValue = Utilidades.bytesToInt(tib.getMajor());
        reiniciarTemporizador(uuid, majorValue, tib, dispositivoBuscado); // Reinicia el temporizador al recibir el beacon

        int minorValue = Utilidades.bytesToInt(tib.getMinor());

        // Actualizar los TextView de CO2 o temperatura según el beacon detectado
        runOnUiThread(() -> {
            if (procesarBeacon(majorValue, minorValue) == 1 && Utilidades.stringToUUID(Utilidades.bytesToString(tib.getUUID())).equals(dispositivoBuscado)) {
                beaconCO2Activo = true; // El beacon de CO2 está activo
                dato1.setText("CO2: " + minorValue); // Mostrar el valor de minor inmediatamente
            } else if (procesarBeacon(majorValue, minorValue) == 2 && Utilidades.stringToUUID(Utilidades.bytesToString(tib.getUUID())).equals(dispositivoBuscado)) {
                beaconTemperaturaActivo = true; // El beacon de temperatura está activo
                dato2.setText("ºC: " + minorValue); // Mostrar el valor de minor inmediatamente
            }
        });

        // Verificar si ya tenemos una vista para este dispositivo
        if (vistasDispositivosDetectados.containsKey(uuid)) {
            // El dispositivo ya fue detectado, actualizamos solo los valores visuales de major y minor
            View vistaExistente = vistasDispositivosDetectados.get(uuid);

            // Actualizamos los valores de major y minor en la vista existente
            TextView majorTextView = vistaExistente.findViewById(R.id.majorTextView);
            TextView minorTextView = vistaExistente.findViewById(R.id.minorTextView);

            majorTextView.setText("Major: " + Utilidades.bytesToInt(tib.getMajor()));
            minorTextView.setText("Minor: " + Utilidades.bytesToInt(tib.getMinor()));

            Log.d(ETIQUETA_LOG, "Dispositivo ya registrado. Actualizando valores:");
            Log.d(ETIQUETA_LOG, "major: " + Utilidades.bytesToHexString(tib.getMajor()) +
                    "( " + Utilidades.bytesToInt(tib.getMajor()) + " )");
            Log.d(ETIQUETA_LOG, "minor: " + Utilidades.bytesToHexString(tib.getMinor()) +
                    "( " + Utilidades.bytesToInt(tib.getMinor()) + " )");

        } else {
            // El dispositivo no ha sido detectado antes, agregamos una nueva vista
            View nuevaVista = crearVistaDispositivo(bluetoothDevice, tib, dispositivoBuscado);
            vistasDispositivosDetectados.put(uuid, nuevaVista);

            // Agregar la vista al contenedor visual, por ejemplo un LinearLayout en el NestedScrollView
            LinearLayout contenedor = findViewById(R.id.contenedorBeacons);
            contenedor.addView(nuevaVista);

            Log.d(ETIQUETA_LOG, "Dispositivo nuevo detectado. Añadiendo nueva vista.");
        }
    }

    private void actualizarVistaCO2yTemperatura(int majorValue, int minorValue) {
        // Actualizar el TextView en la interfaz de usuario
        runOnUiThread(() -> {
            if (procesarBeacon(majorValue, minorValue) == 1) {
                beaconCO2Activo = true; // El beacon de CO2 está activo
                dato1.setText("CO2: " + minorValue); // Mostrar el valor de CO2
                dato_image.setVisibility(View.GONE);
                dato_image.setVisibility(View.VISIBLE);
                new Handler().postDelayed(() -> dato_image.setVisibility(View.GONE), 50);
            } else if (procesarBeacon(majorValue, minorValue) == 2) {
                beaconTemperaturaActivo = true; // El beacon de temperatura está activo
                dato2.setText("ºC: " + minorValue); // Mostrar el valor de temperatura
                dato2_image.setVisibility(View.GONE);
                dato2_image.setVisibility(View.VISIBLE);
                new Handler().postDelayed(() -> dato2_image.setVisibility(View.GONE), 50);
            }
        });
    }


    // Método para crear una nueva vista para un dispositivo detectado
    private View crearVistaDispositivo(BluetoothDevice bluetoothDevice, TramaIBeacon tib, UUID dispositivoBuscado) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.vista_dispositivo, null); // Inflar un layout personalizado

        // Configurar la vista con la información del dispositivo
        TextView nombreTextView = view.findViewById(R.id.nombreTextView);
        TextView uuidTextView = view.findViewById(R.id.uuidTextView);
        TextView majorTextView = view.findViewById(R.id.majorTextView);
        TextView minorTextView = view.findViewById(R.id.minorTextView);

        nombreTextView.setText("Nombre: " + bluetoothDevice.getName());
        uuidTextView.setText("UUID: " + Utilidades.bytesToHexString(tib.getUUID()));
        majorTextView.setText("Major: " + Utilidades.bytesToInt(tib.getMajor()));
        minorTextView.setText("Minor: " + Utilidades.bytesToInt(tib.getMinor()));

        return view;
    }

    private void reiniciarTemporizador(final String uuid, final int majorValue, TramaIBeacon tib, UUID dispositivoBuscado) {
        // Si ya existe un temporizador para este dispositivo, cancélalo
        if (temporizadoresDispositivos.containsKey(uuid)) {
            Handler temporizadorExistente = temporizadoresDispositivos.get(uuid);
            temporizadorExistente.removeCallbacksAndMessages(null);
        }

        // Crear un nuevo temporizador
        Handler nuevoTemporizador = new Handler();
        final long tiempoTotal = 5000; // 5 segundos
        final long intervalo = 1000;   // 1 segundo

        final TextView cuentaAtrasCO2 = findViewById(R.id.cuentaAtrasCO2); // Asegúrate de tener este TextView en tu layout
        final TextView cuentaAtrasTemperatura = findViewById(R.id.cuentaAtrasTemperatura); // Asegúrate de tener este TextView en tu layout

        Runnable actualizarCuentaAtras = new Runnable() {
            long tiempoRestante = tiempoTotal;

            @Override
            public void run() {
                long segundosRestantes = tiempoRestante / 1000;

                if (procesarBeacon(Utilidades.bytesToInt(tib.getMajor()), Utilidades.bytesToInt(tib.getMinor())) == 1) {
                    cuentaAtrasCO2.setText("Desconectando CO2 en: " + segundosRestantes + "s...");
                } else if (procesarBeacon(Utilidades.bytesToInt(tib.getMajor()), Utilidades.bytesToInt(tib.getMinor())) == 2) {
                    cuentaAtrasTemperatura.setText("Desconectando Temperatura en: " + segundosRestantes + "s...");
                }

                if (tiempoRestante > 0) {
                    tiempoRestante -= intervalo;
                    nuevoTemporizador.postDelayed(this, intervalo);
                } else {
                    // Cuando el tiempo se acabe, elimina la vista
                    eliminarVistaDispositivo(uuid, majorValue, tib, dispositivoBuscado);
                }
            }
        };

        // Iniciar la cuenta atrás
        nuevoTemporizador.post(actualizarCuentaAtras);

        // Almacenar el nuevo temporizador
        temporizadoresDispositivos.put(uuid, nuevoTemporizador);
    }

    private void eliminarVistaDispositivo(String uuid, int majorValue, TramaIBeacon tib, UUID dispositivoBuscado) {
        if (vistasDispositivosDetectados.containsKey(uuid)) {
            View vistaAEliminar = vistasDispositivosDetectados.get(uuid);

            // Determinar si es el beacon de CO2 o temperatura según el valor de major
            runOnUiThread(() -> {
                if (procesarBeacon(majorValue, 0) == 1) {
                    beaconCO2Activo = false; // El beacon de CO2 ha dejado de ser detectado
                    dato1.setText("CO2: Off"); // Actualizar el TextView a "Off"
                } else if (procesarBeacon(majorValue, 0) == 2) {
                    beaconTemperaturaActivo = false; // El beacon de temperatura ha dejado de ser detectado
                    dato2.setText("ºC: Off"); // Actualizar el TextView a "Off"
                }
            });

            // Eliminar la vista del contenedor visual
            LinearLayout contenedor = findViewById(R.id.contenedorBeacons);
            contenedor.removeView(vistaAEliminar);

            // Remover el dispositivo del mapa de vistas y del mapa de temporizadores
            vistasDispositivosDetectados.remove(uuid);
            temporizadoresDispositivos.remove(uuid);

            Log.d(ETIQUETA_LOG, "Dispositivo eliminado por inactividad: " + uuid);
        }
    }



    public int procesarBeacon(int major, int minor) {
        int tipoMedicion = major >> 8;  // Obtener el identificador de la medición (CO2 o Temperatura)
        int contador = major & 0xFF;    // Obtener el contador (opcional si es útil para tu lógica)

        switch (tipoMedicion) {
            case 11:  // CO2
                Log.d("Beacon", "Dato de CO2 detectado. Valor: " + minor + ", Contador: " + contador);
                return 1;
            case 12:  // Temperatura
                Log.d("Beacon", "Dato de temperatura detectado. Valor: " + minor + ", Contador: " + contador);
                return 2;
            default:
                Log.d("Beacon", "Tipo de dato no reconocido");
                break;
        }
        return 0;
    }

    // --------------------------------------------------------------
    // --------------------------------------------------------------
    private void buscarEsteDispositivoBTLE(final UUID dispositivoBuscado) {
        Log.d(ETIQUETA_LOG, "buscarEsteDispositivoBTLE(): empieza");

        Log.d(ETIQUETA_LOG, "buscarEsteDispositivoBTLE(): instalamos scan callback");

        this.callbackDelEscaneo = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult resultado) {
                super.onScanResult(callbackType, resultado);
                Log.d(ETIQUETA_LOG, "buscarEsteDispositivoBTLE(): onScanResult()");

                byte[] bytes = resultado.getScanRecord().getBytes();

                TramaIBeacon tib = new TramaIBeacon(bytes);
                UUID uuid = Utilidades.stringToUUID(Utilidades.bytesToString(tib.getUUID()));

                if (uuid.equals(dispositivoBuscado)) {
                    Log.d(ETIQUETA_LOG, "Dispositivo encontrado: " + uuid);
                    mostrarInformacionDispositivoBTLE(resultado, dispositivoBuscado);

                    int majorValue = Utilidades.bytesToInt(tib.getMajor());
                    int minorValue = Utilidades.bytesToInt(tib.getMinor());

                    // Llamar al nuevo método para actualizar la interfaz de usuario
                    actualizarVistaCO2yTemperatura(majorValue, minorValue);
                } else {
                    Log.d(ETIQUETA_LOG, "Dispositivo no objetivo encontrado: " + uuid);
                }
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);
                Log.d(ETIQUETA_LOG, "buscarEsteDispositivoBTLE(): onBatchScanResults()");
            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
                Log.d(ETIQUETA_LOG, "buscarEsteDispositivoBTLE(): onScanFailed()");
            }
        };

        // Opciones de escaneo (opcional, para configuración avanzada)
        ScanSettings scanSettings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)  // Modo de escaneo rápido
                .build();

        Log.d(ETIQUETA_LOG, "buscarEsteDispositivoBTLE(): empezamos a escanear buscando: " + dispositivoBuscado);

        // Iniciar el escaneo sin filtros
        this.elEscanner.startScan(null, scanSettings, this.callbackDelEscaneo);
    }

    // --------------------------------------------------------------
    // --------------------------------------------------------------

    public void botonBuscarNuestroDispositivoBTLEPulsado(View v) {
        Log.d(ETIQUETA_LOG, " boton nuestro dispositivo BTLE Pulsado");
        runOnUiThread(() -> image4.setVisibility(View.VISIBLE));
        this.buscarEsteDispositivoBTLE(Utilidades.stringToUUID("HeyJavierJavier!"));
    } // ()

    // --------------------------------------------------------------
    // --------------------------------------------------------------
    private void detenerBusquedaDispositivosBTLE() {
        if (this.callbackDelEscaneo == null) {
            return;
        }
        this.elEscanner.stopScan(this.callbackDelEscaneo);
        this.callbackDelEscaneo = null;

        // Actualizar los TextView para que muestren "Off" cuando se detenga la búsqueda
        runOnUiThread(() -> {
            runOnUiThread(() -> image3.setVisibility(View.GONE));
            runOnUiThread(() -> image4.setVisibility(View.GONE));
            dato1.setText("CO2: Off");
            dato2.setText("ºC: Off");
        });
    } // ()


    // --------------------------------------------------------------
    // --------------------------------------------------------------
    public void botonBuscarDispositivosBTLEPulsado(View v) {
        Log.d(ETIQUETA_LOG, " boton buscar dispositivos BTLE Pulsado");
        runOnUiThread(() -> image3.setVisibility(View.VISIBLE));
        this.buscarTodosLosDispositivosBTLE();
    } // ()

    // --------------------------------------------------------------
    // --------------------------------------------------------------
    public void botonDetenerBusquedaDispositivosBTLEPulsado(View v) {
        Log.d(ETIQUETA_LOG, " boton detener busqueda dispositivos BTLE Pulsado");
        this.detenerBusquedaDispositivosBTLE();
    } // ()

    // --------------------------------------------------------------
    // --------------------------------------------------------------
    private void inicializarBlueTooth() {
        Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): obtenemos adaptador BT ");

        BluetoothAdapter bta = BluetoothAdapter.getDefaultAdapter();

        Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): habilitamos adaptador BT ");

        bta.enable();

        Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): habilitado =  " + bta.isEnabled());

        Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): estado =  " + bta.getState());

        Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): obtenemos escaner btle ");

        this.elEscanner = bta.getBluetoothLeScanner();

        if (this.elEscanner == null) {
            Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): Socorro: NO hemos obtenido escaner btle  !!!!");
        }

        Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): voy a perdir permisos (si no los tuviera) !!!!");

        // Comprobamos la versión de Android
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Para Android 12 y versiones posteriores, pedimos permisos de "dispositivos cercanos"
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        MainActivity.this,
                        new String[]{Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT},
                        CODIGO_PETICION_PERMISOS
                );
            } else {
                Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): parece que YA tengo los permisos necesarios en Android 12+ !!!!");
            }
        } else {
            // Para Android 11 y versiones anteriores, pedimos los permisos de Bluetooth y localización antiguos
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        MainActivity.this,
                        new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.ACCESS_FINE_LOCATION},
                        CODIGO_PETICION_PERMISOS
                );
            } else {
                Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): parece que YA tengo los permisos necesarios !!!!");
            }
        }
    } // ()

    // --------------------------------------------------------------
    // --------------------------------------------------------------
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case CODIGO_PETICION_PERMISOS:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(ETIQUETA_LOG, " onRequestPermissionResult(): permisos concedidos  !!!!");
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                } else {
                    Log.d(ETIQUETA_LOG, " onRequestPermissionResult(): Socorro: permisos NO concedidos  !!!!");
                }
                return;
        }
        // Other 'case' lines to check for other
        // permissions this app might request.
    } // ()
} // class

// --------------------------------------------------------------
// --------------------------------------------------------------
// --------------------------------------------------------------
// --------------------------------------------------------------


