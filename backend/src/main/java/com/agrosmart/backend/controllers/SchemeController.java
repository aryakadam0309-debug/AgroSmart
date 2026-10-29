package com.agrosmart.backend.controllers;

import com.agrosmart.backend.entities.GovernmentScheme;
import com.agrosmart.backend.payload.FarmerProfileDto;
import com.agrosmart.backend.payload.SchemeEligibilityResultDto;
import com.agrosmart.backend.services.SchemeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/schemes")
@CrossOrigin(origins = "*")
public class SchemeController {

    private final SchemeService schemeService;

    public SchemeController(SchemeService schemeService) {
        this.schemeService = schemeService;
    }

    /**
     * 1. Browse and Filter Schemes (PS 5.3 Requirement 3).
     */
    @GetMapping
    public List<GovernmentScheme> getSchemes(
            @RequestParam(required = false, defaultValue = "All") String category,
            @RequestParam(required = false, defaultValue = "All") String level,
            @RequestParam(required = false, defaultValue = "ALL_INDIA") String state,
            @RequestParam(required = false, defaultValue = "All") String status,
            @RequestParam(required = false, defaultValue = "") String query) {
        return schemeService.listSchemes(category, level, state, status, query);
    }

    /**
     * 2. Get Single Scheme by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<GovernmentScheme> getSchemeById(@PathVariable String id) {
        return schemeService.getSchemeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 3. Evaluate Eligibility for a specific scheme against a farmer's profile (PS 5.3 Requirement 4).
     */
    @PostMapping("/{id}/evaluate")
    public ResponseEntity<SchemeEligibilityResultDto> evaluateScheme(
            @PathVariable String id,
            @RequestBody(required = false) FarmerProfileDto profile) {
        SchemeEligibilityResultDto result = schemeService.evaluateSingleScheme(id, profile);
        if (result != null) {
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * 4. Get Personalized Recommendations ranked by Eligibility (PS 5.3 Requirement 9.B).
     */
    @PostMapping("/recommended")
    public ResponseEntity<List<SchemeEligibilityResultDto>> getRecommendedSchemes(
            @RequestBody(required = false) FarmerProfileDto profile,
            @RequestParam(required = false) String phone) {
        if (profile == null) {
            profile = schemeService.resolveProfile(phone);
        }
        return ResponseEntity.ok(schemeService.getRecommendedSchemes(profile));
    }

    @GetMapping("/recommended")
    public ResponseEntity<List<SchemeEligibilityResultDto>> getRecommendedSchemesByPhone(
            @RequestParam(required = false, defaultValue = "9876543210") String phone) {
        FarmerProfileDto profile = schemeService.resolveProfile(phone);
        return ResponseEntity.ok(schemeService.getRecommendedSchemes(profile));
    }

    /**
     * 5. Save / Bookmark Scheme (PS 5.3 Requirement 9.D).
     */
    @PostMapping("/save")
    public ResponseEntity<Map<String, Object>> saveScheme(
            @RequestParam String phone,
            @RequestParam String schemeId) {
        boolean ok = schemeService.saveScheme(phone, schemeId);
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", ok);
        resp.put("message", ok ? "Scheme bookmarked successfully." : "Failed to bookmark scheme.");
        return ResponseEntity.ok(resp);
    }

    /**
     * 6. Unsave / Remove Bookmark.
     */
    @PostMapping("/unsave")
    public ResponseEntity<Map<String, Object>> unsaveScheme(
            @RequestParam String phone,
            @RequestParam String schemeId) {
        boolean ok = schemeService.unsaveScheme(phone, schemeId);
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", ok);
        resp.put("message", ok ? "Scheme bookmark removed." : "Failed to remove bookmark.");
        return ResponseEntity.ok(resp);
    }

    /**
     * 7. Retrieve Saved Schemes for Farmer.
     */
    @GetMapping("/saved")
    public ResponseEntity<List<GovernmentScheme>> getSavedSchemes(
            @RequestParam(required = false, defaultValue = "9876543210") String phone) {
        return ResponseEntity.ok(schemeService.getSavedSchemes(phone));
    }

    /**
     * 8. Admin / Hackathon Live Scenario: Toggle Scheme Status (PS 5.3 Section 12).
     * Demonstrates changing a scheme between OPEN and CLOSED to verify live app reflection.
     */
    @PostMapping("/admin/toggle-status")
    public ResponseEntity<GovernmentScheme> toggleSchemeStatus(
            @RequestParam String schemeId,
            @RequestParam String status,
            @RequestParam(required = false) String deadlineNote) {
        GovernmentScheme updated = schemeService.updateSchemeStatus(schemeId, status, deadlineNote);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.notFound().build();
    }
}
