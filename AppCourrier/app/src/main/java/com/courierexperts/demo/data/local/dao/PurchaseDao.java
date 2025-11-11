package com.courierexperts.demo.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.courierexperts.demo.data.local.entity.PurchaseEntity;

import java.util.List;

@Dao
public interface PurchaseDao {

    @Query("SELECT * FROM purchases ORDER BY createdAt DESC")
    LiveData<List<PurchaseEntity>> observeAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsertAll(List<PurchaseEntity> items);

    @Query("DELETE FROM purchases")
    void clear();
}
