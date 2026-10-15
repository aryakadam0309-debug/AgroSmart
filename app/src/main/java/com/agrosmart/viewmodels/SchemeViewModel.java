package com.agrosmart.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.agrosmart.models.GovernmentScheme;
import com.agrosmart.repository.SchemeRepository;

import java.util.List;

public class SchemeViewModel extends AndroidViewModel {

    private final SchemeRepository repository;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>(null);
    private final MutableLiveData<List<GovernmentScheme>> schemesList = new MutableLiveData<>();

    private String currentCategory = "All";
    private String currentSearch = "";

    public SchemeViewModel(@NonNull Application application) {
        super(application);
        this.repository = SchemeRepository.getInstance();
    }

    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<List<GovernmentScheme>> getSchemesList() { return schemesList; }

    public void filterCategory(String category) {
        this.currentCategory = category != null ? category : "All";
        fetch();
    }

    public void search(String query) {
        this.currentSearch = query != null ? query : "";
        fetch();
    }

    private void fetch() {
        isLoading.setValue(true);
        errorMessage.setValue(null);

        repository.getSchemes(currentCategory, currentSearch, new SchemeRepository.SchemeCallback() {
            @Override
            public void onSuccess(List<GovernmentScheme> schemes) {
                isLoading.postValue(false);
                schemesList.postValue(schemes);
            }

            @Override
            public void onError(String message) {
                isLoading.postValue(false);
                errorMessage.postValue(message);
            }
        });
    }
}
