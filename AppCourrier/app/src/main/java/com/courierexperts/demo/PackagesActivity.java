package com.courierexperts.demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.courierexperts.demo.databinding.ActivityPaquetesBinding;
import com.courierexperts.demo.ui.packages.PackageAdapter;
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
        // Estado inicial: deshabilitado hasta que haya selección
        b.btnSolicitar.setEnabled(false);
        adapter.setOnSelectionChangeListener(count -> b.btnSolicitar.setEnabled(count > 0));

        adapter.setOnItemClickListener(item ->
                startActivity(new Intent(this, PackageDetailActivity.class)
                        .putExtra("packageId", item.id))
        );

        vm = new ViewModelProvider(this).get(PackagesViewModel.class);
        vm.getPackages().observe(this, list -> {
            adapter.submit(list);
            boolean empty = (list == null || list.isEmpty());
            b.tvEmptyPaquetes.setVisibility(empty ? android.view.View.VISIBLE : android.view.View.GONE);
            b.rvPaquetes.setVisibility(empty ? android.view.View.GONE : android.view.View.VISIBLE);
        });

        // CTA: ir a Envíos
        b.btnSolicitar.setOnClickListener(v -> onSolicitarEnvio());

        setupBottomBar();
    }

    private void setupBottomBar() {
        BottomNavigationView bottom = b.bottomNav;
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

    private void onSolicitarEnvio() {
        java.util.List<Long> ids = adapter.getSelectedIds();
        if (ids == null || ids.isEmpty()) {
            android.widget.Toast.makeText(this, "Seleccioná al menos un paquete", android.widget.Toast.LENGTH_SHORT).show();
            return;
        }

        android.widget.Toast.makeText(this, "Creando envío con " + ids.size() + " paquete(s)…", android.widget.Toast.LENGTH_SHORT).show();

        com.courierexperts.demo.data.repository.ShipmentRepository repo = new com.courierexperts.demo.data.repository.ShipmentRepository(this);
        repo.createShipment(ids, new com.courierexperts.demo.data.repository.ShipmentRepository.Callback() {
            @Override public void onSuccess(long shipmentId) {
                startActivity(new Intent(PackagesActivity.this, ShipmentsActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
            }
            @Override public void onHttpError(int code) {
                android.widget.Toast.makeText(PackagesActivity.this, "En este momento no hay servicio, intentar más tarde", android.widget.Toast.LENGTH_LONG).show();
            }
            @Override public void onOffline() {
                android.widget.Toast.makeText(PackagesActivity.this, "Sin internet. Intenta nuevamente cuando tengas conexión", android.widget.Toast.LENGTH_LONG).show();
            }
        });
    }
}

