package com.courierexperts.demo.data.local.entity;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "purchases",
        indices = {
            @Index(value = "fsId", unique = true),
            @Index(value = "status")
        })
public class PurchaseEntity {
    @PrimaryKey(autoGenerate = true) public long id;
    public String fsId;
    public String storeName;
    public String orderId;
    public String description;
    public String status;
    public long createdAt;
    public String thumbnailUrl;
    public boolean pendingSync;

    public String productName;
    public String carrierName;
    public Double price;

}
