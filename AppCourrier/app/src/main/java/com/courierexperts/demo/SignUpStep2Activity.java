package com.courierexperts.demo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.IntentCompat;
import androidx.lifecycle.ViewModelProvider;

import com.courierexperts.demo.databinding.ActivitySignupStep2Binding;
import com.courierexperts.demo.ui.signup.SignUpData;
import com.courierexperts.demo.ui.signup.SignUpStep2Event;
import com.courierexperts.demo.ui.signup.SignUpStep2UiState;
import com.courierexperts.demo.ui.signup.SignUpStep2ViewModel;
import com.google.android.material.textfield.TextInputEditText;

public class SignUpStep2Activity extends AppCompatActivity {

    public static final String EXTRA_SIGNUP_DATA = "extra_signup_data";

    private ActivitySignupStep2Binding b;
    private SignUpStep2ViewModel vm;
    private SignUpData step1Data;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivitySignupStep2Binding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        step1Data = IntentCompat.getParcelableExtra(getIntent(), EXTRA_SIGNUP_DATA, SignUpData.class);
        if (step1Data == null) {
            Toast.makeText(this, R.string.signup_missing_step, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        vm = new ViewModelProvider(this).get(SignUpStep2ViewModel.class);
        observeViewModel();

        b.btnBack.setOnClickListener(v -> finish());
        b.btnSave.setOnClickListener(v -> doRegister());
    }

    private void observeViewModel() {
        vm.getUiState().observe(this, state -> {
            boolean loading = state instanceof SignUpStep2UiState.Loading;
            b.btnSave.setEnabled(!loading);
            if (b.progressBar != null) {
                b.progressBar.setVisibility(loading ? android.view.View.VISIBLE : android.view.View.GONE);
            }
        });

        vm.getEvents().observe(this, event -> {
            if (event == null) return;
            SignUpStep2Event payload = event.getContentIfNotHandled();
            if (payload == null) return;
            if (payload.getType() == SignUpStep2Event.Type.SHOW_MESSAGE) {
                if (payload.getMessage() != null) {
                    Toast.makeText(this, payload.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else if (payload.getType() == SignUpStep2Event.Type.NAVIGATE_HOME) {
                navigateHome();
            }
        });
    }

    private void doRegister() {
        String direccion = textOf(b.etDireccionSignup);
        String provincia = b.spProvinciaSignup != null && b.spProvinciaSignup.getSelectedItem() != null
                ? b.spProvinciaSignup.getSelectedItem().toString()
                : "";
        String telefono = textOf(b.etTelefonoSignup);
        String email = textOf(b.etEmailSignup);
        String password = textOf(b.etPasswordSignup);
        vm.register(step1Data, direccion, provincia, telefono, email, password);
    }

    private void navigateHome() {
        startActivity(new Intent(this, HomeActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
        finish();
    }

    private static String textOf(@Nullable TextInputEditText et) {
        if (et != null && et.getText() != null) {
            return et.getText().toString();
        }
        return "";
    }
}
