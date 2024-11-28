package com.example.usuario_upv.proyecto3a;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Tab2 extends Fragment {

    // --------------------------------------------------------------
    // Constantes
    // --------------------------------------------------------------
    /**
     * @brief Etiqueta utilizada para el logging.
     */
    static final String ETIQUETA_LOG = ">>>>";

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
     * @brief EditText donde el usuario ingresa la IP para la conexión.
     */
    private EditText ipInput;

    /**
     * @brief EditText donde el usuario ingresa la UUID para detectar el beacon deseado.
     */
    private EditText uuidDeseado;

    /**
     * @brief String de puerto e ip por defecto (localhost).
     */
    private String ip = "0.0.0.0";

    // --------------------------------------------------------------
    // Estados de los beacons
    // --------------------------------------------------------------
    /**
     * @brief Estado que indica si el beacon de ozono está activo.
     */
    private boolean beaconozonoActivo = false;

    /**
     * @brief Estado que indica si el beacon de temperatura está activo.
     */
    private boolean beaconTemperaturaActivo = false;

    /**
     * @brief Instancia de la API que maneja la comunicación con los sensores.
     */
    LogicaFake api;

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

    LinearLayout contenedor;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab2, container, false);

        Log.d(ETIQUETA_LOG, "onCreate(): empieza");

        inicializarBlueTooth();

        api = RetrofitClient.getClient(Config.BASE_URL).create(LogicaFake.class);

        dato1 = rootView.findViewById(R.id.valor1);
        dato2 = rootView.findViewById(R.id.valor2);
        dato_image = rootView.findViewById(R.id.valor1_image);
        dato2_image = rootView.findViewById(R.id.valor2_image);
        image3 = rootView.findViewById(R.id.image_buscarDispositivos);
        image4 = rootView.findViewById(R.id.image_buscarNuestro);
        ipInput = rootView.findViewById(R.id.serverIP);
        uuidDeseado = rootView.findViewById(R.id.elegirUuid);
        contenedor = rootView.findViewById(R.id.contenedorBeacons);

        // Botones
        Button submitIPButton = rootView.findViewById(R.id.submitIP2);
        Button submitUUIDButton = rootView.findViewById(R.id.actualizarUuid);
        Button SearchAllButton = rootView.findViewById(R.id.botonBuscarDispositivosBTLE);
        Button SearchOurButton = rootView.findViewById(R.id.botonBuscarNuestroDispositivoBTLE);
        Button StopButton = rootView.findViewById(R.id.botonDetenerBusquedaDispositivosBTLE);

        // Configurar los listeners de clic para los botones
        submitIPButton.setOnClickListener(this::actualizarIP);
        submitUUIDButton.setOnClickListener(this::actualizarUuid);
        SearchAllButton.setOnClickListener(this::botonBuscarDispositivosBTLEPulsado);
        SearchOurButton.setOnClickListener(this::botonBuscarNuestrosDispositivosBTLEPulsado);
        StopButton.setOnClickListener(this::botonDetenerBusquedaDispositivosBTLEPulsado);

        Log.d(ETIQUETA_LOG, "onCreate(): termina");

        return rootView;
    }

    // Método para iniciar el servicio BLE
    private void startBLEService() {
        Intent serviceIntent = new Intent(getContext(), BLEService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (requireContext().checkSelfPermission(Manifest.permission.FOREGROUND_SERVICE_DATA_SYNC)
                    != PackageManager.PERMISSION_GRANTED) {
                // Request the permission
                requestPermissions(new String[]{
                        Manifest.permission.FOREGROUND_SERVICE_DATA_SYNC
                }, CODIGO_PETICION_PERMISOS);
                return;
            }
            requireActivity().startForegroundService(serviceIntent);
        } else {
            requireActivity().startService(serviceIntent);
        }
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
            Toast.makeText(getActivity(), "Please enter a valid IP", Toast.LENGTH_SHORT).show();
            return;
        }

        // Configurar la URL base para Retrofit
        String baseUrl = "http://" + ip + ":13000/";
        Config.BASE_URL = baseUrl;
        api = RetrofitClient.getClient(baseUrl).create(LogicaFake.class);


        // Mostrar un mensaje con la IP configurada
        Toast.makeText(getActivity(), "Server IP set to: " + baseUrl, Toast.LENGTH_SHORT).show();

        // Verificar la conexión con el servidor
        checkConnection();
    }

    public void actualizarUuid(View view) {

        // Validar que la IP no esté vacía
        if (uuidDeseado.getText().toString().trim().isEmpty()) {
            Toast.makeText(getActivity(), "Please enter a valid UUID", Toast.LENGTH_SHORT).show();
        }

        Config.addUUID(uuidDeseado.getText().toString().trim());

        Toast.makeText(getActivity(), "UUID actualizado", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getActivity(), "Connected to server", Toast.LENGTH_SHORT).show();
                    // Aquí puedes realizar otras operaciones como insertar datos o usuarios
                } else {
                    Toast.makeText(getActivity(), "Failed to connect to server", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getActivity(), "Connection error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
        try {
            this.elEscanner.startScan(null, scanSettings, this.callbackDelEscaneo);
        }catch (Exception e){
            Toast.makeText(getActivity(), "Por favor acepta los permisos de bletooth y actívalo", Toast.LENGTH_SHORT).show();
            Log.d(ETIQUETA_LOG, "Error de bletooth: " + e);
        }
    }



    // --------------------------------------------------------------
    // --------------------------------------------------------------
    /**
     * @brief Muestra la información de un dispositivo Bluetooth Low Energy (BTLE) detectado.
     *
     * Este método procesa los resultados obtenidos de un escaneo BTLE, extrayendo información del dispositivo
     * como el nombre, dirección, intensidad de señal (RSSI) y otros detalles específicos del protocolo iBeacon.
     * Además, actualiza o crea una vista para mostrar la información del dispositivo.
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

        int majorValue = Utilidades.bytesToInt(tib.getMajor());
        int minorValue = Utilidades.bytesToInt(tib.getMinor());

        // Actualizar la interfaz de usuario con los valores de ozono o temperatura según el beacon detectado
        getActivity().runOnUiThread(() -> {
            if (procesarBeacon(majorValue, minorValue) == 1 && Utilidades.stringToUUID(Utilidades.bytesToString(tib.getUUID())).equals(dispositivoBuscado)) {
                beaconozonoActivo = true;
                dato1.setText("Ozono: " + minorValue); // Mostrar el valor de ozono (minor)
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
            contenedor.addView(nuevaVista);

            Log.d(ETIQUETA_LOG, "Dispositivo nuevo detectado. Añadiendo nueva vista.");
        }
    }


    /**
     * @brief Actualiza la vista de la interfaz de usuario con los valores de ozono y temperatura.
     *
     * Este método se encarga de actualizar los `TextView` correspondientes en la interfaz de usuario
     * para mostrar los valores de ozono y temperatura según el beacon detectado. También gestiona la
     * visibilidad de las imágenes asociadas, mostrando brevemente un indicador visual al recibir
     * nuevos datos.
     *
     * @param majorValue El valor 'major' del beacon detectado.
     * @param minorValue El valor 'minor' del beacon detectado, que representa los datos de ozono o temperatura.
     */
    private void actualizarVistaozonoyTemperatura(int majorValue, int minorValue) {
        // Actualizar el TextView en la interfaz de usuario en el hilo principal
        getActivity().runOnUiThread(() -> {
            if (procesarBeacon(majorValue, minorValue) == 1) {
                beaconozonoActivo = true; // El beacon de ozono está activo
                dato1.setText("Ozono: " + minorValue); // Mostrar el valor de ozono
                dato_image.setVisibility(View.GONE); // Ocultar la imagen anterior
                dato_image.setVisibility(View.VISIBLE); // Mostrar la imagen de ozono
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
        LayoutInflater inflater = LayoutInflater.from(getContext());
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
     * @brief Procesa la información del beacon detectado.
     *
     * Este método determina el tipo de medición a partir del valor 'major' del beacon
     * y registra el valor 'minor'. Los tipos de medición reconocidos son ozono y
     * Temperatura, identificados por sus respectivos códigos.
     *
     * @param major El valor 'major' del beacon, que contiene el tipo de medición y un contador.
     * @param minor El valor 'minor' del beacon, que representa el dato medido (por ejemplo, ozono o temperatura).
     * @return Un entero que representa el tipo de medición:
     *         - 1 si el dato es de ozono
     *         - 2 si el dato es de temperatura
     *         - 0 si el tipo de dato no es reconocido.
     */
    public int procesarBeacon(int major, int minor) {
        int tipoMedicion = major >> 8;  // Obtener el identificador de la medición (ozono o Temperatura)
        int contador = major & 0xFF;    // Obtener el contador (opcional si es útil para tu lógica)

        switch (tipoMedicion) {
            case 11:  // ozono
                Log.d("Beacon", "Dato de ozono detectado. Valor: " + minor + ", Contador: " + contador);
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
     * @brief Busca dispositivos BLE específicos por sus UUIDs.
     *
     * Este método inicia un escaneo para buscar dispositivos Bluetooth Low Energy
     * (BLE) que coincidan con los UUIDs proporcionados. Si se encuentran dispositivos,
     * se actualiza la interfaz de usuario y se envían los datos del sensor al servidor.
     *
     * @param dispositivosBuscados Un arreglo de UUIDs de los dispositivos que se desea encontrar.
     */
    private void buscarEstosDispositivosBTLE(final ArrayList<String> dispositivosBuscados) {
        Log.d(ETIQUETA_LOG, "buscarEstosDispositivosBTLE(): empieza");

        Log.d(ETIQUETA_LOG, "buscarEstosDispositivosBTLE(): instalamos scan callback");

        this.callbackDelEscaneo = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult resultado) {
                super.onScanResult(callbackType, resultado);
                Log.d(ETIQUETA_LOG, "buscarEstosDispositivosBTLE(): onScanResult()");

                byte[] bytes = resultado.getScanRecord().getBytes();
                TramaIBeacon tib = new TramaIBeacon(bytes);
                UUID uuid = Utilidades.stringToUUID(Utilidades.bytesToString(tib.getUUID()));

                // Comprobar si se ha encontrado alguno de los dispositivos buscados
                for (String uuidStr : dispositivosBuscados) {
                    if (Utilidades.uuidToString(uuid).equals(uuidStr)) {
                        Log.d(ETIQUETA_LOG, "Dispositivo encontrado: " + uuid);
                        mostrarInformacionDispositivoBTLE(resultado, Utilidades.stringToUUID(uuidStr));

                        int majorValue = Utilidades.bytesToInt(tib.getMajor());
                        int minorValue = Utilidades.bytesToInt(tib.getMinor());

                        // Llamar al método para actualizar la interfaz de usuario
                        actualizarVistaozonoyTemperatura(majorValue, minorValue);

                        // Iniciar el servicio BLE
                        Intent serviceIntent = new Intent(getContext(), BLEService.class);
                        getContext().startService(serviceIntent);

                        serviceIntent.putExtra("majorValue", majorValue);
                        serviceIntent.putExtra("minorValue", minorValue);
                        getContext().startService(serviceIntent);

                        Point location = new Point(1, 2);
                        int sensorTipo = procesarBeacon(majorValue, minorValue);
                        SensorData sensorData = new SensorData(Utilidades.uuidToString(uuid), minorValue, sensorTipo, location);

                        Call<Void> call = api.createSensorData(sensorData);
                        Log.d(ETIQUETA_LOG, "Enviando datos...");

                        // Enviar datos del sensor al servidor
                        call.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                Log.d(ETIQUETA_LOG, "onResponse llamado");
                                if (response.isSuccessful()) {
                                    Toast.makeText(getActivity(), "Medición insertada con éxito en el servidor", Toast.LENGTH_SHORT).show();
                                    Log.d(ETIQUETA_LOG, "Medición insertada con éxito en el servidor");
                                } else {
                                    Log.d(ETIQUETA_LOG, "Respuesta no exitosa: " + response.code());
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Log.d(ETIQUETA_LOG, "onFailure llamado");
                                Log.e(ETIQUETA_LOG, "Error detallado: ", t);
                                Toast.makeText(getActivity(), "Error al insertar medición en el servidor", Toast.LENGTH_SHORT).show();
                                Log.d(ETIQUETA_LOG, "Error al insertar medición en el servidor");
                            }
                        });
                    } else {
                        Log.d(ETIQUETA_LOG, "Dispositivo no objetivo encontrado: " + uuid);
                    }
                }
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);
                Log.d(ETIQUETA_LOG, "buscarEstosDispositivosBTLE(): onBatchScanResults()");
            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
                Log.d(ETIQUETA_LOG, "buscarEstosDispositivosBTLE(): onScanFailed()");
            }
        };

        // Opciones de escaneo (opcional, para configuración avanzada)
        ScanSettings scanSettings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)  // Modo de escaneo rápido
                .build();


        // Iniciar el escaneo sin filtros
        try {
            this.elEscanner.startScan(null, scanSettings, this.callbackDelEscaneo);
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Por favor acepta los permisos de Bluetooth y actívalo", Toast.LENGTH_SHORT).show();
            Log.d(ETIQUETA_LOG, "Error de Bluetooth: " + e);
        }
    }

    /**
     * @brief Maneja el evento de pulsación del botón para buscar dispositivos BLE específicos.
     *
     * Este método se invoca cuando se pulsa el botón correspondiente en la interfaz de usuario.
     * Cambia la visibilidad de una imagen y llama al método para buscar dispositivos BLE
     * con los UUIDs especificados.
     *
     * @param v La vista que ha sido pulsada (el botón).
     */
    public void botonBuscarNuestrosDispositivosBTLEPulsado(View v) {
        Log.d(ETIQUETA_LOG, "boton nuestros dispositivos BTLE Pulsado");

        // Mostrar la imagen indicando que la búsqueda está en curso
        getActivity().runOnUiThread(() -> image4.setVisibility(View.VISIBLE));

        try {
            // AQUÍ VA LO DE EN SEGUNDO PLANO
            // --------------------------------------------------------------------
            Intent serviceIntent = new Intent(getContext(), BLEService.class);
            requireActivity().startService(serviceIntent);
            // --------------------------------------------------------------------
            // AQUÍ VA LO DE EN SEGUNDO PLANO
            if (Config.UUIDs != null){

                // Llamar al método para buscar los dispositivos BTLE con los UUID especificados
                this.buscarEstosDispositivosBTLE(Config.UUIDs);
            }else {
                getActivity().runOnUiThread(() -> image4.setVisibility(View.GONE));
                Log.d(ETIQUETA_LOG, "No tienes sensores que detectar");
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Por favor, activa el Bluetooth", Toast.LENGTH_SHORT).show();
        }
    }




    /**
     * @brief Detiene la búsqueda de dispositivos Bluetooth Low Energy (BTLE).
     *
     * Este método se encarga de detener el escaneo de dispositivos BTLE y eliminar todas las vistas
     * asociadas a los dispositivos detectados. También actualiza la interfaz de usuario para indicar
     * que los sensores están apagados.
     *
     * - Detiene el escaneo si hay un callback activo.
     * - Elimina todas las vistas del contenedor visual.
     * - Actualiza los TextView para mostrar "Off" y oculta las imágenes asociadas a los sensores.
     */
    private void detenerBusquedaDispositivosBTLE() {
        // Verificar si no hay un callback de escaneo activo
        if (this.callbackDelEscaneo == null) {
            return;
        }

        try {
            this.elEscanner.stopScan(this.callbackDelEscaneo);
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Por favor acepta los permisos de Bluetooth y actívalo", Toast.LENGTH_SHORT).show();
            Log.d(ETIQUETA_LOG, "Error de Bluetooth: " + e);
        }

        // Detener el escaneo y limpiar el callback
        this.callbackDelEscaneo = null;

        // Eliminar todas las vistas de dispositivos detectados
        getActivity().runOnUiThread(() -> {
            contenedor.removeAllViews();  // Elimina todas las vistas del contenedor

            vistasDispositivosDetectados.clear();

            // Actualizar los TextView para mostrar "Off" y ocultar las imágenes
            image3.setVisibility(View.GONE);
            image4.setVisibility(View.GONE);
            dato1.setText("Ozono: Off");
            dato2.setText("ºC: Off");
        });

        // Detener el servicio BLEService
        Intent serviceIntent = new Intent(getActivity(), BLEService.class);
        getActivity().stopService(serviceIntent);
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
        getActivity().runOnUiThread(() -> image3.setVisibility(View.VISIBLE));

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
     * - Para Android 13 (T) y versiones posteriores, se requieren permisos
     *   específicos para el escaneo y conexión Bluetooth, así como notificaciones si son necesarias.
     * - Para Android 12 (S) se requieren permisos de escaneo y conexión Bluetooth.
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Para Android 13 y versiones posteriores
            if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        getActivity(),
                        new String[]{android.Manifest.permission.BLUETOOTH_SCAN, android.Manifest.permission.BLUETOOTH_CONNECT, android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.POST_NOTIFICATIONS},
                        CODIGO_PETICION_PERMISOS
                );
                startBLEService();
            } else {
                Log.d(ETIQUETA_LOG, "inicializarBlueTooth(): YA tengo los permisos necesarios en Android 13+.");
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Para Android 12
            if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        getActivity(),
                        new String[]{android.Manifest.permission.BLUETOOTH_SCAN, android.Manifest.permission.BLUETOOTH_CONNECT, android.Manifest.permission.ACCESS_FINE_LOCATION},
                        CODIGO_PETICION_PERMISOS
                );
                startBLEService();
            } else {
                Log.d(ETIQUETA_LOG, "inicializarBlueTooth(): YA tengo los permisos necesarios en Android 12.");
            }
        } else {
            // Para Android 11 y versiones anteriores
            if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        getActivity(),
                        new String[]{android.Manifest.permission.BLUETOOTH, android.Manifest.permission.BLUETOOTH_ADMIN, android.Manifest.permission.ACCESS_FINE_LOCATION},
                        CODIGO_PETICION_PERMISOS
                );
            } else {
                Log.d(ETIQUETA_LOG, "inicializarBlueTooth(): YA tengo los permisos necesarios.");
            }
        }
    }



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

        startBLEService();

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
        }
    } // ()
}




