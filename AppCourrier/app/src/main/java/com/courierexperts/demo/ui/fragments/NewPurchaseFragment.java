package com.courierexperts.demo.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.courierexperts.demo.databinding.ActivityNuevaCompraBinding;
import com.courierexperts.demo.ui.purchases.NewPurchaseEvent;
import com.courierexperts.demo.ui.purchases.NewPurchaseUiState;
import com.courierexperts.demo.ui.purchases.NewPurchaseViewModel;
import com.google.android.material.textfield.TextInputEditText;

public class NewPurchaseFragment extends Fragment {

    private ActivityNuevaCompraBinding binding;
    private NewPurchaseViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = ActivityNuevaCompraBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(NewPurchaseViewModel.class);

        if (binding.bottomNav != null) {
            binding.bottomNav.setVisibility(View.GONE);
        }
        observeViewModel();
        if (binding.btnCancelarnuevaCompra != null) {
            binding.btnCancelarnuevaCompra.setOnClickListener(v ->
                    NavHostFragment.findNavController(NewPurchaseFragment.this).popBackStack());
        }
        if (binding.btnGuardarNuevaCompra != null) {
            binding.btnGuardarNuevaCompra.setOnClickListener(v -> savePurchase());
        }
    }

    private void observeViewModel() {
        viewModel.getUiState().observe(getViewLifecycleOwner(), state -> {
            boolean loading = state instanceof NewPurchaseUiState.Loading;
            if (binding.btnGuardarNuevaCompra != null) {
                binding.btnGuardarNuevaCompra.setEnabled(!loading);
            }
            if (binding.progressBar != null) {
                binding.progressBar.setVisibility(
                        loading ? View.VISIBLE : View.GONE
                );
            }
        });

        viewModel.getEvents().observe(getViewLifecycleOwner(), event -> {
            if (event == null) return;
            NewPurchaseEvent payload = event.getContentIfNotHandled();
            if (payload == null) return;

            if (payload.getType() == NewPurchaseEvent.Type.SHOW_MESSAGE) {
                if (payload.getMessage() != null) {
                    Toast.makeText(requireContext(), payload.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else if (payload.getType() == NewPurchaseEvent.Type.SUCCESS) {
                if (payload.getMessage() != null) {
                    Toast.makeText(requireContext(), payload.getMessage(), Toast.LENGTH_SHORT).show();
                }
                NavHostFragment.findNavController(NewPurchaseFragment.this).popBackStack();
            }
        });
    }

    private void savePurchase() {
        String productName = textOf(binding.etProductName);
        String description = textOf(binding.etDescription);
        String storeName   = textOf(binding.etStoreName);
        String carrierName = textOf(binding.etCarrierName);
        String orderId     = textOf(binding.etOrderId);
        String priceStr    = textOf(binding.etPrice);

        viewModel.savePurchase(
                productName,
                description,
                storeName,
                carrierName,
                priceStr,
                orderId
        );
    }

    private static String textOf(@Nullable TextInputEditText et) {
        if (et != null && et.getText() != null) {
            return et.getText().toString().trim();
        }
        return "";
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
