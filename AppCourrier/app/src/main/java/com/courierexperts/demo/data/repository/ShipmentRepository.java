package com.courierexperts.demo.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.courierexperts.demo.data.local.dao.ShipmentDao;
import com.courierexperts.demo.data.local.db.AppDatabase;
import com.courierexperts.demo.data.local.entity.ShipmentEntity;
import com.courierexperts.demo.data.remote.RetrofitClient;
import com.courierexperts.demo.domain.model.Shipment;
import com.courierexperts.demo.util.AppExecutors;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

public class ShipmentRepository {

    private final ShipmentDao dao;
    private final Context app;

    public ShipmentRepository(Context ctx) {
        this.app = ctx.getApplicationContext();
        this.dao = AppDatabase.get(this.app).shipmentDao();
    }

    public LiveData<List<ShipmentEntity>> observeShipments() {
        refreshFromNetwork();
        return dao.observeAll();
    }

    public void refreshFromNetwork() {
        AppExecutors.io().execute(() -> {
            try {
                Response<List<Shipment>> resp = RetrofitClient.api(app).getShipments().execute();
                if (resp.isSuccessful() && resp.body() != null) {
                    List<ShipmentEntity> list = new ArrayList<>();
                    for (Shipment s : resp.body()) {
                        ShipmentEntity e = new ShipmentEntity();
                        e.id = s.id;
                        e.title = s.title;
                        e.trackingNumber = s.trackingNumber;
                        e.status = s.status;
                        e.lastUpdate = s.lastUpdate;
                        e.thumbnailUrl = s.thumbnailUrl;
                        list.add(e);
                    }
                    dao.clear();
                    dao.upsertAll(list);
                }
            } catch (Exception ignored) { }
        });
    }
}
