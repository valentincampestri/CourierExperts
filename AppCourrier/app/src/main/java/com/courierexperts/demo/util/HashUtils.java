package com.courierexperts.demo.util;

/**
 * Utilidades para generar IDs estables a partir de strings.
 */
public class HashUtils {

    /**
     * Genera un long positivo y estable a partir de un string.
     * Útil para generar IDs locales únicos basados en IDs de Firestore.
     *
     * @param s String de entrada (típicamente un ID de Firestore)
     * @return long positivo y determinista basado en el string
     */
    public static long stableLongFromString(String s) {
        if (s == null) return System.currentTimeMillis();
        long h = 1125899906842597L; // prime number
        for (int i = 0; i < s.length(); i++) {
            h = 31 * h + s.charAt(i);
        }
        return h & Long.MAX_VALUE; // ensure positive
    }
}
