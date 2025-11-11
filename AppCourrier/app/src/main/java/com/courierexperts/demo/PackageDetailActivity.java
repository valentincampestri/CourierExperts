package com.courierexperts.demo;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.courierexperts.demo.databinding.ActivityPackageDetailBinding;

public class PackageDetailActivity extends AppCompatActivity {

    private ActivityPackageDetailBinding b;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityPackageDetailBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        // Toolbar simple con back
        b.toolbar.setNavigationOnClickListener(v -> finish());

        long packageId = getIntent().getLongExtra("packageId", -1);
        if (packageId == -1) {
            Toast.makeText(this, "Paquete no encontrado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Por ahora mostramos el ID recibido. Después lo enlazamos a Room.
        b.tvTitle.setText("Paquete #" + packageId);
        b.tvStatus.setText("Estado: (pendiente de data)");
        b.tvDesc.setText("Descripción: (pendiente de data)");

        // Imagen placeholder
        Glide.with(this).load("https://picsum.photos/seed/pack" + packageId + "/300/200")
                .into(b.ivThumb);
    }
}
