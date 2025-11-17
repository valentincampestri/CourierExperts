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
import com.courierexperts.demo.WelcomeActivity;
import com.courierexperts.demo.databinding.PerfilActivityBinding;
import com.courierexperts.demo.ui.profile.ProfileUiState;
import com.courierexperts.demo.ui.profile.ProfileViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class ProfileFragment extends Fragment {

    private PerfilActivityBinding binding;
    private ProfileViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = PerfilActivityBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        if (binding.bottomNav != null) {
            binding.bottomNav.setVisibility(View.GONE);
        }

        observeState();
        observeLogout();
        setupClicks();
    }

    private void observeState() {
        viewModel.getUiState().observe(getViewLifecycleOwner(), state -> {
            binding.progressBar.setVisibility(View.GONE);
            binding.tvStateMessage.setVisibility(View.GONE);

            if (state instanceof ProfileUiState.Loading) {
                binding.progressBar.setVisibility(View.VISIBLE);
            } else if (state instanceof ProfileUiState.Success) {
                renderProfile(((ProfileUiState.Success) state).getProfile());
            } else if (state instanceof ProfileUiState.Error) {
                binding.tvStateMessage.setVisibility(View.VISIBLE);
                binding.tvStateMessage.setText(R.string.state_error_retry);
                Toast.makeText(requireContext(), R.string.state_error_retry, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void observeLogout() {
        viewModel.getLogoutEvents().observe(getViewLifecycleOwner(), success -> {
            if (Boolean.TRUE.equals(success)) {
                android.content.Intent intent = new android.content.Intent(requireContext(), WelcomeActivity.class)
                        .addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
                                | android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                                | android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                requireActivity().finish();
            }
        });
    }

    private void setupClicks() {
        binding.cardCuenta.setOnClickListener(v -> navigateToEditProfile());
        binding.direccion.setOnClickListener(v -> navigateToEditProfile());
        binding.telefono.setOnClickListener(v -> navigateToEditProfile());
        binding.cardLogout.setOnClickListener(v -> showLogoutDialog());

        binding.switchNotificaciones.setOnCheckedChangeListener((buttonView, isChecked) ->
                viewModel.updateNotifications(isChecked));
    }

    private void navigateToEditProfile() {
        NavHostFragment.findNavController(this)
                .navigate(R.id.action_nav_profile_to_editProfileFragment);
    }

    private void showLogoutDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Cerrar sesión")
                .setMessage("¿Seguro que querés cerrar sesión?")
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Sí, cerrar sesión", (d, w) -> viewModel.logout())
                .show();
    }

    private void renderProfile(com.courierexperts.demo.data.local.entity.UserProfileEntity profile) {
        if (profile == null) return;
        String name = profile.name != null ? profile.name.trim() : "";
        binding.tvSaludo.setText(name.isEmpty() ? getString(R.string.home_greeting_generic) : ("Hola, " + name));
        binding.tvMail.setText(profile.email != null ? profile.email.trim() : "");
        binding.tvDireccion.setText(profile.address != null && !profile.address.trim().isEmpty()
                ? profile.address.trim()
                : getString(R.string.profile_placeholder_address));
        binding.tvTelefono.setText(profile.phone != null && !profile.phone.trim().isEmpty()
                ? profile.phone.trim()
                : getString(R.string.profile_placeholder_phone));

        boolean enabled = profile.notificationsEnabled != null && profile.notificationsEnabled;
        if (binding.switchNotificaciones.isChecked() != enabled) {
            binding.switchNotificaciones.setChecked(enabled);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
