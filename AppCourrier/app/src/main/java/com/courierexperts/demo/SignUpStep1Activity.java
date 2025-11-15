package com.courierexperts.demo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.courierexperts.demo.databinding.ActivitySignupStep1Binding;
import com.courierexperts.demo.ui.signup.SignUpStep1Event;
import com.courierexperts.demo.ui.signup.SignUpStep1ViewModel;

public class SignUpStep1Activity extends AppCompatActivity {

    private ActivitySignupStep1Binding b;
    private SignUpStep1ViewModel vm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivitySignupStep1Binding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        vm = new ViewModelProvider(this).get(SignUpStep1ViewModel.class);
        observeViewModel();

        b.btnBack.setOnClickListener(v -> finish());
        b.btnNext.setOnClickListener(v -> doNext());
    }

    private void observeViewModel() {
        vm.getEvents().observe(this, event -> {
            if (event == null) return;
            SignUpStep1Event payload = event.getContentIfNotHandled();
            if (payload == null) return;
            if (payload.getType() == SignUpStep1Event.Type.SHOW_MESSAGE) {
                if (payload.getMessage() != null) {
                    Toast.makeText(this, payload.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else if (payload.getType() == SignUpStep1Event.Type.NAVIGATE_STEP2 && payload.getData() != null) {
                Intent i = new Intent(this, SignUpStep2Activity.class);
                i.putExtra(SignUpStep2Activity.EXTRA_SIGNUP_DATA, payload.getData());
                startActivity(i);
            }
        });
    }

    private void doNext() {
        String nombre = textOf(b.etNombreSignup);
        String apellido = textOf(b.etApellidoSignup);
        String dni = textOf(b.etDniSignUp);
        String cuil = textOf(b.etCuilSignUp);
        vm.submitStepOne(nombre, apellido, dni, cuil);
    }

    private static String textOf(@Nullable com.google.android.material.textfield.TextInputEditText et) {
        if (et != null && et.getText() != null) {
            return et.getText().toString();
        }
        return "";
    }
}
