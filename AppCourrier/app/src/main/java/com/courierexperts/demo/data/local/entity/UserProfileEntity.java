package com.courierexperts.demo.data.local.entity;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_profile",
        indices = {
            @Index(value = "uid", unique = true)
        })
public class UserProfileEntity {
    @PrimaryKey(autoGenerate = true) public long id;
    public String uid;
    public String name;         // nombre del usuario
    public String lastName;     // apellido del usuario
    public String email;        // email del usuario
    public String address;      // dirección del usuario
    public String province;     // provincia del usuario
    public String country;      // país del usuario
    public String phone;        // teléfono del usuario
    public String dni;          // dni del usuario
    public String cuil;         // cuil del usuario
    public Long depositId;      // id de depósito seleccionado (FK lógica)
    public Boolean notificationsEnabled; // preferencia notificaciones
    public String updatedAt;    // ISO8601 para sync futura
    public String lastSyncedAt; // ISO8601 última vez que se sincronizó con backend
    public String remoteVersion; // versión/etag del backend para merge optimista
    public Boolean dirty;       // true si hay cambios locales pendientes de sync
}
