package com.courierexperts.demo.ui.packages;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.courierexperts.demo.data.local.entity.PackageEntity;
import com.courierexperts.demo.data.repository.PackageRepository;

import java.util.List;

public class PackagesViewModel extends AndroidViewModel {

    private final PackageRepository repo;
    private final LiveData<List<PackageEntity>> packages;

    public PackagesViewModel(@NonNull Application app) {
        super(app);
        repo = new PackageRepository(app);
        packages = repo.observeAllOrdered();
    }

    public LiveData<List<PackageEntity>> getPackages() { return packages; }
    public void refresh() { repo.refreshFromNetwork(); }
}
