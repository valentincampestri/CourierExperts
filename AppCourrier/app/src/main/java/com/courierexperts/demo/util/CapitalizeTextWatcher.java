package com.courierexperts.demo.util;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;




public class CapitalizeTextWatcher implements TextWatcher {
    
    private final EditText editText;
    private boolean isFormatting = false;
    
    public CapitalizeTextWatcher(EditText editText) {
        this.editText = editText;
    }
    
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        
    }
    
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        
    }
    
    @Override
    public void afterTextChanged(Editable s) {
        if (isFormatting || s.length() == 0) {
            return;
        }
        
        isFormatting = true;
        
        String original = s.toString();
        String capitalized = capitalizeWords(original);
        
        if (!original.equals(capitalized)) {
            int cursorPosition = editText.getSelectionStart();
            s.replace(0, s.length(), capitalized);
            
            
            if (cursorPosition <= capitalized.length()) {
                editText.setSelection(cursorPosition);
            }
        }
        
        isFormatting = false;
    }
    
    


    private String capitalizeWords(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        
        StringBuilder result = new StringBuilder(text.length());
        boolean capitalizeNext = true;
        
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            
            if (Character.isWhitespace(c)) {
                capitalizeNext = true;
                result.append(c);
            } else if (capitalizeNext) {
                result.append(Character.toUpperCase(c));
                capitalizeNext = false;
            } else {
                result.append(c);
            }
        }
        
        return result.toString();
    }
}
