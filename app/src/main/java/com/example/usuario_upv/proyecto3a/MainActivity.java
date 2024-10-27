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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

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
     * @brief EditText donde el usuario ingresa la IP para la conexión.
     */
    private EditText ipInput;

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

    /**
     * @brief Mapa que almacena los temporizadores para eliminar las vistas de dispositivos inactivos.
     *
     * La clave es el identificador único del dispositivo, y el valor es un temporizador
     * para gestionar el tiempo de visualización.
     */
    private Map<String, Handler> temporizadoresDispositivos = new HashMap<>();

    // --------------------------------------------------------------
    // --------------------------------------------------------------

    // Nombres de las pestañas
    private int[] imagenes = new int[]{R.drawable.mapa, R.drawable.menu, R.drawable.usuario};

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

        // Inicializa el Bluetooth
        try {
            inicializarBlueTooth();
        }catch (Exception e){
            Toast.makeText(MainActivity.this, "Por favor, activa el Bluetooth", Toast.LENGTH_SHORT).show();
        }

        initializeApp();
    }

    protected void initializeApp() {
        //Pestañas
        ViewPager2 viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(new MainActivity.MiPagerAdapter(this));
        TabLayout tabs = findViewById(R.id.tabs);
        new TabLayoutMediator(tabs, viewPager,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    // Dentro de onConfigureTab
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position){
                        View tabView = getLayoutInflater().inflate(R.layout.custom_tab, null);
                        ImageView imageView = tabView.findViewById(R.id.tab_image);
                        imageView.setImageResource(imagenes[position]); // Establecer la imagen aquí

                        // Ajusta el tamaño de la imagen aquí
                        imageView.getLayoutParams().height = 125;
                        imageView.getLayoutParams().width = 125;

                        tab.setCustomView(tabView);
                    }
                }).attach();
        // Deshabilitar el cambio de fragmentos deslizando horizontalmente
        viewPager.setUserInputEnabled(false);
    }

    public class MiPagerAdapter extends FragmentStateAdapter {

        public MiPagerAdapter(FragmentActivity activity){
            super(activity);
        }

        @Override
        public int getItemCount() {
            return 3;
        }

        @Override @NonNull
        public Fragment createFragment(int position) {
            switch (position) {
                case 0: return new Tab1();
                case 1: return new Tab2();
                case 2: return new Tab3();
            }
            return null;
        }
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
            if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.FOREGROUND_SERVICE_DATA_SYNC) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        MainActivity.this,
                        new String[]{android.Manifest.permission.BLUETOOTH_SCAN, android.Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.ACCESS_FINE_LOCATION},
                        CODIGO_PETICION_PERMISOS
                );
            } else {
                Log.d(ETIQUETA_LOG, "inicializarBlueTooth(): parece que YA tengo los permisos necesarios en Android 12+ !!!!");
            }
        } else {
            // Para Android 11 y versiones anteriores, pedimos los permisos de Bluetooth y localización antiguos
            if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        MainActivity.this,
                        new String[]{android.Manifest.permission.BLUETOOTH, android.Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.ACCESS_FINE_LOCATION},
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
        }
    } // ()
} // class
