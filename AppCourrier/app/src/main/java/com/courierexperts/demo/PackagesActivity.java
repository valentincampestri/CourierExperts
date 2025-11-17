package com.courierexperts.demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.courierexperts.demo.databinding.ActivityPaquetesBinding;
import com.courierexperts.demo.ui.packages.PackageAdapter;
import com.courierexperts.demo.ui.packages.PackagesUiState;
import com.courierexperts.demo.ui.packages.PackagesViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class PackagesActivity extends AppCompatActivity {

    private ActivityPaquetesBinding b;
    private PackagesViewModel vm;
    private PackageAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        b = ActivityPaquetesBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        adapter = new PackageAdapter();
        b.rvPaquetes.setLayoutManager(new LinearLayoutManager(this));
        b.rvPaquetes.setAdapter(adapter);
        b.btnSolicitar.setEnabled(false);
        adapter.setOnSelectionChangeListener(count -> b.btnSolicitar.setEnabled(count > 0));

        adapter.setOnItemClickListener(item ->
                startActivity(new Intent(this, PackageDetailActivity.class)
                        .putExtra("packageId", item.id))
        );

        vm = new ViewModelProvider(this).get(PackagesViewModel.class);
        observeUiState();
        vm.refresh();

        b.btnSolicitar.setOnClickListener(v -> onSolicitarEnvio());

        setupBottomBar();
    }

    private void observeUiState() {
        vm.getUiState().observe(this, state -> {
            b.progressBar.setVisibility(android.view.View.GONE);
            b.tvStateMessage.setVisibility(android.view.View.GONE);
            if (state instanceof PackagesUiState.Loading) {
                b.progressBar.setVisibility(android.view.View.VISIBLE);
                b.rvPaquetes.setVisibility(android.view.View.GONE);
                b.btnSolicitar.setEnabled(false);
            } else if (state instanceof PackagesUiState.Success) {
                b.rvPaquetes.setVisibility(android.view.View.VISIBLE);
                adapter.submit(((PackagesUiState.Success) state).getPackages());
            } else if (state instanceof PackagesUiState.Empty) {
                b.tvStateMessage.setVisibility(android.view.View.VISIBLE);
                b.tvStateMessage.setText(R.string.packages_empty_message);
                b.rvPaquetes.setVisibility(android.view.View.GONE);
                b.btnSolicitar.setEnabled(false);
            } else if (state instanceof PackagesUiState.Error) {
                b.tvStateMessage.setVisibility(android.view.View.VISIBLE);
                b.tvStateMessage.setText(R.string.state_error_retry);
                b.rvPaquetes.setVisibility(android.view.View.GONE);
                b.btnSolicitar.setEnabled(false);
                Toast.makeText(this, R.string.state_error_retry, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupBottomBar() {
        BottomNavigationView bottom = b.bottomNav;
        if (bottom == null) return;

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

    private void onSolicitarEnvio() {
        java.util.List<Long> ids = adapter.getSelectedIds();
        if (ids == null || ids.isEmpty()) {
            android.widget.Toast.makeText(this, "Selecciona al menos un paquete", android.widget.Toast.LENGTH_SHORT).show();
            return;
        }

        android.widget.Toast.makeText(this, "Creando envio con " + ids.size() + " paquete(s)...", android.widget.Toast.LENGTH_SHORT).show();

        com.courierexperts.demo.data.repository.ShipmentRepository repo = new com.courierexperts.demo.data.repository.ShipmentRepository(this);
        repo.createShipment(ids, new com.courierexperts.demo.data.repository.ShipmentRepository.Callback() {
            @Override public void onSuccess(long shipmentId) {
                startActivity(new Intent(PackagesActivity.this, ShipmentsActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
            }
            @Override public void onHttpError(int code) {
                Toast.makeText(PackagesActivity.this, R.string.state_error_retry, Toast.LENGTH_LONG).show();
            }
            @Override public void onOffline() {
                Toast.makeText(PackagesActivity.this, R.string.state_error_retry, Toast.LENGTH_LONG).show();
            }
        });
    }
}
