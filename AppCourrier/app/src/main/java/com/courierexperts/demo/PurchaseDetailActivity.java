package com.courierexperts.demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.courierexperts.demo.R;
import com.courierexperts.demo.data.local.entity.PurchaseEntity;
import com.courierexperts.demo.databinding.ActivityPurchaseDetailBinding;
import com.courierexperts.demo.domain.StatusMapper;
import com.courierexperts.demo.ui.purchases.PurchaseDetailUiState;
import com.courierexperts.demo.ui.purchases.PurchaseDetailViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.Locale;

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

        // Botón Volver
        if (b.btnBack != null) {
            b.btnBack.setOnClickListener(v -> finish());
        }

        setupBottomBar();
    }

    private void observeUiState() {
        vm.getUiState().observe(this, state -> {

            if (state instanceof PurchaseDetailUiState.Loading) {
                // Si quisieras, podrías mostrar un ProgressBar en el layout.
                // Por ahora no hacemos nada especial.
            } else if (state instanceof PurchaseDetailUiState.Success) {
                PurchaseEntity p = ((PurchaseDetailUiState.Success) state).getPurchase();
                renderDetail(p);
            } else if (state instanceof PurchaseDetailUiState.NotFound) {
                // Mostramos algo parecido a la versión vieja
                Toast.makeText(this, R.string.purchase_detail_not_found, Toast.LENGTH_LONG).show();
                showNotFoundState();
            } else if (state instanceof PurchaseDetailUiState.Error) {
                Toast.makeText(this, R.string.state_error_retry, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void renderDetail(PurchaseEntity p) {
        if (p == null) return;

        // Label de estado
        String statusLabel = StatusMapper.labelPurchase(p.status);

        // HEADER: subtítulo con nombre de la tienda
        b.tvSub.setText(safe(p.storeName));

        // Valores individuales en la card
        b.tvProductNameValue.setText(safe(p.productName));
        b.tvProductDescriptionValue.setText(safe(p.description));
        b.tvPriceValue.setText(p.price != null ? "$ " + p.price : "");
        b.tvStoreValue.setText(safe(p.storeName));
        b.tvOrderValue.setText(safe(p.orderId));
        b.tvDateValue.setText(formatDate(p.createdAt));

        // Imagen real del producto
        Glide.with(this)
                .load(p.thumbnailUrl)
                .into(b.imgCompraDetalle);

        // Chip de estado con color según estado
        applyStatusChip(b.tvEstado, statusLabel);
    }

    private void showNotFoundState() {
        // Similar a la versión vieja cuando no hay id o no se encuentra compra
        b.tvSub.setText(getString(R.string.purchase_detail_not_found));
        b.tvStoreValue.setText("");
        b.tvOrderValue.setText("");
        b.tvDateValue.setText("");
        b.tvEstado.setText("");
        // Podrías también cambiar la imagen si querés (ícono genérico, etc.)
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
            // Cancelada u otros: por ahora la tratamos como pendiente
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

        View fab = findViewById(R.id.fabAdd);
        if (fab != null) {
            fab.setOnClickListener(v ->
                    startActivity(new Intent(PurchaseDetailActivity.this, NewPurchaseActivity.class))
            );
        }
    }
}
