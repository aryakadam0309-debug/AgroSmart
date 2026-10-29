package com.agrosmart.weather;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.agrosmart.models.FarmingAdvisory;
import com.agrosmart.models.ForecastItem;
import com.agrosmart.models.WeatherAdvisoryChange;
import com.agrosmart.models.WeatherResponse;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Unit Tests for Weather & Smart Farming Advisory Models (PS 5.4).
 * Verifies model getters/setters, risk indicator logic, advisory contracts,
 * and previous vs. updated forecast advisory diffing integrity.
 */
public class WeatherAdvisoryModelTest {

    @Test
    public void testFarmingAdvisoryModelEncapsulation() {
        FarmingAdvisory advisory = new FarmingAdvisory();
        advisory.setActivity("Harvesting");
        advisory.setStatus("Avoid/Delay");
        advisory.setExplanation("Heavy downpour of 48.0 mm expected. Waterlogged fields can ruin harvested grain.");
        advisory.setTriggerMetrics("Rain: 48.0 mm, Chance: 92%, Wind: 28 km/h");
        advisory.setRecommendedAction("Postpone harvest until fields dry and sunny weather returns.");
        advisory.setForecastDate("2026-11-01");
        advisory.setGeneratedAt("2026-10-30T10:00:00Z");

        assertEquals("Harvesting", advisory.getActivity());
        assertEquals("Avoid/Delay", advisory.getStatus());
        assertTrue(advisory.getExplanation().contains("Heavy downpour"));
        assertTrue(advisory.getTriggerMetrics().contains("48.0 mm"));
        assertTrue(advisory.getRecommendedAction().contains("Postpone harvest"));
        assertEquals("2026-11-01", advisory.getForecastDate());
        assertEquals("2026-10-30T10:00:00Z", advisory.getGeneratedAt());
    }

    @Test
    public void testForecastItemRainAndRiskFields() {
        ForecastItem item = new ForecastItem("Saturday", "31 Oct", 29.0, 21.0, 31.0, "Heavy Rain", "10d");
        item.setRainfallMm(35.5);
        item.setRainChance(88);
        item.setHumidity(94);
        item.setWindSpeed(26.0);
        item.setRiskLevel("HIGH");

        assertEquals("Saturday", item.getDay());
        assertEquals("31 Oct", item.getDate());
        assertEquals(35.5, item.getRainfallMm(), 0.01);
        assertEquals(88, item.getRainChance());
        assertEquals(35.5, item.getExpectedRainfallMm(), 0.01);
        assertEquals(88, item.getRainProbability());
        assertEquals(94, item.getHumidity());
        assertEquals(26.0, item.getWindSpeed(), 0.01);
        assertEquals("HIGH", item.getRiskLevel());
    }

    @Test
    public void testWeatherAdvisoryChangeDiffModel() {
        WeatherAdvisoryChange change = new WeatherAdvisoryChange();
        change.setId(101L);
        change.setFarmerPhone("9876543210");
        change.setLocationName("Baramati, Maharashtra");
        change.setForecastDate("2026-11-02");
        change.setDayName("Monday");
        change.setActivity("Pesticide Spraying");
        change.setPreviousStatus("Suitable");
        change.setUpdatedStatus("Avoid/Delay");
        change.setPreviousForecastSummary("Light breeze, 10% rain chance, 0.0 mm rain");
        change.setUpdatedForecastSummary("Squall warning, 90% rain chance, 42.0 mm heavy rain, 30 km/h wind");
        change.setChangeReason("Rain probability surged from 10% to 90%, washing out chemical treatments.");
        change.setNotificationDeliveryStatus("SENT");
        change.setDetectedAt("2026-10-30T06:30:00Z");

        assertEquals(Long.valueOf(101L), change.getId());
        assertEquals("9876543210", change.getFarmerPhone());
        assertEquals("Baramati, Maharashtra", change.getLocationName());
        assertEquals("Pesticide Spraying", change.getActivity());
        assertEquals("Suitable", change.getPreviousStatus());
        assertEquals("Avoid/Delay", change.getUpdatedStatus());
        assertTrue(change.getPreviousForecastSummary().contains("10% rain chance"));
        assertTrue(change.getUpdatedForecastSummary().contains("Squall warning"));
        assertTrue(change.getChangeReason().contains("washing out chemical"));
        assertEquals("SENT", change.getNotificationDeliveryStatus());
    }

    @Test
    public void testWeatherResponseWithAdvisoriesAndDiffs() {
        WeatherResponse resp = new WeatherResponse();
        resp.setCityName("Pune, Maharashtra");
        resp.setTemp(28.5);
        resp.setRainfallMm(5.2);
        resp.setOverallRiskLevel("MODERATE");
        resp.setFiveDaySummary("Moderate rainfall expected across Saturday and Sunday. Plan spraying accordingly.");

        List<FarmingAdvisory> advisories = new ArrayList<>();
        FarmingAdvisory sowing = new FarmingAdvisory();
        sowing.setActivity("Sowing");
        sowing.setStatus("Caution");
        advisories.add(sowing);
        resp.setAdvisories(advisories);

        WeatherAdvisoryChange diff = new WeatherAdvisoryChange();
        diff.setActivity("Harvesting");
        diff.setPreviousStatus("Suitable");
        diff.setUpdatedStatus("Caution");
        resp.setLatestAdvisoryChange(diff);

        assertEquals("Pune, Maharashtra", resp.getCityName());
        assertEquals(5.2, resp.getRainfallMm(), 0.01);
        assertEquals("MODERATE", resp.getOverallRiskLevel());
        assertNotNull(resp.getFiveDaySummary());
        assertEquals(1, resp.getAdvisories().size());
        assertEquals("Sowing", resp.getAdvisories().get(0).getActivity());
        assertNotNull(resp.getLatestAdvisoryChange());
        assertEquals("Harvesting", resp.getLatestAdvisoryChange().getActivity());
    }
}
