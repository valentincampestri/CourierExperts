package com.courierexperts.demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.courierexperts.demo.ui.home.BannerAdapter;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupBottomBar(R.id.nav_home);

        setupBannerCarousel();

        // Bind saludo Hola, {Nombre}
        final android.widget.TextView tvSaludo = findViewById(R.id.tvSaludo);
        if (tvSaludo != null) {
            com.courierexperts.demo.data.repository.UserProfileRepository repo = new com.courierexperts.demo.data.repository.UserProfileRepository(this);
            repo.observeProfile().observe(this, profile -> {
                String name = (profile != null && profile.name != null) ? profile.name.trim() : "";
                tvSaludo.setText(name.isEmpty() ? "Hola" : ("Hola, " + name));
            });
        }

        View btnCompras = findViewById(R.id.btnCompras);
        View btnPaquetes = findViewById(R.id.btnPaquetes);
        View btnEnvios = findViewById(R.id.btnEnvios);

        if (btnCompras != null) {
            btnCompras.setOnClickListener(v ->
                    startActivity(new Intent(HomeActivity.this, PurchasesActivity.class)));
        }
        if (btnPaquetes != null) {
            btnPaquetes.setOnClickListener(v ->
                    startActivity(new Intent(HomeActivity.this, PackagesActivity.class)));
        }
        if (btnEnvios != null) {
            btnEnvios.setOnClickListener(v ->
                    startActivity(new Intent(HomeActivity.this, ShipmentsActivity.class)));
        }

        View tvVerDetallesHome = findViewById(R.id.tvVerDetallesHome);
        if (tvVerDetallesHome != null) {
            tvVerDetallesHome.setOnClickListener(v -> {
                Intent i = new Intent(HomeActivity.this, ShipmentDetailActivity.class);
                i.putExtra("shipmentId", 5);
                startActivity(i);
            });
        }

        // Si tu layout de Home tiene FAB (como Envíos), podés manejarlo acá:
        View fab = findViewById(R.id.fabAdd);
        if (fab != null) {
            fab.setOnClickListener(v ->
                    startActivity(new Intent(HomeActivity.this, NewPurchaseActivity.class)));
            // Cuando tengamos "Nuevo Envío", cambia a NewShipmentActivity.class si querés.
        }
    }

    private void setupBottomBar(int selectedItemId) {
        BottomNavigationView bottom = findViewById(R.id.bottomNav);
        if (bottom == null) return;

        bottom.setSelectedItemId(selectedItemId);
        bottom.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == bottom.getSelectedItemId()) return true;

                if (id == R.id.nav_home) {
                    // ya estás en Home
                    return true;
                } else if (id == R.id.nav_add) {
                    startActivity(new Intent(HomeActivity.this, NewPurchaseActivity.class));
                    return true;
                } else if (id == R.id.nav_profile) {
                    startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
                    return true;
                }
                return false;
            }
        });
    }

    private final android.os.Handler bannerHandler = new android.os.Handler(android.os.Looper.getMainLooper());
    private boolean bannerPaused = false;
    private int bannerPosition = 0;

    private void setupBannerCarousel() {
        RecyclerView rv = findViewById(R.id.rvBanner);
        if (rv == null) return;

        LinearLayoutManager lm = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        rv.setLayoutManager(lm);
        new PagerSnapHelper().attachToRecyclerView(rv);

        java.util.List<Integer> imgs = java.util.Arrays.asList(
                R.drawable.ic_amazon,
                R.drawable.ic_envios,
                R.drawable.ic_compras
        );
        BannerAdapter adapter = new BannerAdapter(imgs);
        rv.setAdapter(adapter);

        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                bannerPaused = (newState == RecyclerView.SCROLL_STATE_DRAGGING);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    bannerPosition = lm.findFirstVisibleItemPosition();
                }
            }
        });
        rv.setOnTouchListener((v, e) -> {
            switch (e.getAction()) {
                case android.view.MotionEvent.ACTION_DOWN:
                case android.view.MotionEvent.ACTION_MOVE:
                    bannerPaused = true; break;
                case android.view.MotionEvent.ACTION_UP:
                case android.view.MotionEvent.ACTION_CANCEL:
                    bannerPaused = false; break;
            }
            return false;
        });

        bannerHandler.postDelayed(new Runnable() {
            @Override public void run() {
                if (!bannerPaused && adapter.getItemCount() > 0) {
                    bannerPosition++;
                    rv.smoothScrollToPosition(bannerPosition);
                }
                bannerHandler.postDelayed(this, 4000);
            }
        }, 4000);
    }
}
