
package com.example.usuario_upv.proyecto3a;

import java.util.Arrays;

// -----------------------------------------------------------------------------------
// @author: Jordi Bataller i Mascarell
// -----------------------------------------------------------------------------------
public class TramaIBeacon {
    private byte[] prefijo = null; // 9 bytes
    private byte[] uuid = null; // 16 bytes
    private byte[] major = null; // 2 bytes
    private byte[] minor = null; // 2 bytes
    private byte txPower = 0; // 1 byte

    private byte[] losBytes;

    private byte[] advFlags = null; // 3 bytes
    private byte[] advHeader = null; // 2 bytes
    private byte[] companyID = new byte[2]; // 2 bytes
    private byte iBeaconType = 0 ; // 1 byte
    private byte iBeaconLength = 0 ; // 1 byte

    private boolean noadvFlags;

    // -------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------
    public byte[] getPrefijo() {
        return prefijo;
    }

    // -------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------
    public byte[] getUUID() {
        return uuid;
    }

    // -------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------
    public byte[] getMajor() {
        return major;
    }

    // -------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------
    public byte[] getMinor() {
        return minor;
    }

    // -------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------
    public byte getTxPower() {
        return txPower;
    }

    // -------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------
    public byte[] getLosBytes() {
        return losBytes;
    }

    // -------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------
    public byte[] getAdvFlags() {
        return advFlags;
    }

    // -------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------
    public byte[] getAdvHeader() {
        return advHeader;
    }

    // -------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------
    public byte[] getCompanyID() {
        return companyID;
    }

    // -------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------
    public byte getiBeaconType() {
        return iBeaconType;
    }

    // -------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------
    public byte getiBeaconLength() {
        return iBeaconLength;
    }

    // -------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------
    public TramaIBeacon(byte[] bytes ) {
        this.losBytes = bytes;

        // noadvFlags para cuando el beacon detectado no transmita los primeros 3 bytes dedicados a advFlags (por experiencia) 0x02, 0x01, 0x06

        if( losBytes[0] == 02 && losBytes[1] == 01 && losBytes[2] == 06){
            noadvFlags = false;
        }else{
            noadvFlags = true;
        }

        if(noadvFlags){
            prefijo = Arrays.copyOfRange(losBytes, 0, 5+1 ); // 6 bytes
            uuid = Arrays.copyOfRange(losBytes, 6, 21+1 ); // 16 bytes
            major = Arrays.copyOfRange(losBytes, 22, 23+1 ); // 2 bytes
            minor = Arrays.copyOfRange(losBytes, 24, 25+1 ); // 2 bytes
            txPower = losBytes[ 26 ]; // 1 byte

            advHeader = Arrays.copyOfRange( prefijo, 0, 1+1 ); // 2 bytes
            companyID = Arrays.copyOfRange( prefijo, 2, 3+1 ); // 2 bytes
            iBeaconType = prefijo[ 4 ]; // 1 byte
            iBeaconLength = prefijo[ 5 ]; // 1 byte
        }else{
            prefijo = Arrays.copyOfRange(losBytes, 0, 8+1 ); // 9 bytes
            uuid = Arrays.copyOfRange(losBytes, 9, 24+1 ); // 16 bytes
            major = Arrays.copyOfRange(losBytes, 25, 26+1 ); // 2 bytes
            minor = Arrays.copyOfRange(losBytes, 27, 28+1 ); // 2 bytes
            txPower = losBytes[ 29 ]; // 1 byte

            advFlags = Arrays.copyOfRange( prefijo, 0, 2+1 ); // 3 bytes
            advHeader = Arrays.copyOfRange( prefijo, 3, 4+1 ); // 2 bytes
            companyID = Arrays.copyOfRange( prefijo, 5, 6+1 ); // 2 bytes
            iBeaconType = prefijo[ 7 ]; // 1 byte
            iBeaconLength = prefijo[ 8 ]; // 1 byte
        }
    } // ()
} // class
// -----------------------------------------------------------------------------------
// -----------------------------------------------------------------------------------
// -----------------------------------------------------------------------------------
// -----------------------------------------------------------------------------------


