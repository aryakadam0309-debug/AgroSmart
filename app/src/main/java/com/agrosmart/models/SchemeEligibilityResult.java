package com.agrosmart.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SchemeEligibilityResult implements Serializable {
    private String schemeId;
    private String schemeTitle;
    private String eligibilityStatus; // "Likely Eligible", "Possibly Eligible – More Information Required", "Not Eligible", "Application Closed", "Status Unverified"
    private String statusColor;       // #2E7D32, #F57C00, #D32F2F, #757575
    private String summaryExplanation;
    private List<CriterionCheck> criteriaChecks = new ArrayList<>();
    private List<String> missingFields = new ArrayList<>();
    private GovernmentScheme scheme;
    private boolean isSaved = false;

    public SchemeEligibilityResult() {}

    public String getSchemeId() { return schemeId; }
    public void setSchemeId(String schemeId) { this.schemeId = schemeId; }

    public String getSchemeTitle() { return schemeTitle; }
    public void setSchemeTitle(String schemeTitle) { this.schemeTitle = schemeTitle; }

    public String getEligibilityStatus() {
        return eligibilityStatus != null ? eligibilityStatus : "Likely Eligible";
    }
    public void setEligibilityStatus(String eligibilityStatus) { this.eligibilityStatus = eligibilityStatus; }

    public String getStatusColor() {
        if (statusColor != null) return statusColor;
        if ("Not Eligible".equalsIgnoreCase(eligibilityStatus)) return "#D32F2F";
        if ("Application Closed".equalsIgnoreCase(eligibilityStatus)) return "#D32F2F";
        if ("Possibly Eligible – More Information Required".equalsIgnoreCase(eligibilityStatus)) return "#F57C00";
        if ("Status Unverified".equalsIgnoreCase(eligibilityStatus)) return "#757575";
        return "#2E7D32";
    }
    public void setStatusColor(String statusColor) { this.statusColor = statusColor; }

    public String getSummaryExplanation() { return summaryExplanation; }
    public void setSummaryExplanation(String summaryExplanation) { this.summaryExplanation = summaryExplanation; }

    public List<CriterionCheck> getCriteriaChecks() { return criteriaChecks; }
    public void setCriteriaChecks(List<CriterionCheck> criteriaChecks) { this.criteriaChecks = criteriaChecks; }

    public List<String> getMissingFields() { return missingFields; }
    public void setMissingFields(List<String> missingFields) { this.missingFields = missingFields; }

    public GovernmentScheme getScheme() { return scheme; }
    public void setScheme(GovernmentScheme scheme) { this.scheme = scheme; }

    public boolean isSaved() { return isSaved; }
    public void setSaved(boolean saved) { isSaved = saved; }
}
