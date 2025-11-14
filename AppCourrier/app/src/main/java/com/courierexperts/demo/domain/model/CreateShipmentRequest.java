package com.courierexperts.demo.domain.model;

import java.util.List;

public class CreateShipmentRequest {
    public List<Long> packageIds;

    public CreateShipmentRequest() {}
    public CreateShipmentRequest(List<Long> packageIds) { this.packageIds = packageIds; }
}

