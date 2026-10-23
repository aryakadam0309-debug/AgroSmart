package com.agrosmart.backend.repositories;

import com.agrosmart.backend.entities.SavedCropPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SavedCropPlanRepository extends JpaRepository<SavedCropPlan, String> {
    List<SavedCropPlan> findByFarmerPhoneOrderByCreatedDateDesc(String farmerPhone);
    List<SavedCropPlan> findByFarmerIdOrderByCreatedDateDesc(Long farmerId);
}
