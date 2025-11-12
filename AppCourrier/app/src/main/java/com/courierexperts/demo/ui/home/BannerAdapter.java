package com.courierexperts.demo.ui.home;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.courierexperts.demo.databinding.ItemBannerBinding;

import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.VH> {
    private final List<Integer> drawables;
    public BannerAdapter(List<Integer> drawables) { this.drawables = drawables; }

    static class VH extends RecyclerView.ViewHolder {
        ItemBannerBinding b;
        VH(ItemBannerBinding b) { super(b.getRoot()); this.b = b; }
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemBannerBinding b = ItemBannerBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new VH(b);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        if (drawables == null || drawables.isEmpty()) return;
        int idx = position % drawables.size();
        h.b.ivBanner.setImageResource(drawables.get(idx));
        h.b.ivBanner.setScaleType(ImageView.ScaleType.FIT_CENTER);
    }

    @Override public int getItemCount() { return drawables == null ? 0 : Integer.MAX_VALUE; }
}

