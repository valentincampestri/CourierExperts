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
import com.courierexperts.demo.data.local.entity.PackageEntity;
import com.courierexperts.demo.data.local.entity.PurchaseEntity;
import com.courierexperts.demo.data.local.entity.ShipmentEntity;
import com.courierexperts.demo.data.local.entity.UserProfileEntity;
import com.courierexperts.demo.data.repository.PackageRepository;
import com.courierexperts.demo.data.repository.PurchaseRepository;
import com.courierexperts.demo.data.repository.ShipmentRepository;
import com.courierexperts.demo.data.repository.UserProfileRepository;
import com.courierexperts.demo.domain.StatusMapper;
import com.courierexperts.demo.util.Event;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;





public class HomeViewModel extends AndroidViewModel {

    private final UserProfileRepository profileRepository;
    private final ShipmentRepository shipmentRepository;
    private final PurchaseRepository purchaseRepository;
    private final PackageRepository packageRepository;

    private final MediatorLiveData<HomeUiState> uiState = new MediatorLiveData<>();
    private final MutableLiveData<Event<HomeEvent>> events = new MutableLiveData<>();
    private final SharedPreferences profilePrefs;

    private boolean profileLoaded;
    private boolean shipmentsLoaded;
    private boolean purchasesLoaded;
    private boolean packagesLoaded;

    @Nullable private UserProfileEntity latestProfile;
    @Nullable private List<ShipmentEntity> shipmentsList;
    @Nullable private List<PurchaseEntity> purchasesList;
    @Nullable private List<PackageEntity> packagesList;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        profileRepository = new UserProfileRepository(application);
        shipmentRepository = new ShipmentRepository(application);
        purchaseRepository = new PurchaseRepository(application);
        packageRepository = new PackageRepository(application);
        profileRepository.syncFromFirestore();

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
            shipmentsList = list;
            publishState();
        });

        
        LiveData<List<PurchaseEntity>> purchasesSource = purchaseRepository.observePurchases();
        uiState.addSource(purchasesSource, list -> {
            purchasesLoaded = true;
            purchasesList = list;
            publishState();
        });

        
        LiveData<List<PackageEntity>> packagesSource = packageRepository.observeAllOrdered();
        uiState.addSource(packagesSource, list -> {
            packagesLoaded = true;
            packagesList = list;
            publishState();
        });

        
        LiveData<String> shipmentErrors = shipmentRepository.getErrors();
        uiState.addSource(shipmentErrors, message -> {
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
        
        if (!profileLoaded && !shipmentsLoaded && !purchasesLoaded && !packagesLoaded) {
            uiState.setValue(new HomeUiState.Loading());
            return;
        }

        String greeting = formatGreeting(latestProfile);
        List<RecentActivityItem> recent = buildRecentActivityList(
                purchasesList,
                packagesList,
                shipmentsList
        );

        uiState.setValue(new HomeUiState.Content(greeting, recent));
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

    

    





    private List<RecentActivityItem> buildRecentActivityList(
            @Nullable List<PurchaseEntity> purchases,
            @Nullable List<PackageEntity> packages,
            @Nullable List<ShipmentEntity> shipments
    ) {
        class Row {
            long epoch;
            RecentActivityItem item;
        }

        List<Row> rows = new ArrayList<>();

        
        if (purchases != null) {
            for (PurchaseEntity p : purchases) {
                if (p == null) continue;
                Row r = new Row();
                r.epoch = p.createdAt > 0 ? p.createdAt : 0L;

                String title = !TextUtils.isEmpty(p.productName)
                        ? p.productName
                        : getApplication().getString(R.string.home_recent_purchase_fallback_title);

                String subtitle = (p.description != null && !p.description.isEmpty())
                        ? p.description
                        : "-";

                String statusLabel = StatusMapper.labelPurchase(p.status);
                String dateLabel = formatShortDate(r.epoch);
                String thumb = (p.thumbnailUrl != null) ? p.thumbnailUrl : "";

                int iconRes = R.drawable.ic_compras;

                r.item = new RecentActivityItem(
                        p.id,
                        null,
                        RecentActivityItem.Type.PURCHASE,
                        title,
                        subtitle,
                        statusLabel,
                        dateLabel,
                        thumb,
                        iconRes
                );
                rows.add(r);
            }
        }

        
        if (packages != null) {
            for (PackageEntity pkg : packages) {
                if (pkg == null) continue;
                Row r = new Row();
                r.epoch = pkg.lastUpdate > 0 ? pkg.lastUpdate : 0L;

                String title = !TextUtils.isEmpty(pkg.label)
                        ? pkg.label
                        : getApplication().getString(R.string.home_recent_package_fallback_title);

                String subtitle = !TextUtils.isEmpty(pkg.description)
                        ? pkg.description
                        : getApplication().getString(R.string.home_recent_package_subtitle, pkg.id);

                String statusLabel = StatusMapper.labelPackage(pkg.status);
                String dateLabel = formatShortDate(r.epoch);
                String thumb = (pkg.thumbnailUrl != null) ? pkg.thumbnailUrl : "";

                int iconRes = R.drawable.ic_paquetes; 

                r.item = new RecentActivityItem(
                        pkg.id,
                        null,
                        RecentActivityItem.Type.PACKAGE,
                        title,
                        subtitle,
                        statusLabel,
                        dateLabel,
                        thumb,
                        iconRes
                );
                rows.add(r);
            }
        }

        
        if (shipments != null) {
            for (ShipmentEntity s : shipments) {
                if (s == null) continue;
                Row r = new Row();
                r.epoch = s.lastUpdate > 0 ? s.lastUpdate : 0L;

                String title = !TextUtils.isEmpty(s.title)
                        ? s.title
                        : getApplication().getString(R.string.shipment_title_placeholder);

                String subtitle = getApplication().getString(
                        R.string.home_recent_shipment_subtitle,  
                        s.id
                );

                String statusLabel = StatusMapper.labelShipment(s.status);
                String dateLabel = formatShortDate(r.epoch);
                String thumb = (s.thumbnailUrl != null) ? s.thumbnailUrl : "";

                int iconRes = R.drawable.ic_envios;

                r.item = new RecentActivityItem(
                        s.id,
                        s.fsId,
                        RecentActivityItem.Type.SHIPMENT,
                        title,
                        subtitle,
                        statusLabel,
                        dateLabel,
                        thumb,
                        iconRes
                );
                rows.add(r);
            }
        }

        
        Collections.sort(rows, new Comparator<Row>() {
            @Override
            public int compare(Row a, Row b) {
                return Long.compare(b.epoch, a.epoch);
            }
        });

        
        List<RecentActivityItem> result = new ArrayList<>();
        int max = Math.min(rows.size(), 5);
        for (int i = 0; i < max; i++) {
            result.add(rows.get(i).item);
        }

        return result;
    }

    private String formatShortDate(long epochMillis) {
        if (epochMillis <= 0) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM HH:mm", Locale.getDefault());
        return sdf.format(new Date(epochMillis));
    }
}
