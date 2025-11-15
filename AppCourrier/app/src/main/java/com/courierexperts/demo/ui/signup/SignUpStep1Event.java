package com.courierexperts.demo.ui.signup;

import androidx.annotation.Nullable;

public class SignUpStep1Event {
    public enum Type { SHOW_MESSAGE, NAVIGATE_STEP2 }

    private final Type type;
    @Nullable private final String message;
    @Nullable private final SignUpData data;

    private SignUpStep1Event(Type type, @Nullable String message, @Nullable SignUpData data) {
        this.type = type;
        this.message = message;
        this.data = data;
    }

    public static SignUpStep1Event showMessage(String message) {
        return new SignUpStep1Event(Type.SHOW_MESSAGE, message, null);
    }

    public static SignUpStep1Event navigate(SignUpData data) {
        return new SignUpStep1Event(Type.NAVIGATE_STEP2, null, data);
    }

    public Type getType() {
        return type;
    }

    @Nullable
    public String getMessage() {
        return message;
    }

    @Nullable
    public SignUpData getData() {
        return data;
    }
}
