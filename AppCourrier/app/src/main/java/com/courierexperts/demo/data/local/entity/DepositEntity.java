package com.courierexperts.demo.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "deposits")
public class DepositEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public String name;
}

