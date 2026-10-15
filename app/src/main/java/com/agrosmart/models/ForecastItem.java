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

    public String getDay() { return day; }
    public String getDate() { return date; }
    public double getTemp() { return temp; }
    public double getMinTemp() { return minTemp; }
    public double getMaxTemp() { return maxTemp; }
    public String getCondition() { return condition; }
    public String getIcon() { return icon; }
}
