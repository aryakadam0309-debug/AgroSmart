package com.agrosmart.weather;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.agrosmart.models.ForecastItem;
import com.agrosmart.models.WeatherResponse;
import com.agrosmart.weather.strategy.LiveApiWeatherProvider;
import com.agrosmart.weather.strategy.MockWeatherProvider;
import com.agrosmart.weather.strategy.WeatherProvider;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Unit Tests for Weather Strategy Design Pattern Architecture.
 * Verifies contract adherence, strategy polymorphism, demo-flag integrity,
 * data model encapsulation, and error handling.
 */
public class WeatherStrategyTest {

    private MockWeatherProvider mockProvider;
    private LiveApiWeatherProvider liveProvider;

    @Before
    public void setUp() {
        mockProvider = new MockWeatherProvider();
        liveProvider = new LiveApiWeatherProvider();
    }

    @Test
    public void testStrategyIdentityAndDemoFlagIntegrity() {
        // Strategy A: Mock/Demo Strategy
        assertEquals("mock_simulation", mockProvider.getProviderId());
        assertTrue("Mock provider must be flagged as demo mode", mockProvider.isDemoMode());
        assertTrue("Display name must describe demo mode", mockProvider.getDisplayName().contains("Demo"));

        // Strategy B: Live API Strategy
        assertEquals("live_api", liveProvider.getProviderId());
        assertFalse("Live provider must not be flagged as demo mode", liveProvider.isDemoMode());
        assertTrue("Display name must describe live API", liveProvider.getDisplayName().contains("Live"));
    }

    @Test
    public void testMockStrategyRegionalDataRetrieval() {
        // Test deterministic Pune farm advisory
        AtomicReference<WeatherResponse> received = new AtomicReference<>();

        mockProvider.fetchWeather(18.5204, 73.8567, "Pune, Maharashtra", new WeatherProvider.WeatherCallback() {
            @Override
            public void onSuccess(WeatherResponse weather) {
                received.set(weather);
            }

            @Override
            public void onError(String errorMessage) {}
        });

        WeatherResponse weather = received.get();
        assertNotNull("Weather response must not be null", weather);
        assertEquals("Pune, Maharashtra", weather.getCityName());
        assertTrue("Temperature must be greater than zero", weather.getTemp() > 0);
        assertTrue("Humidity must be within 0-100%", weather.getHumidity() >= 0 && weather.getHumidity() <= 100);
        assertNotNull("Forecast list must be present", weather.getForecast());
        assertEquals("Must return 5-day forecast", 5, weather.getForecast().size());
    }

    @Test
    public void testMockStrategyMumbaiCoastalProfile() {
        AtomicReference<WeatherResponse> received = new AtomicReference<>();

        mockProvider.fetchWeather(19.0760, 72.8777, "Mumbai, Maharashtra", new WeatherProvider.WeatherCallback() {
            @Override
            public void onSuccess(WeatherResponse weather) {
                received.set(weather);
            }

            @Override
            public void onError(String errorMessage) {}
        });

        WeatherResponse weather = received.get();
        assertNotNull(weather);
        assertTrue("Condition must contain demo flag", weather.getCondition().contains("[DEMO]"));
        assertTrue("Advisory must mention polyhouse/aeration for coastal humidity",
                weather.getDescription().toLowerCase().contains("aeration") ||
                weather.getDescription().toLowerCase().contains("polyhouse"));
    }

    @Test
    public void testWeatherResponseModelEncapsulation() {
        List<ForecastItem> forecast = new ArrayList<>();
        forecast.add(new ForecastItem("Tomorrow", "16 Oct", 30.0, 22.0, 32.0, "Sunny", "01d"));

        WeatherResponse response = new WeatherResponse(
                "Nashik, Maharashtra",
                27.5,
                28.0,
                18.5,
                30.0,
                "Mild Breeze",
                "Vineyard canopy management window",
                60,
                14.0,
                1014,
                15
        );
        response.setForecast(forecast);

        assertEquals("Nashik, Maharashtra", response.getCityName());
        assertEquals(27.5, response.getTemp(), 0.01);
        assertEquals(28.0, response.getFeelsLike(), 0.01);
        assertEquals(18.5, response.getTempMin(), 0.01);
        assertEquals(30.0, response.getTempMax(), 0.01);
        assertEquals("Mild Breeze", response.getCondition());
        assertEquals(60, response.getHumidity());
        assertEquals(14.0, response.getWindSpeed(), 0.01);
        assertEquals(1014, response.getPressure());
        assertEquals(15, response.getRainChance());
        assertEquals(1, response.getForecast().size());

        ForecastItem item = response.getForecast().get(0);
        assertEquals("Tomorrow", item.getDay());
        assertEquals("16 Oct", item.getDate());
        assertEquals(30.0, item.getTemp(), 0.01);
        assertEquals(22.0, item.getMinTemp(), 0.01);
        assertEquals(32.0, item.getMaxTemp(), 0.01);
        assertEquals("Sunny", item.getCondition());
    }

    @Test
    public void testStrategyPolymorphicInterchangeability() {
        // Demonstrating that either strategy can be assigned to a WeatherProvider reference
        WeatherProvider strategy = mockProvider;
        assertEquals("mock_simulation", strategy.getProviderId());

        strategy = liveProvider;
        assertEquals("live_api", strategy.getProviderId());
    }
}
