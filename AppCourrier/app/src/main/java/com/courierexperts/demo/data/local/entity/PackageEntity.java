package com.courierexperts.demo.data.local.entity;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "packages",
        indices = {
            @Index(value = "fsId", unique = true),
            @Index(value = "purchaseFsId"),
            @Index(value = "shipmentId"),
            @Index(value = "status")
        })
public class PackageEntity {
    @PrimaryKey public long id;
    public String fsId;
    public String purchaseFsId;
    public String label;
    public String description;
    public double price;
    public String status;
    public long lastUpdate;
    public String thumbnailUrl;
    public String shipmentId;
}

