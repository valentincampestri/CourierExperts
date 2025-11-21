package com.courierexperts.demo.ui.auth;




public abstract class AuthUiState {
    private AuthUiState() {}

    public static final class Idle extends AuthUiState { }
    public static final class Loading extends AuthUiState { }
}
