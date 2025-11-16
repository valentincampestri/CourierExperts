package com.courierexperts.demo.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.courierexperts.demo.data.local.dao.PackageDao;
import com.courierexperts.demo.data.local.dao.PurchaseDao;
import com.courierexperts.demo.data.local.db.AppDatabase;
import com.courierexperts.demo.data.local.entity.PackageEntity;
import com.courierexperts.demo.data.local.entity.PurchaseEntity;
import com.courierexperts.demo.util.AppExecutors;
import com.courierexperts.demo.util.NetworkUtils;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.Query;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class PurchaseRepository {

    private final PurchaseDao dao;
    private final PackageDao packageDao;
    private final Context app;
    private ListenerRegistration purchasesListener;
    private final MutableLiveData<String> remoteErrors = new MutableLiveData<>();

    public PurchaseRepository(Context ctx) {
        this.app = ctx.getApplicationContext();
        AppDatabase db = AppDatabase.get(this.app);
        this.dao = db.purchaseDao();
        this.packageDao = db.packageDao();
    }

    /** Observa la lista desde Room y, en paralelo, hace refresh desde red. */
    public LiveData<List<PurchaseEntity>> observePurchases() {
        ensureListener();
        return dao.observeAll();
    }

    public LiveData<String> getErrors() {
        return remoteErrors;
    }

    public LiveData<PurchaseEntity> observePurchaseById(long id) {
        ensureListener();
        return dao.observeById(id);
    }

    /** Descarga (mock/real), mapea y actualiza Room. */
    public void refreshFromNetwork() {
        ensureListener();
    }

    /**
     * Crea localmente y trata de sincronizar con backend.
     * Ahora soporta: productName, storeName, carrierName, price, orderId, description.
     */
    public void createLocalAndSync(
            String productName,
            String storeName,
            String carrierName,
            Double price,
            String orderId,
            String description,
            String createdAtIso
    ) {
        final String uid = safeUid();
        final long createdAtEpoch = parseIsoToEpoch(createdAtIso);

        if (uid == null) {
            // Sin usuario → guardar solo local, pendingSync
            AppExecutors.io().execute(() -> {
                PurchaseEntity e = new PurchaseEntity();
                e.id = 0;
                e.fsId = null;
                e.productName = productName;
                e.storeName = storeName;
                e.carrierName = carrierName;
                e.price = price != null ? price : 0d;
                e.orderId = orderId;
                e.status = "PENDING";
                e.description = description;
                e.createdAt = createdAtEpoch;
                e.thumbnailUrl = "";
                e.pendingSync = true;
                dao.insert(e);
            });
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference doc = db.collection("users").document(uid)
                .collection("purchases").document();

        java.util.HashMap<String, Object> data = new java.util.HashMap<>();
        data.put("id", doc.getId());
        data.put("productName", productName);
        data.put("storeName", storeName);
        data.put("carrierName", carrierName);
        data.put("price", price != null ? price : 0d);
        data.put("orderId", orderId);
        data.put("status", "PENDING");
        data.put("description", description);
        data.put("createdAt", FieldValue.serverTimestamp());
        data.put("thumbnailUrl", "");

        doc.set(data)
                .addOnSuccessListener(v ->
                        doc.get().addOnSuccessListener(this::upsertFromSnapshot)
                )
                .addOnFailureListener(err ->
                        AppExecutors.io().execute(() -> {
                            // Offline / error → guardar local con pendingSync
                            PurchaseEntity e = new PurchaseEntity();
                            e.id = 0;
                            e.fsId = doc.getId();
                            e.productName = productName;
                            e.storeName = storeName;
                            e.carrierName = carrierName;
                            e.price = price != null ? price : 0d;
                            e.orderId = orderId;
                            e.status = "PENDING";
                            e.description = description;
                            e.createdAt = createdAtEpoch;
                            e.thumbnailUrl = "";
                            e.pendingSync = true;
                            dao.insert(e);
                        })
                );
    }

    public void syncPendingIfNetworkAvailable() {
        if (!NetworkUtils.isOnline(app)) return;
        AppExecutors.io().execute(() -> {
            String uid = safeUid();
            if (uid == null) return;
            List<PurchaseEntity> pending = dao.findPendingSync();
            if (pending == null || pending.isEmpty()) return;
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            for (PurchaseEntity entity : pending) {
                DocumentReference doc = preparePendingDocument(db, uid, entity);
                if (doc == null) continue;
                java.util.Map<String, Object> data = buildPendingPayload(entity);
                doc.set(data)
                        .addOnSuccessListener(v -> doc.get().addOnSuccessListener(this::upsertFromSnapshot))
                        .addOnFailureListener(err -> remoteErrors.postValue("Sync pendientes: " + err.getMessage()));
            }
        });
    }

    private static long parseIsoToEpoch(String iso) {
        if (iso == null || iso.isEmpty()) return System.currentTimeMillis();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            return sdf.parse(iso).getTime();
        } catch (ParseException e) {
            return System.currentTimeMillis();
        }
    }

    private static String toIso(long epochMillis) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(new java.util.Date(epochMillis));
    }

    private void ensureListener() {
        if (purchasesListener != null) return;
        final String uid = safeUid();
        if (uid == null) return;
        Query q = FirebaseFirestore.getInstance()
                .collection("users").document(uid)
                .collection("purchases")
                .orderBy("createdAt", Query.Direction.DESCENDING);

        purchasesListener = q.addSnapshotListener(MetadataChanges.EXCLUDE, (snapshots, e) -> {
            if (e != null) {
                remoteErrors.postValue("Firestore: " + e.getMessage());
                return;
            }
            if (snapshots == null) {
                remoteErrors.postValue("Respuesta vacia de Firestore.");
                return;
            }
            List<PurchaseEntity> list = new ArrayList<>();
            for (DocumentSnapshot d : snapshots.getDocuments()) {
                PurchaseEntity pe = mapDoc(d);
                if (pe != null) list.add(pe);
            }
            AppExecutors.io().execute(() -> dao.upsertAll(list));
        });
    }

    private PurchaseEntity mapDoc(DocumentSnapshot d) {
        if (d == null || !d.exists()) return null;
        PurchaseEntity e = new PurchaseEntity();
        e.fsId = d.getId();
        Long existingId = null;
        try {
            existingId = dao.findLocalIdByFsId(e.fsId);
        } catch (Exception ignored) {}
        e.id = existingId != null ? existingId : stableLongFromString(e.fsId);

        // 🔹 Campos nuevos
        e.productName = d.getString("productName");
        e.storeName = d.getString("storeName");
        e.carrierName = d.getString("carrierName");
        Double price = null;
        try { price = d.getDouble("price"); } catch (Exception ignored) {}
        e.price = price != null ? price : 0d;

        e.orderId = d.getString("orderId");
        e.description = d.getString("description");

        String status = d.getString("status");
        boolean delivered = status != null && "DELIVERED".equalsIgnoreCase(status);
        if (delivered) {
            status = "RECEIVED";
            handleDeliveredPurchase(d);
        }
        e.status = status != null ? status : "PENDING";

        Timestamp ts = d.getTimestamp("createdAt");
        e.createdAt = ts != null ? ts.toDate().getTime() : System.currentTimeMillis();

        String thumb = d.getString("thumbnailUrl");
        e.thumbnailUrl = thumb != null ? thumb : "";
        e.pendingSync = false;
        return e;
    }

    private void upsertFromSnapshot(DocumentSnapshot snapshot) {
        PurchaseEntity e = mapDoc(snapshot);
        if (e == null) return;
        AppExecutors.io().execute(() -> {
            List<PurchaseEntity> one = new ArrayList<>();
            one.add(e);
            dao.upsertAll(one);
        });
    }

    private static String safeUid() {
        try {
            return FirebaseAuth.getInstance().getUid();
        } catch (Exception ex) {
            return null;
        }
    }

    private void handleDeliveredPurchase(DocumentSnapshot snapshot) {
        final String purchaseFsId = snapshot.getId();
        if (purchaseFsId == null || purchaseFsId.isEmpty()) return;
        AppExecutors.io().execute(() -> {
            try {
                PackageEntity pkg = packageDao.findByPurchaseFsId(purchaseFsId);
                if (pkg == null) {
                    pkg = buildPackageFromPurchase(snapshot);
                    packageDao.upsertAll(Collections.singletonList(pkg));
                }
                ensureRemotePackage(snapshot, pkg);
            } catch (Exception ignored) {
            }
        });
    }

    private PackageEntity buildPackageFromPurchase(DocumentSnapshot snapshot) {
        PackageEntity pkg = new PackageEntity();
        String purchaseFsId = snapshot.getId();
        pkg.purchaseFsId = purchaseFsId;
        pkg.fsId = purchaseFsId;
        pkg.id = stableLongFromString(purchaseFsId);
        pkg.label = derivePackageLabel(snapshot);
        pkg.description = derivePackageDescription(snapshot);
        Double price = null;
        try {
            price = snapshot.getDouble("price");
        } catch (Exception ignored) {}
        pkg.price = price != null ? price : 0d;
        pkg.status = "RECEIVED";
        pkg.lastUpdate = System.currentTimeMillis();
        String thumb = snapshot.getString("thumbnailUrl");
        pkg.thumbnailUrl = thumb != null ? thumb : "";
        pkg.shipmentId = null;
        return pkg;
    }

    private void ensureRemotePackage(DocumentSnapshot purchaseSnapshot, PackageEntity pkg) {
        String uid = safeUid();
        if (uid == null || pkg == null) return;
        String docId = (pkg.fsId != null && !pkg.fsId.isEmpty()) ? pkg.fsId : purchaseSnapshot.getId();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference ref = db.collection("users").document(uid)
                .collection("packages").document(docId);

        ref.get().addOnSuccessListener(existing -> {
            if (existing != null && existing.exists()) {
                if (pkg.fsId == null || pkg.fsId.isEmpty()) {
                    pkg.fsId = existing.getId();
                    AppExecutors.io().execute(() -> packageDao.upsertAll(Collections.singletonList(pkg)));
                }
            } else {
                java.util.HashMap<String, Object> data = new java.util.HashMap<>();
                data.put("id", docId);
                data.put("purchaseFsId", purchaseSnapshot.getId());
                data.put("label", pkg.label);
                data.put("description", pkg.description);
                data.put("price", pkg.price);
                data.put("status", pkg.status);
                data.put("thumbnailUrl", pkg.thumbnailUrl);
                data.put("shipmentId", pkg.shipmentId);
                data.put("lastUpdate", FieldValue.serverTimestamp());

                ref.set(data).addOnSuccessListener(v -> {
                    pkg.fsId = docId;
                    AppExecutors.io().execute(() -> packageDao.upsertAll(Collections.singletonList(pkg)));
                });
            }
        });
    }

    private DocumentReference preparePendingDocument(FirebaseFirestore db, String uid, PurchaseEntity entity) {
        String fsId = entity.fsId;
        DocumentReference doc;
        if (fsId != null && !fsId.trim().isEmpty()) {
            doc = db.collection("users").document(uid)
                    .collection("purchases").document(fsId);
        } else {
            doc = db.collection("users").document(uid)
                    .collection("purchases").document();
            entity.fsId = doc.getId();
            try {
                dao.upsertAll(Collections.singletonList(entity));
            } catch (Exception ignored) {
            }
        }
        return doc;
    }

    private java.util.Map<String, Object> buildPendingPayload(PurchaseEntity entity) {
        java.util.HashMap<String, Object> data = new java.util.HashMap<>();
        data.put("productName", entity.productName != null ? entity.productName : "");
        data.put("storeName",   entity.storeName   != null ? entity.storeName   : "");
        data.put("carrierName", entity.carrierName != null ? entity.carrierName : "");
        data.put("price",       entity.price       != null ? entity.price       : 0d);
        data.put("orderId",     entity.orderId     != null ? entity.orderId     : "");
        data.put("description", entity.description != null ? entity.description : "");
        data.put("status",      entity.status      != null ? entity.status      : "PENDING");
        data.put("thumbnailUrl",entity.thumbnailUrl!= null ? entity.thumbnailUrl: "");

        if (entity.createdAt > 0) {
            data.put("createdAt", new Timestamp(new java.util.Date(entity.createdAt)));
        } else {
            data.put("createdAt", FieldValue.serverTimestamp());
        }
        return data;
    }

    private static long stableLongFromString(String s) {
        if (s == null) return System.currentTimeMillis();
        long h = 1125899906842597L;
        for (int i = 0; i < s.length(); i++) {
            h = 31 * h + s.charAt(i);
        }
        return h & Long.MAX_VALUE;
    }

    private static String derivePackageLabel(DocumentSnapshot snapshot) {
        String store = snapshot.getString("storeName");
        if (store != null && !store.isEmpty()) return store;
        String order = snapshot.getString("orderId");
        if (order != null && !order.isEmpty()) return "Compra " + order;
        return "Compra " + snapshot.getId();
    }

    private static String derivePackageDescription(DocumentSnapshot snapshot) {
        String description = snapshot.getString("description");
        if (description != null && !description.isEmpty()) return description;
        String order = snapshot.getString("orderId");
        if (order != null && !order.isEmpty()) return "Orden " + order;
        return "Generado desde compra";
    }
}
