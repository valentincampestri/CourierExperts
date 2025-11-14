package com.courierexperts.demo.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.courierexperts.demo.data.local.entity.DepositEntity;

import java.util.List;

@Dao
public interface DepositDao {
    @Query("SELECT * FROM deposits ORDER BY name ASC")
    LiveData<List<DepositEntity>> observeAll();

    @Query("SELECT COUNT(*) FROM deposits")
    int count();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<DepositEntity> items);

    @Query("DELETE FROM deposits")
    void clear();
}

