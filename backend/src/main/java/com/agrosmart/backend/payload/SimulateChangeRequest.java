package com.agrosmart.backend.payload;

import java.io.Serializable;

/**
 * Payload used to simulate an updated forecast introducing heavy rain
 * for controlled hackathon evaluation (Requirement 11).
 */
public class SimulateChangeRequest implements Serializable {

    private String farmerPhone;
    private String locationName;
    private int dayIndex = 2; // e.g. Day 2 (Saturday)
    private Double simulatedRainfallMm = 48.0; // Heavy rainfall
    private Integer simulatedRainProbability = 85; // 85%
    private String condition = "Heavy Rain";
    private boolean heavyRainWarning = true;

    public SimulateChangeRequest() {}

    public String getFarmerPhone() { return farmerPhone; }
    public void setFarmerPhone(String farmerPhone) { this.farmerPhone = farmerPhone; }

    public String getLocationName() { return locationName; }
    public void setLocationName(String locationName) { this.locationName = locationName; }

    public int getDayIndex() { return dayIndex; }
    public void setDayIndex(int dayIndex) { this.dayIndex = dayIndex; }

    public Double getSimulatedRainfallMm() { return simulatedRainfallMm; }
    public void setSimulatedRainfallMm(Double simulatedRainfallMm) { this.simulatedRainfallMm = simulatedRainfallMm; }

    public Integer getSimulatedRainProbability() { return simulatedRainProbability; }
    public void setSimulatedRainProbability(Integer simulatedRainProbability) { this.simulatedRainProbability = simulatedRainProbability; }

    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }

    public boolean isHeavyRainWarning() { return heavyRainWarning; }
    public void setHeavyRainWarning(boolean heavyRainWarning) { this.heavyRainWarning = heavyRainWarning; }
}
