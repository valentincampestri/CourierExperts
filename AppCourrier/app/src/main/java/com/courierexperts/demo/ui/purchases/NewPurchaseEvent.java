package com.courierexperts.demo.ui.purchases;

import androidx.annotation.Nullable;

public class NewPurchaseEvent {
    public enum Type { SHOW_MESSAGE, SUCCESS }

    private final Type type;
    @Nullable private final String message;

    private NewPurchaseEvent(Type type, @Nullable String message) {
        this.type = type;
        this.message = message;
    }

    public static NewPurchaseEvent showMessage(@Nullable String message) {
        return new NewPurchaseEvent(Type.SHOW_MESSAGE, message);
    }

    public static NewPurchaseEvent success(String message) {
        return new NewPurchaseEvent(Type.SUCCESS, message);
    }

    public Type getType() {
        return type;
    }

    @Nullable
    public String getMessage() {
        return message;
    }
}
