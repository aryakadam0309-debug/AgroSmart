package com.agrosmart.backend.services;

import com.agrosmart.backend.entities.User;
import com.agrosmart.backend.entities.WeatherForecastSnapshot;
import com.agrosmart.backend.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Scheduled monitoring job (Requirement 6).
 * Automatically polls weather forecast updates at configurable intervals,
 * compares against previous forecasts, and triggers alerts on significant changes.
 */
@Component
@ConditionalOnProperty(name = "weather.monitoring.enabled", havingValue = "true", matchIfMissing = true)
public class ForecastMonitoringScheduler {

    private static final Logger logger = LoggerFactory.getLogger(ForecastMonitoringScheduler.class);

    private final UserRepository userRepository;
    private final WeatherApiClientService weatherApiClientService;
    private final ForecastMonitoringService forecastMonitoringService;

    public ForecastMonitoringScheduler(UserRepository userRepository,
                                       WeatherApiClientService weatherApiClientService,
                                       ForecastMonitoringService forecastMonitoringService) {
        this.userRepository = userRepository;
        this.weatherApiClientService = weatherApiClientService;
        this.forecastMonitoringService = forecastMonitoringService;
    }

    /**
     * Executes forecast refresh every 15 minutes (or as configured via weather.monitoring.fixed-rate-ms).
     * Avoids duplicate notifications by delegating to ForecastMonitoringService.
     */
    @Scheduled(fixedRateString = "${weather.monitoring.fixed-rate-ms:900000}", initialDelay = 60000)
    public void executeForecastMonitoringCycle() {
        logger.info("Starting automated weather forecast monitoring cycle...");

        List<User> farmers = userRepository.findAll();
        if (farmers.isEmpty()) {
            logger.info("No registered farmers found for weather monitoring.");
            return;
        }

        int monitoredCount = 0;
        for (User farmer : farmers) {
            String phone = farmer.getPhone();
            if (phone == null || phone.trim().isEmpty()) continue;

            String district = farmer.getDistrict() != null ? farmer.getDistrict() : "Pune";
            String state = farmer.getState() != null ? farmer.getState() : "Maharashtra";
            String locationName = district + ", " + state;

            // Default farm coordinates based on district
            double lat = 18.5204;
            double lon = 73.8567;
            if (district.toLowerCase().contains("mumbai")) {
                lat = 19.0760; lon = 72.8777;
            } else if (district.toLowerCase().contains("nashik")) {
                lat = 19.9975; lon = 73.7898;
            } else if (district.toLowerCase().contains("nagpur")) {
                lat = 21.1458; lon = 79.0882;
            }

            try {
                List<WeatherForecastSnapshot> newSnapshots = weatherApiClientService.fetchFiveDayForecast(lat, lon, locationName, phone);
                if (!newSnapshots.isEmpty()) {
                    forecastMonitoringService.processAndMonitorForecast(phone, locationName, newSnapshots, "Cotton");
                    monitoredCount++;
                }
            } catch (Exception e) {
                logger.warn("Failed weather monitoring cycle for farmer {}: {}", phone, e.getMessage());
            }
        }

        logger.info("Completed automated weather monitoring cycle across {} active farm locations.", monitoredCount);
    }
}
