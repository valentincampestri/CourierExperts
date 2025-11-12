package com.courierexperts.demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.courierexperts.demo.data.repository.PurchaseRepository;
import com.google.android.material.textfield.TextInputEditText;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class NewPurchaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nueva_compra);

        setupBottomBar();

        View btnCancelar = findViewById(R.id.btnCancelarnuevaCompra);
        View btnGuardar  = findViewById(R.id.btnGuardarNuevaCompra);
        TextInputEditText etStore = findViewById(R.id.etStoreName);
        TextInputEditText etOrder = findViewById(R.id.etOrderId);

        if (btnCancelar != null) {
            btnCancelar.setOnClickListener(v -> finish());
        }
        if (btnGuardar != null) {
            btnGuardar.setOnClickListener(v -> {
                String store = etStore != null && etStore.getText() != null ? etStore.getText().toString().trim() : "";
                String order = etOrder != null && etOrder.getText() != null ? etOrder.getText().toString().trim() : "";
                if (store.isEmpty() || order.isEmpty()) {
                    Toast.makeText(this, "Completa Tienda y Nro Tracking", Toast.LENGTH_SHORT).show();
                    return;
                }

                String nowIso = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'") {{ setTimeZone(java.util.TimeZone.getTimeZone("UTC")); }}.format(new java.util.Date());
                boolean online = isOnline();
                new PurchaseRepository(this).createLocalAndSync(store, order, nowIso);

                Toast.makeText(this, online ? "Compra guardada" : "Guardado local, se sincronizará luego", Toast.LENGTH_SHORT).show();
                // Volver a lista de compras
                startActivity(new Intent(NewPurchaseActivity.this, PurchasesActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
                finish();
            });
        }
    }

    private void setupBottomBar() {
        BottomNavigationView bottom = findViewById(R.id.bottomNav);
        if (bottom == null) return;

        // No seleccionamos nada en esta pantalla (no hay item central)
        // bottom.setSelectedItemId(...);  // <- intencionalmente NO se usa

        bottom.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    startActivity(new Intent(NewPurchaseActivity.this, HomeActivity.class));
                    return true;
                } else if (id == R.id.nav_add) {
                    // Ya estamos en Nueva Compra; confirmamos selección.
                    return true;
                } else if (id == R.id.nav_profile) {
                    startActivity(new Intent(NewPurchaseActivity.this, ProfileActivity.class));
                    return true;
                }
                return false;
            }
        });
    }

    private boolean isOnline() {
        try {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            if (cm == null) return false;
            if (android.os.Build.VERSION.SDK_INT >= 23) {
                Network n = cm.getActiveNetwork();
                if (n == null) return false;
                NetworkCapabilities caps = cm.getNetworkCapabilities(n);
                return caps != null && (caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                        || caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                        || caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
            } else {
                android.net.NetworkInfo info = cm.getActiveNetworkInfo();
                return info != null && info.isConnected();
            }
        } catch (Exception e) { return false; }
    }
}
