package com.agrosmart.backend.repositories;

import com.agrosmart.backend.entities.DiseaseReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiseaseReportRepository extends JpaRepository<DiseaseReport, String> {
    List<DiseaseReport> findByFarmerPhoneOrderByReportedAtDesc(String farmerPhone);
    List<DiseaseReport> findByDistrictOrderByReportedAtDesc(String district);
    List<DiseaseReport> findByStatusOrderByReportedAtDesc(String status);
    List<DiseaseReport> findByCropNameOrderByReportedAtDesc(String cropName);
    List<DiseaseReport> findAllByOrderByReportedAtDesc();
}
