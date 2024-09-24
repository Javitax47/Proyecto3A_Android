package com.example.usuario_upv.proyecto3a;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.util.UUID;
import org.mockito.Mockito;

import android.bluetooth.le.ScanResult;

public class BeaconTest {

    private final UUID UUID_DISPOSITIVO_BUSCADO = Utilidades.stringToUUID("EPSG-GTI-PROY-3A");
    private ScanResult mockResultado;
    private TramaIBeacon mockTramaIBeacon;
    private MainActivity mainActivity;

    @Before
    public void setUp() {
        // Inicializamos los mocks
        mockResultado = Mockito.mock(ScanResult.class);
        mockTramaIBeacon = Mockito.mock(TramaIBeacon.class);
        mainActivity = new MainActivity(); // Crear instancia de MainActivity

        // Simulamos los valores del beacon
        Mockito.when(mockResultado.getDevice().getName()).thenReturn("Sensor CO2");
        Mockito.when(mockResultado.getRssi()).thenReturn(-50);
        Mockito.when(mockResultado.getScanRecord().getBytes()).thenReturn(new byte[]{});
        Mockito.when(mockTramaIBeacon.getMajor()).thenReturn(new byte[]{0x0B, 0x01}); // Tipo CO2
        Mockito.when(mockTramaIBeacon.getMinor()).thenReturn(new byte[]{0x00, 0x64}); // Valor de CO2 = 100
        Mockito.when(mockTramaIBeacon.getUUID()).thenReturn(UUID_DISPOSITIVO_BUSCADO.toString().getBytes());
    }

    @Test
    public void testBeaconCO2Detection() {
        // Simulamos el escaneo de un beacon y procesamos la información
        mainActivity.mostrarInformacionDispositivoBTLE(mockResultado, UUID_DISPOSITIVO_BUSCADO);

        // Verificamos que el tipo de sensor sea CO2
        int tipoSensor = mainActivity.procesarBeacon(0x0B01, 0x0064); // Major (CO2) y Minor (valor)
        assertEquals(1, tipoSensor); // 1 indica CO2

        // Verificamos que el valor del sensor sea correcto
        assertTrue(mainActivity.beaconCO2Activo);
        assertEquals("CO2: 100", mainActivity.dato1.getText().toString());
    }

    @Test
    public void testBeaconTemperatureDetection() {
        // Cambiamos los valores simulados para representar un beacon de temperatura
        Mockito.when(mockTramaIBeacon.getMajor()).thenReturn(new byte[]{0x0C, 0x01}); // Tipo Temperatura
        Mockito.when(mockTramaIBeacon.getMinor()).thenReturn(new byte[]{0x00, 0x1E}); // Valor de temperatura = 30

        // Simulamos el escaneo y procesamos la información
        mainActivity.mostrarInformacionDispositivoBTLE(mockResultado, UUID_DISPOSITIVO_BUSCADO);

        // Verificamos que el tipo de sensor sea Temperatura
        int tipoSensor = mainActivity.procesarBeacon(0x0C01, 0x001E); // Major (Temperatura) y Minor (valor)
        assertEquals(2, tipoSensor); // 2 indica Temperatura

        // Verificamos que el valor del sensor sea correcto
        assertTrue(mainActivity.beaconTemperaturaActivo);
        assertEquals("ºC: 30", mainActivity.dato2.getText().toString());
    }
}
