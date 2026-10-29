package com.agrosmart.backend.repositories;

import com.agrosmart.backend.entities.DeviceToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceTokenRepository extends JpaRepository<DeviceToken, Long> {
    List<DeviceToken> findByFarmerPhone(String farmerPhone);
    Optional<DeviceToken> findByToken(String token);
    void deleteByToken(String token);
}
