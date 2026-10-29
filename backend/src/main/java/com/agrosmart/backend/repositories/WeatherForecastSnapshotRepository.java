package com.agrosmart.backend.repositories;

import com.agrosmart.backend.entities.WeatherForecastSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WeatherForecastSnapshotRepository extends JpaRepository<WeatherForecastSnapshot, Long> {
    List<WeatherForecastSnapshot> findByFarmerPhoneAndLocationNameOrderByForecastDateAsc(String farmerPhone, String locationName);
    List<WeatherForecastSnapshot> findByLocationNameOrderByForecastDateAsc(String locationName);
    Optional<WeatherForecastSnapshot> findTopByFarmerPhoneAndForecastDateOrderByRetrievedAtDesc(String farmerPhone, String forecastDate);
    Optional<WeatherForecastSnapshot> findTopByLocationNameAndForecastDateOrderByRetrievedAtDesc(String locationName, String forecastDate);
    void deleteByFarmerPhoneAndLocationName(String farmerPhone, String locationName);
}
