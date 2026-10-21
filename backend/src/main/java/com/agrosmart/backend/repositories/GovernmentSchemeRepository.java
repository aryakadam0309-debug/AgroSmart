package com.agrosmart.backend.repositories;

import com.agrosmart.backend.entities.GovernmentScheme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GovernmentSchemeRepository extends JpaRepository<GovernmentScheme, String> {
    List<GovernmentScheme> findByCategoryIgnoreCase(String category);
}
