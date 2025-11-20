package com.courierexperts.demo.ui.fragments;

import android.Manifest;
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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.courierexperts.demo.R;
import com.courierexperts.demo.databinding.ActivitySignupStep1Binding;
import com.courierexperts.demo.ui.signup.SignUpStep1Event;
import com.courierexperts.demo.ui.signup.SignUpStep1ViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class SignUpStep1Fragment extends Fragment {

    private static final int REQ_LOCATION = 1001;

    private ActivitySignupStep1Binding binding;
    private SignUpStep1ViewModel viewModel;

    private FusedLocationProviderClient fusedLocationClient;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = ActivitySignupStep1Binding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(SignUpStep1ViewModel.class);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());

        observeViewModel();
        setupClicks();
    }

    private void setupClicks() {
        binding.btnBack.setOnClickListener(
                v -> NavHostFragment.findNavController(this).popBackStack()
        );

        binding.btnNext.setOnClickListener(v -> doNext());

        // Nuevo: botón para detectar provincia con GPS
        binding.btnDetectProvincia.setOnClickListener(v -> detectarProvincia());
    }

    private void observeViewModel() {
        viewModel.getEvents().observe(getViewLifecycleOwner(), event -> {
            if (event == null) return;
            SignUpStep1Event payload = event.getContentIfNotHandled();
            if (payload == null) return;

            if (payload.getType() == SignUpStep1Event.Type.SHOW_MESSAGE
                    && payload.getMessage() != null) {

                Toast.makeText(requireContext(),
                        payload.getMessage(),
                        Toast.LENGTH_SHORT).show();

            } else if (payload.getType() == SignUpStep1Event.Type.NAVIGATE_STEP2
                    && payload.getData() != null) {

                Bundle args = new Bundle();
                args.putParcelable(SignUpStep2Fragment.ARG_SIGNUP_DATA,
                        payload.getData());
                NavHostFragment.findNavController(this)
                        .navigate(R.id.action_signUpStep1Fragment_to_signUpStep2Fragment, args);
            }
        });
    }

    private void doNext() {
        String nombre = textOf(binding.etNombreSignup);
        String apellido = textOf(binding.etApellidoSignup);
        String dni = textOf(binding.etDniSignUp);
        String cuil = textOf(binding.etCuilSignUp);
        // Si más adelante quieren guardar la provincia:
        // String provincia = textOf(binding.etProvinciaSignup);

        viewModel.submitStepOne(nombre, apellido, dni, cuil);
    }

    private static String textOf(@Nullable TextInputEditText et) {
        if (et != null && et.getText() != null) {
            return et.getText().toString().trim();
        }
        return "";
    }

    // ==========================
    //   GPS + Geocoder
    // ==========================
    private void detectarProvincia() {
        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Pedimos permiso
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
                                "No se pudo obtener tu ubicación. Probá de nuevo.",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(
                        requireContext(),
                        "Error al obtener ubicación",
                        Toast.LENGTH_SHORT
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

                if (provincia != null && !provincia.isEmpty()) {
                    if (binding != null && binding.etProvinciaSignup != null) {
                        binding.etProvinciaSignup.setText(provincia);
                    }
                } else {
                    Toast.makeText(requireContext(),
                            "No se pudo detectar la provincia.",
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(requireContext(),
                        "No se encontró dirección para tu ubicación.",
                        Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Toast.makeText(requireContext(),
                    "Error al leer la dirección.",
                    Toast.LENGTH_SHORT).show();
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
                // Si el usuario aceptó, volvemos a intentar
                detectarProvincia();
            } else {
                Toast.makeText(requireContext(),
                        "Necesitamos el permiso de ubicación para detectar la provincia.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
