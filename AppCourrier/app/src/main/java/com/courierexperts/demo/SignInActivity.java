package com.courierexperts.demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class SignInActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        View btnBack = findViewById(R.id.btnBack);
        View btnLogin = findViewById(R.id.btnLogin);
        btnBack.setOnClickListener(v -> finish());
        auth = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(v -> doEmailLogin());

        // Recuperar contraseña (Olvidé mi contraseña)
        View tvForgot = findViewById(R.id.tvForgot);
        if (tvForgot != null) {
            tvForgot.setOnClickListener(v -> showResetPasswordDialog());
        }

    }

    private void doEmailLogin() {
        com.google.android.material.textfield.TextInputEditText etEmail = findViewById(R.id.etEmailSignin);
        com.google.android.material.textfield.TextInputEditText etPass = findViewById(R.id.etPasswordSignin);
        String email = etEmail != null && etEmail.getText()!=null ? etEmail.getText().toString().trim(): "";
        String pass  = etPass  != null && etPass.getText()!=null  ? etPass.getText().toString()       : "";
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            toast("Email inválido");
            return;
        }
        if (pass.length() < 4 || pass.length() > 20) {
            toast("Contraseña 4 a 20 caracteres");
            return;
        }

        findViewById(R.id.btnLogin).setEnabled(false);
        auth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, task -> {
                    findViewById(R.id.btnLogin).setEnabled(true);
                    if (task.isSuccessful()) {
                        onAuthed(auth.getCurrentUser());
                    } else {
                        toast("No se pudo iniciar sesión");
                    }
                });
    }

    private void showResetPasswordDialog() {
        TextInputLayout til = new TextInputLayout(this);
        til.setHint("Email");
        TextInputEditText et = new TextInputEditText(this);
        et.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        // Prefill con el email si ya está cargado en el form
        TextInputEditText etEmailSignin = findViewById(R.id.etEmailSignin);
        if (etEmailSignin != null && etEmailSignin.getText() != null) {
            et.setText(etEmailSignin.getText().toString());
        }
        til.addView(et);

        final com.google.android.material.dialog.MaterialAlertDialogBuilder b = new MaterialAlertDialogBuilder(this)
                .setTitle("Recuperar contraseña")
                .setMessage("Ingresá tu email para enviarte el enlace de recuperación")
                .setView(til)
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Enviar", null);

        final androidx.appcompat.app.AlertDialog d = b.create();
        d.setOnShowListener(dialog -> {
            android.widget.Button positive = d.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE);
            positive.setOnClickListener(v -> {
                String email = et.getText() != null ? et.getText().toString().trim() : "";
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    til.setError("Email inválido");
                    return;
                }
                til.setError(null);
                positive.setEnabled(false);
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                        .addOnCompleteListener(task -> {
                            positive.setEnabled(true);
                            if (task.isSuccessful()) {
                                toast("Si su cuenta existe, le enviaremos un email para reestablecer su contraseña");
                                d.dismiss();
                            } else {
                                toast("No pudimos enviar el email, intentá de nuevo");
                            }
                        });
            });
        });
        d.show();
    }

    private void onAuthed(FirebaseUser user) {
        if (user == null) { toast("Error de autenticación"); return; }
        // Inicializar/actualizar perfil local y sincronizar remoto
        com.courierexperts.demo.data.repository.UserProfileRepository repo = new com.courierexperts.demo.data.repository.UserProfileRepository(this);
        // Primero traemos Firestore -> Room para evitar sobreescrituras con vacíos
        repo.syncFromFirestore();
        // Luego aplicamos updates puntuales que harán merge en Firestore
        String email = user.getEmail()!=null?user.getEmail():"";
        if (!email.isEmpty()) repo.updateEmail(email);
        if (user.getDisplayName()!=null && !user.getDisplayName().trim().isEmpty()) {
            repo.updateName(user.getDisplayName().trim());
        }
        repo.enqueueSyncNow();
        startActivity(new Intent(SignInActivity.this, HomeActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK));
        finish();
    }

    private void toast(@NonNull String s) { Toast.makeText(this, s, Toast.LENGTH_SHORT).show(); }
}
