package com.agrosmart.backend.repositories;

import com.agrosmart.backend.entities.IotDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IotDeviceRepository extends JpaRepository<IotDevice, String> {
    List<IotDevice> findByOwnerId(Long ownerId);
    List<IotDevice> findByFarmerPhone(String farmerPhone);
    Optional<IotDevice> findByDeviceIdAndApiKey(String deviceId, String apiKey);
}
