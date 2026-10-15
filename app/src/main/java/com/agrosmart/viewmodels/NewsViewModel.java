package com.agrosmart.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.agrosmart.models.NewsItem;
import com.agrosmart.repository.AgriNewsRepository;

import java.util.List;

public class NewsViewModel extends AndroidViewModel {

    private final AgriNewsRepository repository;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>(null);
    private final MutableLiveData<List<NewsItem>> newsList = new MutableLiveData<>();
    private String currentCategory = "All";

    public NewsViewModel(@NonNull Application application) {
        super(application);
        this.repository = AgriNewsRepository.getInstance();
    }

    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<List<NewsItem>> getNewsList() { return newsList; }

    public void loadNews(String category) {
        this.currentCategory = category != null ? category : "All";
        isLoading.setValue(true);
        errorMessage.setValue(null);

        repository.getNewsByCategory(this.currentCategory, new AgriNewsRepository.NewsCallback() {
            @Override
            public void onSuccess(List<NewsItem> list) {
                isLoading.postValue(false);
                newsList.postValue(list);
            }

            @Override
            public void onError(String message) {
                isLoading.postValue(false);
                errorMessage.postValue(message);
            }
        });
    }
}
