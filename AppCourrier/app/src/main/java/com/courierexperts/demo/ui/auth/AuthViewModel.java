package com.courierexperts.demo.ui.auth;

import android.app.Application;
import android.text.TextUtils;
import android.util.Patterns;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.courierexperts.demo.R;
import com.courierexperts.demo.data.repository.UserProfileRepository;
import com.courierexperts.demo.util.Event;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Gestiona login, recordatorio de contraseA3n y navegaciA3n de Welcome/SignIn.
 */
public class AuthViewModel extends AndroidViewModel {

    private final FirebaseAuth auth;
    private final UserProfileRepository profileRepository;
    private final MutableLiveData<AuthUiState> uiState = new MutableLiveData<>(new AuthUiState.Idle());
    private final MutableLiveData<Event<AuthEvent>> events = new MutableLiveData<>();

    public AuthViewModel(@NonNull Application application) {
        super(application);
        this.auth = FirebaseAuth.getInstance();
        this.profileRepository = new UserProfileRepository(application);
    }

    public LiveData<AuthUiState> getUiState() {
        return uiState;
    }

    public LiveData<Event<AuthEvent>> getEvents() {
        return events;
    }

    public void checkSession() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            events.setValue(new Event<>(AuthEvent.navigateHome()));
        }
    }

    public void login(String email, String password) {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            events.setValue(new Event<>(AuthEvent.showMessage(getApplication().getString(R.string.auth_error_invalid_email))));
            return;
        }
        if (password.length() < 4 || password.length() > 20) {
            events.setValue(new Event<>(AuthEvent.showMessage(getApplication().getString(R.string.auth_error_invalid_password))));
            return;
        }
        uiState.setValue(new AuthUiState.Loading());
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    uiState.setValue(new AuthUiState.Idle());
                    if (task.isSuccessful()) {
                        onAuthed(auth.getCurrentUser());
                    } else {
                        events.setValue(new Event<>(AuthEvent.showMessage(getApplication().getString(R.string.auth_error_login))));
                    }
                });
    }

    public void resetPassword(String email) {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            events.setValue(new Event<>(AuthEvent.resetEmailResult(
                    false,
                    getApplication().getString(R.string.auth_error_invalid_email),
                    AuthEvent.ResetReason.INVALID_EMAIL)));
            return;
        }
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        events.setValue(new Event<>(AuthEvent.resetEmailResult(
                                true,
                                getApplication().getString(R.string.auth_reset_email_sent),
                                null)));
                    } else {
                        events.setValue(new Event<>(AuthEvent.resetEmailResult(
                                false,
                                getApplication().getString(R.string.auth_reset_email_failed),
                                AuthEvent.ResetReason.GENERIC)));
                    }
                });
    }

    private void onAuthed(FirebaseUser user) {
        if (user == null) {
            events.setValue(new Event<>(AuthEvent.showMessage(getApplication().getString(R.string.auth_error_generic))));
            return;
        }
        // Sincronizar perfil local <-> Firestore
        profileRepository.syncFromFirestore();
        String email = user.getEmail();
        if (!TextUtils.isEmpty(email)) {
            profileRepository.updateEmail(email);
        }
        if (!TextUtils.isEmpty(user.getDisplayName())) {
            profileRepository.updateName(user.getDisplayName());
        }
        profileRepository.enqueueSyncNow();
        events.setValue(new Event<>(AuthEvent.navigateHome()));
    }
}
