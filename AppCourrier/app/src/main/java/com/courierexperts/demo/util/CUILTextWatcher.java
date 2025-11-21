package com.courierexperts.demo.util;

import android.text.Editable;
import android.text.TextWatcher;





public class CUILTextWatcher implements TextWatcher {

    private boolean isFormatting = false;

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (isFormatting) {
            return;
        }

        isFormatting = true;

        
        String digits = s.toString().replaceAll("[^0-9]", "");

        
        StringBuilder formatted = new StringBuilder();
        int length = digits.length();

        if (length > 0) {
            
            formatted.append(digits.substring(0, Math.min(2, length)));
            
            if (length > 2) {
                formatted.append("-");
                
                formatted.append(digits.substring(2, Math.min(10, length)));
                
                if (length > 10) {
                    formatted.append("-");
                    
                    formatted.append(digits.substring(10, Math.min(11, length)));
                }
            }
        }

        s.replace(0, s.length(), formatted.toString());

        isFormatting = false;
    }
}
