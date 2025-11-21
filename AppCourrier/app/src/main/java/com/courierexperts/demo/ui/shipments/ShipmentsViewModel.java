package com.courierexperts.demo.ui.shipments;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.courierexperts.demo.data.local.entity.ShipmentEntity;
import com.courierexperts.demo.data.repository.ShipmentRepository;

import java.util.List;

public class ShipmentsViewModel extends AndroidViewModel {

    private final ShipmentRepository repo;
    private final MediatorLiveData<ShipmentsUiState> uiState = new MediatorLiveData<>();

    private boolean isDataLoaded = false;

    public ShipmentsViewModel(@NonNull Application app) {
        super(app);
        repo = new ShipmentRepository(app);

        uiState.setValue(new ShipmentsUiState.Loading());

        LiveData<List<ShipmentEntity>> shipmentsSource = repo.observeShipments();
        uiState.addSource(shipmentsSource, list -> {
            if (list == null || list.isEmpty()) {
                uiState.setValue(new ShipmentsUiState.Empty());
            } else {
                isDataLoaded = true;
                uiState.setValue(new ShipmentsUiState.Success(list));
            }
        });

        LiveData<String> errorSource = repo.getErrors();
        uiState.addSource(errorSource, message -> {
            if (!isDataLoaded && message != null && !message.trim().isEmpty()) {
                uiState.setValue(new ShipmentsUiState.Error(message));
            }
        });
    }

    public LiveData<ShipmentsUiState> getUiState() { return uiState; }

    public void refresh() {
        ShipmentsUiState current = uiState.getValue();
        boolean showingData = current instanceof ShipmentsUiState.Success;

        if (!showingData) {
            uiState.setValue(new ShipmentsUiState.Loading());
        }

        repo.refreshFromNetwork();
    }
}