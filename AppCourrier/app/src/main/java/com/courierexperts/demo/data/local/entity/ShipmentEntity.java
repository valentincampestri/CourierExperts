package com.courierexperts.demo.data.local.entity;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "shipments",
        indices = {
            @Index(value = "fsId", unique = true),
            @Index(value = "status")
        })
public class ShipmentEntity {
    @PrimaryKey public long id;
    public String fsId;
    public String title;
    public String trackingNumber;
    public String status;
    public long lastUpdate;
    public String thumbnailUrl;
    public String packageIdsJson;
    public double cost;
}
