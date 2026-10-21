package com.agrosmart.backend.repositories;

import com.agrosmart.backend.entities.TractorBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TractorBookingRepository extends JpaRepository<TractorBooking, String> {
    List<TractorBooking> findByFarmerPhone(String farmerPhone);
    List<TractorBooking> findByStatus(String status);
}
