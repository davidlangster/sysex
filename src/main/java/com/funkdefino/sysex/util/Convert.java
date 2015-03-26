package com.funkdefino.sysex.util;

/**
 * <p/>
 * <code>$Id: $</code>
 * @author Funkdefino (David M. Lang)
 * @version $Revision: $
 */
public final class Convert {

    /**
     * Converts a hexadecimal string to a byte array.
     * @param s the string.
     * @return the byte array.
     */
    public static byte[] getBytes(String s) {

        int n = s.length() / 2;
        byte[] data = new byte[n];
        for (int i = 0; i < n; i++) {
            data[i] = (byte)Integer.parseInt(s.substring(i*2, (i*2)+2), 16);
        }

        return data;

    }   // getBytes()

    /**
     * Hex-dumps a byte array.
     * @param data the array..
     */
    public static void dump(byte[] data) {

        StringBuilder sb = new StringBuilder();
        for (byte b : data)
            sb.append(String.format("%02X ", b));
        System.out.println(sb.toString());

    }   // dump()

}   // class Convert
