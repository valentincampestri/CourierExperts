package com.courierexperts.demo.ui.shipments;

import com.courierexperts.demo.data.local.entity.ShipmentEntity;

import java.util.List;

public abstract class ShipmentDetailUiState {
    private ShipmentDetailUiState() {}

    public static final class Loading extends ShipmentDetailUiState {}
    public static final class NotFound extends ShipmentDetailUiState {}

    public static final class Success extends ShipmentDetailUiState {
        private final ShipmentEntity shipment;
        private final List<com.courierexperts.demo.data.local.entity.PackageEntity> packages;

        public Success(ShipmentEntity shipment, List<com.courierexperts.demo.data.local.entity.PackageEntity> packages) {
            this.shipment = shipment;
            this.packages = packages;
        }

        public ShipmentEntity getShipment() {
            return shipment;
        }

        public List<com.courierexperts.demo.data.local.entity.PackageEntity> getPackages() {
            return packages;
        }
    }

    public static final class Error extends ShipmentDetailUiState {
        private final String message;

        public Error(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}
