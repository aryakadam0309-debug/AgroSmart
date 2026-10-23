package com.agrosmart.backend.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "saved_crop_plans")
public class SavedCropPlan {

    @Id
    private String id;

    @Column(name = "farmer_id")
    private Long farmerId;

    @Column(name = "farmer_phone", nullable = false)
    private String farmerPhone;

    @Column(name = "farmer_name")
    private String farmerName;

    @Column(name = "district", nullable = false)
    private String district;

    @Column(name = "taluka")
    private String taluka;

    @Column(name = "season", nullable = false)
    private String season;

    @Column(name = "crop_name", nullable = false)
    private String cropName;

    @Column(name = "variety")
    private String variety;

    @Column(name = "plan_type", nullable = false)
    private String planType; // "BALANCED_BEST_FIT", "LOW_WATER_RESILIENT", "DIVERSIFICATION"

    @Column(name = "water_availability")
    private String waterAvailability;

    @Column(name = "land_type")
    private String landType;

    @Column(name = "soil_type")
    private String soilType;

    @Column(name = "previous_crop")
    private String previousCrop;

    @Column(name = "investment_budget")
    private String investmentBudget;

    @Column(name = "farmer_goal")
    private String farmerGoal;

    @Column(name = "duration_days")
    private String durationDays;

    @Column(name = "estimated_cost_per_acre")
    private Double estimatedCostPerAcre;

    @Column(name = "target_yield")
    private String targetYield;

    @Column(name = "suitability_reason", columnDefinition = "TEXT")
    private String suitabilityReason;

    @Column(name = "key_risks", columnDefinition = "TEXT")
    private String keyRisks;

    @Column(name = "university_source")
    private String universitySource;

    @Column(name = "is_planted")
    private Boolean isPlanted = false;

    @Column(name = "created_date")
    private String createdDate;

    @PrePersist
    public void prePersist() {
        if (id == null || id.trim().isEmpty()) {
            id = "PLAN-" + System.currentTimeMillis();
        }
        if (createdDate == null || createdDate.trim().isEmpty()) {
            createdDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a"));
        }
    }

    public SavedCropPlan() {}

    // Getters and Setters
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

    public Boolean getIsPlanted() { return isPlanted; }
    public void setIsPlanted(Boolean isPlanted) { this.isPlanted = isPlanted; }

    public String getCreatedDate() { return createdDate; }
    public void setCreatedDate(String createdDate) { this.createdDate = createdDate; }
}
