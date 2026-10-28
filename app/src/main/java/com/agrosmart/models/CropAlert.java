package com.agrosmart.models;

import java.io.Serializable;

public class CropAlert implements Serializable {
    private String id;
    private String cropName;
    private String district;
    private String village;
    private String alertTitle;
    private String severity; // ADVISORY, MODERATE, HIGH, SEVERE
    private String summary;
    private int reportCount;
    private String authority;
    private String recommendedActions;
    private String status; // ACTIVE, RESOLVED, WITHDRAWN
    private String publishedBy;
    private String publishedAt;
    private String resolvedAt;

    public CropAlert() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCropName() { return cropName; }
    public void setCropName(String cropName) { this.cropName = cropName; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public String getVillage() { return village; }
    public void setVillage(String village) { this.village = village; }

    public String getAlertTitle() { return alertTitle; }
    public void setAlertTitle(String alertTitle) { this.alertTitle = alertTitle; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public int getReportCount() { return reportCount; }
    public void setReportCount(int reportCount) { this.reportCount = reportCount; }

    public String getAuthority() { return authority; }
    public void setAuthority(String authority) { this.authority = authority; }

    public String getRecommendedActions() { return recommendedActions; }
    public void setRecommendedActions(String recommendedActions) { this.recommendedActions = recommendedActions; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPublishedBy() { return publishedBy; }
    public void setPublishedBy(String publishedBy) { this.publishedBy = publishedBy; }

    public String getPublishedAt() { return publishedAt; }
    public void setPublishedAt(String publishedAt) { this.publishedAt = publishedAt; }

    public String getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(String resolvedAt) { this.resolvedAt = resolvedAt; }
}
