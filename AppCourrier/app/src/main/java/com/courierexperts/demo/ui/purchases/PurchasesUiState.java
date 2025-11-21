package com.courierexperts.demo.ui.purchases;

import com.courierexperts.demo.data.local.entity.PurchaseEntity;

import java.util.List;





public abstract class PurchasesUiState {

    private PurchasesUiState() {
        
    }

    public static final class Loading extends PurchasesUiState {
        public Loading() {
            super();
        }
    }

    public static final class Empty extends PurchasesUiState {
        public Empty() {
            super();
        }
    }

    public static final class Success extends PurchasesUiState {
        private final List<PurchaseEntity> purchases;

        public Success(List<PurchaseEntity> purchases) {
            super();
            this.purchases = purchases;
        }

        public List<PurchaseEntity> getPurchases() {
            return purchases;
        }
    }

    public static final class Error extends PurchasesUiState {
        private final String message;

        public Error(String message) {
            super();
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}
