
package com.example.usuario_upv.proyecto3a;

import java.util.Arrays;

/**
 * @class TramaIBeacon
 * @brief Clase que representa la trama de un beacon iBeacon.
 *
 * Esta clase se encarga de interpretar los datos de un beacon iBeacon
 * a partir de un array de bytes. Extrae información relevante como el
 * UUID, el major, el minor y el TxPower.
 */
public class TramaIBeacon {
    private byte[] prefijo = null;  ///< Prefijo del beacon (hasta 9 bytes).
    private byte[] uuid = null;     ///< UUID del beacon (16 bytes).
    private byte[] major = null;    ///< Major del beacon (2 bytes).
    private byte[] minor = null;    ///< Minor del beacon (2 bytes).
    private byte txPower = 0;       ///< TxPower del beacon (1 byte).

    private byte[] losBytes;         ///< Array de bytes original recibido.

    private byte[] advFlags = null;  ///< Banderas de publicidad (3 bytes).
    private byte[] advHeader = null;  ///< Encabezado de publicidad (2 bytes).
    private byte[] companyID = new byte[2]; ///< ID de la compañía (2 bytes).
    private byte iBeaconType = 0;    ///< Tipo de iBeacon (1 byte).
    private byte iBeaconLength = 0;   ///< Longitud del iBeacon (1 byte).

    private boolean noadvFlags;       ///< Indica si hay banderas de publicidad.

    // -------------------------------------------------------------------------------
    // Métodos Getters
    // -------------------------------------------------------------------------------

    /**
     * @brief Obtiene el prefijo del beacon.
     * @return Array de bytes que representa el prefijo.
     */
    public byte[] getPrefijo() { return prefijo; }

    /**
     * @brief Obtiene el UUID del beacon.
     * @return Array de bytes que representa el UUID.
     */
    public byte[] getUUID() { return uuid; }

    /**
     * @brief Obtiene el major del beacon.
     * @return Array de bytes que representa el major.
     */
    public byte[] getMajor() { return major; }

    /**
     * @brief Obtiene el minor del beacon.
     * @return Array de bytes que representa el minor.
     */
    public byte[] getMinor() { return minor; }

    /**
     * @brief Obtiene el TxPower del beacon.
     * @return Byte que representa el TxPower.
     */
    public byte getTxPower() { return txPower; }

    /**
     * @brief Obtiene el array de bytes original recibido.
     * @return Array de bytes original.
     */
    public byte[] getLosBytes() { return losBytes; }

    /**
     * @brief Obtiene las banderas de publicidad.
     * @return Array de bytes que representa las banderas de publicidad.
     */
    public byte[] getAdvFlags() { return advFlags; }

    /**
     * @brief Obtiene el encabezado de publicidad.
     * @return Array de bytes que representa el encabezado de publicidad.
     */
    public byte[] getAdvHeader() { return advHeader; }

    /**
     * @brief Obtiene el ID de la compañía.
     * @return Array de bytes que representa el ID de la compañía.
     */
    public byte[] getCompanyID() { return companyID; }

    /**
     * @brief Obtiene el tipo de iBeacon.
     * @return Byte que representa el tipo de iBeacon.
     */
    public byte getiBeaconType() { return iBeaconType; }

    /**
     * @brief Obtiene la longitud del iBeacon.
     * @return Byte que representa la longitud del iBeacon.
     */
    public byte getiBeaconLength() { return iBeaconLength; }

    // -------------------------------------------------------------------------------
    // Métodos Setters
    // -------------------------------------------------------------------------------

    /**
     * @brief Establece el valor del major del beacon.
     * @param major Array de bytes que representa el major.
     */
    public void setMajor(byte[] major) { this.major = major; }

    /**
     * @brief Establece el valor del minor del beacon.
     * @param minor Array de bytes que representa el minor.
     */
    public void setMinor(byte[] minor) { this.minor = minor; }

    // -------------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------------

    /**
     * @brief Constructor que crea una instancia de TramaIBeacon.
     *
     * @param bytes Array de bytes que contiene los datos del beacon.
     */
    public TramaIBeacon(byte[] bytes) {
        this.losBytes = bytes;

        // Verificar si el beacon transmite los primeros 3 bytes dedicados a advFlags.
        noadvFlags = !(losBytes[0] == 02 && losBytes[1] == 01 && losBytes[2] == 06);

        if (noadvFlags) {
            // Extraer datos sin las banderas de publicidad
            prefijo = Arrays.copyOfRange(losBytes, 0, 6); // 6 bytes
            uuid = Arrays.copyOfRange(losBytes, 6, 22); // 16 bytes
            major = Arrays.copyOfRange(losBytes, 22, 24); // 2 bytes
            minor = Arrays.copyOfRange(losBytes, 24, 26); // 2 bytes
            txPower = losBytes[26]; // 1 byte

            advHeader = Arrays.copyOfRange(prefijo, 0, 2); // 2 bytes
            companyID = Arrays.copyOfRange(prefijo, 2, 4); // 2 bytes
            iBeaconType = prefijo[4]; // 1 byte
            iBeaconLength = prefijo[5]; // 1 byte
        } else {
            // Extraer datos con las banderas de publicidad
            prefijo = Arrays.copyOfRange(losBytes, 0, 9); // 9 bytes
            uuid = Arrays.copyOfRange(losBytes, 9, 25); // 16 bytes
            major = Arrays.copyOfRange(losBytes, 25, 27); // 2 bytes
            minor = Arrays.copyOfRange(losBytes, 27, 29); // 2 bytes
            txPower = losBytes[29]; // 1 byte

            advFlags = Arrays.copyOfRange(prefijo, 0, 3); // 3 bytes
            advHeader = Arrays.copyOfRange(prefijo, 3, 5); // 2 bytes
            companyID = Arrays.copyOfRange(prefijo, 5, 7); // 2 bytes
            iBeaconType = prefijo[7]; // 1 byte
            iBeaconLength = prefijo[8]; // 1 byte
        }
    } // ()
} // class