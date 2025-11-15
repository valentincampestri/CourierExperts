package com.courierexperts.demo.ui.purchases;

import static com.courierexperts.demo.domain.StatusMapper.PurchaseStatus.CANCELLED;
import static com.courierexperts.demo.domain.StatusMapper.PurchaseStatus.DELIVERED;
import static com.courierexperts.demo.domain.StatusMapper.PurchaseStatus.PENDING;
import static com.courierexperts.demo.domain.StatusMapper.PurchaseStatus.RECEIVED;
import static com.courierexperts.demo.domain.StatusMapper.PurchaseStatus.SHIPPED;

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

        // Texto
        h.b.tvStore.setText(it.storeName);
        h.b.tvOrder.setText(it.orderId);

        // Obtenemos el label a mostrar y el enum de estado
        StatusMapper.PurchaseStatus statusEnum = StatusMapper.purchaseFrom(it.status);
        String statusLabel = StatusMapper.label(statusEnum); // o StatusMapper.labelPurchase(it.status)
        h.b.tvStatus.setText(statusLabel);

        // Imagen
        Glide.with(h.b.getRoot())
                .load(it.thumbnailUrl)
                .into(h.b.ivThumb);

        // Color del chip según estado
        switch (statusEnum) {
            case PENDING:
                // Pendiente → amarillo
                h.b.tvStatus.setBackgroundResource(R.drawable.bg_status_chip_pending);
                break;

            case RECEIVED:
            case SHIPPED:
            case DELIVERED:
                // Todo lo que está "en curso" o "terminado" → verde
                h.b.tvStatus.setBackgroundResource(R.drawable.bg_status_chip_delivered);
                break;

            case CANCELLED:
            default:
                // Cancelada u otros → también amarillo (si querés después hacemos un color gris/rojo aparte)
                h.b.tvStatus.setBackgroundResource(R.drawable.bg_status_chip_pending);
                break;
        }

        // Click del item
        h.b.getRoot().setOnClickListener(v -> {
            if (listener != null) listener.onClick(it);
        });
    }



    @Override public int getItemCount() { return items.size(); }
}
