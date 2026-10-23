package com.agrosmart.backend.repositories;

import com.agrosmart.backend.entities.SoilHealthRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SoilHealthRecordRepository extends JpaRepository<SoilHealthRecord, String> {
    List<SoilHealthRecord> findByFarmerPhoneOrderBySampleDateDesc(String farmerPhone);
}
