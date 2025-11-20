package com.courierexperts.demo.ui.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.content.Context;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.os.BundleCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.courierexperts.demo.FragmentsHostActivity;
import com.courierexperts.demo.R;
import com.courierexperts.demo.databinding.ActivitySignupStep2Binding;
import com.courierexperts.demo.ui.signup.SignUpData;
import com.courierexperts.demo.ui.signup.SignUpStep2Event;
import com.courierexperts.demo.ui.signup.SignUpStep2UiState;
import com.courierexperts.demo.ui.signup.SignUpStep2ViewModel;
import com.courierexperts.demo.util.CapitalizeTextWatcher;
import com.google.android.material.textfield.TextInputEditText;

public class SignUpStep2Fragment extends Fragment {

    public static final String ARG_SIGNUP_DATA = "arg_signup_data";

    private ActivitySignupStep2Binding binding;
    private SignUpStep2ViewModel viewModel;
    private SignUpData step1Data;
    private ActivityResultLauncher<String[]> locationPermissionLauncher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ActivitySignupStep2Binding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Registrar launcher de permisos
        locationPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {
                    Boolean fineLocation = result.get(Manifest.permission.ACCESS_FINE_LOCATION);
                    Boolean coarseLocation = result.get(Manifest.permission.ACCESS_COARSE_LOCATION);
                    
                    if (fineLocation != null && fineLocation) {
                        getLocationAndSetProvince();
                    } else {
                        Toast.makeText(requireContext(), "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show();
                    }
                }
        );
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
        binding.btnBack.setOnClickListener(v -> 
                NavHostFragment.findNavController(SignUpStep2Fragment.this).navigateUp());
        binding.btnSave.setOnClickListener(v -> doRegister());
        
        if (binding.btnUseLocation != null) {
            binding.btnUseLocation.setOnClickListener(v -> requestLocationAndSetProvince());
        }
        
        // Capitalizar primera letra de dirección
        if (binding.etDireccionSignup != null) {
            binding.etDireccionSignup.addTextChangedListener(new CapitalizeTextWatcher(binding.etDireccionSignup));
        }
    }
    
    private void observeViewModel() {
        viewModel.getUiState().observe(getViewLifecycleOwner(), state -> {
            boolean loading = state instanceof SignUpStep2UiState.Loading;
            if (binding.progressBar != null) {
                binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
            }
        });

        viewModel.getEvents().observe(getViewLifecycleOwner(), event -> {
            if (event == null) return;
            SignUpStep2Event payload = event.getContentIfNotHandled();
            if (payload == null) return;
            if (payload.getType() == SignUpStep2Event.Type.SHOW_MESSAGE && payload.getMessage() != null) {
                showErrorForMessage(payload.getMessage());
                Toast.makeText(requireContext(), payload.getMessage(), Toast.LENGTH_SHORT).show();
            } else if (payload.getType() == SignUpStep2Event.Type.NAVIGATE_HOME) {
                navigateHome();
            }
        });
    }

    private void clearAllErrors() {
        if (binding == null) return;
        if (binding.tilDireccionSignup != null) binding.tilDireccionSignup.setError(null);
        if (binding.tilTelefonoSignup != null) binding.tilTelefonoSignup.setError(null);
        if (binding.tilEmailSignup != null) binding.tilEmailSignup.setError(null);
        if (binding.tilPasswordSignup != null) binding.tilPasswordSignup.setError(null);
        if (binding.tilConfirmPasswordSignup != null) binding.tilConfirmPasswordSignup.setError(null);
    }
    
    private void showErrorForMessage(String message) {
        clearAllErrors();
        if (binding == null || message == null) return;
        
        // Convertir a minúsculas para comparación case-insensitive
        String msgLower = message.toLowerCase();
        
        // Identificar qué campo tiene error por el mensaje
        if (msgLower.contains("dirección") || msgLower.contains("direccion")) {
            if (binding.tilDireccionSignup != null) {
                binding.tilDireccionSignup.setError(" "); // Espacio para mostrar solo el indicador rojo
                binding.etDireccionSignup.requestFocus();
            }
        } else if (msgLower.contains("provincia")) {
            // Spinner no tiene setError, pero el mensaje lo indica
        } else if (msgLower.contains("teléfono") || msgLower.contains("telefono")) {
            if (binding.tilTelefonoSignup != null) {
                binding.tilTelefonoSignup.setError(" ");
                binding.etTelefonoSignup.requestFocus();
            }
        } else if (msgLower.contains("email") || msgLower.contains("correo") || msgLower.contains("mail")) {
            if (binding.tilEmailSignup != null) {
                binding.tilEmailSignup.setError(" ");
                binding.etEmailSignup.requestFocus();
            }
        } else if (msgLower.contains("contraseña")) {
            if (msgLower.contains("coincid") || msgLower.contains("confirmar")) {
                if (binding.tilConfirmPasswordSignup != null) {
                    binding.tilConfirmPasswordSignup.setError(" ");
                    binding.etConfirmPasswordSignup.requestFocus();
                }
            } else {
                if (binding.tilPasswordSignup != null) {
                    binding.tilPasswordSignup.setError(" ");
                    binding.etPasswordSignup.requestFocus();
                }
            }
        }
    }

    private void doRegister() {
        clearAllErrors();
        String direccion = textOf(binding.etDireccionSignup);
        String provincia = binding.spProvinciaSignup != null && binding.spProvinciaSignup.getSelectedItem() != null
                ? binding.spProvinciaSignup.getSelectedItem().toString()
                : "";
        
        // Combinar código de país + número de teléfono
        String countryCode = "";
        if (binding.spCountryCode != null && binding.spCountryCode.getSelectedItem() != null) {
            String selected = binding.spCountryCode.getSelectedItem().toString();
            // Extraer solo el código: "🇦🇷 +54" -> "+54"
            if (selected.contains("+")) {
                countryCode = selected.substring(selected.indexOf("+")).trim();
            }
        }
        String phoneNumber = textOf(binding.etTelefonoSignup);
        String telefono = countryCode + " " + phoneNumber;
        
        String email = textOf(binding.etEmailSignup);
        String password = textOf(binding.etPasswordSignup);
        String confirmPassword = textOf(binding.etConfirmPasswordSignup);
        viewModel.register(step1Data, direccion, provincia, telefono, email, password, confirmPassword);
    }

    private String textOf(@Nullable TextInputEditText et) {
        if (et != null && et.getText() != null) {
            return et.getText().toString();
        }
        return "";
    }

    private void requestLocationAndSetProvince() {
        // Verificar permisos
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Pedir permisos usando el launcher moderno
            locationPermissionLauncher.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
            return;
        }
        
        getLocationAndSetProvince();
    }
    
    private void getLocationAndSetProvince() {
        try {
            LocationManager locationManager = (LocationManager) requireContext()
                    .getSystemService(Context.LOCATION_SERVICE);
            
            if (locationManager == null) {
                Toast.makeText(requireContext(), "No se puede acceder a la ubicación", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Verificar permisos nuevamente
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            
            // Obtener última ubicación conocida
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location == null) {
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            
            if (location != null) {
                getProvinceFromLocation(location.getLatitude(), location.getLongitude());
            } else {
                Toast.makeText(requireContext(), "No se pudo obtener la ubicación", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Error al obtener ubicación", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void getProvinceFromLocation(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            geocoder.getFromLocation(latitude, longitude, 1, addresses -> {
                if (addresses != null && !addresses.isEmpty()) {
                    Address address = addresses.get(0);
                    String province = address.getAdminArea();
                    
                    if (province != null && binding != null && binding.spProvinciaSignup != null) {
                        requireActivity().runOnUiThread(() -> setProvinceInSpinner(province));
                    } else {
                        requireActivity().runOnUiThread(() -> 
                            Toast.makeText(requireContext(), "No se pudo determinar la provincia", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    requireActivity().runOnUiThread(() -> 
                        Toast.makeText(requireContext(), "No se pudo determinar la provincia", Toast.LENGTH_SHORT).show());
                }
            });
        } else {
            try {
                @SuppressWarnings("deprecation")
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                
                if (addresses != null && !addresses.isEmpty()) {
                    Address address = addresses.get(0);
                    String province = address.getAdminArea();
                    
                    if (province != null && binding.spProvinciaSignup != null) {
                        setProvinceInSpinner(province);
                    } else {
                        Toast.makeText(requireContext(), "No se pudo determinar la provincia", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireContext(), "No se pudo determinar la provincia", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                Toast.makeText(requireContext(), "Error al obtener la dirección", Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    private void setProvinceInSpinner(String provinceName) {
        if (binding.spProvinciaSignup == null) return;
        
        // Buscar la provincia en el spinner
        for (int i = 0; i < binding.spProvinciaSignup.getCount(); i++) {
            String item = binding.spProvinciaSignup.getItemAtPosition(i).toString();
            if (item.equalsIgnoreCase(provinceName) || 
                item.toLowerCase().contains(provinceName.toLowerCase())) {
                binding.spProvinciaSignup.setSelection(i);
                Toast.makeText(requireContext(), "Provincia detectada: " + item, Toast.LENGTH_SHORT).show();
                return;
            }
        }
        
        Toast.makeText(requireContext(), "Provincia detectada: " + provinceName, Toast.LENGTH_SHORT).show();
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
