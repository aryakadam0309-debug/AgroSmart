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
    private String region; // "Western Maharashtra", "Marathwada", "Vidarbha", "Konkan", "All Maharashtra"
    private String suitableSoilCategory; // "BLACK_COTTON", "RED_LATERITIC", "SANDY_LOAM", "ALLUVIAL", "ANY"

    public CropGuide() {}

    public CropGuide(String id, String cropName, String scientificName, String season,
                     String duration, String soilType, String waterRequirement,
                     String temperature, String expectedYield, String sowingWindow,
                     String harvestingWindow, String npkAdvisory, String growthStages,
                     String bestPractices) {
        this(id, cropName, scientificName, season, duration, soilType, waterRequirement,
                temperature, expectedYield, sowingWindow, harvestingWindow, npkAdvisory,
                growthStages, bestPractices, "All Maharashtra", "ANY");
    }

    public CropGuide(String id, String cropName, String scientificName, String season,
                     String duration, String soilType, String waterRequirement,
                     String temperature, String expectedYield, String sowingWindow,
                     String harvestingWindow, String npkAdvisory, String growthStages,
                     String bestPractices, String region, String suitableSoilCategory) {
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
        this.region = region != null ? region : "All Maharashtra";
        this.suitableSoilCategory = suitableSoilCategory != null ? suitableSoilCategory : "ANY";
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
    public String getRegion() { return region != null ? region : "All Maharashtra"; }
    public void setRegion(String region) { this.region = region; }
    public String getSuitableSoilCategory() { return suitableSoilCategory != null ? suitableSoilCategory : "ANY"; }
    public void setSuitableSoilCategory(String suitableSoilCategory) { this.suitableSoilCategory = suitableSoilCategory; }
}
