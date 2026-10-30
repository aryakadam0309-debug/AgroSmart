package com.agrosmart.backend.repositories;

import com.agrosmart.backend.entities.Tractor;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TractorRepository extends JpaRepository<Tractor, String> {

    List<Tractor> findByBrandIgnoreCase(String brand);

    List<Tractor> findByIsAvailableTrue();

    List<Tractor> findByApprovalStatus(String approvalStatus);

    List<Tractor> findByOwnerPhone(String ownerPhone);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM Tractor t WHERE t.id = :id")
    Optional<Tractor> findByIdForUpdate(@Param("id") String id);
}
