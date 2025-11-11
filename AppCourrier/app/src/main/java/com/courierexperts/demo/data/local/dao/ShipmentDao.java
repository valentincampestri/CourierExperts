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

    @Query("DELETE FROM shipments")
    void clear();
}
