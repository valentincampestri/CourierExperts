package com.courierexperts.demo.util;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Utilidades para formatear precios y monedas.
 */
public class CurrencyUtils {

    /**
     * Formatea un precio con símbolo de dólar USD.
     * Ejemplo: 99.99 → "$99.99 USD"
     *
     * @param amount Monto a formatear
     * @return String formateado con símbolo de moneda
     */
    public static String formatUSD(double amount) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.US);
        return formatter.format(amount) + " USD";
    }

    /**
     * Formatea un precio sin símbolo pero con separadores.
     * Ejemplo: 1299.50 → "1,299.50"
     *
     * @param amount Monto a formatear
     * @return String formateado con separadores
     */
    public static String formatAmount(double amount) {
        NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US);
        formatter.setMinimumFractionDigits(2);
        formatter.setMaximumFractionDigits(2);
        return formatter.format(amount);
    }

    /**
     * Calcula el 20% de un monto (usado para costo de envío).
     *
     * @param totalAmount Monto total
     * @return 20% del monto
     */
    public static double calculateShippingCost(double totalAmount) {
        return totalAmount * 0.20;
    }
}
