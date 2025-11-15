package com.courierexperts.demo.ui.packages;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.courierexperts.demo.data.local.entity.PackageEntity;
import com.courierexperts.demo.data.repository.PackageRepository;

import java.util.List;

public class PackagesViewModel extends AndroidViewModel {

    private final PackageRepository repo;
    private final MediatorLiveData<PackagesUiState> uiState = new MediatorLiveData<>();

    public PackagesViewModel(@NonNull Application app) {
        super(app);
        repo = new PackageRepository(app);
        uiState.setValue(new PackagesUiState.Loading());

        LiveData<List<PackageEntity>> packagesSource = repo.observeAllOrdered();
        uiState.addSource(packagesSource, list -> {
            if (list == null || list.isEmpty()) {
                uiState.setValue(new PackagesUiState.Empty());
            } else {
                uiState.setValue(new PackagesUiState.Success(list));
            }
        });

        LiveData<String> errorSource = repo.getErrors();
        uiState.addSource(errorSource, message -> {
            if (message != null && !message.trim().isEmpty()) {
                uiState.setValue(new PackagesUiState.Error(message));
            }
        });
    }

    public LiveData<PackagesUiState> getUiState() { return uiState; }

    public void refresh() {
        uiState.setValue(new PackagesUiState.Loading());
        repo.refreshFromNetwork();
    }
}
