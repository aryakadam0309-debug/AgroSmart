package com.agrosmart.backend.controllers;

import com.agrosmart.backend.entities.GovernmentScheme;
import com.agrosmart.backend.repositories.GovernmentSchemeRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schemes")
@CrossOrigin(origins = "*")
public class SchemeController {

    private final GovernmentSchemeRepository schemeRepository;

    public SchemeController(GovernmentSchemeRepository schemeRepository) {
        this.schemeRepository = schemeRepository;
    }

    @GetMapping
    public List<GovernmentScheme> getAllSchemes(@RequestParam(required = false) String category) {
        if (category != null && !category.equalsIgnoreCase("All Categories")) {
            return schemeRepository.findByCategoryIgnoreCase(category);
        }
        return schemeRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<GovernmentScheme> getSchemeById(@PathVariable String id) {
        return schemeRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
