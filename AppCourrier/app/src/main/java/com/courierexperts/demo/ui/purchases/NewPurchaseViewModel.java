package com.courierexperts.demo.ui.purchases;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.courierexperts.demo.R;
import com.courierexperts.demo.data.repository.PurchaseRepository;
import com.courierexperts.demo.util.Event;
import com.courierexperts.demo.util.NetworkUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class NewPurchaseViewModel extends AndroidViewModel {

    private final PurchaseRepository repository;
    private final MutableLiveData<NewPurchaseUiState> uiState = new MutableLiveData<>(new NewPurchaseUiState.Idle());
    private final MutableLiveData<Event<NewPurchaseEvent>> events = new MutableLiveData<>();

    public NewPurchaseViewModel(@NonNull Application application) {
        super(application);
        repository = new PurchaseRepository(application);
    }

    public LiveData<NewPurchaseUiState> getUiState() {
        return uiState;
    }

    public LiveData<Event<NewPurchaseEvent>> getEvents() {
        return events;
    }

    public void savePurchase(String store, String description, String orderId) {
        String storeName = safe(store);
        String order = safe(orderId);
        if (storeName.isEmpty() || order.isEmpty()) {
            events.setValue(new Event<>(NewPurchaseEvent.showMessage(getApplication().getString(R.string.new_purchase_error_required))));
            return;
        }
        String desc = safe(description);
        uiState.setValue(new NewPurchaseUiState.Loading());

        String nowIso = nowIso();
        repository.createLocalAndSync(storeName, order, desc, nowIso);

        boolean online = NetworkUtils.isOnline(getApplication());
        String message = online
                ? getApplication().getString(R.string.new_purchase_saved_online)
                : getApplication().getString(R.string.new_purchase_saved_offline);
        uiState.setValue(new NewPurchaseUiState.Idle());
        events.setValue(new Event<>(NewPurchaseEvent.success(message)));
    }

    private static String safe(String value) {
        return value != null ? value.trim() : "";
    }

    private static String nowIso() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(new Date());
    }
}
