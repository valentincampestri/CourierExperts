package com.courierexperts.demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        View btnSignIn = findViewById(R.id.btnSignIn);
        View btnRegister = findViewById(R.id.btnRegister);

        btnSignIn.setOnClickListener(v -> {
            Intent i = new Intent(WelcomeActivity.this, SignInActivity.class);
            startActivity(i);
        });
        btnRegister.setOnClickListener(v -> {
            Intent i = new Intent(WelcomeActivity.this, SignUpStep1Activity.class);
            startActivity(i);
        });
    }
}
