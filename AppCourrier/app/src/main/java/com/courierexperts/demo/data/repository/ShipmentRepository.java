package com.courierexperts.demo.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.courierexperts.demo.data.local.dao.PackageDao;
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

    public interface Callback {
        void onSuccess(long shipmentId);
        void onHttpError(int code);
        void onOffline();
    }

<<<<<<< Updated upstream
    public void createShipment(List<Long> packageIds, Callback cb) {
        AppExecutors.io().execute(() -> {
            try {
                com.courierexperts.demo.domain.model.CreateShipmentRequest req =
                        new com.courierexperts.demo.domain.model.CreateShipmentRequest(packageIds);
                Response<Shipment> resp = RetrofitClient.api(app).createShipment(req).execute();
                if (resp.isSuccessful() && resp.body() != null) {
                    Shipment s = resp.body();
                    ShipmentEntity e = new ShipmentEntity();
                    e.id = s.id;
                    e.title = s.title;
                    e.trackingNumber = s.trackingNumber;
                    e.status = s.status;
                    e.lastUpdate = s.lastUpdate;
                    e.thumbnailUrl = s.thumbnailUrl;
                    List<ShipmentEntity> one = new ArrayList<>();
=======
    public LiveData<List<ShipmentEntity>> observeShipments() {
        ensureListener();
        return dao.observeAll();
    }

    public LiveData<String> getErrors() {
        return remoteErrors;
    }

    public void refreshFromNetwork() {
        ensureListener();
    }

    private void ensureListener() {
        if (shipmentsListener != null) return;
        String uid = safeUid();
        if (uid == null) return;
        Query q = FirebaseFirestore.getInstance()
                .collection("users").document(uid)
                .collection("shipments")
                .orderBy("lastUpdate", Query.Direction.DESCENDING);

        shipmentsListener = q.addSnapshotListener((snapshots, e) -> {
            if (e != null) {
                remoteErrors.postValue(e.getMessage());
                return;
            }
            if (snapshots == null) {
                remoteErrors.postValue("Respuesta vacia de Firestore");
                return;
            }
            List<ShipmentEntity> list = new ArrayList<>();
            for (DocumentSnapshot d : snapshots.getDocuments()) {
                ShipmentEntity se = mapDoc(d);
                if (se != null) list.add(se);
            }
            AppExecutors.io().execute(() -> dao.upsertAll(list));
        });
    }

    private ShipmentEntity mapDoc(DocumentSnapshot d) {
        if (d == null || !d.exists()) return null;
        ShipmentEntity e = new ShipmentEntity();
        e.fsId = d.getId();
        Long existingId = null;
        try { existingId = dao.findLocalIdByFsId(e.fsId); } catch (Exception ignored) {}
        e.id = existingId != null ? existingId : stableLongFromString(e.fsId);
        e.title = safeStr(d.getString("title"));
        e.trackingNumber = safeStr(d.getString("trackingNumber"));
        String status = d.getString("status");
        e.status = status != null ? status : "CREATED";
        com.google.firebase.Timestamp ts = d.getTimestamp("lastUpdate");
        e.lastUpdate = ts != null ? ts.toDate().getTime() : System.currentTimeMillis();
        e.thumbnailUrl = safeStr(d.getString("thumbnailUrl"));
        Object pkgField = d.get("packageIds");
        java.util.List<String> pkgIds = new java.util.ArrayList<>();
        if (pkgField instanceof java.util.List) {
            for (Object o : (java.util.List<?>) pkgField) {
                if (o != null) pkgIds.add(String.valueOf(o));
            }
        }
        e.packageIdsJson = toJsonArray(pkgIds);
        Double cost = null;
        try { cost = d.getDouble("cost"); } catch (Exception ignored) {}
        e.cost = cost != null ? cost : 0d;
        return e;
    }

    private static String toJsonArray(java.util.List<String> ids) {
        if (ids == null) return "[]";
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < ids.size(); i++) {
            if (i > 0) sb.append(',');
            sb.append('"').append(ids.get(i).replace("\"","\\\"")).append('"');
        }
        sb.append(']');
        return sb.toString();
    }

    private static long stableLongFromString(String s) {
        long h = 1125899906842597L;
        for (int i = 0; i < s.length(); i++) h = 31*h + s.charAt(i);
        return h & Long.MAX_VALUE;
    }

    private static String safeStr(String s) { return s != null ? s : ""; }

    private double calculateShipmentCost(java.util.List<Long> packageIds, PackageDao dao) {
        if (packageIds == null || packageIds.isEmpty() || dao == null) return 0d;
        try {
            List<Double> prices = dao.findPricesByIds(packageIds);
            double total = 0d;
            if (prices != null) {
                for (Double price : prices) {
                    if (price != null) total += price;
                }
            }
            return roundCost(total * 0.2d);
        } catch (Exception ignored) {
            return 0d;
        }
    }

    private static double roundCost(double value) {
        return Math.round(value * 100d) / 100d;
    }

    private static String safeUid() {
        try { return FirebaseAuth.getInstance().getUid(); } catch (Exception ex) { return null; }
    }

    public void createShipment(java.util.List<Long> packageIds, Callback cb) {
        AppExecutors.io().execute(() -> {
            try {
                String uid = safeUid();
                if (uid == null) { AppExecutors.main().execute(cb::onOffline); return; }

                // Map local package IDs (long) -> Firestore doc ids (String)
                PackageDao pdao = AppDatabase.get(app).packageDao();
                java.util.List<String> pkgFsIds = new java.util.ArrayList<>();
                if (packageIds != null) {
                    for (Long pid : packageIds) {
                        if (pid == null) continue;
                        String fsid = pdao.getFsIdByLocalId(pid);
                        if (fsid != null && !fsid.isEmpty()) pkgFsIds.add(fsid);
                    }
                }
                double shipmentCost = calculateShipmentCost(packageIds, pdao);

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference shDoc = db.collection("users").document(uid)
                        .collection("shipments").document();

                java.util.HashMap<String, Object> sh = new java.util.HashMap<>();
                String id = shDoc.getId();
                sh.put("id", id);
                sh.put("title", "Envío " + id.substring(0, Math.min(6, id.length())));
                sh.put("trackingNumber", String.format(java.util.Locale.US, "TRK-%06d", 100000 + new java.util.Random().nextInt(900000)));
                sh.put("status", "CREATED");
                sh.put("lastUpdate", FieldValue.serverTimestamp());
                sh.put("thumbnailUrl", "https://picsum.photos/seed/newship/96/96");
                sh.put("packageIds", pkgFsIds);
                sh.put("cost", shipmentCost);

                WriteBatch batch = db.batch();
                batch.set(shDoc, sh);
                for (String pkgId : pkgFsIds) {
                    DocumentReference pdoc = db.collection("users").document(uid)
                            .collection("packages").document(pkgId);
                    java.util.Map<String, Object> upd = new java.util.HashMap<>();
                    upd.put("shipmentId", id);
                    upd.put("status", "IN_TRANSIT");
                    upd.put("lastUpdate", FieldValue.serverTimestamp());
                    batch.update(pdoc, upd);
                }

                batch.commit()
                        .addOnSuccessListener(v -> AppExecutors.main().execute(() -> cb.onSuccess(stableLongFromString(id))))
                        .addOnFailureListener(err -> AppExecutors.main().execute(cb::onOffline));

            } catch (Exception ex) {
                // Offline: crear envío local y marcar packages localmente como IN_TRANSIT + shipmentId local
                AppExecutors.io().execute(() -> {
                    String localId = "local-" + System.currentTimeMillis();
                    ShipmentEntity e = new ShipmentEntity();
                    e.id = stableLongFromString(localId);
                    e.fsId = localId;
                    e.title = "Envío " + localId.substring(Math.max(0, localId.length()-6));
                    e.trackingNumber = String.format(java.util.Locale.US, "TRK-%06d", (int)(System.currentTimeMillis()%1000000));
                    e.status = "CREATED";
                    e.lastUpdate = System.currentTimeMillis();
                    e.thumbnailUrl = "";
                    // build local package ids list for json
                    PackageDao pdao = AppDatabase.get(app).packageDao();
                    java.util.List<String> pkgJsonIds = new java.util.ArrayList<>();
                    if (packageIds != null) {
                        for (Long pid : packageIds) {
                            if (pid == null) continue;
                            String fs = null;
                            try { fs = pdao.getFsIdByLocalId(pid); } catch (Exception ignore) {}
                            pkgJsonIds.add(fs != null && !fs.isEmpty() ? fs : String.valueOf(pid));
                        }
                    }
                    double shipmentCost = calculateShipmentCost(packageIds, pdao);
                    e.packageIdsJson = toJsonArray(pkgJsonIds);
                    e.cost = shipmentCost;
                    java.util.List<ShipmentEntity> one = new java.util.ArrayList<>();
>>>>>>> Stashed changes
                    one.add(e);
                    dao.upsertAll(one);
                    AppExecutors.main().execute(() -> cb.onSuccess(s.id));
                } else {
                    int code = resp != null ? resp.code() : 500;
                    AppExecutors.main().execute(() -> cb.onHttpError(code));
                }
            } catch (Exception e) {
                AppExecutors.main().execute(cb::onOffline);
            }
        });
    }
}
