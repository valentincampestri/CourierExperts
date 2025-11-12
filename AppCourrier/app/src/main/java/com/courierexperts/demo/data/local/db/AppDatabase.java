package com.courierexperts.demo.data.local.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.courierexperts.demo.data.local.dao.PackageDao;
import com.courierexperts.demo.data.local.dao.DepositDao;
import com.courierexperts.demo.data.local.dao.UserProfileDao;
import com.courierexperts.demo.data.local.dao.PurchaseDao;
import com.courierexperts.demo.data.local.dao.ShipmentDao;
import com.courierexperts.demo.data.local.entity.PackageEntity;
import com.courierexperts.demo.data.local.entity.DepositEntity;
import com.courierexperts.demo.data.local.entity.UserProfileEntity;
import com.courierexperts.demo.data.local.entity.PurchaseEntity;
import com.courierexperts.demo.data.local.entity.ShipmentEntity;

@Database(
        entities = { PurchaseEntity.class, PackageEntity.class, ShipmentEntity.class, DepositEntity.class, UserProfileEntity.class },
        version = 8,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    public abstract PurchaseDao purchaseDao();
    public abstract PackageDao packageDao();
    public abstract ShipmentDao shipmentDao();
    public abstract DepositDao depositDao();
    public abstract UserProfileDao userProfileDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase get(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "courierexperts.db"
                            )
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
