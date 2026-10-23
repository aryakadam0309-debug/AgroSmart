package com.agrosmart.backend.repositories;

import com.agrosmart.backend.entities.FarmerFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FarmerFeedbackRepository extends JpaRepository<FarmerFeedback, String> {
    List<FarmerFeedback> findAllByOrderBySubmittedAtDesc();
    List<FarmerFeedback> findByFarmerPhone(String farmerPhone);
}
