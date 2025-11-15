package com.courierexperts.demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.courierexperts.demo.data.repository.PurchaseRepository;
import com.courierexperts.demo.util.NetworkUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;

public class NewPurchaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nueva_compra);

        setupBottomBar();

        View btnCancelar = findViewById(R.id.btnCancelarnuevaCompra);
        View btnGuardar  = findViewById(R.id.btnGuardarNuevaCompra);

        TextInputEditText etStore       = findViewById(R.id.etStoreName);
        TextInputEditText etOrder       = findViewById(R.id.etOrderId);

        // NUEVOS CAMPOS (asegúrate de tener estos IDs en el XML)
        TextInputEditText    etDescription = findViewById(R.id.etDescription); // descripción / nombre producto
        AutoCompleteTextView etCarrier     = findViewById(R.id.etCarrier);     // empresa de envío
        TextInputEditText    etPrice       = findViewById(R.id.etPrice);       // precio
        TextInputEditText etName        = findViewById(R.id.etName);        // nombre del producto

        if (btnCancelar != null) {
            btnCancelar.setOnClickListener(v -> finish());
        }

        if (btnGuardar != null) {
            btnGuardar.setOnClickListener(v -> {
                String store = etStore != null && etStore.getText() != null
                        ? etStore.getText().toString().trim()
                        : "";
                String order = etOrder != null && etOrder.getText() != null
                        ? etOrder.getText().toString().trim()
                        : "";

                if (store.isEmpty() || order.isEmpty()) {
                    Toast.makeText(this,
                            "Completa Tienda y Nro Tracking",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                // NUEVOS VALORES
                String description = etDescription != null && etDescription.getText() != null
                        ? etDescription.getText().toString().trim()
                        : "";
                String carrier = etCarrier != null && etCarrier.getText() != null
                        ? etCarrier.getText().toString().trim()
                        : "";
                String priceStr = etPrice != null && etPrice.getText() != null
                        ? etPrice.getText().toString().trim()
                        : "";
                String name = etName != null && etName.getText() != null
                        ? etName.getText().toString().trim()
                        : "";

                Double price = null;
                if (!priceStr.isEmpty()) {
                    try {
                        price = Double.valueOf(priceStr);
                    } catch (NumberFormatException ex) {
                        Toast.makeText(this,
                                "Precio inválido",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                }


                String nowIso = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
                {{
                    setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
                }}.format(new java.util.Date());

                boolean online = NetworkUtils.isOnline(this);

                PurchaseRepository repo = new PurchaseRepository(this);
                repo.createLocalAndSync(
                        store,          // storeName
                        order,          // orderId
                        name,           // name (nuevo campo)
                        description,    // description
                        carrier,        // carrier
                        price,          // price
                        "",             // thumbnailUrl (por ahora vacío)
                        nowIso          // createdAtIso
                );

                Toast.makeText(
                        this,
                        online ? "Compra guardada" : "Guardado local, se sincronizará luego",
                        Toast.LENGTH_SHORT
                ).show();

                // Volver a lista de compras
                startActivity(new Intent(NewPurchaseActivity.this, PurchasesActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
                finish();
            });
        }
    }

    private void setupBottomBar() {
        BottomNavigationView bottom = findViewById(R.id.bottomNav);
        if (bottom == null) return;

        // Seleccionar el item central (add)
        bottom.setSelectedItemId(R.id.nav_add);

        bottom.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    startActivity(new Intent(NewPurchaseActivity.this, HomeActivity.class));
                    return true;
                } else if (id == R.id.nav_add) {
                    // Ya estamos en Nueva Compra
                    return true;
                } else if (id == R.id.nav_profile) {
                    startActivity(new Intent(NewPurchaseActivity.this, ProfileActivity.class));
                    return true;
                }
                return false;
            }
        });
    }

}
