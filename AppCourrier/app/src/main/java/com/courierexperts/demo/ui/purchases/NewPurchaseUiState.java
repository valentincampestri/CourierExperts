package com.courierexperts.demo.ui.purchases;

public abstract class NewPurchaseUiState {
    private NewPurchaseUiState() {}

    public static final class Idle extends NewPurchaseUiState {}
    public static final class Loading extends NewPurchaseUiState {}
}
