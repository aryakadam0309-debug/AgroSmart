package com.agrosmart.repository;

import android.content.Context;

import com.agrosmart.models.WeatherResponse;
import com.agrosmart.weather.strategy.LiveApiWeatherProvider;
import com.agrosmart.weather.strategy.MockWeatherProvider;
import com.agrosmart.weather.strategy.WeatherProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * WeatherRepository acts as the Strategy Context in the Strategy Design Pattern.
 * It maintains a reference to an interchangeable {@link WeatherProvider} strategy
 * and delegates retrieval tasks to the currently active strategy.
 *
 * This design enables runtime switching between real-time network radar telemetry
 * and deterministic offline simulation models without modifying client layers.
 */
public class WeatherRepository {

    private final Context context;
    private WeatherProvider activeStrategy;
    private final List<WeatherProvider> availableStrategies = new ArrayList<>();

    public interface WeatherCallback extends WeatherProvider.WeatherCallback {
        @Override
        void onSuccess(WeatherResponse weather);

        @Override
        void onError(String message);
    }

    public WeatherRepository(Context context) {
        this.context = context;
        initStrategies();
    }

    /**
     * Initializes the supported strategy implementations.
     * Strategy 1: Live Satellite API (Open-Meteo)
     * Strategy 2: Offline Farm Simulation (Academic Demo Mode)
     */
    private void initStrategies() {
        LiveApiWeatherProvider liveProvider = new LiveApiWeatherProvider();
        MockWeatherProvider mockProvider = new MockWeatherProvider();

        availableStrategies.add(liveProvider);
        availableStrategies.add(mockProvider);

        // Default to Live API for real telemetry, switchable anytime
        this.activeStrategy = liveProvider;
    }

    /**
     * Sets the active weather retrieval strategy (Strategy Pattern Context method).
     *
     * @param strategy The new strategy implementation to use.
     */
    public void setStrategy(WeatherProvider strategy) {
        if (strategy != null) {
            this.activeStrategy = strategy;
        }
    }

    /**
     * Convenience method to select strategy by ID.
     */
    public void setStrategyById(String providerId) {
        for (WeatherProvider provider : availableStrategies) {
            if (provider.getProviderId().equalsIgnoreCase(providerId)) {
                this.activeStrategy = provider;
                return;
            }
        }
    }

    /**
     * Gets the currently active weather retrieval strategy.
     */
    public WeatherProvider getActiveProvider() {
        return activeStrategy;
    }

    /**
     * Lists all available strategy implementations for UI selection.
     */
    public List<WeatherProvider> getAvailableStrategies() {
        return new ArrayList<>(availableStrategies);
    }

    /**
     * Delegates weather retrieval to the active strategy.
     *
     * @param lat Latitude of target farm location
     * @param lon Longitude of target farm location
     * @param locationName Formatted city / district name
     * @param callback Callback notified on result or failure
     */
    public void getWeather(double lat, double lon, String locationName, WeatherProvider.WeatherCallback callback) {
        if (activeStrategy != null) {
            activeStrategy.fetchWeather(lat, lon, locationName, callback);
        } else if (callback != null) {
            callback.onError("No weather provider strategy configured in repository.");
        }
    }
}
