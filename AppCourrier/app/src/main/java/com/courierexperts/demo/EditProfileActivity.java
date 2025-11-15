package com.courierexperts.demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.courierexperts.demo.databinding.PerfilDatosActivityBinding;
import com.courierexperts.demo.ui.profile.EditProfileUiState;
import com.courierexperts.demo.ui.profile.EditProfileViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputLayout;

public class EditProfileActivity extends AppCompatActivity {

    private PerfilDatosActivityBinding binding;
    private EditProfileViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = PerfilDatosActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(EditProfileViewModel.class);

        binding.btnCancelarPerfil.setOnClickListener(v -> finish());
        binding.btnGuardardatosPerfil.setOnClickListener(v -> onSave());

        observeUiState();
        setupBottomBar();
    }

    private void observeUiState() {
        viewModel.getUiState().observe(this, state -> {
            binding.progressBar.setVisibility(android.view.View.GONE);
            binding.tvStateMessage.setVisibility(android.view.View.GONE);
            binding.scrollContent.setVisibility(android.view.View.GONE);

            if (state instanceof EditProfileUiState.Loading) {
                binding.progressBar.setVisibility(android.view.View.VISIBLE);
            } else if (state instanceof EditProfileUiState.Success) {
                binding.scrollContent.setVisibility(android.view.View.VISIBLE);
                bindForm(((EditProfileUiState.Success) state));
            } else if (state instanceof EditProfileUiState.Error) {
                binding.tvStateMessage.setVisibility(android.view.View.VISIBLE);
                binding.tvStateMessage.setText(((EditProfileUiState.Error) state).getMessage());
            } else if (state instanceof EditProfileUiState.Saved) {
                finish();
            }
        });
    }

    private void bindForm(EditProfileUiState.Success state) {
        com.courierexperts.demo.data.local.entity.UserProfileEntity profile = state.getProfile();
        binding.etNombrePerfil.setText(profile.name != null ? profile.name : "");
        binding.etMailPerfil.setText(profile.email != null ? profile.email : "");
        binding.etTelefonoPerfil.setText(profile.phone != null ? profile.phone : "");
        binding.etDireccionPerfil.setText(profile.address != null ? profile.address : "");

        android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        for (com.courierexperts.demo.data.local.entity.DepositEntity d : state.getDeposits()) {
            adapter.add(d.name);
        }
        binding.spinnerDeposito.setAdapter(adapter);

        Long selected = profile.depositId != null ? profile.depositId : viewModel.getSavedDepositId();
        if (selected != null) {
            for (int i = 0; i < state.getDeposits().size(); i++) {
                if (state.getDeposits().get(i).id == selected) {
                    binding.spinnerDeposito.setSelection(i);
                    break;
                }
            }
        }
    }

    private void onSave() {
        TextInputLayout tilNombre = (TextInputLayout) binding.etNombrePerfil.getParent().getParent();
        TextInputLayout tilMail = (TextInputLayout) binding.etMailPerfil.getParent().getParent();
        TextInputLayout tilTelefono = (TextInputLayout) binding.etTelefonoPerfil.getParent().getParent();
        TextInputLayout tilDireccion = (TextInputLayout) binding.etDireccionPerfil.getParent().getParent();

        String nombre = binding.etNombrePerfil.getText() != null ? binding.etNombrePerfil.getText().toString().trim() : "";
        String mail = binding.etMailPerfil.getText() != null ? binding.etMailPerfil.getText().toString().trim() : "";
        String telefono = binding.etTelefonoPerfil.getText() != null ? binding.etTelefonoPerfil.getText().toString().trim() : "";
        String direccion = binding.etDireccionPerfil.getText() != null ? binding.etDireccionPerfil.getText().toString().trim() : "";
        Long depositId = null;
        EditProfileUiState current = viewModel.getUiState().getValue();
        if (current instanceof EditProfileUiState.Success) {
            int pos = binding.spinnerDeposito.getSelectedItemPosition();
            if (pos >= 0 && pos < ((EditProfileUiState.Success) current).getDeposits().size()) {
                depositId = ((EditProfileUiState.Success) current).getDeposits().get(pos).id;
            }
        }

        tilNombre.setError(null);
        tilMail.setError(null);
        tilTelefono.setError(null);
        tilDireccion.setError(null);

        viewModel.save(
                nombre,
                getLastNameFromState(),
                telefono,
                direccion,
                mail,
                depositId
        );
    }

    private String getLastNameFromState() {
        EditProfileUiState state = viewModel.getUiState().getValue();
        if (state instanceof EditProfileUiState.Success) {
            return ((EditProfileUiState.Success) state).getProfile().lastName != null
                    ? ((EditProfileUiState.Success) state).getProfile().lastName : "";
        }
        return "";
    }

    private void setupBottomBar() {
        BottomNavigationView bottom = binding.bottomNav;
        if (bottom == null) return;

        bottom.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    startActivity(new Intent(EditProfileActivity.this, HomeActivity.class));
                    return true;
                } else if (id == R.id.nav_add) {
                    startActivity(new Intent(EditProfileActivity.this, NewPurchaseActivity.class));
                    return true;
                } else if (id == R.id.nav_profile) {
                    startActivity(new Intent(EditProfileActivity.this, ProfileActivity.class));
                    return true;
                }
                return false;
            }
        });
    }
}
