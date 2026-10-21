package com.agrosmart.backend.controllers;

import com.agrosmart.backend.entities.CropDisease;
import com.agrosmart.backend.repositories.CropDiseaseRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for Crop Diseases Directory
 * Strictly encyclopedic agricultural lookup (ICAR / KVK compliant).
 * ZERO camera-based disease detection, ZERO AI/ML models.
 */
@RestController
@RequestMapping("/api/diseases")
@CrossOrigin(origins = "*")
public class CropDiseaseController {

    private final CropDiseaseRepository diseaseRepository;

    public CropDiseaseController(CropDiseaseRepository diseaseRepository) {
        this.diseaseRepository = diseaseRepository;
    }

    @GetMapping
    public List<CropDisease> getDiseases(@RequestParam(required = false) String crop,
                                         @RequestParam(required = false) String query) {
        if (crop != null && !crop.equalsIgnoreCase("All Crops")) {
            return diseaseRepository.findByCropNameIgnoreCase(crop);
        }
        if (query != null && !query.trim().isEmpty()) {
            return diseaseRepository.findByDiseaseNameContainingIgnoreCaseOrSymptomsContainingIgnoreCase(query.trim(), query.trim());
        }
        return diseaseRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CropDisease> getDiseaseById(@PathVariable String id) {
        return diseaseRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
