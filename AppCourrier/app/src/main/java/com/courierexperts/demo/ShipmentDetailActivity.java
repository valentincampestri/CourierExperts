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

import com.courierexperts.demo.databinding.ActivityEnviosBinding;
import com.courierexperts.demo.domain.StatusMapper;
import com.courierexperts.demo.ui.packages.PackageAdapter;
import com.courierexperts.demo.ui.shipments.ShipmentDetailUiState;
import com.courierexperts.demo.ui.shipments.ShipmentDetailViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ShipmentDetailActivity extends AppCompatActivity {

    private ActivityEnviosBinding b;
    private ShipmentDetailViewModel vm;
    private PackageAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityEnviosBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        adapter = new PackageAdapter();
        adapter.setSelectionEnabled(false);
        b.rvEnvios.setLayoutManager(new LinearLayoutManager(this));
        b.rvEnvios.setAdapter(adapter);
        adapter.setOnItemClickListener(item -> {
            Intent i = new Intent(ShipmentDetailActivity.this, PackageDetailActivity.class);
            i.putExtra("packageId", item.id);
            startActivity(i);
        });

        vm = new ViewModelProvider(this).get(ShipmentDetailViewModel.class);
        observeUiState();

        String shipmentFsId = getIntent().getStringExtra("shipmentFsId");
        long shipmentLocalId = getIntent().getLongExtra("shipmentLocalId", -1);
        if (shipmentFsId != null && !shipmentFsId.isEmpty()) {
            vm.loadByFirestoreId(shipmentFsId);
        } else {
            vm.loadByLocalId(shipmentLocalId);
        }

        setupBottomBar();
    }

    private void observeUiState() {
        vm.getUiState().observe(this, state -> {
            b.progressBar.setVisibility(android.view.View.GONE);
            b.tvStateMessage.setVisibility(android.view.View.GONE);

            if (state instanceof ShipmentDetailUiState.Loading) {
                b.progressBar.setVisibility(android.view.View.VISIBLE);
            } else if (state instanceof ShipmentDetailUiState.Success) {
                ShipmentDetailUiState.Success success = (ShipmentDetailUiState.Success) state;
                b.tvEnvios.setText(success.getShipment().title != null ? success.getShipment().title : "Envio");
                b.tvMensaje.setText(StatusMapper.labelShipment(success.getShipment().status));
                adapter.submit(success.getPackages());
                b.rvEnvios.setVisibility(android.view.View.VISIBLE);
            } else if (state instanceof ShipmentDetailUiState.NotFound) {
                b.tvStateMessage.setVisibility(android.view.View.VISIBLE);
                b.tvStateMessage.setText(R.string.shipment_detail_not_found);
                b.rvEnvios.setVisibility(android.view.View.GONE);
            } else if (state instanceof ShipmentDetailUiState.Error) {
                b.tvStateMessage.setVisibility(android.view.View.VISIBLE);
                b.tvStateMessage.setText(R.string.state_error_retry);
                b.rvEnvios.setVisibility(android.view.View.GONE);
                Toast.makeText(this, R.string.state_error_retry, Toast.LENGTH_LONG).show();
            }
        });
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
                    startActivity(new Intent(ShipmentDetailActivity.this, HomeActivity.class));
                    return true;
                } else if (id == R.id.nav_add) {
                    startActivity(new Intent(ShipmentDetailActivity.this, NewPurchaseActivity.class));
                    return true;
                } else if (id == R.id.nav_profile) {
                    startActivity(new Intent(ShipmentDetailActivity.this, ProfileActivity.class));
                    return true;
                }
                return false;
            }
        });
    }
}
