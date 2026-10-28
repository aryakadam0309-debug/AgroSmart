package com.agrosmart.backend.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "crop_advisories")
public class CropAdvisory {

    @Id
    @Column(name = "id", length = 50)
    private String id;

    @Column(name = "title", length = 200, nullable = false)
    private String title;

    @Column(name = "crop_name", length = 100, nullable = false)
    private String cropName; // e.g. "Soybean", "Cotton", "Sugarcane"

    @Column(name = "variety", length = 100)
    private String variety;

    @Column(name = "problem_category", length = 100, nullable = false) // e.g. "Unusual Leaf Spots", "Wilting & Drying", "Pest Sightings"
    private String problemCategory;

    @Column(name = "location_coverage", length = 150) // e.g. "Vidarbha & Western Maharashtra", "Pune & Solapur", "All Maharashtra"
    private String locationCoverage;

    @Column(name = "advisory_text", columnDefinition = "TEXT", nullable = false)
    private String advisoryText;

    @Column(name = "advisory_text_marathi", columnDefinition = "TEXT")
    private String advisoryTextMarathi;

    @Column(name = "language", length = 30) // "ENGLISH", "MARATHI", "BILINGUAL"
    private String language = "BILINGUAL";

    @Column(name = "source_name", length = 150, nullable = false) // e.g. "Dr. PDKV Akola E-Pest Surveillance", "Maharashtra Dept of Agriculture CROPSAP"
    private String sourceName;

    @Column(name = "source_url", length = 300, nullable = false) // e.g. "https://www.pdkv.ac.in/?page_id=203", "https://krishi.maharashtra.gov.in/"
    private String sourceUrl;

    @Column(name = "publication_date", length = 50) // e.g. "Kharif 2026", "15 Oct 2026"
    private String publicationDate;

    @Column(name = "date_added", length = 50)
    private String dateAdded;

    @Column(name = "validity_period", length = 100) // e.g. "Kharif Season 2026", "Valid through November 2026"
    private String validityPeriod;

    @Column(name = "verification_status", length = 30) // "VERIFIED", "UNDER_REVIEW", "EXPIRED", "WITHDRAWN"
    private String verificationStatus = "VERIFIED";

    @Column(name = "reviewing_admin", length = 100)
    private String reviewingAdmin;

    @Column(name = "last_reviewed_at", length = 50)
    private String lastReviewedAt;

    @Column(name = "ingestion_type", length = 50) // "MANUAL_ENTRY", "AUTOMATED_IMPORT"
    private String ingestionType = "MANUAL_ENTRY";

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
