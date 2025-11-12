package com.courierexperts.demo.ui.packages;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.courierexperts.demo.data.local.entity.PackageEntity;
import com.courierexperts.demo.databinding.ItemPackageBinding;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PackageAdapter extends RecyclerView.Adapter<PackageAdapter.VH> {

    public interface OnItemClickListener { void onClick(PackageEntity item); }
    private OnItemClickListener listener;
    public void setOnItemClickListener(OnItemClickListener l) { this.listener = l; }

    public interface OnSelectionChangeListener { void onSelectionChanged(int count); }
    private OnSelectionChangeListener selectionListener;
    public void setOnSelectionChangeListener(OnSelectionChangeListener l) { this.selectionListener = l; }

    private final List<PackageEntity> items = new ArrayList<>();
    private final Set<Long> selectedIds = new HashSet<>();
    public void submit(List<PackageEntity> data) {
        items.clear();
        if (data != null) items.addAll(data);
        selectedIds.clear();
        notifyDataSetChanged();
        if (selectionListener != null) selectionListener.onSelectionChanged(0);
    }

    public List<Long> getSelectedIds() { return new ArrayList<>(selectedIds); }

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

        // checkbox state
        boolean checked = selectedIds.contains(it.id);
        h.b.cbSelect.setOnCheckedChangeListener(null);
        h.b.cbSelect.setChecked(checked);
        h.b.cbSelect.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) selectedIds.add(it.id); else selectedIds.remove(it.id);
            if (selectionListener != null) selectionListener.onSelectionChanged(selectedIds.size());
        });

        // click en fila mantiene navegación existente
        h.b.getRoot().setOnClickListener(v -> { if (listener != null) listener.onClick(it); });
    }

    @Override public int getItemCount() { return items.size(); }
}
