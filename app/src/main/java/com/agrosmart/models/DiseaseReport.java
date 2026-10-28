package com.agrosmart.models;

import java.io.Serializable;

public class DiseaseReport implements Serializable {
    private String id;
    private String farmerPhone;
    private String farmerName;
    private String cropName;
    private String variety;
    private String affectedPart;
    private String symptoms;
    private String severity;
    private String village;
    private String taluka;
    private String district;
    private Double latitude;
    private Double longitude;
    private Boolean locationConsent = false;
    private Boolean isDuplicate = false;
    private String duplicateOfReportId;
    private String matchedAdvisoryId;
    private String imageUrl;
    private String reportedAt;
    private String status; // PENDING_REVIEW, UNDER_REVIEW, VERIFIED, NOT_VERIFIED, DUPLICATE, RESOLVED
    private String adminAdvisory;
    private String problemCategory;
    private String growthStage;
    private String reviewNote;
    private String adminGuidance;
    private String adminSource;
    private String reviewedBy;
    private String reviewedAt;

    public DiseaseReport() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFarmerPhone() { return farmerPhone; }
    public void setFarmerPhone(String farmerPhone) { this.farmerPhone = farmerPhone; }

    public String getFarmerName() { return farmerName; }
    public void setFarmerName(String farmerName) { this.farmerName = farmerName; }

    public String getCropName() { return cropName; }
    public void setCropName(String cropName) { this.cropName = cropName; }

    public String getVariety() { return variety; }
    public void setVariety(String variety) { this.variety = variety; }

    public String getAffectedPart() { return affectedPart; }
    public void setAffectedPart(String affectedPart) { this.affectedPart = affectedPart; }

    public String getSymptoms() { return symptoms; }
    public void setSymptoms(String symptoms) { this.symptoms = symptoms; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public String getVillage() { return village; }
    public void setVillage(String village) { this.village = village; }

    public String getTaluka() { return taluka; }
    public void setTaluka(String taluka) { this.taluka = taluka; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Boolean getLocationConsent() { return locationConsent; }
    public void setLocationConsent(Boolean locationConsent) { this.locationConsent = locationConsent; }

    public Boolean getIsDuplicate() { return isDuplicate; }
    public void setIsDuplicate(Boolean isDuplicate) { this.isDuplicate = isDuplicate; }

    public String getDuplicateOfReportId() { return duplicateOfReportId; }
    public void setDuplicateOfReportId(String duplicateOfReportId) { this.duplicateOfReportId = duplicateOfReportId; }

    public String getMatchedAdvisoryId() { return matchedAdvisoryId; }
    public void setMatchedAdvisoryId(String matchedAdvisoryId) { this.matchedAdvisoryId = matchedAdvisoryId; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getReportedAt() { return reportedAt; }
    public void setReportedAt(String reportedAt) { this.reportedAt = reportedAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getAdminAdvisory() { return adminAdvisory; }
    public void setAdminAdvisory(String adminAdvisory) { this.adminAdvisory = adminAdvisory; }

    public String getProblemCategory() { return problemCategory; }
    public void setProblemCategory(String problemCategory) { this.problemCategory = problemCategory; }

    public String getGrowthStage() { return growthStage; }
    public void setGrowthStage(String growthStage) { this.growthStage = growthStage; }

    public String getReviewNote() { return reviewNote; }
    public void setReviewNote(String reviewNote) { this.reviewNote = reviewNote; }

    public String getAdminGuidance() { return adminGuidance; }
    public void setAdminGuidance(String adminGuidance) { this.adminGuidance = adminGuidance; }

    public String getAdminSource() { return adminSource; }
    public void setAdminSource(String adminSource) { this.adminSource = adminSource; }

    public String getReviewedBy() { return reviewedBy; }
    public void setReviewedBy(String reviewedBy) { this.reviewedBy = reviewedBy; }

    public String getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(String reviewedAt) { this.reviewedAt = reviewedAt; }
}
