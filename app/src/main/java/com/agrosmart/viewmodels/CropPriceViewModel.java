package com.agrosmart.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.agrosmart.models.CropPrice;
import com.agrosmart.repository.CropPriceRepository;

import java.util.List;

public class CropPriceViewModel extends ViewModel {

    private final CropPriceRepository repository;
    private final MutableLiveData<List<CropPrice>> cropPrices = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    private String currentSearch = "";
    private String currentState = "All States";
    private String currentCategory = "All Crops";

    public CropPriceViewModel() {
        this.repository = CropPriceRepository.getInstance();
        loadPrices();
    }

    public LiveData<List<CropPrice>> getCropPrices() {
        return cropPrices;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void loadPrices() {
        isLoading.setValue(true);
        LiveData<List<CropPrice>> result = repository.getCropPrices(currentSearch, currentState, currentCategory);
        cropPrices.setValue(result.getValue());
        isLoading.setValue(false);
    }

    public void search(String query) {
        this.currentSearch = query;
        loadPrices();
    }

    public void filterByState(String state) {
        this.currentState = state;
        loadPrices();
    }

    public void filterByCategory(String category) {
        this.currentCategory = category;
        loadPrices();
    }

    public void refresh() {
        loadPrices();
    }
}
