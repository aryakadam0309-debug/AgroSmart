package com.agrosmart.backend.repositories;

import com.agrosmart.backend.entities.WeatherAdvisoryChange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WeatherAdvisoryChangeRepository extends JpaRepository<WeatherAdvisoryChange, Long> {
    List<WeatherAdvisoryChange> findByFarmerPhoneOrderByDetectedAtDesc(String farmerPhone);
    List<WeatherAdvisoryChange> findByLocationNameOrderByDetectedAtDesc(String locationName);
    Optional<WeatherAdvisoryChange> findTopByFarmerPhoneOrderByDetectedAtDesc(String farmerPhone);
    Optional<WeatherAdvisoryChange> findTopByFarmerPhoneAndDedupKey(String farmerPhone, String dedupKey);
    boolean existsByFarmerPhoneAndDedupKey(String farmerPhone, String dedupKey);
}
