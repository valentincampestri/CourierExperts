package com.courierexperts.demo.ui.purchases;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.courierexperts.demo.data.local.entity.PurchaseEntity;
import com.courierexperts.demo.data.repository.PurchaseRepository;

import java.util.List;

public class PurchasesViewModel extends AndroidViewModel {

    private final PurchaseRepository repo;
    private final MediatorLiveData<PurchasesUiState> uiState = new MediatorLiveData<>();

    public PurchasesViewModel(@NonNull Application app) {
        super(app);
        repo = new PurchaseRepository(app);
        uiState.setValue(new PurchasesUiState.Loading());

        LiveData<List<PurchaseEntity>> purchasesSource = repo.observePurchases();
        uiState.addSource(purchasesSource, list -> {
            if (list == null || list.isEmpty()) {
                uiState.setValue(new PurchasesUiState.Empty());
            } else {
                uiState.setValue(new PurchasesUiState.Success(list));
            }
        });

        LiveData<String> errorSource = repo.getErrors();
        uiState.addSource(errorSource, message -> {
            if (message != null && !message.trim().isEmpty()) {
                uiState.setValue(new PurchasesUiState.Error(message));
            }
        });
    }

    public LiveData<PurchasesUiState> getUiState() {
        return uiState;
    }

    public void refresh() {
        uiState.setValue(new PurchasesUiState.Loading());
        repo.refreshFromNetwork();
    }

    public void syncPendingIfNetworkAvailable() {
        repo.syncPendingIfNetworkAvailable();
    }
}
