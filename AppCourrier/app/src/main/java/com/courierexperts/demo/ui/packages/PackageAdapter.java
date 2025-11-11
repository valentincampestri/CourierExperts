package com.courierexperts.demo.ui.packages;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.courierexperts.demo.data.local.entity.PackageEntity;
import com.courierexperts.demo.databinding.ItemPackageBinding;

import java.util.ArrayList;
import java.util.List;

public class PackageAdapter extends RecyclerView.Adapter<PackageAdapter.VH> {

    public interface OnItemClickListener { void onClick(PackageEntity item); }
    private OnItemClickListener listener;
    public void setOnItemClickListener(OnItemClickListener l) { this.listener = l; }

    private final List<PackageEntity> items = new ArrayList<>();
    public void submit(List<PackageEntity> data) {
        items.clear();
        if (data != null) items.addAll(data);
        notifyDataSetChanged();
    }

    static class VH extends RecyclerView.ViewHolder {
        ItemPackageBinding b;
        VH(ItemPackageBinding b) { super(b.getRoot()); this.b = b; }
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPackageBinding b = ItemPackageBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new VH(b);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        PackageEntity it = items.get(position);
        h.b.tvLabel.setText(it.label);
        h.b.tvStatus.setText(it.status);
        h.b.tvDesc.setText(it.description);
        Glide.with(h.b.getRoot()).load(it.thumbnailUrl).into(h.b.ivThumb);

        h.b.getRoot().setOnClickListener(v -> { if (listener != null) listener.onClick(it); });
    }

    @Override public int getItemCount() { return items.size(); }
}
