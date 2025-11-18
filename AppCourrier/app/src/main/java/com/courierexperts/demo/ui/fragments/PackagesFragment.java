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
import com.courierexperts.demo.databinding.ActivityPaquetesBinding;
import com.courierexperts.demo.ui.packages.PackageAdapter;
import com.courierexperts.demo.ui.packages.PackagesUiState;
import com.courierexperts.demo.ui.packages.PackagesViewModel;

public class PackagesFragment extends Fragment {

    private ActivityPaquetesBinding binding;
    private PackagesViewModel viewModel;
    private PackageAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = ActivityPaquetesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(PackagesViewModel.class);

        setupRecycler();
        observeUiState();
        viewModel.refresh();

        binding.btnSolicitar.setOnClickListener(v -> onSolicitarEnvio());

        if (binding.bottomNav != null) {
            binding.bottomNav.setVisibility(View.GONE);
        }
    }

    private void setupRecycler() {
        adapter = new PackageAdapter();
        binding.rvPaquetes.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvPaquetes.setAdapter(adapter);
        binding.btnSolicitar.setEnabled(false);
        binding.btnSolicitar.setVisibility(View.GONE);
        adapter.setOnSelectionChangeListener(count -> {
            boolean hasSelection = count > 0;
            binding.btnSolicitar.setEnabled(hasSelection);
            binding.btnSolicitar.setVisibility(hasSelection ? View.VISIBLE : View.GONE);
        });

        adapter.setOnItemClickListener(item -> {
            Bundle args = new Bundle();
            args.putLong(PackageDetailFragment.ARG_PACKAGE_ID, item.id);
            NavHostFragment.findNavController(PackagesFragment.this)
                    .navigate(R.id.packageDetailFragment, args);
        });
    }

    private void observeUiState() {
        viewModel.getUiState().observe(getViewLifecycleOwner(), state -> {
            binding.progressBar.setVisibility(View.GONE);
            binding.tvStateMessage.setVisibility(View.GONE);
            if (state instanceof PackagesUiState.Loading) {
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.rvPaquetes.setVisibility(View.GONE);
                binding.btnSolicitar.setEnabled(false);
                binding.btnSolicitar.setVisibility(View.GONE);
            } else if (state instanceof PackagesUiState.Success) {
                binding.rvPaquetes.setVisibility(View.VISIBLE);
                adapter.submit(((PackagesUiState.Success) state).getPackages());
            } else if (state instanceof PackagesUiState.Empty) {
                binding.tvStateMessage.setVisibility(View.VISIBLE);
                binding.tvStateMessage.setText(R.string.packages_empty_message);
                binding.rvPaquetes.setVisibility(View.GONE);
                binding.btnSolicitar.setEnabled(false);
                binding.btnSolicitar.setVisibility(View.GONE);
            } else if (state instanceof PackagesUiState.Error) {
                binding.tvStateMessage.setVisibility(View.VISIBLE);
                binding.tvStateMessage.setText(R.string.state_error_retry);
                binding.rvPaquetes.setVisibility(View.GONE);
                binding.btnSolicitar.setEnabled(false);
                binding.btnSolicitar.setVisibility(View.GONE);
                Toast.makeText(requireContext(), R.string.state_error_retry, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void onSolicitarEnvio() {
        java.util.List<Long> ids = adapter.getSelectedIds();
        if (ids == null || ids.isEmpty()) {
            Toast.makeText(requireContext(), "Selecciona al menos un paquete", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(requireContext(), "Creando envío con " + ids.size() + " paquete(s)...", Toast.LENGTH_SHORT).show();

        com.courierexperts.demo.data.repository.ShipmentRepository repo =
                new com.courierexperts.demo.data.repository.ShipmentRepository(requireContext());
        repo.createShipment(ids, new com.courierexperts.demo.data.repository.ShipmentRepository.Callback() {
            @Override public void onSuccess(long shipmentId) {
                NavHostFragment.findNavController(PackagesFragment.this).navigate(R.id.shipmentsFragment);
            }
            @Override public void onHttpError(int code) {
                Toast.makeText(requireContext(), R.string.state_error_retry, Toast.LENGTH_LONG).show();
            }
            @Override public void onOffline() {
                Toast.makeText(requireContext(), R.string.state_error_retry, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (binding != null && binding.rvPaquetes != null) {
            binding.rvPaquetes.setAdapter(null);
        }
        binding = null;
        adapter = null;
    }
}
