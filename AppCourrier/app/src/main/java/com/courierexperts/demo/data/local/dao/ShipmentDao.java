package com.courierexperts.demo.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.courierexperts.demo.data.local.entity.ShipmentEntity;

import java.util.List;

@Dao
public interface ShipmentDao {

    @Query("SELECT * FROM shipments ORDER BY lastUpdate DESC")
    LiveData<List<ShipmentEntity>> observeAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsertAll(List<ShipmentEntity> items);

    @Query("SELECT * FROM shipments WHERE fsId = :fsId LIMIT 1")
    LiveData<ShipmentEntity> observeByFsId(String fsId);

    @Query("SELECT * FROM shipments WHERE id = :id LIMIT 1")
    LiveData<ShipmentEntity> observeById(long id);

    @Query("SELECT fsId FROM shipments WHERE id = :id LIMIT 1")
    String getFsIdByLocalId(long id);

    @Query("SELECT id FROM shipments WHERE fsId = :fsId LIMIT 1")
    Long findLocalIdByFsId(String fsId);

    @Query("DELETE FROM shipments")
    void clear();
}
