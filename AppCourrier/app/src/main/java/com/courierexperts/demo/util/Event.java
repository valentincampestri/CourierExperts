package com.courierexperts.demo.util;

import androidx.annotation.Nullable;

/**
 * Wrapper simple para exponer eventos de LiveData y consumirlos una sola vez
 * (por ejemplo, toasts o navegaciones).
 */
public class Event<T> {

    private final T content;
    private boolean handled;

    public Event(T content) {
        this.content = content;
    }

    /**
     * Devuelve el contenido si aún no fue consumido; de lo contrario retorna null.
     */
    @Nullable
    public T getContentIfNotHandled() {
        if (handled) {
            return null;
        }
        handled = true;
        return content;
    }

    /**
     * Devuelve el contenido sin modificar el flag de consumo.
     */
    public T peek() {
        return content;
    }
}
