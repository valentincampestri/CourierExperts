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

        try {
            HashMap<String,Object> map = new HashMap<>();
            map.put("name", e.name);
            map.put("lastName", e.lastName);
            map.put("dni", e.dni);
            map.put("cuil", e.cuil);
            map.put("address", e.address);
            map.put("province", e.province);
            map.put("country", e.country);
            map.put("email", e.email);
            map.put("phone", e.phone);
            map.put("depositId", e.depositId);
            map.put("notificationsEnabled", e.notificationsEnabled != null && e.notificationsEnabled);
            map.put("updatedAt", nowIso());
            Tasks.await(FirebaseFirestore.getInstance().collection("users").document(uid).set(map));
            // marcar clean
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

    public static void enqueue(Context ctx) {
        Constraints c = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();
        OneTimeWorkRequest req = new OneTimeWorkRequest.Builder(ProfileSyncWorker.class).setConstraints(c).build();
        WorkManager.getInstance(ctx.getApplicationContext()).enqueueUniqueWork(UNIQUE_NAME, androidx.work.ExistingWorkPolicy.APPEND_OR_REPLACE, req);
    }
}
