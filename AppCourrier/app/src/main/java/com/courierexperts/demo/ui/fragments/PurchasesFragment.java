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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.courierexperts.demo.R;
import com.courierexperts.demo.databinding.ActivityComprasBinding;
import com.courierexperts.demo.ui.purchases.PurchaseAdapter;
import com.courierexperts.demo.ui.purchases.PurchasesUiState;
import com.courierexperts.demo.ui.purchases.PurchasesViewModel;

public class PurchasesFragment extends Fragment {

    private ActivityComprasBinding binding;
    private PurchasesViewModel viewModel;
    private PurchaseAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = ActivityComprasBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(PurchasesViewModel.class);

        setupRecycler();
        observeUiState();
        viewModel.refresh();
        setupFab();

        if (binding.bottomNav != null) {
            binding.bottomNav.setVisibility(View.GONE);
        }
    }

    private void setupRecycler() {
        adapter = new PurchaseAdapter();
        binding.rvCompras.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvCompras.setAdapter(adapter);

        adapter.setOnItemClickListener(item -> {
            Bundle args = new Bundle();
            args.putLong(PurchaseDetailFragment.ARG_PURCHASE_ID, item.id);
            NavHostFragment.findNavController(PurchasesFragment.this)
                    .navigate(R.id.purchaseDetailFragment, args);
        });
    }

    private void setupFab() {
        View fab = binding.getRoot().findViewById(R.id.fabAdd);
        if (fab != null) {
            fab.setOnClickListener(v ->
                    NavHostFragment.findNavController(PurchasesFragment.this)
                            .navigate(R.id.action_purchasesFragment_to_newPurchaseFragment));
        }
    }

    private void observeUiState() {
        viewModel.getUiState().observe(getViewLifecycleOwner(), state -> {
            binding.progressBar.setVisibility(View.GONE);
            binding.tvStateMessage.setVisibility(View.GONE);

            if (state instanceof PurchasesUiState.Loading) {
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.rvCompras.setVisibility(View.GONE);
            } else if (state instanceof PurchasesUiState.Success) {
                binding.rvCompras.setVisibility(View.VISIBLE);
                adapter.submit(((PurchasesUiState.Success) state).getPurchases());
            } else if (state instanceof PurchasesUiState.Empty) {
                binding.tvStateMessage.setVisibility(View.VISIBLE);
                binding.tvStateMessage.setText(R.string.purchases_empty_message);
                binding.rvCompras.setVisibility(View.GONE);
            } else if (state instanceof PurchasesUiState.Error) {
                binding.tvStateMessage.setVisibility(View.VISIBLE);
                binding.tvStateMessage.setText(R.string.state_error_retry);
                binding.rvCompras.setVisibility(View.GONE);
                Toast.makeText(requireContext(), R.string.state_error_retry, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (viewModel != null) {
            viewModel.syncPendingIfNetworkAvailable();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (binding != null) {
            binding.rvCompras.setAdapter(null);
        }
        binding = null;
        adapter = null;
    }
}
