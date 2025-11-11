package com.courierexperts.demo.domain.model;

public class Shipment {
    public long id;
    public String title;
    public String trackingNumber;
    public String status;
    public String lastUpdate;
    public String thumbnailUrl;

    public Shipment() {}
    public Shipment(long id, String title, String trackingNumber, String status, String lastUpdate, String thumbnailUrl) {
        this.id = id;
        this.title = title;
        this.trackingNumber = trackingNumber;
        this.status = status;
        this.lastUpdate = lastUpdate;
        this.thumbnailUrl = thumbnailUrl;
    }
}
