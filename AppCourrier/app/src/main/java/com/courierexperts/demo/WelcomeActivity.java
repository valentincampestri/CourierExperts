package com.courierexperts.demo;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.courierexperts.demo.databinding.ActivityWelcomeBinding;
import com.courierexperts.demo.ui.auth.AuthEvent;
import com.courierexperts.demo.ui.auth.AuthViewModel;

public class WelcomeActivity extends AppCompatActivity {

    private ActivityWelcomeBinding b;
    private AuthViewModel vm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityWelcomeBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        vm = new ViewModelProvider(this).get(AuthViewModel.class);
        observeViewModel();
        vm.checkSession();

        b.btnSignIn.setOnClickListener(v -> startActivity(new Intent(this, SignInActivity.class)));
        b.btnRegister.setOnClickListener(v -> startActivity(new Intent(this, SignUpStep1Activity.class)));
    }

    private void observeViewModel() {
        vm.getEvents().observe(this, event -> {
            if (event == null) return;
            AuthEvent payload = event.getContentIfNotHandled();
            if (payload == null) return;
            if (payload.getType() == AuthEvent.Type.NAVIGATE_HOME) {
                navigateHome();
            }
        });
    }

    private void navigateHome() {
        startActivity(new Intent(this, HomeActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
        finish();
    }
}
