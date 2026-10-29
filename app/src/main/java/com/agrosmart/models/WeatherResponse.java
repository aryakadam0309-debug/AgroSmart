package com.agrosmart.models;

import java.io.Serializable;
import java.util.List;

public class WeatherResponse implements Serializable {
    private String cityName;
    private double temp;
    private double feelsLike;
    private double tempMin;
    private double tempMax;
    private String condition;
    private String description;
    private int humidity;
    private double windSpeed;
    private int pressure;
    private int rainChance;
    private List<ForecastItem> forecast;

    // Enhanced metrics & advisory layer (PS 5.4)
    private double rainfallMm = 0.0;
    private String overallRiskLevel = "LOW";
    private String fiveDaySummary;
    private List<FarmingAdvisory> advisories;
    private WeatherAdvisoryChange latestAdvisoryChange;

    public WeatherResponse() {}

    public WeatherResponse(String cityName, double temp, double feelsLike, double tempMin, double tempMax,
                           String condition, String description, int humidity, double windSpeed, int pressure, int rainChance) {
        this.cityName = cityName;
        this.temp = temp;
        this.feelsLike = feelsLike;
        this.tempMin = tempMin;
        this.tempMax = tempMax;
        this.condition = condition;
        this.description = description;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.pressure = pressure;
        this.rainChance = rainChance;
    }

    public String getCityName() { return cityName; }
    public void setCityName(String cityName) { this.cityName = cityName; }
    public String getLocation() { return cityName; }

    public double getTemp() { return temp; }
    public void setTemp(double temp) { this.temp = temp; }
    public double getCurrentTemp() { return temp; }

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

    public List<ForecastItem> getForecast() { return forecast; }
    public void setForecast(List<ForecastItem> forecast) { this.forecast = forecast; }

    public double getRainfallMm() { return rainfallMm; }
    public void setRainfallMm(double rainfallMm) { this.rainfallMm = rainfallMm; }

    public String getOverallRiskLevel() { return overallRiskLevel; }
    public void setOverallRiskLevel(String overallRiskLevel) { this.overallRiskLevel = overallRiskLevel; }

    public String getFiveDaySummary() { return fiveDaySummary; }
    public void setFiveDaySummary(String fiveDaySummary) { this.fiveDaySummary = fiveDaySummary; }

    public List<FarmingAdvisory> getAdvisories() { return advisories; }
    public void setAdvisories(List<FarmingAdvisory> advisories) { this.advisories = advisories; }

    public WeatherAdvisoryChange getLatestAdvisoryChange() { return latestAdvisoryChange; }
    public void setLatestAdvisoryChange(WeatherAdvisoryChange latestAdvisoryChange) { this.latestAdvisoryChange = latestAdvisoryChange; }
}
