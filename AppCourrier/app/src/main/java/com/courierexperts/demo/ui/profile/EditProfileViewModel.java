package com.courierexperts.demo.ui.profile;

import android.app.Application;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Patterns;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.courierexperts.demo.data.local.entity.DepositEntity;
import com.courierexperts.demo.data.local.entity.UserProfileEntity;
import com.courierexperts.demo.data.repository.DepositRepository;
import com.courierexperts.demo.data.repository.UserProfileRepository;

import java.util.List;

public class EditProfileViewModel extends AndroidViewModel {

    private static final String PREFS = "profile_prefs";
    private static final String KEY_SELECTED_DEPOSIT_ID = "selected_deposit_id";

    private final UserProfileRepository userRepo;
    private final DepositRepository depositRepo;
    private final MediatorLiveData<EditProfileUiState> uiState = new MediatorLiveData<>();

    public EditProfileViewModel(@NonNull Application application) {
        super(application);
        userRepo = new UserProfileRepository(application);
        depositRepo = new DepositRepository(application);
        uiState.setValue(new EditProfileUiState.Loading());

        LiveData<UserProfileEntity> profile = userRepo.observeProfile();
        LiveData<List<DepositEntity>> deposits = depositRepo.observeDeposits();

        uiState.addSource(profile, prof -> emitUiState(prof, deposits.getValue()));
        uiState.addSource(deposits, deps -> emitUiState(profile.getValue(), deps));
    }

    private void emitUiState(UserProfileEntity profile, List<DepositEntity> deposits) {
        if (profile == null || deposits == null) {
            uiState.setValue(new EditProfileUiState.Loading());
        } else {
            uiState.setValue(new EditProfileUiState.Success(profile, deposits));
        }
    }

    public LiveData<EditProfileUiState> getUiState() { return uiState; }

    public void normalizeNamesIfNeeded(String name, String lastName) {
        // Aquí usamos las versiones individuales porque son actualizaciones parciales reactivas
        if (!TextUtils.isEmpty(name)) {
            userRepo.updateName(name);
        }
        if (!TextUtils.isEmpty(lastName)) {
            userRepo.updateLastName(lastName);
        }
    }

    public void save(String name,
                     String lastName,
                     String phone,
                     String address,
                     String email,
                     Long depositId) {
        // Validaciones
        if (TextUtils.isEmpty(name) || name.length() < 2) {
            uiState.setValue(new EditProfileUiState.Error("Nombre inválido"));
            return;
        }
        if (TextUtils.isEmpty(lastName) || lastName.length() < 2) {
            uiState.setValue(new EditProfileUiState.Error("Apellido inválido"));
            return;
        }
        if (TextUtils.isEmpty(address) || address.length() < 5) {
            uiState.setValue(new EditProfileUiState.Error("Dirección inválida"));
            return;
        }
        if (!phone.matches("[+0-9\\s\\-]{6,25}")) {
            uiState.setValue(new EditProfileUiState.Error("Teléfono inválido"));
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            uiState.setValue(new EditProfileUiState.Error("Email inválido"));
            return;
        }

        // SOLUCIÓN: Usamos el nuevo método que actualiza todo junto
        // Esto evita que los hilos se pisen entre sí.
        userRepo.updateProfileData(name, lastName, phone, address, email, depositId);

        saveSelectedDeposit(depositId);
        uiState.setValue(new EditProfileUiState.Saved());
    }

    public Long getSavedDepositId() {
        SharedPreferences sp = getApplication().getSharedPreferences(PREFS, Application.MODE_PRIVATE);
        if (!sp.contains(KEY_SELECTED_DEPOSIT_ID)) return null;
        return sp.getLong(KEY_SELECTED_DEPOSIT_ID, -1L);
    }

    private void saveSelectedDeposit(Long id) {
        if (id == null) return;
        SharedPreferences sp = getApplication().getSharedPreferences(PREFS, Application.MODE_PRIVATE);
        sp.edit().putLong(KEY_SELECTED_DEPOSIT_ID, id).apply();
    }
}