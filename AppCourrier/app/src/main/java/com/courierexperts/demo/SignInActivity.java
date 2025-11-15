package com.courierexperts.demo;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.courierexperts.demo.databinding.ActivitySigninBinding;
import com.courierexperts.demo.ui.auth.AuthEvent;
import com.courierexperts.demo.ui.auth.AuthUiState;
import com.courierexperts.demo.ui.auth.AuthViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class SignInActivity extends AppCompatActivity {

    private ActivitySigninBinding b;
    private AuthViewModel vm;
    private AlertDialog resetDialog;
    private android.widget.Button resetPositiveButton;
    private TextInputLayout resetEmailTil;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivitySigninBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        vm = new ViewModelProvider(this).get(AuthViewModel.class);
        observeViewModel();

        if (b.btnBack != null) {
            b.btnBack.setOnClickListener(v -> finish());
        }
        if (b.btnLogin != null) {
            b.btnLogin.setOnClickListener(v -> doEmailLogin());
        }
        if (b.tvForgot != null) {
            b.tvForgot.setOnClickListener(v -> showResetPasswordDialog());
        }
    }

    private void observeViewModel() {
        vm.getUiState().observe(this, state -> {
            boolean loading = state instanceof AuthUiState.Loading;
            if (b.btnLogin != null) {
                b.btnLogin.setEnabled(!loading);
            }
            if (b.progressBar != null) {
                b.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
            }
        });

        vm.getEvents().observe(this, event -> {
            if (event == null) return;
            AuthEvent payload = event.getContentIfNotHandled();
            if (payload == null) return;
            if (payload.getType() == AuthEvent.Type.NAVIGATE_HOME) {
                navigateHome();
            } else if (payload.getType() == AuthEvent.Type.SHOW_MESSAGE) {
                if (payload.getMessage() != null) {
                    toast(payload.getMessage());
                }
            } else if (payload.getType() == AuthEvent.Type.RESET_EMAIL_DONE) {
                handleResetResult(payload);
            }
        });
    }

    private void doEmailLogin() {
        String email = b.etEmailSignin != null && b.etEmailSignin.getText() != null
                ? b.etEmailSignin.getText().toString().trim()
                : "";
        String pass = b.etPasswordSignin != null && b.etPasswordSignin.getText() != null
                ? b.etPasswordSignin.getText().toString()
                : "";
        vm.login(email, pass);
    }

    private void showResetPasswordDialog() {
        resetEmailTil = new TextInputLayout(this);
        resetEmailTil.setHint("Email");
        TextInputEditText et = new TextInputEditText(this);
        et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        if (b.etEmailSignin != null && b.etEmailSignin.getText() != null) {
            et.setText(b.etEmailSignin.getText().toString());
        }
        resetEmailTil.addView(et);

        resetDialog = new MaterialAlertDialogBuilder(this)
                .setTitle("Recuperar contrasena")
                .setMessage("Ingresa tu email para enviarte el enlace de recuperacion")
                .setView(resetEmailTil)
                .setNegativeButton("Cancelar", (d, w) -> clearResetDialogRefs())
                .setPositiveButton("Enviar", null)
                .create();
        resetDialog.setOnDismissListener(dialog -> clearResetDialogRefs());
        resetDialog.setOnShowListener(dialog -> {
            resetPositiveButton = resetDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            if (resetPositiveButton != null) {
                resetPositiveButton.setOnClickListener(v -> {
                    if (resetEmailTil != null) {
                        resetEmailTil.setError(null);
                    }
                    resetPositiveButton.setEnabled(false);
                    String email = et.getText() != null ? et.getText().toString().trim() : "";
                    vm.resetPassword(email);
                });
            }
        });
        resetDialog.show();
    }

    private void handleResetResult(@NonNull AuthEvent event) {
        if (event.getMessage() != null) {
            toast(event.getMessage());
        }
        if (event.isSuccess()) {
            if (resetDialog != null && resetDialog.isShowing()) {
                resetDialog.dismiss();
            }
            clearResetDialogRefs();
        } else {
            if (resetPositiveButton != null) {
                resetPositiveButton.setEnabled(true);
            }
            if (resetEmailTil != null && event.getResetReason() == AuthEvent.ResetReason.INVALID_EMAIL) {
                resetEmailTil.setError(event.getMessage());
            }
        }
    }

    private void navigateHome() {
        startActivity(new Intent(this, HomeActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
        finish();
    }

    private void clearResetDialogRefs() {
        resetPositiveButton = null;
        resetEmailTil = null;
        resetDialog = null;
    }

    private void toast(@NonNull String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }
}
