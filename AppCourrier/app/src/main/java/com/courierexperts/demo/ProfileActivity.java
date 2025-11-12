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

        // Cerrar sesión
        View cardLogout = findViewById(R.id.cardLogout);
        if (cardLogout != null) {
            cardLogout.setOnClickListener(v -> {
                try { com.google.firebase.auth.FirebaseAuth.getInstance().signOut(); } catch (Exception ignored) {}
                Intent i = new Intent(ProfileActivity.this, WelcomeActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
            });
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
        // Bind saludo y email
        final TextView tvSaludo = findViewById(R.id.tvSaludo);
        final TextView tvMail = findViewById(R.id.tvMail);
        {
            com.courierexperts.demo.data.repository.UserProfileRepository repo3 = new com.courierexperts.demo.data.repository.UserProfileRepository(this);
            repo3.observeProfile().observe(this, profile -> {
                String name = (profile != null && profile.name != null) ? profile.name.trim() : "";
                if (tvSaludo != null) tvSaludo.setText(name.isEmpty() ? "Hola" : ("Hola, " + name));
                String email = (profile != null && profile.email != null) ? profile.email.trim() : "";
                if (tvMail != null) tvMail.setText(email);
            });
        }

        // Notificaciones: bind switch y solicitar permiso si aplica
        com.google.android.material.switchmaterial.SwitchMaterial sw = findViewById(R.id.switchNotificaciones);
        if (sw != null) {
            com.courierexperts.demo.data.repository.UserProfileRepository repo4 = new com.courierexperts.demo.data.repository.UserProfileRepository(this);
            repo4.observeProfile().observe(this, profile -> {
                boolean enabled = profile != null && profile.notificationsEnabled != null && profile.notificationsEnabled;
                if (sw.isChecked() != enabled) sw.setChecked(enabled);
            });
            sw.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked && android.os.Build.VERSION.SDK_INT >= 33) {
                    if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1001);
                    }
                }
                new com.courierexperts.demo.data.repository.UserProfileRepository(this).updateNotifications(isChecked);
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











