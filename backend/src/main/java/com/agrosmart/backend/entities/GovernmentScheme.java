package com.agrosmart.backend.entities;

import jakarta.persistence.*;

/**
 * Government Scheme Entity (PS 5.3).
 * Stores comprehensive verified government agricultural schemes,
 * official portal references, eligibility constraints, and application status.
 */
@Entity
@Table(name = "government_schemes")
public class GovernmentScheme {

    @Id
    private String id; // e.g. "SCH01"

    @Column(nullable = false)
    private String title;

    private String ministry;
    private String subsidyAmount;

    @Column(columnDefinition = "TEXT")
    private String eligibility;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String benefits;

    @Column(columnDefinition = "TEXT")
    private String requiredDocuments;

    @Column(columnDefinition = "TEXT")
    private String applicationProcess;

    private String officialUrl;
    private String sourceUrl;
    private String helpline;
    private String category; // "Financial Assistance", "Crop Insurance", "Machinery", "Irrigation", "Credit / Loans", "Soil Health"
    private String level;    // "CENTRAL", "STATE"
    private String applicableState; // "ALL_INDIA", "Maharashtra", "Punjab", etc.
    private String deadline; // e.g. "Ongoing (Open)", "31 July 2026", "Closed"
    private String applicationStatus = "OPEN"; // "OPEN", "CLOSED", "CONTINUOUS", "UNVERIFIED"
    private String lastVerifiedDate; // e.g. "2026-10-25"

    // Structured Rule Thresholds for Domain Eligibility Matching
    private Double minLandAcres;
    private Double maxLandAcres;
    private String allowedFarmerCategories; // "SMALL_MARGINAL,ALL", "SC_ST", etc.
    private String allowedCrops;            // "Soybean,Cotton,Wheat,Rice,ALL"
    private Double maxAnnualIncome;
    private Boolean requiresLandOwnership = false;

    public GovernmentScheme() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMinistry() { return ministry; }
    public void setMinistry(String ministry) { this.ministry = ministry; }

    public String getSubsidyAmount() { return subsidyAmount; }
    public void setSubsidyAmount(String subsidyAmount) { this.subsidyAmount = subsidyAmount; }

    public String getEligibility() { return eligibility; }
    public void setEligibility(String eligibility) { this.eligibility = eligibility; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getBenefits() { return benefits; }
    public void setBenefits(String benefits) { this.benefits = benefits; }

    public String getRequiredDocuments() { return requiredDocuments; }
    public void setRequiredDocuments(String requiredDocuments) { this.requiredDocuments = requiredDocuments; }

    public String getApplicationProcess() { return applicationProcess; }
    public void setApplicationProcess(String applicationProcess) { this.applicationProcess = applicationProcess; }

    public String getOfficialUrl() { return officialUrl; }
    public void setOfficialUrl(String officialUrl) { this.officialUrl = officialUrl; }

    public String getSourceUrl() { return sourceUrl; }
    public void setSourceUrl(String sourceUrl) { this.sourceUrl = sourceUrl; }

    public String getHelpline() { return helpline; }
    public void setHelpline(String helpline) { this.helpline = helpline; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }

    public String getApplicableState() { return applicableState; }
    public void setApplicableState(String applicableState) { this.applicableState = applicableState; }

    public String getDeadline() { return deadline; }
    public void setDeadline(String deadline) { this.deadline = deadline; }

    public String getApplicationStatus() { return applicationStatus; }
    public void setApplicationStatus(String applicationStatus) { this.applicationStatus = applicationStatus; }

    public String getLastVerifiedDate() { return lastVerifiedDate; }
    public void setLastVerifiedDate(String lastVerifiedDate) { this.lastVerifiedDate = lastVerifiedDate; }

    public Double getMinLandAcres() { return minLandAcres; }
    public void setMinLandAcres(Double minLandAcres) { this.minLandAcres = minLandAcres; }

    public Double getMaxLandAcres() { return maxLandAcres; }
    public void setMaxLandAcres(Double maxLandAcres) { this.maxLandAcres = maxLandAcres; }

    public String getAllowedFarmerCategories() { return allowedFarmerCategories; }
    public void setAllowedFarmerCategories(String allowedFarmerCategories) { this.allowedFarmerCategories = allowedFarmerCategories; }

    public String getAllowedCrops() { return allowedCrops; }
    public void setAllowedCrops(String allowedCrops) { this.allowedCrops = allowedCrops; }

    public Double getMaxAnnualIncome() { return maxAnnualIncome; }
    public void setMaxAnnualIncome(Double maxAnnualIncome) { this.maxAnnualIncome = maxAnnualIncome; }

    public Boolean getRequiresLandOwnership() { return requiresLandOwnership; }
    public void setRequiresLandOwnership(Boolean requiresLandOwnership) { this.requiresLandOwnership = requiresLandOwnership; }
}
