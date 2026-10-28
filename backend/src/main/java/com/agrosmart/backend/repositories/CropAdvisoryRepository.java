package com.agrosmart.backend.repositories;

import com.agrosmart.backend.entities.CropAdvisory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CropAdvisoryRepository extends JpaRepository<CropAdvisory, String> {
    List<CropAdvisory> findByVerificationStatusOrderByDateAddedDesc(String status);
    List<CropAdvisory> findByCropNameAndVerificationStatus(String cropName, String status);
    List<CropAdvisory> findByCropNameIgnoreCaseAndProblemCategoryIgnoreCaseAndVerificationStatus(
            String cropName, String problemCategory, String verificationStatus);
    List<CropAdvisory> findAllByOrderByDateAddedDesc();
}
