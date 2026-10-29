package com.agrosmart.models;

import java.io.Serializable;

public class GovernmentScheme implements Serializable {
    private Long id;
    private String schemeId;
    private String schemeName;
    private String title;
    private String category; // Financial Assistance, Subsidy, Insurance, Irrigation, Machinery, Credit
    private String ministry;
    private String subsidyAmount;
    private String description;
    private String benefits;
    private String eligibility;
    private String requiredDocuments;
    private String applicationProcess;
    private String officialPortalUrl;
    private String officialUrl;
    private String sourceUrl;
    private String helpline;
    private String importantDates;
    private String deadline;
    private String level = "CENTRAL"; // "CENTRAL", "STATE"
    private String applicableState = "ALL_INDIA";
    private String applicationStatus = "OPEN"; // "OPEN", "CLOSED", "CONTINUOUS", "UNVERIFIED"
    private String lastVerifiedDate = "2026-10-25";
    private boolean active = true;

    // Eligibility Thresholds
    private Double minLandAcres;
    private Double maxLandAcres;
    private String allowedFarmerCategories;
    private String allowedCrops;
    private Double maxAnnualIncome;
    private Boolean requiresLandOwnership = false;

    public GovernmentScheme() {}

    public GovernmentScheme(Long id, String schemeName, String category, String description,
                            String benefits, String eligibility, String requiredDocuments,
                            String applicationProcess, String officialPortalUrl, String importantDates, boolean active) {
        this.id = id;
        this.schemeName = schemeName;
        this.title = schemeName;
        this.category = category;
        this.description = description;
        this.benefits = benefits;
        this.eligibility = eligibility;
        this.requiredDocuments = requiredDocuments;
        this.applicationProcess = applicationProcess;
        this.officialPortalUrl = officialPortalUrl;
        this.officialUrl = officialPortalUrl;
        this.importantDates = importantDates;
        this.deadline = importantDates;
        this.active = active;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSchemeId() {
        if (schemeId != null) return schemeId;
        return id != null ? "SCH0" + id : "SCH01";
    }
    public void setSchemeId(String schemeId) { this.schemeId = schemeId; }

    public String getSchemeName() {
        if (schemeName != null) return schemeName;
        return title != null ? title : "";
    }
    public void setSchemeName(String schemeName) {
        this.schemeName = schemeName;
        this.title = schemeName;
    }

    public String getTitle() { return getSchemeName(); }
    public void setTitle(String title) { setSchemeName(title); }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getMinistry() { return ministry != null ? ministry : "Ministry of Agriculture & Farmers Welfare"; }
    public void setMinistry(String ministry) { this.ministry = ministry; }

    public String getSubsidyAmount() { return subsidyAmount; }
    public void setSubsidyAmount(String subsidyAmount) { this.subsidyAmount = subsidyAmount; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getBenefits() { return benefits; }
    public void setBenefits(String benefits) { this.benefits = benefits; }

    public String getEligibility() { return eligibility; }
    public void setEligibility(String eligibility) { this.eligibility = eligibility; }

    public String getRequiredDocuments() { return requiredDocuments; }
    public void setRequiredDocuments(String requiredDocuments) { this.requiredDocuments = requiredDocuments; }

    public String getApplicationProcess() { return applicationProcess; }
    public void setApplicationProcess(String applicationProcess) { this.applicationProcess = applicationProcess; }

    public String getOfficialPortalUrl() {
        if (officialPortalUrl != null) return officialPortalUrl;
        return officialUrl != null ? officialUrl : "https://agricoop.nic.in";
    }
    public void setOfficialPortalUrl(String officialPortalUrl) {
        this.officialPortalUrl = officialPortalUrl;
        this.officialUrl = officialPortalUrl;
    }

    public String getOfficialUrl() { return getOfficialPortalUrl(); }
    public void setOfficialUrl(String officialUrl) { setOfficialPortalUrl(officialUrl); }

    public String getSourceUrl() { return sourceUrl != null ? sourceUrl : getOfficialPortalUrl(); }
    public void setSourceUrl(String sourceUrl) { this.sourceUrl = sourceUrl; }

    public String getHelpline() { return helpline != null ? helpline : "1800-180-1551 (Kisan Call Center)"; }
    public void setHelpline(String helpline) { this.helpline = helpline; }

    public String getImportantDates() {
        if (importantDates != null) return importantDates;
        return deadline != null ? deadline : "Ongoing (Open)";
    }
    public void setImportantDates(String importantDates) {
        this.importantDates = importantDates;
        this.deadline = importantDates;
    }

    public String getDeadline() { return getImportantDates(); }
    public void setDeadline(String deadline) { setImportantDates(deadline); }

    public String getLevel() { return level != null ? level : "CENTRAL"; }
    public void setLevel(String level) { this.level = level; }

    public String getApplicableState() { return applicableState != null ? applicableState : "ALL_INDIA"; }
    public void setApplicableState(String applicableState) { this.applicableState = applicableState; }

    public String getApplicationStatus() { return applicationStatus != null ? applicationStatus : "OPEN"; }
    public void setApplicationStatus(String applicationStatus) { this.applicationStatus = applicationStatus; }

    public String getLastVerifiedDate() { return lastVerifiedDate != null ? lastVerifiedDate : "2026-10-25"; }
    public void setLastVerifiedDate(String lastVerifiedDate) { this.lastVerifiedDate = lastVerifiedDate; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

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
