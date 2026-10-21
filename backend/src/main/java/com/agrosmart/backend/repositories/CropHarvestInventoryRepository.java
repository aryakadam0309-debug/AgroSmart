package com.agrosmart.backend.repositories;

import com.agrosmart.backend.entities.CropHarvestInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CropHarvestInventoryRepository extends JpaRepository<CropHarvestInventory, String> {
    List<CropHarvestInventory> findByFarmerPhone(String farmerPhone);
}
