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
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.courierexperts.demo.databinding.ActivityMainBinding;
import com.courierexperts.demo.ui.home.BannerAdapter;
import com.courierexperts.demo.ui.home.HomeEvent;
import com.courierexperts.demo.ui.home.HomeUiState;
import com.courierexperts.demo.ui.home.HomeViewModel;
import com.courierexperts.demo.ui.home.RecentActivityAdapter;
import com.courierexperts.demo.ui.home.RecentActivityItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private ActivityMainBinding b;
    private HomeViewModel vm;

    private final android.os.Handler bannerHandler =
            new android.os.Handler(android.os.Looper.getMainLooper());
    private boolean bannerPaused = false;
    private int bannerPosition = 0;

    private RecentActivityAdapter recentAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        vm = new ViewModelProvider(this).get(HomeViewModel.class);

        setupBottomBar();
        setupBannerCarousel();
        setupButtons();
        setupRecentActivityList();
        observeViewModel();
        setupSeedDebug();
    }

    private void observeViewModel() {
        vm.getUiState().observe(this, state -> {
            if (state instanceof HomeUiState.Loading) {
                renderLoadingState();
            } else if (state instanceof HomeUiState.Content) {
                renderContentState((HomeUiState.Content) state);
            } else if (state instanceof HomeUiState.Error) {
                renderErrorState((HomeUiState.Error) state);
            }
        });

        vm.getEvents().observe(this, event -> {
            if (event == null) return;
            HomeEvent payload = event.getContentIfNotHandled();
            if (payload == null) return;
            if (payload.getType() == HomeEvent.Type.PROMPT_DEPOSIT) {
                handleDepositReminder();
            } else if (payload.getType() == HomeEvent.Type.SHOW_ERROR) {
                Toast.makeText(this, R.string.state_error_retry, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void renderLoadingState() {
        if (b.tvSaludo != null) {
            b.tvSaludo.setText(getString(R.string.home_greeting_generic));
        }
        if (recentAdapter != null) {
            recentAdapter.submit(Collections.emptyList());
        }
    }

    private void renderContentState(HomeUiState.Content content) {
        if (b.tvSaludo != null) {
            b.tvSaludo.setText(content.getGreeting());
        }

        if (recentAdapter != null) {
            List<RecentActivityItem> items = content.getRecentActivityItems();
            if (items != null) {
                recentAdapter.submit(items);
            } else {
                recentAdapter.submit(Collections.emptyList());
            }
        }
    }

    private void renderErrorState(HomeUiState.Error error) {
        Toast.makeText(this, error.getMessage(), Toast.LENGTH_LONG).show();
        if (recentAdapter != null) {
            recentAdapter.submit(Collections.emptyList());
        }
    }

    private void handleDepositReminder() {
        Toast.makeText(this, R.string.home_deposit_reminder, Toast.LENGTH_LONG).show();
        startActivity(new Intent(this, EditProfileActivity.class));
    }

    private void setupButtons() {
        if (b.btnCompras != null) {
            b.btnCompras.setOnClickListener(v ->
                    startActivity(new Intent(this, PurchasesActivity.class)));
        }
        if (b.btnPaquetes != null) {
            b.btnPaquetes.setOnClickListener(v ->
                    startActivity(new Intent(this, PackagesActivity.class)));
        }
        if (b.btnEnvios != null) {
            b.btnEnvios.setOnClickListener(v ->
                    startActivity(new Intent(this, ShipmentsActivity.class)));
        }
    }

    private void setupSeedDebug() {
        if (b.tvSaludo == null) {
            return;
        }
        boolean isDebug = (getApplicationInfo().flags
                & android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        if (!isDebug) {
            return;
        }
        b.tvSaludo.setOnLongClickListener(v -> {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Seed Firestore (debug)")
                    .setMessage("Crear 10 compras, 10 paquetes y 10 envios para el usuario actual?")
                    .setNegativeButton("Cancelar", null)
                    .setPositiveButton("Sembrar",
                            (d, which) -> com.courierexperts.demo.util.SeedDebug.seedNow(this))
                    .show();
            return true;
        });
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

    private void setupBannerCarousel() {
        RecyclerView rv = b.rvBanner;
        if (rv == null) return;

        LinearLayoutManager lm = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        rv.setLayoutManager(lm);
        new PagerSnapHelper().attachToRecyclerView(rv);

        List<Integer> imgs = Arrays.asList(
                R.drawable.ic_amazon,
                R.drawable.ic_samsung,
                R.drawable.ic_infinity,
                R.drawable.ic_galaxy
        );
        BannerAdapter adapter = new BannerAdapter(imgs);
        rv.setAdapter(adapter);

        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                bannerPaused = (newState == RecyclerView.SCROLL_STATE_DRAGGING);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    bannerPosition = lm.findFirstVisibleItemPosition();
                }
            }
        });
        rv.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case android.view.MotionEvent.ACTION_DOWN:
                case android.view.MotionEvent.ACTION_MOVE:
                    bannerPaused = true;
                    break;
                case android.view.MotionEvent.ACTION_UP:
                case android.view.MotionEvent.ACTION_CANCEL:
                    bannerPaused = false;
                    break;
            }
            return false;
        });

        bannerHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!bannerPaused && adapter.getItemCount() > 0) {
                    bannerPosition++;
                    rv.smoothScrollToPosition(bannerPosition);
                }
                bannerHandler.postDelayed(this, 4000);
            }
        }, 4000);
    }

    private void setupRecentActivityList() {
        if (b.rvActividadReciente == null) return;

        recentAdapter = new RecentActivityAdapter();
        b.rvActividadReciente.setLayoutManager(new LinearLayoutManager(this));
        b.rvActividadReciente.setAdapter(recentAdapter);

        recentAdapter.setOnItemClickListener(item -> {
            switch (item.getType()) {
                case PURCHASE:
                    startActivity(new Intent(this, PurchaseDetailActivity.class)
                            .putExtra("purchaseId", item.getId()));
                    break;
                case PACKAGE:
                    startActivity(new Intent(this, PackageDetailActivity.class)
                            .putExtra("packageId", item.getId()));
                    break;
                case SHIPMENT:
                    Intent intent = new Intent(this, ShipmentDetailActivity.class);

                    // 🔹 Enviamos el Firestore ID si está disponible
                    if (item.getFirestoreId() != null && !item.getFirestoreId().isEmpty()) {
                        intent.putExtra("shipmentFsId", item.getFirestoreId());
                    }

                    // 🔹 Siempre mandamos también el ID local
                    intent.putExtra("shipmentLocalId", item.getId());

                    startActivity(intent);
                    break;
            }
        });
    }
}
