package com.agrosmart.weather.strategy;

import com.agrosmart.models.WeatherResponse;

/**
 * Strategy Interface for the Weather Forecast Module.
 * Defines the contract for weather data retrieval algorithms.
 * Allows switching between live network sources and offline demonstration models
 * dynamically at runtime, adhering to the Open/Closed Principle (OCP).
 */
public interface WeatherProvider {

    /**
     * Unique machine-readable identifier for the provider.
     *
     * @return provider ID string
     */
    String getProviderId();

    /**
     * Human-readable display label for the user interface and evaluation demo.
     *
     * @return display title
     */
    String getDisplayName();

    /**
     * Indicates whether this strategy serves simulated test/demo data.
     * Used by the presentation layer to display explicit demo badges.
     *
     * @return true if simulated, false if live network data
     */
    boolean isDemoMode();

    /**
     * Asynchronously fetches weather data for the specified geographical coordinates.
     *
     * @param lat          Latitude of target location
     * @param lon          Longitude of target location
     * @param locationName Descriptive city or district name
     * @param callback     Callback to notify on completion or failure
     */
    void fetchWeather(double lat, double lon, String locationName, WeatherCallback callback);

    /**
     * Asynchronous callback interface for weather retrieval.
     */
    interface WeatherCallback {
        void onSuccess(WeatherResponse weather);
        void onError(String errorMessage);
    }
}
