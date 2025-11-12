package com.courierexperts.demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.perfil_activity);

        setupBottomBar(R.id.nav_profile);

        View cardCuenta = findViewById(R.id.cardCuenta);
        if (cardCuenta != null) {
            cardCuenta.setOnClickListener(v ->
                    startActivity(new Intent(ProfileActivity.this, EditProfileActivity.class))
            );
        }

        // Si este layout tiene FAB, manejá el click (opcional)
        View fab = findViewById(R.id.fabAdd);
        if (fab != null) {
            fab.setOnClickListener(v ->
                    startActivity(new Intent(ProfileActivity.this, NewPurchaseActivity.class))
            );
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
                    startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
                    return true;
                } else if (id == R.id.nav_add) {
                    startActivity(new Intent(ProfileActivity.this, NewPurchaseActivity.class));
                    return true;
                } else if (id == R.id.nav_profile) {
                    // ya estás en Perfil
                    return true;
                }
                return false;
            }
        });
    }
}
