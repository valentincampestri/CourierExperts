package com.courierexperts.demo.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.os.BundleCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.courierexperts.demo.FragmentsHostActivity;
import com.courierexperts.demo.R;
import com.courierexperts.demo.databinding.ActivitySignupStep2Binding;
import com.courierexperts.demo.ui.signup.SignUpData;
import com.courierexperts.demo.ui.signup.SignUpStep2Event;
import com.courierexperts.demo.ui.signup.SignUpStep2UiState;
import com.courierexperts.demo.ui.signup.SignUpStep2ViewModel;
import com.google.android.material.textfield.TextInputEditText;

public class SignUpStep2Fragment extends Fragment {

    public static final String ARG_SIGNUP_DATA = "arg_signup_data";

    private ActivitySignupStep2Binding binding;
    private SignUpStep2ViewModel viewModel;
    private SignUpData step1Data;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ActivitySignupStep2Binding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        step1Data = BundleCompat.getParcelable(requireArguments(), ARG_SIGNUP_DATA, SignUpData.class);
        if (step1Data == null) {
            Toast.makeText(requireContext(), R.string.signup_missing_step, Toast.LENGTH_LONG).show();
            NavHostFragment.findNavController(this).popBackStack();
            return;
        }
        viewModel = new ViewModelProvider(this).get(SignUpStep2ViewModel.class);
        observeViewModel();
        setupClicks();
    }

    private void setupClicks() {
        binding.btnBack.setOnClickListener(v -> NavHostFragment.findNavController(this).popBackStack());
        binding.btnSave.setOnClickListener(v -> doRegister());
    }

    private void observeViewModel() {
        viewModel.getUiState().observe(getViewLifecycleOwner(), state -> {
            boolean loading = state instanceof SignUpStep2UiState.Loading;
            binding.btnSave.setEnabled(!loading);
            if (binding.progressBar != null) {
                binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
            }
        });

        viewModel.getEvents().observe(getViewLifecycleOwner(), event -> {
            if (event == null) return;
            SignUpStep2Event payload = event.getContentIfNotHandled();
            if (payload == null) return;
            if (payload.getType() == SignUpStep2Event.Type.SHOW_MESSAGE && payload.getMessage() != null) {
                Toast.makeText(requireContext(), payload.getMessage(), Toast.LENGTH_SHORT).show();
            } else if (payload.getType() == SignUpStep2Event.Type.NAVIGATE_HOME) {
                navigateHome();
            }
        });
    }

    private void doRegister() {
        String direccion = textOf(binding.etDireccionSignup);
        String provincia = binding.spProvinciaSignup != null && binding.spProvinciaSignup.getSelectedItem() != null
                ? binding.spProvinciaSignup.getSelectedItem().toString()
                : "";
        String telefono = textOf(binding.etTelefonoSignup);
        String email = textOf(binding.etEmailSignup);
        String password = textOf(binding.etPasswordSignup);
        viewModel.register(step1Data, direccion, provincia, telefono, email, password);
    }

    private String textOf(@Nullable TextInputEditText et) {
        if (et != null && et.getText() != null) {
            return et.getText().toString();
        }
        return "";
    }

    private void navigateHome() {
        Intent intent = new Intent(requireContext(), FragmentsHostActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
