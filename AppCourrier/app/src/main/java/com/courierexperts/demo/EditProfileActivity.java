package com.courierexperts.demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class EditProfileActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.perfil_datos_activity);

        View btnCancelar = findViewById(R.id.btnCancelarPerfil);
        View btnGuardar = findViewById(R.id.btnGuardardatosPerfil);
        if (btnCancelar != null) btnCancelar.setOnClickListener(v -> finish());
        if (btnGuardar != null) btnGuardar.setOnClickListener(v -> startActivity(new Intent(EditProfileActivity.this, HomeActivity.class)));

        BottomNavigationView bottom = findViewById(R.id.bottomNav);
        if (bottom != null) {
            bottom.setSelectedItemId(R.id.nav_profile);
            bottom.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    int id = item.getItemId();
                    if (id == R.id.nav_home) {
                        startActivity(new Intent(EditProfileActivity.this, HomeActivity.class));
                        return true;
                    } else if (id == R.id.nav_add) {
                        startActivity(new Intent(EditProfileActivity.this, NewPurchaseActivity.class));
                        return true;
                    } else if (id == R.id.nav_profile) {
                        startActivity(new Intent(EditProfileActivity.this, ProfileActivity.class));
                        return true;
                    }
                    return false;
                }
            });
        }
    }
}
