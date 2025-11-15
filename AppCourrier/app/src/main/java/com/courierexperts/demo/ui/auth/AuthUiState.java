package com.courierexperts.demo.ui.auth;

/**
 * Estados simples para pantallas de autenticaciA3n.
 */
public abstract class AuthUiState {
    private AuthUiState() {}

    public static final class Idle extends AuthUiState { }
    public static final class Loading extends AuthUiState { }
}
