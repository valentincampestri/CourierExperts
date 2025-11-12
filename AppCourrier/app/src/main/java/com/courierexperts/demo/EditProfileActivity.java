package com.courierexperts.demo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class EditProfileActivity extends AppCompatActivity {

    private static final String PREFS = "profile_prefs";
    private static final String KEY_SELECTED_DEPOSIT_ID = "selected_deposit_id";

    private final List<com.courierexperts.demo.data.local.entity.DepositEntity> depositsCache = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.perfil_datos_activity);

        View btnCancelar = findViewById(R.id.btnCancelarPerfil);
        View btnGuardar  = findViewById(R.id.btnGuardardatosPerfil);
        Spinner spinner   = findViewById(R.id.spinnerDeposito);

        if (btnCancelar != null) {
            btnCancelar.setOnClickListener(v -> finish());
        }
        if (btnGuardar != null) {
            btnGuardar.setOnClickListener(v -> {
                // Validación de dirección (5..80 chars)
                View etDirView = findViewById(R.id.etDireccionPerfil);
                if (etDirView instanceof com.google.android.material.textfield.TextInputEditText) {
                    String addr = ((com.google.android.material.textfield.TextInputEditText) etDirView).getText() != null ? ((com.google.android.material.textfield.TextInputEditText) etDirView).getText().toString().trim() : "";
                    if (addr.length() < 5 || addr.length() > 80) {
                        // set error en su TextInputLayout padre si existe
                        View parent = (View) etDirView.getParent().getParent();
                        if (parent instanceof com.google.android.material.textfield.TextInputLayout) {
                            ((com.google.android.material.textfield.TextInputLayout) parent).setError("La dirección debe tener entre 5 y 80 caracteres");
                        }
                        return;
                    } else {
                        View parent = (View) etDirView.getParent().getParent();
                        if (parent instanceof com.google.android.material.textfield.TextInputLayout) {
                            ((com.google.android.material.textfield.TextInputLayout) parent).setError(null);
                        }
                        // Persistir en Room
                        new com.courierexperts.demo.data.repository.UserProfileRepository(this).updateAddress(addr);
                    }
                }

                // Validación de teléfono (6..20, solo + y dígitos)
                View etPhoneView = findViewById(R.id.etTelefonoPerfil);
                if (etPhoneView instanceof com.google.android.material.textfield.TextInputEditText) {
                    String phone = ((com.google.android.material.textfield.TextInputEditText) etPhoneView).getText() != null ? ((com.google.android.material.textfield.TextInputEditText) etPhoneView).getText().toString().trim() : "";
                    boolean allowed = phone.matches("[+0-9]{6,20}");
                    if (!allowed) {
                        View parent = (View) etPhoneView.getParent().getParent();
                        if (parent instanceof com.google.android.material.textfield.TextInputLayout) {
                            ((com.google.android.material.textfield.TextInputLayout) parent).setError("Teléfono inválido (usar solo + y dígitos, 6 a 20)");
                        }
                        return;
                    } else {
                        View parent = (View) etPhoneView.getParent().getParent();
                        if (parent instanceof com.google.android.material.textfield.TextInputLayout) {
                            ((com.google.android.material.textfield.TextInputLayout) parent).setError(null);
                        }
                        new com.courierexperts.demo.data.repository.UserProfileRepository(this).updatePhone(phone);
                    }
                }
                if (spinner != null && spinner.getAdapter() != null) {
                    int pos = spinner.getSelectedItemPosition();
                    if (pos >= 0 && pos < depositsCache.size()) {
                        long id = depositsCache.get(pos).id;
                        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
                        prefs.edit().putLong(KEY_SELECTED_DEPOSIT_ID, id).apply();
                    }
                }
                startActivity(new Intent(EditProfileActivity.this, HomeActivity.class));
            });
        }

        // Spinner de Depósito desde Room + restauración
        if (spinner != null) {
            com.courierexperts.demo.data.repository.DepositRepository repo = new com.courierexperts.demo.data.repository.DepositRepository(this);
            repo.observeDeposits().observe(this, list -> {
                depositsCache.clear();
                if (list != null) depositsCache.addAll(list);

                List<String> names = new ArrayList<>();
                for (com.courierexperts.demo.data.local.entity.DepositEntity d : depositsCache) names.add(d.name);

                ArrayAdapter<String> a = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, names);
                a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(a);

                SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
                long savedId = prefs.getLong(KEY_SELECTED_DEPOSIT_ID, -1L);
                int sel = 0;
                if (savedId != -1L) {
                    for (int i = 0; i < depositsCache.size(); i++) {
                        if (depositsCache.get(i).id == savedId) { sel = i; break; }
                    }
                }
                if (!names.isEmpty()) spinner.setSelection(sel);
            });
        }

        // Cargar dirección actual en el campo
        View etDirView = findViewById(R.id.etDireccionPerfil);
        if (etDirView instanceof com.google.android.material.textfield.TextInputEditText) {
            com.courierexperts.demo.data.repository.UserProfileRepository upRepo = new com.courierexperts.demo.data.repository.UserProfileRepository(this);
            upRepo.observeProfile().observe(this, profile -> {
                String addr = (profile != null && profile.address != null) ? profile.address : "";
                ((com.google.android.material.textfield.TextInputEditText) etDirView).setText(addr);
            });
        }

        // Bottom bar
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
