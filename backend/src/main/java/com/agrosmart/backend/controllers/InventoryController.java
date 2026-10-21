package com.agrosmart.backend.controllers;

import com.agrosmart.backend.entities.CropHarvestInventory;
import com.agrosmart.backend.entities.FertilizerInventory;
import com.agrosmart.backend.repositories.CropHarvestInventoryRepository;
import com.agrosmart.backend.repositories.FertilizerInventoryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@CrossOrigin(origins = "*")
public class InventoryController {

    private final FertilizerInventoryRepository fertilizerRepository;
    private final CropHarvestInventoryRepository harvestRepository;

    public InventoryController(FertilizerInventoryRepository fertilizerRepository,
                               CropHarvestInventoryRepository harvestRepository) {
        this.fertilizerRepository = fertilizerRepository;
        this.harvestRepository = harvestRepository;
    }

    // Fertilizer Inventory
    @GetMapping("/fertilizers")
    public List<FertilizerInventory> getFertilizers(@RequestParam(required = false) String phone) {
        if (phone != null && !phone.trim().isEmpty()) {
            return fertilizerRepository.findByFarmerPhone(phone.trim());
        }
        return fertilizerRepository.findAll();
    }

    @PostMapping("/fertilizers")
    public ResponseEntity<FertilizerInventory> addFertilizer(@RequestBody FertilizerInventory item) {
        if (item.getId() == null || item.getId().trim().isEmpty()) {
            item.setId("FER-" + (System.currentTimeMillis() % 10000));
        }
        return ResponseEntity.ok(fertilizerRepository.save(item));
    }

    @DeleteMapping("/fertilizers/{id}")
    public ResponseEntity<?> deleteFertilizer(@PathVariable String id) {
        fertilizerRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // Crop Harvest Inventory
    @GetMapping("/harvests")
    public List<CropHarvestInventory> getHarvests(@RequestParam(required = false) String phone) {
        if (phone != null && !phone.trim().isEmpty()) {
            return harvestRepository.findByFarmerPhone(phone.trim());
        }
        return harvestRepository.findAll();
    }

    @PostMapping("/harvests")
    public ResponseEntity<CropHarvestInventory> addHarvest(@RequestBody CropHarvestInventory item) {
        if (item.getId() == null || item.getId().trim().isEmpty()) {
            item.setId("HRV-" + (System.currentTimeMillis() % 10000));
        }
        return ResponseEntity.ok(harvestRepository.save(item));
    }

    @DeleteMapping("/harvests/{id}")
    public ResponseEntity<?> deleteHarvest(@PathVariable String id) {
        harvestRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
