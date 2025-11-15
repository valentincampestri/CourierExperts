package com.courierexperts.demo.ui.home;

import android.app.Application;
import android.content.SharedPreferences;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.courierexperts.demo.R;
import com.courierexperts.demo.data.local.entity.ShipmentEntity;
import com.courierexperts.demo.data.local.entity.UserProfileEntity;
import com.courierexperts.demo.data.repository.ShipmentRepository;
import com.courierexperts.demo.data.repository.UserProfileRepository;
import com.courierexperts.demo.domain.StatusMapper;
import com.courierexperts.demo.util.Event;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

/**
 * Combina perfil + ultimo envA-o para renderizar HomeActivity y expone eventos
 * puntuales (recordatorio de depA3sito, errores remotos).
 */
public class HomeViewModel extends AndroidViewModel {

    private final UserProfileRepository profileRepository;
    private final ShipmentRepository shipmentRepository;
    private final MediatorLiveData<HomeUiState> uiState = new MediatorLiveData<>();
    private final MutableLiveData<Event<HomeEvent>> events = new MutableLiveData<>();
    private final SharedPreferences profilePrefs;

    private boolean profileLoaded;
    private boolean shipmentsLoaded;
    @Nullable private UserProfileEntity latestProfile;
    @Nullable private ShipmentEntity latestShipment;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        profileRepository = new UserProfileRepository(application);
        shipmentRepository = new ShipmentRepository(application);
        profilePrefs = application.getSharedPreferences("profile_prefs", Application.MODE_PRIVATE);
        uiState.setValue(new HomeUiState.Loading());

        LiveData<UserProfileEntity> profileSource = profileRepository.observeProfile();
        uiState.addSource(profileSource, profile -> {
            profileLoaded = true;
            latestProfile = profile;
            maybeTriggerDepositReminder(profile);
            publishState();
        });

        LiveData<List<ShipmentEntity>> shipmentsSource = shipmentRepository.observeShipments();
        uiState.addSource(shipmentsSource, list -> {
            shipmentsLoaded = true;
            latestShipment = pickLatestShipment(list);
            publishState();
        });

        LiveData<String> errors = shipmentRepository.getErrors();
        uiState.addSource(errors, message -> {
            if (message != null && !message.trim().isEmpty()) {
                events.setValue(new Event<>(HomeEvent.showError(message)));
            }
        });
    }

    public LiveData<HomeUiState> getUiState() {
        return uiState;
    }

    public LiveData<Event<HomeEvent>> getEvents() {
        return events;
    }

    public void refreshShipments() {
        shipmentRepository.refreshFromNetwork();
    }

    private void publishState() {
        if (!profileLoaded && !shipmentsLoaded) {
            uiState.setValue(new HomeUiState.Loading());
            return;
        }
        String greeting = formatGreeting(latestProfile);
        HomeUiState.LastShipmentCard card = buildLastShipmentCard(latestShipment);
        uiState.setValue(new HomeUiState.Content(greeting, card));
    }

    private String formatGreeting(@Nullable UserProfileEntity profile) {
        Application app = getApplication();
        if (profile == null || TextUtils.isEmpty(profile.name)) {
            return app.getString(R.string.home_greeting_generic);
        }
        String name = profile.name.trim();
        if (name.isEmpty()) {
            return app.getString(R.string.home_greeting_generic);
        }
        return app.getString(R.string.home_greeting_with_name, name);
    }

    private void maybeTriggerDepositReminder(@Nullable UserProfileEntity profile) {
        if (profile == null || profile.depositId != null) {
            return;
        }
        String uid = resolveUid(profile);
        if (uid == null || uid.trim().isEmpty()) {
            return;
        }
        String key = "prompt_deposit_done_" + uid;
        if (profilePrefs.getBoolean(key, false)) {
            return;
        }
        profilePrefs.edit().putBoolean(key, true).apply();
        events.setValue(new Event<>(HomeEvent.depositReminder()));
    }

    @Nullable
    private String resolveUid(@Nullable UserProfileEntity profile) {
        if (profile != null && !TextUtils.isEmpty(profile.uid)) {
            return profile.uid;
        }
        try {
            return FirebaseAuth.getInstance().getUid();
        } catch (Exception ignored) {
            return null;
        }
    }

    @Nullable
    private ShipmentEntity pickLatestShipment(@Nullable List<ShipmentEntity> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        ShipmentEntity latest = null;
        for (ShipmentEntity entity : list) {
            if (entity == null) {
                continue;
            }
            if (latest == null) {
                latest = entity;
                continue;
            }
            if (entity.lastUpdate > latest.lastUpdate) {
                latest = entity;
            } else if (entity.lastUpdate == latest.lastUpdate && entity.id > latest.id) {
                latest = entity;
            }
        }
        return latest;
    }

    @Nullable
    private HomeUiState.LastShipmentCard buildLastShipmentCard(@Nullable ShipmentEntity entity) {
        if (entity == null) {
            return null;
        }
        String title = !TextUtils.isEmpty(entity.title)
                ? entity.title
                : getApplication().getString(R.string.shipment_title_placeholder);
        String status = StatusMapper.labelShipment(entity.status);
        return new HomeUiState.LastShipmentCard(entity.id, entity.fsId, title, status);
    }
}
