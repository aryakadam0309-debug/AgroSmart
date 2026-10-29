package com.agrosmart.backend.services;

import com.agrosmart.backend.entities.WeatherForecastSnapshot;
import com.agrosmart.backend.payload.FarmingAdvisoryDto;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Domain-specific Farming Advisory Engine (PS 5.4 Requirement 5).
 * Converts meteorological measurements into understandable, actionable agronomic advice
 * for Sowing, Pesticide Spraying, and Harvesting with documented thresholds.
 */
@Service
public class FarmingAdvisoryEngine {

    // Documented Agronomic Thresholds (Configurable)
    public static final double HEAVY_RAIN_THRESHOLD_MM = 25.0;
    public static final int HEAVY_RAIN_POP_THRESHOLD = 70;
    public static final double SPRAY_MAX_WIND_SPEED_KMH = 18.0;
    public static final double SPRAY_WASH_RAIN_THRESHOLD_MM = 2.5;
    public static final int SPRAY_WASH_POP_THRESHOLD = 40;
    public static final double HARVEST_RISK_RAIN_MM = 15.0;
    public static final int HARVEST_RISK_POP = 50;

    /**
     * Generates a comprehensive list of activity advisories for the given daily weather snapshot.
     */
    public List<FarmingAdvisoryDto> generateAdvisories(WeatherForecastSnapshot snap, String cropType) {
        List<FarmingAdvisoryDto> advisories = new ArrayList<>();
        if (snap == null) return advisories;

        advisories.add(evaluateSowing(snap, cropType));
        advisories.add(evaluateSpraying(snap, cropType));
        advisories.add(evaluateHarvesting(snap, cropType));

        return advisories;
    }

    /**
     * Sowing Advisory Evaluation
     */
    public FarmingAdvisoryDto evaluateSowing(WeatherForecastSnapshot snap, String cropType) {
        double rain = snap.getExpectedRainfallMm() != null ? snap.getExpectedRainfallMm() : 0.0;
        int pop = snap.getRainProbability() != null ? snap.getRainProbability() : 0;
        double maxTemp = snap.getMaxTemp() != null ? snap.getMaxTemp() : 30.0;
        String dateStr = snap.getForecastDate() != null ? snap.getForecastDate() : "Today";
        String nowStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a"));
        String cropContext = (cropType != null && !cropType.trim().isEmpty()) ? " for " + cropType : "";

        String triggerMetrics = String.format(Locale.US, "Rain: %.1f mm | Pop: %d%% | Max Temp: %.1f°C", rain, pop, maxTemp);

        if (rain >= HEAVY_RAIN_THRESHOLD_MM || pop >= HEAVY_RAIN_POP_THRESHOLD) {
            return new FarmingAdvisoryDto(
                    "Sowing",
                    "Avoid/Delay",
                    "#D32F2F", // Red
                    String.format(Locale.US, "Heavy rain is forecast (%.1f mm, %d%% probability)%s. Waterlogging and severe seed washout risk will hinder germination.",
                            rain, pop, cropContext),
                    triggerMetrics,
                    "Postpone sowing until heavy rains subside and field drainage is restored. Ensure drainage furrows are unblocked.",
                    dateStr,
                    nowStr
            );
        } else if (rain >= 10.0 && rain < HEAVY_RAIN_THRESHOLD_MM && pop >= 40) {
            return new FarmingAdvisoryDto(
                    "Sowing",
                    "Caution",
                    "#F57C00", // Orange
                    String.format(Locale.US, "Moderate rain showers expected (%.1f mm)%s. Soil will have sufficient moisture, but assess field drainage capacity.",
                            rain, cropContext),
                    triggerMetrics,
                    "Check seedbed elevation and sow on raised beds or ridges to avoid seed suffocation in poorly drained soils.",
                    dateStr,
                    nowStr
            );
        } else if (maxTemp >= 38.0 && rain < 1.0) {
            return new FarmingAdvisoryDto(
                    "Sowing",
                    "Caution",
                    "#F57C00", // Orange
                    String.format(Locale.US, "Excessive heat (%.1f°C) and zero rainfall detected. High surface evaporation will dry out newly sown seeds.", maxTemp),
                    triggerMetrics,
                    "Perform pre-sowing irrigation (paleva) or mulch rows to retain soil moisture before planting.",
                    dateStr,
                    nowStr
            );
        } else {
            return new FarmingAdvisoryDto(
                    "Sowing",
                    "Suitable",
                    "#2E7D32", // Green
                    String.format(Locale.US, "Optimal soil temperature and moisture conditions%s. No adverse rain extremes predicted.", cropContext),
                    triggerMetrics,
                    "Proceed with sowing as per standard line sowing depth (3-5 cm) and apply recommended basal fertilizer.",
                    dateStr,
                    nowStr
            );
        }
    }

    /**
     * Pesticide Spraying Advisory Evaluation
     */
    public FarmingAdvisoryDto evaluateSpraying(WeatherForecastSnapshot snap, String cropType) {
        double rain = snap.getExpectedRainfallMm() != null ? snap.getExpectedRainfallMm() : 0.0;
        int pop = snap.getRainProbability() != null ? snap.getRainProbability() : 0;
        double wind = snap.getWindSpeed() != null ? snap.getWindSpeed() : 10.0;
        int humidity = snap.getHumidity() != null ? snap.getHumidity() : 60;
        String dateStr = snap.getForecastDate() != null ? snap.getForecastDate() : "Today";
        String nowStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a"));
        String cropContext = (cropType != null && !cropType.trim().isEmpty()) ? " in " + cropType : "";

        String triggerMetrics = String.format(Locale.US, "Rain: %.1f mm | Pop: %d%% | Wind: %.1f km/h | Hum: %d%%",
                rain, pop, wind, humidity);

        if (rain >= SPRAY_WASH_RAIN_THRESHOLD_MM || pop >= SPRAY_WASH_POP_THRESHOLD) {
            return new FarmingAdvisoryDto(
                    "Pesticide Spraying",
                    "Avoid/Delay",
                    "#D32F2F", // Red
                    String.format(Locale.US, "Precipitation predicted (%.1f mm, %d%% chance)%s. Rain within 6-12 hours of foliar application will wash away active chemical ingredients.",
                            rain, pop, cropContext),
                    triggerMetrics,
                    "Halt spraying operations. Wait for an unbroken 24-hour dry window to ensure systemic absorption.",
                    dateStr,
                    nowStr
            );
        } else if (wind >= SPRAY_MAX_WIND_SPEED_KMH) {
            return new FarmingAdvisoryDto(
                    "Pesticide Spraying",
                    "Avoid/Delay",
                    "#D32F2F", // Red
                    String.format(Locale.US, "Strong winds (%.1f km/h) exceed safety limits (18 km/h). Excessive droplet drift will waste chemical and hazard neighboring fields.", wind),
                    triggerMetrics,
                    "Delay spraying until wind speeds decrease below 15 km/h, preferably during early morning calm hours.",
                    dateStr,
                    nowStr
            );
        } else if (humidity >= 85) {
            return new FarmingAdvisoryDto(
                    "Pesticide Spraying",
                    "Caution",
                    "#F57C00", // Orange
                    String.format(Locale.US, "High relative humidity (%d%%) slows evaporation and encourages fungal spore dispersion.", humidity),
                    triggerMetrics,
                    "Use adjuvant sticking agents (surfactants) and calibrate hollow cone nozzles to achieve fine, uniform droplet spread.",
                    dateStr,
                    nowStr
            );
        } else {
            return new FarmingAdvisoryDto(
                    "Pesticide Spraying",
                    "Suitable",
                    "#2E7D32", // Green
                    String.format(Locale.US, "Excellent spray window%s: Calm winds (%.1f km/h), clear canopy, and low rain chance (%d%%).",
                            cropContext, wind, pop),
                    triggerMetrics,
                    "Conduct target foliar spray using protective gear. Spray between 7:00 AM - 10:30 AM or 4:00 PM - 6:30 PM for maximum uptake.",
                    dateStr,
                    nowStr
            );
        }
    }

    /**
     * Harvesting Advisory Evaluation
     */
    public FarmingAdvisoryDto evaluateHarvesting(WeatherForecastSnapshot snap, String cropType) {
        double rain = snap.getExpectedRainfallMm() != null ? snap.getExpectedRainfallMm() : 0.0;
        int pop = snap.getRainProbability() != null ? snap.getRainProbability() : 0;
        String dateStr = snap.getForecastDate() != null ? snap.getForecastDate() : "Today";
        String nowStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a"));
        String cropContext = (cropType != null && !cropType.trim().isEmpty()) ? " for " + cropType : "";

        String triggerMetrics = String.format(Locale.US, "Rain: %.1f mm | Pop: %d%% | Risk: %s",
                rain, pop, snap.getRiskLevel() != null ? snap.getRiskLevel() : "LOW");

        if (rain >= HARVEST_RISK_RAIN_MM || pop >= HARVEST_RISK_POP || snap.isHeavyRain()) {
            return new FarmingAdvisoryDto(
                    "Harvesting",
                    "Avoid/Delay",
                    "#D32F2F", // Red
                    String.format(Locale.US, "Severe rainfall threat (%.1f mm, %d%% probability)%s. Wet standing crop risks grain shattering, fungal discoloration, and field mudding.",
                            rain, pop, cropContext),
                    triggerMetrics,
                    "Do not cut standing crop. For already harvested produce lying in open threshing floors, immediately cover with tarpaulins or transfer to dry godowns.",
                    dateStr,
                    nowStr
            );
        } else if (rain >= 2.0 && rain < HARVEST_RISK_RAIN_MM) {
            return new FarmingAdvisoryDto(
                    "Harvesting",
                    "Caution",
                    "#F57C00", // Orange
                    String.format(Locale.US, "Light to moderate showers anticipated (%.1f mm)%s. Cut grain may suffer moisture absorption if left in rows.",
                            rain, cropContext),
                    triggerMetrics,
                    "Harvest only if mechanical combines and immediate transit to covered drying yards are on standby.",
                    dateStr,
                    nowStr
            );
        } else {
            return new FarmingAdvisoryDto(
                    "Harvesting",
                    "Suitable",
                    "#2E7D32", // Green
                    String.format(Locale.US, "Ideal dry harvesting conditions%s. Clear skies ensure low grain moisture and safe mechanical harvesting.", cropContext),
                    triggerMetrics,
                    "Proceed with harvesting, threshing, and sun-drying grain to the recommended moisture level (< 12%) for safe storage.",
                    dateStr,
                    nowStr
            );
        }
    }
}
