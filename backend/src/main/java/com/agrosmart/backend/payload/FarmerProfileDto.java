package com.agrosmart.backend.payload;

public class FarmerProfileDto {
    private String farmerPhone;
    private String fullName;
    private String state = "Maharashtra";
    private String district;
    private Double landAreaAcres;
    private String farmerCategory = "SMALL_MARGINAL"; // "SMALL_MARGINAL" (<=5 acres), "LARGE" (>5 acres), "SC_ST", "WOMEN", "GENERAL"
    private String primaryCrops;                      // e.g. "Soybean, Cotton, Wheat"
    private Double annualIncome;
    private Boolean hasIrrigation = false;
    private String ownershipStatus = "OWNER";         // "OWNER", "TENANT", "SHARECROPPER"

    public FarmerProfileDto() {}

    public FarmerProfileDto(String farmerPhone, String fullName, String state, String district,
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

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public Double getLandAreaAcres() { return landAreaAcres; }
    public void setLandAreaAcres(Double landAreaAcres) { this.landAreaAcres = landAreaAcres; }

    public String getFarmerCategory() { return farmerCategory; }
    public void setFarmerCategory(String farmerCategory) { this.farmerCategory = farmerCategory; }

    public String getPrimaryCrops() { return primaryCrops; }
    public void setPrimaryCrops(String primaryCrops) { this.primaryCrops = primaryCrops; }

    public Double getAnnualIncome() { return annualIncome; }
    public void setAnnualIncome(Double annualIncome) { this.annualIncome = annualIncome; }

    public Boolean getHasIrrigation() { return hasIrrigation; }
    public void setHasIrrigation(Boolean hasIrrigation) { this.hasIrrigation = hasIrrigation; }

    public String getOwnershipStatus() { return ownershipStatus; }
    public void setOwnershipStatus(String ownershipStatus) { this.ownershipStatus = ownershipStatus; }
}
