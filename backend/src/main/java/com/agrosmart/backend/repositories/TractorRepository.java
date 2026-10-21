package com.agrosmart.backend.repositories;

import com.agrosmart.backend.entities.Tractor;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TractorRepository extends JpaRepository<Tractor, String> {
    List<Tractor> findByBrandIgnoreCase(String brand);
    List<Tractor> findByIsAvailableTrue();
}
