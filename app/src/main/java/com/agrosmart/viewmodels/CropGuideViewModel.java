package com.agrosmart.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.agrosmart.models.CropGuide;
import com.agrosmart.repository.CropGuideRepository;

import java.util.List;

public class CropGuideViewModel extends ViewModel {

    private final CropGuideRepository repository;
    private final MutableLiveData<List<CropGuide>> guides = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    private String currentQuery = "";
    private String currentSeason = "All Seasons";

    public CropGuideViewModel() {
        this.repository = CropGuideRepository.getInstance();
        loadGuides();
    }

    public LiveData<List<CropGuide>> getGuides() { return guides; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }

    public void loadGuides() {
        isLoading.setValue(true);
        LiveData<List<CropGuide>> result = repository.getGuides(currentQuery, currentSeason);
        guides.setValue(result.getValue());
        isLoading.setValue(false);
    }

    public void search(String query) {
        this.currentQuery = query;
        loadGuides();
    }

    public void filterBySeason(String season) {
        this.currentSeason = season;
        loadGuides();
    }

    public void refresh() {
        loadGuides();
    }
}
