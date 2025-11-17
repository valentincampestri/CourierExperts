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
import com.courierexperts.demo.databinding.ActivityEnviosBinding;
import com.courierexperts.demo.ui.shipments.ShipmentAdapter;
import com.courierexperts.demo.ui.shipments.ShipmentsUiState;
import com.courierexperts.demo.ui.shipments.ShipmentsViewModel;

public class ShipmentsFragment extends Fragment {

    private ActivityEnviosBinding binding;
    private ShipmentsViewModel viewModel;
    private ShipmentAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = ActivityEnviosBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ShipmentsViewModel.class);
        setupRecycler();
        observeUiState();
        viewModel.refresh();
        if (binding.bottomNav != null) {
            binding.bottomNav.setVisibility(View.GONE);
        }
    }

    private void setupRecycler() {
        adapter = new ShipmentAdapter();
        binding.rvEnvios.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvEnvios.setAdapter(adapter);
        adapter.setOnItemClickListener(item -> {
            Bundle args = new Bundle();
            if (item.fsId != null && !item.fsId.isEmpty()) {
                args.putString(ShipmentDetailFragment.ARG_SHIPMENT_FS_ID, item.fsId);
            }
            args.putLong(ShipmentDetailFragment.ARG_SHIPMENT_LOCAL_ID, item.id);
            NavHostFragment.findNavController(ShipmentsFragment.this)
                    .navigate(R.id.shipmentDetailFragment, args);
        });
    }

    private void observeUiState() {
        viewModel.getUiState().observe(getViewLifecycleOwner(), state -> {
            binding.progressBar.setVisibility(View.GONE);
            binding.tvStateMessage.setVisibility(View.GONE);

            if (state instanceof ShipmentsUiState.Loading) {
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.rvEnvios.setVisibility(View.GONE);
            } else if (state instanceof ShipmentsUiState.Success) {
                binding.rvEnvios.setVisibility(View.VISIBLE);
                adapter.submit(((ShipmentsUiState.Success) state).getShipments());
            } else if (state instanceof ShipmentsUiState.Empty) {
                binding.tvStateMessage.setVisibility(View.VISIBLE);
                binding.tvStateMessage.setText(R.string.envios_empty);
                binding.rvEnvios.setVisibility(View.GONE);
            } else if (state instanceof ShipmentsUiState.Error) {
                binding.tvStateMessage.setVisibility(View.VISIBLE);
                binding.tvStateMessage.setText(R.string.state_error_retry);
                binding.rvEnvios.setVisibility(View.GONE);
                Toast.makeText(requireContext(), R.string.state_error_retry, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (binding != null && binding.rvEnvios != null) {
            binding.rvEnvios.setAdapter(null);
        }
        binding = null;
        adapter = null;
    }
}
