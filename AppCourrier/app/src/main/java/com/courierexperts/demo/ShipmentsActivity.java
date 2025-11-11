package com.courierexperts.demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.courierexperts.demo.databinding.ActivityEnviosBinding;
// imports de tu adapter/viewmodel si los usás
// import com.courierexperts.demo.ui.shipments.ShipmentAdapter;
// import com.courierexperts.demo.ui.shipments.ShipmentsViewModel;

public class ShipmentsActivity extends AppCompatActivity {

    private ActivityEnviosBinding b;
//    private ShipmentsViewModel vm;
//    private ShipmentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityEnviosBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        // --- RecyclerView (si ya lo tenés armado podés dejar lo tuyo) ---
//        adapter = new ShipmentAdapter();
//        b.rvEnvios.setLayoutManager(new LinearLayoutManager(this));
//        b.rvEnvios.setAdapter(adapter);
//
//        vm = new ViewModelProvider(this).get(ShipmentsViewModel.class);
//        vm.getShipments().observe(this, list -> {
//            adapter.submit(list);
//            boolean empty = (list == null || list.isEmpty());
//            b.tvEmptyEnvios.setVisibility(empty ? View.VISIBLE : View.GONE);
//            b.rvEnvios.setVisibility(empty ? View.GONE : View.VISIBLE);
//        });

        // (2) CLICK DEL FAB — va en esta Activity
        b.fabAdd.setOnClickListener(v ->
                        startActivity(new Intent(this, NewPurchaseActivity.class))
                // cuando tengas el flujo de "Nuevo Envío", cambiá a NewShipmentActivity.class
        );

        // (3) LISTENER DE LA BOTTOM BAR (sin ítem central)
        setupBottomBar();
    }

    private void setupBottomBar() {
        b.bottomNav.setOnItemSelectedListener(new com.google.android.material.navigation.NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    startActivity(new Intent(ShipmentsActivity.this, HomeActivity.class));
                    return true;
                } else if (id == R.id.nav_profile) {
                    startActivity(new Intent(ShipmentsActivity.this, ProfileActivity.class));
                    return true;
                }
                return false;
            }
        });
    }
}
