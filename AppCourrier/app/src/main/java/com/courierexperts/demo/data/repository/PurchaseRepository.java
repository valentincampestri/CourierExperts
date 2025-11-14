package com.courierexperts.demo.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.courierexperts.demo.data.local.dao.PackageDao;
import com.courierexperts.demo.data.local.dao.PurchaseDao;
import com.courierexperts.demo.data.local.db.AppDatabase;
import com.courierexperts.demo.data.local.entity.PackageEntity;
import com.courierexperts.demo.data.local.entity.PurchaseEntity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.FieldValue;
import com.courierexperts.demo.util.AppExecutors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.TimeZone;

public class PurchaseRepository {

    private final PurchaseDao dao;
    private final PackageDao packageDao;
    private final Context app;
    private ListenerRegistration purchasesListener;

    public PurchaseRepository(Context ctx) {
        this.app = ctx.getApplicationContext();
        AppDatabase db = AppDatabase.get(this.app);
        this.dao = db.purchaseDao();
        this.packageDao = db.packageDao();
    }

    /** Observa la lista desde Room y, en paralelo, hace refresh desde red (mock por ahora). */
    public LiveData<List<PurchaseEntity>> observePurchases() {
        ensureListener();
        return dao.observeAll();
    }

    public androidx.lifecycle.LiveData<com.courierexperts.demo.data.local.entity.PurchaseEntity> observePurchaseById(long id) {
        ensureListener();
        return dao.observeById(id);
    }

    /** Descarga (mock/real), mapea y actualiza Room. */
    public void refreshFromNetwork() {
        ensureListener();
    }

    /** Crea localmente y trata de sincronizar con backend (mock). */
    public void createLocalAndSync(String storeName, String orderId, String description, String createdAtIso) {
        // Crear documento en Firestore; si falla, guardar local como pending
        final String uid = safeUid();
        if (uid == null) {
            // sin usuario, guardar local
            AppExecutors.io().execute(() -> {
                PurchaseEntity e = new PurchaseEntity();
                e.id = 0;
                e.fsId = null;
                e.storeName = storeName;
                e.orderId = orderId;
                e.status = "PENDING";
                e.description = description;
                e.createdAt = System.currentTimeMillis();
                e.thumbnailUrl = "";
                e.pendingSync = true;
                dao.insert(e);
            });
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        com.google.firebase.firestore.DocumentReference doc = db.collection("users").document(uid)
                .collection("purchases").document();

        java.util.HashMap<String, Object> data = new java.util.HashMap<>();
        data.put("id", doc.getId());
        data.put("storeName", storeName);
        data.put("orderId", orderId);
        data.put("status", "PENDING");
        data.put("description", description);
        data.put("createdAt", FieldValue.serverTimestamp());
        data.put("thumbnailUrl", "");

        doc.set(data)
                .addOnSuccessListener(v -> doc.get().addOnSuccessListener(snapshot -> upsertFromSnapshot(snapshot)))
                .addOnFailureListener(err -> AppExecutors.io().execute(() -> {
                    // Offline: queda local con pendingSync
                    PurchaseEntity e = new PurchaseEntity();
                    e.id = 0;
                    e.fsId = doc.getId();
                    e.storeName = storeName;
                    e.orderId = orderId;
                    e.status = "PENDING";
                    e.description = description;
                    e.createdAt = System.currentTimeMillis();
                    e.thumbnailUrl = "";
                    e.pendingSync = true;
                    dao.insert(e);
                }));
    }

    public void syncPendingIfNetworkAvailable() {
        // Firestore listeners manejarán la sincronización; no-op aquí
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
            if (e != null || snapshots == null) return;
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
        try { existingId = dao.findLocalIdByFsId(e.fsId); } catch (Exception ignored) {}
        e.id = existingId != null ? existingId : stableLongFromString(e.fsId);
        e.storeName = d.getString("storeName");
        e.orderId = d.getString("orderId");
        e.description = d.getString("description");
        String status = d.getString("status");
        boolean delivered = status != null && "DELIVERED".equalsIgnoreCase(status);
        if (delivered) {
            status = "RECEIVED";
            handleDeliveredPurchase(d);
        }
        e.status = status != null ? status : "PENDING";
        com.google.firebase.Timestamp ts = d.getTimestamp("createdAt");
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
        try { return FirebaseAuth.getInstance().getUid(); } catch (Exception ex) { return null; }
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
        try { price = snapshot.getDouble("price"); } catch (Exception ignored) {}
        pkg.price = price != null ? price : 0d;
        pkg.status = "PENDING";
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
                data.put("status", "PENDING");
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


