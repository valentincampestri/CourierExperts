package com.courierexperts.demo.ui.home;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;

public class RecentActivityItem {

    public enum Type {
        PURCHASE,
        PACKAGE,
        SHIPMENT
    }

    private final long id;

    
    @Nullable
    private final String firestoreId;

    private final Type type;
    private final String title;
    private final String subtitle;
    private final String statusLabel;
    private final String dateLabel;
    private final String thumbnailUrl;

    @DrawableRes
    private final int iconResId;

    public RecentActivityItem(long id,
                              @Nullable String firestoreId,   
                              Type type,
                              String title,
                              String subtitle,
                              String statusLabel,
                              String dateLabel,
                              String thumbnailUrl,
                              @DrawableRes int iconResId) {
        this.id = id;
        this.firestoreId = firestoreId;
        this.type = type;
        this.title = title;
        this.subtitle = subtitle;
        this.statusLabel = statusLabel;
        this.dateLabel = dateLabel;
        this.thumbnailUrl = thumbnailUrl;
        this.iconResId = iconResId;
    }

    public long getId() {
        return id;
    }

    @Nullable
    public String getFirestoreId() {  
        return firestoreId;
    }

    public Type getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getStatusLabel() {
        return statusLabel;
    }

    public String getDateLabel() {
        return dateLabel;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public int getIconResId() {
        return iconResId;
    }
}
