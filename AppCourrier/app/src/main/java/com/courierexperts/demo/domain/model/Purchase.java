package com.courierexperts.demo.domain.model;

public class Purchase {
    public long id;
    public String storeName;
    public String orderId;
    public String status;       // pending / received / shipped / delivered
    public String createdAt;    // ISO 8601 (e.g. 2025-11-01T10:00:00Z)
    public String thumbnailUrl; // URL logo/imagen

    public Purchase(long id, String storeName, String orderId, String status, String createdAt, String thumbnailUrl) {
        this.id = id;
        this.storeName = storeName;
        this.orderId = orderId;
        this.status = status;
        this.createdAt = createdAt;
        this.thumbnailUrl = thumbnailUrl;
    }

    // Constructor vacío por si Gson/Retrofit lo necesita
    public Purchase() {}
}
