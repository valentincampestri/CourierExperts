package com.courierexperts.demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class PackagesActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paquetes);
        setupBottomBar();

        View btnSolicitar = findViewById(R.id.btnSolicitar);
        if (btnSolicitar != null) {
            btnSolicitar.setOnClickListener(v -> startActivity(new Intent(PackagesActivity.this, ShipmentsActivity.class)));
        }
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
                    startActivity(new Intent(PackagesActivity.this, HomeActivity.class));
                    return true;
                } else if (id == R.id.nav_add) {
                    startActivity(new Intent(PackagesActivity.this, NewPurchaseActivity.class));
                    return true;
                } else if (id == R.id.nav_profile) {
                    startActivity(new Intent(PackagesActivity.this, ProfileActivity.class));
                    return true;
                }
                return false;
            }
        });
    }
}
