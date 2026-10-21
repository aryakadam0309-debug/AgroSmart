package com.agrosmart.backend.repositories;

import com.agrosmart.backend.entities.SensorReading;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SensorReadingRepository extends JpaRepository<SensorReading, String> {
    Optional<SensorReading> findFirstByDeviceIdOrderByRecordedAtDesc(String deviceId);
    List<SensorReading> findByDeviceIdOrderByRecordedAtDesc(String deviceId, Pageable pageable);
    List<SensorReading> findByFarmerIdOrderByRecordedAtDesc(Long farmerId, Pageable pageable);
}
