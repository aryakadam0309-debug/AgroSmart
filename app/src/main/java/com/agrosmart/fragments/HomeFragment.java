package com.agrosmart.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.agrosmart.databinding.FragmentHomeBinding;
import com.agrosmart.models.WeatherResponse;
import com.agrosmart.repository.IotRepository;
import com.agrosmart.repository.WeatherRepository;
import com.agrosmart.utils.SessionManager;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private SessionManager sessionManager;
    private WeatherRepository weatherRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sessionManager = new SessionManager(requireContext());
        weatherRepository = new WeatherRepository(requireContext());

        binding.tvFarmerName.setText(sessionManager.getUserName());

        // Setup My Farm Today briefing date & season
        setupMyFarmToday();

        // Navigation click listeners
        binding.cardMyFarmToday.setOnClickListener(v -> openFeature("Crop Planner", "com.agrosmart.activities.farmer.CropRecommendationActivity"));
        binding.cardWeather.setOnClickListener(v -> openFeature("Weather Forecast", "com.agrosmart.activities.farmer.WeatherActivity"));
        binding.cardIotSmartFarming.setOnClickListener(v -> openFeature("IoT Smart Farming", "com.agrosmart.activities.farmer.IotDashboardActivity"));
        binding.cardCropPrices.setOnClickListener(v -> openFeature("Crop Prices", "com.agrosmart.activities.farmer.CropPriceActivity"));
        binding.cardSchemes.setOnClickListener(v -> openFeature("Govt Schemes", "com.agrosmart.activities.farmer.SchemeActivity"));
        binding.cardCropGuide.setOnClickListener(v -> openFeature("Crop Guide", "com.agrosmart.activities.farmer.CropGuideActivity"));
        binding.cardDiseaseInfo.setOnClickListener(v -> openFeature("Crop Diseases", "com.agrosmart.activities.farmer.CropDiseaseActivity"));
        binding.cardCropRecommendation.setOnClickListener(v -> openFeature("Crop Recommendation", "com.agrosmart.activities.farmer.CropRecommendationActivity"));
        binding.cardNews.setOnClickListener(v -> openFeature("Agriculture News", "com.agrosmart.activities.farmer.NewsActivity"));
        binding.ivNotification.setOnClickListener(v -> openFeature("Notifications", "com.agrosmart.activities.farmer.NotificationsActivity"));
    }

    private void setupMyFarmToday() {
        if (binding == null) return;
        LocalDate now = LocalDate.now();
        int month = now.getMonthValue();
        String seasonName;
        String stageName;
        String advisory;

        if (month >= 6 && month <= 10) {
            seasonName = "Kharif Season (Monsoon)";
            stageName = "Vegetative & Flowering Window";
            advisory = "Monitor soil moisture closely; ensure balanced nitrogen-phosphorus top dressing.";
        } else if (month >= 11 || month <= 2) {
            seasonName = "Rabi Season (Winter)";
            stageName = "Wheat & Gram Sowing / Tillering";
            advisory = "Optimal cold window for wheat irrigation and pod-borer scouting on chickpea.";
        } else {
            seasonName = "Zaid Season (Summer)";
            stageName = "Interim Groundnut / Vegetable Phase";
            advisory = "Ensure drip irrigation or light evening watering to prevent heat stress.";
        }

        binding.tvFarmDate.setText(now.format(DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.getDefault())).toUpperCase());
        binding.tvFarmSeason.setText(seasonName + " • " + stageName);
        binding.tvDailyCropAdvisory.setText(advisory);
        binding.tvMandiPulse.setText("Soybean: ₹4,850/Qtl (Pune APMC) • Wheat: ₹2,420/Qtl • Onion: ₹2,100/Qtl");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (binding != null && sessionManager != null) {
            binding.tvFarmerName.setText(sessionManager.getUserName());
        }
        setupMyFarmToday();
        loadLiveWeather();
        loadIotStatus();
    }

    private void loadLiveWeather() {
        if (binding == null) return;
        String city = sessionManager.getSelectedCity();
        weatherRepository.getWeather(0, 0, city, new WeatherRepository.WeatherCallback() {
            @Override
            public void onSuccess(WeatherResponse weather) {
                if (binding != null) {
                    binding.tvWeatherLocation.setText(weather.getLocation());
                    binding.tvWeatherTemp.setText(String.format(Locale.getDefault(), "%.1f°C • %s", weather.getCurrentTemp(), weather.getCondition()));
                    binding.tvHumidity.setText("💧 Humidity: " + weather.getHumidity() + "%");
                    binding.tvWind.setText(String.format(Locale.getDefault(), "💨 Wind: %.0f km/h", weather.getWindSpeed()));
                    binding.tvRain.setText("🌧️ Rain: " + weather.getRainChance() + "%");

                    // Dynamic agronomic precaution based on live weather
                    if (weather.getRainChance() > 40) {
                        binding.tvWeatherAlertPrecaution.setText("🌧️ High rain chance (" + weather.getRainChance() + "%): Defer foliar pesticide spray & clear drainage furrows.");
                    } else if (weather.getHumidity() > 75) {
                        binding.tvWeatherAlertPrecaution.setText("⚠️ High humidity (" + weather.getHumidity() + "%): Watch for fungal rust & downy mildew. Ensure field aeration.");
                    } else if (weather.getCurrentTemp() > 34) {
                        binding.tvWeatherAlertPrecaution.setText("☀️ High temperature (" + String.format(Locale.getDefault(), "%.0f", weather.getCurrentTemp()) + "°C): Water during evening hours to minimize evaporation losses.");
                    } else {
                        binding.tvWeatherAlertPrecaution.setText("🌤️ Favorable microclimate: Suitable for weeding, fertilizer top-dressing and field inspection.");
                    }
                }
            }

            @Override
            public void onError(String message) {
                if (binding != null) {
                    binding.tvWeatherLocation.setText(sessionManager.getSelectedCity());
                }
            }
        });
    }

    private void loadIotStatus() {
        if (binding == null) return;
        IotRepository iotRepo = IotRepository.getInstance(requireContext());
        Boolean isOnline = iotRepo.getIsOnlineLiveData().getValue();
        if (Boolean.TRUE.equals(isOnline)) {
            binding.tvIotBadge.setText("ESP32 LIVE");
            iotRepo.getLatestReadingLiveData().observe(getViewLifecycleOwner(), reading -> {
                if (reading != null && binding != null) {
                    binding.tvIotMoisture.setText(String.format(Locale.US, "🌱 Soil Moisture: %.1f%%", reading.getSoilMoisturePct()));
                    if (reading.getTemperatureC() != null) {
                        binding.tvIotTemp.setText(String.format(Locale.US, "⛅ Temp: %.1f°C", reading.getTemperatureC()));
                    }
                }
            });
        } else {
            binding.tvIotBadge.setText("HARDWARE OFFLINE");
            binding.tvIotMoisture.setText("🌱 Soil Moisture: -- %");
            binding.tvIotTemp.setText("⛅ Temp: -- °C");
            
        }
    }

    private void openFeature(String featureName, String className) {
        try {
            Class<?> target = Class.forName(className);
            startActivity(new Intent(requireContext(), target));
        } catch (ClassNotFoundException e) {
            Toast.makeText(requireContext(), featureName + " module ready in upcoming phase", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
