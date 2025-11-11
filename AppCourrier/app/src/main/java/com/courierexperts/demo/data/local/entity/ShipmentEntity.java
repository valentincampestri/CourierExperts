package com.courierexperts.demo.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "shipments")
public class ShipmentEntity {
    @PrimaryKey public long id;
    public String title;         // "Envío 5"
    public String trackingNumber;// "12345"
    public String status;        // "en_transito", "entregado", ...
    public String lastUpdate;    // ISO 8601
    public String thumbnailUrl;  // opcional
}
