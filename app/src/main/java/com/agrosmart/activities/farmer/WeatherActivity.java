package com.agrosmart.activities.farmer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.agrosmart.activities.BaseActivity;
import com.agrosmart.adapters.FarmingAdvisoryAdapter;
import com.agrosmart.adapters.ForecastAdapter;
import com.agrosmart.database.AgroSmartDbHelper;
import com.agrosmart.databinding.ActivityWeatherBinding;
import com.agrosmart.models.FarmingAdvisory;
import com.agrosmart.models.ForecastItem;
import com.agrosmart.models.WeatherAdvisoryChange;
import com.agrosmart.models.WeatherResponse;
import com.agrosmart.network.ApiClient;
import com.agrosmart.utils.AgroSmartNotificationHelper;
import com.agrosmart.utils.SessionManager;
import com.agrosmart.viewmodels.WeatherViewModel;
import com.agrosmart.weather.strategy.WeatherProvider;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.IOException;
import java.util.*;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * WeatherActivity — Main User Interface for Weather Monitoring, Rain Alerts,
 * and Smart Farming Advisory (PS 5.4).
 *
 * Implements:
 * 1. 5-day weather forecast with rainfall mm, probability %, humidity, and wind speed.
 * 2. Smart Farming Advisories for Sowing, Pesticide Spraying, and Harvesting.
 * 3. 5-Day summary banner with special emphasis on heavy rain warnings.
 * 4. Advisory Alert & Forecast Change card showing previous vs. updated recommendations.
 * 5. Repeatable live hackathon demonstration scenario trigger.
 * 6. Dual-tier offline SQLite caching.
 */
public class WeatherActivity extends BaseActivity {

    private static final String TAG = "WeatherActivity";

    private ActivityWeatherBinding binding;
    private WeatherViewModel weatherViewModel;
    private ForecastAdapter forecastAdapter;
    private FarmingAdvisoryAdapter advisoryAdapter;
    private FusedLocationProviderClient fusedLocationClient;
    private SessionManager sessionManager;
    private AgroSmartDbHelper dbHelper;

    private double currentLatitude = 18.5204;
    private double currentLongitude = 73.8567;
    private String currentLocationName = "Pune, Maharashtra";
    private String currentCrop = "Cotton";

    private final ActivityResultLauncher<String[]> locationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                Boolean fineGranted = result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
                Boolean coarseGranted = result.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false);
                if ((fineGranted != null && fineGranted) || (coarseGranted != null && coarseGranted)) {
                    loadWeatherWithGps();
                } else {
                    loadSelectedCityWeather();
                }
            });

    private final ActivityResultLauncher<String> notificationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Log.i(TAG, "Notification permission granted");
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWeatherBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);
        dbHelper = AgroSmartDbHelper.getInstance(this);
        weatherViewModel = new ViewModelProvider(this).get(WeatherViewModel.class);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        setupRecyclerView();
        setupObservers();
        setupListeners();
        requestNotificationPermissionIfNeeded();

        // Check if launched from a rain alert notification deep-link
        boolean fromAlert = getIntent().getBooleanExtra(AgroSmartNotificationHelper.EXTRA_OPEN_ADVISORY_ALERT, false);
        if (fromAlert) {
            Toast.makeText(this, "⚠️ Opened from Rain Alert: Reviewing updated farming advice", Toast.LENGTH_LONG).show();
        }

        // Default immediately to saved city or Pune so UI renders immediately
        loadSelectedCityWeather();

        // Check for live GPS access
        checkLocationPermission();
    }

    private void requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
        AgroSmartNotificationHelper.createNotificationChannel(this);
    }

    private void setupRecyclerView() {
        forecastAdapter = new ForecastAdapter();
        binding.rvForecast.setLayoutManager(new LinearLayoutManager(this));
        binding.rvForecast.setAdapter(forecastAdapter);

        advisoryAdapter = new FarmingAdvisoryAdapter();
        binding.rvAdvisories.setLayoutManager(new LinearLayoutManager(this));
        binding.rvAdvisories.setAdapter(advisoryAdapter);
    }

    private void setupObservers() {
        weatherViewModel.getIsLoading().observe(this, loading -> {
            if (binding != null) {
                binding.swipeRefresh.setRefreshing(loading != null && loading);
            }
        });


        weatherViewModel.getErrorMessage().observe(this, error -> {
            if (error != null && !error.trim().isEmpty()) {
                showErrorDialog(error);
            }
        });

        weatherViewModel.getWeatherData().observe(this, weather -> {
            if (weather != null && binding != null) {
                renderWeatherData(weather);
            }
        });
    }

    private void setupListeners() {
        binding.btnBack.setOnClickListener(v -> finish());
        binding.swipeRefresh.setOnRefreshListener(this::refreshCurrentWeather);
        binding.btnChangeCity.setOnClickListener(v -> showCitySelectionDialog());
        binding.btnSimulateAlert.setOnClickListener(v -> triggerHackathonDemonstration());
    }

    /**
     * Main Weather & Advisory Fetch Flow.
     * Queries Spring Boot backend first.
     * On network failure, falls back seamlessly to offline SQLite cache.
     */
    private void refreshCurrentWeather() {
        binding.swipeRefresh.setRefreshing(true);
        String phone = sessionManager.getUserPhone();

        ApiClient.getInstance().getApi().getWeatherForecast(
                currentLatitude, currentLongitude, currentLocationName, currentCrop, phone
        ).enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                binding.swipeRefresh.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse weather = response.body();
                    renderWeatherData(weather);
                    // Cache to SQLite for offline resilience
                    dbHelper.saveWeatherForecastCache(weather);
                    if (weather.getLatestAdvisoryChange() != null) {
                        dbHelper.saveAdvisoryChange(weather.getLatestAdvisoryChange());
                    }
                } else {
                    loadFromOfflineCacheOrStrategy();
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                binding.swipeRefresh.setRefreshing(false);
                Log.w(TAG, "Backend unreachable for forecast: " + t.getMessage());
                loadFromOfflineCacheOrStrategy();
            }
        });
    }

    private void loadFromOfflineCacheOrStrategy() {
        WeatherResponse cached = dbHelper.getCachedWeatherForecast(currentLocationName);
        if (cached != null) {
            renderWeatherData(cached);
            Toast.makeText(this, "📱 Displaying offline cached weather telemetry", Toast.LENGTH_SHORT).show();
        } else {
            // Strategy Pattern fallback
            weatherViewModel.fetchWeather(currentLatitude, currentLongitude, currentLocationName);
        }
    }

    private void renderWeatherData(WeatherResponse weather) {
        binding.tvLocation.setText("📍 " + weather.getCityName());
        binding.tvMainTemp.setText(String.format(Locale.US, "%.0f°C", weather.getTemp()));
        binding.tvConditionDesc.setText(weather.getCondition() + " • " + weather.getDescription());
        binding.tvHumidityValue.setText(weather.getHumidity() + "%");
        binding.tvWindValue.setText(String.format(Locale.US, "%.1f km/h", weather.getWindSpeed()));
        binding.tvRainValue.setText(weather.getRainChance() + "%");
        binding.tvPressureValue.setText(weather.getPressure() + " hPa");

        // 5-Day Agricultural Summary Banner
        if (weather.getFiveDaySummary() != null && !weather.getFiveDaySummary().trim().isEmpty()) {
            binding.bannerFiveDaySummary.setVisibility(View.VISIBLE);
            binding.tvFiveDaySummaryText.setText(weather.getFiveDaySummary());
            if (weather.getFiveDaySummary().contains("HEAVY RAIN") || weather.getFiveDaySummary().contains("WARNING")) {
                binding.bannerFiveDaySummary.setBackgroundColor(Color.parseColor("#FFCDD2"));
                binding.tvSummaryIcon.setText("⚠️");
                binding.tvFiveDaySummaryText.setTextColor(Color.parseColor("#B71C1C"));
            } else {
                binding.bannerFiveDaySummary.setBackgroundColor(Color.parseColor("#E8F5E9"));
                binding.tvSummaryIcon.setText("🌤️");
                binding.tvFiveDaySummaryText.setTextColor(Color.parseColor("#1B5E20"));
            }
        }

        // Render Previous vs. Updated Advisory Alert Card (Requirement 8)
        WeatherAdvisoryChange change = weather.getLatestAdvisoryChange();
        if (change != null) {
            binding.cardAdvisoryChange.setVisibility(View.VISIBLE);
            binding.tvChangeActivity.setText(change.getActivity() != null ? change.getActivity() + " Activity" : "Advisory Update");
            binding.tvPrevStatus.setText("Previous: " + (change.getPreviousStatus() != null ? change.getPreviousStatus() : "Suitable"));
            binding.tvNewStatus.setText("Updated: " + (change.getUpdatedStatus() != null ? change.getUpdatedStatus() : "Avoid/Delay"));
            binding.tvChangeReason.setText("Reason: " + (change.getChangeReason() != null ? change.getChangeReason() : "Significant forecast change detected."));

            String deliveryStatus = change.getNotificationDeliveryStatus() != null ? change.getNotificationDeliveryStatus() : "DELIVERED";
            binding.tvNotifBadge.setText("DELIVERED".equalsIgnoreCase(deliveryStatus) ? "🔔 Alert Delivered" : "📱 Saved in App");
            binding.tvChangeTimestamps.setText("Retrieved: " + (change.getUpdatedRetrievedAt() != null ? change.getUpdatedRetrievedAt() : "Just now"));
        } else {
            binding.cardAdvisoryChange.setVisibility(View.GONE);
        }

        // 5-Day Forecast items
        if (weather.getForecast() != null) {
            forecastAdapter.setItems(weather.getForecast());
        }

        // Smart Farming Advisories
        if (weather.getAdvisories() != null && !weather.getAdvisories().isEmpty()) {
            advisoryAdapter.setItems(weather.getAdvisories());
        } else {
            // Generate fallback default advisories based on current weather
            List<FarmingAdvisory> defaults = generateFallbackAdvisories(weather);
            advisoryAdapter.setItems(defaults);
        }
    }

    private List<FarmingAdvisory> generateFallbackAdvisories(WeatherResponse w) {
        List<FarmingAdvisory> list = new ArrayList<>();
        boolean rainRisk = w.getRainChance() >= 50 || w.getRainfallMm() >= 15.0;
        boolean windRisk = w.getWindSpeed() >= 18.0;

        // Sowing
        list.add(new FarmingAdvisory(
                "Sowing",
                rainRisk ? "Avoid/Delay" : "Suitable",
                rainRisk ? "#D32F2F" : "#2E7D32",
                rainRisk ? "Excessive rain probability creates high waterlogging risks." : "Soil moisture and temperature are favorable.",
                String.format(Locale.US, "Rain: %.1f mm | Pop: %d%%", w.getRainfallMm(), w.getRainChance()),
                rainRisk ? "Delay sowing until field drains." : "Proceed with sowing at standard depth.",
                "Today", "Just now"
        ));

        // Spraying
        list.add(new FarmingAdvisory(
                "Pesticide Spraying",
                (rainRisk || windRisk) ? "Avoid/Delay" : "Suitable",
                (rainRisk || windRisk) ? "#D32F2F" : "#2E7D32",
                rainRisk ? "Rainfall will wash away chemical application." : (windRisk ? "Strong winds will cause spray drift." : "Calm winds ensure high spray efficacy."),
                String.format(Locale.US, "Wind: %.1f km/h | Pop: %d%%", w.getWindSpeed(), w.getRainChance()),
                (rainRisk || windRisk) ? "Postpone spraying until calm, dry window." : "Conduct foliar spraying in morning hours.",
                "Today", "Just now"
        ));

        // Harvesting
        list.add(new FarmingAdvisory(
                "Harvesting",
                rainRisk ? "Avoid/Delay" : "Suitable",
                rainRisk ? "#D32F2F" : "#2E7D32",
                rainRisk ? "Heavy rain threatens grain spoilage and field mudding." : "Dry clear weather suitable for threshing.",
                String.format(Locale.US, "Rain: %.1f mm | Pop: %d%%", w.getRainfallMm(), w.getRainChance()),
                rainRisk ? "Cover harvested heaps immediately." : "Proceed with combine harvesting and drying.",
                "Today", "Just now"
        ));

        return list;
    }

    /**
     * Hackathon Demonstration Trigger (Requirement 11).
     * Simulates an updated weather forecast introducing heavy rain on Saturday,
     * recalculates advice, logs changes, updates UI, and triggers real system notification.
     */
    private void triggerHackathonDemonstration() {
        binding.swipeRefresh.setRefreshing(true);
        Map<String, Object> payload = new HashMap<>();
        payload.put("farmerPhone", sessionManager.getUserPhone());
        payload.put("locationName", currentLocationName);
        payload.put("dayIndex", 2); // Saturday
        payload.put("simulatedRainfallMm", 48.0);
        payload.put("simulatedRainProbability", 85);
        payload.put("condition", "Heavy Rain");
        payload.put("heavyRainWarning", true);

        ApiClient.getInstance().getApi().simulateWeatherChange(payload)
                .enqueue(new Callback<Map<String, Object>>() {
                    @Override
                    public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                        binding.swipeRefresh.setRefreshing(false);
                        if (response.isSuccessful() && response.body() != null) {
                            // Fire system heads-up notification on phone
                            AgroSmartNotificationHelper.showRainAlertNotification(
                                    WeatherActivity.this,
                                    "Heavy Rain Alert – AgroSmart",
                                    "The latest forecast predicts heavy rain for Saturday (48 mm, 85% prob). Your harvesting advisory has changed to Avoid/Delay.",
                                    "Harvesting"
                            );

                            Toast.makeText(WeatherActivity.this,
                                    "⚡ Demonstration Triggered! Heavy Rain alert simulated on Saturday.",
                                    Toast.LENGTH_LONG).show();

                            // Reload forecast to render updated change banner immediately
                            refreshCurrentWeather();
                        } else {
                            runLocalSimulationFallback();
                        }
                    }

                    @Override
                    public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                        binding.swipeRefresh.setRefreshing(false);
                        runLocalSimulationFallback();
                    }
                });
    }

    private void runLocalSimulationFallback() {
        // Local simulation fallback if backend server is not running during presentation
        AgroSmartNotificationHelper.showRainAlertNotification(
                this,
                "Heavy Rain Alert – AgroSmart",
                "The latest forecast predicts heavy rain for Saturday (48 mm, 85% prob). Your harvesting advisory has changed to Avoid/Delay.",
                "Harvesting"
        );

        binding.cardAdvisoryChange.setVisibility(View.VISIBLE);
        binding.tvChangeActivity.setText("🌾 Harvesting Activity");
        binding.tvPrevStatus.setText("Previous: Suitable");
        binding.tvNewStatus.setText("Updated: Avoid/Delay");
        binding.tvChangeReason.setText("Reason: Heavy rain warning introduced for Saturday (48.0 mm expected, 85% probability). Advisory shifted from Suitable to Avoid/Delay.");
        binding.tvNotifBadge.setText("🔔 Alert Delivered");
        binding.tvChangeTimestamps.setText("Prev: 12 hrs ago • Updated: Just now");

        binding.bannerFiveDaySummary.setVisibility(View.VISIBLE);
        binding.bannerFiveDaySummary.setBackgroundColor(Color.parseColor("#FFCDD2"));
        binding.tvSummaryIcon.setText("⚠️");
        binding.tvFiveDaySummaryText.setText("⚠️ HEAVY RAIN WARNING: Severe rainfall predicted on Saturday (48.0 mm). Sowing and harvesting operations should be paused.");
        binding.tvFiveDaySummaryText.setTextColor(Color.parseColor("#B71C1C"));

        // Update harvesting card in list
        List<FarmingAdvisory> advisories = new ArrayList<>();
        advisories.add(new FarmingAdvisory("Sowing", "Avoid/Delay", "#D32F2F",
                "Heavy rain forecast (48.0 mm, 85% probability). Waterlogging and severe seed washout risk will hinder germination.",
                "Rain: 48.0 mm | Pop: 85%", "Postpone sowing until heavy rains subside.", "Saturday", "Just now"));
        advisories.add(new FarmingAdvisory("Pesticide Spraying", "Avoid/Delay", "#D32F2F",
                "Precipitation predicted (48.0 mm, 85% chance). Rain will wash away active chemical ingredients.",
                "Rain: 48.0 mm | Pop: 85% | Wind: 24.5 km/h", "Halt spraying operations.", "Saturday", "Just now"));
        advisories.add(new FarmingAdvisory("Harvesting", "Avoid/Delay", "#D32F2F",
                "Severe rainfall threat (48.0 mm, 85% probability). Wet standing crop risks grain shattering and mold.",
                "Rain: 48.0 mm | Pop: 85% | Risk: HIGH", "Do not cut standing crop. Shelter existing harvested piles.", "Saturday", "Just now"));

        advisoryAdapter.setItems(advisories);

        Toast.makeText(this, "⚡ Local Demonstration Active: Heavy rain warning simulated!", Toast.LENGTH_LONG).show();
    }

    private void showErrorDialog(String errorMessage) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Weather Retrieval Issue")
                .setMessage(errorMessage)
                .setPositiveButton("Retry", (dialog, which) -> refreshCurrentWeather())
                .setNegativeButton("Close", null)
                .show();
    }

    private void showCitySelectionDialog() {
        String[] cities = {
                "Pune, Maharashtra (Default)",
                "Mumbai, Maharashtra",
                "Nashik, Maharashtra",
                "Nagpur, Maharashtra",
                "Chhatrapati Sambhajinagar, Maharashtra",
                "📡 Detect My Live GPS Location"
        };

        new MaterialAlertDialogBuilder(this)
                .setTitle("Select Weather Location")
                .setItems(cities, (dialog, which) -> {
                    if (which == 0) {
                        selectCity("Pune, Maharashtra", 18.5204, 73.8567);
                    } else if (which == 1) {
                        selectCity("Mumbai, Maharashtra", 19.0760, 72.8777);
                    } else if (which == 2) {
                        selectCity("Nashik, Maharashtra", 19.9975, 73.7898);
                    } else if (which == 3) {
                        selectCity("Nagpur, Maharashtra", 21.1458, 79.0882);
                    } else if (which == 4) {
                        selectCity("Chhatrapati Sambhajinagar, Maharashtra", 19.8762, 75.3433);
                    } else if (which == 5) {
                        checkLocationPermission();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void selectCity(String cityName, double lat, double lon) {
        currentLatitude = lat;
        currentLongitude = lon;
        currentLocationName = cityName;
        sessionManager.setSelectedCity(cityName);
        refreshCurrentWeather();
        Toast.makeText(this, "Location set to: " + cityName, Toast.LENGTH_SHORT).show();
    }

    private void loadSelectedCityWeather() {
        String savedCity = sessionManager.getSelectedCity();
        if (savedCity != null && savedCity.contains("Mumbai")) {
            selectCity("Mumbai, Maharashtra", 19.0760, 72.8777);
        } else if (savedCity != null && savedCity.contains("Nashik")) {
            selectCity("Nashik, Maharashtra", 19.9975, 73.7898);
        } else if (savedCity != null && savedCity.contains("Nagpur")) {
            selectCity("Nagpur, Maharashtra", 21.1458, 79.0882);
        } else {
            selectCity("Pune, Maharashtra", 18.5204, 73.8567);
        }
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            loadWeatherWithGps();
        } else {
            locationPermissionLauncher.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
        }
    }

    private void loadWeatherWithGps() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                currentLatitude = location.getLatitude();
                currentLongitude = location.getLongitude();
                resolveLocationName(currentLatitude, currentLongitude);
            } else {
                refreshCurrentWeather();
            }
        }).addOnFailureListener(e -> refreshCurrentWeather());
    }

    private void resolveLocationName(double lat, double lon) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                String locality = address.getLocality();
                String adminArea = address.getAdminArea();
                if (locality != null && adminArea != null) {
                    currentLocationName = locality + ", " + adminArea;
                } else if (locality != null) {
                    currentLocationName = locality;
                } else if (adminArea != null) {
                    currentLocationName = adminArea;
                } else {
                    currentLocationName = String.format(Locale.US, "Lat: %.2f, Lon: %.2f", lat, lon);
                }
            }
        } catch (IOException ignored) {}
        refreshCurrentWeather();
    }
}
