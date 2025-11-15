package com.courierexperts.demo.ui.purchases;

import com.courierexperts.demo.data.local.entity.PurchaseEntity;

public abstract class PurchaseDetailUiState {
    private PurchaseDetailUiState() {}

    public static final class Loading extends PurchaseDetailUiState {}

    public static final class NotFound extends PurchaseDetailUiState {}

    public static final class Success extends PurchaseDetailUiState {
        private final PurchaseEntity purchase;

        public Success(PurchaseEntity purchase) {
            this.purchase = purchase;
        }

        public PurchaseEntity getPurchase() {
            return purchase;
        }
    }

    public static final class Error extends PurchaseDetailUiState {
        private final String message;

        public Error(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}
