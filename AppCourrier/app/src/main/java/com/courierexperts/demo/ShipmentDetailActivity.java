package com.courierexperts.demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.courierexperts.demo.R;
import com.courierexperts.demo.databinding.ActivityEnviosBinding;
import com.courierexperts.demo.data.local.db.AppDatabase;
import com.courierexperts.demo.data.local.entity.ShipmentEntity;
import com.courierexperts.demo.domain.StatusMapper;
import com.courierexperts.demo.ui.packages.PackageAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ShipmentDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityEnviosBinding b = ActivityEnviosBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        String shipmentFsId = getIntent().getStringExtra("shipmentFsId");
        long shipmentLocalId = getIntent().getLongExtra("shipmentLocalId", -1);

        final TextView tvTitle = b.tvEnvios;
        final TextView tvStatus = b.tvMensaje;

        final PackageAdapter adapter = new PackageAdapter();
        adapter.setSelectionEnabled(false);
        b.rvEnvios.setLayoutManager(new LinearLayoutManager(this));
        b.rvEnvios.setAdapter(adapter);

        final AppDatabase db = AppDatabase.get(getApplicationContext());
        if (shipmentFsId != null && !shipmentFsId.isEmpty()) {
            wireHeadAndList(db, shipmentFsId, tvTitle, tvStatus, b, adapter);
        } else if (shipmentLocalId > 0) {
            LiveData<ShipmentEntity> ld = db.shipmentDao().observeById(shipmentLocalId);
            ld.observe(this, new Observer<ShipmentEntity>() {
                @Override public void onChanged(ShipmentEntity se) {
                    if (se == null) return;
                    String fs = se.fsId != null ? se.fsId : String.valueOf(se.id);
                    tvTitle.setText(se.title != null ? se.title : "Envio");
                    tvStatus.setText(StatusMapper.labelShipment(se.status));
                    wireListOnly(db, fs, b, adapter);
                    ld.removeObserver(this);
                }
            });
        } else {
            tvTitle.setText("Envio");
            tvStatus.setText("");
        }

        setupBottomBar();

        View fab = b.fabAdd;
        if (fab != null) {
            fab.setOnClickListener(v ->
                    startActivity(new Intent(ShipmentDetailActivity.this, NewPurchaseActivity.class))
            );
        }
    }

    private void wireHeadAndList(AppDatabase db, String fsId, TextView tvTitle, TextView tvStatus,
                                 ActivityEnviosBinding b, PackageAdapter adapter) {
        db.shipmentDao().observeByFsId(fsId).observe(this, se -> {
            if (se == null) return;
            tvTitle.setText(se.title != null ? se.title : "Envio");
            tvStatus.setText(StatusMapper.labelShipment(se.status));
        });
        wireListOnly(db, fsId, b, adapter);
    }

    private void wireListOnly(AppDatabase db, String shipmentId, ActivityEnviosBinding b, PackageAdapter adapter) {
        db.packageDao().observeByShipmentId(shipmentId).observe(this, list -> {
            adapter.submit(list);
            boolean empty = (list == null || list.isEmpty());
            b.tvEmptyEnvios.setVisibility(empty ? View.VISIBLE : View.GONE);
            b.rvEnvios.setVisibility(empty ? View.GONE : View.VISIBLE);
        });
        adapter.setOnItemClickListener(item -> {
            Intent i = new Intent(ShipmentDetailActivity.this, PackageDetailActivity.class);
            i.putExtra("packageId", item.id);
            startActivity(i);
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


