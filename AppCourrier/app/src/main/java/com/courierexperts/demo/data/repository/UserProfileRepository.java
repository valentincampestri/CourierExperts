package com.courierexperts.demo.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.courierexperts.demo.data.local.dao.UserProfileDao;
import com.courierexperts.demo.data.local.db.AppDatabase;
import com.courierexperts.demo.data.local.entity.UserProfileEntity;
import com.courierexperts.demo.util.AppExecutors;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class UserProfileRepository {
    private final UserProfileDao dao;
    private final Context app;

    public UserProfileRepository(Context ctx) {
        this.app = ctx.getApplicationContext();
        this.dao = AppDatabase.get(this.app).userProfileDao();
        ensureSeed();
    }

    public LiveData<UserProfileEntity> observeProfile() {
        String uid = currentUid();
        return dao.observeProfile(uid);
    }

    public void updateAddress(String address) {
        AppExecutors.io().execute(() -> {
            String uid = currentUid();
            UserProfileEntity e = dao.getProfileSync(uid);
            if (e == null) { e = new UserProfileEntity(); e.uid = uid; }
            e.address = address;
            stamp(e);
            dao.upsert(e);
            pushToFirestoreAsync(e);
        });
    }

    public void updatePhone(String phone) {
        AppExecutors.io().execute(() -> {
            String uid = currentUid();
            UserProfileEntity e = dao.getProfileSync(uid);
            if (e == null) { e = new UserProfileEntity(); e.uid = uid; }
            e.phone = phone;
            stamp(e);
            dao.upsert(e);
            pushToFirestoreAsync(e);
        });
    }

    public void updateName(String name) {
        AppExecutors.io().execute(() -> {
            String uid = currentUid();
            UserProfileEntity e = dao.getProfileSync(uid);
            if (e == null) { e = new UserProfileEntity(); e.uid = uid; }
            e.name = name;
            stamp(e);
            dao.upsert(e);
            pushToFirestoreAsync(e);
        });
    }

    public void updateEmail(String email) {
        AppExecutors.io().execute(() -> {
            String uid = currentUid();
            UserProfileEntity e = dao.getProfileSync(uid);
            if (e == null) { e = new UserProfileEntity(); e.uid = uid; }
            e.email = email;
            stamp(e);
            dao.upsert(e);
            pushToFirestoreAsync(e);
        });
    }

    public void updateDepositId(Long depositId) {
        AppExecutors.io().execute(() -> {
            String uid = currentUid();
            UserProfileEntity e = dao.getProfileSync(uid);
            if (e == null) { e = new UserProfileEntity(); e.uid = uid; }
            e.depositId = depositId;
            stamp(e);
            dao.upsert(e);
            pushToFirestoreAsync(e);
        });
    }

    public void updateNotifications(boolean enabled) {
        AppExecutors.io().execute(() -> {
            String uid = currentUid();
            UserProfileEntity e = dao.getProfileSync(uid);
            if (e == null) { e = new UserProfileEntity(); e.uid = uid; }
            e.notificationsEnabled = enabled;
            stamp(e);
            dao.upsert(e);
            pushToFirestoreAsync(e);
        });
    }

    private void ensureSeed() {
        AppExecutors.io().execute(() -> {
            String uid = currentUid();
            if (uid == null) return; // sin usuario, no se siembra
            UserProfileEntity e = dao.getProfileSync(uid);
            if (e == null) {
                e = new UserProfileEntity();
                e.uid = uid;
                e.name = "";
                e.email = "";
                e.address = "";
                e.phone = "";
                e.depositId = null;
                e.notificationsEnabled = Boolean.FALSE;
                e.updatedAt = null;
                e.lastSyncedAt = null;
                e.remoteVersion = null;
                e.dirty = Boolean.FALSE;
                dao.upsert(e);
            }
        });
    }

    private static void stamp(UserProfileEntity e) {
        e.updatedAt = nowIso();
        e.dirty = Boolean.TRUE;
    }

    private static String nowIso() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(new Date());
    }

    public void saveAllSignupProfile(String nombre, String apellido, String dni, String cuil,
                                     String direccion, String provincia, String pais, String email, String phone) {
        AppExecutors.io().execute(() -> {
            String uid = currentUid();
            if (uid == null) return;
            UserProfileEntity e = dao.getProfileSync(uid);
            if (e == null) { e = new UserProfileEntity(); e.uid = uid; }
            e.name = nombre;
            e.lastName = apellido;
            e.dni = dni;
            e.cuil = cuil;
            e.address = direccion;
            e.province = provincia;
            e.country = pais;
            e.email = email;
            e.phone = phone;
            stamp(e);
            dao.upsert(e);
            pushToFirestoreAsync(e);
        });
    }

    private String currentUid() {
        try { return FirebaseAuth.getInstance().getUid(); } catch (Exception e) { return null; }
    }

    public void syncFromFirestore() {
        final String uid = currentUid();
        if (uid == null) return;
        FirebaseFirestore.getInstance().collection("users").document(uid).get()
                .addOnSuccessListener(doc -> AppExecutors.io().execute(() -> applyRemote(uid, doc)))
                .addOnFailureListener(err -> { /* opcional: log */ });
    }

    private void applyRemote(String uid, DocumentSnapshot doc) {
        if (doc == null || !doc.exists()) return;
        UserProfileEntity local = dao.getProfileSync(uid);
        UserProfileEntity e = (local != null) ? local : new UserProfileEntity();
        e.uid = uid;
        e.name = doc.getString("name");
        e.lastName = doc.getString("lastName");
        e.email = doc.getString("email");
        e.address = doc.getString("address");
        e.province = doc.getString("province");
        e.country = doc.getString("country");
        e.phone = doc.getString("phone");
        e.dni = doc.getString("dni");
        e.cuil = doc.getString("cuil");
        Object dep = doc.get("depositId");
        e.depositId = (dep instanceof Number) ? ((Number) dep).longValue() : null;
        Boolean notif = doc.getBoolean("notificationsEnabled");
        e.notificationsEnabled = notif != null ? notif : Boolean.FALSE;
        e.updatedAt = doc.getString("updatedAt");
        e.dirty = Boolean.FALSE;
        dao.upsert(e);
    }

    private void pushToFirestoreAsync(UserProfileEntity e) {
        final String uid = currentUid();
        if (uid == null) return;
        FirebaseFirestore.getInstance().collection("users").document(uid)
                .set(new java.util.HashMap<String, Object>() {{
                    put("name", e.name);
                    put("lastName", e.lastName);
                    put("email", e.email);
                    put("address", e.address);
                    put("province", e.province);
                    put("country", e.country);
                    put("phone", e.phone);
                    put("dni", e.dni);
                    put("cuil", e.cuil);
                    put("depositId", e.depositId);
                    put("notificationsEnabled", e.notificationsEnabled != null && e.notificationsEnabled);
                    put("updatedAt", e.updatedAt);
                }})
                .addOnSuccessListener(v -> AppExecutors.io().execute(() -> { e.dirty = Boolean.FALSE; dao.upsert(e);} ))
                .addOnFailureListener(err -> { try { com.courierexperts.demo.work.ProfileSyncWorker.enqueue(app); } catch (Exception ignored) {} });
    }

    public void enqueueSyncNow() {
        try { com.courierexperts.demo.work.ProfileSyncWorker.enqueue(app); } catch (Exception ignored) {}
    }
}
