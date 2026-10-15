package com.agrosmart.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.agrosmart.models.FertilizerItem;
import com.agrosmart.repository.FertilizerRepository;

import java.util.List;

public class FertilizerViewModel extends ViewModel {

    private final FertilizerRepository repository;

    public FertilizerViewModel() {
        this.repository = FertilizerRepository.getInstance();
    }

    public LiveData<List<FertilizerItem>> getFertilizers() {
        return repository.getFertilizers();
    }

    public void addFertilizer(FertilizerItem item) {
        repository.addFertilizer(item);
    }

    public void removeFertilizer(String id) {
        repository.removeFertilizer(id);
    }

    public void updateQuantity(String id, int newQty) {
        repository.updateQuantity(id, newQty);
    }
}
