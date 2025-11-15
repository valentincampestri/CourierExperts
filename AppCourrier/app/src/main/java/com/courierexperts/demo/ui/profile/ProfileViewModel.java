package com.courierexperts.demo.ui.profile;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.courierexperts.demo.data.local.db.AppDatabase;
import com.courierexperts.demo.data.local.entity.UserProfileEntity;
import com.courierexperts.demo.data.repository.UserProfileRepository;
import com.courierexperts.demo.util.AppExecutors;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileViewModel extends AndroidViewModel {

    private final UserProfileRepository repository;
    private final MediatorLiveData<ProfileUiState> uiState = new MediatorLiveData<>();
    private final MutableLiveData<Boolean> logoutEvents = new MutableLiveData<>();

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        repository = new UserProfileRepository(application);
        uiState.setValue(new ProfileUiState.Loading());

        LiveData<UserProfileEntity> source = repository.observeProfile();
        uiState.addSource(source, profile -> {
            if (profile == null) {
                uiState.setValue(new ProfileUiState.Error("Perfil no disponible"));
            } else {
                uiState.setValue(new ProfileUiState.Success(profile));
            }
        });
    }

    public LiveData<ProfileUiState> getUiState() {
        return uiState;
    }

    public LiveData<Boolean> getLogoutEvents() {
        return logoutEvents;
    }

    public void updateNotifications(boolean enabled) {
        repository.updateNotifications(enabled);
    }

    public void logout() {
        AppExecutors.io().execute(() -> {
            try { FirebaseAuth.getInstance().signOut(); } catch (Exception ignored) {}
            clearLocalData();
            logoutEvents.postValue(true);
        });
    }

    private void clearLocalData() {
        Application app = getApplication();
        AppDatabase db = AppDatabase.get(app);
        try { db.purchaseDao().clear(); } catch (Exception ignored) {}
        try { db.packageDao().clear(); } catch (Exception ignored) {}
        try { db.shipmentDao().clear(); } catch (Exception ignored) {}
        try { db.depositDao().clear(); } catch (Exception ignored) {}

        try {
            SharedPreferences sp = app.getSharedPreferences("profile_prefs", Application.MODE_PRIVATE);
            sp.edit().clear().apply();
        } catch (Exception ignored) {}

        try { com.bumptech.glide.Glide.get(app).clearDiskCache(); } catch (Exception ignored) {}
        try { deleteDirQuiet(app.getCacheDir()); } catch (Exception ignored) {}
        try {
            java.io.File ext = app.getExternalCacheDir();
            if (ext != null) deleteDirQuiet(ext);
        } catch (Exception ignored) {}

        new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
            try { com.bumptech.glide.Glide.get(app).clearMemory(); } catch (Exception ignored) {}
        });
    }

    private static void deleteDirQuiet(java.io.File dir) {
        if (dir == null || !dir.exists()) return;
        java.io.File[] files = dir.listFiles();
        if (files != null) {
            for (java.io.File f : files) {
                if (f.isDirectory()) deleteDirQuiet(f);
                else try { f.delete(); } catch (Exception ignored) {}
            }
        }
        try { dir.delete(); } catch (Exception ignored) {}
    }
}
