package com.agrosmart.backend.weather;

import com.agrosmart.backend.entities.WeatherForecastSnapshot;
import com.agrosmart.backend.payload.FarmingAdvisoryDto;
import com.agrosmart.backend.services.FarmingAdvisoryEngine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Farming Advisory Engine Rule Tests")
public class FarmingAdvisoryEngineTest {

    private FarmingAdvisoryEngine engine;

    @BeforeEach
    public void setUp() {
        engine = new FarmingAdvisoryEngine();
    }

    @Test
    @DisplayName("Sowing should be Avoid/Delay when heavy rainfall is forecast")
    public void testSowingAvoidDelayOnHeavyRain() {
        WeatherForecastSnapshot snap = new WeatherForecastSnapshot();
        snap.setForecastDate("2026-11-01");
        snap.setDayName("Saturday");
        snap.setExpectedRainfallMm(35.0);
        snap.setRainProbability(85);
        snap.setMaxTemp(28.0);
        snap.setHeavyRain(true);

        FarmingAdvisoryDto advisory = engine.evaluateSowing(snap, "Cotton");

        assertNotNull(advisory);
        assertEquals("Sowing", advisory.getActivity());
        assertEquals("Avoid/Delay", advisory.getStatus());
        assertTrue(advisory.getExplanation().contains("Heavy rain"));
        assertTrue(advisory.getExplanation().contains("washout"));
        assertTrue(advisory.getRecommendedAction().contains("Postpone sowing"));
    }

    @Test
    @DisplayName("Sowing should be Caution under extreme drought / heat")
    public void testSowingCautionOnHeat() {
        WeatherForecastSnapshot snap = new WeatherForecastSnapshot();
        snap.setForecastDate("2026-11-01");
        snap.setDayName("Saturday");
        snap.setExpectedRainfallMm(0.0);
        snap.setRainProbability(5);
        snap.setMaxTemp(40.5);

        FarmingAdvisoryDto advisory = engine.evaluateSowing(snap, "Soybean");

        assertEquals("Caution", advisory.getStatus());
        assertTrue(advisory.getExplanation().contains("heat") || advisory.getExplanation().contains("evaporation"));
    }

    @Test
    @DisplayName("Sowing should be Suitable under mild weather with moderate moisture")
    public void testSowingSuitableUnderMildConditions() {
        WeatherForecastSnapshot snap = new WeatherForecastSnapshot();
        snap.setForecastDate("2026-11-01");
        snap.setDayName("Saturday");
        snap.setExpectedRainfallMm(5.0);
        snap.setRainProbability(20);
        snap.setMaxTemp(28.0);

        FarmingAdvisoryDto advisory = engine.evaluateSowing(snap, "Wheat");

        assertEquals("Suitable", advisory.getStatus());
        assertTrue(advisory.getRecommendedAction().contains("Proceed with sowing"));
    }

    @Test
    @DisplayName("Spraying should be Avoid/Delay when rainfall will cause foliar washout")
    public void testSprayingAvoidDelayOnRain() {
        WeatherForecastSnapshot snap = new WeatherForecastSnapshot();
        snap.setForecastDate("2026-11-01");
        snap.setDayName("Saturday");
        snap.setExpectedRainfallMm(8.0);
        snap.setRainProbability(65);
        snap.setWindSpeed(10.0);

        FarmingAdvisoryDto advisory = engine.evaluateSpraying(snap, "Cotton");

        assertEquals("Pesticide Spraying", advisory.getActivity());
        assertEquals("Avoid/Delay", advisory.getStatus());
        assertTrue(advisory.getExplanation().contains("wash away"));
    }

    @Test
    @DisplayName("Spraying should be Avoid/Delay when wind speed exceeds 18 km/h drift limit")
    public void testSprayingAvoidDelayOnHighWind() {
        WeatherForecastSnapshot snap = new WeatherForecastSnapshot();
        snap.setForecastDate("2026-11-01");
        snap.setDayName("Saturday");
        snap.setExpectedRainfallMm(0.0);
        snap.setRainProbability(10);
        snap.setWindSpeed(22.5); // > 18 km/h

        FarmingAdvisoryDto advisory = engine.evaluateSpraying(snap, "Paddy");

        assertEquals("Avoid/Delay", advisory.getStatus());
        assertTrue(advisory.getExplanation().contains("drift"));
    }

    @Test
    @DisplayName("Spraying should be Suitable under calm dry conditions")
    public void testSprayingSuitableUnderCalmDry() {
        WeatherForecastSnapshot snap = new WeatherForecastSnapshot();
        snap.setForecastDate("2026-11-01");
        snap.setDayName("Saturday");
        snap.setExpectedRainfallMm(0.0);
        snap.setRainProbability(10);
        snap.setWindSpeed(8.0);
        snap.setHumidity(55);

        FarmingAdvisoryDto advisory = engine.evaluateSpraying(snap, "Paddy");

        assertEquals("Suitable", advisory.getStatus());
        assertTrue(advisory.getExplanation().contains("adhesion") || advisory.getExplanation().contains("window"));
    }

    @Test
    @DisplayName("Harvesting should be Avoid/Delay during heavy rainfall warning")
    public void testHarvestingAvoidDelayOnHeavyRain() {
        WeatherForecastSnapshot snap = new WeatherForecastSnapshot();
        snap.setForecastDate("2026-11-01");
        snap.setDayName("Saturday");
        snap.setExpectedRainfallMm(30.0);
        snap.setRainProbability(80);
        snap.setHeavyRain(true);

        FarmingAdvisoryDto advisory = engine.evaluateHarvesting(snap, "Soybean");

        assertEquals("Harvesting", advisory.getActivity());
        assertEquals("Avoid/Delay", advisory.getStatus());
        assertTrue(advisory.getExplanation().contains("rotting") || advisory.getExplanation().contains("threat"));
        assertTrue(advisory.getRecommendedAction().contains("Do not cut") || advisory.getRecommendedAction().contains("tarpaulins"));
    }

    @Test
    @DisplayName("Harvesting should be Suitable in clear dry weather")
    public void testHarvestingSuitableClearWeather() {
        WeatherForecastSnapshot snap = new WeatherForecastSnapshot();
        snap.setForecastDate("2026-11-01");
        snap.setDayName("Saturday");
        snap.setExpectedRainfallMm(0.0);
        snap.setRainProbability(5);

        FarmingAdvisoryDto advisory = engine.evaluateHarvesting(snap, "Wheat");

        assertEquals("Suitable", advisory.getStatus());
        assertTrue(advisory.getRecommendedAction().contains("threshing"));
    }

    @Test
    @DisplayName("All three activities are evaluated in batch")
    public void testBatchEvaluation() {
        WeatherForecastSnapshot snap = new WeatherForecastSnapshot();
        snap.setForecastDate("2026-11-01");
        snap.setDayName("Saturday");
        snap.setExpectedRainfallMm(0.0);
        snap.setRainProbability(10);
        snap.setWindSpeed(8.0);
        snap.setHumidity(50);
        snap.setMaxTemp(29.0);

        List<FarmingAdvisoryDto> batch = engine.generateAdvisories(snap, "Cotton");
        assertEquals(3, batch.size());
        assertEquals("Sowing", batch.get(0).getActivity());
        assertEquals("Pesticide Spraying", batch.get(1).getActivity());
        assertEquals("Harvesting", batch.get(2).getActivity());
    }
}
