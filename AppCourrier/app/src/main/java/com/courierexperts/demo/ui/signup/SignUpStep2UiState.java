package com.courierexperts.demo.ui.signup;

public abstract class SignUpStep2UiState {
    private SignUpStep2UiState() {}

    public static final class Idle extends SignUpStep2UiState {}
    public static final class Loading extends SignUpStep2UiState {}
}
