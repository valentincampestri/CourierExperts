package com.courierexperts.demo.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.courierexperts.demo.R;
import com.courierexperts.demo.data.local.entity.PackageEntity;
import com.courierexperts.demo.databinding.ActivityPackageDetailBinding;
import com.courierexperts.demo.domain.StatusMapper;
import com.courierexperts.demo.ui.packages.PackageDetailUiState;
import com.courierexperts.demo.ui.packages.PackageDetailViewModel;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class PackageDetailFragment extends Fragment {

    public static final String ARG_PACKAGE_ID = "packageId";

    private ActivityPackageDetailBinding binding;
    private PackageDetailViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = ActivityPackageDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(PackageDetailViewModel.class);

        if (binding.bottomNav != null) {
            binding.bottomNav.setVisibility(View.GONE);
        }

        long packageId = 0L;
        Bundle args = getArguments();
        if (args != null) {
            packageId = args.getLong(ARG_PACKAGE_ID, 0L);
        }

        observeState();
        if (packageId > 0L) {
            viewModel.load(packageId);
        } else {
            showNotFoundState();
        }

        binding.btnBack.setOnClickListener(v ->
                NavHostFragment.findNavController(PackageDetailFragment.this).navigateUp());
    }

    private void observeState() {
        viewModel.getUiState().observe(getViewLifecycleOwner(), state -> {
            binding.ticketCard.setVisibility(View.GONE);
            if (state instanceof PackageDetailUiState.Success) {
                PackageEntity entity = ((PackageDetailUiState.Success) state).getEntity();
                render(entity);
            } else if (state instanceof PackageDetailUiState.NotFound) {
                Toast.makeText(requireContext(), R.string.package_detail_not_found, Toast.LENGTH_LONG).show();
                showNotFoundState();
            } else if (state instanceof PackageDetailUiState.Error) {
                Toast.makeText(requireContext(), R.string.state_error_retry, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void render(@Nullable PackageEntity entity) {
        if (entity == null || binding == null) return;
        binding.ticketCard.setVisibility(View.VISIBLE);

        String label = entity.label != null && !entity.label.isEmpty()
                ? entity.label
                : ("Paquete #" + entity.id);
        binding.tvSub.setText(label);
        binding.tvTicketTitle.setText(R.string.package_detail_ticket_title);

        String imageUrl = (entity.thumbnailUrl != null && !entity.thumbnailUrl.isEmpty())
                ? entity.thumbnailUrl
                : "https://picsum.photos/seed/pack" + entity.id + "/300/200";
        Glide.with(this).load(imageUrl).into(binding.ivThumb);

        String statusLabel = StatusMapper.labelPackage(entity.status);
        binding.tvStatus.setText(statusLabel);
        binding.tvStatus.setBackgroundResource(statusChipBg(entity.status));

        binding.tvTitle.setText(label);
        binding.tvDesc.setText(entity.description != null ? entity.description : "");
        //binding.tvPriceValue.setText(entity.price != null
            //    ? String.format(Locale.getDefault(), "$ %.2f", entity.price)
              //  : "");
        binding.tvPriceValue.setText(
                String.format(Locale.getDefault(), "$ %.2f", entity.price)
        );
        binding.tvDateValue.setText(formatDate(entity.lastUpdate));
    }

    private int statusChipBg(@Nullable String status) {
        if (status == null) return R.drawable.bg_status_chip_pending;
        switch (status.toUpperCase(Locale.ROOT)) {
            case "DELIVERED":
            case "RECEIVED":
                return R.drawable.bg_status_chip_delivered;
            case "IN_TRANSIT":
                return R.drawable.bg_status_chip_transit;
            case "SHIPPED":
                return R.drawable.bg_status_chip_delivered;
            case "CANCELLED":
                return R.drawable.bg_status_chip_cancelled;
            default:
                return R.drawable.bg_status_chip_pending;
        }
    }

    private void showNotFoundState() {
        if (binding == null) return;
        binding.ticketCard.setVisibility(View.GONE);
        binding.tvSub.setText(R.string.package_detail_not_found);
    }

    private static String formatDate(long epochMillis) {
        if (epochMillis <= 0) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        return sdf.format(new java.util.Date(epochMillis));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
