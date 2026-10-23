package com.agrosmart.backend.controllers;

import com.agrosmart.backend.entities.CropHarvestInventory;
import com.agrosmart.backend.entities.FertilizerInventory;
import com.agrosmart.backend.entities.NotificationItem;
import com.agrosmart.backend.repositories.CropHarvestInventoryRepository;
import com.agrosmart.backend.repositories.FertilizerInventoryRepository;
import com.agrosmart.backend.repositories.NotificationRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@CrossOrigin(origins = "*")
public class InventoryController {

    private final FertilizerInventoryRepository fertilizerRepository;
    private final CropHarvestInventoryRepository harvestRepository;
    private final NotificationRepository notificationRepository;

    public InventoryController(FertilizerInventoryRepository fertilizerRepository,
                               CropHarvestInventoryRepository harvestRepository,
                               NotificationRepository notificationRepository) {
        this.fertilizerRepository = fertilizerRepository;
        this.harvestRepository = harvestRepository;
        this.notificationRepository = notificationRepository;
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
        if (item.getFarmerPhone() == null || item.getFarmerPhone().trim().isEmpty()) {
            item.setFarmerPhone("9876543210");
        }
        FertilizerInventory saved = fertilizerRepository.save(item);

        if (saved.getQuantityBags() <= saved.getLowStockThreshold()) {
            try {
                NotificationItem notif = new NotificationItem();
                notif.setId("NOTIF-" + System.currentTimeMillis());
                notif.setFarmerPhone(saved.getFarmerPhone());
                notif.setTitle("⚠️ Low Stock Alert: " + saved.getName());
                notif.setMessage("Inventory running low (" + saved.getQuantityBags() + " bags left). Reorder recommended.");
                notif.setTimestamp("Just now");
                notif.setType("INVENTORY");
                notif.setRead(false);
                notificationRepository.save(notif);
            } catch (Exception ignored) {}
        }

        return ResponseEntity.ok(saved);
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
        if (item.getFarmerPhone() == null || item.getFarmerPhone().trim().isEmpty()) {
            item.setFarmerPhone("9876543210");
        }
        if (item.getStockRemainingQuintals() <= 0 && item.getHarvestedQuintals() > 0) {
            item.setStockRemainingQuintals(Math.max(0, item.getHarvestedQuintals() - item.getSoldQuintals()));
        }
        return ResponseEntity.ok(harvestRepository.save(item));
    }

    @DeleteMapping("/harvests/{id}")
    public ResponseEntity<?> deleteHarvest(@PathVariable String id) {
        harvestRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
