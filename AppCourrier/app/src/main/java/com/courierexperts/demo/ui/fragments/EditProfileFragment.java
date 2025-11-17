package com.courierexperts.demo.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.courierexperts.demo.databinding.PerfilDatosActivityBinding;
import com.courierexperts.demo.ui.profile.EditProfileUiState;
import com.courierexperts.demo.ui.profile.EditProfileViewModel;

public class EditProfileFragment extends Fragment {

    private PerfilDatosActivityBinding binding;
    private EditProfileViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = PerfilDatosActivityBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(EditProfileViewModel.class);

        if (binding.bottomNav != null) {
            binding.bottomNav.setVisibility(View.GONE);
        }

        binding.btnCancelarPerfil.setOnClickListener(v ->
                NavHostFragment.findNavController(EditProfileFragment.this).popBackStack());
        binding.btnGuardardatosPerfil.setOnClickListener(v -> onSave());

        observeUiState();
    }

    private void observeUiState() {
        viewModel.getUiState().observe(getViewLifecycleOwner(), state -> {
            binding.progressBar.setVisibility(View.GONE);
            binding.tvStateMessage.setVisibility(View.GONE);
            binding.scrollContent.setVisibility(View.GONE);

            if (state instanceof EditProfileUiState.Loading) {
                binding.progressBar.setVisibility(View.VISIBLE);
            } else if (state instanceof EditProfileUiState.Success) {
                binding.scrollContent.setVisibility(View.VISIBLE);
                render(((EditProfileUiState.Success) state));
            } else if (state instanceof EditProfileUiState.Error) {
                binding.tvStateMessage.setVisibility(View.VISIBLE);
                binding.tvStateMessage.setText(((EditProfileUiState.Error) state).getMessage());
                Toast.makeText(requireContext(), ((EditProfileUiState.Error) state).getMessage(), Toast.LENGTH_LONG).show();
            } else if (state instanceof EditProfileUiState.Saved) {
                Toast.makeText(requireContext(), "Datos guardados", Toast.LENGTH_SHORT).show();
                NavHostFragment.findNavController(EditProfileFragment.this).popBackStack();
            }
        });
    }

    private void render(EditProfileUiState.Success state) {
        binding.scrollContent.setVisibility(View.VISIBLE);
        binding.etNombrePerfil.setText(state.getProfile().name);
        EditText lastNameInput = binding.tilApellidoPerfil.getEditText();
        if (lastNameInput != null) {
            lastNameInput.setText(state.getProfile().lastName);
        }
        binding.etMailPerfil.setText(state.getProfile().email);
        binding.etTelefonoPerfil.setText(state.getProfile().phone);
        binding.etDireccionPerfil.setText(state.getProfile().address);
        // TODO: set deposit selector
    }

    private void onSave() {
        String name = textOf(binding.etNombrePerfil);
        EditText lastNameInput = binding.tilApellidoPerfil.getEditText();
        String lastName = textOf(lastNameInput);
        String phone = textOf(binding.etTelefonoPerfil);
        String address = textOf(binding.etDireccionPerfil);
        String email = textOf(binding.etMailPerfil);
        Long depositId = null; // TODO: obtener de selector

        viewModel.save(name, lastName, phone, address, email, depositId);
    }

    private String textOf(@Nullable EditText input) {
        if (input != null && input.getText() != null) {
            return input.getText().toString().trim();
        }
        return "";
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
