package com.courierexperts.demo.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.courierexperts.demo.data.local.dao.UserProfileDao;
import com.courierexperts.demo.data.local.db.AppDatabase;
import com.courierexperts.demo.data.local.entity.UserProfileEntity;
import com.courierexperts.demo.util.AppExecutors;

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
            dao.upsert(e);
        });
    }

    public void updatePhone(String phone) {
        AppExecutors.io().execute(() -> {
            UserProfileEntity e = dao.getProfileSync();
            if (e == null) { e = new UserProfileEntity(); e.id = 1L; }
            e.phone = phone;
            dao.upsert(e);
        });
    }

    private void ensureSeed() {
        AppExecutors.io().execute(() -> {
            UserProfileEntity e = dao.getProfileSync();
            if (e == null) {
                e = new UserProfileEntity();
                e.id = 1L;
                e.address = ""; // vacío por defecto
                e.phone = "";
                dao.upsert(e);
            }
        });
    }
}
