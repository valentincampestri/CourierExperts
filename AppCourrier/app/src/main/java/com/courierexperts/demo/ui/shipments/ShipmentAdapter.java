package com.courierexperts.demo.ui.shipments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.courierexperts.demo.R;
import com.courierexperts.demo.data.local.entity.ShipmentEntity;
import com.courierexperts.demo.domain.StatusMapper;
import com.courierexperts.demo.databinding.ItemShipmentBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ShipmentAdapter extends RecyclerView.Adapter<ShipmentAdapter.VH> {

    public interface OnItemClickListener {
        void onClick(ShipmentEntity item);
    }

    private final List<ShipmentEntity> items = new ArrayList<>();
    private OnItemClickListener listener;

    public void submit(List<ShipmentEntity> data) {
        items.clear();
        if (data != null) items.addAll(data);
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener l) { this.listener = l; }

    static class VH extends RecyclerView.ViewHolder {
        ItemShipmentBinding b;
        VH(ItemShipmentBinding b) { super(b.getRoot()); this.b = b; }
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemShipmentBinding b = ItemShipmentBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new VH(b);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        ShipmentEntity it = items.get(position);

        h.b.tvTitle.setText(it.title);
        String subtitle = "#" + (it.trackingNumber != null ? it.trackingNumber : "");
        if (it.fsId != null && it.fsId.startsWith("local-")) {
            subtitle += " - sin conexion";
        }
        h.b.tvSubtitle.setText(subtitle);

        String formattedCost = String.format(Locale.getDefault(), "$ %.2f", it.cost);
        h.b.tvCost.setText(h.b.getRoot().getContext()
                .getString(R.string.shipment_cost_label, formattedCost));

        h.b.tvStatus.setText(StatusMapper.labelShipment(it.status));

        Glide.with(h.b.getRoot())
                .load(it.thumbnailUrl)
                .into(h.b.ivThumb);

        h.b.getRoot().setOnClickListener(v -> {
            if (listener != null) listener.onClick(it);
        });
    }

    @Override
    public int getItemCount() { return items.size(); }
}
