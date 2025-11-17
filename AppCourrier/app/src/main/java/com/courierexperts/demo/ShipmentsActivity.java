package com.courierexperts.demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.courierexperts.demo.databinding.ActivityEnviosBinding;
import com.courierexperts.demo.ui.shipments.ShipmentAdapter;
import com.courierexperts.demo.ui.shipments.ShipmentsUiState;
import com.courierexperts.demo.ui.shipments.ShipmentsViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class ShipmentsActivity extends AppCompatActivity {

    private ActivityEnviosBinding b;
    private ShipmentsViewModel vm;
    private ShipmentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityEnviosBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        adapter = new ShipmentAdapter();
        b.rvEnvios.setLayoutManager(new LinearLayoutManager(this));
        b.rvEnvios.setAdapter(adapter);
        adapter.setOnItemClickListener(item -> {
            Intent i = new Intent(ShipmentsActivity.this, ShipmentDetailActivity.class);
            if (item.fsId != null && !item.fsId.isEmpty()) {
                i.putExtra("shipmentFsId", item.fsId);
            }
            i.putExtra("shipmentLocalId", item.id);
            startActivity(i);
        });

        vm = new ViewModelProvider(this).get(ShipmentsViewModel.class);
        observeUiState();
        vm.refresh();

        setupBottomBar();
    }

    private void observeUiState() {
        vm.getUiState().observe(this, state -> {
            b.progressBar.setVisibility(View.GONE);
            b.tvStateMessage.setVisibility(View.GONE);
            if (state instanceof ShipmentsUiState.Loading) {
                b.progressBar.setVisibility(View.VISIBLE);
                b.rvEnvios.setVisibility(View.GONE);
            } else if (state instanceof ShipmentsUiState.Success) {
                b.rvEnvios.setVisibility(View.VISIBLE);
                adapter.submit(((ShipmentsUiState.Success) state).getShipments());
            } else if (state instanceof ShipmentsUiState.Empty) {
                b.tvStateMessage.setVisibility(View.VISIBLE);
                b.tvStateMessage.setText(R.string.envios_empty);
                b.rvEnvios.setVisibility(View.GONE);
            } else if (state instanceof ShipmentsUiState.Error) {
                b.tvStateMessage.setVisibility(View.VISIBLE);
                b.tvStateMessage.setText(R.string.state_error_retry);
                b.rvEnvios.setVisibility(View.GONE);
                Toast.makeText(this, R.string.state_error_retry, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupBottomBar() {
        b.bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    startActivity(new Intent(ShipmentsActivity.this, HomeActivity.class));
                    return true;
                } else if (id == R.id.nav_add) {
                    startActivity(new Intent(ShipmentsActivity.this, NewPurchaseActivity.class));
                    return true;
                } else if (id == R.id.nav_profile) {
                    startActivity(new Intent(ShipmentsActivity.this, ProfileActivity.class));
                    return true;
                }
                return false;
            }
        });
    }
}
