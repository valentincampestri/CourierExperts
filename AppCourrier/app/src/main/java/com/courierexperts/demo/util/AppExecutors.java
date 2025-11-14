package com.courierexperts.demo.util;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AppExecutors {
    private static final Executor IO = Executors.newSingleThreadExecutor();
    private static final Executor MAIN = new Executor() {
        private final Handler h = new Handler(Looper.getMainLooper());
        @Override public void execute(Runnable command) { h.post(command); }
    };

    public static Executor io() { return IO; }
    public static Executor main() { return MAIN; }
}
