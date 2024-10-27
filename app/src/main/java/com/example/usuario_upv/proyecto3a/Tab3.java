package com.example.usuario_upv.proyecto3a;

import static android.content.Context.MODE_PRIVATE;
import static com.example.usuario_upv.proyecto3a.Tab2.ETIQUETA_LOG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Tab3 extends Fragment {
    private static final int CAMERA_PERMISSION_REQUEST = 100;
    private RecyclerView sensorsRecyclerView;
    private SensorAdapter sensorAdapter;
    private List<String> sensorList;
    private LogicaFake api;
    String userEmail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab3, container, false);

        // Inicializar la lista de sensores
        sensorList = new ArrayList<>();
        setupRecyclerView(view);
        setupQRScanner(view);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(LogicaFake.class);

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        userEmail = sharedPreferences.getString("userEmail", null);

        obtenerSensoresPorUsuario();

        return view;
    }

    private void obtenerSensoresPorUsuario() {
        Call<ResponseBody> call = api.getUserSensors(userEmail);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseData = response.body().string();
                        Log.d(ETIQUETA_LOG, "Sensores obtenidos: " + responseData);

                        // Parsear el JSON para obtener el array de objetos
                        JSONArray jsonArray = new JSONArray(responseData);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String uuid = jsonObject.getString("uuid");

                            if (!sensorList.contains(uuid)) {
                                Config.addUUID(uuid);
                                sensorList.add(uuid);
                                sensorAdapter.notifyItemInserted(sensorList.size() - 1);
                            } else {
                                Log.d(ETIQUETA_LOG, "UUID ya existe en la lista: " + uuid);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d(ETIQUETA_LOG, "Error al procesar la respuesta: " + e.getMessage());
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d(ETIQUETA_LOG, "Error al parsear el JSON: " + e.getMessage());
                    }
                } else {
                    Toast.makeText(getContext(), "Error al obtener los sensores", Toast.LENGTH_SHORT).show();
                    Log.d(ETIQUETA_LOG, "Error al obtener los sensores: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                Log.d(ETIQUETA_LOG, "Error al obtener: " + t.getMessage());
            }
        });
    }

    private void setupRecyclerView(View view) {
        sensorsRecyclerView = view.findViewById(R.id.sensorsRecyclerView);
        sensorAdapter = new SensorAdapter(sensorList);
        sensorsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        sensorsRecyclerView.setAdapter(sensorAdapter);
    }

    private void setupQRScanner(View view) {
        FloatingActionButton scanQrButton = view.findViewById(R.id.scanQrButton);
        scanQrButton.setOnClickListener(v -> {
            if (checkCameraPermission()) {
                startQRScanner();
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

        Log.d(ETIQUETA_LOG, "Uuid: " + uuid);
        Log.d(ETIQUETA_LOG, "userEmail: " + userEmail);
        insertarSensor(sensor);

        obtenerSensoresPorUsuario();
        Toast.makeText(getContext(), "Sensor añadido: " + uuid,
                Toast.LENGTH_SHORT).show();
    }

    private void insertarSensor(Sensor sensor){

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
}