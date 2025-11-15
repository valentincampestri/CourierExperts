package com.courierexperts.demo.ui.purchases;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.courierexperts.demo.data.local.entity.PurchaseEntity;
import com.courierexperts.demo.data.repository.PurchaseRepository;

public class PurchaseDetailViewModel extends AndroidViewModel {

    private final PurchaseRepository repo;
    private final MediatorLiveData<PurchaseDetailUiState> uiState = new MediatorLiveData<>();
    private LiveData<PurchaseEntity> currentSource;

    public PurchaseDetailViewModel(@NonNull Application application) {
        super(application);
        repo = new PurchaseRepository(application);
        uiState.setValue(new PurchaseDetailUiState.Loading());
    }

    public LiveData<PurchaseDetailUiState> getUiState() {
        return uiState;
    }

    public void load(long purchaseId) {
        if (currentSource != null) {
            uiState.removeSource(currentSource);
        }
        if (purchaseId <= 0) {
            uiState.setValue(new PurchaseDetailUiState.NotFound());
            return;
        }
        uiState.setValue(new PurchaseDetailUiState.Loading());
        currentSource = repo.observePurchaseById(purchaseId);
        uiState.addSource(currentSource, entity -> {
            if (entity == null) {
                uiState.setValue(new PurchaseDetailUiState.NotFound());
            } else {
                uiState.setValue(new PurchaseDetailUiState.Success(entity));
            }
        });
    }
}
