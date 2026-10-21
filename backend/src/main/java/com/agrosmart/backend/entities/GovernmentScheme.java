package com.agrosmart.backend.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "government_schemes")
public class GovernmentScheme {
    @Id
    private String id;

    @Column(nullable = false)
    private String title;

    private String ministry;
    private String subsidyAmount;

    @Column(columnDefinition = "TEXT")
    private String eligibility;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String officialUrl;
    private String category;
    private String deadline;

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

    public String getOfficialUrl() { return officialUrl; }
    public void setOfficialUrl(String officialUrl) { this.officialUrl = officialUrl; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDeadline() { return deadline; }
    public void setDeadline(String deadline) { this.deadline = deadline; }
}
