package com.example.usuario_upv.proyecto3a;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.google.maps.android.geometry.Point;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.WeightedLatLng;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
/**
 * @class Tab1
 * @brief Fragmento que muestra un mapa con datos de sensores.
 *
 * Este fragmento contiene un mapa que puede mostrar datos de sensores
 * en forma de mapa de calor o marcadores. Permite seleccionar la fecha
 * y el tipo de medición para visualizar los datos correspondientes.
 */
public class Tab1 extends Fragment implements OnMapReadyCallback {
    private GoogleMap mapa;
    private View rootView;
    private HeatmapTileProvider heatmapO3Provider;
    private HeatmapTileProvider heatmapTempProvider;
    private TileOverlay heatmapO3Overlay;
    private TileOverlay heatmapTempOverlay;
    private HeatmapTileProvider heatmapProvider;
    private TileOverlay heatmapOverlay;
    private LogicaFake api;
    private Button btnSelectDate;
    private Button btnToggleView;  // Botón para alternar entre mapa de calor y puntos
    private Button btnUpdateData;  // Botón para actualizar datos manualmente
    private boolean isHeatmapVisible = true;  // Controla si estamos mostrando el mapa de calor o los puntos
    private List<LatLng> o3Locations = new ArrayList<>();
    private List<LatLng> tempLocations = new ArrayList<>();
    // Reemplaza las listas de LatLng por listas de WeightedLatLng
    private List<WeightedLatLng> o3Data = new ArrayList<>();
    private List<WeightedLatLng> tempData = new ArrayList<>();

    private static final String DATE_FORMAT = "yyyy-MM-dd";  // Formato de fecha
    private String currentSelectedDate; // Almacena la fecha actualmente seleccionada por el usuario

    private Spinner spinnerMeasurementType; // Spinner para seleccionar el tipo de medición
    private int selectedMeasurementType = 0; // Tipo de medición actual (2 = Ozono, 1 = Temperatura)
    static int[] colors = {
            Color.argb(255,   0,   255, 0),   // Verde invisible al inicio
            Color.argb(255, 255, 255, 0),   // Amarillo opaco
            Color.argb(255, 255, 0,   0)    // Rojo opaco
    };
    static float[] startPoints = {
            0.2f, 0.5f, 1.0f
    };

    private static final Gradient O3_GRADIENT = new Gradient(colors, startPoints);

    private GroundOverlay customOverlay;
/**
 * @brief Método llamado cuando se crea la vista del fragmento.
 * @param inflater El LayoutInflater.
 * @param container El contenedor ViewGroup.
 * @param savedInstanceState El estado previamente guardado.
 * @return La vista creada.
 */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.tab1, container, false);
        api = RetrofitClient.getClient(Config.BASE_URL).create(LogicaFake.class);

        // Configurar el Spinner
        spinnerMeasurementType = rootView.findViewById(R.id.spinnerMeasurementType);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.measurement_types,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMeasurementType.setAdapter(adapter);

        // Listener para manejar cambios en la selección
        spinnerMeasurementType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Cambiar el tipo de medición basado en la selección
                selectedMeasurementType = position; // 0: Todos, 2: Ozono, 1: Temperatura
                updateViewBasedOnSelection();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No hacer nada si no hay selección
            }
        });

        btnSelectDate = rootView.findViewById(R.id.btnSelectDate);
        btnSelectDate.setOnClickListener(v -> showDatePickerDialog());

        btnToggleView = rootView.findViewById(R.id.btnToggleView);
        btnToggleView.setOnClickListener(v -> toggleMapView());

        btnUpdateData = rootView.findViewById(R.id.btnUpdateData);
        btnUpdateData.setOnClickListener(v -> fetchSensorData(SensorData.getCurrentTimestamp()));

        fetchSensorData(SensorData.getCurrentTimestamp()); // Cargar datos iniciales

        return rootView;
    }
/**
 * @brief Actualiza la vista basada en la selección del tipo de medición.
 */
    private void updateViewBasedOnSelection() {
        if (selectedMeasurementType == 2) {
            // Mostrar solo Ozono
            if (isHeatmapVisible) {
                updateHeatmap(o3Data, Collections.emptyList());
            } else {
                updateMarkers(o3Data, Collections.emptyList());
            }
        } else if (selectedMeasurementType == 1) {
            // Mostrar solo Temperatura
            if (isHeatmapVisible) {
                updateHeatmap(Collections.emptyList(), tempData);
            } else {
                updateMarkers(Collections.emptyList(), tempData);
            }
        } else {
            // Mostrar ambos (Ozono y Temperatura)
            if (isHeatmapVisible) {
                updateHeatmap(o3Data, tempData);
            } else {
                updateMarkers(o3Data, tempData);
            }
        }
    }

    /**
     * @brief Método llamado cuando la vista del fragmento ha sido creada.
     * @param view La vista creada.
     * @param savedInstanceState El estado previamente guardado.
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.rootView = view;

        // Configurar el mapa
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapa);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }
    /**
     * @brief Método llamado cuando el mapa está listo para ser usado.
     * @param googleMap La instancia de GoogleMap.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapa = googleMap;
        mapa.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        mapa.setOnCameraIdleListener(() -> {
            CameraPosition position = mapa.getCameraPosition();
            double lat = position.target.latitude;
            float zoom = position.zoom;

            // Ajustamos el radio de los heatmaps
            actualizarRadioHeatmaps(lat, zoom);
        });

        // Agregar el overlay personalizado inicialmente
        addCustomOverlay();

        // Ejemplo: mover cámara a la posición del overlay
        LatLng customLatLng = new LatLng(39.456111109894, -0.37583332983165);
        mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(customLatLng, 17f));

        // Otros listeners y configuraciones si los tienes...
        mapa.setOnCameraIdleListener(() -> {
            CameraPosition position = mapa.getCameraPosition();
            double lat = position.target.latitude;
            float zoom = position.zoom;
            actualizarRadioHeatmaps(lat, zoom);
        });
    }
    /**
     * @brief Agrega un overlay personalizado al mapa.
     */
    private void addCustomOverlay() {
        // Definir la posición donde se ubicará el overlay
        LatLng customLatLng = new LatLng(39.456111109894, -0.37583332983165);

        // Configurar las opciones del overlay:
        GroundOverlayOptions groundOverlayOptions = new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.oficial))
                // Se establece el ancho en 50 metros (el alto se ajustará manteniendo la relación de aspecto de la imagen)
                .position(customLatLng, 50)
                // Asigna un z-index mayor para que se pinte por encima de otros elementos (por ejemplo, heatmap)
                .zIndex(2)
                .bearing(-mapa.getCameraPosition().bearing)
                // Configura la transparencia si lo deseas (0.0f opaco, 1.0f totalmente transparente)
                .transparency(0.0f);

        // Agregar el overlay al mapa y guardar la referencia en la variable global
        customOverlay = mapa.addGroundOverlay(groundOverlayOptions);
    }

    /**
     * @brief Alterna entre la vista de mapa de calor y marcadores.
     */
    private void toggleMapView() {
        isHeatmapVisible = !isHeatmapVisible;

        if (isHeatmapVisible) {
            // Limpiar todo el mapa
            mapa.clear();

            // Reagregar el overlay personalizado para que siempre se vea
            addCustomOverlay();

            // Agregar el heatmap según tu lógica actual
            addHeatmap(
                    o3Locations.stream().map(location -> new WeightedLatLng(location)).collect(Collectors.toList()),
                    tempLocations.stream().map(location -> new WeightedLatLng(location)).collect(Collectors.toList())
            );
        } else {
            // Remover overlays de heatmap si están presentes (en caso de que se hayan agregado de forma separada)
            if (heatmapO3Overlay != null) heatmapO3Overlay.remove();
            if (heatmapTempOverlay != null) heatmapTempOverlay.remove();

            // Limpiar el mapa
            mapa.clear();

            // Reagregar el overlay personalizado
            addCustomOverlay();

            // Agregar los marcadores según tu lógica actual
            addMarkers(o3Locations, tempLocations);
        }
    }
    /**
     * @brief Actualiza el radio de los heatmaps basado en la latitud y el zoom.
     * @param lat La latitud actual.
     * @param zoom El nivel de zoom actual.
     */
    private void actualizarRadioHeatmaps(double lat, float zoom) {
        // 2. Definir el radio deseado en metros
        double radioMetrosDeseado = 500.0;

        // 3. Calcular la resolución del terreno (aprox. metros/pixel)
        double scale = 1 << (int) zoom; // 2^zoom
        double groundResolution = (Math.cos(Math.toRadians(lat)) * 40075016.686)
                / (256 * scale);

        // Convertir los metros deseados a pixeles
        double radioEnPixeles = radioMetrosDeseado / groundResolution;

        // Ejemplo: limitar el rango del radio en pixeles para que no sea demasiado grande o demasiado pequeño
        int radioPixels = (int) Math.max(5, Math.min(radioEnPixeles, 200));

        // 4. Asignar el nuevo radio y refrescar si los providers no son nulos
        if (heatmapProvider != null) {
            heatmapProvider.setRadius(radioPixels);
            if (heatmapOverlay != null) {
                heatmapOverlay.clearTileCache();
            }
        }
    }

    /**
     * @brief Calcula el AQI aproximado para Ozono (O3) en función de la concentración.
     * @param concentration La concentración de Ozono.
     * @return El valor de AQI calculado.
     */
    private double computeAqiOzone(double concentration) {

        if (concentration <= 0) {
            // Evitar valores negativos o cero
            return 0;
        } else if (concentration <= 54) {
            return linearInterpolate(concentration, 0, 54,   0,  50);
        } else if (concentration <= 70) {
            return linearInterpolate(concentration, 55, 70, 51, 100);
        } else if (concentration <= 85) {
            return linearInterpolate(concentration, 71, 85, 101, 150);
        } else if (concentration <= 105) {
            return linearInterpolate(concentration, 86, 105, 151, 200);
        } else {
            return 300;
        }
    }

    /**
     * @brief Función auxiliar para la interpolación lineal entre dos tramos.
     * @param c El valor de concentración.
     * @param cLow El valor bajo de concentración.
     * @param cHigh El valor alto de concentración.
     * @param iLow El valor bajo de índice.
     * @param iHigh El valor alto de índice.
     * @return El valor interpolado.
     */
    private double linearInterpolate(double c, double cLow, double cHigh, double iLow, double iHigh) {
        return ( (iHigh - iLow) / (cHigh - cLow) ) * (c - cLow) + iLow;
    }


    /**
     * @brief Muestra el DatePicker cuando el usuario hace clic en el botón.
     */
    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();

        // Si ya hay una fecha seleccionada, usarla como inicial
        if (currentSelectedDate != null) {
            try {
                Date selectedDate = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).parse(currentSelectedDate);
                if (selectedDate != null) {
                    calendar.setTime(selectedDate);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                (view, year, monthOfYear, dayOfMonth) -> {
                    // Formatear la fecha seleccionada
                    calendar.set(year, monthOfYear, dayOfMonth);
                    String selectedDate = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(calendar.getTime());
                    currentSelectedDate = selectedDate; // Guardar la fecha seleccionada
                    fetchSensorData(selectedDate);  // Llamar a la API con la fecha seleccionada
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }


    /**
     * @brief Llama a la API para obtener las mediciones de una fecha específica.
     * @param selectedDate La fecha seleccionada.
     */
    private void fetchSensorData(String selectedDate) {
        currentSelectedDate = selectedDate; // Actualizar la fecha seleccionada

        Call<List<SensorData>> call = api.medicionesbbdd(selectedDate);
        call.enqueue(new Callback<List<SensorData>>() {
            @Override
            public void onResponse(Call<List<SensorData>> call, Response<List<SensorData>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    processMeasurements(response.body());
                } else {
                    Log.d(">>>>", "" + response);
                    Toast.makeText(getContext(), "Error fetching data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<SensorData>> call, Throwable t) {
                Log.d(">>>>", "" + t.getMessage());
                Toast.makeText(getContext(), "Failed to connect to server: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Activar el menú de opciones
        setHasOptionsMenu(true);
    }
    /**
     * @brief Procesa las mediciones obtenidas de la API.
     * @param measurements La lista de mediciones.
     */
    private void processMeasurements(List<SensorData> measurements) {
        // Limpiar las listas antes de llenarlas de nuevo
        o3Data.clear();
        tempData.clear();

        for (SensorData m : measurements) {
            // Evitar valores sin coordenadas reales
            if (m.getLatitude() != 0.0 || m.getLongitude() != 0.0) {

                double intensity;
                if (m.getTipo() == 2) {
                    // Es O3 ⇒ Calculamos AQI
                    intensity = computeAqiOzone(m.getValor());
                } else {
                    // Ejemplo: si no es O3 (p.ej., temperatura),
                    // podrías dejar el valor tal cual, o escalarlo
                    intensity = m.getValor();
                }

                WeightedLatLng weightedPoint = new WeightedLatLng(
                        new LatLng(m.getLongitude(), m.getLatitude()),
                        intensity
                );

                // Clasificar según tipo
                if (m.getTipo() == 2) {
                    // O3
                    o3Data.add(weightedPoint);
                } else if (m.getTipo() == 1) {
                    // Temperatura
                    tempData.add(weightedPoint);
                }
            }
        }

        updateViewBasedOnSelection();
    }
    /**
     * @brief Actualiza el mapa de calor con los puntos de Ozono y Temperatura.
     * @param o3Points Los puntos de Ozono.
     * @param tempPoints Los puntos de Temperatura.
     */
    private void updateHeatmap(List<WeightedLatLng> o3Points, List<WeightedLatLng> tempPoints) {
        // 1. Combinar todas las mediciones en una sola lista
        List<WeightedLatLng> unifiedData = new ArrayList<>();
        unifiedData.addAll(o3Points);
        unifiedData.addAll(tempPoints);

        // 2. Si hay datos, actualizamos o creamos el Heatmap unificado
        if (!unifiedData.isEmpty()) {
            if (heatmapProvider == null) {
                heatmapProvider = new HeatmapTileProvider.Builder()
                        .weightedData(unifiedData)
                        .radius(50)
                        // AÑADE tu gradiente aquí, si quieres que todo use el mismo
                        .gradient(O3_GRADIENT)
                        .build();

                // Overlay
                heatmapOverlay = mapa.addTileOverlay(
                        new TileOverlayOptions().tileProvider(heatmapProvider)
                );
            } else {
                // Ya existía, solo actualizamos
                heatmapProvider.setWeightedData(unifiedData);
                if (heatmapOverlay != null) {
                    heatmapOverlay.clearTileCache();
                }
            }
        } else {
            // 3. Si no hay datos, eliminamos el Heatmap si existe
            if (heatmapOverlay != null) {
                heatmapOverlay.remove();
                heatmapOverlay = null;
            }
            heatmapProvider = null;
        }
    }

    /**
     * @brief Actualiza los marcadores dinámicamente en el mapa.
     * @param o3Points Los puntos de Ozono.
     * @param tempPoints Los puntos de Temperatura.
     */
    private void updateMarkers(List<WeightedLatLng> o3Points, List<WeightedLatLng> tempPoints) {
        mapa.clear();

        // Marcadores para Ozono
        for (WeightedLatLng w : o3Points) {
            Point p = w.getPoint();
            LatLng position = new LatLng(p.y, p.x);
            mapa.addMarker(new MarkerOptions().position(position).title("O3: " + w.getIntensity()));
        }

        // Marcadores para Temperatura
        for (WeightedLatLng w : tempPoints) {
            Point p = w.getPoint();
            LatLng position = new LatLng(p.y, p.x);
            mapa.addMarker(new MarkerOptions().position(position).title("Temp: " + w.getIntensity()));
        }
    }


    /**
     * @brief Agrega un mapa de calor con los puntos de Ozono y Temperatura.
     * @param o3Points Los puntos de Ozono.
     * @param tempPoints Los puntos de Temperatura.
     */
    private void addHeatmap(List<WeightedLatLng> o3Points, List<WeightedLatLng> tempPoints) {
        // Eliminar los overlays de mapa de calor si ya existen
        if (heatmapO3Overlay != null) {
            heatmapO3Overlay.remove();
        }
        if (heatmapTempOverlay != null) {
            heatmapTempOverlay.remove();
        }

        if (o3Points.size() > 0) {
            heatmapO3Provider = new HeatmapTileProvider.Builder().weightedData(o3Points).build();
            heatmapO3Overlay = mapa.addTileOverlay(new TileOverlayOptions().tileProvider(heatmapO3Provider));
        }

        if (tempPoints.size() > 0) {
            heatmapTempProvider = new HeatmapTileProvider.Builder().weightedData(tempPoints).build();
            heatmapTempOverlay = mapa.addTileOverlay(new TileOverlayOptions().tileProvider(heatmapTempProvider));
        }
    }

    /**
     * @brief Agrega marcadores en el mapa.
     * @param o3Locations Las ubicaciones de Ozono.
     * @param tempLocations Las ubicaciones de Temperatura.
     */
    private void addMarkers(List<LatLng> o3Locations, List<LatLng> tempLocations) {
        // Limpiar los puntos antiguos si estamos en el modo de marcadores
        mapa.clear();

        for (LatLng o3Location : o3Locations) {
            mapa.addMarker(new MarkerOptions().position(o3Location).title("O3"));
        }

        for (LatLng tempLocation : tempLocations) {
            mapa.addMarker(new MarkerOptions().position(tempLocation).title("Temp"));
        }
    }
}