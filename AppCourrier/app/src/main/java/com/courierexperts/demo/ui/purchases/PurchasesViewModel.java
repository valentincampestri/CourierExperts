package com.courierexperts.demo.ui.purchases;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.courierexperts.demo.data.local.entity.PurchaseEntity;
import com.courierexperts.demo.data.repository.PurchaseRepository;

import java.util.List;

public class PurchasesViewModel extends AndroidViewModel {

    private final PurchaseRepository repo;
    private final LiveData<List<PurchaseEntity>> purchases;

    public PurchasesViewModel(@NonNull Application app) {
        super(app);
        repo = new PurchaseRepository(app);
        purchases = repo.observePurchases();
    }

    public LiveData<List<PurchaseEntity>> getPurchases() {
        return purchases;
    }

    public void refresh() {
        repo.refreshFromNetwork();
    }
}
