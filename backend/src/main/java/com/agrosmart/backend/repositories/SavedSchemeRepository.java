package com.agrosmart.backend.repositories;

import com.agrosmart.backend.entities.SavedScheme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SavedSchemeRepository extends JpaRepository<SavedScheme, Long> {
    List<SavedScheme> findByFarmerPhoneOrderBySavedAtDesc(String farmerPhone);
    Optional<SavedScheme> findByFarmerPhoneAndSchemeId(String farmerPhone, String schemeId);
    void deleteByFarmerPhoneAndSchemeId(String farmerPhone, String schemeId);
    boolean existsByFarmerPhoneAndSchemeId(String farmerPhone, String schemeId);
}
