package com.courierexperts.demo.util;

/**
 * Utilidades de validación para campos de formulario.
 */
public class ValidationUtils {

    /**
     * Valida el formato y dígito verificador de un CUIL argentino.
     * Formato esperado: NN-NNNNNNNN-N
     *
     * @param cuil String con el CUIL a validar
     * @return true si el CUIL es válido (formato y dígito verificador correcto)
     */
    public static boolean isValidCUIL(String cuil) {
        if (cuil == null || cuil.isEmpty()) {
            return false;
        }

        // Remover guiones para facilitar validación
        String cuilClean = cuil.replace("-", "").trim();

        // Debe tener exactamente 11 dígitos
        if (cuilClean.length() != 11 || !cuilClean.matches("\\d{11}")) {
            return false;
        }

        // Extraer partes
        int xy = Integer.parseInt(cuilClean.substring(0, 2));  // Tipo (20, 23, 24, 27, etc)
        String dni = cuilClean.substring(2, 10);                // DNI (8 dígitos)
        int digitoVerificador = Integer.parseInt(cuilClean.substring(10, 11));

        // Validar tipo (XY) - valores comunes en Argentina
        if (xy != 20 && xy != 23 && xy != 24 && xy != 27 && xy != 30 && xy != 33 && xy != 34) {
            return false;
        }

        // Calcular dígito verificador
        int[] multiplicadores = {5, 4, 3, 2, 7, 6, 5, 4, 3, 2};
        int suma = 0;

        // XY
        suma += (xy / 10) * multiplicadores[0];
        suma += (xy % 10) * multiplicadores[1];

        // DNI
        for (int i = 0; i < 8; i++) {
            suma += Character.getNumericValue(dni.charAt(i)) * multiplicadores[i + 2];
        }

        int resto = suma % 11;
        int digitoCalculado = 11 - resto;

        // Casos especiales
        if (digitoCalculado == 11) {
            digitoCalculado = 0;
        } else if (digitoCalculado == 10) {
            digitoCalculado = 9;
        }

        return digitoCalculado == digitoVerificador;
    }

    /**
     * Valida el formato básico de CUIL sin verificar el dígito.
     * Útil para validaciones menos estrictas.
     *
     * @param cuil String con el CUIL
     * @return true si tiene el formato correcto NN-NNNNNNNN-N
     */
    public static boolean hasValidCUILFormat(String cuil) {
        if (cuil == null) return false;
        return cuil.matches("\\d{2}-\\d{8}-\\d");
    }
}
