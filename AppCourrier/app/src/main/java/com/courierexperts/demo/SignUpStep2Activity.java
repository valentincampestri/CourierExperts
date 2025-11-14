package com.courierexperts.demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignUpStep2Activity extends AppCompatActivity {
    private FirebaseAuth auth;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_step2);

        View btnBack = findViewById(R.id.btnBack);
        View btnSave = findViewById(R.id.btnSave);
        btnBack.setOnClickListener(v -> finish());
        auth = FirebaseAuth.getInstance();
        // Wire the Save button to trigger registration
        if (btnSave != null) {
            btnSave.setOnClickListener(v -> doRegister());
        }
    }

    private void doRegister() {
        // Datos del paso 1
        String nombre = getIntent().getStringExtra("signup_nombre");
        String apellido = getIntent().getStringExtra("signup_apellido");
        String dni = getIntent().getStringExtra("signup_dni");
        String cuil = getIntent().getStringExtra("signup_cuil");

        // Campos del paso 2
        com.google.android.material.textfield.TextInputEditText etDireccion = findViewById(R.id.etDireccionSignup);
        android.widget.Spinner spProvincia = findViewById(R.id.spProvinciaSignup);
        com.google.android.material.textfield.TextInputEditText etTelefono = findViewById(R.id.etTelefonoSignup);
        com.google.android.material.textfield.TextInputEditText etEmail = findViewById(R.id.etEmailSignup);
        com.google.android.material.textfield.TextInputEditText etPass = findViewById(R.id.etPasswordSignup);
        String direccion = etDireccion != null && etDireccion.getText()!=null ? etDireccion.getText().toString().trim(): "";
        String provincia = (spProvincia != null && spProvincia.getSelectedItem()!=null) ? spProvincia.getSelectedItem().toString().trim() : "";
        String pais      = "Argentina";
        String telefono  = etTelefono  != null && etTelefono.getText()!=null  ? etTelefono.getText().toString().trim()  : "";
        String email     = etEmail     != null && etEmail.getText()!=null     ? etEmail.getText().toString().trim()     : "";
        String pass      = etPass      != null && etPass.getText()!=null      ? etPass.getText().toString()              : "";
        if (direccion.length() < 2 || direccion.length() > 50) { toast("Dirección 2 a 50"); return; }
        if ("Seleccionar".equalsIgnoreCase(provincia) || provincia.length() < 2 || provincia.length() > 50) { toast("Seleccioná una provincia"); return; }
        if (!telefono.matches("[+0-9]{6,20}")) { toast("Teléfono inválido (usar + y dígitos, 6 a 20)"); return; }
        
        // Email y contraseña
        
        
        
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) { toast("Email inválido"); return; }
        if (pass.length() < 4 || pass.length() > 20) { toast("Contraseña 4 a 20 caracteres"); return; }
        findViewById(R.id.btnSave).setEnabled(false);
        auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(this, t -> {
            findViewById(R.id.btnSave).setEnabled(true);
            if (t.isSuccessful()) {
                // Set displayName
                if (auth.getCurrentUser() != null) {
                    auth.getCurrentUser().updateProfile(new com.google.firebase.auth.UserProfileChangeRequest.Builder().setDisplayName(nombre != null ? nombre : "").build());
                }
                // Persistir Firestore completo
                if (auth.getCurrentUser() != null) {
                    String uid = auth.getCurrentUser().getUid();
                    java.util.Map<String,Object> map = new java.util.HashMap<>();
                    map.put("name", nombre);
                    map.put("lastName", apellido);
                    map.put("dni", dni);
                    map.put("cuil", cuil);
                    map.put("address", direccion);
                    map.put("province", provincia);
                    map.put("country", pais);
                    map.put("email", email);
                    map.put("phone", telefono);
                    map.put("updatedAt", new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", java.util.Locale.US){ { setTimeZone(java.util.TimeZone.getTimeZone("UTC")); } }.format(new java.util.Date()));
                    com.google.firebase.firestore.FirebaseFirestore.getInstance().collection("users").document(uid).set(map);
                }
                // Persistir Room por uid
                new com.courierexperts.demo.data.repository.UserProfileRepository(this)
                        .saveAllSignupProfile(nombre, apellido, dni, cuil, direccion, provincia, pais, email, telefono);
                startActivity(new Intent(SignUpStep2Activity.this, HomeActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK));
                finish();
            } else toast("No se pudo registrar");
        });
    }

    private void toast(String s){ Toast.makeText(this, s, Toast.LENGTH_SHORT).show(); }
}
