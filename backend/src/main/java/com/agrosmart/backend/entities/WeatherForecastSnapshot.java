package com.agrosmart.backend.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "weather_forecast_snapshots")
public class WeatherForecastSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String farmerPhone;
    private String locationName;
    private Double latitude;
    private Double longitude;

    private String forecastDate; // e.g. "2026-11-01"
    private String dayName;      // e.g. "Saturday"

    private Double minTemp;
    private Double maxTemp;
    private Double temp;
    private Integer humidity;
    private Double windSpeed;
    private Integer rainProbability;
    private Double expectedRainfallMm;

    private String condition;
    private String conditionDescription;
    private String riskLevel; // "LOW", "MODERATE", "HIGH"
    private boolean isHeavyRain;

    private LocalDateTime retrievedAt = LocalDateTime.now();
    private String provider; // "OpenWeatherMap", "Open-Meteo"

    public WeatherForecastSnapshot() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFarmerPhone() { return farmerPhone; }
    public void setFarmerPhone(String farmerPhone) { this.farmerPhone = farmerPhone; }

    public String getLocationName() { return locationName; }
    public void setLocationName(String locationName) { this.locationName = locationName; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public String getForecastDate() { return forecastDate; }
    public void setForecastDate(String forecastDate) { this.forecastDate = forecastDate; }

    public String getDayName() { return dayName; }
    public void setDayName(String dayName) { this.dayName = dayName; }

    public Double getMinTemp() { return minTemp; }
    public void setMinTemp(Double minTemp) { this.minTemp = minTemp; }

    public Double getMaxTemp() { return maxTemp; }
    public void setMaxTemp(Double maxTemp) { this.maxTemp = maxTemp; }

    public Double getTemp() { return temp; }
    public void setTemp(Double temp) { this.temp = temp; }

    public Integer getHumidity() { return humidity; }
    public void setHumidity(Integer humidity) { this.humidity = humidity; }

    public Double getWindSpeed() { return windSpeed; }
    public void setWindSpeed(Double windSpeed) { this.windSpeed = windSpeed; }

    public Integer getRainProbability() { return rainProbability; }
    public void setRainProbability(Integer rainProbability) { this.rainProbability = rainProbability; }

    public Double getExpectedRainfallMm() { return expectedRainfallMm; }
    public void setExpectedRainfallMm(Double expectedRainfallMm) { this.expectedRainfallMm = expectedRainfallMm; }

    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }

    public String getConditionDescription() { return conditionDescription; }
    public void setConditionDescription(String conditionDescription) { this.conditionDescription = conditionDescription; }

    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }

    public boolean isHeavyRain() { return isHeavyRain; }
    public void setHeavyRain(boolean heavyRain) { isHeavyRain = heavyRain; }

    public LocalDateTime getRetrievedAt() { return retrievedAt; }
    public void setRetrievedAt(LocalDateTime retrievedAt) { this.retrievedAt = retrievedAt; }

    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }
}
