package com.courierexperts.demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ShipmentDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipment_detail);

        int shipmentId = getIntent().getIntExtra("shipmentId", 5);

        TextView tvSub = findViewById(R.id.tvSub);
        if (tvSub != null) {
            if (shipmentId == 5) {
                tvSub.setText("Envío 5\n#12345");
            } else if (shipmentId == 4) {
                tvSub.setText("Envío 4\n#67890");
            }
        }

        View tvEmpty = findViewById(R.id.tvEmpty);
        LinearLayout containerList = findViewById(R.id.containerList);
        if (shipmentId == 4) {
            if (tvEmpty != null) tvEmpty.setVisibility(View.VISIBLE);
            if (containerList != null) containerList.setVisibility(View.GONE);
        } else {
            if (tvEmpty != null) tvEmpty.setVisibility(View.GONE);
            if (containerList != null) containerList.setVisibility(View.VISIBLE);
        }

        BottomNavigationView bottom = findViewById(R.id.bottomNav);
        if (bottom != null) {
            bottom.setSelectedItemId(R.id.nav_home);
            bottom.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    int id = item.getItemId();
                    if (id == R.id.nav_home) {
                        startActivity(new Intent(ShipmentDetailActivity.this, HomeActivity.class));
                        return true;
                    } else if (id == R.id.nav_add) {
                        startActivity(new Intent(ShipmentDetailActivity.this, NewPurchaseActivity.class));
                        return true;
                    } else if (id == R.id.nav_profile) {
                        startActivity(new Intent(ShipmentDetailActivity.this, ProfileActivity.class));
                        return true;
                    }
                    return false;
                }
            });
        }
    }
}
