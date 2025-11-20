package com.courierexperts.demo.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.widget.Toast;

/**
 * Utilidades para copiar texto al portapapeles.
 */
public class ClipboardUtils {

    /**
     * Copia un texto al portapapeles y muestra un Toast de confirmación.
     *
     * @param context Contexto de Android
     * @param label Etiqueta descriptiva (ej: "Tracking Number")
     * @param text Texto a copiar
     */
    public static void copyToClipboard(Context context, String label, String text) {
        if (context == null || text == null) {
            return;
        }

        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard != null) {
            ClipData clip = ClipData.newPlainText(label, text);
            clipboard.setPrimaryClip(clip);
            
            // Mostrar confirmación
            Toast.makeText(context, "✓ Copiado al portapapeles", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Versión simplificada que usa "Texto" como label por defecto.
     *
     * @param context Contexto de Android
     * @param text Texto a copiar
     */
    public static void copyToClipboard(Context context, String text) {
        copyToClipboard(context, "Texto", text);
    }
}
