package com.agrosmart.backend.payload;

import com.agrosmart.backend.entities.WeatherAdvisoryChange;
import com.agrosmart.backend.entities.WeatherForecastSnapshot;

import java.io.Serializable;
import java.util.List;

/**
 * Top-level response returned by the backend weather endpoint.
 * Contains current weather metrics, 5-day daily forecast snapshots,
 * actionable smart farming advisories, and the latest previous vs. updated advisory change.
 */
public class WeatherForecastDto implements Serializable {

    private String cityName;
    private double currentTemp;
    private double feelsLike;
    private double tempMin;
    private double tempMax;
    private String condition;
    private String description;
    private int humidity;
    private double windSpeed;
    private int pressure;
    private int rainChance;
    private double rainfallMm;
    private String overallRiskLevel; // "LOW", "MODERATE", "HIGH"
    private String fiveDaySummary;
    private String retrievedAt;
    private String provider;

    private List<WeatherForecastSnapshot> forecast;
    private List<FarmingAdvisoryDto> advisories;
    private WeatherAdvisoryChange latestAdvisoryChange;

    public WeatherForecastDto() {}

    public String getCityName() { return cityName; }
    public void setCityName(String cityName) { this.cityName = cityName; }

    public double getCurrentTemp() { return currentTemp; }
    public void setCurrentTemp(double currentTemp) { this.currentTemp = currentTemp; }

    public double getFeelsLike() { return feelsLike; }
    public void setFeelsLike(double feelsLike) { this.feelsLike = feelsLike; }

    public double getTempMin() { return tempMin; }
    public void setTempMin(double tempMin) { this.tempMin = tempMin; }

    public double getTempMax() { return tempMax; }
    public void setTempMax(double tempMax) { this.tempMax = tempMax; }

    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getHumidity() { return humidity; }
    public void setHumidity(int humidity) { this.humidity = humidity; }

    public double getWindSpeed() { return windSpeed; }
    public void setWindSpeed(double windSpeed) { this.windSpeed = windSpeed; }

    public int getPressure() { return pressure; }
    public void setPressure(int pressure) { this.pressure = pressure; }

    public int getRainChance() { return rainChance; }
    public void setRainChance(int rainChance) { this.rainChance = rainChance; }

    public double getRainfallMm() { return rainfallMm; }
    public void setRainfallMm(double rainfallMm) { this.rainfallMm = rainfallMm; }

    public String getOverallRiskLevel() { return overallRiskLevel; }
    public void setOverallRiskLevel(String overallRiskLevel) { this.overallRiskLevel = overallRiskLevel; }

    public String getFiveDaySummary() { return fiveDaySummary; }
    public void setFiveDaySummary(String fiveDaySummary) { this.fiveDaySummary = fiveDaySummary; }

    public String getRetrievedAt() { return retrievedAt; }
    public void setRetrievedAt(String retrievedAt) { this.retrievedAt = retrievedAt; }

    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }

    public List<WeatherForecastSnapshot> getForecast() { return forecast; }
    public void setForecast(List<WeatherForecastSnapshot> forecast) { this.forecast = forecast; }

    public List<FarmingAdvisoryDto> getAdvisories() { return advisories; }
    public void setAdvisories(List<FarmingAdvisoryDto> advisories) { this.advisories = advisories; }

    public WeatherAdvisoryChange getLatestAdvisoryChange() { return latestAdvisoryChange; }
    public void setLatestAdvisoryChange(WeatherAdvisoryChange latestAdvisoryChange) { this.latestAdvisoryChange = latestAdvisoryChange; }
}
