package com.agrosmart.backend.weather;

import com.agrosmart.backend.entities.WeatherAdvisoryChange;
import com.agrosmart.backend.entities.WeatherForecastSnapshot;
import com.agrosmart.backend.repositories.WeatherAdvisoryChangeRepository;
import com.agrosmart.backend.repositories.WeatherForecastSnapshotRepository;
import com.agrosmart.backend.services.FarmingAdvisoryEngine;
import com.agrosmart.backend.services.ForecastMonitoringService;
import com.agrosmart.backend.services.PushNotificationService;
import com.agrosmart.backend.services.WeatherApiClientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("Forecast Monitoring & Change Detection Service Tests")
public class ForecastMonitoringServiceTest {

    private WeatherForecastSnapshotRepository snapshotRepository;
    private WeatherAdvisoryChangeRepository advisoryChangeRepository;
    private FarmingAdvisoryEngine advisoryEngine;
    private PushNotificationService notificationService;
    private WeatherApiClientService weatherApiClientService;
    private ForecastMonitoringService monitoringService;

    @BeforeEach
    public void setUp() {
        snapshotRepository = mock(WeatherForecastSnapshotRepository.class);
        advisoryChangeRepository = mock(WeatherAdvisoryChangeRepository.class);
        advisoryEngine = new FarmingAdvisoryEngine();
        notificationService = mock(PushNotificationService.class);
        weatherApiClientService = mock(WeatherApiClientService.class);

        monitoringService = new ForecastMonitoringService(
                snapshotRepository,
                advisoryChangeRepository,
                advisoryEngine,
                notificationService,
                weatherApiClientService
        );
    }

    @Test
    @DisplayName("Initial forecast with no prior snapshot does not trigger spurious alert")
    public void testInitialForecastNoSpuriousAlert() {
        when(snapshotRepository.findTopByFarmerPhoneAndForecastDateOrderByRetrievedAtDesc(anyString(), anyString()))
                .thenReturn(Optional.empty());

        WeatherForecastSnapshot snap = new WeatherForecastSnapshot();
        snap.setFarmerPhone("9876543210");
        snap.setForecastDate("2026-11-01");
        snap.setDayName("Saturday");
        snap.setExpectedRainfallMm(5.0);
        snap.setRainProbability(20);

        List<WeatherAdvisoryChange> changes = monitoringService.processAndMonitorForecast(
                "9876543210", "Pune, Maharashtra", List.of(snap), "Cotton"
        );

        assertTrue(changes.isEmpty(), "Initial forecast baseline must not trigger spurious alerts");
        verify(notificationService, never()).sendFarmerAlert(anyString(), anyString(), anyString(), anyString());
        verify(snapshotRepository, times(1)).save(snap);
    }

    @Test
    @DisplayName("Heavy rain warning introduction triggers advisory change and dispatches notification")
    public void testHeavyRainIntroductionTriggersAlert() {
        String phone = "9876543210";
        String date = "2026-11-01";

        // Previous Snapshot: 2mm rain, 20% pop (Harvesting Suitable)
        WeatherForecastSnapshot prevSnap = new WeatherForecastSnapshot();
        prevSnap.setFarmerPhone(phone);
        prevSnap.setForecastDate(date);
        prevSnap.setDayName("Saturday");
        prevSnap.setExpectedRainfallMm(2.0);
        prevSnap.setRainProbability(20);
        prevSnap.setHeavyRain(false);
        prevSnap.setTemp(29.0);
        prevSnap.setCondition("Clear");
        prevSnap.setRetrievedAt(LocalDateTime.now().minusHours(12));

        when(snapshotRepository.findTopByFarmerPhoneAndForecastDateOrderByRetrievedAtDesc(phone, date))
                .thenReturn(Optional.of(prevSnap));
        when(advisoryChangeRepository.existsByFarmerPhoneAndDedupKey(eq(phone), anyString()))
                .thenReturn(false);
        when(notificationService.sendFarmerAlert(eq(phone), anyString(), anyString(), anyString()))
                .thenReturn("DELIVERED");

        // Updated Snapshot: 45mm heavy rain, 85% pop
        WeatherForecastSnapshot newSnap = new WeatherForecastSnapshot();
        newSnap.setFarmerPhone(phone);
        newSnap.setForecastDate(date);
        newSnap.setDayName("Saturday");
        newSnap.setExpectedRainfallMm(45.0);
        newSnap.setRainProbability(85);
        newSnap.setHeavyRain(true);
        newSnap.setTemp(25.0);
        newSnap.setCondition("Heavy Rain");
        newSnap.setRetrievedAt(LocalDateTime.now());

        List<WeatherAdvisoryChange> changes = monitoringService.processAndMonitorForecast(
                phone, "Pune, Maharashtra", List.of(newSnap), "Cotton"
        );

        assertFalse(changes.isEmpty(), "Changes must be detected when heavy rain is introduced");

        // Check harvesting advisory change
        Optional<WeatherAdvisoryChange> harvestChange = changes.stream()
                .filter(c -> "Harvesting".equalsIgnoreCase(c.getActivity()))
                .findFirst();

        assertTrue(harvestChange.isPresent());
        assertEquals("Avoid/Delay", harvestChange.get().getUpdatedStatus());
        assertTrue(harvestChange.get().isNotificationSent());
        assertEquals("DELIVERED", harvestChange.get().getNotificationDeliveryStatus());
        assertTrue(harvestChange.get().getChangeReason().contains("Heavy rain"));

        verify(notificationService, atLeastOnce()).sendFarmerAlert(eq(phone), contains("Heavy Rain"), anyString(), eq("HEAVY_RAIN_ALERT"));
        verify(advisoryChangeRepository, atLeastOnce()).save(any(WeatherAdvisoryChange.class));
    }

    @Test
    @DisplayName("Deduplication prevents sending duplicate alerts for the same change")
    public void testDeduplicationPreventsDuplicateAlerts() {
        String phone = "9876543210";
        String date = "2026-11-01";

        WeatherForecastSnapshot prevSnap = new WeatherForecastSnapshot();
        prevSnap.setFarmerPhone(phone);
        prevSnap.setForecastDate(date);
        prevSnap.setDayName("Saturday");
        prevSnap.setExpectedRainfallMm(0.0);
        prevSnap.setRainProbability(10);
        prevSnap.setHeavyRain(false);
        prevSnap.setTemp(30.0);
        prevSnap.setCondition("Clear");

        when(snapshotRepository.findTopByFarmerPhoneAndForecastDateOrderByRetrievedAtDesc(phone, date))
                .thenReturn(Optional.of(prevSnap));

        // Simulate deduplication key already exists!
        when(advisoryChangeRepository.existsByFarmerPhoneAndDedupKey(eq(phone), anyString()))
                .thenReturn(true);

        WeatherForecastSnapshot newSnap = new WeatherForecastSnapshot();
        newSnap.setFarmerPhone(phone);
        newSnap.setForecastDate(date);
        newSnap.setDayName("Saturday");
        newSnap.setExpectedRainfallMm(35.0);
        newSnap.setRainProbability(80);
        newSnap.setHeavyRain(true);
        newSnap.setTemp(26.0);
        newSnap.setCondition("Heavy Rain");

        List<WeatherAdvisoryChange> changes = monitoringService.processAndMonitorForecast(
                phone, "Pune, Maharashtra", List.of(newSnap), "Cotton"
        );

        assertTrue(changes.isEmpty(), "Duplicate alerts must be skipped by deduplication engine");
        verify(notificationService, never()).sendFarmerAlert(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Routine forecast refresh without status change does not trigger notifications")
    public void testRoutineRefreshNoNotification() {
        String phone = "9876543210";
        String date = "2026-11-01";

        WeatherForecastSnapshot prevSnap = new WeatherForecastSnapshot();
        prevSnap.setFarmerPhone(phone);
        prevSnap.setForecastDate(date);
        prevSnap.setDayName("Saturday");
        prevSnap.setExpectedRainfallMm(0.0);
        prevSnap.setRainProbability(10);
        prevSnap.setWindSpeed(8.0);
        prevSnap.setTemp(28.0);
        prevSnap.setCondition("Clear");
        prevSnap.setHeavyRain(false);

        when(snapshotRepository.findTopByFarmerPhoneAndForecastDateOrderByRetrievedAtDesc(phone, date))
                .thenReturn(Optional.of(prevSnap));

        // Slightly different temp (+1°C), same dry weather
        WeatherForecastSnapshot newSnap = new WeatherForecastSnapshot();
        newSnap.setFarmerPhone(phone);
        newSnap.setForecastDate(date);
        newSnap.setDayName("Saturday");
        newSnap.setExpectedRainfallMm(0.0);
        newSnap.setRainProbability(10);
        newSnap.setWindSpeed(9.0);
        newSnap.setTemp(29.0);
        newSnap.setCondition("Clear");
        newSnap.setHeavyRain(false);

        List<WeatherAdvisoryChange> changes = monitoringService.processAndMonitorForecast(
                phone, "Pune, Maharashtra", List.of(newSnap), "Cotton"
        );

        assertTrue(changes.isEmpty(), "Routine minor weather change must not trigger alerts");
        verify(notificationService, never()).sendFarmerAlert(anyString(), anyString(), anyString(), anyString());
    }
}
