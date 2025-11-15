package com.courierexperts.demo.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "purchases")
public class PurchaseEntity {
    @PrimaryKey(autoGenerate = true) public long id;
    public String fsId;         // Firestore document id (users/{uid}/purchases/{fsId})
    public String storeName;
    public String orderId;
    public String status;       // PENDING / RECEIVED / SHIPPED / DELIVERED / CANCELLED
    public long createdAt;      // epoch millis (Firestore Timestamp)
    public String thumbnailUrl; // URL del logo/imagen de la tienda
    public boolean pendingSync; // true si falta enviar al servidor

    public String description;   // NUEVO
    public String carrier;       // NUEVO
    public Double price;         // NUEVO

    public String name;          // NUEVO
}
