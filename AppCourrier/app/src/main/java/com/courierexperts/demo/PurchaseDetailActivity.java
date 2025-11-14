package com.courierexperts.demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.lifecycle.ViewModelProvider;

import com.courierexperts.demo.data.local.entity.PurchaseEntity;
import com.courierexperts.demo.data.repository.PurchaseRepository;

public class PurchaseDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_detail);

        View btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        long id = getIntent().getLongExtra("purchaseId", 0L);
        final android.widget.TextView tvSub = findViewById(R.id.tvSub);
        final android.widget.TextView tvDetail = findViewById(R.id.tvDetail);

        if (id > 0 && tvDetail != null) {
            PurchaseRepository repo = new PurchaseRepository(this);
            repo.observePurchaseById(id).observe(this, p -> {
                if (p == null) return;
                if (tvSub != null) tvSub.setText(p.storeName != null ? p.storeName : "");
                String date = formatDate(p.createdAt);
                String status = com.courierexperts.demo.domain.StatusMapper.labelPurchase(p.status);
                String detail = "Tienda: " + safe(p.storeName) + "\n" +
                        "Orden: " + safe(p.orderId) + "\n" +
                        "DescripciA3n: " + safe(p.description) + "\n" +
                        "Estado: " + status + "\n" +
                        "Fecha: " + date;
                tvDetail.setText(detail);
            });
        } else {
            if (tvDetail != null) tvDetail.setText("Compra no encontrada");
        }

        setupBottomBar();
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

        // Si este layout tuviera FAB:
        View fab = findViewById(R.id.fabAdd);
        if (fab != null) {
            fab.setOnClickListener(v ->
                    startActivity(new Intent(PurchaseDetailActivity.this, NewPurchaseActivity.class))
            );
        }
    }
}
