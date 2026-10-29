package com.agrosmart.models;

import java.io.Serializable;

public class CriterionCheck implements Serializable {
    private String criterionName;
    private Boolean isSatisfied; // true = satisfied, false = unmet, null = missing info
    private String farmerValue;
    private String requiredCondition;
    private String explanation;

    public CriterionCheck() {}

    public CriterionCheck(String criterionName, Boolean isSatisfied, String farmerValue, String requiredCondition, String explanation) {
        this.criterionName = criterionName;
        this.isSatisfied = isSatisfied;
        this.farmerValue = farmerValue;
        this.requiredCondition = requiredCondition;
        this.explanation = explanation;
    }

    public String getCriterionName() { return criterionName; }
    public void setCriterionName(String criterionName) { this.criterionName = criterionName; }

    public Boolean getIsSatisfied() { return isSatisfied; }
    public void setIsSatisfied(Boolean isSatisfied) { this.isSatisfied = isSatisfied; }

    public String getFarmerValue() { return farmerValue; }
    public void setFarmerValue(String farmerValue) { this.farmerValue = farmerValue; }

    public String getRequiredCondition() { return requiredCondition; }
    public void setRequiredCondition(String requiredCondition) { this.requiredCondition = requiredCondition; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }
}
