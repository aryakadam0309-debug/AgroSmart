package com.agrosmart.models;

import java.io.Serializable;

public class CropGuide implements Serializable {
    private String id;
    private String cropName;
    private String scientificName;
    private String season; // "Kharif", "Rabi", "Zaid", "Annual"
    private String duration; // e.g. "110-130 Days"
    private String soilType;
    private String waterRequirement;
    private String temperature;
    private String expectedYield;
    private String sowingWindow;
    private String harvestingWindow;
    private String npkAdvisory;
    private String growthStages;
    private String bestPractices;

    public CropGuide() {}

    public CropGuide(String id, String cropName, String scientificName, String season,
                     String duration, String soilType, String waterRequirement,
                     String temperature, String expectedYield, String sowingWindow,
                     String harvestingWindow, String npkAdvisory, String growthStages,
                     String bestPractices) {
        this.id = id;
        this.cropName = cropName;
        this.scientificName = scientificName;
        this.season = season;
        this.duration = duration;
        this.soilType = soilType;
        this.waterRequirement = waterRequirement;
        this.temperature = temperature;
        this.expectedYield = expectedYield;
        this.sowingWindow = sowingWindow;
        this.harvestingWindow = harvestingWindow;
        this.npkAdvisory = npkAdvisory;
        this.growthStages = growthStages;
        this.bestPractices = bestPractices;
    }

    public String getId() { return id; }
    public String getCropName() { return cropName; }
    public String getScientificName() { return scientificName; }
    public String getSeason() { return season; }
    public String getDuration() { return duration; }
    public String getSoilType() { return soilType; }
    public String getWaterRequirement() { return waterRequirement; }
    public String getTemperature() { return temperature; }
    public String getExpectedYield() { return expectedYield; }
    public String getSowingWindow() { return sowingWindow; }
    public String getHarvestingWindow() { return harvestingWindow; }
    public String getNpkAdvisory() { return npkAdvisory; }
    public String getGrowthStages() { return growthStages; }
    public String getBestPractices() { return bestPractices; }
}
