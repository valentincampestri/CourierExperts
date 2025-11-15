package com.courierexperts.demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.courierexperts.demo.data.repository.PurchaseRepository;
import com.courierexperts.demo.domain.StatusMapper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class PurchaseDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_detail);

        // Views de la UI
        View btnBack = findViewById(R.id.btnBack);
        final TextView tvSub = findViewById(R.id.tvSub);
        final TextView tvEstado = findViewById(R.id.tvEstado);
        final ImageView imgCompraDetalle = findViewById(R.id.imgCompraDetalle);

        final TextView tvStoreValue = findViewById(R.id.tvStoreValue);
        final TextView tvOrderValue = findViewById(R.id.tvOrderValue);
        final TextView tvDateValue = findViewById(R.id.tvDateValue);

        final TextView tvname = findViewById(R.id.tvNameValue);
        final TextView tvDescription = findViewById(R.id.tvDescription);
        final TextView tvPrice = findViewById(R.id.tvPriceValue);


        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        long id = getIntent().getLongExtra("purchaseId", 0L);

        if (id > 0) {
            PurchaseRepository repo = new PurchaseRepository(this);
            repo.observePurchaseById(id).observe(this, p -> {
                if (p == null) return;

                // HEADER: subtítulo con nombre de la tienda
                if (tvSub != null) {
                    tvSub.setText(safe(p.storeName));
                }

                // Datos formateados
                String date = formatDate(p.createdAt);
                String statusLabel = StatusMapper.labelPurchase(p.status);

                // Seteamos SOLO los valores (los labels están en el XML en negrita)
                if (tvStoreValue != null) tvStoreValue.setText(safe(p.storeName));
                if (tvOrderValue != null) tvOrderValue.setText(safe(p.orderId));
                if (tvDateValue != null) tvDateValue.setText(date);
                if (tvname != null) tvname.setText(safe(p.name));
                if (tvDescription != null) tvDescription.setText(safe(p.description));
                if (tvPrice != null) tvPrice.setText(String.format(Locale.getDefault(), "$ %.2f", p.price));

                // Imagen real del producto
                if (imgCompraDetalle != null) {
                    Glide.with(this)
                            .load(p.thumbnailUrl)
                            .into(imgCompraDetalle);
                }

                // Chip de estado con color según estado
                applyStatusChip(tvEstado, statusLabel);
            });
        } else {
            // Si vino sin id, mostramos mensaje básico
            if (tvStoreValue != null) tvStoreValue.setText("Compra no encontrada");
            if (tvOrderValue != null) tvOrderValue.setText("");
            if (tvDateValue != null) tvDateValue.setText("");
            if (tvname != null) tvname.setText("");
            if (tvDescription != null) tvDescription.setText("");
            if (tvPrice != null) tvPrice.setText("");
        }

        setupBottomBar();
    }

    // Helpers

    private static String safe(String s) {
        return s != null ? s : "";
    }

    private static String formatDate(long epoch) {
        SimpleDateFormat sdf =
                new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        return sdf.format(new java.util.Date(epoch));
    }

    private void applyStatusChip(TextView view, String statusLabel) {
        if (view == null) return;

        view.setText(statusLabel);

        String s = statusLabel.toLowerCase(Locale.getDefault());
        int bgRes;

        if (s.contains("pendiente")) {
            bgRes = R.drawable.bg_status_chip_pending;
        } else if (s.contains("entreg")
                || s.contains("despach")
                || s.contains("recibid")) {
            bgRes = R.drawable.bg_status_chip_delivered;
        } else {
            // Cancelada u otros: por ahora lo tratamos como pendiente
            bgRes = R.drawable.bg_status_chip_pending;
        }

        view.setBackgroundResource(bgRes);
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