package com.courierexperts.demo.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.courierexperts.demo.data.local.dao.PackageDao;
import com.courierexperts.demo.data.local.db.AppDatabase;
import com.courierexperts.demo.data.local.entity.PackageEntity;
import com.courierexperts.demo.util.AppExecutors;
import com.courierexperts.demo.util.HashUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class PackageRepository {

    private final PackageDao dao;
    private final Context app;
    private ListenerRegistration packagesListener;
    private final MutableLiveData<String> remoteErrors = new MutableLiveData<>();

    public PackageRepository(Context ctx) {
        this.app = ctx.getApplicationContext();
        this.dao = AppDatabase.get(this.app).packageDao();
    }

    public LiveData<List<PackageEntity>> observePackages() {
        ensureListener();
        return dao.observeAll();
    }

    public LiveData<List<PackageEntity>> observeAllOrdered() {
        ensureListener();
        return dao.observeAllOrdered();
    }

    public LiveData<String> getErrors() {
        return remoteErrors;
    }

    public void refreshFromNetwork() {
        ensureListener();
    }

    private void ensureListener() {
        if (packagesListener != null) return;
        String uid = safeUid();
        if (uid == null) return;
        Query q = FirebaseFirestore.getInstance()
                .collection("users").document(uid)
                .collection("packages")
                .orderBy("lastUpdate", Query.Direction.DESCENDING);

        packagesListener = q.addSnapshotListener((snapshots, e) -> {
            if (e != null) {
                remoteErrors.postValue(e.getMessage());
                return;
            }
            if (snapshots == null) {
                remoteErrors.postValue("Respuesta vacia de Firestore.");
                return;
            }
            List<PackageEntity> list = new ArrayList<>();
            for (DocumentSnapshot d : snapshots.getDocuments()) {
                PackageEntity pe = mapDoc(d);
                if (pe != null) list.add(pe);
            }
            AppExecutors.io().execute(() -> {
                // opcional: dao.clear(); // preferimos Upsert para mantener selección
                dao.upsertAll(list);
            });
        });
    }

    private PackageEntity mapDoc(DocumentSnapshot d) {
        if (d == null || !d.exists()) return null;
        PackageEntity e = new PackageEntity();
        e.fsId = d.getId();
        e.purchaseFsId = d.getString("purchaseFsId");
        Long existingId = null;
        try { existingId = dao.findLocalIdByFsId(e.fsId); } catch (Exception ignored) {}
        if (existingId == null && e.purchaseFsId != null && !e.purchaseFsId.isEmpty()) {
            try { existingId = dao.findLocalIdByPurchaseFsId(e.purchaseFsId); } catch (Exception ignored) {}
        }
        e.id = existingId != null ? existingId : HashUtils.stableLongFromString(e.fsId);
        e.label = d.getString("label");
        e.description = d.getString("description");
        Double price = null;
        try { price = d.getDouble("price"); } catch (Exception ignored) {}
        e.price = price != null ? price : 0d;
        String status = d.getString("status");
        e.status = status != null ? status : "PENDING";
        com.google.firebase.Timestamp ts = d.getTimestamp("lastUpdate");
        e.lastUpdate = ts != null ? ts.toDate().getTime() : System.currentTimeMillis();
        String thumb = d.getString("thumbnailUrl");
        e.thumbnailUrl = thumb != null ? thumb : "";
        String sh = d.getString("shipmentId");
        e.shipmentId = sh != null ? sh : null;
        return e;
    }

    private static String safeUid() {
        try { return FirebaseAuth.getInstance().getUid(); } catch (Exception ex) { return null; }
    }
}
