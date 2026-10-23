package com.agrosmart.backend.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "farmer_feedback")
public class FarmerFeedback {

    @Id
    @Column(name = "id", length = 50)
    private String id;

    @Column(name = "farmer_phone", length = 20, nullable = false)
    private String farmerPhone;

    @Column(name = "farmer_name", length = 100)
    private String farmerName;

    @Column(name = "rating", nullable = false)
    private Integer rating; // 1 to 5

    @Column(name = "category", length = 50, nullable = false)
    private String category;

    @Column(name = "comments", columnDefinition = "TEXT", nullable = false)
    private String comments;

    @Column(name = "app_version", length = 30)
    private String appVersion;

    @Column(name = "submitted_at", length = 30)
    private String submittedAt;

    public FarmerFeedback() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFarmerPhone() { return farmerPhone; }
    public void setFarmerPhone(String farmerPhone) { this.farmerPhone = farmerPhone; }

    public String getFarmerName() { return farmerName; }
    public void setFarmerName(String farmerName) { this.farmerName = farmerName; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }

    public String getAppVersion() { return appVersion; }
    public void setAppVersion(String appVersion) { this.appVersion = appVersion; }

    public String getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(String submittedAt) { this.submittedAt = submittedAt; }
}
