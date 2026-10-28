package com.agrosmart.models;

import java.io.Serializable;

public class CropAdvisory implements Serializable {
    private String id;
    private String title;
    private String cropName;
    private String variety;
    private String problemCategory;
    private String locationCoverage;
    private String advisoryText;
    private String advisoryTextMarathi;
    private String language;
    private String sourceName;
    private String sourceUrl;
    private String publicationDate;
    private String dateAdded;
    private String validityPeriod;
    private String verificationStatus;
    private String reviewingAdmin;
    private String lastReviewedAt;
    private String ingestionType;

    public CropAdvisory() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCropName() { return cropName; }
    public void setCropName(String cropName) { this.cropName = cropName; }

    public String getVariety() { return variety; }
    public void setVariety(String variety) { this.variety = variety; }

    public String getProblemCategory() { return problemCategory; }
    public void setProblemCategory(String problemCategory) { this.problemCategory = problemCategory; }

    public String getLocationCoverage() { return locationCoverage; }
    public void setLocationCoverage(String locationCoverage) { this.locationCoverage = locationCoverage; }

    public String getAdvisoryText() { return advisoryText; }
    public void setAdvisoryText(String advisoryText) { this.advisoryText = advisoryText; }

    public String getAdvisoryTextMarathi() { return advisoryTextMarathi; }
    public void setAdvisoryTextMarathi(String advisoryTextMarathi) { this.advisoryTextMarathi = advisoryTextMarathi; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public String getSourceName() { return sourceName; }
    public void setSourceName(String sourceName) { this.sourceName = sourceName; }

    public String getSourceUrl() { return sourceUrl; }
    public void setSourceUrl(String sourceUrl) { this.sourceUrl = sourceUrl; }

    public String getPublicationDate() { return publicationDate; }
    public void setPublicationDate(String publicationDate) { this.publicationDate = publicationDate; }

    public String getDateAdded() { return dateAdded; }
    public void setDateAdded(String dateAdded) { this.dateAdded = dateAdded; }

    public String getValidityPeriod() { return validityPeriod; }
    public void setValidityPeriod(String validityPeriod) { this.validityPeriod = validityPeriod; }

    public String getVerificationStatus() { return verificationStatus; }
    public void setVerificationStatus(String verificationStatus) { this.verificationStatus = verificationStatus; }

    public String getReviewingAdmin() { return reviewingAdmin; }
    public void setReviewingAdmin(String reviewingAdmin) { this.reviewingAdmin = reviewingAdmin; }

    public String getLastReviewedAt() { return lastReviewedAt; }
    public void setLastReviewedAt(String lastReviewedAt) { this.lastReviewedAt = lastReviewedAt; }

    public String getIngestionType() { return ingestionType; }
    public void setIngestionType(String ingestionType) { this.ingestionType = ingestionType; }
}
