package com.agrosmart.backend.controllers;

import com.agrosmart.backend.entities.SoilHealthRecord;
import com.agrosmart.backend.repositories.SoilHealthRecordRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/advisory")
@CrossOrigin(origins = "*")
public class AdvisoryController {

    private final SoilHealthRecordRepository soilHealthRepository;
    private final com.agrosmart.backend.services.CropAdvisorService cropAdvisorService;

    public AdvisoryController(SoilHealthRecordRepository soilHealthRepository,
                              com.agrosmart.backend.services.CropAdvisorService cropAdvisorService) {
        this.soilHealthRepository = soilHealthRepository;
        this.cropAdvisorService = cropAdvisorService;
    }

    // --- SOIL HEALTH CARD ADVISORY ---

    @GetMapping("/soil-health")
    public List<SoilHealthRecord> getFarmerSoilRecords(@RequestParam(required = false) String phone) {
        if (phone != null && !phone.trim().isEmpty()) {
            return soilHealthRepository.findByFarmerPhoneOrderBySampleDateDesc(phone);
        }
        return soilHealthRepository.findAll();
    }

    @PostMapping("/soil-health")
    public ResponseEntity<SoilHealthRecord> saveSoilRecord(@RequestBody SoilHealthRecord record) {
        if (record.getId() == null || record.getId().trim().isEmpty()) {
            record.setId("SHC-" + System.currentTimeMillis());
        }
        if (record.getSampleDate() == null || record.getSampleDate().trim().isEmpty()) {
            record.setSampleDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
        }

        // ICAR Standard Agro-Nutrient Evaluation
        StringBuilder advisory = new StringBuilder();
        String rating = "BALANCED";

        Double ph = record.getPh();
        if (ph != null) {
            if (ph < 6.0) {
                advisory.append("• Acidic Soil (pH ").append(ph).append("): Apply agricultural lime or dolomite at 1.5 - 2.5 tonnes/ha before monsoon sowing to neutralize soil acidity.\n");
                rating = "ACIDIC";
            } else if (ph > 8.0) {
                advisory.append("• Alkaline/Sodic Soil (pH ").append(ph).append("): Apply gypsum at 2.0 - 4.0 tonnes/ha with organic green manuring (Dhaincha/Sunhemp). Prefer SSP over DAP.\n");
                rating = "ALKALINE";
            } else {
                advisory.append("• Optimal Soil pH (").append(ph).append("): Ideal for nutrient uptake and root proliferation.\n");
            }
        }

        Double oc = record.getOrganicCarbon();
        if (oc != null) {
            if (oc < 0.50) {
                advisory.append("• Low Organic Carbon (").append(oc).append("%): Add 8-10 tonnes well-decomposed FYM or 2.5 tonnes vermicompost/acre. Incorporate crop residues instead of burning.\n");
                if ("BALANCED".equals(rating)) rating = "LOW_ORGANIC_MATTER";
            } else if (oc > 0.75) {
                advisory.append("• High Organic Carbon (").append(oc).append("%): Excellent microbial activity and moisture retention.\n");
            }
        }

        Double n = record.getNitrogenKgHa();
        Double p = record.getPhosphorusKgHa();
        Double k = record.getPotassiumKgHa();

        if (n != null && n < 280.0) {
            advisory.append("• Low Available Nitrogen: Increase basal dose of Urea or Neem-coated Urea by 25% or intercrop with legumes.\n");
        }
        if (p != null && p < 10.0) {
            advisory.append("• Low Available Phosphorus: Apply Single Super Phosphate (SSP) near root zone at sowing.\n");
        }
        if (k != null && k < 110.0) {
            advisory.append("• Low Available Potassium: Apply Muriate of Potash (MOP) to improve crop drought tolerance and grain weight.\n");
        }

        Double zn = record.getZincPpm();
        if (zn != null && zn < 0.6) {
            advisory.append("• Zinc Deficiency (< 0.6 ppm): Soil application of Zinc Sulphate (ZnSO₄) at 20-25 kg/ha or foliar spray of 0.5% ZnSO₄.\n");
        }

        record.setFertilityRating(rating);
        record.setAdvisoryNotes(advisory.toString().trim());

        SoilHealthRecord saved = soilHealthRepository.save(record);
        return ResponseEntity.ok(saved);
    }

    // --- REGIONAL CROP RECOMMENDATION & ROTATION PLANNER ---

    @GetMapping("/recommend-crops")
    public ResponseEntity<?> recommendCrops(
            @RequestParam(defaultValue = "Pune") String zone,
            @RequestParam(defaultValue = "Medium Black") String soil,
            @RequestParam(defaultValue = "LIMITED") String irrigation,
            @RequestParam(defaultValue = "Kharif") String season) {

        String water = irrigation.toUpperCase().contains("WELL") || irrigation.toUpperCase().contains("CANAL") || irrigation.toUpperCase().contains("ASSURED")
                ? "ASSURED"
                : (irrigation.toUpperCase().contains("RAIN") ? "RAINFED" : "LIMITED");

        Map<String, Object> simulation = cropAdvisorService.generateRecommendationsAndPlans(
                zone, season, water, "PLAINS", "None", "MEDIUM", "BALANCED", soil,
                false, false, false, false
        );

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> plans = (List<Map<String, Object>>) simulation.getOrDefault("generatedPlans", Collections.emptyList());

        List<Map<String, Object>> recommendations = new ArrayList<>();
        for (Map<String, Object> plan : plans) {
            recommendations.add(Map.of(
                    "cropName", plan.getOrDefault("cropName", "") + " (" + plan.getOrDefault("variety", "") + ")",
                    "season", season,
                    "duration", plan.getOrDefault("durationDays", ""),
                    "expectedYield", plan.getOrDefault("targetYield", ""),
                    "suitabilityScore", plan.getOrDefault("suitabilityScore", 90),
                    "justification", plan.getOrDefault("suitabilityReason", "")
            ));
        }

        return ResponseEntity.ok(Map.of(
                "zone", zone,
                "soil", soil,
                "irrigation", irrigation,
                "season", season,
                "recommendations", recommendations
        ));
    }

    @PostMapping("/rotation-plan")
    public ResponseEntity<?> evaluateRotationPlan(@RequestBody Map<String, String> payload) {
        String previousCrop = payload.getOrDefault("previousCrop", "Cotton").trim();
        String plannedCrop = payload.getOrDefault("plannedCrop", "Soybean").trim();

        boolean isHighRisk = false;
        String alertTitle;
        String advisoryMessage;
        int soilHealthImpactScore;

        if (previousCrop.equalsIgnoreCase(plannedCrop)) {
            isHighRisk = true;
            alertTitle = "⚠️ Severe Soil Exhaustion & Pest Hazard";
            advisoryMessage = "Mono-cropping " + plannedCrop + " back-to-back depletes specific root-zone nutrients and multiplies pest populations (e.g. nematodes, wilt pathogens). Strongly recommend rotating with a nitrogen-fixing pulse.";
            soilHealthImpactScore = 35;
        } else if ((previousCrop.toLowerCase().contains("cotton") || previousCrop.toLowerCase().contains("sugarcane")) &&
                   (plannedCrop.toLowerCase().contains("gram") || plannedCrop.toLowerCase().contains("chickpea") || plannedCrop.toLowerCase().contains("soybean") || plannedCrop.toLowerCase().contains("moong"))) {
            alertTitle = "✅ Excellent Restorative Rotation";
            advisoryMessage = "Planting legume pulse (" + plannedCrop + ") after nutrient-exhaustive " + previousCrop + " fixes 30-40 kg atmospheric nitrogen per hectare and breaks pest life cycles.";
            soilHealthImpactScore = 95;
        } else if (previousCrop.toLowerCase().contains("soybean") && plannedCrop.toLowerCase().contains("wheat")) {
            alertTitle = "✅ Optimal Cereal-Legume Synergy";
            advisoryMessage = "Soybean leaves behind substantial residual nitrogen and organic matter, reducing wheat nitrogen fertilizer requirement by 15-20%.";
            soilHealthImpactScore = 92;
        } else {
            alertTitle = "ℹ️ Standard Crop Rotation";
            advisoryMessage = "Maintain balanced soil fertility by applying 5 tonnes/ha FYM and alternating deep-rooted with shallow-rooted crops.";
            soilHealthImpactScore = 75;
        }

        return ResponseEntity.ok(Map.of(
                "previousCrop", previousCrop,
                "plannedCrop", plannedCrop,
                "isHighRisk", isHighRisk,
                "alertTitle", alertTitle,
                "advisoryMessage", advisoryMessage,
                "soilHealthImpactScore", soilHealthImpactScore
        ));
    }
}
