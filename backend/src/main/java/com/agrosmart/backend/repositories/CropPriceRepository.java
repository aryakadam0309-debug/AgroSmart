package com.agrosmart.backend.repositories;

import com.agrosmart.backend.entities.CropPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CropPriceRepository extends JpaRepository<CropPrice, String> {
    List<CropPrice> findByStateIgnoreCase(String state);
    List<CropPrice> findByCategoryIgnoreCase(String category);
    List<CropPrice> findByCropNameContainingIgnoreCase(String query);
}
