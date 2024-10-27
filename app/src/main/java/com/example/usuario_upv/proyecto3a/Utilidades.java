package com.example.usuario_upv.proyecto3a;


import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.UUID;

/**
 * @class Utilidades
 * @brief Clase utilitaria para conversiones entre diferentes tipos de datos.
 *
 * Esta clase contiene métodos para convertir entre cadenas, UUIDs y arreglos de bytes,
 * así como otros métodos de utilidad.
 */
public class Utilidades {

    // -------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------

    /**
     * @brief Convierte una cadena a un arreglo de bytes.
     *
     * @param texto Cadena a convertir.
     * @return Arreglo de bytes correspondiente a la cadena.
     */
    public static byte[] stringToBytes(String texto) {
        return texto.getBytes();
    } // ()

    // -------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------

    /**
     * @brief Convierte una cadena de 16 caracteres a un UUID.
     *
     * @param uuid Cadena de 16 caracteres que representa un UUID.
     * @return El UUID correspondiente.
     * @throws Error Si la cadena no tiene 16 caracteres.
     */
    public static UUID stringToUUID(String uuid) {
        if (uuid.length() != 16) {
            throw new Error("stringUUID: string no tiene 16 caracteres ");
        }
        byte[] comoBytes = uuid.getBytes();
        String masSignificativo = uuid.substring(0, 8);
        String menosSignificativo = uuid.substring(8, 16);
        UUID res = new UUID(Utilidades.bytesToLong(masSignificativo.getBytes()), Utilidades.bytesToLong(menosSignificativo.getBytes()));
        return res;
    } // ()

    // -------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------

    /**
     * @brief Convierte un UUID a una cadena.
     *
     * @param uuid El UUID a convertir.
     * @return La representación de cadena del UUID.
     */
    public static String uuidToString(UUID uuid) {
        return bytesToString(dosLongToBytes(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits()));
    } // ()

    // -------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------

    /**
     * @brief Convierte un UUID a una cadena hexadecimal.
     *
     * @param uuid El UUID a convertir.
     * @return La representación hexadecimal del UUID.
     */
    public static String uuidToHexString(UUID uuid) {
        return bytesToHexString(dosLongToBytes(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits()));
    } // ()

    // -------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------

    /**
     * @brief Convierte un arreglo de bytes a una cadena.
     *
     * @param bytes Arreglo de bytes a convertir.
     * @return La cadena correspondiente al arreglo de bytes.
     */
    public static String bytesToString(byte[] bytes) {
        if (bytes == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append((char) b);
        }
        return sb.toString();
    }

    // -------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------

    /**
     * @brief Convierte dos longitudes a un arreglo de bytes.
     *
     * @param masSignificativos Longitud más significativa.
     * @param menosSignificativos Longitud menos significativa.
     * @return Arreglo de bytes que representa las dos longitudes.
     */
    public static byte[] dosLongToBytes(long masSignificativos, long menosSignificativos) {
        ByteBuffer buffer = ByteBuffer.allocate(2 * Long.BYTES);
        buffer.putLong(masSignificativos);
        buffer.putLong(menosSignificativos);
        return buffer.array();
    }

    // -------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------

    /**
     * @brief Convierte un arreglo de bytes a un entero.
     *
     * @param bytes Arreglo de bytes a convertir.
     * @return El entero correspondiente.
     */
    public static int bytesToInt(byte[] bytes) {
        return new BigInteger(bytes).intValue();
    }

    // -------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------

    /**
     * @brief Convierte un arreglo de bytes a un long.
     *
     * @param bytes Arreglo de bytes a convertir.
     * @return El long correspondiente.
     */
    public static long bytesToLong(byte[] bytes) {
        return new BigInteger(bytes).longValue();
    }

    // -------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------

    /**
     * @brief Convierte un arreglo de bytes a un entero, manejando excepciones.
     *
     * @param bytes Arreglo de bytes a convertir.
     * @return El entero correspondiente.
     * @throws Error Si el arreglo de bytes es nulo o tiene más de 4 bytes.
     */
    public static int bytesToIntOK(byte[] bytes) {
        if (bytes == null) {
            return 0;
        }

        if (bytes.length > 4) {
            throw new Error("demasiados bytes para pasar a int ");
        }
        int res = 0;

        for (byte b : bytes) {
            res = (res << 8) + (b & 0xFF);
        }

        if ((bytes[0] & 0x8) != 0) {
            res = -(~(byte) res) - 1; // complemento a 2
        }

        return res;
    } // ()

    // -------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------

    /**
     * @brief Convierte un arreglo de bytes a una cadena hexadecimal.
     *
     * @param bytes Arreglo de bytes a convertir.
     * @return La representación hexadecimal del arreglo de bytes.
     */
    public static String bytesToHexString(byte[] bytes) {
        if (bytes == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
            sb.append(':');
        }
        return sb.toString();
    } // ()
} // class Utilidades
