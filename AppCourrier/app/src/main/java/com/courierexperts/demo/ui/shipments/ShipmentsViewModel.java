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

    // Flag para saber si ya cargamos datos exitosamente al menos una vez
    private boolean isDataLoaded = false;

    public ShipmentsViewModel(@NonNull Application app) {
        super(app);
        repo = new ShipmentRepository(app);

        // Estado inicial Loading (solo la primera vez)
        uiState.setValue(new ShipmentsUiState.Loading());

        LiveData<List<ShipmentEntity>> shipmentsSource = repo.observeShipments();
        uiState.addSource(shipmentsSource, list -> {
            if (list == null || list.isEmpty()) {
                uiState.setValue(new ShipmentsUiState.Empty());
            } else {
                isDataLoaded = true; // Marcamos que ya tenemos datos
                uiState.setValue(new ShipmentsUiState.Success(list));
            }
        });

        LiveData<String> errorSource = repo.getErrors();
        uiState.addSource(errorSource, message -> {
            // Solo mostramos error en pantalla completa si NO tenemos datos previos.
            // Si ya hay datos, mejor mostrar un Toast o ignorar el error para no borrar la lista.
            if (!isDataLoaded && message != null && !message.trim().isEmpty()) {
                uiState.setValue(new ShipmentsUiState.Error(message));
            }
        });
    }

    public LiveData<ShipmentsUiState> getUiState() { return uiState; }

    public void refresh() {
        // CORRECCIÓN: Evitar el "Loading Infinito" al volver del detalle.

        ShipmentsUiState current = uiState.getValue();
        boolean showingData = current instanceof ShipmentsUiState.Success;

        if (!showingData) {
            // Solo ponemos Loading si la pantalla estaba vacía
            uiState.setValue(new ShipmentsUiState.Loading());
        }

        // Si ya había datos, esto corre en segundo plano ("Silent refresh")
        // y si hay cambios, Room notificará automáticamente.
        repo.refreshFromNetwork();
    }
}