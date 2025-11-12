package com.courierexperts.demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.courierexperts.demo.databinding.ActivityComprasBinding;
import com.courierexperts.demo.ui.purchases.PurchaseAdapter;
import com.courierexperts.demo.ui.purchases.PurchasesViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class PurchasesActivity extends AppCompatActivity {

    private ActivityComprasBinding b;
    private PurchasesViewModel vm;
    private PurchaseAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ViewBinding del layout activity_compras.xml
        b = ActivityComprasBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        // RecyclerView + Adapter
        adapter = new PurchaseAdapter();
        b.rvCompras.setLayoutManager(new LinearLayoutManager(this));
        b.rvCompras.setAdapter(adapter);

        // Click a detalle
        adapter.setOnItemClickListener(item ->
                startActivity(new Intent(this, PurchaseDetailActivity.class)
                        .putExtra("purchaseId", item.id))
        );

        // ViewModel + LiveData
        vm = new ViewModelProvider(this).get(PurchasesViewModel.class);
        vm.getPurchases().observe(this, list -> {
            adapter.submit(list);
            boolean empty = (list == null || list.isEmpty());
            b.tvEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
            b.rvCompras.setVisibility(empty ? View.GONE : View.VISIBLE);
        });

        // Si este layout tuviera FAB central (opcional)
        View fab = findViewById(R.id.fabAdd);
        if (fab != null) {
            fab.setOnClickListener(v ->
                    startActivity(new Intent(PurchasesActivity.this, NewPurchaseActivity.class))
            );
        }

        setupBottomBar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (vm != null) {
            vm.syncPendingIfNetworkAvailable();
        }
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
                    startActivity(new Intent(PurchasesActivity.this, HomeActivity.class));
                    return true;
                } else if (id == R.id.nav_add) {
                    startActivity(new Intent(PurchasesActivity.this, NewPurchaseActivity.class));
                    return true;
                } else if (id == R.id.nav_profile) {
                    startActivity(new Intent(PurchasesActivity.this, ProfileActivity.class));
                    return true;
                }
                return false;
            }
        });
    }
}
