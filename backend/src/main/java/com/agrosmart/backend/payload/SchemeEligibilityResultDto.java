package com.agrosmart.backend.payload;

import com.agrosmart.backend.entities.GovernmentScheme;
import java.util.ArrayList;
import java.util.List;

/**
 * Result DTO containing evaluation of a farmer's profile against a scheme (PS 5.3).
 * Returns one of the 5 official outcomes:
 * 1. Likely Eligible
 * 2. Possibly Eligible – More Information Required
 * 3. Not Eligible
 * 4. Application Closed
 * 5. Status Unverified
 */
public class SchemeEligibilityResultDto {

    private String schemeId;
    private String schemeTitle;
    private String eligibilityStatus; // "Likely Eligible", "Possibly Eligible – More Information Required", "Not Eligible", "Application Closed", "Status Unverified"
    private String statusColor;       // Green (#2E7D32), Orange (#F57C00), Red (#D32F2F), Grey (#757575)
    private String summaryExplanation;
    private List<CriterionCheckDto> criteriaChecks = new ArrayList<>();
    private List<String> missingFields = new ArrayList<>();
    private GovernmentScheme scheme;
    private boolean isSaved = false;

    public SchemeEligibilityResultDto() {}

    public SchemeEligibilityResultDto(String schemeId, String schemeTitle, String eligibilityStatus,
                                     String statusColor, String summaryExplanation,
                                     List<CriterionCheckDto> criteriaChecks, List<String> missingFields,
                                     GovernmentScheme scheme) {
        this.schemeId = schemeId;
        this.schemeTitle = schemeTitle;
        this.eligibilityStatus = eligibilityStatus;
        this.statusColor = statusColor;
        this.summaryExplanation = summaryExplanation;
        this.criteriaChecks = criteriaChecks != null ? criteriaChecks : new ArrayList<>();
        this.missingFields = missingFields != null ? missingFields : new ArrayList<>();
        this.scheme = scheme;
    }

    public String getSchemeId() { return schemeId; }
    public void setSchemeId(String schemeId) { this.schemeId = schemeId; }

    public String getSchemeTitle() { return schemeTitle; }
    public void setSchemeTitle(String schemeTitle) { this.schemeTitle = schemeTitle; }

    public String getEligibilityStatus() { return eligibilityStatus; }
    public void setEligibilityStatus(String eligibilityStatus) { this.eligibilityStatus = eligibilityStatus; }

    public String getStatusColor() { return statusColor; }
    public void setStatusColor(String statusColor) { this.statusColor = statusColor; }

    public String getSummaryExplanation() { return summaryExplanation; }
    public void setSummaryExplanation(String summaryExplanation) { this.summaryExplanation = summaryExplanation; }

    public List<CriterionCheckDto> getCriteriaChecks() { return criteriaChecks; }
    public void setCriteriaChecks(List<CriterionCheckDto> criteriaChecks) { this.criteriaChecks = criteriaChecks; }

    public List<String> getMissingFields() { return missingFields; }
    public void setMissingFields(List<String> missingFields) { this.missingFields = missingFields; }

    public GovernmentScheme getScheme() { return scheme; }
    public void setScheme(GovernmentScheme scheme) { this.scheme = scheme; }

    public boolean isSaved() { return isSaved; }
    public void setSaved(boolean saved) { isSaved = saved; }
}
