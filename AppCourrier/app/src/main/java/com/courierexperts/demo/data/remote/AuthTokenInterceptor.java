package com.courierexperts.demo.data.remote;

import androidx.annotation.NonNull;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;

public class AuthTokenInterceptor implements Interceptor {
    // Cambiar a true cuando el backend exija Bearer
    public static final boolean ENABLED = false;

    @Override
    public @NonNull Response intercept(@NonNull Chain chain) throws IOException {
        Request original = chain.request();
        String token = null;
        try {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                token = Tasks.await(FirebaseAuth.getInstance().getCurrentUser().getIdToken(false)).getToken();
            }
        } catch (Exception ignored) {}
        if (token == null || token.trim().isEmpty()) return chain.proceed(original);
        Request withAuth = original.newBuilder().header("Authorization", "Bearer " + token).build();
        return chain.proceed(withAuth);
    }
}

