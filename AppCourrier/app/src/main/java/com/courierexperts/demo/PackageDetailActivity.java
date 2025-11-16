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

import com.bumptech.glide.Glide;
import com.courierexperts.demo.data.local.entity.PackageEntity;
import com.courierexperts.demo.databinding.ActivityPackageDetailBinding;
import com.courierexperts.demo.domain.StatusMapper;
import com.courierexperts.demo.ui.packages.PackageDetailUiState;
import com.courierexperts.demo.ui.packages.PackageDetailViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class PackageDetailActivity extends AppCompatActivity {

    private ActivityPackageDetailBinding b;
    private PackageDetailViewModel vm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityPackageDetailBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        vm = new ViewModelProvider(this).get(PackageDetailViewModel.class);

        // Botón Volver dentro de la card
        b.btnBack.setOnClickListener(v -> finish());

        // Bottom nav igual que en otras pantallas
        setupBottomBar();

        long packageId = getIntent().getLongExtra("packageId", -1L);
        observeState();
        vm.load(packageId);
    }

    private void observeState() {
        vm.getUiState().observe(this, state -> {
            // Por defecto ocultamos la card, la mostramos solo en Success
            b.ticketCard.setVisibility(View.GONE);

            if (state instanceof PackageDetailUiState.Loading) {
                // Podés mostrar un pequeño feedback si querés
                // por ahora simplemente no mostramos nada hasta tener datos
            } else if (state instanceof PackageDetailUiState.Success) {
                PackageEntity entity = ((PackageDetailUiState.Success) state).getEntity();
                render(entity);
            } else if (state instanceof PackageDetailUiState.NotFound) {
                Toast.makeText(this, R.string.package_detail_not_found, Toast.LENGTH_LONG).show();
            } else if (state instanceof PackageDetailUiState.Error) {
                Toast.makeText(this, R.string.state_error_retry, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void render(PackageEntity entity) {
        if (entity == null) return;

        b.ticketCard.setVisibility(View.VISIBLE);

        // Header: subtítulo con nombre del paquete
        String label = entity.label != null && !entity.label.isEmpty()
                ? entity.label
                : ("Paquete #" + entity.id);
        b.tvSub.setText(label);

        // Título dentro del ticket
        b.tvTicketTitle.setText("Detalles del paquete");

        // Imagen del paquete
        String imageUrl = (entity.thumbnailUrl != null && !entity.thumbnailUrl.isEmpty())
                ? entity.thumbnailUrl
                : "https://picsum.photos/seed/pack" + entity.id + "/300/200";
        Glide.with(this).load(imageUrl).into(b.ivThumb);

        // Estado: texto + color de pill
        String statusLabel = StatusMapper.labelPackage(entity.status);
        b.tvStatus.setText(statusLabel);

        String statusCode = entity.status != null ? entity.status.toUpperCase(Locale.ROOT) : "";
        int bgRes;
        switch (statusCode) {
            case "DELIVERED":
            case "RECEIVED":
                bgRes = R.drawable.bg_status_chip_delivered;
                break;
            case "IN_TRANSIT":
                bgRes = R.drawable.bg_status_chip_transit;
                break;
            case "SHIPPED":
                bgRes = R.drawable.bg_status_chip_delivered; // o uno específico si tenés
                break;
            case "CANCELLED":
                bgRes = R.drawable.bg_status_chip_cancelled;
                break;
            default:
                bgRes = R.drawable.bg_status_chip_pending;
                break;
        }
        b.tvStatus.setBackgroundResource(bgRes);

        // Nombre (usamos label)
        b.tvTitle.setText(label);

        // Descripción
        b.tvDesc.setText(
                entity.description != null ? entity.description : ""
        );

        // Precio
        b.tvPriceValue.setText(
                String.format(Locale.getDefault(), "$ %.2f", entity.price)
        );

        // Fecha (lastUpdate del paquete)
        b.tvDateValue.setText(formatDate(entity.lastUpdate));

    }

    private static String formatDate(long epochMillis) {
        if (epochMillis <= 0) return "";
        SimpleDateFormat sdf =
                new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        return sdf.format(new java.util.Date(epochMillis));
    }

    private void setupBottomBar() {
        BottomNavigationView bottom = b.bottomNav;
        if (bottom == null) return;

        bottom.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    startActivity(new Intent(PackageDetailActivity.this, HomeActivity.class));
                    return true;
                } else if (id == R.id.nav_add) {
                    startActivity(new Intent(PackageDetailActivity.this, NewPurchaseActivity.class));
                    return true;
                } else if (id == R.id.nav_profile) {
                    startActivity(new Intent(PackageDetailActivity.this, ProfileActivity.class));
                    return true;
                }
                return false;
            }
        });
    }
}
