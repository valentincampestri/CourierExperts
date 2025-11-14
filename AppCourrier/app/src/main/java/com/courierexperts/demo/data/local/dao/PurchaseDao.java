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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(PurchaseEntity item);

    @Query("SELECT id FROM purchases WHERE fsId = :fsId LIMIT 1")
    Long findLocalIdByFsId(String fsId);

    @Query("SELECT * FROM purchases WHERE id = :id LIMIT 1")
    LiveData<PurchaseEntity> observeById(long id);

    @Query("SELECT * FROM purchases WHERE pendingSync = 1")
    List<PurchaseEntity> listPending();

    @Query("DELETE FROM purchases WHERE id = :id")
    void deleteById(long id);

    @Query("DELETE FROM purchases")
    void clear();
}
