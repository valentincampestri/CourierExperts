package com.courierexperts.demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ShipmentsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_envios);
        setupBottomBar();

        View card1 = findViewById(R.id.cardPaquete);
        View card2 = findViewById(R.id.cardPaquete2);
        if (card1 != null) card1.setOnClickListener(v -> {
            Intent i = new Intent(ShipmentsActivity.this, ShipmentDetailActivity.class);
            i.putExtra("shipmentId", 5);
            startActivity(i);
        });
        if (card2 != null) card2.setOnClickListener(v -> {
            Intent i = new Intent(ShipmentsActivity.this, ShipmentDetailActivity.class);
            i.putExtra("shipmentId", 4);
            startActivity(i);
        });
    }

    private void setupBottomBar() {
        BottomNavigationView bottom = findViewById(R.id.bottomNav);
        if (bottom == null) return;
        bottom.setSelectedItemId(R.id.nav_home);
        bottom.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    startActivity(new Intent(ShipmentsActivity.this, HomeActivity.class));
                    return true;
                } else if (id == R.id.nav_add) {
                    startActivity(new Intent(ShipmentsActivity.this, NewPurchaseActivity.class));
                    return true;
                } else if (id == R.id.nav_profile) {
                    startActivity(new Intent(ShipmentsActivity.this, ProfileActivity.class));
                    return true;
                }
                return false;
            }
        });
    }
}
