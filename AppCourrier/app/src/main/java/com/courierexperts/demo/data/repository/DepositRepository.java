package com.courierexperts.demo.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.courierexperts.demo.data.local.dao.DepositDao;
import com.courierexperts.demo.data.local.db.AppDatabase;
import com.courierexperts.demo.data.local.entity.DepositEntity;
import com.courierexperts.demo.util.AppExecutors;

import java.util.ArrayList;
import java.util.List;

public class DepositRepository {
    private final DepositDao dao;
    private final Context app;

    public DepositRepository(Context ctx) {
        this.app = ctx.getApplicationContext();
        this.dao = AppDatabase.get(this.app).depositDao();
        seedIfEmpty();
    }

    public LiveData<List<DepositEntity>> observeDeposits() {
        return dao.observeAll();
    }

    private void seedIfEmpty() {
        AppExecutors.io().execute(() -> {
            try {
                if (dao.count() == 0) {
                    List<DepositEntity> list = new ArrayList<>();
                    list.add(make("Miami"));
                    list.add(make("New York"));
                    list.add(make("Los Angeles"));
                    dao.insertAll(list);
                }
            } catch (Exception ignored) { }
        });
    }

    private static DepositEntity make(String name) {
        DepositEntity e = new DepositEntity();
        e.name = name;
        return e;
    }
}

