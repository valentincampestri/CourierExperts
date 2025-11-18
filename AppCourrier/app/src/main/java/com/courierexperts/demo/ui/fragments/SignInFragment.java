package com.courierexperts.demo.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.courierexperts.demo.FragmentsHostActivity;
import com.courierexperts.demo.databinding.ActivitySigninBinding;
import com.courierexperts.demo.ui.auth.AuthEvent;
import com.courierexperts.demo.ui.auth.AuthUiState;
import com.courierexperts.demo.ui.auth.AuthViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class SignInFragment extends Fragment {

    private ActivitySigninBinding binding;
    private AuthViewModel viewModel;
    private AlertDialog resetDialog;
    private android.widget.Button resetPositiveButton;
    private TextInputLayout resetEmailTil;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ActivitySigninBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
        observeViewModel();
        setupClicks();
    }

    private void setupClicks() {
        if (binding.btnBack != null) {
            binding.btnBack.setOnClickListener(v -> requireActivity().getOnBackPressedDispatcher().onBackPressed());
        }
        if (binding.btnLogin != null) {
            binding.btnLogin.setOnClickListener(v -> doEmailLogin());
        }
        if (binding.tvForgot != null) {
            binding.tvForgot.setOnClickListener(v -> showResetPasswordDialog());
        }
    }

    private void observeViewModel() {
        viewModel.getUiState().observe(getViewLifecycleOwner(), state -> {
            boolean loading = state instanceof AuthUiState.Loading;
            if (binding.btnLogin != null) {
                binding.btnLogin.setEnabled(!loading);
            }
            if (binding.progressBar != null) {
                binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
            }
        });

        viewModel.getEvents().observe(getViewLifecycleOwner(), event -> {
            if (event == null) return;
            AuthEvent payload = event.getContentIfNotHandled();
            if (payload == null) return;
            if (payload.getType() == AuthEvent.Type.NAVIGATE_HOME) {
                navigateHome();
            } else if (payload.getType() == AuthEvent.Type.SHOW_MESSAGE && payload.getMessage() != null) {
                toast(payload.getMessage());
            } else if (payload.getType() == AuthEvent.Type.RESET_EMAIL_DONE) {
                handleResetResult(payload);
            }
        });
    }

    private void doEmailLogin() {
        String email = binding.etEmailSignin != null && binding.etEmailSignin.getText() != null
                ? binding.etEmailSignin.getText().toString().trim()
                : "";
        String pass = binding.etPasswordSignin != null && binding.etPasswordSignin.getText() != null
                ? binding.etPasswordSignin.getText().toString()
                : "";
        viewModel.login(email, pass);
    }

    private void showResetPasswordDialog() {
        resetEmailTil = new TextInputLayout(requireContext());
        resetEmailTil.setHint("Email");
        TextInputEditText et = new TextInputEditText(requireContext());
        et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        if (binding.etEmailSignin != null && binding.etEmailSignin.getText() != null) {
            et.setText(binding.etEmailSignin.getText().toString());
        }
        resetEmailTil.addView(et);

        resetDialog = new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Recuperar contrase\u00f1a")
                .setMessage("Ingresa tu email para enviarte el enlace de recuperaci\u00f3n")
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
                    viewModel.resetPassword(email);
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
        Intent intent = new Intent(requireContext(), FragmentsHostActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    private void clearResetDialogRefs() {
        resetPositiveButton = null;
        resetEmailTil = null;
        resetDialog = null;
    }

    private void toast(@NonNull String s) {
        Toast.makeText(requireContext(), s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        clearResetDialogRefs();
        binding = null;
    }
}
