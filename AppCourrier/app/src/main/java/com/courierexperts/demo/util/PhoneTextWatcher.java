package com.courierexperts.demo.util;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

/**
 * TextWatcher que formatea automáticamente números de teléfono según el país.
 * El usuario solo escribe dígitos y el formato se aplica automáticamente.
 */
public class PhoneTextWatcher implements TextWatcher {
    
    private final EditText editText;
    private String countryCode = "+54"; // Por defecto Argentina
    private boolean isUpdating = false;
    
    public PhoneTextWatcher(EditText editText) {
        this.editText = editText;
    }
    
    /**
     * Actualizar el código de país para cambiar el formato
     */
    public void setCountryCode(String code) {
        this.countryCode = code != null ? code : "+54";
    }
    
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // No necesitamos hacer nada aquí
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // No necesitamos hacer nada aquí
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (isUpdating) {
            return;
        }
        
        isUpdating = true;
        
        // Extraer solo los dígitos
        String original = s.toString();
        String digitsOnly = original.replaceAll("[^0-9]", "");
        
        // Aplicar formato según el país
        String formatted = formatByCountry(digitsOnly, countryCode);
        
        // Si el formato cambió, actualizar el texto
        if (!original.equals(formatted)) {
            s.replace(0, s.length(), formatted);
            // Asegurar que la posición del cursor sea válida
            try {
                if (formatted.length() > 0) {
                    editText.setSelection(formatted.length());
                }
            } catch (IndexOutOfBoundsException e) {
                // Ignorar si hay error de posición del cursor
            }
        }
        
        isUpdating = false;
    }
    
    private String formatByCountry(String digits, String code) {
        if (digits.isEmpty()) {
            return "";
        }
        
        switch (code) {
            case "+54": // Argentina: XX XXXX-XXXX (10 dígitos)
                return formatArgentina(digits);
                
            case "+1": // USA/Canadá: (XXX) XXX-XXXX (10 dígitos)
                return formatUSA(digits);
                
            case "+55": // Brasil: (XX) XXXXX-XXXX (11 dígitos)
                return formatBrasil(digits);
                
            case "+56": // Chile: X XXXX XXXX (9 dígitos)
                return formatChile(digits);
                
            case "+52": // México: XX XXXX XXXX (10 dígitos)
                return formatMexico(digits);
                
            case "+34": // España: XXX XX XX XX (9 dígitos)
                return formatEspana(digits);
                
            default:
                // Formato genérico: grupos de 3-4 dígitos
                return formatGeneric(digits);
        }
    }
    
    private String formatArgentina(String digits) {
        // Formato: XX XXXX-XXXX
        if (digits.length() <= 2) {
            return digits;
        } else if (digits.length() <= 6) {
            return digits.substring(0, 2) + " " + digits.substring(2);
        } else if (digits.length() <= 10) {
            return digits.substring(0, 2) + " " + 
                   digits.substring(2, 6) + "-" + 
                   digits.substring(6);
        } else {
            // Máximo 10 dígitos
            return digits.substring(0, 2) + " " + 
                   digits.substring(2, 6) + "-" + 
                   digits.substring(6, 10);
        }
    }
    
    private String formatUSA(String digits) {
        // Formato: (XXX) XXX-XXXX
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
        // Formato: (XX) XXXXX-XXXX
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
        // Formato: X XXXX XXXX
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
        // Formato: XX XXXX XXXX
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
        // Formato: XXX XX XX XX
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
        // Formato genérico: XXX XXX XXX...
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
