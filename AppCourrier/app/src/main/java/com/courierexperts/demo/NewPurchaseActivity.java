package com.courierexperts.demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.courierexperts.demo.databinding.ActivityNuevaCompraBinding;
import com.courierexperts.demo.ui.purchases.NewPurchaseEvent;
import com.courierexperts.demo.ui.purchases.NewPurchaseUiState;
import com.courierexperts.demo.ui.purchases.NewPurchaseViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class NewPurchaseActivity extends AppCompatActivity {

    private ActivityNuevaCompraBinding b;
    private NewPurchaseViewModel vm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityNuevaCompraBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        vm = new ViewModelProvider(this).get(NewPurchaseViewModel.class);
        observeViewModel();

        setupBottomBar();

        if (b.btnCancelarnuevaCompra != null) {
            b.btnCancelarnuevaCompra.setOnClickListener(v -> finish());
        }
        if (b.btnGuardarNuevaCompra != null) {
            b.btnGuardarNuevaCompra.setOnClickListener(v -> savePurchase());
        }
    }

    private void observeViewModel() {
        vm.getUiState().observe(this, state -> {
            boolean loading = state instanceof NewPurchaseUiState.Loading;
            if (b.btnGuardarNuevaCompra != null) {
                b.btnGuardarNuevaCompra.setEnabled(!loading);
            }
            if (b.progressBar != null) {
                b.progressBar.setVisibility(loading ? android.view.View.VISIBLE : android.view.View.GONE);
            }
        });

        vm.getEvents().observe(this, event -> {
            if (event == null) return;
            NewPurchaseEvent payload = event.getContentIfNotHandled();
            if (payload == null) return;
            if (payload.getType() == NewPurchaseEvent.Type.SHOW_MESSAGE) {
                if (payload.getMessage() != null) {
                    Toast.makeText(this, payload.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else if (payload.getType() == NewPurchaseEvent.Type.SUCCESS) {
                if (payload.getMessage() != null) {
                    Toast.makeText(this, payload.getMessage(), Toast.LENGTH_SHORT).show();
                }
                startActivity(new Intent(this, PurchasesActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
                finish();
            }
        });
    }

    private void savePurchase() {
        String store = textOf(b.etStoreName);
        String description = textOf(b.etDescription);
        String orderId = textOf(b.etOrderId);
        vm.savePurchase(store, description, orderId);
    }

    private static String textOf(@Nullable com.google.android.material.textfield.TextInputEditText et) {
        if (et != null && et.getText() != null) {
            return et.getText().toString();
        }
        return "";
    }

    private void setupBottomBar() {
        BottomNavigationView bottom = b.bottomNav;
        if (bottom == null) return;

        bottom.setSelectedItemId(R.id.nav_add);

        bottom.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    startActivity(new Intent(NewPurchaseActivity.this, HomeActivity.class));
                    return true;
                } else if (id == R.id.nav_add) {
                    return true;
                } else if (id == R.id.nav_profile) {
                    startActivity(new Intent(NewPurchaseActivity.this, ProfileActivity.class));
                    return true;
                }
                return false;
            }
        });
    }
}
