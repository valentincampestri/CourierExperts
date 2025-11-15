package com.courierexperts.demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.courierexperts.demo.databinding.ActivityPurchaseDetailBinding;
import com.courierexperts.demo.domain.StatusMapper;
import com.courierexperts.demo.ui.purchases.PurchaseDetailUiState;
import com.courierexperts.demo.ui.purchases.PurchaseDetailViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class PurchaseDetailActivity extends AppCompatActivity {

    private ActivityPurchaseDetailBinding b;
    private PurchaseDetailViewModel vm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityPurchaseDetailBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        vm = new ViewModelProvider(this).get(PurchaseDetailViewModel.class);

        long id = getIntent().getLongExtra("purchaseId", 0L);
        observeUiState();
        vm.load(id);

        b.btnBack.setOnClickListener(v -> finish());
        setupBottomBar();
    }

    private void observeUiState() {
        vm.getUiState().observe(this, state -> {
            b.progressBar.setVisibility(android.view.View.GONE);
            b.tvStateMessage.setVisibility(android.view.View.GONE);
            b.tvDetail.setVisibility(android.view.View.GONE);

            if (state instanceof PurchaseDetailUiState.Loading) {
                b.progressBar.setVisibility(android.view.View.VISIBLE);
            } else if (state instanceof PurchaseDetailUiState.Success) {
                b.tvDetail.setVisibility(android.view.View.VISIBLE);
                renderDetail(((PurchaseDetailUiState.Success) state).getPurchase());
            } else if (state instanceof PurchaseDetailUiState.NotFound) {
                b.tvStateMessage.setVisibility(android.view.View.VISIBLE);
                b.tvStateMessage.setText(R.string.purchase_detail_not_found);
            } else if (state instanceof PurchaseDetailUiState.Error) {
                b.tvStateMessage.setVisibility(android.view.View.VISIBLE);
                b.tvStateMessage.setText(R.string.state_error_retry);
                Toast.makeText(this, R.string.state_error_retry, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void renderDetail(com.courierexperts.demo.data.local.entity.PurchaseEntity p) {
        if (p == null) return;
        String status = StatusMapper.labelPurchase(p.status);
        String detail = "Tienda: " + safe(p.storeName) + "\n" +
                "Orden: " + safe(p.orderId) + "\n" +
                "Descripción: " + safe(p.description) + "\n" +
                "Estado: " + status + "\n" +
                "Fecha: " + formatDate(p.createdAt);
        b.tvDetail.setText(detail);
        b.tvSub.setText(safe(p.storeName));
    }

    private static String safe(String s) { return s != null ? s : ""; }
    private static String formatDate(long epoch) {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault());
        return sdf.format(new java.util.Date(epoch));
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
                    startActivity(new Intent(PurchaseDetailActivity.this, HomeActivity.class));
                    return true;
                } else if (id == R.id.nav_add) {
                    startActivity(new Intent(PurchaseDetailActivity.this, NewPurchaseActivity.class));
                    return true;
                } else if (id == R.id.nav_profile) {
                    startActivity(new Intent(PurchaseDetailActivity.this, ProfileActivity.class));
                    return true;
                }
                return false;
            }
        });

        View fab = findViewById(R.id.fabAdd);
        if (fab != null) {
            fab.setOnClickListener(v ->
                    startActivity(new Intent(PurchaseDetailActivity.this, NewPurchaseActivity.class))
            );
        }
    }
}
