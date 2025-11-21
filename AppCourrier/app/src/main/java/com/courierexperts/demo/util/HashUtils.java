package com.courierexperts.demo.util;




public class HashUtils {

    






    public static long stableLongFromString(String s) {
        if (s == null) return System.currentTimeMillis();
        long h = 1125899906842597L; 
        for (int i = 0; i < s.length(); i++) {
            h = 31 * h + s.charAt(i);
        }
        return h & Long.MAX_VALUE; 
    }
}
