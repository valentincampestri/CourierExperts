package com.courierexperts.demo.ui.signup;

import androidx.annotation.Nullable;

public class SignUpStep2Event {
    public enum Type { SHOW_MESSAGE, NAVIGATE_HOME }

    private final Type type;
    @Nullable private final String message;

    private SignUpStep2Event(Type type, @Nullable String message) {
        this.type = type;
        this.message = message;
    }

    public static SignUpStep2Event showMessage(@Nullable String message) {
        return new SignUpStep2Event(Type.SHOW_MESSAGE, message);
    }

    public static SignUpStep2Event navigateHome() {
        return new SignUpStep2Event(Type.NAVIGATE_HOME, null);
    }

    public Type getType() {
        return type;
    }

    @Nullable
    public String getMessage() {
        return message;
    }
}
