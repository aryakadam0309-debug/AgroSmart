package com.agrosmart.backend.repositories;

import com.agrosmart.backend.entities.PumpCommand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PumpCommandRepository extends JpaRepository<PumpCommand, String> {
    List<PumpCommand> findByDeviceIdAndStatusOrderByCreatedAtAsc(String deviceId, String status);
    Optional<PumpCommand> findFirstByDeviceIdOrderByCreatedAtDesc(String deviceId);
}
