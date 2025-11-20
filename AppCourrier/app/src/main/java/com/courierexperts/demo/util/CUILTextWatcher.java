package com.courierexperts.demo.util;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * TextWatcher que formatea automáticamente el CUIL mientras se escribe.
 * Formato: NN-NNNNNNNN-N
 */
public class CUILTextWatcher implements TextWatcher {

    private boolean isFormatting = false;

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // No action needed
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // No action needed
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (isFormatting) {
            return;
        }

        isFormatting = true;

        // Remover todos los guiones
        String digits = s.toString().replaceAll("[^0-9]", "");

        // Formatear según la cantidad de dígitos
        StringBuilder formatted = new StringBuilder();
        int length = digits.length();

        if (length > 0) {
            // Primeros 2 dígitos
            formatted.append(digits.substring(0, Math.min(2, length)));
            
            if (length > 2) {
                formatted.append("-");
                // Siguientes 8 dígitos
                formatted.append(digits.substring(2, Math.min(10, length)));
                
                if (length > 10) {
                    formatted.append("-");
                    // Último dígito
                    formatted.append(digits.substring(10, Math.min(11, length)));
                }
            }
        }

        s.replace(0, s.length(), formatted.toString());

        isFormatting = false;
    }
}
