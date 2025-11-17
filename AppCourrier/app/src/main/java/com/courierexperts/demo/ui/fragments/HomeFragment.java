package com.courierexperts.demo.ui.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.courierexperts.demo.R;
import com.courierexperts.demo.databinding.ActivityMainBinding;
import com.courierexperts.demo.ui.fragments.PackageDetailFragment;
import com.courierexperts.demo.ui.fragments.PurchaseDetailFragment;
import com.courierexperts.demo.ui.fragments.ShipmentDetailFragment;
import com.courierexperts.demo.ui.home.BannerAdapter;
import com.courierexperts.demo.ui.home.HomeEvent;
import com.courierexperts.demo.ui.home.HomeUiState;
import com.courierexperts.demo.ui.home.HomeViewModel;
import com.courierexperts.demo.ui.home.RecentActivityAdapter;
import com.courierexperts.demo.ui.home.RecentActivityItem;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class HomeFragment extends Fragment {

    private ActivityMainBinding binding;
    private HomeViewModel viewModel;
    private final Handler bannerHandler = new Handler(Looper.getMainLooper());
    private boolean bannerPaused = false;
    private int bannerPosition = 0;
    private LinearLayoutManager bannerLayoutManager;
    private RecentActivityAdapter recentAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = ActivityMainBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        if (binding.bottomNav != null) {
            binding.bottomNav.setVisibility(View.GONE);
        }

        setupBannerCarousel();
        setupButtons();
        setupRecentActivityList();
        observeViewModel();
        setupSeedDebug();
    }

    private void observeViewModel() {
        viewModel.getUiState().observe(getViewLifecycleOwner(), state -> {
            if (state instanceof HomeUiState.Loading) {
                renderLoadingState();
            } else if (state instanceof HomeUiState.Content) {
                renderContentState((HomeUiState.Content) state);
            } else if (state instanceof HomeUiState.Error) {
                renderErrorState((HomeUiState.Error) state);
            }
        });

        viewModel.getEvents().observe(getViewLifecycleOwner(), event -> {
            if (event == null) return;
            HomeEvent payload = event.getContentIfNotHandled();
            if (payload == null) return;
            if (payload.getType() == HomeEvent.Type.PROMPT_DEPOSIT) {
                handleDepositReminder();
            } else if (payload.getType() == HomeEvent.Type.SHOW_ERROR) {
                Toast.makeText(requireContext(), R.string.state_error_retry, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void renderLoadingState() {
        if (binding == null) return;
        if (binding.tvSaludo != null) {
            binding.tvSaludo.setText(getString(R.string.home_greeting_generic));
        }
        if (recentAdapter != null) {
            recentAdapter.submit(Collections.emptyList());
        }
    }

    private void renderContentState(HomeUiState.Content content) {
        if (binding == null) return;
        if (binding.tvSaludo != null) {
            binding.tvSaludo.setText(content.getGreeting());
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
        Toast.makeText(requireContext(), error.getMessage(), Toast.LENGTH_LONG).show();
        if (recentAdapter != null) {
            recentAdapter.submit(Collections.emptyList());
        }
    }

    private void handleDepositReminder() {
        Toast.makeText(requireContext(), R.string.home_deposit_reminder, Toast.LENGTH_LONG).show();
        NavHostFragment.findNavController(HomeFragment.this)
                .navigate(R.id.action_nav_home_to_editProfileFragment);
    }

    private void setupButtons() {
        if (binding == null) return;
        if (binding.btnCompras != null) {
            binding.btnCompras.setOnClickListener(v ->
                    NavHostFragment.findNavController(HomeFragment.this)
                            .navigate(R.id.action_nav_home_to_purchasesFragment));
        }
        if (binding.btnPaquetes != null) {
            binding.btnPaquetes.setOnClickListener(v ->
                    NavHostFragment.findNavController(HomeFragment.this)
                            .navigate(R.id.action_nav_home_to_packagesFragment));
        }
        if (binding.btnEnvios != null) {
            binding.btnEnvios.setOnClickListener(v ->
                    NavHostFragment.findNavController(HomeFragment.this)
                            .navigate(R.id.action_nav_home_to_shipmentsFragment));
        }
    }

    private void setupSeedDebug() {
        if (binding == null || binding.tvSaludo == null) {
            return;
        }
        binding.tvSaludo.setOnLongClickListener(v -> {
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Seed Firestore (debug)")
                    .setMessage("Crear 10 compras, 10 paquetes y 10 envios para el usuario actual?")
                    .setNegativeButton("Cancelar", null)
                    .setPositiveButton("Sembrar",
                            (d, which) -> com.courierexperts.demo.util.SeedDebug.seedNow(requireContext()))
                    .show();
            return true;
        });
    }

    private void setupBannerCarousel() {
        if (binding == null || binding.rvBanner == null) return;

        bannerLayoutManager = new LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false);
        binding.rvBanner.setLayoutManager(bannerLayoutManager);
        new PagerSnapHelper().attachToRecyclerView(binding.rvBanner);

        List<Integer> imgs = Arrays.asList(
                R.drawable.ic_amazon,
                R.drawable.ic_samsung,
                R.drawable.ic_infinity,
                R.drawable.ic_galaxy
        );
        BannerAdapter adapter = new BannerAdapter(imgs);
        binding.rvBanner.setAdapter(adapter);

        binding.rvBanner.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                bannerPaused = (newState == RecyclerView.SCROLL_STATE_DRAGGING);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && bannerLayoutManager != null) {
                    bannerPosition = bannerLayoutManager.findFirstVisibleItemPosition();
                }
            }
        });
        binding.rvBanner.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN
                    || event.getAction() == MotionEvent.ACTION_MOVE) {
                bannerPaused = true;
            } else if (event.getAction() == MotionEvent.ACTION_UP
                    || event.getAction() == MotionEvent.ACTION_CANCEL) {
                bannerPaused = false;
            }
            return false;
        });

        bannerHandler.postDelayed(bannerAutoScrollRunnable, 4000);
    }

    private final Runnable bannerAutoScrollRunnable = new Runnable() {
        @Override
        public void run() {
            if (binding != null && binding.rvBanner != null && binding.rvBanner.getAdapter() != null) {
                if (!bannerPaused && binding.rvBanner.getAdapter().getItemCount() > 0) {
                    bannerPosition++;
                    binding.rvBanner.smoothScrollToPosition(bannerPosition);
                }
                bannerHandler.postDelayed(this, 4000);
            }
        }
    };

    private void setupRecentActivityList() {
        if (binding == null || binding.rvActividadReciente == null) return;

        recentAdapter = new RecentActivityAdapter();
        binding.rvActividadReciente.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvActividadReciente.setAdapter(recentAdapter);

        recentAdapter.setOnItemClickListener(item -> {
            Bundle navArgs = new Bundle();
            switch (item.getType()) {
                case PURCHASE:
                    navArgs.putLong(PurchaseDetailFragment.ARG_PURCHASE_ID, item.getId());
                    NavHostFragment.findNavController(HomeFragment.this)
                            .navigate(R.id.purchaseDetailFragment, navArgs);
                    break;
                case PACKAGE:
                    navArgs.putLong(PackageDetailFragment.ARG_PACKAGE_ID, item.getId());
                    NavHostFragment.findNavController(HomeFragment.this)
                            .navigate(R.id.packageDetailFragment, navArgs);
                    break;
                case SHIPMENT:
                    if (item.getFirestoreId() != null && !item.getFirestoreId().isEmpty()) {
                        navArgs.putString(ShipmentDetailFragment.ARG_SHIPMENT_FS_ID, item.getFirestoreId());
                    } else {
                        navArgs.remove(ShipmentDetailFragment.ARG_SHIPMENT_FS_ID);
                    }
                    navArgs.putLong(ShipmentDetailFragment.ARG_SHIPMENT_LOCAL_ID, item.getId());
                    NavHostFragment.findNavController(HomeFragment.this)
                            .navigate(R.id.shipmentDetailFragment, navArgs);
                    break;
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        bannerHandler.removeCallbacksAndMessages(null);
        if (binding != null) {
            if (binding.rvBanner != null) {
                binding.rvBanner.setAdapter(null);
            }
            if (binding.rvActividadReciente != null) {
                binding.rvActividadReciente.setAdapter(null);
            }
        }
        bannerLayoutManager = null;
        binding = null;
    }
}
