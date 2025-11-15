package com.courierexperts.demo.ui.home;

import androidx.annotation.Nullable;

/**
 * Representa los estados de HomeActivity (saludo y tarjeta del A?ltimo envA-o).
 */
public abstract class HomeUiState {

    private HomeUiState() { }

    public static final class Loading extends HomeUiState { }

    public static final class Content extends HomeUiState {
        private final String greeting;
        @Nullable private final LastShipmentCard lastShipment;

        public Content(String greeting, @Nullable LastShipmentCard lastShipment) {
            this.greeting = greeting;
            this.lastShipment = lastShipment;
        }

        public String getGreeting() {
            return greeting;
        }

        @Nullable
        public LastShipmentCard getLastShipment() {
            return lastShipment;
        }
    }

    public static final class Error extends HomeUiState {
        private final String message;

        public Error(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    public static final class LastShipmentCard {
        private final long localId;
        @Nullable private final String firestoreId;
        private final String title;
        private final String statusLabel;

        public LastShipmentCard(long localId,
                                @Nullable String firestoreId,
                                String title,
                                String statusLabel) {
            this.localId = localId;
            this.firestoreId = firestoreId;
            this.title = title;
            this.statusLabel = statusLabel;
        }

        public long getLocalId() {
            return localId;
        }

        @Nullable
        public String getFirestoreId() {
            return firestoreId;
        }

        public String getTitle() {
            return title;
        }

        public String getStatusLabel() {
            return statusLabel;
        }
    }
}
