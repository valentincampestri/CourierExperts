package com.courierexperts.demo.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.courierexperts.demo.data.local.dao.PackageDao;
import com.courierexperts.demo.data.local.db.AppDatabase;
import com.courierexperts.demo.data.local.entity.PackageEntity;
import com.courierexperts.demo.data.remote.RetrofitClient;
import com.courierexperts.demo.domain.model.UserPackage;
import com.courierexperts.demo.util.AppExecutors;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

public class PackageRepository {

    private final PackageDao dao;
    private final Context app;

    public PackageRepository(Context ctx) {
        this.app = ctx.getApplicationContext();
        this.dao = AppDatabase.get(this.app).packageDao();
    }

    public LiveData<List<PackageEntity>> observePackages() {
        refreshFromNetwork();
        return dao.observeAll();
    }

    public void refreshFromNetwork() {
        AppExecutors.io().execute(() -> {
            try {
                Response<List<UserPackage>> resp = RetrofitClient.api(app).getPackages().execute();
                if (resp.isSuccessful() && resp.body() != null) {
                    List<PackageEntity> list = new ArrayList<>();
                    for (UserPackage p : resp.body()) {
                        PackageEntity e = new PackageEntity();
                        e.id = p.id;
                        e.label = p.label;
                        e.description = p.description;
                        e.status = p.status;
                        e.lastUpdate = p.lastUpdate;
                        e.thumbnailUrl = p.thumbnailUrl;
                        list.add(e);
                    }
                    dao.clear();
                    dao.upsertAll(list);
                }
            } catch (Exception ignored) { }
        });
    }
}
