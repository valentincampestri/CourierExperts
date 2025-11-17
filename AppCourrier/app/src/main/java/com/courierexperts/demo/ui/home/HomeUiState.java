package com.courierexperts.demo.ui.home;

import java.util.List;

/**
 * Representa los estados de HomeActivity
 * (saludo y lista de actividad reciente).
 */
public abstract class HomeUiState {

    private HomeUiState() { }

    // Estado de carga
    public static final class Loading extends HomeUiState { }

    // Estado con contenido
    public static final class Content extends HomeUiState {
        private final String greeting;
        private final List<RecentActivityItem> recentActivityItems;

        public Content(String greeting, List<RecentActivityItem> recentActivityItems) {
            this.greeting = greeting;
            this.recentActivityItems = recentActivityItems;
        }

        public String getGreeting() {
            return greeting;
        }

        public List<RecentActivityItem> getRecentActivityItems() {
            return recentActivityItems;
        }
    }

    // Estado de error
    public static final class Error extends HomeUiState {
        private final String message;

        public Error(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}
