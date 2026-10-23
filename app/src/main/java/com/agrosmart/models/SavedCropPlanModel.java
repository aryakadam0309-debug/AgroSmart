package com.agrosmart.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class SavedCropPlanModel implements Serializable {

    @SerializedName("id")
    private String id;

    @SerializedName("farmerId")
    private Long farmerId;

    @SerializedName("farmerPhone")
    private String farmerPhone;

    @SerializedName("farmerName")
    private String farmerName;

    @SerializedName("district")
    private String district;

    @SerializedName("taluka")
    private String taluka;

    @SerializedName("season")
    private String season;

    @SerializedName("cropName")
    private String cropName;

    @SerializedName("variety")
    private String variety;

    @SerializedName("planType")
    private String planType; // "BALANCED_BEST_FIT", "LOW_WATER_RESILIENT", "DIVERSIFICATION"

    @SerializedName("waterAvailability")
    private String waterAvailability;

    @SerializedName("landType")
    private String landType;

    @SerializedName("soilType")
    private String soilType;

    @SerializedName("previousCrop")
    private String previousCrop;

    @SerializedName("investmentBudget")
    private String investmentBudget;

    @SerializedName("farmerGoal")
    private String farmerGoal;

    @SerializedName("durationDays")
    private String durationDays;

    @SerializedName("estimatedCostPerAcre")
    private Double estimatedCostPerAcre;

    @SerializedName("targetYield")
    private String targetYield;

    @SerializedName("suitabilityReason")
    private String suitabilityReason;

    @SerializedName("keyRisks")
    private String keyRisks;

    @SerializedName("universitySource")
    private String universitySource;

    @SerializedName("isPlanted")
    private Boolean isPlanted = false;

    @SerializedName("createdDate")
    private String createdDate;

    // Optional timeline milestones
    private List<String> timelineMilestones;

    public SavedCropPlanModel() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Long getFarmerId() { return farmerId; }
    public void setFarmerId(Long farmerId) { this.farmerId = farmerId; }

    public String getFarmerPhone() { return farmerPhone; }
    public void setFarmerPhone(String farmerPhone) { this.farmerPhone = farmerPhone; }

    public String getFarmerName() { return farmerName; }
    public void setFarmerName(String farmerName) { this.farmerName = farmerName; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public String getTaluka() { return taluka; }
    public void setTaluka(String taluka) { this.taluka = taluka; }

    public String getSeason() { return season; }
    public void setSeason(String season) { this.season = season; }

    public String getCropName() { return cropName; }
    public void setCropName(String cropName) { this.cropName = cropName; }

    public String getVariety() { return variety; }
    public void setVariety(String variety) { this.variety = variety; }

    public String getPlanType() { return planType; }
    public void setPlanType(String planType) { this.planType = planType; }

    public String getWaterAvailability() { return waterAvailability; }
    public void setWaterAvailability(String waterAvailability) { this.waterAvailability = waterAvailability; }

    public String getLandType() { return landType; }
    public void setLandType(String landType) { this.landType = landType; }

    public String getSoilType() { return soilType; }
    public void setSoilType(String soilType) { this.soilType = soilType; }

    public String getPreviousCrop() { return previousCrop; }
    public void setPreviousCrop(String previousCrop) { this.previousCrop = previousCrop; }

    public String getInvestmentBudget() { return investmentBudget; }
    public void setInvestmentBudget(String investmentBudget) { this.investmentBudget = investmentBudget; }

    public String getFarmerGoal() { return farmerGoal; }
    public void setFarmerGoal(String farmerGoal) { this.farmerGoal = farmerGoal; }

    public String getDurationDays() { return durationDays; }
    public void setDurationDays(String durationDays) { this.durationDays = durationDays; }

    public Double getEstimatedCostPerAcre() { return estimatedCostPerAcre; }
    public void setEstimatedCostPerAcre(Double estimatedCostPerAcre) { this.estimatedCostPerAcre = estimatedCostPerAcre; }

    public String getTargetYield() { return targetYield; }
    public void setTargetYield(String targetYield) { this.targetYield = targetYield; }

    public String getSuitabilityReason() { return suitabilityReason; }
    public void setSuitabilityReason(String suitabilityReason) { this.suitabilityReason = suitabilityReason; }

    public String getKeyRisks() { return keyRisks; }
    public void setKeyRisks(String keyRisks) { this.keyRisks = keyRisks; }

    public String getUniversitySource() { return universitySource; }
    public void setUniversitySource(String universitySource) { this.universitySource = universitySource; }

    public Boolean getIsPlanted() { return isPlanted != null && isPlanted; }
    public void setIsPlanted(Boolean isPlanted) { this.isPlanted = isPlanted; }

    public String getCreatedDate() { return createdDate; }
    public void setCreatedDate(String createdDate) { this.createdDate = createdDate; }

    public List<String> getTimelineMilestones() { return timelineMilestones; }
    public void setTimelineMilestones(List<String> timelineMilestones) { this.timelineMilestones = timelineMilestones; }
}
