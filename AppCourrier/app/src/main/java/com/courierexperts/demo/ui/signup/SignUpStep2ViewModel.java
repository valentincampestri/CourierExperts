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
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
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
                         String password) {
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
        if (!phone.matches("[+0-9]{6,20}")) {
            events.setValue(new Event<>(SignUpStep2Event.showMessage(getApplication().getString(R.string.signup_error_phone))));
            return;
        }
        String mail = safe(email);
        if (!Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
            events.setValue(new Event<>(SignUpStep2Event.showMessage(getApplication().getString(R.string.auth_error_invalid_email))));
            return;
        }
        if (password == null || password.length() < 4 || password.length() > 20) {
            events.setValue(new Event<>(SignUpStep2Event.showMessage(getApplication().getString(R.string.auth_error_invalid_password))));
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
            user.updateProfile(new UserProfileChangeRequest.Builder()
                    .setDisplayName(step1.getNombre())
                    .build());
            String uid = user.getUid();
            Map<String, Object> map = new HashMap<>();
            map.put("name", step1.getNombre());
            map.put("lastName", step1.getApellido());
            map.put("dni", step1.getDni());
            map.put("cuil", step1.getCuil());
            map.put("address", direccion);
            map.put("province", provincia);
            map.put("country", "Argentina");
            map.put("email", email);
            map.put("phone", telefono);
            map.put("updatedAt", nowIso());
            FirebaseFirestore.getInstance().collection("users").document(uid).set(map);
        }
        profileRepository.saveAllSignupProfile(
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

    private static String safe(String s) {
        return s != null ? s.trim() : "";
    }

    private static String nowIso() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(new Date());
    }
}
