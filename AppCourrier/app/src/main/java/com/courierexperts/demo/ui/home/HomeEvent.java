package com.courierexperts.demo.ui.home;

import androidx.annotation.Nullable;




public class HomeEvent {

    public enum Type { PROMPT_DEPOSIT, SHOW_ERROR }

    private final Type type;
    @Nullable private final String payload;

    private HomeEvent(Type type, @Nullable String payload) {
        this.type = type;
        this.payload = payload;
    }

    public static HomeEvent depositReminder() {
        return new HomeEvent(Type.PROMPT_DEPOSIT, null);
    }

    public static HomeEvent showError(@Nullable String debugMessage) {
        return new HomeEvent(Type.SHOW_ERROR, debugMessage);
    }

    public Type getType() {
        return type;
    }

    @Nullable
    public String getPayload() {
        return payload;
    }
}
