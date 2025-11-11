package com.courierexperts.demo.ui.shipments;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.courierexperts.demo.data.local.entity.ShipmentEntity;
import com.courierexperts.demo.data.repository.ShipmentRepository;

import java.util.List;

public class ShipmentsViewModel extends AndroidViewModel {

    private final ShipmentRepository repo;
    private final LiveData<List<ShipmentEntity>> shipments;

    public ShipmentsViewModel(@NonNull Application app) {
        super(app);
        repo = new ShipmentRepository(app);
        shipments = repo.observeShipments();
    }

    public LiveData<List<ShipmentEntity>> getShipments() { return shipments; }
    public void refresh() { repo.refreshFromNetwork(); }
}
