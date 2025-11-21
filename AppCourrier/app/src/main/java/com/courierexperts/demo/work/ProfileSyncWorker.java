package com.courierexperts.demo.work;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.courierexperts.demo.data.local.db.AppDatabase;
import com.courierexperts.demo.data.local.entity.UserProfileEntity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.android.gms.tasks.Tasks;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

public class ProfileSyncWorker extends Worker {
    public static final String UNIQUE_NAME = "profile_sync_work";

    public ProfileSyncWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull @Override
    public Result doWork() {
        String uid;
        try { uid = FirebaseAuth.getInstance().getUid(); } catch (Exception e) { uid = null; }
        if (uid == null) return Result.success();
        UserProfileEntity e = AppDatabase.get(getApplicationContext()).userProfileDao().getProfileSync(uid);
        if (e == null) return Result.success();
        if (e.dirty != null && !e.dirty) return Result.success();

        try {
            HashMap<String,Object> map = new HashMap<>();
            putIfNotEmpty(map, "name", e.name);
            putIfNotEmpty(map, "lastName", e.lastName);
            putIfNotEmpty(map, "dni", e.dni);
            putIfNotEmpty(map, "cuil", e.cuil);
            putIfNotEmpty(map, "address", e.address);
            putIfNotEmpty(map, "province", e.province);
            putIfNotEmpty(map, "country", e.country);
            putIfNotEmpty(map, "email", e.email);
            putIfNotEmpty(map, "phone", e.phone);
            if (e.depositId != null) {
                map.put("depositId", e.depositId);
            }
            if (e.notificationsEnabled != null) {
                map.put("notificationsEnabled", e.notificationsEnabled);
            }
            if (map.isEmpty()) return Result.success();
            map.put("updatedAt", nowIso());
            Tasks.await(FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(uid)
                    .set(map, SetOptions.merge()));
            e.dirty = Boolean.FALSE;
            AppDatabase.get(getApplicationContext()).userProfileDao().upsert(e);
            return Result.success();
        } catch (Exception ex) {
            return Result.retry();
        }
    }

    private static String nowIso() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(new Date());
    }

    private static void putIfNotEmpty(HashMap<String, Object> map, String key, String value) {
        if (value != null && !value.trim().isEmpty()) {
            map.put(key, value);
        }
    }

    public static void enqueue(Context ctx) {
        Constraints c = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();
        OneTimeWorkRequest req = new OneTimeWorkRequest.Builder(ProfileSyncWorker.class).setConstraints(c).build();
        WorkManager.getInstance(ctx.getApplicationContext()).enqueueUniqueWork(UNIQUE_NAME, androidx.work.ExistingWorkPolicy.APPEND_OR_REPLACE, req);
    }
}
