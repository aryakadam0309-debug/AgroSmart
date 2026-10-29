package com.agrosmart.backend.payload;

import java.io.Serializable;

/**
 * Data Transfer Object representing an agronomic recommendation generated
 * by the Farming Advisory Engine for a specific farming activity.
 */
public class FarmingAdvisoryDto implements Serializable {

    private String activity; // "Sowing", "Pesticide Spraying", "Harvesting"
    private String status;   // "Suitable", "Caution", "Avoid/Delay"
    private String statusColor; // Hex color code for Android UI badge
    private String explanation;
    private String triggerMetrics;
    private String recommendedAction;
    private String forecastDate;
    private String generatedAt;
    private String advisoryDisclaimer = "Advisory only. Follow local Krishi Vigyan Kendra (KVK) guidelines.";

    public FarmingAdvisoryDto() {}

    public FarmingAdvisoryDto(String activity, String status, String statusColor,
                              String explanation, String triggerMetrics,
                              String recommendedAction, String forecastDate, String generatedAt) {
        this.activity = activity;
        this.status = status;
        this.statusColor = statusColor;
        this.explanation = explanation;
        this.triggerMetrics = triggerMetrics;
        this.recommendedAction = recommendedAction;
        this.forecastDate = forecastDate;
        this.generatedAt = generatedAt;
    }

    public String getActivity() { return activity; }
    public void setActivity(String activity) { this.activity = activity; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getStatusColor() { return statusColor; }
    public void setStatusColor(String statusColor) { this.statusColor = statusColor; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }

    public String getTriggerMetrics() { return triggerMetrics; }
    public void setTriggerMetrics(String triggerMetrics) { this.triggerMetrics = triggerMetrics; }

    public String getRecommendedAction() { return recommendedAction; }
    public void setRecommendedAction(String recommendedAction) { this.recommendedAction = recommendedAction; }

    public String getForecastDate() { return forecastDate; }
    public void setForecastDate(String forecastDate) { this.forecastDate = forecastDate; }

    public String getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(String generatedAt) { this.generatedAt = generatedAt; }

    public String getAdvisoryDisclaimer() { return advisoryDisclaimer; }
    public void setAdvisoryDisclaimer(String advisoryDisclaimer) { this.advisoryDisclaimer = advisoryDisclaimer; }
}
