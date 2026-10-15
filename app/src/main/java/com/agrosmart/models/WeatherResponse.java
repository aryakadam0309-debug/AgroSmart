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
}
