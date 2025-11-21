package com.courierexperts.demo.util;

public class ValidationUtils {

    public static boolean isValidCUIL(String cuil) {
        if (cuil == null || cuil.isEmpty()) {
            return false;
        }

        String cuilClean = cuil.replace("-", "").trim();

        if (cuilClean.length() != 11 || !cuilClean.matches("\\d{11}")) {
            return false;
        }

        int xy = Integer.parseInt(cuilClean.substring(0, 2));
        String dni = cuilClean.substring(2, 10);
        int digitoVerificador = Integer.parseInt(cuilClean.substring(10, 11));

        if (xy != 20 && xy != 23 && xy != 24 && xy != 27 && xy != 30 && xy != 33 && xy != 34) {
            return false;
        }

        int[] multiplicadores = {5, 4, 3, 2, 7, 6, 5, 4, 3, 2};
        int suma = 0;

        suma += (xy / 10) * multiplicadores[0];
        suma += (xy % 10) * multiplicadores[1];

        for (int i = 0; i < 8; i++) {
            suma += Character.getNumericValue(dni.charAt(i)) * multiplicadores[i + 2];
        }

        int resto = suma % 11;
        int digitoCalculado = 11 - resto;

        if (digitoCalculado == 11) {
            digitoCalculado = 0;
        } else if (digitoCalculado == 10) {
            digitoCalculado = 9;
        }

        return digitoCalculado == digitoVerificador;
    }

    public static boolean hasValidCUILFormat(String cuil) {
        if (cuil == null) return false;
        return cuil.matches("\\d{2}-\\d{8}-\\d");
    }
}
