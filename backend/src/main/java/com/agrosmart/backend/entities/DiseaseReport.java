package com.agrosmart.backend.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "crop_disease_reports")
public class DiseaseReport {

    @Id
    @Column(name = "id", length = 50)
    private String id;

    @Column(name = "farmer_phone", length = 20, nullable = false)
    private String farmerPhone;

    @Column(name = "farmer_name", length = 100)
    private String farmerName;

    @Column(name = "crop_name", length = 100, nullable = false)
    private String cropName;

    @Column(name = "variety", length = 100)
    private String variety;

    @Column(name = "affected_part", length = 50)
    private String affectedPart;

    @Column(name = "symptoms", columnDefinition = "TEXT", nullable = false)
    private String symptoms;

    @Column(name = "severity", length = 30) // LOW, MEDIUM, SEVERE
    private String severity;

    @Column(name = "village", length = 100)
    private String village;

    @Column(name = "taluka", length = 100)
    private String taluka;

    @Column(name = "district", length = 100)
    private String district;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "location_consent")
    private Boolean locationConsent = false;

    @Column(name = "is_duplicate")
    private Boolean isDuplicate = false;

    @Column(name = "duplicate_of_report_id", length = 50)
    private String duplicateOfReportId;

    @Column(name = "matched_advisory_id", length = 50)
    private String matchedAdvisoryId;

    @Column(name = "image_url", length = 255)
    private String imageUrl;

    @Column(name = "reported_at", length = 30)
    private String reportedAt;

    @Column(name = "status", length = 30) // REPORTED, UNDER_REVIEW, PRELIMINARY_SIGNAL, VERIFIED_ALERT, RESOLVED, PENDING_REVIEW, VERIFIED, NOT_VERIFIED
    private String status;

    @Column(name = "admin_advisory", columnDefinition = "TEXT")
    private String adminAdvisory;

    @Column(name = "problem_category", length = 100)
    private String problemCategory;

    @Column(name = "growth_stage", length = 50)
    private String growthStage;

    @Column(name = "review_note", columnDefinition = "TEXT")
    private String reviewNote;

    @Column(name = "admin_guidance", columnDefinition = "TEXT")
    private String adminGuidance;

    @Column(name = "admin_source", length = 150)
    private String adminSource;

    @Column(name = "reviewed_by", length = 50)
    private String reviewedBy;

    @Column(name = "reviewed_at", length = 50)
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

    public String getAffectedPart() { return affectedPart; }
    public void setAffectedPart(String affectedPart) { this.affectedPart = affectedPart; }

    public String getSymptoms() { return symptoms; }
    public void setSymptoms(String symptoms) { this.symptoms = symptoms; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public String getVillage() { return village; }
    public void setVillage(String village) { this.village = village; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

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

    public String getVariety() { return variety; }
    public void setVariety(String variety) { this.variety = variety; }

    public String getTaluka() { return taluka; }
    public void setTaluka(String taluka) { this.taluka = taluka; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Boolean getLocationConsent() { return locationConsent; }
    public void setLocationConsent(Boolean locationConsent) { this.locationConsent = locationConsent; }

    public Boolean getIsDuplicate() { return isDuplicate; }
    public void setIsDuplicate(Boolean duplicate) { isDuplicate = duplicate; }

    public String getDuplicateOfReportId() { return duplicateOfReportId; }
    public void setDuplicateOfReportId(String duplicateOfReportId) { this.duplicateOfReportId = duplicateOfReportId; }

    public String getMatchedAdvisoryId() { return matchedAdvisoryId; }
    public void setMatchedAdvisoryId(String matchedAdvisoryId) { this.matchedAdvisoryId = matchedAdvisoryId; }
}
