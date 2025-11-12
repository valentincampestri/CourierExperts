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
                        e.pendingSync = false;
                        list.add(e);
                    }
                    dao.upsertAll(list);
                }
            } catch (Exception e) {
                // Podés loguear si querés: Log.e("Repository", "refresh error", e);
            }
        });
    }

    /** Crea localmente y trata de sincronizar con backend (mock). */
    public void createLocalAndSync(String storeName, String orderId, String createdAtIso) {
        AppExecutors.io().execute(() -> {
            try {
                PurchaseEntity e = new PurchaseEntity();
                e.id = 0; // autogenerado local
                e.storeName = storeName;
                e.orderId = orderId;
                e.status = "pending";
                e.createdAt = createdAtIso;
                e.thumbnailUrl = "";
                long localId = dao.insert(e);

                // Intentar POST
                Purchase payload = new Purchase(0, storeName, orderId, e.status, createdAtIso, e.thumbnailUrl);
                try {
                    Response<Purchase> resp = RetrofitClient.api(app).createPurchase(payload).execute();
                    if (resp.isSuccessful() && resp.body() != null) {
                        Purchase p = resp.body();
                        // Reemplazar registro local con el id del servidor
                        PurchaseEntity server = new PurchaseEntity();
                        server.id = p.id; // usar id del server
                        server.storeName = p.storeName;
                        server.orderId = p.orderId;
                        server.status = p.status;
                        server.createdAt = p.createdAt;
                        server.thumbnailUrl = p.thumbnailUrl;
                        server.pendingSync = false;
                        dao.deleteById(localId);
                        List<PurchaseEntity> list = new ArrayList<>();
                        list.add(server);
                        dao.upsertAll(list);
                    }
                } catch (Exception ignored) {
                    // Offline: queda local con id autogenerado
                }
            } catch (Exception ignored) { }
        });
    }

    public void syncPendingIfNetworkAvailable() {
        if (!com.courierexperts.demo.util.NetworkUtils.isOnline(app)) return;
        AppExecutors.io().execute(() -> {
            List<PurchaseEntity> pendings;
            try {
                pendings = dao.listPending();
            } catch (Exception e) { return; }
            if (pendings == null || pendings.isEmpty()) return;
            for (PurchaseEntity e : pendings) {
                try {
                    Purchase payload = new Purchase(0, e.storeName, e.orderId, e.status, e.createdAt, e.thumbnailUrl);
                    Response<Purchase> resp = RetrofitClient.api(app).createPurchase(payload).execute();
                    if (resp.isSuccessful() && resp.body() != null) {
                        Purchase p = resp.body();
                        dao.deleteById(e.id);
                        PurchaseEntity server = new PurchaseEntity();
                        server.id = p.id;
                        server.storeName = p.storeName;
                        server.orderId = p.orderId;
                        server.status = p.status;
                        server.createdAt = p.createdAt;
                        server.thumbnailUrl = p.thumbnailUrl;
                        server.pendingSync = false;
                        List<PurchaseEntity> list = new ArrayList<>();
                        list.add(server);
                        dao.upsertAll(list);
                    }
                } catch (Exception ignored) { }
            }
        });
    }

    
}
