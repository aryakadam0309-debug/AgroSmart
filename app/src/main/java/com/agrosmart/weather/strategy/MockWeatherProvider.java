package com.agrosmart.weather.strategy;

import android.os.Handler;
import android.os.Looper;

import com.agrosmart.models.ForecastItem;
import com.agrosmart.models.WeatherResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Concrete Strategy A: Offline Farm Simulation Provider.
 * Provides deterministic, localized agro-meteorological test data for
 * Maharashtra farming belts (Mumbai, Pune, Nashik, Nagpur, etc.).
 *
 * Explicitly flagged as Demo Mode so that simulated data is never
 * confused with live satellite radar measurements.
 */
public class MockWeatherProvider implements WeatherProvider {

    public static final String PROVIDER_ID = "mock_simulation";
    public static final String DISPLAY_NAME = "🧪 Offline Simulation (Demo Mode)";

    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    public String getProviderId() {
        return PROVIDER_ID;
    }

    @Override
    public String getDisplayName() {
        return DISPLAY_NAME;
    }

    @Override
    public boolean isDemoMode() {
        return true;
    }

    @Override
    public void fetchWeather(double lat, double lon, String locationName, WeatherCallback callback) {
        String city = (locationName != null && !locationName.trim().isEmpty()) ? locationName : "Mumbai, Maharashtra";
        String lowerCity = city.toLowerCase();

        WeatherResponse response;
        List<ForecastItem> forecast = new ArrayList<>();

        if (lowerCity.contains("mumbai")) {
            response = new WeatherResponse(
                    "Mumbai, Maharashtra",
                    31.5,   // Current Temp
                    36.0,   // Feels like
                    26.0,   // Min Temp
                    33.5,   // Max Temp
                    "Warm & Humid [DEMO]",
                    "Coastal humid climate. Ensure good aeration for polyhouses and harvested storage godowns.",
                    74,     // Humidity %
                    18.5,   // Wind km/h
                    1009,   // Pressure hPa
                    20      // Rain Chance %
            );
            forecast.add(new ForecastItem("Today", "Day 1", 31.5, 26.0, 33.5, "Partly Cloudy", "02d"));
            forecast.add(new ForecastItem("Tomorrow", "Day 2", 32.0, 26.5, 34.0, "Sunny", "01d"));
            forecast.add(new ForecastItem("Day 3", "Day 3", 31.0, 25.8, 33.0, "Hazy Sunshine", "02d"));
            forecast.add(new ForecastItem("Day 4", "Day 4", 30.5, 25.0, 32.5, "Passing Clouds", "03d"));
            forecast.add(new ForecastItem("Day 5", "Day 5", 31.0, 25.5, 33.0, "Clear Sky", "01d"));
        } else if (lowerCity.contains("pune")) {
            response = new WeatherResponse(
                    "Pune, Maharashtra",
                    28.0,
                    28.5,
                    20.0,
                    31.0,
                    "Pleasant & Clear [DEMO]",
                    "Mild climate. Ideal conditions for foliar nutrient spraying and crop inspection.",
                    58,
                    12.0,
                    1013,
                    10
            );
            forecast.add(new ForecastItem("Today", "Day 1", 28.0, 20.0, 31.0, "Clear Sky", "01d"));
            forecast.add(new ForecastItem("Tomorrow", "Day 2", 28.5, 20.5, 31.5, "Sunny", "01d"));
            forecast.add(new ForecastItem("Day 3", "Day 3", 27.5, 19.8, 30.0, "Partly Cloudy", "02d"));
            forecast.add(new ForecastItem("Day 4", "Day 4", 29.0, 20.0, 32.0, "Sunny", "01d"));
            forecast.add(new ForecastItem("Day 5", "Day 5", 28.0, 20.2, 31.0, "Clear Sky", "01d"));
        } else if (lowerCity.contains("nashik")) {
            response = new WeatherResponse(
                    "Nashik, Maharashtra",
                    27.5,
                    28.0,
                    18.5,
                    30.0,
                    "Mild Breeze [DEMO]",
                    "Cool mornings. Favorable window for vineyard canopy management and onion harvesting.",
                    60,
                    14.0,
                    1014,
                    15
            );
            forecast.add(new ForecastItem("Today", "Day 1", 27.5, 18.5, 30.0, "Sunny", "01d"));
            forecast.add(new ForecastItem("Tomorrow", "Day 2", 27.0, 18.0, 29.5, "Partly Cloudy", "02d"));
            forecast.add(new ForecastItem("Day 3", "Day 3", 26.5, 17.5, 29.0, "Clear Sky", "01d"));
            forecast.add(new ForecastItem("Day 4", "Day 4", 28.0, 18.2, 30.5, "Sunny", "01d"));
            forecast.add(new ForecastItem("Day 5", "Day 5", 27.5, 18.0, 30.0, "Sunny", "01d"));
        } else if (lowerCity.contains("nagpur")) {
            response = new WeatherResponse(
                    "Nagpur, Maharashtra",
                    34.0,
                    36.5,
                    24.0,
                    37.0,
                    "Hot & Dry [DEMO]",
                    "High evapotranspiration zone. Schedule drip irrigation for orange orchards during evening hours.",
                    42,
                    16.0,
                    1008,
                    5
            );
            forecast.add(new ForecastItem("Today", "Day 1", 34.0, 24.0, 37.0, "Clear Sky", "01d"));
            forecast.add(new ForecastItem("Tomorrow", "Day 2", 34.5, 24.5, 37.5, "Sunny", "01d"));
            forecast.add(new ForecastItem("Day 3", "Day 3", 35.0, 25.0, 38.0, "Hot Sunshine", "01d"));
            forecast.add(new ForecastItem("Day 4", "Day 4", 33.5, 23.8, 36.5, "Passing Clouds", "02d"));
            forecast.add(new ForecastItem("Day 5", "Day 5", 34.0, 24.0, 37.0, "Clear Sky", "01d"));
        } else {
            // General or GPS-detected location fallback
            response = new WeatherResponse(
                    city + " [Simulated]",
                    29.5,
                    31.0,
                    22.0,
                    32.5,
                    "Partly Cloudy [DEMO]",
                    "Favorable regional weather. Monitor soil moisture sensor levels before running irrigation pump.",
                    65,
                    15.0,
                    1011,
                    15
            );
            forecast.add(new ForecastItem("Today", "Day 1", 29.5, 22.0, 32.5, "Partly Cloudy", "02d"));
            forecast.add(new ForecastItem("Tomorrow", "Day 2", 30.0, 22.5, 33.0, "Sunny", "01d"));
            forecast.add(new ForecastItem("Day 3", "Day 3", 29.0, 21.8, 32.0, "Passing Clouds", "03d"));
            forecast.add(new ForecastItem("Day 4", "Day 4", 28.5, 21.0, 31.5, "Clear Sky", "01d"));
            forecast.add(new ForecastItem("Day 5", "Day 5", 29.0, 21.5, 32.0, "Sunny", "01d"));
        }

        response.setForecast(forecast);

        if (callback != null) {
            callback.onSuccess(response);
        }
    }
}
