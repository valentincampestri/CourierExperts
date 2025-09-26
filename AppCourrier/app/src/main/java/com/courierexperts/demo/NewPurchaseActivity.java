package com.courierexperts.demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class NewPurchaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nueva_compra);
        setupBottomBar();

        View btnCancelar = findViewById(R.id.btnCancelarnuevaCompra);
        View btnGuardar = findViewById(R.id.btnGuardarNuevaCompra);
        if (btnCancelar != null) btnCancelar.setOnClickListener(v -> finish());
        if (btnGuardar != null) btnGuardar.setOnClickListener(v -> startActivity(new Intent(NewPurchaseActivity.this, HomeActivity.class)));
    }

    private void setupBottomBar() {
        BottomNavigationView bottom = findViewById(R.id.bottomNav);
        if (bottom == null) return;
        bottom.setSelectedItemId(R.id.nav_add);
        bottom.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    startActivity(new Intent(NewPurchaseActivity.this, HomeActivity.class));
                    return true;
                } else if (id == R.id.nav_add) {
                    return true;
                } else if (id == R.id.nav_profile) {
                    startActivity(new Intent(NewPurchaseActivity.this, ProfileActivity.class));
                    return true;
                }
                return false;
            }
        });
    }
}
