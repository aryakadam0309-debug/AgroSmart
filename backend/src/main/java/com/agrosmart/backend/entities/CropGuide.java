package com.agrosmart.backend.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "crop_guides")
public class CropGuide {
    @Id
    private String id;

    @Column(nullable = false)
    private String cropName;

    private String scientificName;
    private String season;
    private String duration;
    private String soilType;
    private String waterRequirement;
    private String temperature;
    private String expectedYield;
    private String sowingWindow;
    private String harvestingWindow;

    @Column(columnDefinition = "TEXT")
    private String npkAdvisory;

    @Column(columnDefinition = "TEXT")
    private String growthStages;

    @Column(columnDefinition = "TEXT")
    private String bestPractices;

    public CropGuide() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCropName() { return cropName; }
    public void setCropName(String cropName) { this.cropName = cropName; }

    public String getScientificName() { return scientificName; }
    public void setScientificName(String scientificName) { this.scientificName = scientificName; }

    public String getSeason() { return season; }
    public void setSeason(String season) { this.season = season; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public String getSoilType() { return soilType; }
    public void setSoilType(String soilType) { this.soilType = soilType; }

    public String getWaterRequirement() { return waterRequirement; }
    public void setWaterRequirement(String waterRequirement) { this.waterRequirement = waterRequirement; }

    public String getTemperature() { return temperature; }
    public void setTemperature(String temperature) { this.temperature = temperature; }

    public String getExpectedYield() { return expectedYield; }
    public void setExpectedYield(String expectedYield) { this.expectedYield = expectedYield; }

    public String getSowingWindow() { return sowingWindow; }
    public void setSowingWindow(String sowingWindow) { this.sowingWindow = sowingWindow; }

    public String getHarvestingWindow() { return harvestingWindow; }
    public void setHarvestingWindow(String harvestingWindow) { this.harvestingWindow = harvestingWindow; }

    public String getNpkAdvisory() { return npkAdvisory; }
    public void setNpkAdvisory(String npkAdvisory) { this.npkAdvisory = npkAdvisory; }

    public String getGrowthStages() { return growthStages; }
    public void setGrowthStages(String growthStages) { this.growthStages = growthStages; }

    public String getBestPractices() { return bestPractices; }
    public void setBestPractices(String bestPractices) { this.bestPractices = bestPractices; }
}
