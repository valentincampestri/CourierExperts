package com.courierexperts.demo.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.widget.Toast;




public class ClipboardUtils {

    






    public static void copyToClipboard(Context context, String label, String text) {
        if (context == null || text == null) {
            return;
        }

        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard != null) {
            ClipData clip = ClipData.newPlainText(label, text);
            clipboard.setPrimaryClip(clip);
            
            
            Toast.makeText(context, "✓ Copiado al portapapeles", Toast.LENGTH_SHORT).show();
        }
    }

    





    public static void copyToClipboard(Context context, String text) {
        copyToClipboard(context, "Texto", text);
    }
}
