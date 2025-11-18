package com.courierexperts.demo.ui.fragments;

import android.content.Intent;
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

import com.courierexperts.demo.FragmentsHostActivity;
import com.courierexperts.demo.R;
import com.courierexperts.demo.databinding.ActivityWelcomeBinding;
import com.courierexperts.demo.ui.auth.AuthEvent;
import com.courierexperts.demo.ui.auth.AuthViewModel;

public class WelcomeFragment extends Fragment {

    private ActivityWelcomeBinding binding;
    private AuthViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ActivityWelcomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
        observeViewModel();
        setupClicks();
        viewModel.checkSession();
    }

    private void setupClicks() {
        binding.btnSignIn.setOnClickListener(v ->
                NavHostFragment.findNavController(this)
                        .navigate(R.id.action_welcomeFragment_to_signInFragment));
        binding.btnRegister.setOnClickListener(v ->
                NavHostFragment.findNavController(this)
                        .navigate(R.id.action_welcomeFragment_to_signUpStep1Fragment));
    }

    private void observeViewModel() {
        viewModel.getEvents().observe(getViewLifecycleOwner(), event -> {
            if (event == null) return;
            AuthEvent payload = event.getContentIfNotHandled();
            if (payload == null) return;
            if (payload.getType() == AuthEvent.Type.NAVIGATE_HOME) {
                navigateHome();
            } else if (payload.getType() == AuthEvent.Type.SHOW_MESSAGE && payload.getMessage() != null) {
                Toast.makeText(requireContext(), payload.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
