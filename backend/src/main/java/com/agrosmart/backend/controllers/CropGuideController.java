package com.agrosmart.backend.controllers;

import com.agrosmart.backend.entities.CropGuide;
import com.agrosmart.backend.repositories.CropGuideRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/guides")
@CrossOrigin(origins = "*")
public class CropGuideController {

    private final CropGuideRepository guideRepository;

    public CropGuideController(CropGuideRepository guideRepository) {
        this.guideRepository = guideRepository;
    }

    @GetMapping
    public List<CropGuide> getAllGuides(@RequestParam(required = false) String season,
                                        @RequestParam(required = false) String search) {
        if (season != null && !season.equalsIgnoreCase("All Seasons")) {
            return guideRepository.findBySeasonIgnoreCase(season);
        }
        if (search != null && !search.trim().isEmpty()) {
            return guideRepository.findByCropNameContainingIgnoreCase(search.trim());
        }
        return guideRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CropGuide> getGuideById(@PathVariable String id) {
        return guideRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
