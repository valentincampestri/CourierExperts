package com.courierexperts.demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class SignInActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    @SuppressWarnings("deprecation")
    private GoogleSignInClient google;
    private ActivityResultLauncher<Intent> googleLauncher;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        View btnBack = findViewById(R.id.btnBack);
        View btnLogin = findViewById(R.id.btnLogin);
        View btnGoogle = findViewById(R.id.btnGoogle);
        btnBack.setOnClickListener(v -> finish());
        auth = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(v -> doEmailLogin());
        if (btnGoogle != null) { btnGoogle.setOnClickListener(v -> doGoogle()); }

        // Config clásico de Google Sign-In (API deprecada, suprimimos warning por ahora)
        @SuppressWarnings("deprecation")
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        @SuppressWarnings("deprecation")
        GoogleSignInClient client = GoogleSignIn.getClient(this, gso);
        this.google = client;

        // Launcher para Google Sign-In usando Activity Result API
        googleLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    Intent data = result.getData();
                    @SuppressWarnings("deprecation")
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                    try {
                        GoogleSignInAccount account = task.getResult(ApiException.class);
                        if (account != null) {
                            AuthCredential cred = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                            auth.signInWithCredential(cred).addOnCompleteListener(this, t -> {
                                if (t.isSuccessful()) onAuthed(auth.getCurrentUser()); else toast("Error con Google");
                            });
                        }
                    } catch (ApiException e) { toast("Cancelado"); }
                }
        );
    }

    private void doEmailLogin() {
        com.google.android.material.textfield.TextInputEditText etEmail = findViewById(R.id.etEmailSignin);
        com.google.android.material.textfield.TextInputEditText etPass = findViewById(R.id.etPasswordSignin);
        String email = etEmail != null && etEmail.getText()!=null ? etEmail.getText().toString().trim(): "";
        String pass  = etPass  != null && etPass.getText()!=null  ? etPass.getText().toString()       : "";
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            toast("Email inválido");
            return;
        }
        if (pass.length() < 4 || pass.length() > 20) {
            toast("Contraseña 4 a 20 caracteres");
            return;
        }

        // Bypass de login para pruebas locales (sin depender de Google/Firebase)
        if ("mail@gmail.com".equalsIgnoreCase(email) && "123456".equals(pass)) {
            new com.courierexperts.demo.data.repository.UserProfileRepository(this).updateEmail(email);
            startActivity(new Intent(SignInActivity.this, HomeActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK));
            finish();
            return;
        }
        findViewById(R.id.btnLogin).setEnabled(false);
        auth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, task -> {
                    findViewById(R.id.btnLogin).setEnabled(true);
                    if (task.isSuccessful()) {
                        onAuthed(auth.getCurrentUser());
                    } else {
                        toast("No se pudo iniciar sesión");
                    }
                });
    }

    private void doGoogle() {
        @SuppressWarnings("deprecation")
        Intent signInIntent = google.getSignInIntent();
        googleLauncher.launch(signInIntent);
    }

    private void onAuthed(FirebaseUser user) {
        if (user == null) { toast("Error de autenticación"); return; }
        // Opcional: inicializar perfil local
        new com.courierexperts.demo.data.repository.UserProfileRepository(this).updateEmail(user.getEmail()!=null?user.getEmail():"");
        if (user.getDisplayName()!=null) new com.courierexperts.demo.data.repository.UserProfileRepository(this).updateName(user.getDisplayName());
        startActivity(new Intent(SignInActivity.this, HomeActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK));
        finish();
    }

    private void toast(@NonNull String s) { Toast.makeText(this, s, Toast.LENGTH_SHORT).show(); }
}
