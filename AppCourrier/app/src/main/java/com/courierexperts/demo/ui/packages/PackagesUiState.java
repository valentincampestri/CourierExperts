package com.courierexperts.demo.ui.packages;

import com.courierexperts.demo.data.local.entity.PackageEntity;

import java.util.List;

public abstract class PackagesUiState {

    private PackagesUiState() {}

    public static final class Loading extends PackagesUiState {}

    public static final class Empty extends PackagesUiState {}

    public static final class Success extends PackagesUiState {
        private final List<PackageEntity> packages;

        public Success(List<PackageEntity> packages) {
            this.packages = packages;
        }

        public List<PackageEntity> getPackages() {
            return packages;
        }
    }

    public static final class Error extends PackagesUiState {
        private final String message;

        public Error(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}
