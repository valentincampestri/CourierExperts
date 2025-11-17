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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.courierexperts.demo.databinding.ActivityComprasBinding;
import com.courierexperts.demo.ui.purchases.PurchaseAdapter;
import com.courierexperts.demo.ui.purchases.PurchasesUiState;
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
        observeUiState();
        vm.refresh();

        // Si este layout tuviera FAB central (opcional)
        View fab = findViewById(R.id.fabAdd);
        if (fab != null) {
            fab.setOnClickListener(v ->
                    startActivity(new Intent(PurchasesActivity.this, NewPurchaseActivity.class))
            );
        }
        setupBottomBar();
    }

    private void observeUiState() {
        vm.getUiState().observe(this, state -> {
            b.progressBar.setVisibility(View.GONE);
            b.tvStateMessage.setVisibility(View.GONE);
            if (state instanceof PurchasesUiState.Loading) {
                b.progressBar.setVisibility(View.VISIBLE);
                b.rvCompras.setVisibility(View.GONE);
            } else if (state instanceof PurchasesUiState.Success) {
                b.rvCompras.setVisibility(View.VISIBLE);
                adapter.submit(((PurchasesUiState.Success) state).getPurchases());
            } else if (state instanceof PurchasesUiState.Empty) {
                b.tvStateMessage.setVisibility(View.VISIBLE);
                b.tvStateMessage.setText(R.string.purchases_empty_message);
                b.rvCompras.setVisibility(View.GONE);
            } else if (state instanceof PurchasesUiState.Error) {
                b.tvStateMessage.setVisibility(View.VISIBLE);
                b.tvStateMessage.setText(R.string.state_error_retry);
                b.rvCompras.setVisibility(View.GONE);
                Toast.makeText(this, R.string.state_error_retry, Toast.LENGTH_LONG).show();
            }
        });
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

        // No marcar ningún item en esta pantalla
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
