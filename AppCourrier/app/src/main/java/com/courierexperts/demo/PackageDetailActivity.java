package com.courierexperts.demo;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.courierexperts.demo.databinding.ActivityPackageDetailBinding;
import com.courierexperts.demo.data.local.db.AppDatabase;
import com.courierexperts.demo.data.local.entity.PackageEntity;
import com.courierexperts.demo.domain.StatusMapper;

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

        AppDatabase.get(getApplicationContext()).packageDao().observeById(packageId)
                .observe(this, (PackageEntity it) -> {
                    if (it == null) return;
                    b.tvTitle.setText(it.label != null ? it.label : ("Paquete #" + packageId));
                    b.tvStatus.setText("Estado: " + StatusMapper.labelPackage(it.status));
                    b.tvDesc.setText("Descripción: " + (it.description != null ? it.description : ""));
                    Glide.with(this)
                            .load(it.thumbnailUrl != null && !it.thumbnailUrl.isEmpty() ? it.thumbnailUrl : ("https://picsum.photos/seed/pack" + packageId + "/300/200"))
                            .into(b.ivThumb);
                });
        b.tvStatus.setText("Estado: (pendiente de data)");
        b.tvDesc.setText("Descripción: (pendiente de data)");

        // Imagen placeholder
        Glide.with(this).load("https://picsum.photos/seed/pack" + packageId + "/300/200")
                .into(b.ivThumb);
    }
}

