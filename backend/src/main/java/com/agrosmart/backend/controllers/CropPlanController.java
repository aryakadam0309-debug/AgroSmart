package com.agrosmart.backend.controllers;

import com.agrosmart.backend.entities.SavedCropPlan;
import com.agrosmart.backend.repositories.SavedCropPlanRepository;
import com.agrosmart.backend.services.CropAdvisorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/crops")
@CrossOrigin(origins = "*")
public class CropPlanController {

    private final CropAdvisorService cropAdvisorService;
    private final SavedCropPlanRepository savedCropPlanRepository;

    public CropPlanController(CropAdvisorService cropAdvisorService,
                              SavedCropPlanRepository savedCropPlanRepository) {
        this.cropAdvisorService = cropAdvisorService;
        this.savedCropPlanRepository = savedCropPlanRepository;
    }

    /**
     * Core multi-factor Maharashtra crop recommendation & "Before You Sow" Simulator.
     * Evaluates agronomic fit across DBSKKV, MPKV, VNMKV, PDKV data with What-If scenarios.
     */
    @PostMapping("/recommend-and-simulate")
    public ResponseEntity<Map<String, Object>> recommendAndSimulate(@RequestBody Map<String, Object> request) {
        String district = (String) request.getOrDefault("district", "Pune");
        String season = (String) request.getOrDefault("season", "Kharif");
        String waterAvailability = (String) request.getOrDefault("waterAvailability", "LIMITED");
        String landType = (String) request.getOrDefault("landType", "PLAINS");
        String previousCrop = (String) request.getOrDefault("previousCrop", "None");
        String budgetLevel = (String) request.getOrDefault("budgetLevel", "MEDIUM");
        String farmerGoal = (String) request.getOrDefault("farmerGoal", "BALANCED");
        String soilType = (String) request.getOrDefault("soilType", "ANY");

        boolean scenarioWaterDeficit = Boolean.parseBoolean(String.valueOf(request.getOrDefault("scenarioWaterDeficit", "false")));
        boolean scenarioBudgetCut = Boolean.parseBoolean(String.valueOf(request.getOrDefault("scenarioBudgetCut", "false")));
        boolean scenarioEarlyHarvest = Boolean.parseBoolean(String.valueOf(request.getOrDefault("scenarioEarlyHarvest", "false")));
        boolean scenarioDelayedSowing = Boolean.parseBoolean(String.valueOf(request.getOrDefault("scenarioDelayedSowing", "false")));

        Map<String, Object> result = cropAdvisorService.generateRecommendationsAndPlans(
                district, season, waterAvailability, landType, previousCrop,
                budgetLevel, farmerGoal, soilType,
                scenarioWaterDeficit, scenarioBudgetCut, scenarioEarlyHarvest, scenarioDelayedSowing
        );

        return ResponseEntity.ok(result);
    }

    /**
     * Persist a chosen plan to MySQL.
     */
    @PostMapping("/plans/save")
    public ResponseEntity<SavedCropPlan> savePlan(@RequestBody SavedCropPlan plan) {
        if (plan.getFarmerPhone() == null || plan.getFarmerPhone().trim().isEmpty()) {
            plan.setFarmerPhone("0000000000");
        }
        if (plan.getId() == null || plan.getId().trim().isEmpty()) {
            plan.setId("PLAN-" + System.currentTimeMillis());
        }
        SavedCropPlan saved = savedCropPlanRepository.save(plan);
        return ResponseEntity.ok(saved);
    }

    /**
     * Fetch saved plans for a farmer by phone or ID.
     */
    @GetMapping("/plans/my-plans")
    public ResponseEntity<List<SavedCropPlan>> getMyPlans(
            @RequestParam(required = false) String farmerPhone,
            @RequestParam(required = false) Long farmerId) {
        if (farmerPhone != null && !farmerPhone.trim().isEmpty()) {
            return ResponseEntity.ok(savedCropPlanRepository.findByFarmerPhoneOrderByCreatedDateDesc(farmerPhone));
        }
        if (farmerId != null && farmerId > 0) {
            return ResponseEntity.ok(savedCropPlanRepository.findByFarmerIdOrderByCreatedDateDesc(farmerId));
        }
        return ResponseEntity.ok(savedCropPlanRepository.findAll());
    }

    /**
     * Update plan planting status (e.g. marked as currently planted on field).
     */
    @PutMapping("/plans/{id}/status")
    public ResponseEntity<?> updatePlantedStatus(@PathVariable String id, @RequestParam boolean isPlanted) {
        Optional<SavedCropPlan> planOpt = savedCropPlanRepository.findById(id);
        if (planOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        SavedCropPlan plan = planOpt.get();
        plan.setIsPlanted(isPlanted);
        savedCropPlanRepository.save(plan);
        return ResponseEntity.ok(Map.of("success", true, "id", id, "isPlanted", isPlanted));
    }

    /**
     * Delete a saved plan.
     */
    @DeleteMapping("/plans/{id}")
    public ResponseEntity<?> deletePlan(@PathVariable String id) {
        if (!savedCropPlanRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        savedCropPlanRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("success", true, "deletedId", id));
    }
}
