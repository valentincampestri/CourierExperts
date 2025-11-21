package com.courierexperts.demo.util;

import java.text.NumberFormat;
import java.util.Locale;




public class CurrencyUtils {

    






    public static String formatUSD(double amount) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.US);
        return formatter.format(amount) + " USD";
    }

    






    public static String formatAmount(double amount) {
        NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US);
        formatter.setMinimumFractionDigits(2);
        formatter.setMaximumFractionDigits(2);
        return formatter.format(amount);
    }

    





    public static double calculateShippingCost(double totalAmount) {
        return totalAmount * 0.20;
    }
}
