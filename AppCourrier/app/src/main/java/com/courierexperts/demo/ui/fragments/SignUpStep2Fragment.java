package com.courierexperts.demo.ui.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
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
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class SignUpStep2Fragment extends Fragment {

    public static final String ARG_SIGNUP_DATA = "arg_signup_data";
    private static final int REQ_LOCATION = 2001;

    private ActivitySignupStep2Binding binding;
    private SignUpStep2ViewModel viewModel;
    private SignUpData step1Data;

    private FusedLocationProviderClient fusedLocationClient;

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
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());

        observeViewModel();
        setupClicks();
    }

    private void setupClicks() {
        binding.btnBack.setOnClickListener(v -> NavHostFragment.findNavController(this).popBackStack());

        binding.btnSave.setOnClickListener(v -> doRegister());

        binding.btnDetectProvincia.setOnClickListener(v -> detectarProvincia());
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

    // ==========================
    //   GPS + Geocoder -> Spinner
    // ==========================
    private void detectarProvincia() {
        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQ_LOCATION
            );
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        completarProvinciaConLocation(location);
                    } else {
                        Toast.makeText(requireContext(),
                                "No se pudo obtener tu ubicación. Completá la provincia manualmente.",
                                Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(
                        requireContext(),
                        "Error al obtener ubicación. Completá la provincia manualmente.",
                        Toast.LENGTH_LONG
                ).show());
    }

    private void completarProvinciaConLocation(@NonNull Location location) {
        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
        try {
            List<Address> results =
                    geocoder.getFromLocation(location.getLatitude(),
                            location.getLongitude(),
                            1);

            if (results != null && !results.isEmpty()) {
                Address addr = results.get(0);
                String provincia = addr.getAdminArea();
                if (provincia == null || provincia.isEmpty()) {
                    provincia = addr.getSubAdminArea();
                }

                if (provincia != null && !provincia.isEmpty() && binding.spProvinciaSignup != null) {
                    String[] provincias = getResources().getStringArray(R.array.provincias_ar);
                    int index = -1;
                    String provinciaLower = provincia.toLowerCase(Locale.ROOT);

                    for (int i = 0; i < provincias.length; i++) {
                        String p = provincias[i].toLowerCase(Locale.ROOT);
                        if (p.equals(provinciaLower) || p.contains(provinciaLower) || provinciaLower.contains(p)) {
                            index = i;
                            break;
                        }
                    }

                    if (index >= 0) {
                        binding.spProvinciaSignup.setSelection(index);
                        Toast.makeText(requireContext(),
                                "Provincia detectada: " + provincias[index],
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(),
                                "No se pudo asociar \"" + provincia + "\" a la lista de provincias.",
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(requireContext(),
                            "No se pudo detectar la provincia.",
                            Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(requireContext(),
                        "No se encontró dirección para tu ubicación.",
                        Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            Toast.makeText(requireContext(),
                    "Error al leer la dirección.",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQ_LOCATION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                detectarProvincia();
            } else {
                Toast.makeText(requireContext(),
                        "No pudimos usar tu ubicación. Elegí la provincia del listado.",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
