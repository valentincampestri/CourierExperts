package com.courierexperts.demo.ui.model;

public class OnboardingPage {
    private String title;
    private String description;
    private int imageResource;

    public OnboardingPage(String title, String description, int imageResource) {
        this.title = title;
        this.description = description;
        this.imageResource = imageResource;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getImageResource() {
        return imageResource;
    }
}
