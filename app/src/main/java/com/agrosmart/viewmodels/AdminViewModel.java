package com.agrosmart.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.agrosmart.models.AdminDashboardStats;
import com.agrosmart.repository.AdminRepository;

public class AdminViewModel extends AndroidViewModel {

    private final AdminRepository repository;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>(null);
    private final MutableLiveData<AdminDashboardStats> dashboardStats = new MutableLiveData<>(null);

    public AdminViewModel(@NonNull Application application) {
        super(application);
        this.repository = new AdminRepository();
    }

    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<AdminDashboardStats> getDashboardStats() { return dashboardStats; }

    public void fetchStats() {
        isLoading.setValue(true);
        repository.getDashboardStats(getApplication(), new AdminRepository.StatsCallback() {
            @Override
            public void onSuccess(AdminDashboardStats stats) {
                isLoading.postValue(false);
                dashboardStats.postValue(stats);
            }

            @Override
            public void onError(String message) {
                isLoading.postValue(false);
                errorMessage.postValue(message);
            }
        });
    }
}
