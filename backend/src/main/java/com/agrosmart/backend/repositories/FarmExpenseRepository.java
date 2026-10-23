package com.agrosmart.backend.repositories;

import com.agrosmart.backend.entities.FarmExpense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FarmExpenseRepository extends JpaRepository<FarmExpense, String> {
    List<FarmExpense> findByFarmerPhoneOrderByEntryDateDesc(String farmerPhone);
}
