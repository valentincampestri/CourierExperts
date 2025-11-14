package com.courierexperts.demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class WelcomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(this, HomeActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
            finish();
            return;
        }

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
