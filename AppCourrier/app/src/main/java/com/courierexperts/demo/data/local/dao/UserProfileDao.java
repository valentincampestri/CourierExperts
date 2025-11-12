package com.courierexperts.demo.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.courierexperts.demo.data.local.entity.UserProfileEntity;

@Dao
public interface UserProfileDao {
    @Query("SELECT * FROM user_profile WHERE id = 1")
    LiveData<UserProfileEntity> observeProfile();

    @Query("SELECT * FROM user_profile WHERE id = 1")
    UserProfileEntity getProfileSync();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsert(UserProfileEntity e);
}

