package com.agrosmart.backend.repositories;

import com.agrosmart.backend.entities.FertilizerInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FertilizerInventoryRepository extends JpaRepository<FertilizerInventory, String> {
    List<FertilizerInventory> findByFarmerPhone(String farmerPhone);
}
