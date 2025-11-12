package com.courierexperts.demo.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_profile")
public class UserProfileEntity {
    @PrimaryKey public long id; // usar 1L como fila única
    public String address;      // dirección del usuario
    public String phone;        // teléfono del usuario
}
