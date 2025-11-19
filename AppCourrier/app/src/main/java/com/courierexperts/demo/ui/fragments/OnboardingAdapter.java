package com.courierexperts.demo.ui.fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.courierexperts.demo.R;
import com.courierexperts.demo.ui.model.OnboardingPage;

import java.util.List;

public class OnboardingAdapter extends RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder> {

    private List<OnboardingPage> onboardingPages;

    public OnboardingAdapter(List<OnboardingPage> onboardingPages) {
        this.onboardingPages = onboardingPages;
    }

    @NonNull
    @Override
    public OnboardingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OnboardingViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_onboarding_page, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull OnboardingViewHolder holder, int position) {
        holder.bind(onboardingPages.get(position));
    }

    @Override
    public int getItemCount() {
        return onboardingPages.size();
    }

    static class OnboardingViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView tvTitle;
        private TextView tvDescription;

        public OnboardingViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
        }

        void bind(OnboardingPage onboardingPage) {
            tvTitle.setText(onboardingPage.getTitle());
            tvDescription.setText(onboardingPage.getDescription());
            imageView.setImageResource(onboardingPage.getImageResource());
        }
    }
}
