package com.courierexperts.demo.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "purchases")
public class PurchaseEntity {
    @PrimaryKey public long id;
    public String storeName;
    public String orderId;
    public String status;       // pending / received / shipped / delivered
    public String createdAt;    // ISO 8601 (ej: 2025-11-01T10:00:00Z)
    public String thumbnailUrl; // URL del logo/imagen de la tienda
}
