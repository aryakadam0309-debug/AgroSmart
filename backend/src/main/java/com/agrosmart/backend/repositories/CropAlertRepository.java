package com.agrosmart.backend.repositories;

import com.agrosmart.backend.entities.CropAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CropAlertRepository extends JpaRepository<CropAlert, String> {
    List<CropAlert> findByStatusOrderByPublishedAtDesc(String status);
    List<CropAlert> findByDistrictAndStatusOrderByPublishedAtDesc(String district, String status);
    List<CropAlert> findByCropNameAndStatusOrderByPublishedAtDesc(String cropName, String status);
    List<CropAlert> findAllByOrderByPublishedAtDesc();
}
