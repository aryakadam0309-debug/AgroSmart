package com.agrosmart.backend.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "crop_alerts")
public class CropAlert {

    @Id
    @Column(name = "id", length = 50)
    private String id;

    @Column(name = "crop_name", length = 100, nullable = false)
    private String cropName;

    @Column(name = "district", length = 100, nullable = false)
    private String district;

    @Column(name = "village", length = 100)
    private String village;

    @Column(name = "alert_title", length = 200, nullable = false)
    private String alertTitle;

    @Column(name = "severity", length = 30) // ADVISORY, MODERATE, HIGH, SEVERE
    private String severity;

    @Column(name = "summary", columnDefinition = "TEXT", nullable = false)
    private String summary;

    @Column(name = "report_count")
    private int reportCount;

    @Column(name = "authority", length = 150) // e.g. "KVK Baramati & MPKV Extension Wing"
    private String authority;

    @Column(name = "recommended_actions", columnDefinition = "TEXT")
    private String recommendedActions;

    @Column(name = "status", length = 30) // ACTIVE, RESOLVED, WITHDRAWN
    private String status;

    @Column(name = "published_by", length = 50)
    private String publishedBy;

    @Column(name = "published_at", length = 50)
    private String publishedAt;

    @Column(name = "resolved_at", length = 50)
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
