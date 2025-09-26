package com.courierexperts.demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SignUpStep1Activity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_step1);

        View btnBack = findViewById(R.id.btnBack);
        View btnNext = findViewById(R.id.btnNext);
        btnBack.setOnClickListener(v -> finish());
        btnNext.setOnClickListener(v -> startActivity(new Intent(SignUpStep1Activity.this, SignUpStep2Activity.class)));
    }
}
