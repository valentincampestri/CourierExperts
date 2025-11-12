package com.courierexperts.demo.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.courierexperts.demo.data.local.dao.UserProfileDao;
import com.courierexperts.demo.data.local.db.AppDatabase;
import com.courierexperts.demo.data.local.entity.UserProfileEntity;
import com.courierexperts.demo.util.AppExecutors;

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
        return dao.observeProfile();
    }

    public void updateAddress(String address) {
        AppExecutors.io().execute(() -> {
            UserProfileEntity e = dao.getProfileSync();
            if (e == null) { e = new UserProfileEntity(); e.id = 1L; }
            e.address = address;
            stamp(e);
            dao.upsert(e);
        });
    }

    public void updatePhone(String phone) {
        AppExecutors.io().execute(() -> {
            UserProfileEntity e = dao.getProfileSync();
            if (e == null) { e = new UserProfileEntity(); e.id = 1L; }
            e.phone = phone;
            stamp(e);
            dao.upsert(e);
        });
    }

    public void updateName(String name) {
        AppExecutors.io().execute(() -> {
            UserProfileEntity e = dao.getProfileSync();
            if (e == null) { e = new UserProfileEntity(); e.id = 1L; }
            e.name = name;
            stamp(e);
            dao.upsert(e);
        });
    }

    public void updateEmail(String email) {
        AppExecutors.io().execute(() -> {
            UserProfileEntity e = dao.getProfileSync();
            if (e == null) { e = new UserProfileEntity(); e.id = 1L; }
            e.email = email;
            stamp(e);
            dao.upsert(e);
        });
    }

    public void updateDepositId(Long depositId) {
        AppExecutors.io().execute(() -> {
            UserProfileEntity e = dao.getProfileSync();
            if (e == null) { e = new UserProfileEntity(); e.id = 1L; }
            e.depositId = depositId;
            stamp(e);
            dao.upsert(e);
        });
    }

    public void updateNotifications(boolean enabled) {
        AppExecutors.io().execute(() -> {
            UserProfileEntity e = dao.getProfileSync();
            if (e == null) { e = new UserProfileEntity(); e.id = 1L; }
            e.notificationsEnabled = enabled;
            stamp(e);
            dao.upsert(e);
        });
    }

    private void ensureSeed() {
        AppExecutors.io().execute(() -> {
            UserProfileEntity e = dao.getProfileSync();
            if (e == null) {
                e = new UserProfileEntity();
                e.id = 1L;
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
}
