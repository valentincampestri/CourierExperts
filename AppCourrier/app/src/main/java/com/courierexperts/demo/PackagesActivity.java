package com.courierexperts.demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.courierexperts.demo.databinding.ActivityPaquetesBinding;
import com.courierexperts.demo.ui.packages.PackageAdapter;
import com.courierexperts.demo.ui.packages.PackagesViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class PackagesActivity extends AppCompatActivity {

    private ActivityPaquetesBinding b;
    private PackagesViewModel vm;
    private PackageAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        b = ActivityPaquetesBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        adapter = new PackageAdapter();
        b.rvPaquetes.setLayoutManager(new LinearLayoutManager(this));
        b.rvPaquetes.setAdapter(adapter);

        adapter.setOnItemClickListener(item ->
                startActivity(new Intent(this, PackageDetailActivity.class)
                        .putExtra("packageId", item.id))
        );

        vm = new ViewModelProvider(this).get(PackagesViewModel.class);
        vm.getPackages().observe(this, list -> {
            adapter.submit(list);
            boolean empty = (list == null || list.isEmpty());
            b.tvEmptyPaquetes.setVisibility(empty ? android.view.View.VISIBLE : android.view.View.GONE);
            b.rvPaquetes.setVisibility(empty ? android.view.View.GONE : android.view.View.VISIBLE);
        });

        // CTA: ir a Envíos
        b.btnSolicitar.setOnClickListener(v ->
                startActivity(new Intent(PackagesActivity.this, ShipmentsActivity.class))
        );

        setupBottomBar();
    }

    private void setupBottomBar() {
        BottomNavigationView bottom = b.bottomNav;
        if (bottom == null) return;

        bottom.setSelectedItemId(R.id.nav_home);
        bottom.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    startActivity(new Intent(PackagesActivity.this, HomeActivity.class));
                    return true;
                } else if (id == R.id.nav_add) {
                    startActivity(new Intent(PackagesActivity.this, NewPurchaseActivity.class));
                    return true;
                } else if (id == R.id.nav_profile) {
                    startActivity(new Intent(PackagesActivity.this, ProfileActivity.class));
                    return true;
                }
                return false;
            }
        });
    }
}
