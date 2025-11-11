package com.courierexperts.demo.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "packages")
public class PackageEntity {
    @PrimaryKey public long id;
    public String label;        // "Paquete 1"
    public String description;  // "Descripción del paquete"
    public String status;       // "en_deposito", "listo", "entregado", etc.
    public String lastUpdate;   // ISO 8601
    public String thumbnailUrl; // imagen opcional
}
