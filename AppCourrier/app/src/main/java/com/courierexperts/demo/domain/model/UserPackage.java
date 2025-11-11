package com.courierexperts.demo.domain.model;

public class UserPackage {
    public long id;
    public String label;
    public String description;
    public String status;
    public String lastUpdate;
    public String thumbnailUrl;

    public UserPackage() {}
    public UserPackage(long id, String label, String description, String status, String lastUpdate, String thumbnailUrl) {
        this.id = id;
        this.label = label;
        this.description = description;
        this.status = status;
        this.lastUpdate = lastUpdate;
        this.thumbnailUrl = thumbnailUrl;
    }
}
