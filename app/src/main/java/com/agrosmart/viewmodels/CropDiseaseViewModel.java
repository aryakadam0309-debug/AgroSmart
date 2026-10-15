package com.agrosmart.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.agrosmart.models.CropDisease;
import com.agrosmart.repository.CropDiseaseRepository;

import java.util.List;

public class CropDiseaseViewModel extends ViewModel {

    private final CropDiseaseRepository repository;
    private final MutableLiveData<List<CropDisease>> diseases = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    private String currentQuery = "";
    private String currentCrop = "All Crops";

    public CropDiseaseViewModel() {
        this.repository = CropDiseaseRepository.getInstance();
        loadDiseases();
    }

    public LiveData<List<CropDisease>> getDiseases() { return diseases; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }

    public void loadDiseases() {
        isLoading.setValue(true);
        LiveData<List<CropDisease>> res = repository.getDiseases(currentQuery, currentCrop);
        diseases.setValue(res.getValue());
        isLoading.setValue(false);
    }

    public void search(String query) {
        this.currentQuery = query;
        loadDiseases();
    }

    public void filterByCrop(String crop) {
        this.currentCrop = crop;
        loadDiseases();
    }

    public void refresh() {
        loadDiseases();
    }
}
