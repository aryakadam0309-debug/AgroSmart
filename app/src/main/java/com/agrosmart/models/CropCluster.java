package com.agrosmart.models;

import java.io.Serializable;
import java.util.List;

public class CropCluster implements Serializable {
    private String clusterKey;
    private String cropName;
    private String problemCategory;
    private String district;
    private String village;
    private int distinctFarmerCount;
    private int totalReportCount;
    private String earliestReportDate;
    private String latestReportDate;
    private boolean isPotentialCluster;
    private String clusterStatus;
    private String warningNote;
    private List<String> sampleSymptoms;

    public CropCluster() {}

    public String getClusterKey() { return clusterKey; }
    public void setClusterKey(String clusterKey) { this.clusterKey = clusterKey; }

    public String getCropName() { return cropName; }
    public void setCropName(String cropName) { this.cropName = cropName; }

    public String getProblemCategory() { return problemCategory; }
    public void setProblemCategory(String problemCategory) { this.problemCategory = problemCategory; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public String getVillage() { return village; }
    public void setVillage(String village) { this.village = village; }

    public int getDistinctFarmerCount() { return distinctFarmerCount; }
    public void setDistinctFarmerCount(int distinctFarmerCount) { this.distinctFarmerCount = distinctFarmerCount; }

    public int getTotalReportCount() { return totalReportCount; }
    public void setTotalReportCount(int totalReportCount) { this.totalReportCount = totalReportCount; }

    public String getEarliestReportDate() { return earliestReportDate; }
    public void setEarliestReportDate(String earliestReportDate) { this.earliestReportDate = earliestReportDate; }

    public String getLatestReportDate() { return latestReportDate; }
    public void setLatestReportDate(String latestReportDate) { this.latestReportDate = latestReportDate; }

    public boolean isPotentialCluster() { return isPotentialCluster; }
    public void setPotentialCluster(boolean potentialCluster) { isPotentialCluster = potentialCluster; }

    public String getClusterStatus() { return clusterStatus; }
    public void setClusterStatus(String clusterStatus) { this.clusterStatus = clusterStatus; }

    public String getWarningNote() { return warningNote; }
    public void setWarningNote(String warningNote) { this.warningNote = warningNote; }

    public List<String> getSampleSymptoms() { return sampleSymptoms; }
    public void setSampleSymptoms(List<String> sampleSymptoms) { this.sampleSymptoms = sampleSymptoms; }
}
