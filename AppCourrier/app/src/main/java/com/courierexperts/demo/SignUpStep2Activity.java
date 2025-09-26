package com.courierexperts.demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SignUpStep2Activity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_step2);

        View btnBack = findViewById(R.id.btnBack);
        View btnSave = findViewById(R.id.btnSave);
        btnBack.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> startActivity(new Intent(SignUpStep2Activity.this, HomeActivity.class)));
    }
}
