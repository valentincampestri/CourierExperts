package com.courierexperts.demo.ui.profile;

import com.courierexperts.demo.data.local.entity.UserProfileEntity;

public abstract class ProfileUiState {
    private ProfileUiState() {}

    public static final class Loading extends ProfileUiState {}

    public static final class Success extends ProfileUiState {
        private final UserProfileEntity profile;

        public Success(UserProfileEntity profile) {
            this.profile = profile;
        }

        public UserProfileEntity getProfile() {
            return profile;
        }
    }

    public static final class Error extends ProfileUiState {
        private final String message;

        public Error(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}
