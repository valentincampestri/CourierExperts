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
        btnNext.setOnClickListener(v -> {
            com.google.android.material.textfield.TextInputEditText et = findViewById(R.id.etNombreSignup);
            String name = et != null && et.getText()!=null ? et.getText().toString().trim(): "";
            if (!name.matches("[\\p{L} ]{2,80}")) {
                android.widget.Toast.makeText(this, "Nombre inválido", android.widget.Toast.LENGTH_SHORT).show();
                return;
            }
            // Persistimos nombre localmente para saludo inmediato
            new com.courierexperts.demo.data.repository.UserProfileRepository(this).updateName(name);
            startActivity(new Intent(SignUpStep1Activity.this, SignUpStep2Activity.class));
        });
    }
}
