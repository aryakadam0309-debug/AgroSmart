package com.agrosmart.backend.repositories;

import com.agrosmart.backend.entities.CropDisease;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CropDiseaseRepository extends JpaRepository<CropDisease, String> {
    List<CropDisease> findByCropNameIgnoreCase(String cropName);
    List<CropDisease> findByDiseaseNameContainingIgnoreCaseOrSymptomsContainingIgnoreCase(String name, String symptom);
}
