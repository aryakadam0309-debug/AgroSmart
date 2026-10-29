package com.agrosmart.backend.services;

import com.agrosmart.backend.entities.WeatherAdvisoryChange;
import com.agrosmart.backend.entities.WeatherForecastSnapshot;
import com.agrosmart.backend.payload.FarmingAdvisoryDto;
import com.agrosmart.backend.payload.SimulateChangeRequest;
import com.agrosmart.backend.repositories.WeatherAdvisoryChangeRepository;
import com.agrosmart.backend.repositories.WeatherForecastSnapshotRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Core Forecast Monitoring & Change Detection Service (PS 5.4 Requirements 6, 8 & 11).
 * Detects significant forecast updates, recalculates smart farming recommendations,
 * deduplicates alerts, triggers notifications, and tracks previous vs. updated advice.
 */
@Service
public class ForecastMonitoringService {

    private static final Logger logger = LoggerFactory.getLogger(ForecastMonitoringService.class);

    private final WeatherForecastSnapshotRepository snapshotRepository;
    private final WeatherAdvisoryChangeRepository advisoryChangeRepository;
    private final FarmingAdvisoryEngine advisoryEngine;
    private final PushNotificationService notificationService;
    private final WeatherApiClientService weatherApiClientService;

    public ForecastMonitoringService(WeatherForecastSnapshotRepository snapshotRepository,
                                     WeatherAdvisoryChangeRepository advisoryChangeRepository,
                                     FarmingAdvisoryEngine advisoryEngine,
                                     PushNotificationService notificationService,
                                     WeatherApiClientService weatherApiClientService) {
        this.snapshotRepository = snapshotRepository;
        this.advisoryChangeRepository = advisoryChangeRepository;
        this.advisoryEngine = advisoryEngine;
        this.notificationService = notificationService;
        this.weatherApiClientService = weatherApiClientService;
    }

    /**
     * Ingests a new 5-day forecast, compares each day's metrics against previous snapshots,
     * recalculates farming advice, logs changes, and triggers notifications if significant shifts occur.
     */
    @Transactional
    public List<WeatherAdvisoryChange> processAndMonitorForecast(String farmerPhone,
                                                                 String locationName,
                                                                 List<WeatherForecastSnapshot> newSnapshots,
                                                                 String cropType) {
        List<WeatherAdvisoryChange> detectedChanges = new ArrayList<>();
        if (newSnapshots == null || newSnapshots.isEmpty()) return detectedChanges;

        for (WeatherForecastSnapshot newSnap : newSnapshots) {
            String dateKey = newSnap.getForecastDate();

            // Find existing previous snapshot for same farmer & forecast date
            Optional<WeatherForecastSnapshot> prevSnapOpt = snapshotRepository
                    .findTopByFarmerPhoneAndForecastDateOrderByRetrievedAtDesc(farmerPhone, dateKey);

            if (prevSnapOpt.isPresent()) {
                WeatherForecastSnapshot prevSnap = prevSnapOpt.get();

                // Evaluate farming advisories for both snapshots
                List<FarmingAdvisoryDto> prevAdvisories = advisoryEngine.generateAdvisories(prevSnap, cropType);
                List<FarmingAdvisoryDto> newAdvisories = advisoryEngine.generateAdvisories(newSnap, cropType);

                // Detect changes per activity (Sowing, Spraying, Harvesting)
                for (int i = 0; i < newAdvisories.size(); i++) {
                    FarmingAdvisoryDto newAdv = newAdvisories.get(i);
                    FarmingAdvisoryDto prevAdv = (i < prevAdvisories.size()) ? prevAdvisories.get(i) : null;

                    boolean statusChanged = (prevAdv != null && !prevAdv.getStatus().equalsIgnoreCase(newAdv.getStatus()));
                    boolean newlyHeavyRain = (!prevSnap.isHeavyRain() && newSnap.isHeavyRain());
                    boolean rainSurge = (newSnap.getExpectedRainfallMm() - prevSnap.getExpectedRainfallMm() >= 15.0);

                    // Only notify if meaningful change occurred
                    if (statusChanged || newlyHeavyRain || rainSurge) {
                        String dedupKey = generateDedupKey(farmerPhone, dateKey, newAdv.getActivity(), newAdv.getStatus(), newSnap.isHeavyRain());

                        // Check deduplication
                        if (advisoryChangeRepository.existsByFarmerPhoneAndDedupKey(farmerPhone, dedupKey)) {
                            logger.info("Skipping duplicate alert for farmer {} on {} - Activity: {}", farmerPhone, dateKey, newAdv.getActivity());
                            continue;
                        }

                        WeatherAdvisoryChange change = new WeatherAdvisoryChange();
                        change.setFarmerPhone(farmerPhone);
                        change.setLocationName(locationName);
                        change.setForecastDate(dateKey);
                        change.setDayName(newSnap.getDayName());
                        change.setActivity(newAdv.getActivity());
                        change.setPreviousStatus(prevAdv != null ? prevAdv.getStatus() : "Unknown");
                        change.setUpdatedStatus(newAdv.getStatus());

                        String prevSummary = String.format(Locale.US, "%s • %.1f°C • %s (Rain: %.1fmm, %d%% prob)",
                                prevSnap.getDayName(), prevSnap.getTemp(), prevSnap.getCondition(),
                                prevSnap.getExpectedRainfallMm(), prevSnap.getRainProbability());
                        String newSummary = String.format(Locale.US, "%s • %.1f°C • %s (Rain: %.1fmm, %d%% prob)",
                                newSnap.getDayName(), newSnap.getTemp(), newSnap.getCondition(),
                                newSnap.getExpectedRainfallMm(), newSnap.getRainProbability());

                        change.setPreviousForecastSummary(prevSummary);
                        change.setUpdatedForecastSummary(newSummary);
                        change.setPreviousRetrievedAt(prevSnap.getRetrievedAt());
                        change.setUpdatedRetrievedAt(newSnap.getRetrievedAt());

                        String reason;
                        if (newlyHeavyRain) {
                            reason = String.format(Locale.US, "Heavy rain alert introduced for %s (%.1f mm expected, %d%% probability). Advisory shifted from %s to %s.",
                                    newSnap.getDayName(), newSnap.getExpectedRainfallMm(), newSnap.getRainProbability(),
                                    change.getPreviousStatus(), change.getUpdatedStatus());
                        } else {
                            reason = String.format(Locale.US, "Weather forecast updated for %s: %s shifted to %s due to changed precipitation / wind metrics.",
                                    newSnap.getDayName(), newAdv.getActivity(), newAdv.getStatus());
                        }
                        change.setChangeReason(reason);
                        change.setDedupKey(dedupKey);

                        // Trigger push notification to farmer
                        String notifTitle = newSnap.isHeavyRain() ? "Heavy Rain Alert – AgroSmart" : "Weather Advisory Update – AgroSmart";
                        String notifMsg = String.format(Locale.US, "The latest forecast predicts %s for %s. Your %s advisory has changed from \"%s\" to \"%s\". Tap to inspect updated guidance.",
                                newSnap.isHeavyRain() ? "heavy rain" : "weather shifts",
                                newSnap.getDayName(),
                                newAdv.getActivity().toLowerCase(),
                                change.getPreviousStatus(),
                                change.getUpdatedStatus());

                        String deliveryStatus = notificationService.sendFarmerAlert(farmerPhone, notifTitle, notifMsg, "HEAVY_RAIN_ALERT");
                        change.setNotificationSent(true);
                        change.setNotificationDeliveryStatus(deliveryStatus);

                        advisoryChangeRepository.save(change);
                        detectedChanges.add(change);
                        logger.info("Recorded advisory change and dispatched alert for {} ({})", farmerPhone, change.getActivity());
                    }
                }
            }

            // Save new snapshot into database
            snapshotRepository.save(newSnap);
        }

        return detectedChanges;
    }

    /**
     * Hackathon Live Demonstration Method (Requirement 11).
     * Simulates an updated weather forecast introducing a sudden heavy-rain warning on a specific day
     * (e.g. Saturday) to demonstrate live before evaluators.
     */
    @Transactional
    public Map<String, Object> simulateForecastChange(SimulateChangeRequest request) {
        String phone = (request.getFarmerPhone() != null && !request.getFarmerPhone().trim().isEmpty())
                ? request.getFarmerPhone().trim()
                : "9876543210";
        String location = (request.getLocationName() != null && !request.getLocationName().trim().isEmpty())
                ? request.getLocationName().trim()
                : "Pune, Maharashtra";

        logger.info("Simulating heavy-rain forecast change for evaluation on farmer: {}", phone);

        // Fetch baseline real forecast first
        List<WeatherForecastSnapshot> currentSnapshots = weatherApiClientService.fetchFiveDayForecast(18.5204, 73.8567, location, phone);
        if (currentSnapshots.isEmpty()) {
            throw new IllegalStateException("Unable to fetch initial forecast for simulation baseline");
        }

        // Persist initial baseline snapshots so there is a previous state
        for (WeatherForecastSnapshot s : currentSnapshots) {
            snapshotRepository.save(s);
        }

        int targetIndex = Math.min(request.getDayIndex(), currentSnapshots.size() - 1);
        WeatherForecastSnapshot targetDay = currentSnapshots.get(targetIndex);

        // Build updated snapshot with simulated heavy-rain conditions
        WeatherForecastSnapshot updatedDay = new WeatherForecastSnapshot();
        updatedDay.setFarmerPhone(phone);
        updatedDay.setLocationName(location);
        updatedDay.setLatitude(targetDay.getLatitude());
        updatedDay.setLongitude(targetDay.getLongitude());
        updatedDay.setForecastDate(targetDay.getForecastDate());
        updatedDay.setDayName(targetDay.getDayName());
        updatedDay.setMinTemp(targetDay.getMinTemp());
        updatedDay.setMaxTemp(targetDay.getMaxTemp());
        updatedDay.setTemp(targetDay.getTemp() - 3.0); // Dropping temp
        updatedDay.setHumidity(92);                    // High humidity
        updatedDay.setWindSpeed(24.5);                 // High wind
        updatedDay.setRainProbability(request.getSimulatedRainProbability() != null ? request.getSimulatedRainProbability() : 85);
        updatedDay.setExpectedRainfallMm(request.getSimulatedRainfallMm() != null ? request.getSimulatedRainfallMm() : 48.0);
        updatedDay.setCondition(request.getCondition() != null ? request.getCondition() : "Heavy Rain");
        updatedDay.setConditionDescription("Severe convective thunderstorm and intense precipitation");
        updatedDay.setHeavyRain(true);
        updatedDay.setRiskLevel("HIGH");
        updatedDay.setProvider("Live Simulation Test Fixture");
        updatedDay.setRetrievedAt(LocalDateTime.now());

        List<WeatherForecastSnapshot> simulationList = new ArrayList<>(currentSnapshots);
        simulationList.set(targetIndex, updatedDay);

        // Process change detection
        List<WeatherAdvisoryChange> changes = processAndMonitorForecast(phone, location, simulationList, "Cotton");

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", "SUCCESS");
        result.put("message", "Simulated heavy-rain forecast update processed successfully");
        result.put("simulatedDay", targetDay.getDayName());
        result.put("forecastDate", targetDay.getForecastDate());
        result.put("rainfallIntroducedMm", updatedDay.getExpectedRainfallMm());
        result.put("rainProbability", updatedDay.getRainProbability());
        result.put("changesDetectedCount", changes.size());
        result.put("detectedChanges", changes);

        return result;
    }

    private String generateDedupKey(String phone, String date, String activity, String status, boolean heavyRain) {
        try {
            String raw = String.format("%s:%s:%s:%s:%b", phone, date, activity, status, heavyRain);
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(raw.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.substring(0, 32);
        } catch (Exception e) {
            return phone + "_" + date + "_" + activity + "_" + status;
        }
    }
}
