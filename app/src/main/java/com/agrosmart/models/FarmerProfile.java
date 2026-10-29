package com.agrosmart.models;

import java.io.Serializable;

public class FarmerProfile implements Serializable {
    private String farmerPhone;
    private String fullName;
    private String state = "Maharashtra";
    private String district = "Baramati";
    private Double landAreaAcres = 2.5;
    private String farmerCategory = "SMALL_MARGINAL"; // "SMALL_MARGINAL", "LARGE", "SC_ST", "WOMEN", "GENERAL"
    private String primaryCrops = "Soybean, Cotton";
    private Double annualIncome = 120000.0;
    private Boolean hasIrrigation = true;
    private String ownershipStatus = "OWNER";         // "OWNER", "TENANT", "SHARECROPPER"

    public FarmerProfile() {}

    public FarmerProfile(String farmerPhone, String fullName, String state, String district,
                         Double landAreaAcres, String farmerCategory, String primaryCrops,
                         Double annualIncome, Boolean hasIrrigation, String ownershipStatus) {
        this.farmerPhone = farmerPhone;
        this.fullName = fullName;
        this.state = state;
        this.district = district;
        this.landAreaAcres = landAreaAcres;
        this.farmerCategory = farmerCategory;
        this.primaryCrops = primaryCrops;
        this.annualIncome = annualIncome;
        this.hasIrrigation = hasIrrigation;
        this.ownershipStatus = ownershipStatus;
    }

    public String getFarmerPhone() { return farmerPhone; }
    public void setFarmerPhone(String farmerPhone) { this.farmerPhone = farmerPhone; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getState() { return state != null ? state : "Maharashtra"; }
    public void setState(String state) { this.state = state; }

    public String getDistrict() { return district != null ? district : "Baramati"; }
    public void setDistrict(String district) { this.district = district; }

    public Double getLandAreaAcres() { return landAreaAcres != null ? landAreaAcres : 2.5; }
    public void setLandAreaAcres(Double landAreaAcres) { this.landAreaAcres = landAreaAcres; }

    public String getFarmerCategory() { return farmerCategory != null ? farmerCategory : "SMALL_MARGINAL"; }
    public void setFarmerCategory(String farmerCategory) { this.farmerCategory = farmerCategory; }

    public String getPrimaryCrops() { return primaryCrops != null ? primaryCrops : "Soybean, Cotton"; }
    public void setPrimaryCrops(String primaryCrops) { this.primaryCrops = primaryCrops; }

    public Double getAnnualIncome() { return annualIncome != null ? annualIncome : 120000.0; }
    public void setAnnualIncome(Double annualIncome) { this.annualIncome = annualIncome; }

    public Boolean getHasIrrigation() { return hasIrrigation != null ? hasIrrigation : true; }
    public void setHasIrrigation(Boolean hasIrrigation) { this.hasIrrigation = hasIrrigation; }

    public String getOwnershipStatus() { return ownershipStatus != null ? ownershipStatus : "OWNER"; }
    public void setOwnershipStatus(String ownershipStatus) { this.ownershipStatus = ownershipStatus; }
}
