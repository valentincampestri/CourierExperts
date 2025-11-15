package com.courierexperts.demo.ui.shipments;

import com.courierexperts.demo.data.local.entity.ShipmentEntity;

import java.util.List;

public abstract class ShipmentsUiState {
    private ShipmentsUiState() {}

    public static final class Loading extends ShipmentsUiState {}
    public static final class Empty extends ShipmentsUiState {}

    public static final class Success extends ShipmentsUiState {
        private final List<ShipmentEntity> shipments;

        public Success(List<ShipmentEntity> shipments) {
            this.shipments = shipments;
        }

        public List<ShipmentEntity> getShipments() {
            return shipments;
        }
    }

    public static final class Error extends ShipmentsUiState {
        private final String message;

        public Error(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}
