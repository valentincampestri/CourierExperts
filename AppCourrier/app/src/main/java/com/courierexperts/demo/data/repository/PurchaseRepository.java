package com.courierexperts.demo.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.courierexperts.demo.data.local.dao.PurchaseDao;
import com.courierexperts.demo.data.local.db.AppDatabase;
import com.courierexperts.demo.data.local.entity.PurchaseEntity;
import com.courierexperts.demo.data.remote.RetrofitClient;
import com.courierexperts.demo.domain.model.Purchase;
import com.courierexperts.demo.util.AppExecutors;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

public class PurchaseRepository {

    private final PurchaseDao dao;
    private final Context app;

    public PurchaseRepository(Context ctx) {
        this.app = ctx.getApplicationContext();
        this.dao = AppDatabase.get(this.app).purchaseDao();
    }

    /** Observa la lista desde Room y, en paralelo, hace refresh desde red (mock por ahora). */
    public LiveData<List<PurchaseEntity>> observePurchases() {
        refreshFromNetwork();
        return dao.observeAll();
    }

    /** Descarga (mock/real), mapea y actualiza Room. */
    public void refreshFromNetwork() {
        AppExecutors.io().execute(() -> {
            try {
                Response<List<Purchase>> resp = RetrofitClient.api(app).getPurchases().execute();
                if (resp.isSuccessful() && resp.body() != null) {
                    List<PurchaseEntity> list = new ArrayList<>();
                    for (Purchase p : resp.body()) {
                        PurchaseEntity e = new PurchaseEntity();
                        e.id = p.id;
                        e.storeName = p.storeName;
                        e.orderId = p.orderId;
                        e.status = p.status;
                        e.createdAt = p.createdAt;
                        e.thumbnailUrl = p.thumbnailUrl;
                        list.add(e);
                    }
                    dao.clear();
                    dao.upsertAll(list);
                }
            } catch (Exception e) {
                // Podés loguear si querés: Log.e("Repository", "refresh error", e);
            }
        });
    }
}
