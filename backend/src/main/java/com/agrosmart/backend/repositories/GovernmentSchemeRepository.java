package com.agrosmart.backend.repositories;

import com.agrosmart.backend.entities.GovernmentScheme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GovernmentSchemeRepository extends JpaRepository<GovernmentScheme, String> {

    List<GovernmentScheme> findByCategoryIgnoreCase(String category);

    List<GovernmentScheme> findByLevelIgnoreCase(String level);

    @Query("SELECT s FROM GovernmentScheme s WHERE " +
           "(:category IS NULL OR :category = 'All' OR LOWER(s.category) = LOWER(:category)) AND " +
           "(:level IS NULL OR :level = 'All' OR LOWER(s.level) = LOWER(:level)) AND " +
           "(:state IS NULL OR :state = 'ALL_INDIA' OR LOWER(s.applicableState) = 'all_india' OR LOWER(s.applicableState) = LOWER(:state)) AND " +
           "(:status IS NULL OR :status = 'All' OR LOWER(s.applicationStatus) = LOWER(:status)) AND " +
           "(:query IS NULL OR :query = '' OR LOWER(s.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(s.description) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<GovernmentScheme> filterSchemes(
            @Param("category") String category,
            @Param("level") String level,
            @Param("state") String state,
            @Param("status") String status,
            @Param("query") String query
    );
}
