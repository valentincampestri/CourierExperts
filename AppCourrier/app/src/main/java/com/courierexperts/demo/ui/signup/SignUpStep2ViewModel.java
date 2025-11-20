package com.courierexperts.demo.ui.signup;

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
import com.google.firebase.auth.UserProfileChangeRequest;
// Se eliminaron imports de Firestore directos para evitar conflictos

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class SignUpStep2ViewModel extends AndroidViewModel {

    private final MutableLiveData<SignUpStep2UiState> uiState = new MutableLiveData<>(new SignUpStep2UiState.Idle());
    private final MutableLiveData<Event<SignUpStep2Event>> events = new MutableLiveData<>();
    private final FirebaseAuth auth;
    private final UserProfileRepository profileRepository;

    public SignUpStep2ViewModel(@NonNull Application application) {
        super(application);
        auth = FirebaseAuth.getInstance();
        profileRepository = new UserProfileRepository(application);
    }

    public LiveData<SignUpStep2UiState> getUiState() {
        return uiState;
    }

    public LiveData<Event<SignUpStep2Event>> getEvents() {
        return events;
    }

    public void register(SignUpData step1,
                         String direccion,
                         String provincia,
                         String telefono,
                         String email,
                         String password,
                         String confirmPassword) {
        if (step1 == null) {
            events.setValue(new Event<>(SignUpStep2Event.showMessage(getApplication().getString(R.string.signup_missing_step))));
            return;
        }
        String dir = safe(direccion);
        if (dir.length() < 2 || dir.length() > 50) {
            events.setValue(new Event<>(SignUpStep2Event.showMessage(getApplication().getString(R.string.signup_error_address))));
            return;
        }
        String prov = safe(provincia);
        if (TextUtils.isEmpty(prov) || "Seleccionar".equalsIgnoreCase(prov)) {
            events.setValue(new Event<>(SignUpStep2Event.showMessage(getApplication().getString(R.string.signup_error_province))));
            return;
        }
        String phone = safe(telefono);
        // Permitir +, dígitos, espacios y guiones
        if (!phone.matches("[+0-9\\s\\-]{6,25}")) {
            events.setValue(new Event<>(SignUpStep2Event.showMessage(getApplication().getString(R.string.signup_error_phone))));
            return;
        }
        String mail = safe(email);
        if (!Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
            events.setValue(new Event<>(SignUpStep2Event.showMessage(getApplication().getString(R.string.auth_error_invalid_email))));
            return;
        }
        if (password == null || password.length() < 8 || password.length() > 20) {
            events.setValue(new Event<>(SignUpStep2Event.showMessage(getApplication().getString(R.string.auth_error_invalid_password))));
            return;
        }
        if (!password.equals(confirmPassword)) {
            events.setValue(new Event<>(SignUpStep2Event.showMessage(getApplication().getString(R.string.error_passwords_dont_match))));
            return;
        }

        uiState.setValue(new SignUpStep2UiState.Loading());
        auth.createUserWithEmailAndPassword(mail, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        applyProfile(step1, dir, prov, phone, mail);
                        uiState.setValue(new SignUpStep2UiState.Idle());
                        events.setValue(new Event<>(SignUpStep2Event.navigateHome()));
                    } else {
                        uiState.setValue(new SignUpStep2UiState.Idle());
                        events.setValue(new Event<>(SignUpStep2Event.showMessage(getApplication().getString(R.string.signup_error_register))));
                    }
                });
    }

    private void applyProfile(SignUpData step1,
                              String direccion,
                              String provincia,
                              String telefono,
                              String email) {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            // 1. Actualizar DisplayName en Auth (Opcional pero recomendado)
            user.updateProfile(new UserProfileChangeRequest.Builder()
                    .setDisplayName(step1.getNombre())
                    .build());

            // 2. Obtener el UID seguro
            String uid = user.getUid();

            // 3. Delegar todo el guardado al Repositorio pasando el UID explícito
            profileRepository.saveAllSignupProfile(
                    uid, // <--- IMPORTANTE: Pasamos el UID aquí
                    step1.getNombre(),
                    step1.getApellido(),
                    step1.getDni(),
                    step1.getCuil(),
                    direccion,
                    provincia,
                    "Argentina",
                    email,
                    telefono
            );
        }
    }

    private static String safe(String s) {
        return s != null ? s.trim() : "";
    }
}