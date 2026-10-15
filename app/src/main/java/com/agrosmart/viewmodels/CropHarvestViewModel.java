package com.agrosmart.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.agrosmart.models.CropHarvest;
import com.agrosmart.repository.CropHarvestRepository;

import java.util.List;

public class CropHarvestViewModel extends ViewModel {

    private final CropHarvestRepository repository;

    public CropHarvestViewModel() {
        this.repository = CropHarvestRepository.getInstance();
    }

    public LiveData<List<CropHarvest>> getHarvests() {
        return repository.getHarvests();
    }

    public void addHarvest(CropHarvest harvest) {
        repository.addHarvest(harvest);
    }

    public void removeHarvest(String id) {
        repository.removeHarvest(id);
    }

    public void sellStock(String id, double qty) {
        repository.sellStock(id, qty);
    }
}
