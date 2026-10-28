package com.agrosmart.backend.services;

import com.agrosmart.backend.entities.CropAdvisory;
import com.agrosmart.backend.entities.DiseaseReport;
import com.agrosmart.backend.payload.CropClusterDto;
import com.agrosmart.backend.repositories.CropAdvisoryRepository;
import com.agrosmart.backend.repositories.DiseaseReportRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CropClusterService {

    public static final int DEFAULT_MIN_DISTINCT_FARMERS = 3;
    public static final int DEFAULT_TIME_WINDOW_DAYS = 14;

    private final DiseaseReportRepository diseaseReportRepository;
    private final CropAdvisoryRepository cropAdvisoryRepository;

    @Value("${agrosmart.cropwatch.cluster.min-farmers:3}")
    private int minDistinctFarmers = DEFAULT_MIN_DISTINCT_FARMERS;

    @Value("${agrosmart.cropwatch.cluster.window-days:14}")
    private int clusterWindowDays = DEFAULT_TIME_WINDOW_DAYS;

    public CropClusterService(DiseaseReportRepository diseaseReportRepository,
                              CropAdvisoryRepository cropAdvisoryRepository) {
        this.diseaseReportRepository = diseaseReportRepository;
        this.cropAdvisoryRepository = cropAdvisoryRepository;
    }

    public List<CropClusterDto> detectClusters() {
        List<DiseaseReport> allReports = diseaseReportRepository.findAllByOrderByReportedAtDesc();

        // Phase 3: Filter out duplicates, retracted reports, or reports explicitly marked NOT_VERIFIED
        List<DiseaseReport> validReports = allReports.stream()
                .filter(r -> r.getStatus() == null ||
                        (!"DUPLICATE".equalsIgnoreCase(r.getStatus()) &&
                         !"NOT_VERIFIED".equalsIgnoreCase(r.getStatus()) &&
                         !Boolean.TRUE.equals(r.getIsDuplicate())))
                .collect(Collectors.toList());

        // Group by cropName + district + problemCategory
        Map<String, List<DiseaseReport>> grouped = new HashMap<>();
        for (DiseaseReport r : validReports) {
            String crop = (r.getCropName() != null ? r.getCropName().trim() : "Unknown").toLowerCase();
            String district = (r.getDistrict() != null ? r.getDistrict().trim() : "District").toLowerCase();
            String category = (r.getProblemCategory() != null ? r.getProblemCategory().trim() :
                    (r.getAffectedPart() != null ? r.getAffectedPart().trim() : "General Symptoms")).toLowerCase();

            String key = crop + "|" + district + "|" + category;
            grouped.computeIfAbsent(key, k -> new ArrayList<>()).add(r);
        }

        List<CropClusterDto> clusters = new ArrayList<>();

        for (Map.Entry<String, List<DiseaseReport>> entry : grouped.entrySet()) {
            List<DiseaseReport> groupList = entry.getValue();

            // Count distinct farmer phones to prevent single farmer inflating reports
            Set<String> distinctFarmers = new HashSet<>();
            for (DiseaseReport r : groupList) {
                if (r.getFarmerPhone() != null && !r.getFarmerPhone().trim().isEmpty()) {
                    distinctFarmers.add(r.getFarmerPhone().trim());
                } else {
                    distinctFarmers.add(r.getId()); // fallback unique identifier
                }
            }

            int distinctCount = distinctFarmers.size();
            int totalReports = groupList.size();

            // Check if cluster threshold is met (e.g. >= 2 for observing, >= minDistinctFarmers for flagged)
            if (distinctCount >= 2) {
                DiseaseReport sample = groupList.get(0);
                CropClusterDto dto = new CropClusterDto();
                dto.setClusterKey(entry.getKey());
                dto.setCropName(sample.getCropName());
                dto.setDistrict(sample.getDistrict());
                dto.setTaluka(sample.getTaluka());
                dto.setVillage(findMostCommonVillage(groupList));
                dto.setProblemCategory(sample.getProblemCategory() != null ? sample.getProblemCategory() : "Observed Symptoms");
                dto.setDistinctFarmerCount(distinctCount);
                dto.setTotalReportCount(totalReports);

                dto.setLatestReportDate(groupList.get(0).getReportedAt());
                dto.setEarliestReportDate(groupList.get(groupList.size() - 1).getReportedAt());

                boolean isFlagged = distinctCount >= minDistinctFarmers;
                dto.setPotentialCluster(isFlagged);
                dto.setClusterStatus(isFlagged ? "POTENTIAL_CLUSTER_FLAGGED" : "OBSERVING");

                dto.setWarningNote(
                        "CRITICAL ACCURACY NOTE: A cluster indicates that multiple similar crop problems (" +
                                distinctCount + " distinct farmers) have been reported in this area. " +
                                "It does NOT prove that an outbreak exists. Official agronomic field triage and expert verification is required prior to alert publication."
                );

                // Collect up to 3 distinct symptom descriptions for admin preview
                List<String> symptoms = groupList.stream()
                        .map(DiseaseReport::getSymptoms)
                        .filter(s -> s != null && !s.trim().isEmpty())
                        .distinct()
                        .limit(3)
                        .collect(Collectors.toList());
                dto.setSampleSymptoms(symptoms);

                // Phase 4: Connect Reports to Verified Advisories from Catalogue (CROPSAP / PDKV)
                connectAdvisoryToCluster(dto, sample.getCropName(), sample.getProblemCategory(), sample.getDistrict());

                clusters.add(dto);
            }
        }

        // Sort: flagged clusters first, then by distinct farmer count descending
        clusters.sort((a, b) -> {
            if (a.isPotentialCluster() != b.isPotentialCluster()) {
                return a.isPotentialCluster() ? -1 : 1;
            }
            return Integer.compare(b.getDistinctFarmerCount(), a.getDistinctFarmerCount());
        });

        return clusters;
    }

    private void connectAdvisoryToCluster(CropClusterDto dto, String cropName, String category, String district) {
        if (cropName == null) {
            dto.setHasMatchedAdvisory(false);
            dto.setAdvisoryMatchNote("No verified advisory matches this report. Issue referred for agricultural officer review.");
            return;
        }

        List<CropAdvisory> verifiedAdvisories = cropAdvisoryRepository.findByCropNameAndVerificationStatus(cropName, "VERIFIED");
        if (verifiedAdvisories.isEmpty()) {
            dto.setHasMatchedAdvisory(false);
            dto.setAdvisoryMatchNote("No verified agricultural advisory currently matches " + cropName + " in the catalogue. Issue referred for expert field triage.");
            return;
        }

        // Search for matching problem category or symptoms
        CropAdvisory bestMatch = null;
        for (CropAdvisory adv : verifiedAdvisories) {
            if (category != null && adv.getProblemCategory() != null &&
                    adv.getProblemCategory().toLowerCase().contains(category.toLowerCase())) {
                bestMatch = adv;
                break;
            }
        }

        // Fallback to first verified advisory for this crop if specific category is generic
        if (bestMatch == null) {
            bestMatch = verifiedAdvisories.get(0);
        }

        dto.setHasMatchedAdvisory(true);
        dto.setMatchedAdvisoryTitle(bestMatch.getTitle());
        dto.setMatchedAdvisoryText(bestMatch.getAdvisoryText());
        dto.setMatchedAdvisoryTextMarathi(bestMatch.getAdvisoryTextMarathi());
        dto.setMatchedAdvisorySourceName(bestMatch.getSourceName());
        dto.setMatchedAdvisorySourceUrl(bestMatch.getSourceUrl());
        dto.setMatchedAdvisoryPublicationDate(bestMatch.getPublicationDate());
        dto.setAdvisoryMatchNote("Matched official advisory from: " + bestMatch.getSourceName() + " (" + bestMatch.getPublicationDate() + ")");
    }

    private String findMostCommonVillage(List<DiseaseReport> reports) {
        Map<String, Integer> counts = new HashMap<>();
        for (DiseaseReport r : reports) {
            String v = r.getVillage() != null && !r.getVillage().trim().isEmpty() ? r.getVillage().trim() : "Local Area";
            counts.put(v, counts.getOrDefault(v, 0) + 1);
        }
        return counts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Local Area");
    }

    public int getMinDistinctFarmers() { return minDistinctFarmers; }
    public void setMinDistinctFarmers(int minDistinctFarmers) { this.minDistinctFarmers = minDistinctFarmers; }

    public int getClusterWindowDays() { return clusterWindowDays; }
    public void setClusterWindowDays(int clusterWindowDays) { this.clusterWindowDays = clusterWindowDays; }
}
