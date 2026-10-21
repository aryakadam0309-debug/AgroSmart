package com.agrosmart.backend.repositories;

import com.agrosmart.backend.entities.IrrigationSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IrrigationSettingRepository extends JpaRepository<IrrigationSetting, String> {
    Optional<IrrigationSetting> findByDeviceId(String deviceId);
}
