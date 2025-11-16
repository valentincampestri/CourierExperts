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
    private final MutableLiveData<NewPurchaseUiState> uiState =
            new MutableLiveData<>(new NewPurchaseUiState.Idle());
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

    /**
     * Guarda una nueva compra con todos los datos del formulario.
     *
     * @param productName  Nombre del producto
     * @param description  Descripción
     * @param storeName    Nombre de la tienda (OBLIGATORIO)
     * @param carrierName  Nombre del carrier / empresa de envío
     * @param priceStr     Precio como texto (se parsea a Double)
     * @param orderId      Id de orden / tracking (OBLIGATORIO)
     */
    public void savePurchase(
            String productName,
            String description,
            String storeName,
            String carrierName,
            String priceStr,
            String orderId
    ) {
        String store  = safe(storeName);
        String order  = safe(orderId);

        // Validación mínima: tienda y orden son obligatorios
        if (store.isEmpty() || order.isEmpty()) {
            events.setValue(new Event<>(
                    NewPurchaseEvent.showMessage(
                            getApplication().getString(R.string.new_purchase_error_required)
                    )
            ));
            return;
        }

        String prod     = safe(productName);
        String desc     = safe(description);
        String carrier  = safe(carrierName);
        String priceTxt = safe(priceStr);

        Double price = null;
        if (!priceTxt.isEmpty()) {
            try {
                price = Double.parseDouble(priceTxt);
            } catch (NumberFormatException e) {
                // Si querés que el precio inválido corte el flujo, podés descomentar:
                // events.setValue(new Event<>(NewPurchaseEvent.showMessage("Precio inválido")));
                // return;
            }
        }

        uiState.setValue(new NewPurchaseUiState.Loading());

        String nowIso = nowIso();

        // Llamamos al repositorio con TODOS los campos
        repository.createLocalAndSync(
                prod,
                store,
                carrier,
                price,
                order,
                desc,
                nowIso
        );

        boolean online = NetworkUtils.isOnline(getApplication());
        String message = online
                ? getApplication().getString(R.string.new_purchase_saved_online)
                : getApplication().getString(R.string.new_purchase_saved_offline);

        uiState.setValue(new NewPurchaseUiState.Idle());
        events.setValue(new Event<>(NewPurchaseEvent.success(message)));
    }

    // Helpers

    private static String safe(String value) {
        return value != null ? value.trim() : "";
    }

    private static String nowIso() {
        SimpleDateFormat sdf =
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(new Date());
    }
}
