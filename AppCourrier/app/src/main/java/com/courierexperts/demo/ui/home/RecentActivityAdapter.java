package com.courierexperts.demo.ui.home;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.courierexperts.demo.R;
import com.courierexperts.demo.databinding.ItemRecentActivityBinding;

import java.util.ArrayList;
import java.util.List;

public class RecentActivityAdapter extends RecyclerView.Adapter<RecentActivityAdapter.VH> {

    public interface OnItemClickListener {
        void onClick(RecentActivityItem item);
    }

    private OnItemClickListener listener;
    public void setOnItemClickListener(OnItemClickListener l) {
        this.listener = l;
    }

    private final List<RecentActivityItem> items = new ArrayList<>();

    public void submit(List<RecentActivityItem> data) {
        items.clear();
        if (data != null) items.addAll(data);
        notifyDataSetChanged();
    }

    static class VH extends RecyclerView.ViewHolder {
        ItemRecentActivityBinding b;
        VH(ItemRecentActivityBinding binding) {
            super(binding.getRoot());
            this.b = binding;
        }
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRecentActivityBinding b = ItemRecentActivityBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new VH(b);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        RecentActivityItem it = items.get(position);

        // Icono según tipo (ya viene resuelto en iconResId)
        h.b.ivTypeIcon.setImageResource(it.getIconResId());

        // Títulos / texto
        h.b.tvTitle.setText(it.getTitle() != null ? it.getTitle() : "");
        h.b.tvSubtitle.setText(it.getSubtitle() != null ? it.getSubtitle() : "");
        h.b.tvStatus.setText(it.getStatusLabel() != null ? it.getStatusLabel() : "");
        h.b.tvDate.setText(it.getDateLabel() != null ? it.getDateLabel() : "");

        // (Opcional) pill de estado: color según texto
        if (it.getStatusLabel() != null) {
            String s = it.getStatusLabel().toLowerCase();
            int bgRes;
            if (s.contains("entreg") || s.contains("recibid")) {
                bgRes = R.drawable.bg_status_chip_delivered;
            } else if (s.contains("tránsito") || s.contains("transit")) {
                bgRes = R.drawable.bg_status_chip_transit;
            } else if (s.contains("cancel")) {
                bgRes = R.drawable.bg_status_chip_cancelled;
            } else {
                bgRes = R.drawable.bg_status_chip_pending;
            }
            h.b.tvStatus.setBackgroundResource(bgRes);
        }

        // Click en la card
        h.b.getRoot().setOnClickListener(v -> {
            if (listener != null) listener.onClick(it);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
