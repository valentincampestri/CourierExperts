package com.courierexperts.demo.ui.profile;

import com.courierexperts.demo.data.local.entity.DepositEntity;
import com.courierexperts.demo.data.local.entity.UserProfileEntity;

import java.util.List;

public abstract class EditProfileUiState {
    private EditProfileUiState() {}

    public static final class Loading extends EditProfileUiState {}

    public static final class Success extends EditProfileUiState {
        private final UserProfileEntity profile;
        private final List<DepositEntity> deposits;

        public Success(UserProfileEntity profile, List<DepositEntity> deposits) {
            this.profile = profile;
            this.deposits = deposits;
        }

        public UserProfileEntity getProfile() { return profile; }
        public List<DepositEntity> getDeposits() { return deposits; }
    }

    public static final class Error extends EditProfileUiState {
        private final String message;

        public Error(String message) { this.message = message; }
        public String getMessage() { return message; }
    }

    public static final class Saved extends EditProfileUiState {}
}
