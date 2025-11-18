package com.courierexperts.demo;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.courierexperts.demo.databinding.ActivityAuthHostBinding;

public class WelcomeActivity extends AppCompatActivity {

    private ActivityAuthHostBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        // APLICAR MODO OSCURO / CLARO ANTES DEL setContentView
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        boolean isDark = prefs.getBoolean("dark_mode", false);

        AppCompatDelegate.setDefaultNightMode(
                isDark ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );

        super.onCreate(savedInstanceState);

        binding = ActivityAuthHostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
