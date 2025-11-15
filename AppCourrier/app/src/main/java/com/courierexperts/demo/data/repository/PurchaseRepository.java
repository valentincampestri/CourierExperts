package com.courierexperts.demo.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.courierexperts.demo.data.local.dao.PurchaseDao;
import com.courierexperts.demo.data.local.db.AppDatabase;
import com.courierexperts.demo.data.local.entity.PurchaseEntity;
import com.courierexperts.demo.util.AppExecutors;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.Query;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class PurchaseRepository {

    private final PurchaseDao dao;
    private final Context app;
    private ListenerRegistration purchasesListener;

    public PurchaseRepository(Context ctx) {
        this.app = ctx.getApplicationContext();
        this.dao = AppDatabase.get(this.app).purchaseDao();
    }

    /** Observa la lista desde Room y, en paralelo, hace refresh desde red (mock por ahora). */
    public LiveData<List<PurchaseEntity>> observePurchases() {
        ensureListener();
        return dao.observeAll();
    }

    public androidx.lifecycle.LiveData<PurchaseEntity> observePurchaseById(long id) {
        ensureListener();
        return dao.observeById(id);
    }

    /** Descarga (mock/real), mapea y actualiza Room. */
    public void refreshFromNetwork() {
        ensureListener();
    }

    // ===================== NUEVA VERSIÓN =====================

    /**
     * Versión nueva que acepta todos los campos de la compra.
     *
     * @param storeName   nombre de la tienda (ebay, amazon, etc.)
     * @param orderId     id / tracking de la orden
     * @param name        nombre del producto (nuevo campo)
     * @param description descripción libre
     * @param carrier     empresa de envío
     * @param price       precio (Double, puede ser null)
     * @param thumbnailUrl url de imagen/logo del producto
     * @param createdAtIso ISO 8601 opcional (por ahora no se usa porque Firestore pone serverTimestamp)
     */
    public void createLocalAndSync(
            String storeName,
            String orderId,
            String name,
            String description,
            String carrier,
            Double price,
            String thumbnailUrl,
            String createdAtIso
    ) {
        final String uid = safeUid();
        final String safeThumb = thumbnailUrl != null ? thumbnailUrl : "";

        if (uid == null) {
            // sin usuario logueado → sólo guardamos local en Room con pendingSync
            AppExecutors.io().execute(() -> {
                PurchaseEntity e = new PurchaseEntity();
                e.id = 0;
                e.fsId = null;
                e.storeName = storeName;
                e.orderId = orderId;
                e.status = "PENDING";
                e.createdAt = System.currentTimeMillis();
                e.thumbnailUrl = safeThumb;
                // NUEVOS CAMPOS
                e.name = name;
                e.description = description;
                e.carrier = carrier;
                e.price = price;
                e.pendingSync = true;
                dao.insert(e);
            });
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        com.google.firebase.firestore.DocumentReference doc = db
                .collection("users").document(uid)
                .collection("purchases").document();

        java.util.HashMap<String, Object> data = new java.util.HashMap<>();
        data.put("id", doc.getId());
        data.put("storeName", storeName);
        data.put("orderId", orderId);
        data.put("status", "PENDING");
        data.put("createdAt", FieldValue.serverTimestamp());
        data.put("thumbnailUrl", safeThumb);

        // NUEVOS CAMPOS A FIRESTORE
        if (name != null && !name.isEmpty()) {
            data.put("name", name);
        }
        if (description != null && !description.isEmpty()) {
            data.put("description", description);
        }
        if (carrier != null && !carrier.isEmpty()) {
            data.put("carrier", carrier);
        }
        if (price != null) {
            data.put("price", price);
        }

        doc.set(data)
                .addOnSuccessListener(v ->
                        doc.get().addOnSuccessListener(this::upsertFromSnapshot))
                .addOnFailureListener(err -> AppExecutors.io().execute(() -> {
                    // Offline / error: guardamos local con pendingSync
                    PurchaseEntity e = new PurchaseEntity();
                    e.id = 0;
                    e.fsId = doc.getId();
                    e.storeName = storeName;
                    e.orderId = orderId;
                    e.status = "PENDING";
                    e.createdAt = System.currentTimeMillis();
                    e.thumbnailUrl = safeThumb;
                    // NUEVOS CAMPOS
                    e.name = name;
                    e.description = description;
                    e.carrier = carrier;
                    e.price = price;
                    e.pendingSync = true;
                    dao.insert(e);
                }));
    }

    /**
     * Versión vieja para mantener compatibilidad.
     * Sólo storeName + orderId + createdAtIso → delega en la nueva pasando nulls.
     */
    public void createLocalAndSync(String storeName, String orderId, String createdAtIso) {
        createLocalAndSync(
                storeName,
                orderId,
                null,   // name
                null,   // description
                null,   // carrier
                null,   // price
                "",     // thumbnailUrl
                createdAtIso
        );
    }

    // ========================================================

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
        try {
            existingId = dao.findLocalIdByFsId(e.fsId);
        } catch (Exception ignored) {}

        e.id = existingId != null ? existingId : (long) (e.fsId.hashCode() & 0x7fffffff);
        e.storeName = d.getString("storeName");
        e.orderId = d.getString("orderId");
        String status = d.getString("status");
        e.status = status != null ? status : "PENDING";
        com.google.firebase.Timestamp ts = d.getTimestamp("createdAt");
        e.createdAt = ts != null ? ts.toDate().getTime() : System.currentTimeMillis();
        String thumb = d.getString("thumbnailUrl");
        e.thumbnailUrl = thumb != null ? thumb : "";
        e.pendingSync = false;

        // NUEVOS CAMPOS DESDE FIRESTORE
        e.name = d.getString("name");
        e.description = d.getString("description");
        e.carrier = d.getString("carrier");

        Double price = null;
        try {
            price = d.getDouble("price");
        } catch (Exception ignored) {}
        e.price = price;

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

}
