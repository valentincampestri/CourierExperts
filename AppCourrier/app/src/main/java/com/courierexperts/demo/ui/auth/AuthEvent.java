package com.courierexperts.demo.ui.auth;

import androidx.annotation.Nullable;

public class AuthEvent {
    public enum Type { NAVIGATE_HOME, SHOW_MESSAGE, RESET_EMAIL_DONE }
    public enum ResetReason { INVALID_EMAIL, GENERIC }

    private final Type type;
    @Nullable private final String message;
    private final boolean success;
    @Nullable private final ResetReason resetReason;

    private AuthEvent(Type type, @Nullable String message, boolean success, @Nullable ResetReason resetReason) {
        this.type = type;
        this.message = message;
        this.success = success;
        this.resetReason = resetReason;
    }

    public static AuthEvent navigateHome() {
        return new AuthEvent(Type.NAVIGATE_HOME, null, true, null);
    }

    public static AuthEvent showMessage(@Nullable String message) {
        return new AuthEvent(Type.SHOW_MESSAGE, message, false, null);
    }

    public static AuthEvent resetEmailResult(boolean success,
                                             @Nullable String message,
                                             @Nullable ResetReason reason) {
        return new AuthEvent(Type.RESET_EMAIL_DONE, message, success, reason);
    }

    public Type getType() {
        return type;
    }

    @Nullable
    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return success;
    }

    @Nullable
    public ResetReason getResetReason() {
        return resetReason;
    }
}
