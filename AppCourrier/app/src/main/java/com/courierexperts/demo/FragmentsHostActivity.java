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

        binding.bottomNavHost.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_add) {
                navController.navigate(R.id.newPurchaseFragment);
                return false;
            }
            return NavigationUI.onNavDestinationSelected(item, navController);
        });

        binding.bottomNavHost.setOnItemReselectedListener(item -> {
            if (item.getItemId() == R.id.nav_add) {
                navController.navigate(R.id.newPurchaseFragment);
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
