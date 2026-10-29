package com.agrosmart.models;

import java.io.Serializable;

public class ForecastItem implements Serializable {
    private String day;
    private String date;
    private double temp;
    private double minTemp;
    private double maxTemp;
    private String condition;
    private String icon;

    // Enhanced metrics for Smart Farming & Rain Alert (PS 5.4)
    private int humidity = 60;
    private double windSpeed = 10.0;
    private int rainProbability = 15;
    private double expectedRainfallMm = 0.0;
    private String riskLevel = "LOW"; // "LOW", "MODERATE", "HIGH"
    private boolean isHeavyRain = false;

    public ForecastItem() {}

    public ForecastItem(String day, String date, double temp, double minTemp, double maxTemp, String condition, String icon) {
        this.day = day;
        this.date = date;
        this.temp = temp;
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.condition = condition;
        this.icon = icon;
    }

    public ForecastItem(String day, String date, double temp, double minTemp, double maxTemp,
                        String condition, String icon, int humidity, double windSpeed,
                        int rainProbability, double expectedRainfallMm, String riskLevel, boolean isHeavyRain) {
        this(day, date, temp, minTemp, maxTemp, condition, icon);
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.rainProbability = rainProbability;
        this.expectedRainfallMm = expectedRainfallMm;
        this.riskLevel = riskLevel;
        this.isHeavyRain = isHeavyRain;
    }

    public String getDay() { return day; }
    public void setDay(String day) { this.day = day; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public double getTemp() { return temp; }
    public void setTemp(double temp) { this.temp = temp; }

    public double getMinTemp() { return minTemp; }
    public void setMinTemp(double minTemp) { this.minTemp = minTemp; }

    public double getMaxTemp() { return maxTemp; }
    public void setMaxTemp(double maxTemp) { this.maxTemp = maxTemp; }

    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public int getHumidity() { return humidity; }
    public void setHumidity(int humidity) { this.humidity = humidity; }

    public double getWindSpeed() { return windSpeed; }
    public void setWindSpeed(double windSpeed) { this.windSpeed = windSpeed; }

    public int getRainProbability() { return rainProbability; }
    public void setRainProbability(int rainProbability) { this.rainProbability = rainProbability; }

    public int getRainChance() { return rainProbability; }
    public void setRainChance(int rainChance) { this.rainProbability = rainChance; }

    public double getExpectedRainfallMm() { return expectedRainfallMm; }
    public void setExpectedRainfallMm(double expectedRainfallMm) { this.expectedRainfallMm = expectedRainfallMm; }

    public double getRainfallMm() { return expectedRainfallMm; }
    public void setRainfallMm(double rainfallMm) { this.expectedRainfallMm = rainfallMm; }

    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }

    public boolean isHeavyRain() { return isHeavyRain; }
    public void setHeavyRain(boolean heavyRain) { isHeavyRain = heavyRain; }
}
