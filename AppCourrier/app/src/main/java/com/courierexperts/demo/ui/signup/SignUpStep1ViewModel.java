package com.courierexperts.demo.ui.signup;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.courierexperts.demo.R;
import com.courierexperts.demo.util.Event;
import com.courierexperts.demo.util.ValidationUtils;

public class SignUpStep1ViewModel extends AndroidViewModel {

    private final MutableLiveData<Event<SignUpStep1Event>> events = new MutableLiveData<>();

    public SignUpStep1ViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<Event<SignUpStep1Event>> getEvents() {
        return events;
    }

    public void submitStepOne(String nombre,
                              String apellido,
                              String dni,
                              String cuil) {
        String n = safe(nombre);
        if (n.length() < 2 || n.length() > 50) {
            events.setValue(new Event<>(SignUpStep1Event.showMessage(getApplication().getString(R.string.signup_error_name))));
            return;
        }
        String a = safe(apellido);
        if (a.length() < 2 || a.length() > 50) {
            events.setValue(new Event<>(SignUpStep1Event.showMessage(getApplication().getString(R.string.signup_error_last_name))));
            return;
        }
        String d = safe(dni);
        if (!d.matches("\\d{7,10}")) {
            events.setValue(new Event<>(SignUpStep1Event.showMessage(getApplication().getString(R.string.signup_error_dni))));
            return;
        }
        String c = safe(cuil);
        if (!ValidationUtils.hasValidCUILFormat(c)) {
            events.setValue(new Event<>(SignUpStep1Event.showMessage(getApplication().getString(R.string.signup_error_cuil))));
            return;
        }
        if (!ValidationUtils.isValidCUIL(c)) {
            events.setValue(new Event<>(SignUpStep1Event.showMessage("CUIL inv\u00e1lido. Verifique el d\u00edgito verificador")));
            return;
        }
        events.setValue(new Event<>(SignUpStep1Event.navigate(new SignUpData(n, a, d, c))));
    }

    private static String safe(String input) {
        return input != null ? input.trim() : "";
    }
}
