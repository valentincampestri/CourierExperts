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

    @Query("SELECT * FROM packages ORDER BY CASE WHEN status = 'IN_WAREHOUSE' THEN 0 ELSE 1 END, lastUpdate DESC, id DESC")
    LiveData<List<PackageEntity>> observeAllOrdered();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsertAll(List<PackageEntity> items);

    @Query("SELECT id FROM packages WHERE fsId = :fsId LIMIT 1")
    Long findLocalIdByFsId(String fsId);

    @Query("SELECT id FROM packages WHERE purchaseFsId = :purchaseFsId LIMIT 1")
    Long findLocalIdByPurchaseFsId(String purchaseFsId);

    @Query("SELECT * FROM packages WHERE purchaseFsId = :purchaseFsId LIMIT 1")
    PackageEntity findByPurchaseFsId(String purchaseFsId);

    @Query("SELECT fsId FROM packages WHERE id = :id LIMIT 1")
    String getFsIdByLocalId(long id);

    @Query("UPDATE packages SET shipmentId = :shipmentId, status = :status, lastUpdate = :lastUpdate WHERE id = :id")
    void updateShipmentAndStatus(long id, String shipmentId, String status, long lastUpdate);

    @Query("SELECT * FROM packages WHERE shipmentId = :shipmentId ORDER BY lastUpdate DESC")
    LiveData<List<PackageEntity>> observeByShipmentId(String shipmentId);

    @Query("SELECT * FROM packages WHERE id = :id LIMIT 1")
    LiveData<PackageEntity> observeById(long id);

    @Query("DELETE FROM packages")
    void clear();
}
