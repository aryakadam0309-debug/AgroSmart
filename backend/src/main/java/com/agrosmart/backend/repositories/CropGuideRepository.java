package com.agrosmart.backend.repositories;

import com.agrosmart.backend.entities.CropGuide;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CropGuideRepository extends JpaRepository<CropGuide, String> {
    List<CropGuide> findBySeasonIgnoreCase(String season);
    List<CropGuide> findByCropNameContainingIgnoreCase(String keyword);
}
