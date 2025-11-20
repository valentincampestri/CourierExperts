package com.courierexperts.demo.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "shipments")
public class ShipmentEntity {
    @PrimaryKey public long id;
    public String fsId;           // Firestore document id
    public String title;           // "Envío 5"
    public String trackingNumber;  // "12345"
    public String status;          // CREATED / IN_TRANSIT / OUT_FOR_DELIVERY / DELIVERED / CANCELLED
    public long lastUpdate;        // epoch millis (Firestore Timestamp)
    public String thumbnailUrl;    // opcional
    public String packageIdsJson;  // JSON con array de IDs de paquetes
    public double cost;            // Costo estimado (20% de la suma de paquetes)
}
