package com.agrosmart.backend.repositories;

import com.agrosmart.backend.entities.AgriNews;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AgriNewsRepository extends JpaRepository<AgriNews, String> {
    List<AgriNews> findByCategoryIgnoreCase(String category);
}
