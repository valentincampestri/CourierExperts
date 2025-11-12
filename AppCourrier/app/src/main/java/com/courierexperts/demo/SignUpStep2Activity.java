package com.courierexperts.demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class SignUpStep2Activity extends AppCompatActivity {
    private FirebaseAuth auth;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_step2);

        View btnBack = findViewById(R.id.btnBack);
        View btnSave = findViewById(R.id.btnSave);
        btnBack.setOnClickListener(v -> finish());
        auth = FirebaseAuth.getInstance();
        btnSave.setOnClickListener(v -> doRegister());
    }

    private void doRegister() {
        com.google.android.material.textfield.TextInputEditText etEmail = findViewById(R.id.etEmailSignup);
        com.google.android.material.textfield.TextInputEditText etPass = findViewById(R.id.etPasswordSignup);
        String email = etEmail != null && etEmail.getText()!=null ? etEmail.getText().toString().trim(): "";
        String pass  = etPass  != null && etPass.getText()!=null  ? etPass.getText().toString()       : "";
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) { toast("Email inválido"); return; }
        if (pass.length() < 4 || pass.length() > 20) { toast("Contraseña 4 a 20 caracteres"); return; }
        findViewById(R.id.btnSave).setEnabled(false);
        auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(this, t -> {
            findViewById(R.id.btnSave).setEnabled(true);
            if (t.isSuccessful()) {
                // Guardar perfil básico
                new com.courierexperts.demo.data.repository.UserProfileRepository(this).updateEmail(email);
                startActivity(new Intent(SignUpStep2Activity.this, HomeActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK));
                finish();
            } else toast("No se pudo registrar");
        });
    }

    private void toast(String s){ Toast.makeText(this, s, Toast.LENGTH_SHORT).show(); }
}
