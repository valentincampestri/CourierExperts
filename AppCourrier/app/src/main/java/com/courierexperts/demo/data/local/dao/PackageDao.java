package com.courierexperts.demo.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.courierexperts.demo.data.local.entity.PackageEntity;

import java.util.List;

@Dao
public interface PackageDao {

    @Query("SELECT * FROM packages ORDER BY lastUpdate DESC")
    LiveData<List<PackageEntity>> observeAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsertAll(List<PackageEntity> items);

    @Query("DELETE FROM packages")
    void clear();
}
