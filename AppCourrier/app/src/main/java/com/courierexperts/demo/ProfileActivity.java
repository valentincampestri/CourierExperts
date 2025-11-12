package com.courierexperts.demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;

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

        // Hacer clickeable el bloque de dirección para editar
        View direccion = findViewById(R.id.direccion);
        if (direccion != null) {
            direccion.setOnClickListener(v ->
                    startActivity(new Intent(ProfileActivity.this, EditProfileActivity.class))
            );
        }

        // Observa dirección y la muestra en tvDireccionLabel
        final TextView tv = findViewById(R.id.tvDireccion);
        if (tv != null) {
            com.courierexperts.demo.data.repository.UserProfileRepository repo = new com.courierexperts.demo.data.repository.UserProfileRepository(this);
            repo.observeProfile().observe(this, profile -> {
                String addr = (profile != null && profile.address != null && !profile.address.trim().isEmpty())
                        ? profile.address.trim()
                        : "Sin dirección";
                tv.setText(addr);
            });
        }

        // Si este layout tiene FAB, manejá el click (opcional)
        View fab = findViewById(R.id.fabAdd);
        if (fab != null) {
            fab.setOnClickListener(v ->
                    startActivity(new Intent(ProfileActivity.this, NewPurchaseActivity.class))
            );
        }

        // Teléfono: clic para editar y binding del valor persistido
        View telefono = findViewById(R.id.telefono);
        if (telefono != null) {
            telefono.setOnClickListener(v ->
                    startActivity(new Intent(ProfileActivity.this, EditProfileActivity.class))
            );
        }
        final TextView tvTel = findViewById(R.id.tvTelefono);
        if (tvTel != null) {
            com.courierexperts.demo.data.repository.UserProfileRepository repo2 = new com.courierexperts.demo.data.repository.UserProfileRepository(this);
            repo2.observeProfile().observe(this, profile -> {
                String phone = (profile != null && profile.phone != null && !profile.phone.trim().isEmpty())
                        ? profile.phone.trim()
                        : "";
                tvTel.setText(phone);
            });
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









