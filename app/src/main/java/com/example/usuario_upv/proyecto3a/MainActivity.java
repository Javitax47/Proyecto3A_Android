package com.example.usuario_upv.proyecto3a;
// ------------------------------------------------------------------
// ------------------------------------------------------------------

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOError;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// ------------------------------------------------------------------
// ------------------------------------------------------------------

/**
 * @brief MainActivity de la aplicación.
 *
 * Esta clase es la actividad principal de la aplicación que gestiona la interfaz de usuario
 * y las interacciones con los sensores a través de Bluetooth LE.
 */
public class MainActivity extends AppCompatActivity {

    // --------------------------------------------------------------
    // Constantes
    // --------------------------------------------------------------
    /**
     * @brief Etiqueta utilizada para el logging.
     */
    private static final String ETIQUETA_LOG = ">>>>";

    /**
     * @brief Código para la petición de permisos necesarios para usar Bluetooth.
     */
    private static final int CODIGO_PETICION_PERMISOS = 11223344;

    // --------------------------------------------------------------
    // Variables Bluetooth
    // --------------------------------------------------------------
    /**
     * @brief Escáner Bluetooth LE utilizado para detectar dispositivos cercanos.
     */
    private BluetoothLeScanner elEscanner;

    /**
     * @brief Callback que maneja los eventos del escaneo Bluetooth LE.
     */
    private ScanCallback callbackDelEscaneo = null;

    // --------------------------------------------------------------
    // Elementos de la interfaz de usuario
    // --------------------------------------------------------------
    /**
     * @brief TextView para mostrar el primer dato.
     */
    private TextView dato1;

    /**
     * @brief TextView para mostrar el segundo dato.
     */
    private TextView dato2;

    /**
     * @brief ImageView para mostrar una imagen asociada al primer dato.
     */
    private ImageView dato_image;

    /**
     * @brief ImageView para mostrar una imagen asociada al segundo dato.
     */
    private ImageView dato2_image;

    /**
     * @brief ImageView adicional para mostrar una tercera imagen.
     */
    private ImageView image3;

    /**
     * @brief ImageView adicional para mostrar una cuarta imagen.
     */
    private ImageView image4;

    /**
     * @brief Cadena que representa el sensor seleccionado.
     */
    private String sensor;

    /**
     * @brief EditText donde el usuario ingresa la IP para la conexión.
     */
    private EditText ipInput;

    /**
     * @brief EditText donde el usuario ingresa la UUID para detectar el beacon deseado.
     */
    private EditText uuidDeseado;

    private String nuevoUuid;
    private String ip = "http://0.0.0.0:13000/";

    // --------------------------------------------------------------
    // Estados de los beacons
    // --------------------------------------------------------------
    /**
     * @brief Estado que indica si el beacon de CO2 está activo.
     */
    private boolean beaconCO2Activo = false;

    /**
     * @brief Estado que indica si el beacon de temperatura está activo.
     */
    private boolean beaconTemperaturaActivo = false;

    /**
     * @brief Instancia de la API que maneja la comunicación con los sensores.
     */
    SensorApi api;

    /**
     * @brief Contenedor que almacena las vistas asociadas a los beacons detectados.
     */
    private LinearLayout contenedorBeacons;

    // --------------------------------------------------------------
    // Mapas para gestión de dispositivos detectados
    // --------------------------------------------------------------
    /**
     * @brief Mapa que almacena las vistas correspondientes a cada dispositivo detectado.
     *
     * La clave es el identificador único del dispositivo, y el valor es la vista asociada.
     */
    private Map<String, View> vistasDispositivosDetectados = new HashMap<>();

    /**
     * @brief Mapa que almacena los temporizadores para eliminar las vistas de dispositivos inactivos.
     *
     * La clave es el identificador único del dispositivo, y el valor es un temporizador
     * para gestionar el tiempo de visualización.
     */
    private Map<String, Handler> temporizadoresDispositivos = new HashMap<>();

    // --------------------------------------------------------------
    // --------------------------------------------------------------
    /**
     * @brief Método llamado cuando se crea la actividad.
     *
     * Este método inicializa la actividad, configurando el layout y asignando las referencias
     * a los elementos de la interfaz de usuario, como los TextView, ImageView, EditText y el contenedor
     * de los beacons. También se inicializa el Bluetooth.
     *
     * @param savedInstanceState Estado previamente guardado de la actividad, si existe.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(ETIQUETA_LOG, "onCreate(): empieza");

        // Inicializa el Bluetooth
        inicializarBlueTooth();

        Log.d(ETIQUETA_LOG, "onCreate(): termina");

        nuevoUuid = "EPSG-GTI-PROY-3A";

        api = RetrofitClient.getClient(ip).create(SensorApi.class);

        // Inicializa los TextView
        dato1 = findViewById(R.id.valor1);
        dato2 = findViewById(R.id.valor2);

        // Inicializa los ImageView
        dato_image = findViewById(R.id.valor1_image);
        dato2_image = findViewById(R.id.valor2_image);
        image3 = findViewById(R.id.image_buscarDispositivos);
        image4 = findViewById(R.id.image_buscarNuestro);

        // Inicializa el EditText para la IP del servidor
        ipInput = findViewById(R.id.serverIP);

        // Inicializa el EditText para la IP del servidor
        uuidDeseado = findViewById(R.id.elegirUuid);

        // Inicializa el contenedor para los beacons detectados
        contenedorBeacons = findViewById(R.id.contenedorBeacons);
    }

    /**
     * @brief Actualiza la dirección IP del servidor para la conexión con la API.
     *
     * Este método es llamado cuando el usuario introduce una IP y presiona el botón para actualizarla.
     * Valida que la IP no esté vacía, construye la URL base para las peticiones HTTP mediante Retrofit
     * y actualiza la instancia de la API. Muestra un mensaje en pantalla indicando la nueva IP del servidor.
     *
     * @param view La vista que desencadena este método, generalmente el botón de envío de la IP.
     */
    public void actualizarIP(View view) {
        // Obtener la IP ingresada por el usuario
        ip = ipInput.getText().toString().trim();

        // Validar que la IP no esté vacía
        if (ip.isEmpty()) {
            Toast.makeText(MainActivity.this, "Please enter a valid IP", Toast.LENGTH_SHORT).show();
            return;
        }

        // Configurar la URL base para Retrofit
        String baseUrl = "http://" + ip + ":13000/";
        api = RetrofitClient.getClient(baseUrl).create(SensorApi.class);


        // Mostrar un mensaje con la IP configurada
        Toast.makeText(MainActivity.this, "Server IP set to: " + baseUrl, Toast.LENGTH_SHORT).show();

        // Verificar la conexión con el servidor
        checkConnection();
    }

    public void actualizarUuid(View view) {
        // Obtener la UUID ingresada por el usuario
        nuevoUuid = uuidDeseado.getText().toString().trim();

        // Validar que la IP no esté vacía
        if (nuevoUuid.isEmpty()) {
            Toast.makeText(MainActivity.this, "Please enter a valid UUID", Toast.LENGTH_SHORT).show();
        }

        Toast.makeText(MainActivity.this, "UUID actualizado", Toast.LENGTH_SHORT).show();
    }


    // Método para comprobar la conexión con el servidor
    /**
     * @brief Verifica la conexión con el servidor mediante una llamada a la API.
     *
     * Este método realiza una petición al servidor para verificar si la conexión es exitosa.
     * Si la conexión es exitosa, muestra un mensaje indicando que la conexión ha sido establecida.
     * Si falla, muestra un mensaje de error indicando el problema.
     *
     * Utiliza Retrofit para realizar la llamada asíncrona al servidor.
     */
    private void checkConnection() {
        // Llamada al endpoint 'setup' para verificar la conexión
        Call<Void> call = api.checkConnection();
        call.enqueue(new Callback<Void>() {

            /**
             * @brief Maneja la respuesta del servidor.
             *
             * Si la respuesta es exitosa, muestra un mensaje indicando que la conexión fue exitosa.
             * En caso contrario, muestra un mensaje de fallo en la conexión.
             *
             * @param call Llamada realizada a la API.
             * @param response Respuesta recibida del servidor.
             */
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Connected to server", Toast.LENGTH_SHORT).show();
                    // Aquí puedes realizar otras operaciones como insertar datos o usuarios
                } else {
                    Toast.makeText(MainActivity.this, "Failed to connect to server", Toast.LENGTH_SHORT).show();
                }
            }

            /**
             * @brief Maneja los errores en la conexión con el servidor.
             *
             * Si la conexión falla debido a un error de red u otro problema, muestra un mensaje de error
             * con la descripción del problema.
             *
             * @param call Llamada realizada a la API.
             * @param t Excepción o error que causó la falla en la conexión.
             */
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Connection error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    // --------------------------------------------------------------
    // --------------------------------------------------------------
    /**
     * @brief Inicia el escaneo de todos los dispositivos Bluetooth Low Energy (BTLE).
     *
     * Este método configura el callback del escaneo para gestionar los resultados obtenidos
     * al buscar dispositivos BTLE cercanos. Utiliza un modo de escaneo de baja latencia
     * para detectar dispositivos rápidamente y mostrar la información relevante de cada uno.
     *
     * El escaneo se inicia utilizando el `BluetoothLeScanner` de Android.
     */
    private void buscarTodosLosDispositivosBTLE() {
        Log.d(ETIQUETA_LOG, "buscarTodosLosDispositivosBTLE(): empieza");

        // Configuración del callback del escaneo BTLE
        this.callbackDelEscaneo = new ScanCallback() {

            /**
             * @brief Callback para un solo resultado de escaneo.
             *
             * Este método se llama cuando se detecta un dispositivo. Se muestra la información del dispositivo encontrado.
             *
             * @param callbackType Tipo de callback del escaneo.
             * @param resultado Resultado del escaneo, que contiene la información del dispositivo detectado.
             */
            @Override
            public void onScanResult(int callbackType, ScanResult resultado) {
                super.onScanResult(callbackType, resultado);
                Log.d(ETIQUETA_LOG, "buscarTodosLosDispositivosBTLE(): onScanResult()");

                // Muestra la información del dispositivo detectado
                mostrarInformacionDispositivoBTLE(resultado, null);
            }

            /**
             * @brief Callback para resultados de escaneo en lote.
             *
             * Este método se llama cuando se detectan varios dispositivos al mismo tiempo.
             *
             * @param results Lista de resultados del escaneo, con la información de los dispositivos detectados.
             */
            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);
                Log.d(ETIQUETA_LOG, "buscarTodosLosDispositivosBTLE(): onBatchScanResults()");
            }

            /**
             * @brief Callback cuando el escaneo falla.
             *
             * Este método se llama si el escaneo de dispositivos BTLE falla por algún error.
             *
             * @param errorCode Código de error que indica el motivo de la falla.
             */
            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
                Log.d(ETIQUETA_LOG, "buscarTodosLosDispositivosBTLE(): onScanFailed()");
            }
        };

        // Configuración del modo de escaneo: baja latencia
        ScanSettings scanSettings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build();

        Log.d(ETIQUETA_LOG, "buscarTodosLosDispositivosBTLE(): empezamos a escanear todos los dispositivos");

        // Iniciar el escaneo de dispositivos BTLE
        this.elEscanner.startScan(null, scanSettings, this.callbackDelEscaneo);
    }



    // --------------------------------------------------------------
    // --------------------------------------------------------------
    /**
     * @brief Muestra la información de un dispositivo Bluetooth Low Energy (BTLE) detectado.
     *
     * Este método procesa los resultados obtenidos de un escaneo BTLE, extrayendo información del dispositivo
     * como el nombre, dirección, intensidad de señal (RSSI) y otros detalles específicos del protocolo iBeacon.
     * Además, actualiza o crea una vista para mostrar la información del dispositivo, y reinicia el temporizador
     * para gestionar la detección continua del dispositivo.
     *
     * @param resultado Resultado del escaneo BTLE que contiene la información del dispositivo detectado.
     * @param dispositivoBuscado UUID del dispositivo específico que se está buscando, si corresponde. Puede ser null.
     */
    private void mostrarInformacionDispositivoBTLE(ScanResult resultado, UUID dispositivoBuscado) {
        // Obtener el dispositivo detectado y su información
        BluetoothDevice bluetoothDevice = resultado.getDevice();
        byte[] bytes = resultado.getScanRecord().getBytes();
        int rssi = resultado.getRssi();
        TramaIBeacon tib = new TramaIBeacon(bytes);

        // Registrar información del dispositivo detectado en los logs
        Log.d(ETIQUETA_LOG, " ****************************************************");
        Log.d(ETIQUETA_LOG, " ****** DISPOSITIVO DETECTADO BTLE ****************** ");
        Log.d(ETIQUETA_LOG, " ****************************************************");
        Log.d(ETIQUETA_LOG, " nombre = " + bluetoothDevice.getName());
        Log.d(ETIQUETA_LOG, " toString = " + bluetoothDevice.toString());
        Log.d(ETIQUETA_LOG, " dirección = " + bluetoothDevice.getAddress());
        Log.d(ETIQUETA_LOG, " rssi = " + rssi);
        Log.d(ETIQUETA_LOG, " bytes = " + new String(bytes));
        Log.d(ETIQUETA_LOG, " bytes (" + bytes.length + ") = " + Utilidades.bytesToHexString(bytes));

        Log.d(ETIQUETA_LOG, " ----------------------------------------------------");
        Log.d(ETIQUETA_LOG, " prefijo  = " + Utilidades.bytesToHexString(tib.getPrefijo()));
        Log.d(ETIQUETA_LOG, "          advFlags = " + Utilidades.bytesToHexString(tib.getAdvFlags()));
        Log.d(ETIQUETA_LOG, "          advHeader = " + Utilidades.bytesToHexString(tib.getAdvHeader()));
        Log.d(ETIQUETA_LOG, "          companyID = " + Utilidades.bytesToHexString(tib.getCompanyID()));
        Log.d(ETIQUETA_LOG, "          iBeacon type = " + Integer.toHexString(tib.getiBeaconType()));
        Log.d(ETIQUETA_LOG, "          iBeacon length 0x = " + Integer.toHexString(tib.getiBeaconLength()) + " ( " + tib.getiBeaconLength() + " ) ");
        Log.d(ETIQUETA_LOG, " uuid  = " + Utilidades.bytesToHexString(tib.getUUID()));
        Log.d(ETIQUETA_LOG, " uuid  = " + Utilidades.bytesToString(tib.getUUID()));
        Log.d(ETIQUETA_LOG, " major  = " + Utilidades.bytesToHexString(tib.getMajor()) + "( " + Utilidades.bytesToInt(tib.getMajor()) + " ) ");
        Log.d(ETIQUETA_LOG, " minor  = " + Utilidades.bytesToHexString(tib.getMinor()) + "( " + Utilidades.bytesToInt(tib.getMinor()) + " ) ");
        Log.d(ETIQUETA_LOG, " txPower  = " + Integer.toHexString(tib.getTxPower()) + " ( " + tib.getTxPower() + " )");
        Log.d(ETIQUETA_LOG, " ****************************************************");

        String uuid = Utilidades.bytesToString(tib.getUUID());

        // Reiniciar o crear el temporizador para el dispositivo detectado
        int majorValue = Utilidades.bytesToInt(tib.getMajor());
        reiniciarTemporizador(uuid, majorValue, tib, dispositivoBuscado);

        int minorValue = Utilidades.bytesToInt(tib.getMinor());

        // Actualizar la interfaz de usuario con los valores de CO2 o temperatura según el beacon detectado
        runOnUiThread(() -> {
            if (procesarBeacon(majorValue, minorValue) == 1 && Utilidades.stringToUUID(Utilidades.bytesToString(tib.getUUID())).equals(dispositivoBuscado)) {
                beaconCO2Activo = true;
                dato1.setText("CO2: " + minorValue); // Mostrar el valor de CO2 (minor)
            } else if (procesarBeacon(majorValue, minorValue) == 2 && Utilidades.stringToUUID(Utilidades.bytesToString(tib.getUUID())).equals(dispositivoBuscado)) {
                beaconTemperaturaActivo = true;
                dato2.setText("ºC: " + minorValue); // Mostrar el valor de temperatura (minor)
            }
        });

        // Crear una clave única para cada combinación de UUID y tipo de sensor (majorValue)
        String claveVista = uuid + "_" + majorValue;

        // Verificar si el dispositivo ya tiene una vista asociada y actualizarla, o crear una nueva
        if (vistasDispositivosDetectados.containsKey(claveVista)) {
            // Actualizar vista existente
            View vistaExistente = vistasDispositivosDetectados.get(claveVista);
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
            // Crear nueva vista para el dispositivo detectado
            View nuevaVista = crearVistaDispositivo(bluetoothDevice, tib, dispositivoBuscado);
            vistasDispositivosDetectados.put(claveVista, nuevaVista);

            // Agregar la nueva vista al contenedor visual
            LinearLayout contenedor = findViewById(R.id.contenedorBeacons);
            contenedor.addView(nuevaVista);

            Log.d(ETIQUETA_LOG, "Dispositivo nuevo detectado. Añadiendo nueva vista.");
        }
    }


    /**
     * @brief Actualiza la vista de la interfaz de usuario con los valores de CO2 y temperatura.
     *
     * Este método se encarga de actualizar los `TextView` correspondientes en la interfaz de usuario
     * para mostrar los valores de CO2 y temperatura según el beacon detectado. También gestiona la
     * visibilidad de las imágenes asociadas, mostrando brevemente un indicador visual al recibir
     * nuevos datos.
     *
     * @param majorValue El valor 'major' del beacon detectado.
     * @param minorValue El valor 'minor' del beacon detectado, que representa los datos de CO2 o temperatura.
     */
    private void actualizarVistaCO2yTemperatura(int majorValue, int minorValue) {
        // Actualizar el TextView en la interfaz de usuario en el hilo principal
        runOnUiThread(() -> {
            if (procesarBeacon(majorValue, minorValue) == 1) {
                beaconCO2Activo = true; // El beacon de CO2 está activo
                dato1.setText("CO2: " + minorValue); // Mostrar el valor de CO2
                dato_image.setVisibility(View.GONE); // Ocultar la imagen anterior
                dato_image.setVisibility(View.VISIBLE); // Mostrar la imagen de CO2
                new Handler().postDelayed(() -> dato_image.setVisibility(View.GONE), 50); // Ocultar la imagen después de 50 ms
            } else if (procesarBeacon(majorValue, minorValue) == 2) {
                beaconTemperaturaActivo = true; // El beacon de temperatura está activo
                dato2.setText("ºC: " + minorValue); // Mostrar el valor de temperatura
                dato2_image.setVisibility(View.GONE); // Ocultar la imagen anterior
                dato2_image.setVisibility(View.VISIBLE); // Mostrar la imagen de temperatura
                new Handler().postDelayed(() -> dato2_image.setVisibility(View.GONE), 50); // Ocultar la imagen después de 50 ms
            }
        });
    }



    /**
     * @brief Crea una nueva vista para un dispositivo Bluetooth detectado.
     *
     * Este método infla un layout personalizado y configura los elementos de la vista
     * con la información del dispositivo Bluetooth, incluyendo su nombre, UUID,
     * y los valores 'major' y 'minor' extraídos del iBeacon.
     *
     * @param bluetoothDevice El objeto BluetoothDevice que representa el dispositivo detectado.
     * @param tib La instancia de TramaIBeacon que contiene la información del iBeacon.
     * @param dispositivoBuscado UUID del dispositivo específico que se está buscando, si corresponde.
     *
     * @return La vista configurada que muestra la información del dispositivo.
     */
    private View crearVistaDispositivo(BluetoothDevice bluetoothDevice, TramaIBeacon tib, UUID dispositivoBuscado) {
        // Inflar un layout personalizado para la vista del dispositivo
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.vista_dispositivo, null);

        // Configurar la vista con la información del dispositivo
        TextView nombreTextView = view.findViewById(R.id.nombreTextView);
        TextView uuidTextView = view.findViewById(R.id.uuidTextView);
        TextView majorTextView = view.findViewById(R.id.majorTextView);
        TextView minorTextView = view.findViewById(R.id.minorTextView);

        nombreTextView.setText("Nombre: " + bluetoothDevice.getName());
        uuidTextView.setText("UUID: " + Utilidades.bytesToHexString(tib.getUUID()));
        majorTextView.setText("Major: " + Utilidades.bytesToInt(tib.getMajor()));
        minorTextView.setText("Minor: " + Utilidades.bytesToInt(tib.getMinor()));

        return view; // Retornar la vista configurada
    }


    /**
     * @brief Reinicia el temporizador para un dispositivo detectado.
     *
     * Este método gestiona el temporizador que controla el tiempo de desconexión de
     * los dispositivos de tipo CO2 y temperatura. Si ya existe un temporizador para
     * el dispositivo, lo cancela y crea uno nuevo. El temporizador cuenta regresivamente
     * desde 10 segundos y actualiza la interfaz de usuario con el tiempo restante.
     *
     * Si el tiempo se agota, se llama al método para eliminar la vista del dispositivo
     * correspondiente.
     *
     * @param uuid El UUID del dispositivo cuyo temporizador se va a reiniciar.
     * @param majorValue El valor 'major' del beacon detectado.
     * @param tib La instancia de TramaIBeacon que contiene la información del iBeacon.
     * @param dispositivoBuscado UUID del dispositivo específico que se está buscando, si corresponde.
     */
    private void reiniciarTemporizador(final String uuid, final int majorValue, TramaIBeacon tib, UUID dispositivoBuscado) {
        // Crear una clave única basada en el UUID y el tipo de sensor (majorValue)
        String claveVista = uuid + "_" + majorValue;

        // Verificar si la vista ya existe en el mapa
        if (!vistasDispositivosDetectados.containsKey(claveVista)) {
            Log.e(ETIQUETA_LOG, "Error: No se encontró la vista para el dispositivo con UUID: " + uuid + " y major: " + majorValue);
            return;  // Detener ejecución si no existe la vista
        }

        // Obtener la vista correspondiente al dispositivo
        View vistaDispositivo = vistasDispositivosDetectados.get(claveVista);

        // Asegurarse de que la vista no sea nula
        if (vistaDispositivo == null) {
            Log.e(ETIQUETA_LOG, "Error: La vista del dispositivo es nula para el UUID: " + uuid + " y major: " + majorValue);
            return;  // Detener ejecución si la vista es nula
        }

        // Crear un nuevo temporizador
        Handler nuevoTemporizador = new Handler();
        final long tiempoTotal = 10000; // 10 segundos
        final long intervalo = 1000;   // 1 segundo

        // Inicializar variables de tiempo restante para CO2 y Temperatura de forma independiente
        final TextView cuentaAtrasCO2 = findViewById(R.id.cuentaAtrasCO2);
        final TextView cuentaAtrasTemperatura = findViewById(R.id.cuentaAtrasTemperatura);

        TextView cuentaAtrasTextView = vistaDispositivo.findViewById(R.id.cuentaAtrasTextView);

        Runnable actualizarCuentaAtras = new Runnable() {
            long tiempoRestanteCO2 = tiempoTotal;
            long tiempoRestanteTemperatura = tiempoTotal;
            long tiempoRestante = tiempoTotal;

            @Override
            public void run() {
                // Obtener el tipo de beacon (CO2 o Temperatura) basado en el majorValue
                int tipoBeacon = procesarBeacon(Utilidades.bytesToInt(tib.getMajor()), Utilidades.bytesToInt(tib.getMinor()));

                if (tipoBeacon == 1) { // CO2
                    long segundosRestantesCO2 = tiempoRestanteCO2 / 1000;
                    if (tiempoRestanteCO2 >= 0) {
                        cuentaAtrasCO2.setText("Desconectando CO2 en: " + segundosRestantesCO2 + "s...");
                        tiempoRestanteCO2 -= intervalo;
                    } else {
                        eliminarVistaDispositivo(uuid, majorValue, tib, dispositivoBuscado);
                        return; // Detener ejecución cuando el tiempo de CO2 llegue a 0
                    }
                } else if (tipoBeacon == 2) { // Temperatura
                    long segundosRestantesTemperatura = tiempoRestanteTemperatura / 1000;
                    if (tiempoRestanteTemperatura >= 0) {
                        cuentaAtrasTemperatura.setText("Desconectando Temperatura en: " + segundosRestantesTemperatura + "s...");
                        tiempoRestanteTemperatura -= intervalo;
                    } else {
                        eliminarVistaDispositivo(uuid, majorValue, tib, dispositivoBuscado);
                        return; // Detener ejecución cuando el tiempo de Temperatura llegue a 0
                    }
                } else {
                    if (tiempoRestante >= 0) {
                        long segundosRestantes = tiempoRestante / 1000;
                        cuentaAtrasTextView.setText("Desconectando en: " + segundosRestantes + "s...");
                        tiempoRestante -= intervalo;
                    } else {
                        eliminarVistaDispositivo(uuid, majorValue, tib, dispositivoBuscado);
                    }
                }
                nuevoTemporizador.postDelayed(this, intervalo); // Repetir el runnable cada segundo
            }
        };

        // Iniciar la cuenta atrás
        nuevoTemporizador.post(actualizarCuentaAtras);

        // Almacenar el nuevo temporizador con la clave basada en UUID y tipo de sensor
        temporizadoresDispositivos.put(claveVista, nuevoTemporizador);
    }

    /**
     * @brief Elimina la vista de un dispositivo detectado de la interfaz de usuario.
     *
     * Este método verifica si existe una vista para el dispositivo especificado por su UUID.
     * Si es así, actualiza la interfaz de usuario para reflejar que el dispositivo ha dejado de
     * ser detectado (ya sea un beacon de CO2 o de temperatura), elimina la vista del contenedor
     * visual, y también remueve el dispositivo de los mapas de vistas y temporizadores.
     *
     * @param uuid El UUID del dispositivo cuya vista se va a eliminar.
     * @param majorValue El valor 'major' del beacon detectado.
     * @param tib La instancia de TramaIBeacon que contiene la información del iBeacon.
     * @param dispositivoBuscado UUID del dispositivo específico que se está buscando, si corresponde.
     */
    private void eliminarVistaDispositivo(String uuid, int majorValue, TramaIBeacon tib, UUID dispositivoBuscado) {
        // Crear una clave única basada en el UUID y el tipo de sensor (majorValue)
        String claveVista = uuid + "_" + majorValue;

        // Verifica si hay una vista existente para el dispositivo y tipo de sensor
        if (vistasDispositivosDetectados.containsKey(claveVista)) {
            View vistaAEliminar = vistasDispositivosDetectados.get(claveVista);

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
            vistasDispositivosDetectados.remove(claveVista);
            temporizadoresDispositivos.remove(claveVista);

            Log.d(ETIQUETA_LOG, "Dispositivo eliminado por inactividad: " + claveVista);
        } else {
            Log.d(ETIQUETA_LOG, "No se encontró vista para eliminar con clave: " + claveVista);
        }
    }

    /**
     * @brief Procesa la información del beacon detectado.
     *
     * Este método determina el tipo de medición a partir del valor 'major' del beacon
     * y registra el valor 'minor'. Los tipos de medición reconocidos son CO2 y
     * Temperatura, identificados por sus respectivos códigos.
     *
     * @param major El valor 'major' del beacon, que contiene el tipo de medición y un contador.
     * @param minor El valor 'minor' del beacon, que representa el dato medido (por ejemplo, CO2 o temperatura).
     * @return Un entero que representa el tipo de medición:
     *         - 1 si el dato es de CO2
     *         - 2 si el dato es de temperatura
     *         - 0 si el tipo de dato no es reconocido.
     */
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
        return 0; // Retornar 0 si el tipo de dato no es reconocido
    }


    /**
     * @brief Busca un dispositivo BLE específico por su UUID.
     *
     * Este método inicia un escaneo para buscar un dispositivo Bluetooth Low Energy
     * (BLE) que coincida con el UUID proporcionado. Si se encuentra el dispositivo,
     * se actualiza la interfaz de usuario y se envían los datos del sensor al servidor.
     *
     * @param dispositivoBuscado El UUID del dispositivo que se desea encontrar.
     */
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

                // Comprobar si se ha encontrado el dispositivo buscado
                if (uuid.equals(dispositivoBuscado)) {
                    Log.d(ETIQUETA_LOG, "Dispositivo encontrado: " + uuid);
                    mostrarInformacionDispositivoBTLE(resultado, dispositivoBuscado);

                    int majorValue = Utilidades.bytesToInt(tib.getMajor());
                    int minorValue = Utilidades.bytesToInt(tib.getMinor());

                    // Llamar al método para actualizar la interfaz de usuario
                    actualizarVistaCO2yTemperatura(majorValue, minorValue);

                    // Identificar el tipo de sensor
                    if (procesarBeacon(majorValue, minorValue) == 1) {
                        sensor = "CO2";
                    } else if (procesarBeacon(majorValue, minorValue) == 2) {
                        sensor = "temperature";
                    }

                    SensorData sensorData = new SensorData(sensor, minorValue, 1);

                    Call<Void> call = api.createSensorData(sensorData); // Instanciar call dentro del try

                    // Enviar datos del sensor al servidor
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(MainActivity.this, "Medición insertada con éxito en el servidor", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(MainActivity.this, "Error al insertar medición en el servidor", Toast.LENGTH_SHORT).show();
                        }
                    });
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


    /**
     * @brief Maneja el evento de pulsación del botón para buscar un dispositivo BLE específico.
     *
     * Este método se invoca cuando se pulsa el botón correspondiente en la interfaz de usuario.
     * Cambia la visibilidad de una imagen y llama al método para buscar un dispositivo BLE
     * con un UUID específico.
     *
     * @param v La vista que ha sido pulsada (el botón).
     */
    public void botonBuscarNuestroDispositivoBTLEPulsado(View v) {
        Log.d(ETIQUETA_LOG, "boton nuestro dispositivo BTLE Pulsado");

        // Mostrar la imagen indicando que la búsqueda está en curso
        runOnUiThread(() -> image4.setVisibility(View.VISIBLE));

        // Llamar al método para buscar el dispositivo BTLE con el UUID especificado
        this.buscarEsteDispositivoBTLE(Utilidades.stringToUUID(nuevoUuid));
    }


    /**
     * @brief Detiene la búsqueda de dispositivos BLE.
     *
     * Este método cancela el escaneo activo de dispositivos BLE si existe
     * un callback de escaneo registrado. También actualiza la interfaz
     * de usuario para reflejar que no hay dispositivos detectados.
     */
    private void detenerBusquedaDispositivosBTLE() {
        // Verificar si no hay un callback de escaneo activo
        if (this.callbackDelEscaneo == null) {
            return;
        }

        // Detener el escaneo y limpiar el callback
        this.elEscanner.stopScan(this.callbackDelEscaneo);
        this.callbackDelEscaneo = null;

        // Actualizar los TextView para mostrar "Off" y ocultar las imágenes
        runOnUiThread(() -> {
            image3.setVisibility(View.GONE);
            image4.setVisibility(View.GONE);
            dato1.setText("CO2: Off");
            dato2.setText("ºC: Off");
        });
    }


    /**
     * @brief Maneja el evento de pulsación del botón para buscar todos los dispositivos BLE.
     *
     * Este método se invoca cuando se pulsa el botón correspondiente en la interfaz de usuario.
     * Cambia la visibilidad de una imagen para indicar que la búsqueda está en curso
     * y llama al método que inicia la búsqueda de todos los dispositivos BLE disponibles.
     *
     * @param v La vista que ha sido pulsada (el botón).
     */
    public void botonBuscarDispositivosBTLEPulsado(View v) {
        Log.d(ETIQUETA_LOG, "boton buscar dispositivos BTLE Pulsado");

        // Mostrar la imagen indicando que la búsqueda está en curso
        runOnUiThread(() -> image3.setVisibility(View.VISIBLE));

        // Llamar al método para buscar todos los dispositivos BLE disponibles
        this.buscarTodosLosDispositivosBTLE();
    }


    /**
     * @brief Maneja el evento de pulsación del botón para detener la búsqueda de dispositivos BLE.
     *
     * Este método se invoca cuando se pulsa el botón correspondiente en la interfaz de usuario.
     * Registra en el log que se ha pulsado el botón y llama al método que detiene la búsqueda
     * activa de dispositivos BLE.
     *
     * @param v La vista que ha sido pulsada (el botón).
     */
    public void botonDetenerBusquedaDispositivosBTLEPulsado(View v) {
        Log.d(ETIQUETA_LOG, "boton detener busqueda dispositivos BTLE Pulsado");

        // Llamar al método para detener la búsqueda de dispositivos BLE
        this.detenerBusquedaDispositivosBTLE();
    }


    /**
     * @brief Inicializa el adaptador Bluetooth y solicita permisos necesarios.
     *
     * Este método se encarga de obtener el adaptador Bluetooth del dispositivo,
     * habilitarlo si es necesario, y obtener el escáner BLE. También verifica y
     * solicita los permisos necesarios según la versión de Android en uso.
     *
     * - Para Android 12 (S) y versiones posteriores, se requieren permisos
     *   específicos para el escaneo y conexión Bluetooth.
     * - Para Android 11 (R) y versiones anteriores, se requieren permisos de
     *   Bluetooth y localización.
     */
    private void inicializarBlueTooth() {
        Log.d(ETIQUETA_LOG, "inicializarBlueTooth(): obtenemos adaptador BT ");

        BluetoothAdapter bta = BluetoothAdapter.getDefaultAdapter();

        Log.d(ETIQUETA_LOG, "inicializarBlueTooth(): habilitamos adaptador BT ");

        bta.enable();

        Log.d(ETIQUETA_LOG, "inicializarBlueTooth(): habilitado =  " + bta.isEnabled());

        Log.d(ETIQUETA_LOG, "inicializarBlueTooth(): estado =  " + bta.getState());

        Log.d(ETIQUETA_LOG, "inicializarBlueTooth(): obtenemos escaner btle ");

        this.elEscanner = bta.getBluetoothLeScanner();

        if (this.elEscanner == null) {
            Log.d(ETIQUETA_LOG, "inicializarBlueTooth(): Socorro: NO hemos obtenido escaner btle  !!!!");
        }

        Log.d(ETIQUETA_LOG, "inicializarBlueTooth(): voy a pedir permisos (si no los tuviera) !!!!");

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
                Log.d(ETIQUETA_LOG, "inicializarBlueTooth(): parece que YA tengo los permisos necesarios en Android 12+ !!!!");
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
                Log.d(ETIQUETA_LOG, "inicializarBlueTooth(): parece que YA tengo los permisos necesarios !!!!");
            }
        }
    } // ()

    /**
     * @brief Maneja el resultado de las solicitudes de permisos.
     *
     * Este método es llamado cuando el usuario responde a una solicitud de permisos.
     * Verifica si los permisos solicitados han sido concedidos y registra el resultado.
     *
     * @param requestCode El código de la solicitud de permisos.
     * @param permissions Un arreglo de permisos solicitados.
     * @param grantResults Un arreglo de resultados correspondientes a cada permiso.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case CODIGO_PETICION_PERMISOS:
                // Si la solicitud es cancelada, los arreglos de resultado están vacíos.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(ETIQUETA_LOG, "onRequestPermissionResult(): permisos concedidos  !!!!");
                    // El permiso ha sido concedido. Continuar con la acción o flujo en la app.
                } else {
                    Log.d(ETIQUETA_LOG, "onRequestPermissionResult(): Socorro: permisos NO concedidos  !!!!");
                }
                return;
        }
        // Otras líneas 'case' para verificar otros permisos que esta app podría solicitar.
    } // ()

} // class
