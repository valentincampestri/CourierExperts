package com.courierexperts.demo;

import android.app.Application;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

public class CourierApplication extends Application {

    private static final String TAG = "CourierApplication";

    @Override
    public void onCreate() {
        super.onCreate();

        // Inicializar Firebase
        try {
            FirebaseApp.initializeApp(this);
            Log.d(TAG, "✅ Firebase inicializado correctamente");
        } catch (Exception e) {
            Log.e(TAG, "❌ Error al inicializar Firebase", e);
        }

        // Habilitar persistencia de Firestore (modo offline)
        try {
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();

            // La persistencia ahora está habilitada por defecto en versiones recientes
            // Solo necesitamos obtener la instancia
            firestore.setFirestoreSettings(
                new FirebaseFirestoreSettings.Builder()
                    .build()
            );

            Log.d(TAG, "✅ Firestore configurado correctamente");
        } catch (Exception e) {
            Log.e(TAG, "❌ Error al configurar Firestore", e);
        }
    }
}

