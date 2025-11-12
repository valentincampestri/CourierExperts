package com.courierexperts.demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupBottomBar(R.id.nav_home);

        View btnCompras = findViewById(R.id.btnCompras);
        View btnPaquetes = findViewById(R.id.btnPaquetes);
        View btnEnvios = findViewById(R.id.btnEnvios);

        if (btnCompras != null) {
            btnCompras.setOnClickListener(v ->
                    startActivity(new Intent(HomeActivity.this, PurchasesActivity.class)));
        }
        if (btnPaquetes != null) {
            btnPaquetes.setOnClickListener(v ->
                    startActivity(new Intent(HomeActivity.this, PackagesActivity.class)));
        }
        if (btnEnvios != null) {
            btnEnvios.setOnClickListener(v ->
                    startActivity(new Intent(HomeActivity.this, ShipmentsActivity.class)));
        }

        View tvVerDetallesHome = findViewById(R.id.tvVerDetallesHome);
        if (tvVerDetallesHome != null) {
            tvVerDetallesHome.setOnClickListener(v -> {
                Intent i = new Intent(HomeActivity.this, ShipmentDetailActivity.class);
                i.putExtra("shipmentId", 5);
                startActivity(i);
            });
        }

        // Si tu layout de Home tiene FAB (como Envíos), podés manejarlo acá:
        View fab = findViewById(R.id.fabAdd);
        if (fab != null) {
            fab.setOnClickListener(v ->
                    startActivity(new Intent(HomeActivity.this, NewPurchaseActivity.class)));
            // Cuando tengamos "Nuevo Envío", cambia a NewShipmentActivity.class si querés.
        }
    }

    private void setupBottomBar(int selectedItemId) {
        BottomNavigationView bottom = findViewById(R.id.bottomNav);
        if (bottom == null) return;

        bottom.setSelectedItemId(selectedItemId);
        bottom.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == bottom.getSelectedItemId()) return true;

                if (id == R.id.nav_home) {
                    // ya estás en Home
                    return true;
                } else if (id == R.id.nav_add) {
                    startActivity(new Intent(HomeActivity.this, NewPurchaseActivity.class));
                    return true;
                } else if (id == R.id.nav_profile) {
                    startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
                    return true;
                }
                return false;
            }
        });
    }
}
