package com.courierexperts.demo.ui.packages;

import com.courierexperts.demo.data.local.entity.PackageEntity;

public abstract class PackageDetailUiState {
    private PackageDetailUiState() {}

    public static final class Loading extends PackageDetailUiState {}
    public static final class NotFound extends PackageDetailUiState {}

    public static final class Success extends PackageDetailUiState {
        private final PackageEntity entity;

        public Success(PackageEntity entity) {
            this.entity = entity;
        }

        public PackageEntity getEntity() {
            return entity;
        }
    }

    public static final class Error extends PackageDetailUiState {
        private final String message;

        public Error(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}
