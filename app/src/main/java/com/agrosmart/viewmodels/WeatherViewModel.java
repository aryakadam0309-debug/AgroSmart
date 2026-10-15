package com.agrosmart.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.agrosmart.models.WeatherResponse;
import com.agrosmart.repository.WeatherRepository;
import com.agrosmart.weather.strategy.WeatherProvider;

import java.util.List;

/**
 * ViewModel for the Weather Forecast Module.
 * Mediates between the UI layer (WeatherActivity) and the data layer (WeatherRepository).
 * Exposes observable LiveData streams for weather data, loading progress, error feedback,
 * and active strategy state.
 */
public class WeatherViewModel extends AndroidViewModel {

    private final WeatherRepository repository;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>(null);
    private final MutableLiveData<WeatherResponse> weatherData = new MutableLiveData<>(null);
    private final MutableLiveData<WeatherProvider> activeStrategyLiveData = new MutableLiveData<>();

    public WeatherViewModel(@NonNull Application application) {
        super(application);
        this.repository = new WeatherRepository(application);
        this.activeStrategyLiveData.setValue(this.repository.getActiveProvider());
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<WeatherResponse> getWeatherData() {
        return weatherData;
    }

    public LiveData<WeatherProvider> getActiveStrategy() {
        return activeStrategyLiveData;
    }

    /**
     * Changes the active weather provider strategy at runtime.
     *
     * @param provider The new strategy implementation to switch to.
     */
    public void setStrategy(WeatherProvider provider) {
        if (provider != null) {
            repository.setStrategy(provider);
            activeStrategyLiveData.setValue(provider);
        }
    }

    /**
     * Changes the active strategy by provider ID.
     */
    public void setStrategyById(String providerId) {
        repository.setStrategyById(providerId);
        activeStrategyLiveData.setValue(repository.getActiveProvider());
    }

    public WeatherProvider getCurrentStrategy() {
        return repository.getActiveProvider();
    }

    public List<WeatherProvider> getAvailableStrategies() {
        return repository.getAvailableStrategies();
    }

    /**
     * Requests weather data from the repository using the current active strategy.
     *
     * @param lat Latitude
     * @param lon Longitude
     * @param locationName Location display name
     */
    public void fetchWeather(double lat, double lon, String locationName) {
        isLoading.setValue(true);
        errorMessage.setValue(null);

        repository.getWeather(lat, lon, locationName, new WeatherProvider.WeatherCallback() {
            @Override
            public void onSuccess(WeatherResponse weather) {
                isLoading.postValue(false);
                weatherData.postValue(weather);
            }

            @Override
            public void onError(String message) {
                isLoading.postValue(false);
                errorMessage.postValue(message);
            }
        });
    }
}
