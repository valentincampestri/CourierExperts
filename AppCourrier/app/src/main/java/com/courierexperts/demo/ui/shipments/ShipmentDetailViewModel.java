package com.courierexperts.demo.ui.shipments;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.courierexperts.demo.data.local.db.AppDatabase;
import com.courierexperts.demo.data.local.entity.PackageEntity;
import com.courierexperts.demo.data.local.entity.ShipmentEntity;

import java.util.List;

public class ShipmentDetailViewModel extends AndroidViewModel {

    private final AppDatabase db;
    private final MediatorLiveData<ShipmentDetailUiState> uiState = new MediatorLiveData<>();
    private LiveData<ShipmentEntity> shipmentSource;
    private LiveData<List<PackageEntity>> packagesSource;

    public ShipmentDetailViewModel(@NonNull Application application) {
        super(application);
        db = AppDatabase.get(application);
        uiState.setValue(new ShipmentDetailUiState.Loading());
    }

    public LiveData<ShipmentDetailUiState> getUiState() {
        return uiState;
    }

    public void loadByFirestoreId(String fsId) {
        detachSources();
        uiState.setValue(new ShipmentDetailUiState.Loading());

        shipmentSource = db.shipmentDao().observeByFsId(fsId);
        packagesSource = db.packageDao().observeByShipmentId(fsId);
        attachSources();
    }

    public void loadByLocalId(long localId) {
        detachSources();
        if (localId <= 0) {
            uiState.setValue(new ShipmentDetailUiState.NotFound());
            return;
        }
        uiState.setValue(new ShipmentDetailUiState.Loading());
        shipmentSource = db.shipmentDao().observeById(localId);
        packagesSource = db.packageDao().observeByShipmentId(String.valueOf(localId));
        attachSources();
    }

    private void attachSources() {
        if (shipmentSource != null) {
            uiState.addSource(shipmentSource, shipment -> emitState(shipment, packagesSource != null ? packagesSource.getValue() : null));
        }
        if (packagesSource != null) {
            uiState.addSource(packagesSource, packages -> emitState(shipmentSource != null ? shipmentSource.getValue() : null, packages));
        }
    }

    private void detachSources() {
        if (shipmentSource != null) {
            uiState.removeSource(shipmentSource);
            shipmentSource = null;
        }
        if (packagesSource != null) {
            uiState.removeSource(packagesSource);
            packagesSource = null;
        }
    }

    private void emitState(ShipmentEntity shipment, List<PackageEntity> packages) {
        if (shipment == null) {
            uiState.setValue(new ShipmentDetailUiState.NotFound());
            return;
        }
        uiState.setValue(new ShipmentDetailUiState.Success(shipment, packages != null ? packages : java.util.Collections.emptyList()));
    }
}
