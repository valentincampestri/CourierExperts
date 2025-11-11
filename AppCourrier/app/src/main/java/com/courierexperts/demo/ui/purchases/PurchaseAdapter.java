package com.courierexperts.demo.ui.purchases;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.courierexperts.demo.data.local.entity.PurchaseEntity;
import com.courierexperts.demo.databinding.ItemPurchaseBinding;

import java.util.ArrayList;
import java.util.List;

public class PurchaseAdapter extends RecyclerView.Adapter<PurchaseAdapter.VH> {

    public interface OnItemClickListener { void onClick(PurchaseEntity item); }
    private OnItemClickListener listener;
    public void setOnItemClickListener(OnItemClickListener l) { this.listener = l; }

    private final List<PurchaseEntity> items = new ArrayList<>();
    public void submit(List<PurchaseEntity> data) {
        items.clear();
        if (data != null) items.addAll(data);
        notifyDataSetChanged();
    }

    static class VH extends RecyclerView.ViewHolder {
        ItemPurchaseBinding b;
        VH(ItemPurchaseBinding b) { super(b.getRoot()); this.b = b; }
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPurchaseBinding b = ItemPurchaseBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new VH(b);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        PurchaseEntity it = items.get(position);
        h.b.tvStore.setText(it.storeName);
        h.b.tvOrder.setText(it.orderId);
        h.b.tvStatus.setText(it.status);
        Glide.with(h.b.getRoot()).load(it.thumbnailUrl).into(h.b.ivThumb);

        h.b.getRoot().setOnClickListener(v -> { if (listener != null) listener.onClick(it); });
    }

    @Override public int getItemCount() { return items.size(); }
}
