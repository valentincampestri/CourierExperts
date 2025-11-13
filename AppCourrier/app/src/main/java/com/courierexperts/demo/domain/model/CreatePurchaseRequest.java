package com.courierexperts.demo.domain.model;

public class CreatePurchaseRequest {
    public String storeName;
    public String orderId;
    public String thumbnailUrl;

    public CreatePurchaseRequest() {}
    public CreatePurchaseRequest(String storeName, String orderId, String thumbnailUrl) {
        this.storeName = storeName;
        this.orderId = orderId;
        this.thumbnailUrl = thumbnailUrl;
    }
}

