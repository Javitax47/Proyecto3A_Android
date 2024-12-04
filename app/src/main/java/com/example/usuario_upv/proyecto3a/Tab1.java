package com.example.usuario_upv.proyecto3a;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;

public class Tab1 extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener {
    private GoogleMap mapa;
    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.tab1, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        super.onViewCreated(view, savedInstanceState);
        this.rootView = view;

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapa);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        FloatingActionButton fab = view.findViewById(R.id.floatingActionButton2);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mapa.setMyLocationEnabled(true);
                    LocationManager locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);
                    Criteria criteria = new Criteria();
                    String provider = locationManager.getBestProvider(criteria, false);
                    Location location = locationManager.getLastKnownLocation(provider);

                    if (location != null) {
                        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        mapa.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 18));
                    }
                }
            }
        });

        Toolbar toolbar = view.findViewById(R.id.toolbar); // Asegúrate de que el ID del Toolbar es correcto
        if (toolbar != null) {
            // Configurar el Toolbar como ActionBar
            ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
            // Opcional: Establecer el título
            ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle("Mi Mapa");
        }

        // Activar el menú de opciones
        setHasOptionsMenu(true);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapa = googleMap;
        mapa.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mapa.getUiSettings().setZoomControlsEnabled(false);
        mapa.getUiSettings().setCompassEnabled(false);
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_tab1, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Manejar el clic en el botón del menú
        if (item.getItemId() == R.id.action_button) {
            // Iniciar la actividad de alertas cuando el botón sea presionado
            Intent intent = new Intent(getContext(), AlertActivity.class);
            startActivity(intent); // Inicia la nueva actividad
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
