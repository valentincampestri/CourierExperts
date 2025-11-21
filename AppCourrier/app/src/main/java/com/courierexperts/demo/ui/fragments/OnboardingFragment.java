package com.courierexperts.demo.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.viewpager2.widget.ViewPager2;

import com.courierexperts.demo.R;
import com.courierexperts.demo.databinding.FragmentOnboardingBinding;
import com.courierexperts.demo.ui.model.OnboardingPage;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class OnboardingFragment extends Fragment {

    private FragmentOnboardingBinding binding;
    private OnboardingAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("courier_prefs", Context.MODE_PRIVATE);
        if (sharedPreferences.getBoolean("onboarding_completed", false)) {
            navigateToWelcome();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentOnboardingBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupViewPager();

        
        binding.btnSkip.setOnClickListener(v -> completeOnboarding());
    }

    private void setupViewPager() {
        List<OnboardingPage> pages = new ArrayList<>();
        
        pages.add(new OnboardingPage(
                "Tus compras de USA en Argentina",
                "Recibí tus compras en nuestra dirección de USA y nosotros te la llevamos a Argentina.",
                R.drawable.ic_infinity));
        pages.add(new OnboardingPage(
                "Pre-alerta de paquetes",
                "Cargá la información de tu compra para que podamos identificar tu paquete.",
                R.drawable.ic_galaxy)); 
        pages.add(new OnboardingPage(
                "Seguimiento en tiempo real",
                "Seguí el estado de tu envío desde que sale de USA hasta que llega a tus manos.",
                R.drawable.ic_samsung));


        adapter = new OnboardingAdapter(pages);
        binding.viewPager.setAdapter(adapter);

        
        new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> {
            tab.setCustomView(R.layout.item_tab_dot);
        }).attach();

        
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getCustomView() != null) {
                    View dotView = tab.getCustomView().findViewById(R.id.dotView);
                    dotView.setBackgroundResource(R.drawable.dot_selected);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (tab.getCustomView() != null) {
                    View dotView = tab.getCustomView().findViewById(R.id.dotView);
                    dotView.setBackgroundResource(R.drawable.dot_unselected);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                
            }
        });
    }

    


    private void completeOnboarding() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("courier_prefs", Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean("onboarding_completed", true).apply();
        navigateToWelcome();
    }

    


    private void navigateToWelcome() {
        if (isAdded()) { 
            NavHostFragment.findNavController(this).navigate(R.id.action_onboardingFragment_to_welcomeFragment);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}