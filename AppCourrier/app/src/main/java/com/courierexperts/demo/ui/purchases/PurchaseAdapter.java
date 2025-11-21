package com.courierexperts.demo.ui.purchases;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.courierexperts.demo.R;
import com.courierexperts.demo.data.local.entity.PurchaseEntity;
import com.courierexperts.demo.domain.StatusMapper;
import com.courierexperts.demo.databinding.ItemPurchaseBinding;

import java.util.ArrayList;
import java.util.List;

public class PurchaseAdapter extends RecyclerView.Adapter<PurchaseAdapter.VH> {

    public interface OnItemClickListener {
        void onClick(PurchaseEntity item);
    }

    private OnItemClickListener listener;
    public void setOnItemClickListener(OnItemClickListener l) {
        this.listener = l;
    }

    private final List<PurchaseEntity> items = new ArrayList<>();

    public void submit(List<PurchaseEntity> data) {
        items.clear();
        if (data != null) items.addAll(data);
        notifyDataSetChanged();
    }

    static class VH extends RecyclerView.ViewHolder {
        ItemPurchaseBinding b;
        VH(ItemPurchaseBinding b) {
            super(b.getRoot());
            this.b = b;
        }
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPurchaseBinding b = ItemPurchaseBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new VH(b);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        PurchaseEntity it = items.get(position);

        h.b.tvProductName.setText(it.productName);
        h.b.tvProductDescription.setText(it.description);

        StatusMapper.PurchaseStatus statusEnum = StatusMapper.purchaseFrom(it.status);

        String statusLabel = StatusMapper.labelPurchase(it.status);
        h.b.tvStatus.setText(statusLabel);

        switch (statusEnum) {
            case PENDING:
                h.b.tvStatus.setBackgroundResource(R.drawable.bg_status_chip_pending);
                break;

            case RECEIVED:
            case SHIPPED:
            case DELIVERED:
                h.b.tvStatus.setBackgroundResource(R.drawable.bg_status_chip_delivered);
                break;

            case CANCELLED:
            default:
                h.b.tvStatus.setBackgroundResource(R.drawable.bg_status_chip_pending);
                break;
        }

        Glide.with(h.b.getRoot())
                .load(it.thumbnailUrl)
                .into(h.b.ivThumb);

        h.b.getRoot().setOnClickListener(v -> {
            if (listener != null) listener.onClick(it);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
