package com.agrosmart.backend.repositories;

import com.agrosmart.backend.entities.IotAlert;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IotAlertRepository extends JpaRepository<IotAlert, String> {
    List<IotAlert> findByDeviceIdOrderByCreatedAtDesc(String deviceId, Pageable pageable);
    List<IotAlert> findByFarmerIdOrderByCreatedAtDesc(Long farmerId, Pageable pageable);
    boolean existsByDeviceIdAndAlertTypeAndIsReadFalse(String deviceId, String alertType);
}
