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
    public String fsId;           // Firestore document id
    public String purchaseFsId;   // Referencia a la compra origen
    public String label;          // "Paquete 1"
    public String description;    // "Descripción del paquete"
    public double price;          // Copia del precio declarado en la compra
    public String status;         // PENDING / IN_WAREHOUSE / IN_TRANSIT / DELIVERED / CANCELLED
    public long lastUpdate;       // epoch millis (Firestore Timestamp)
    public String thumbnailUrl;   // imagen opcional
    public String shipmentId;     // id del shipment asociado (mismo usuario)
}

