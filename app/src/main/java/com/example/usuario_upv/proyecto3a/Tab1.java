package com.example.usuario_upv.proyecto3a;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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

public class Tab1 extends Fragment implements OnMapReadyCallback {
    private GoogleMap mapa;
    private View rootView;
    private HeatmapTileProvider heatmapO3Provider;
    private HeatmapTileProvider heatmapTempProvider;
    private TileOverlay heatmapO3Overlay;
    private TileOverlay heatmapTempOverlay;
    private LogicaFake api;
    private Button btnSelectDate;
    private Button btnToggleView;  // Botón para alternar entre mapa de calor y puntos
    private Button btnUpdateData;  // Botón para actualizar datos manualmente
    private boolean isHeatmapVisible = true;  // Controla si estamos mostrando el mapa de calor o los puntos
    private List<LatLng> o3Locations = new ArrayList<>();
    private List<LatLng> tempLocations = new ArrayList<>();

    private static final String DATE_FORMAT = "yyyy-MM-dd";  // Formato de fecha
    private String currentSelectedDate; // Almacena la fecha actualmente seleccionada por el usuario

    private Spinner spinnerMeasurementType; // Spinner para seleccionar el tipo de medición
    private int selectedMeasurementType = 0; // Tipo de medición actual (2 = Ozono, 1 = Temperatura)


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

    private void updateViewBasedOnSelection() {
        if (selectedMeasurementType == 2) { // Mostrar solo ozono
            if (isHeatmapVisible) {
                updateHeatmap(
                        o3Locations.stream().map(location -> new WeightedLatLng(location)).collect(Collectors.toList()),
                        Collections.emptyList()
                );
            } else {
                updateMarkers(o3Locations, Collections.emptyList());
            }
        } else if (selectedMeasurementType == 1) { // Mostrar solo temperatura
            if (isHeatmapVisible) {
                updateHeatmap(
                        Collections.emptyList(),
                        tempLocations.stream().map(location -> new WeightedLatLng(location)).collect(Collectors.toList())
                );
            } else {
                updateMarkers(Collections.emptyList(), tempLocations);
            }
        } else { // Mostrar todos (ozono y temperatura)
            if (isHeatmapVisible) {
                updateHeatmap(
                        o3Locations.stream().map(location -> new WeightedLatLng(location)).collect(Collectors.toList()),
                        tempLocations.stream().map(location -> new WeightedLatLng(location)).collect(Collectors.toList())
                );
            } else {
                updateMarkers(o3Locations, tempLocations);
            }
        }
    }

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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapa = googleMap;
        mapa.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    // Mostrar el DatePicker cuando el usuario haga clic en el botón
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


    // Llamada a la API para obtener las mediciones de una fecha específica
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

    private void processMeasurements(List<SensorData> measurements) {
        List<WeightedLatLng> newO3Points = new ArrayList<>();
        List<WeightedLatLng> newTempPoints = new ArrayList<>();
        o3Locations.clear();
        tempLocations.clear();

        for (SensorData m : measurements) {
            if (m.getLatitude() != 0.0 || m.getLongitude() != 0.0) {
                LatLng location = new LatLng(m.getLatitude(), m.getLongitude());
                if (m.getTipo() == 2) {
                    newO3Points.add(new WeightedLatLng(location, m.getValor()));
                    o3Locations.add(location);
                } else if (m.getTipo() == 1) {
                    newTempPoints.add(new WeightedLatLng(location, m.getValor()));
                    tempLocations.add(location);
                }
            }
        }

        // Actualizar mapa según la vista actual
        if (isHeatmapVisible) {
            updateHeatmap(newO3Points, newTempPoints);
        } else {
            updateMarkers(o3Locations, tempLocations);
        }
    }

    // Actualizar el heatmap dinámicamente
    private void updateHeatmap(List<WeightedLatLng> o3Points, List<WeightedLatLng> tempPoints) {
        // Actualizar o crear el heatmap para ozono
        if (!o3Points.isEmpty()) {
            if (heatmapO3Provider == null) {
                // Crear el proveedor y agregar el overlay si no existe
                heatmapO3Provider = new HeatmapTileProvider.Builder().weightedData(o3Points).build();
                heatmapO3Overlay = mapa.addTileOverlay(new TileOverlayOptions().tileProvider(heatmapO3Provider));
            } else {
                // Actualizar los datos del proveedor existente sin remover el overlay
                heatmapO3Provider.setWeightedData(o3Points);
                heatmapO3Overlay.clearTileCache(); // Forzar la actualización visual
            }
        } else if (heatmapO3Overlay != null) {
            // Remover el overlay si no hay datos
            heatmapO3Overlay.remove();
            heatmapO3Overlay = null;
            heatmapO3Provider = null;
        }
        // Actualizar o crear el heatmap para temperatura
        if (!tempPoints.isEmpty()) {
            if (heatmapTempProvider == null) {
                // Crear el proveedor y agregar el overlay si no existe
                heatmapTempProvider = new HeatmapTileProvider.Builder().weightedData(tempPoints).build();
                heatmapTempOverlay = mapa.addTileOverlay(new TileOverlayOptions().tileProvider(heatmapTempProvider));
            } else {
                // Actualizar los datos del proveedor existente sin remover el overlay
                heatmapTempProvider.setWeightedData(tempPoints);
                heatmapTempOverlay.clearTileCache(); // Forzar la actualización visual
            }
        } else if (heatmapTempOverlay != null) {
            // Remover el overlay si no hay datos
            heatmapTempOverlay.remove();
            heatmapTempOverlay = null;
            heatmapTempProvider = null;
        }
    }


    // Actualizar los marcadores dinámicamente
    private void updateMarkers(List<LatLng> o3Locations, List<LatLng> tempLocations) {
        mapa.clear();

        for (LatLng o3Location : o3Locations) {
            mapa.addMarker(new MarkerOptions().position(o3Location).title("O3"));
        }

        for (LatLng tempLocation : tempLocations) {
            mapa.addMarker(new MarkerOptions().position(tempLocation).title("Temp"));
        }
    }


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

    // Agregar marcadores en el mapa
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

    private void toggleMapView() {
        isHeatmapVisible = !isHeatmapVisible;

        if (isHeatmapVisible) {
            // Eliminar los marcadores actuales
            mapa.clear();  // Asegurarse de eliminar solo los marcadores
            // Volver a agregar los heatmaps
            addHeatmap(
                    o3Locations.stream().map(location -> new WeightedLatLng(location)).collect(Collectors.toList()),
                    tempLocations.stream().map(location -> new WeightedLatLng(location)).collect(Collectors.toList())
            );
        } else {
            // Eliminar los overlays de heatmap si están presentes
            if (heatmapO3Overlay != null) heatmapO3Overlay.remove();
            if (heatmapTempOverlay != null) heatmapTempOverlay.remove();
            // Agregar los marcadores
            addMarkers(o3Locations, tempLocations);
        }
    }

}
