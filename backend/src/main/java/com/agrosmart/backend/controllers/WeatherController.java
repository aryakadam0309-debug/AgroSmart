package com.agrosmart.backend.controllers;

import com.agrosmart.backend.entities.DeviceToken;
import com.agrosmart.backend.entities.WeatherAdvisoryChange;
import com.agrosmart.backend.entities.WeatherForecastSnapshot;
import com.agrosmart.backend.payload.FarmingAdvisoryDto;
import com.agrosmart.backend.payload.SimulateChangeRequest;
import com.agrosmart.backend.payload.WeatherForecastDto;
import com.agrosmart.backend.repositories.WeatherAdvisoryChangeRepository;
import com.agrosmart.backend.services.FarmingAdvisoryEngine;
import com.agrosmart.backend.services.ForecastMonitoringService;
import com.agrosmart.backend.services.PushNotificationService;
import com.agrosmart.backend.services.WeatherApiClientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/weather")
@CrossOrigin(origins = "*")
public class WeatherController {

    private final WeatherApiClientService weatherApiClientService;
    private final FarmingAdvisoryEngine advisoryEngine;
    private final ForecastMonitoringService forecastMonitoringService;
    private final WeatherAdvisoryChangeRepository advisoryChangeRepository;
    private final PushNotificationService pushNotificationService;

    public WeatherController(WeatherApiClientService weatherApiClientService,
                             FarmingAdvisoryEngine advisoryEngine,
                             ForecastMonitoringService forecastMonitoringService,
                             WeatherAdvisoryChangeRepository advisoryChangeRepository,
                             PushNotificationService pushNotificationService) {
        this.weatherApiClientService = weatherApiClientService;
        this.advisoryEngine = advisoryEngine;
        this.forecastMonitoringService = forecastMonitoringService;
        this.advisoryChangeRepository = advisoryChangeRepository;
        this.pushNotificationService = pushNotificationService;
    }

    /**
     * Main 5-Day Weather Forecast & Smart Farming Advisory Endpoint.
     * Retains OpenWeather / satellite API keys on backend, returning comprehensive farm metrics.
     */
    @GetMapping("/forecast")
    public ResponseEntity<WeatherForecastDto> getForecast(
            @RequestParam(defaultValue = "18.5204") double lat,
            @RequestParam(defaultValue = "73.8567") double lon,
            @RequestParam(defaultValue = "Pune, Maharashtra") String location,
            @RequestParam(defaultValue = "Cotton") String crop,
            @RequestParam(required = false) String phone) {

        String farmerPhone = (phone != null && !phone.trim().isEmpty()) ? phone.trim() : "9876543210";

        List<WeatherForecastSnapshot> snapshots = weatherApiClientService.fetchFiveDayForecast(lat, lon, location, farmerPhone);

        // Run automated change monitoring against previous snapshots
        forecastMonitoringService.processAndMonitorForecast(farmerPhone, location, snapshots, crop);

        WeatherForecastDto dto = new WeatherForecastDto();
        dto.setCityName(location);
        dto.setRetrievedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")));

        if (!snapshots.isEmpty()) {
            WeatherForecastSnapshot today = snapshots.get(0);
            dto.setCurrentTemp(today.getTemp());
            dto.setFeelsLike(today.getTemp() + 1.2);
            dto.setTempMin(today.getMinTemp());
            dto.setTempMax(today.getMaxTemp());
            dto.setCondition(today.getCondition());
            dto.setDescription(today.getConditionDescription());
            dto.setHumidity(today.getHumidity());
            dto.setWindSpeed(today.getWindSpeed());
            dto.setRainChance(today.getRainProbability());
            dto.setRainfallMm(today.getExpectedRainfallMm());
            dto.setPressure(1012);
            dto.setOverallRiskLevel(today.getRiskLevel());
            dto.setProvider(today.getProvider());

            // 5-Day Agricultural Summary
            boolean upcomingHeavyRain = snapshots.stream().anyMatch(WeatherForecastSnapshot::isHeavyRain);
            if (upcomingHeavyRain) {
                Optional<WeatherForecastSnapshot> heavyDay = snapshots.stream().filter(WeatherForecastSnapshot::isHeavyRain).findFirst();
                dto.setFiveDaySummary(String.format("⚠️ HEAVY RAIN WARNING: Severe rainfall predicted on %s (%.1f mm). Sowing and harvesting operations should be paused.",
                        heavyDay.get().getDayName(), heavyDay.get().getExpectedRainfallMm()));
            } else {
                dto.setFiveDaySummary("🟢 NORMAL CONDITIONS: Weather remains favorable for standard farming operations over the next 5 days.");
            }

            // Generate Smart Farming Advisories for today's forecast
            List<FarmingAdvisoryDto> advisories = advisoryEngine.generateAdvisories(today, crop);
            dto.setAdvisories(advisories);
        }

        dto.setForecast(snapshots);

        // Attach latest previous vs. updated advisory change if one exists
        Optional<WeatherAdvisoryChange> latestChange = advisoryChangeRepository.findTopByFarmerPhoneOrderByDetectedAtDesc(farmerPhone);
        latestChange.ifPresent(dto::setLatestAdvisoryChange);

        return ResponseEntity.ok(dto);
    }

    /**
     * Standalone Farming Advisories for Sowing, Spraying, and Harvesting.
     */
    @GetMapping("/advisories")
    public ResponseEntity<List<FarmingAdvisoryDto>> getAdvisories(
            @RequestParam(defaultValue = "18.5204") double lat,
            @RequestParam(defaultValue = "73.8567") double lon,
            @RequestParam(defaultValue = "Pune, Maharashtra") String location,
            @RequestParam(defaultValue = "Cotton") String crop,
            @RequestParam(required = false) String phone) {

        String farmerPhone = (phone != null && !phone.trim().isEmpty()) ? phone.trim() : "9876543210";
        List<WeatherForecastSnapshot> snapshots = weatherApiClientService.fetchFiveDayForecast(lat, lon, location, farmerPhone);

        if (snapshots.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        WeatherForecastSnapshot currentDay = snapshots.get(0);
        return ResponseEntity.ok(advisoryEngine.generateAdvisories(currentDay, crop));
    }

    /**
     * Retrieves historical previous-versus-current advisory changes for an authenticated farmer.
     */
    @GetMapping("/advisory-changes")
    public ResponseEntity<List<WeatherAdvisoryChange>> getAdvisoryChanges(@RequestParam(required = false) String phone) {
        String farmerPhone = (phone != null && !phone.trim().isEmpty()) ? phone.trim() : "9876543210";
        return ResponseEntity.ok(advisoryChangeRepository.findByFarmerPhoneOrderByDetectedAtDesc(farmerPhone));
    }

    /**
     * Live Hackathon Demonstration Endpoint (Requirement 11).
     * Simulates an updated weather forecast introducing heavy rain for a specific day,
     * triggers change detection, recalculates advice, logs changes, and fires push notification.
     */
    @PostMapping("/simulate-change")
    public ResponseEntity<Map<String, Object>> simulateChange(@RequestBody(required = false) SimulateChangeRequest request) {
        if (request == null) {
            request = new SimulateChangeRequest();
        }
        Map<String, Object> result = forecastMonitoringService.simulateForecastChange(request);
        return ResponseEntity.ok(result);
    }

    /**
     * Registers a device push notification token for a farmer.
     */
    @PostMapping("/device-token")
    public ResponseEntity<DeviceToken> registerToken(@RequestBody Map<String, String> payload) {
        String phone = payload.getOrDefault("phone", "9876543210");
        String token = payload.get("token");
        String model = payload.getOrDefault("deviceModel", "Android Device");

        DeviceToken dt = pushNotificationService.registerDeviceToken(phone, token, model);
        return ResponseEntity.ok(dt);
    }
}
