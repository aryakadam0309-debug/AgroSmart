package com.agrosmart.models;

import java.io.Serializable;

public class GovernmentScheme implements Serializable {
    private Long id;
    private String schemeName;
    private String category; // Financial, Subsidy, Insurance, Irrigation, Machinery
    private String description;
    private String benefits;
    private String eligibility;
    private String requiredDocuments;
    private String applicationProcess;
    private String officialPortalUrl;
    private String importantDates;
    private boolean active;

    public GovernmentScheme() {}

    public GovernmentScheme(Long id, String schemeName, String category, String description,
                            String benefits, String eligibility, String requiredDocuments,
                            String applicationProcess, String officialPortalUrl, String importantDates, boolean active) {
        this.id = id;
        this.schemeName = schemeName;
        this.category = category;
        this.description = description;
        this.benefits = benefits;
        this.eligibility = eligibility;
        this.requiredDocuments = requiredDocuments;
        this.applicationProcess = applicationProcess;
        this.officialPortalUrl = officialPortalUrl;
        this.importantDates = importantDates;
        this.active = active;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSchemeName() { return schemeName; }
    public void setSchemeName(String schemeName) { this.schemeName = schemeName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

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

    public String getOfficialPortalUrl() { return officialPortalUrl; }
    public void setOfficialPortalUrl(String officialPortalUrl) { this.officialPortalUrl = officialPortalUrl; }

    public String getImportantDates() { return importantDates; }
    public void setImportantDates(String importantDates) { this.importantDates = importantDates; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
