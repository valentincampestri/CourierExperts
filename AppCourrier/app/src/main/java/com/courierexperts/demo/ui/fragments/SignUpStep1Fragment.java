package com.courierexperts.demo.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.courierexperts.demo.R;
import com.courierexperts.demo.databinding.ActivitySignupStep1Binding;
import com.courierexperts.demo.ui.signup.SignUpStep1Event;
import com.courierexperts.demo.ui.signup.SignUpStep1ViewModel;
import com.courierexperts.demo.util.CUILTextWatcher;
import com.courierexperts.demo.util.CapitalizeTextWatcher;

public class SignUpStep1Fragment extends Fragment {

    private ActivitySignupStep1Binding binding;
    private SignUpStep1ViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ActivitySignupStep1Binding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(SignUpStep1ViewModel.class);
        observeViewModel();
        setupClicks();
        setupFormatters();
    }

    private void setupFormatters() {
        // Formato automático de CUIL: 20303030301 → 20-30303030-1
        binding.etCuilSignUp.addTextChangedListener(new CUILTextWatcher());
        
        // Capitalizar primera letra de nombre y apellido
        binding.etNombreSignup.addTextChangedListener(new CapitalizeTextWatcher(binding.etNombreSignup));
        binding.etApellidoSignup.addTextChangedListener(new CapitalizeTextWatcher(binding.etApellidoSignup));
    }

    private void setupClicks() {
        binding.btnBack.setOnClickListener(v -> NavHostFragment.findNavController(this).popBackStack());
        binding.btnNext.setOnClickListener(v -> doNext());
    }

    private void observeViewModel() {
        viewModel.getEvents().observe(getViewLifecycleOwner(), event -> {
            if (event == null) return;
            SignUpStep1Event payload = event.getContentIfNotHandled();
            if (payload == null) return;
            if (payload.getType() == SignUpStep1Event.Type.SHOW_MESSAGE && payload.getMessage() != null) {
                showErrorForMessage(payload.getMessage());
                Toast.makeText(requireContext(), payload.getMessage(), Toast.LENGTH_SHORT).show();
            } else if (payload.getType() == SignUpStep1Event.Type.NAVIGATE_STEP2 && payload.getData() != null) {
                Bundle args = new Bundle();
                args.putParcelable(SignUpStep2Fragment.ARG_SIGNUP_DATA, payload.getData());
                NavHostFragment.findNavController(this)
                        .navigate(R.id.action_signUpStep1Fragment_to_signUpStep2Fragment, args);
            }
        });
    }
    
    private void clearAllErrors() {
        if (binding == null) return;
        if (binding.tilNombreSignup != null) binding.tilNombreSignup.setError(null);
        if (binding.tilApellidoSignup != null) binding.tilApellidoSignup.setError(null);
        if (binding.tilDniSignup != null) binding.tilDniSignup.setError(null);
        if (binding.tilCuilSignup != null) binding.tilCuilSignup.setError(null);
    }
    
    private void showErrorForMessage(String message) {
        clearAllErrors();
        if (binding == null || message == null) return;
        
        String msgLower = message.toLowerCase();
        
        if (msgLower.contains("nombre")) {
            if (binding.tilNombreSignup != null) {
                binding.tilNombreSignup.setError(" ");
                binding.etNombreSignup.requestFocus();
            }
        } else if (msgLower.contains("apellido")) {
            if (binding.tilApellidoSignup != null) {
                binding.tilApellidoSignup.setError(" ");
                binding.etApellidoSignup.requestFocus();
            }
        } else if (msgLower.contains("dni")) {
            if (binding.tilDniSignup != null) {
                binding.tilDniSignup.setError(" ");
                binding.etDniSignUp.requestFocus();
            }
        } else if (msgLower.contains("cuil") || msgLower.contains("cuit")) {
            if (binding.tilCuilSignup != null) {
                binding.tilCuilSignup.setError(" ");
                binding.etCuilSignUp.requestFocus();
            }
        }
    }

    private void doNext() {
        clearAllErrors();
        String nombre = textOf(binding.etNombreSignup);
        String apellido = textOf(binding.etApellidoSignup);
        String dni = textOf(binding.etDniSignUp);
        String cuil = textOf(binding.etCuilSignUp);
        viewModel.submitStepOne(nombre, apellido, dni, cuil);
    }

    private static String textOf(@Nullable com.google.android.material.textfield.TextInputEditText et) {
        if (et != null && et.getText() != null) {
            return et.getText().toString();
        }
        return "";
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
