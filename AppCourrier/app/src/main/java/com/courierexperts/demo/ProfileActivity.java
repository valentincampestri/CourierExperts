package com.courierexperts.demo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.courierexperts.demo.databinding.PerfilActivityBinding;
import com.courierexperts.demo.ui.profile.ProfileUiState;
import com.courierexperts.demo.ui.profile.ProfileViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class ProfileActivity extends AppCompatActivity {

    private PerfilActivityBinding binding;
    private ProfileViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = PerfilActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        observeState();
        observeLogout();

        binding.cardCuenta.setOnClickListener(v ->
                startActivity(new Intent(ProfileActivity.this, EditProfileActivity.class))
        );
        binding.direccion.setOnClickListener(v ->
                startActivity(new Intent(ProfileActivity.this, EditProfileActivity.class))
        );
        binding.telefono.setOnClickListener(v ->
                startActivity(new Intent(ProfileActivity.this, EditProfileActivity.class))
        );
        binding.cardLogout.setOnClickListener(v -> showLogoutDialog());
        binding.switchNotificaciones.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked && Build.VERSION.SDK_INT >= 33) {
                if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1001);
                }
            }
            viewModel.updateNotifications(isChecked);
        });

        setupBottomBar();
    }

    private void observeState() {
        viewModel.getUiState().observe(this, state -> {
            binding.progressBar.setVisibility(android.view.View.GONE);
            binding.tvStateMessage.setVisibility(android.view.View.GONE);

            if (state instanceof ProfileUiState.Loading) {
                binding.progressBar.setVisibility(android.view.View.VISIBLE);
            } else if (state instanceof ProfileUiState.Success) {
                renderProfile(((ProfileUiState.Success) state).getProfile());
            } else if (state instanceof ProfileUiState.Error) {
                binding.tvStateMessage.setVisibility(android.view.View.VISIBLE);
                binding.tvStateMessage.setText(R.string.state_error_retry);
                Toast.makeText(this, R.string.state_error_retry, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void observeLogout() {
        viewModel.getLogoutEvents().observe(this, success -> {
            if (Boolean.TRUE.equals(success)) {
                Intent i = new Intent(ProfileActivity.this, WelcomeActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
            }
        });
    }

    private void showLogoutDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Cerrar sesión")
                .setMessage("¿Seguro que querés cerrar sesión?")
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Sí, cerrar sesión", (d, w) -> viewModel.logout())
                .show();
    }

    private void renderProfile(com.courierexperts.demo.data.local.entity.UserProfileEntity profile) {
        if (profile == null) return;
        String name = profile.name != null ? profile.name.trim() : "";
        binding.tvSaludo.setText(name.isEmpty() ? "Hola" : ("Hola, " + name));
        binding.tvMail.setText(profile.email != null ? profile.email.trim() : "");
        binding.tvDireccion.setText(profile.address != null && !profile.address.trim().isEmpty()
                ? profile.address.trim()
                : getString(R.string.profile_placeholder_address));
        binding.tvTelefono.setText(profile.phone != null && !profile.phone.trim().isEmpty()
                ? profile.phone.trim()
                : getString(R.string.profile_placeholder_phone));

        boolean enabled = profile.notificationsEnabled != null && profile.notificationsEnabled;
        if (binding.switchNotificaciones.isChecked() != enabled) {
            binding.switchNotificaciones.setChecked(enabled);
        }
    }

    private void setupBottomBar() {
        BottomNavigationView bottom = binding.bottomNav;
        if (bottom == null) return;

        bottom.setSelectedItemId(R.id.nav_profile);
        bottom.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_home) {
                    startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
                    return true;
                } else if (id == R.id.nav_add) {
                    startActivity(new Intent(ProfileActivity.this, NewPurchaseActivity.class));
                    return true;
                } else if (id == R.id.nav_profile) {
                    return true;
                }
                return false;
            }
        });
    }
}
