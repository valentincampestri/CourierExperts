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
import com.courierexperts.demo.data.local.entity.PurchaseEntity;
import com.courierexperts.demo.databinding.ActivityPurchaseDetailBinding;
import com.courierexperts.demo.domain.StatusMapper;
import com.courierexperts.demo.ui.purchases.PurchaseDetailUiState;
import com.courierexperts.demo.ui.purchases.PurchaseDetailViewModel;
import com.courierexperts.demo.util.ClipboardUtils;
import com.courierexperts.demo.util.CurrencyUtils;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class PurchaseDetailFragment extends Fragment {

    public static final String ARG_PURCHASE_ID = "purchaseId";

    private ActivityPurchaseDetailBinding binding;
    private PurchaseDetailViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = ActivityPurchaseDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(PurchaseDetailViewModel.class);

        long id = 0L;
        Bundle args = getArguments();
        if (args != null) {
            id = args.getLong(ARG_PURCHASE_ID, 0L);
        }
        observeUiState();
        if (id > 0L) {
            viewModel.load(id);
        } else {
            showNotFoundState();
        }

        if (binding.btnBack != null) {
            binding.btnBack.setOnClickListener(v ->
                    NavHostFragment.findNavController(PurchaseDetailFragment.this).navigateUp());
        }
    }

    private void observeUiState() {
        viewModel.getUiState().observe(getViewLifecycleOwner(), state -> {
            if (state instanceof PurchaseDetailUiState.Success) {
                PurchaseEntity p = ((PurchaseDetailUiState.Success) state).getPurchase();
                renderDetail(p);
            } else if (state instanceof PurchaseDetailUiState.NotFound) {
                Toast.makeText(requireContext(), R.string.purchase_detail_not_found, Toast.LENGTH_LONG).show();
                showNotFoundState();
            } else if (state instanceof PurchaseDetailUiState.Error) {
                Toast.makeText(requireContext(), R.string.state_error_retry, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void renderDetail(@Nullable PurchaseEntity p) {
        if (p == null || binding == null) return;

        String statusLabel = StatusMapper.labelPurchase(p.status);
        binding.tvSub.setText(safe(p.storeName));
        binding.tvProductNameValue.setText(safe(p.productName));
        binding.tvProductDescriptionValue.setText(safe(p.description));
        binding.tvPriceValue.setText(p.price != null ? CurrencyUtils.formatUSD(p.price) : "N/A");
        binding.tvStoreValue.setText(safe(p.storeName));
        binding.tvOrderValue.setText(safe(p.orderId));
        
        
        binding.tvOrderValue.setOnLongClickListener(v -> {
            String orderId = safe(p.orderId);
            if (!orderId.isEmpty()) {
                ClipboardUtils.copyToClipboard(requireContext(), "Order ID", orderId);
            }
            return true;
        });
        binding.tvDateValue.setText(formatDate(p.createdAt));

        Glide.with(requireContext())
                .load(p.thumbnailUrl)
                .into(binding.imgCompraDetalle);

        applyStatusChip(binding.tvEstado, statusLabel);
    }

    private void showNotFoundState() {
        if (binding == null) return;
        binding.tvSub.setText(getString(R.string.purchase_detail_not_found));
        binding.tvStoreValue.setText("");
        binding.tvOrderValue.setText("");
        binding.tvDateValue.setText("");
        binding.tvEstado.setText("");
    }

    private static String safe(String s) {
        return s != null ? s : "";
    }

    private static String formatDate(long epoch) {
        SimpleDateFormat sdf =
                new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        return sdf.format(new java.util.Date(epoch));
    }

    private void applyStatusChip(@Nullable TextView view, String statusLabel) {
        if (view == null) return;

        view.setText(statusLabel);

        String s = statusLabel.toLowerCase(Locale.getDefault());
        int bgRes;

        if (s.contains("pendiente")) {
            bgRes = R.drawable.bg_status_chip_pending;
        } else if (s.contains("entreg")
                || s.contains("despach")
                || s.contains("recibid")) {
            bgRes = R.drawable.bg_status_chip_delivered;
        } else {
            bgRes = R.drawable.bg_status_chip_pending;
        }

        view.setBackgroundResource(bgRes);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
