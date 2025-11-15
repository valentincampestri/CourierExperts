package com.courierexperts.demo;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.courierexperts.demo.databinding.ActivityPackageDetailBinding;
import com.courierexperts.demo.domain.StatusMapper;
import com.courierexperts.demo.ui.packages.PackageDetailUiState;
import com.courierexperts.demo.ui.packages.PackageDetailViewModel;

public class PackageDetailActivity extends AppCompatActivity {

    private ActivityPackageDetailBinding binding;
    private PackageDetailViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPackageDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(PackageDetailViewModel.class);

        binding.toolbar.setNavigationOnClickListener(v -> finish());

        long packageId = getIntent().getLongExtra("packageId", -1);
        observeState();
        viewModel.load(packageId);
    }

    private void observeState() {
        viewModel.getUiState().observe(this, state -> {
            binding.progressBar.setVisibility(android.view.View.GONE);
            binding.tvStateMessage.setVisibility(android.view.View.GONE);
            binding.ivThumb.setVisibility(android.view.View.GONE);
            binding.tvTitle.setVisibility(android.view.View.GONE);
            binding.tvStatus.setVisibility(android.view.View.GONE);
            binding.tvDesc.setVisibility(android.view.View.GONE);

            if (state instanceof PackageDetailUiState.Loading) {
                binding.progressBar.setVisibility(android.view.View.VISIBLE);
            } else if (state instanceof PackageDetailUiState.Success) {
                render(((PackageDetailUiState.Success) state).getEntity());
            } else if (state instanceof PackageDetailUiState.NotFound) {
                binding.tvStateMessage.setVisibility(android.view.View.VISIBLE);
                binding.tvStateMessage.setText(R.string.package_detail_not_found);
            } else if (state instanceof PackageDetailUiState.Error) {
                binding.tvStateMessage.setVisibility(android.view.View.VISIBLE);
                binding.tvStateMessage.setText(R.string.state_error_retry);
            }
        });
    }

    private void render(com.courierexperts.demo.data.local.entity.PackageEntity entity) {
        if (entity == null) return;
        binding.ivThumb.setVisibility(android.view.View.VISIBLE);
        binding.tvTitle.setVisibility(android.view.View.VISIBLE);
        binding.tvStatus.setVisibility(android.view.View.VISIBLE);
        binding.tvDesc.setVisibility(android.view.View.VISIBLE);

        binding.tvTitle.setText(entity.label != null ? entity.label : ("Paquete #" + entity.id));
        binding.tvStatus.setText("Estado: " + StatusMapper.labelPackage(entity.status));
        binding.tvDesc.setText("Descripción: " + (entity.description != null ? entity.description : ""));
        String imageUrl = entity.thumbnailUrl != null && !entity.thumbnailUrl.isEmpty()
                ? entity.thumbnailUrl
                : "https://picsum.photos/seed/pack" + entity.id + "/300/200";
        Glide.with(this).load(imageUrl).into(binding.ivThumb);
    }
}
