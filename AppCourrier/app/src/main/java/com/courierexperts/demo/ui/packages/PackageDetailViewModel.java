package com.courierexperts.demo.ui.packages;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.courierexperts.demo.data.local.db.AppDatabase;
import com.courierexperts.demo.data.local.entity.PackageEntity;

public class PackageDetailViewModel extends AndroidViewModel {

    private final AppDatabase db;
    private final MediatorLiveData<PackageDetailUiState> uiState = new MediatorLiveData<>();
    private LiveData<PackageEntity> currentSource;

    public PackageDetailViewModel(@NonNull Application application) {
        super(application);
        db = AppDatabase.get(application);
        uiState.setValue(new PackageDetailUiState.Loading());
    }

    public LiveData<PackageDetailUiState> getUiState() {
        return uiState;
    }

    public void load(long packageId) {
        if (currentSource != null) {
            uiState.removeSource(currentSource);
            currentSource = null;
        }
        if (packageId <= 0) {
            uiState.setValue(new PackageDetailUiState.NotFound());
            return;
        }
        uiState.setValue(new PackageDetailUiState.Loading());
        currentSource = db.packageDao().observeById(packageId);
        uiState.addSource(currentSource, entity -> {
            if (entity == null) {
                uiState.setValue(new PackageDetailUiState.NotFound());
            } else {
                uiState.setValue(new PackageDetailUiState.Success(entity));
            }
        });
    }
}
