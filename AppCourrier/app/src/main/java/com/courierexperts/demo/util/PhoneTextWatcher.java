package com.courierexperts.demo.util;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;





public class PhoneTextWatcher implements TextWatcher {
    
    private final EditText editText;
    private String countryCode = "+54"; 
    private boolean isUpdating = false;
    
    public PhoneTextWatcher(EditText editText) {
        this.editText = editText;
    }
    
    


    public void setCountryCode(String code) {
        this.countryCode = code != null ? code : "+54";
    }
    
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (isUpdating) {
            return;
        }
        
        isUpdating = true;
        
        
        String original = s.toString();
        String digitsOnly = original.replaceAll("[^0-9]", "");
        
        
        String formatted = formatByCountry(digitsOnly, countryCode);
        
        
        if (!original.equals(formatted)) {
            s.replace(0, s.length(), formatted);
            
            try {
                if (formatted.length() > 0) {
                    editText.setSelection(formatted.length());
                }
            } catch (IndexOutOfBoundsException e) {
                
            }
        }
        
        isUpdating = false;
    }
    
    private String formatByCountry(String digits, String code) {
        if (digits.isEmpty()) {
            return "";
        }
        
        switch (code) {
            case "+54": 
                return formatArgentina(digits);
                
            case "+1": 
                return formatUSA(digits);
                
            case "+55": 
                return formatBrasil(digits);
                
            case "+56": 
                return formatChile(digits);
                
            case "+52": 
                return formatMexico(digits);
                
            case "+34": 
                return formatEspana(digits);
                
            default:
                
                return formatGeneric(digits);
        }
    }
    
    private String formatArgentina(String digits) {
        
        if (digits.length() <= 2) {
            return digits;
        } else if (digits.length() <= 6) {
            return digits.substring(0, 2) + " " + digits.substring(2);
        } else if (digits.length() <= 10) {
            return digits.substring(0, 2) + " " + 
                   digits.substring(2, 6) + "-" + 
                   digits.substring(6);
        } else {
            
            return digits.substring(0, 2) + " " + 
                   digits.substring(2, 6) + "-" + 
                   digits.substring(6, 10);
        }
    }
    
    private String formatUSA(String digits) {
        
        if (digits.length() <= 3) {
            return digits;
        } else if (digits.length() <= 6) {
            return "(" + digits.substring(0, 3) + ") " + digits.substring(3);
        } else if (digits.length() <= 10) {
            return "(" + digits.substring(0, 3) + ") " + 
                   digits.substring(3, 6) + "-" + 
                   digits.substring(6);
        } else {
            return "(" + digits.substring(0, 3) + ") " + 
                   digits.substring(3, 6) + "-" + 
                   digits.substring(6, 10);
        }
    }
    
    private String formatBrasil(String digits) {
        
        if (digits.length() <= 2) {
            return digits;
        } else if (digits.length() <= 7) {
            return "(" + digits.substring(0, 2) + ") " + digits.substring(2);
        } else if (digits.length() <= 11) {
            return "(" + digits.substring(0, 2) + ") " + 
                   digits.substring(2, 7) + "-" + 
                   digits.substring(7);
        } else {
            return "(" + digits.substring(0, 2) + ") " + 
                   digits.substring(2, 7) + "-" + 
                   digits.substring(7, 11);
        }
    }
    
    private String formatChile(String digits) {
        
        if (digits.length() <= 1) {
            return digits;
        } else if (digits.length() <= 5) {
            return digits.substring(0, 1) + " " + digits.substring(1);
        } else if (digits.length() <= 9) {
            return digits.substring(0, 1) + " " + 
                   digits.substring(1, 5) + " " + 
                   digits.substring(5);
        } else {
            return digits.substring(0, 1) + " " + 
                   digits.substring(1, 5) + " " + 
                   digits.substring(5, 9);
        }
    }
    
    private String formatMexico(String digits) {
        
        if (digits.length() <= 2) {
            return digits;
        } else if (digits.length() <= 6) {
            return digits.substring(0, 2) + " " + digits.substring(2);
        } else if (digits.length() <= 10) {
            return digits.substring(0, 2) + " " + 
                   digits.substring(2, 6) + " " + 
                   digits.substring(6);
        } else {
            return digits.substring(0, 2) + " " + 
                   digits.substring(2, 6) + " " + 
                   digits.substring(6, 10);
        }
    }
    
    private String formatEspana(String digits) {
        
        if (digits.length() <= 3) {
            return digits;
        } else if (digits.length() <= 5) {
            return digits.substring(0, 3) + " " + digits.substring(3);
        } else if (digits.length() <= 7) {
            return digits.substring(0, 3) + " " + 
                   digits.substring(3, 5) + " " + 
                   digits.substring(5);
        } else if (digits.length() <= 9) {
            return digits.substring(0, 3) + " " + 
                   digits.substring(3, 5) + " " + 
                   digits.substring(5, 7) + " " + 
                   digits.substring(7);
        } else {
            return digits.substring(0, 3) + " " + 
                   digits.substring(3, 5) + " " + 
                   digits.substring(5, 7) + " " + 
                   digits.substring(7, 9);
        }
    }
    
    private String formatGeneric(String digits) {
        
        StringBuilder formatted = new StringBuilder();
        for (int i = 0; i < digits.length() && i < 15; i++) {
            if (i > 0 && i % 3 == 0) {
                formatted.append(" ");
            }
            formatted.append(digits.charAt(i));
        }
        return formatted.toString();
    }
}
