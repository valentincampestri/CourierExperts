package com.courierexperts.demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SignInActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        View btnBack = findViewById(R.id.btnBack);
        View btnLogin = findViewById(R.id.btnLogin);
        btnBack.setOnClickListener(v -> finish());
        btnLogin.setOnClickListener(v -> startActivity(new Intent(SignInActivity.this, HomeActivity.class)));
    }
}
