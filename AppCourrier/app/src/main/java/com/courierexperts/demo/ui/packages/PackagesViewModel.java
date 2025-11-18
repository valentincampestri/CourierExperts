package com.courierexperts.demo.ui.packages;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.courierexperts.demo.data.local.entity.PackageEntity;
import com.courierexperts.demo.data.repository.PackageRepository;

import java.util.List;

public class PackagesViewModel extends AndroidViewModel {

    private final PackageRepository repo;
    private final MediatorLiveData<PackagesUiState> uiState = new MediatorLiveData<>();
    // Flag para saber si ya cargamos datos al menos una vez
    private boolean isDataLoaded = false;

    public PackagesViewModel(@NonNull Application app) {
        super(app);
        repo = new PackageRepository(app);

        // Estado inicial Loading solo la primera vez que se crea el VM
        uiState.setValue(new PackagesUiState.Loading());

        LiveData<List<PackageEntity>> packagesSource = repo.observeAllOrdered();
        uiState.addSource(packagesSource, list -> {
            if (list == null || list.isEmpty()) {
                uiState.setValue(new PackagesUiState.Empty());
            } else {
                isDataLoaded = true; // Marcamos que ya tenemos datos
                uiState.setValue(new PackagesUiState.Success(list));
            }
        });

        LiveData<String> errorSource = repo.getErrors();
        uiState.addSource(errorSource, message -> {
            // Solo mostramos error si no tenemos datos previos,
            // para no tapar la lista con un mensaje de error si falla el refresh silencioso
            if (!isDataLoaded && message != null && !message.trim().isEmpty()) {
                uiState.setValue(new PackagesUiState.Error(message));
            }
        });
    }

    public LiveData<PackagesUiState> getUiState() { return uiState; }

    public void refresh() {
        // CORRECCIÓN:
        // Si ya tenemos datos cargados (isDataLoaded o el estado actual es Success),
        // NO forzamos el estado Loading. Dejamos que el usuario vea la lista vieja
        // mientras se actualiza por detrás.

        PackagesUiState current = uiState.getValue();
        boolean showingData = current instanceof PackagesUiState.Success;

        if (!showingData) {
            // Solo mostramos loading si la pantalla estaba vacía o en error
            uiState.setValue(new PackagesUiState.Loading());
        }

        // Llamamos a la red. Si hay cambios, Room avisará y la UI se actualizará sola.
        // Si no hay cambios, Room no avisa, pero como no pusimos Loading, el usuario sigue viendo su lista.
        repo.refreshFromNetwork();
    }
}