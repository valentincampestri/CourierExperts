package com.courierexperts.demo.data.local.entity;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_profile",
        indices = {
            @Index(value = "uid", unique = true)
        })
public class UserProfileEntity {
    @PrimaryKey(autoGenerate = true) public long id;
    public String uid;
    public String name;
    public String lastName;
    public String email;
    public String address;
    public String province;
    public String country;
    public String phone;
    public String dni;
    public String cuil;
    public Long depositId;
    public Boolean notificationsEnabled;
    public String updatedAt;
    public String lastSyncedAt;
    public String remoteVersion;
    public Boolean dirty;
}
