package com.courierexperts.demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SignUpStep1Activity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_step1);

        View btnBack = findViewById(R.id.btnBack);
        View btnNext = findViewById(R.id.btnNext);
        btnBack.setOnClickListener(v -> finish());
        btnNext.setOnClickListener(v -> {
            com.google.android.material.textfield.TextInputEditText etNombre = findViewById(R.id.etNombreSignup);
            com.google.android.material.textfield.TextInputEditText etApellido = findViewById(R.id.etApellidoSignup);
            com.google.android.material.textfield.TextInputEditText etDni = findViewById(R.id.etDniSignUp);
            com.google.android.material.textfield.TextInputEditText etCuil = findViewById(R.id.etCuilSignUp);

            String nombre   = etNombre   != null && etNombre.getText()!=null   ? etNombre.getText().toString().trim()   : "";
            String apellido = etApellido != null && etApellido.getText()!=null ? etApellido.getText().toString().trim() : "";
            String dni      = etDni      != null && etDni.getText()!=null      ? etDni.getText().toString().trim()      : "";
            String cuil     = etCuil     != null && etCuil.getText()!=null     ? etCuil.getText().toString().trim()     : "";

            if (nombre.length() < 2 || nombre.length() > 50) { toast("Ingrese su nombre por favor de 2 a 50 caracteres"); return; }
            if (apellido.length() < 2 || apellido.length() > 50) { toast("Ingrese su apellido por favor de 2 a 50 caracteres"); return; }
            if (!dni.matches("\\d{7,10}")) { toast("DNI 7 a 10 dígitos"); return; }
            if (!cuil.matches("\\d{2}-\\d{8}-\\d")) { toast("CUIL formato NN-NNNNNNNN-N"); return; }

            Intent i = new Intent(SignUpStep1Activity.this, SignUpStep2Activity.class);
            i.putExtra("signup_nombre", nombre);
            i.putExtra("signup_apellido", apellido);
            i.putExtra("signup_dni", dni);
            i.putExtra("signup_cuil", cuil);
            startActivity(i);
        });
    }
    private void toast(String s) { android.widget.Toast.makeText(this, s, android.widget.Toast.LENGTH_SHORT).show(); }
}
