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
import com.google.firebase.firestore.SetOptions;

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
        e.name = firstNonEmpty(doc, "name", "nombre");
        e.lastName = firstNonEmpty(doc, "lastName", "apellido");
        e.email = firstNonEmpty(doc, "email", "correo");
        e.address = firstNonEmpty(doc, "address", "direccion");
        e.province = firstNonEmpty(doc, "province", "provincia");
        e.country = firstNonEmpty(doc, "country", "pais");
        e.phone = firstNonEmpty(doc, "phone", "telefono");
        e.dni = firstNonEmpty(doc, "dni");
        e.cuil = firstNonEmpty(doc, "cuil");
        Object dep = doc.get("depositId");
        if (dep == null) dep = doc.get("depositoId");
        e.depositId = coerceLong(dep);
        Boolean notif = doc.getBoolean("notificationsEnabled");
        e.notificationsEnabled = notif != null ? notif : Boolean.FALSE;
        e.updatedAt = doc.getString("updatedAt");
        e.dirty = Boolean.FALSE;
        dao.upsert(e);
    }

    private void pushToFirestoreAsync(UserProfileEntity e) {
        final String uid = currentUid();
        if (uid == null) return;
        java.util.HashMap<String, Object> data = new java.util.HashMap<>();
        // Sólo mandamos campos no nulos/ no vacíos para evitar sobreescrituras con valores vacíos
        if (notEmpty(e.name)) data.put("name", e.name);
        if (notEmpty(e.lastName)) data.put("lastName", e.lastName);
        if (notEmpty(e.email)) data.put("email", e.email);
        if (notEmpty(e.address)) data.put("address", e.address);
        if (notEmpty(e.province)) data.put("province", e.province);
        if (notEmpty(e.country)) data.put("country", e.country);
        if (notEmpty(e.phone)) data.put("phone", e.phone);
        if (notEmpty(e.dni)) data.put("dni", e.dni);
        if (notEmpty(e.cuil)) data.put("cuil", e.cuil);
        if (e.depositId != null) data.put("depositId", e.depositId);
        if (e.notificationsEnabled != null) data.put("notificationsEnabled", e.notificationsEnabled);
        if (notEmpty(e.updatedAt)) data.put("updatedAt", e.updatedAt);

        FirebaseFirestore.getInstance().collection("users").document(uid)
                .set(data, SetOptions.merge())
                .addOnSuccessListener(v -> AppExecutors.io().execute(() -> { e.dirty = Boolean.FALSE; dao.upsert(e);} ))
                .addOnFailureListener(err -> { try { com.courierexperts.demo.work.ProfileSyncWorker.enqueue(app); } catch (Exception ignored) {} });
    }

    private static String firstNonEmpty(DocumentSnapshot doc, String... keys) {
        if (doc == null || keys == null) return null;
        for (String key : keys) {
            if (key == null) continue;
            String value = doc.getString(key);
            if (value != null) {
                String trimmed = value.trim();
                if (!trimmed.isEmpty()) {
                    return trimmed;
                }
            }
        }
        return null;
    }

    private static Long coerceLong(Object value) {
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        if (value instanceof String) {
            String trimmed = ((String) value).trim();
            if (trimmed.isEmpty()) return null;
            try {
                return Long.parseLong(trimmed);
            } catch (NumberFormatException ignored) {}
        }
        return null;
    }

    private static boolean notEmpty(String s) { return s != null && !s.trim().isEmpty(); }

    public void enqueueSyncNow() {
        try { com.courierexperts.demo.work.ProfileSyncWorker.enqueue(app); } catch (Exception ignored) {}
    }
}
