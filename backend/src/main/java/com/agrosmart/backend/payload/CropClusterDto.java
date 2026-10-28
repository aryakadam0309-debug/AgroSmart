package com.agrosmart.backend.payload;

import java.util.List;

public class CropClusterDto {
    private String clusterKey;
    private String cropName;
    private String problemCategory;
    private String district;
    private String taluka;
    private String village;
    private int distinctFarmerCount;
    private int totalReportCount;
    private String earliestReportDate;
    private String latestReportDate;
    private boolean isPotentialCluster;
    private String clusterStatus; // "POTENTIAL_CLUSTER_FLAGGED" or "OBSERVING"
    private String warningNote;
    private List<String> sampleSymptoms;

    // Phase 4: Connected Verified Advisory from Official Sources (CROPSAP / PDKV)
    private boolean hasMatchedAdvisory;
    private String matchedAdvisoryTitle;
    private String matchedAdvisoryText;
    private String matchedAdvisoryTextMarathi;
    private String matchedAdvisorySourceName;
    private String matchedAdvisorySourceUrl;
    private String matchedAdvisoryPublicationDate;
    private String advisoryMatchNote;

    public CropClusterDto() {}

    public String getClusterKey() { return clusterKey; }
    public void setClusterKey(String clusterKey) { this.clusterKey = clusterKey; }

    public String getCropName() { return cropName; }
    public void setCropName(String cropName) { this.cropName = cropName; }

    public String getProblemCategory() { return problemCategory; }
    public void setProblemCategory(String problemCategory) { this.problemCategory = problemCategory; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public String getTaluka() { return taluka; }
    public void setTaluka(String taluka) { this.taluka = taluka; }

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

    public boolean isHasMatchedAdvisory() { return hasMatchedAdvisory; }
    public void setHasMatchedAdvisory(boolean hasMatchedAdvisory) { this.hasMatchedAdvisory = hasMatchedAdvisory; }

    public String getMatchedAdvisoryTitle() { return matchedAdvisoryTitle; }
    public void setMatchedAdvisoryTitle(String matchedAdvisoryTitle) { this.matchedAdvisoryTitle = matchedAdvisoryTitle; }

    public String getMatchedAdvisoryText() { return matchedAdvisoryText; }
    public void setMatchedAdvisoryText(String matchedAdvisoryText) { this.matchedAdvisoryText = matchedAdvisoryText; }

    public String getMatchedAdvisoryTextMarathi() { return matchedAdvisoryTextMarathi; }
    public void setMatchedAdvisoryTextMarathi(String matchedAdvisoryTextMarathi) { this.matchedAdvisoryTextMarathi = matchedAdvisoryTextMarathi; }

    public String getMatchedAdvisorySourceName() { return matchedAdvisorySourceName; }
    public void setMatchedAdvisorySourceName(String matchedAdvisorySourceName) { this.matchedAdvisorySourceName = matchedAdvisorySourceName; }

    public String getMatchedAdvisorySourceUrl() { return matchedAdvisorySourceUrl; }
    public void setMatchedAdvisorySourceUrl(String matchedAdvisorySourceUrl) { this.matchedAdvisorySourceUrl = matchedAdvisorySourceUrl; }

    public String getMatchedAdvisoryPublicationDate() { return matchedAdvisoryPublicationDate; }
    public void setMatchedAdvisoryPublicationDate(String matchedAdvisoryPublicationDate) { this.matchedAdvisoryPublicationDate = matchedAdvisoryPublicationDate; }

    public String getAdvisoryMatchNote() { return advisoryMatchNote; }
    public void setAdvisoryMatchNote(String advisoryMatchNote) { this.advisoryMatchNote = advisoryMatchNote; }
}
