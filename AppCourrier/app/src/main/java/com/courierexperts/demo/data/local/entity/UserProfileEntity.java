package com.courierexperts.demo.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_profile")
public class UserProfileEntity {
    @PrimaryKey public long id; // usar 1L como fila única
    public String name;         // nombre del usuario
    public String email;        // email del usuario
    public String address;      // dirección del usuario
    public String phone;        // teléfono del usuario
    public Long depositId;      // id de depósito seleccionado (FK lógica)
    public Boolean notificationsEnabled; // preferencia notificaciones
    public String updatedAt;    // ISO8601 para sync futura
    public String lastSyncedAt; // ISO8601 última vez que se sincronizó con backend
    public String remoteVersion; // versión/etag del backend para merge optimista
    public Boolean dirty;       // true si hay cambios locales pendientes de sync
}
