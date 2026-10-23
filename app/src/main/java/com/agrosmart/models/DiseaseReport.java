package com.agrosmart.models;

import java.io.Serializable;

public class DiseaseReport implements Serializable {
    private String id;
    private String farmerPhone;
    private String farmerName;
    private String cropName;
    private String affectedPart;
    private String symptoms;
    private String severity;
    private String village;
    private String district;
    private String imageUrl;
    private String reportedAt;
    private String status; // REPORTED, UNDER_REVIEW, PRELIMINARY_SIGNAL, VERIFIED_ALERT, RESOLVED
    private String adminAdvisory;

    public DiseaseReport() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFarmerPhone() { return farmerPhone; }
    public void setFarmerPhone(String farmerPhone) { this.farmerPhone = farmerPhone; }

    public String getFarmerName() { return farmerName; }
    public void setFarmerName(String farmerName) { this.farmerName = farmerName; }

    public String getCropName() { return cropName; }
    public void setCropName(String cropName) { this.cropName = cropName; }

    public String getAffectedPart() { return affectedPart; }
    public void setAffectedPart(String affectedPart) { this.affectedPart = affectedPart; }

    public String getSymptoms() { return symptoms; }
    public void setSymptoms(String symptoms) { this.symptoms = symptoms; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public String getVillage() { return village; }
    public void setVillage(String village) { this.village = village; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getReportedAt() { return reportedAt; }
    public void setReportedAt(String reportedAt) { this.reportedAt = reportedAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getAdminAdvisory() { return adminAdvisory; }
    public void setAdminAdvisory(String adminAdvisory) { this.adminAdvisory = adminAdvisory; }
}
