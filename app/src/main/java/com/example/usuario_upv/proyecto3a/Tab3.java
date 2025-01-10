package com.example.usuario_upv.proyecto3a;

import android.app.Notification;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.PointF;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.List;
import java.util.UUID;


import android.Manifest;
import android.content.pm.PackageManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Tab3 extends Fragment {
    private static final String ETIQUETA_LOG = "Tab3";
    private static final int CAMERA_PERMISSION_REQUEST = 100;
    private ScanCallback callbackDelEscaneo = null;
    private BluetoothLeScanner elEscanner;
    private static final int CODIGO_PETICION_PERMISOS = 11223344;
    private LogicaFake api;
    String userEmail;
    private TextView sensorNameTop, sensorName, temperatura, ozono;
    private boolean beaconozonoActivo;
    private boolean beaconTemperaturaActivo;
    private ImageView dato_image;
    private ImageView dato2_image;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab3, container, false);
        api = RetrofitClient.getClient(Config.BASE_URL).create(LogicaFake.class);

        inicializarBlueTooth();

        // Recuperar el email del usuario desde las preferencias compartidas
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyAppPrefs", AppCompatActivity.MODE_PRIVATE);
        userEmail = sharedPreferences.getString("userEmail", null);

        if (userEmail == null || userEmail.isEmpty()) {
            Toast.makeText(getContext(), "No se pudo obtener el email del usuario", Toast.LENGTH_SHORT).show();
            Log.d(ETIQUETA_LOG, "El email del usuario no está configurado en las preferencias.");
            return view;
        }

        // Inicializar vistas
        sensorNameTop = view.findViewById(R.id.sensorNameTop);
        sensorName = view.findViewById(R.id.sensorName);
        temperatura = view.findViewById(R.id.textView7); // TextView de temperatura
        ozono = view.findViewById(R.id.textView13); // TextView de ozono
        dato2_image = view.findViewById(R.id.imageView7); // ImageView de temperatura
        dato_image = view.findViewById(R.id.imageView8); // ImageView de ozono

        // Configuración de QR Scanner y permisos
        FloatingActionButton fab = view.findViewById(R.id.floatingActionButton);
        fab.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
        fab.setOnClickListener(v -> {
            if (checkCameraPermission()) {
                startQRScanner();
            }
        });

        // Obtener sensores al cargar el fragmento
        searchData();

        return view;
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


    private void searchData() {
        // Obtener los sensores del usuario desde el servidor
        Call<ResponseBody> call = api.getUserSensors(userEmail);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseData = response.body().string();
                        Log.d(ETIQUETA_LOG, "Sensores obtenidos: " + responseData);

                        JSONArray jsonArray = new JSONArray(responseData);

                        if (jsonArray.length() > 0) {
                            // Usamos el primer sensor asignado al usuario
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            String sensorUUID = jsonObject.getString("uuid");

                            Log.d(ETIQUETA_LOG, "Sensor asignado al usuario: " + sensorUUID);

                            // Iniciar el escaneo con el UUID del sensor
                            iniciarEscaneo(sensorUUID);
                        } else {
                            Log.d(ETIQUETA_LOG, "No hay sensores asignados al usuario.");
                            Toast.makeText(getContext(), "No se encontraron sensores asignados", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                        Log.d(ETIQUETA_LOG, "Error al procesar la respuesta: " + e.getMessage());
                    }
                } else {
                    Toast.makeText(getContext(), "Error al obtener los sensores", Toast.LENGTH_SHORT).show();
                    Log.d(ETIQUETA_LOG, "Error al obtener los sensores: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
                Log.d(ETIQUETA_LOG, "Error al obtener sensores: " + t.getMessage());
            }
        });
    }

    private void iniciarEscaneo(String sensorUUID) {
        this.callbackDelEscaneo = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult resultado) {
                super.onScanResult(callbackType, resultado);
                Log.d(ETIQUETA_LOG, "iniciarEscaneo(): onScanResult()");

                byte[] bytes = resultado.getScanRecord().getBytes();
                TramaIBeacon tib = new TramaIBeacon(bytes);
                UUID uuid = Utilidades.stringToUUID(Utilidades.bytesToString(tib.getUUID()));

                // Comparar el UUID detectado con el del sensor asignado al usuario
                if (Utilidades.uuidToString(uuid).equals(sensorUUID)) {
                    Log.d(ETIQUETA_LOG, "Dispositivo encontrado: " + uuid);

                    int majorValue = Utilidades.bytesToInt(tib.getMajor());
                    int minorValue = Utilidades.bytesToInt(tib.getMinor());

                    // Llamar al método para actualizar la interfaz de usuario
                    actualizarVistaozonoyTemperatura(majorValue, minorValue);

                    // Iniciar el servicio BLE
                    Intent serviceIntent = new Intent(getContext(), BLEService.class);
                    serviceIntent.putExtra("majorValue", majorValue);
                    serviceIntent.putExtra("minorValue", minorValue);
                    getContext().startService(serviceIntent);

                    PointF location = new PointF(1, 2);
                    String formattedLocation = "(" + location.x + ", " + location.y + ")";
                    int sensorTipo = procesarBeacon(majorValue, minorValue);

                    SensorData sensorData = new SensorData(Utilidades.uuidToString(uuid), minorValue, sensorTipo, formattedLocation, SensorData.getCurrentTimestamp());

                    Call<Void> call = api.createSensorData(sensorData);
                    Log.d(ETIQUETA_LOG, "Enviando datos...");

                    // Enviar datos del sensor al servidor
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                Log.d(ETIQUETA_LOG, "Medición insertada con éxito en el servidor");
                            } else {
                                Log.d(ETIQUETA_LOG, "Respuesta no exitosa: " + response.code());
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Log.e(ETIQUETA_LOG, "Error al insertar medición: ", t);
                            Toast.makeText(getActivity(), "Error al insertar medición en el servidor", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Log.d(ETIQUETA_LOG, "Dispositivo no coincide con el asignado: " + uuid);
                }
            }
        };

        // Opciones de escaneo (opcional, para configuración avanzada)
        ScanSettings scanSettings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build();

        // Iniciar el escaneo
        try {
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            this.elEscanner.startScan(null, scanSettings, this.callbackDelEscaneo);
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Por favor acepta los permisos de Bluetooth y actívalo", Toast.LENGTH_SHORT).show();
            Log.d(ETIQUETA_LOG, "Error de Bluetooth: " + e);
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
                ozono.setText(minorValue + "ppm"); // Mostrar el valor de ozono
                dato_image.setVisibility(View.GONE); // Ocultar la imagen anterior
                dato_image.setVisibility(View.VISIBLE); // Mostrar la imagen de ozono
                new Handler().postDelayed(() -> dato_image.setVisibility(View.GONE), 50); // Ocultar la imagen después de 50 ms
            } else if (procesarBeacon(majorValue, minorValue) == 2) {
                beaconTemperaturaActivo = true; // El beacon de temperatura está activo
                temperatura.setText(minorValue + "ºC"); // Mostrar el valor de temperatura
                dato2_image.setVisibility(View.GONE); // Ocultar la imagen anterior
                dato2_image.setVisibility(View.VISIBLE); // Mostrar la imagen de temperatura
                new Handler().postDelayed(() -> dato2_image.setVisibility(View.GONE), 50); // Ocultar la imagen después de 50 ms
            }
        });
    }

    private boolean checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_REQUEST);
            return false;
        }
        return true;
    }

    private void startQRScanner() {
        IntentIntegrator integrator = IntentIntegrator.forSupportFragment(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt("Escanea el código QR del sensor");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(false);
        integrator.initiateScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null && result.getContents() != null) {
            processSensorQRCode(result.getContents());
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void processSensorQRCode(String qrContent) {
        String uuid = qrContent.split("\\|")[0];
        addSensor(uuid);
    }

    private void addSensor(String uuid) {
        Sensor sensor = new Sensor(uuid, userEmail);

        insertarSensor(sensor);

        // Actualizar la interfaz con el nuevo sensor
        sensorNameTop.setText(uuid);
        sensorName.setText(uuid);
        temperatura.setText("Actualizando...");
        ozono.setText("Actualizando...");

        // Refrescar datos del sensor desde el servidor
        searchData();
    }


    private void insertarSensor(Sensor sensor) {

        Call<Void> call = api.createSensor(sensor);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Registro de sensor exitoso.", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(getContext(), MainActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(), "Error al registrar el sensor", Toast.LENGTH_SHORT).show();
                    Log.d(ETIQUETA_LOG, "Error al registrar el sensor: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startQRScanner();
                } else {
                    Toast.makeText(getContext(),
                            "Se requiere permiso de cámara para escanear QR",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
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
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
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
}