package com.courierexperts.demo;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.courierexperts.demo.databinding.ActivityFragmentsHostBinding;

public class FragmentsHostActivity extends AppCompatActivity {

    private ActivityFragmentsHostBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFragmentsHostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment == null) {
            throw new IllegalStateException("NavHostFragment not found");
        }
        NavController navController = navHostFragment.getNavController();

        // Configurar FAB para agregar nueva compra
        binding.fabAdd.setOnClickListener(v -> {
            navController.navigate(R.id.newPurchaseFragment);
        });

        binding.bottomNavHost.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                boolean popped = navController.popBackStack(R.id.nav_home, false);
                if (!popped || navController.getCurrentDestination() == null
                        || navController.getCurrentDestination().getId() != R.id.nav_home) {
                    navController.navigate(R.id.nav_home);
                }
                return true;
            } else if (itemId == R.id.nav_profile) {
                if (navController.getCurrentDestination() == null
                        || navController.getCurrentDestination().getId() != R.id.nav_profile) {
                    navController.navigate(R.id.nav_profile);
                }
                return true;
            }
            return NavigationUI.onNavDestinationSelected(item, navController);
        });

        binding.bottomNavHost.setOnItemReselectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                boolean popped = navController.popBackStack(R.id.nav_home, false);
                if (!popped || navController.getCurrentDestination() == null
                        || navController.getCurrentDestination().getId() != R.id.nav_home) {
                    navController.navigate(R.id.nav_home);
                }
            } else if (itemId == R.id.nav_profile) {
                if (navController.getCurrentDestination() == null
                        || navController.getCurrentDestination().getId() != R.id.nav_profile) {
                    navController.navigate(R.id.nav_profile);
                }
            }
        });

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.nav_home
                    || destination.getId() == R.id.purchasesFragment
                    || destination.getId() == R.id.packagesFragment
                    || destination.getId() == R.id.shipmentsFragment) {
                binding.bottomNavHost.getMenu().findItem(R.id.nav_home).setChecked(true);
            } else if (destination.getId() == R.id.nav_profile) {
                binding.bottomNavHost.getMenu().findItem(R.id.nav_profile).setChecked(true);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
