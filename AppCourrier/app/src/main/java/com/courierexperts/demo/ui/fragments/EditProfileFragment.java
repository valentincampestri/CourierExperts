package com.courierexperts.demo.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.EditText;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.courierexperts.demo.databinding.PerfilDatosActivityBinding;
import com.courierexperts.demo.ui.profile.EditProfileUiState;
import com.courierexperts.demo.ui.profile.EditProfileViewModel;
import com.courierexperts.demo.data.local.entity.DepositEntity;
import com.courierexperts.demo.util.CapitalizeTextWatcher;

import java.util.ArrayList;
import java.util.List;

public class EditProfileFragment extends Fragment {

    private PerfilDatosActivityBinding binding;
    private EditProfileViewModel viewModel;
    private ArrayAdapter<String> depositAdapter;
    private final List<DepositEntity> depositOptions = new ArrayList<>();
    private Long selectedDepositId;
    private boolean suppressSpinnerCallback = false;
    private boolean namesNormalized = false;

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
        setupDepositSpinner();
        setupCapitalizeWatchers();

        observeUiState();
    }

    private void setupCapitalizeWatchers() {
        
        if (binding.etNombrePerfil != null) {
            binding.etNombrePerfil.addTextChangedListener(new CapitalizeTextWatcher(binding.etNombrePerfil));
        }
        
        EditText apellidoInput = binding.tilApellidoPerfil.getEditText();
        if (apellidoInput != null) {
            apellidoInput.addTextChangedListener(new CapitalizeTextWatcher(apellidoInput));
        }
        
        if (binding.etDireccionPerfil != null) {
            binding.etDireccionPerfil.addTextChangedListener(new CapitalizeTextWatcher(binding.etDireccionPerfil));
        }
    }
    
    private void setupDepositSpinner() {
        depositAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, new ArrayList<>());
        depositAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerDeposito.setAdapter(depositAdapter);
        binding.spinnerDeposito.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (suppressSpinnerCallback) return;
                if (position >= 0 && position < depositOptions.size()) {
                    selectedDepositId = depositOptions.get(position).id;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                
            }
        });
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
        String rawName = safe(state.getProfile().name);
        String rawLastName = safe(state.getProfile().lastName);
        String firstName = rawName;
        String lastName = rawLastName;
        if (TextUtils.isEmpty(rawLastName) && rawName.contains(" ")) {
            int idx = rawName.lastIndexOf(' ');
            firstName = rawName.substring(0, idx).trim();
            lastName = rawName.substring(idx + 1).trim();
        } else if (TextUtils.isEmpty(rawLastName) && !rawName.isEmpty()) {
            lastName = rawName;
            firstName = "";
        }
        if (!namesNormalized && !TextUtils.isEmpty(firstName) && !TextUtils.isEmpty(lastName)
                && (TextUtils.isEmpty(rawLastName) || rawName.contains(" "))) {
            namesNormalized = true;
            viewModel.normalizeNamesIfNeeded(firstName, lastName);
        }

        binding.etNombrePerfil.setText(firstName);
        EditText lastNameInput = binding.tilApellidoPerfil.getEditText();
        if (lastNameInput != null) {
            lastNameInput.setText(lastName);
        }
        binding.etMailPerfil.setText(state.getProfile().email);
        
        
        String fullPhone = state.getProfile().phone;
        if (fullPhone != null && fullPhone.contains("+")) {
            
            int spaceIndex = fullPhone.indexOf(" ");
            if (spaceIndex > 0) {
                String code = fullPhone.substring(0, spaceIndex).trim(); 
                String number = fullPhone.substring(spaceIndex + 1).trim(); 
                
                
                if (binding.spCountryCodePerfil != null) {
                    for (int i = 0; i < binding.spCountryCodePerfil.getCount(); i++) {
                        String item = binding.spCountryCodePerfil.getItemAtPosition(i).toString();
                        if (item.contains(code)) {
                            binding.spCountryCodePerfil.setSelection(i);
                            break;
                        }
                    }
                }
                binding.etTelefonoPerfil.setText(number);
            } else {
                binding.etTelefonoPerfil.setText(fullPhone);
            }
        } else {
            binding.etTelefonoPerfil.setText(fullPhone);
        }
        
        binding.etDireccionPerfil.setText(state.getProfile().address);
        populateDepositSpinner(state);
    }

    private void populateDepositSpinner(EditProfileUiState.Success state) {
        List<DepositEntity> deposits = state.getDeposits();
        depositOptions.clear();
        if (deposits != null) {
            depositOptions.addAll(deposits);
        }
        List<String> names = new ArrayList<>();
        for (DepositEntity dep : depositOptions) {
            names.add(dep.name != null ? dep.name : "");
        }
        depositAdapter.clear();
        depositAdapter.addAll(names);
        depositAdapter.notifyDataSetChanged();

        Long targetId = state.getProfile().depositId;
        if (targetId == null) {
            targetId = viewModel.getSavedDepositId();
        }
        if (targetId == null && !depositOptions.isEmpty()) {
            targetId = depositOptions.get(0).id;
        }
        selectedDepositId = targetId;

        if (targetId != null) {
            int index = -1;
            for (int i = 0; i < depositOptions.size(); i++) {
                if (targetId.equals(depositOptions.get(i).id)) {
                    index = i;
                    break;
                }
            }
            if (index >= 0) {
                suppressSpinnerCallback = true;
                binding.spinnerDeposito.setSelection(index, false);
                suppressSpinnerCallback = false;
            }
        }
        binding.spinnerDeposito.setEnabled(!depositOptions.isEmpty());
    }

    private void onSave() {
        String name = textOf(binding.etNombrePerfil);
        EditText lastNameInput = binding.tilApellidoPerfil.getEditText();
        String lastName = textOf(lastNameInput);
        
        
        String countryCode = "";
        if (binding.spCountryCodePerfil != null && binding.spCountryCodePerfil.getSelectedItem() != null) {
            String selected = binding.spCountryCodePerfil.getSelectedItem().toString();
            
            if (selected.contains("+")) {
                countryCode = selected.substring(selected.indexOf("+")).trim();
            }
        }
        String phoneNumber = textOf(binding.etTelefonoPerfil);
        String phone = countryCode + " " + phoneNumber;
        
        String address = textOf(binding.etDireccionPerfil);
        String email = textOf(binding.etMailPerfil);
        Long depositId = selectedDepositId;

        viewModel.save(name, lastName, phone, address, email, depositId);
    }

    private String textOf(@Nullable EditText input) {
        if (input != null && input.getText() != null) {
            return input.getText().toString().trim();
        }
        return "";
    }

    private String safe(String input) {
        return input != null ? input.trim() : "";
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        namesNormalized = false;
    }
}
