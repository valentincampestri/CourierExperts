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
import com.courierexperts.demo.domain.StatusMapper;
import com.courierexperts.demo.ui.packages.PackageAdapter;
import com.courierexperts.demo.ui.shipments.ShipmentDetailUiState;
import com.courierexperts.demo.ui.shipments.ShipmentDetailViewModel;

public class ShipmentDetailFragment extends Fragment {

    public static final String ARG_SHIPMENT_FS_ID = "shipmentFsId";
    public static final String ARG_SHIPMENT_LOCAL_ID = "shipmentLocalId";

    private ActivityEnviosBinding binding;
    private ShipmentDetailViewModel viewModel;
    private PackageAdapter adapter;

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
        viewModel = new ViewModelProvider(this).get(ShipmentDetailViewModel.class);

        adapter = new PackageAdapter();
        adapter.setSelectionEnabled(false);
        binding.rvEnvios.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvEnvios.setAdapter(adapter);

        adapter.setOnItemClickListener(item -> {
            Bundle args = new Bundle();
            args.putLong(PackageDetailFragment.ARG_PACKAGE_ID, item.id);
            NavHostFragment.findNavController(ShipmentDetailFragment.this)
                    .navigate(R.id.packageDetailFragment, args);
        });

        observeUiState();

        Bundle args = getArguments();
        if (args != null) {
            String fsId = args.getString(ARG_SHIPMENT_FS_ID);
            long localId = args.getLong(ARG_SHIPMENT_LOCAL_ID, -1);
            if (fsId != null && !fsId.isEmpty()) {
                viewModel.loadByFirestoreId(fsId);
            } else {
                viewModel.loadByLocalId(localId);
            }
        } else {
            showNotFound();
        }

        if (binding.bottomNav != null) {
            binding.bottomNav.setVisibility(View.GONE);
        }
    }

    private void observeUiState() {
        viewModel.getUiState().observe(getViewLifecycleOwner(), state -> {
            binding.progressBar.setVisibility(View.GONE);
            binding.tvStateMessage.setVisibility(View.GONE);

            if (state instanceof ShipmentDetailUiState.Loading) {
                binding.progressBar.setVisibility(View.VISIBLE);
            } else if (state instanceof ShipmentDetailUiState.Success) {
                render(((ShipmentDetailUiState.Success) state));
            } else if (state instanceof ShipmentDetailUiState.NotFound) {
                showNotFound();
            } else if (state instanceof ShipmentDetailUiState.Error) {
                binding.tvStateMessage.setVisibility(View.VISIBLE);
                binding.tvStateMessage.setText(R.string.state_error_retry);
                binding.rvEnvios.setVisibility(View.GONE);
                Toast.makeText(requireContext(), R.string.state_error_retry, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void render(ShipmentDetailUiState.Success state) {
        binding.tvEnvios.setText(state.getShipment().title != null ? state.getShipment().title : "Envio");
        binding.tvMensaje.setText(StatusMapper.labelShipment(state.getShipment().status));
        adapter.submit(state.getPackages());
        binding.rvEnvios.setVisibility(View.VISIBLE);
    }

    private void showNotFound() {
        binding.tvStateMessage.setVisibility(View.VISIBLE);
        binding.tvStateMessage.setText(R.string.shipment_detail_not_found);
        binding.rvEnvios.setVisibility(View.GONE);
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
