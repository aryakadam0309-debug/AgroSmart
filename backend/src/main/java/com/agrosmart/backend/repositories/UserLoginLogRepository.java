package com.agrosmart.backend.repositories;

import com.agrosmart.backend.entities.UserLoginLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserLoginLogRepository extends JpaRepository<UserLoginLog, Long> {
    List<UserLoginLog> findTop20ByOrderByLoginTimeDesc();
}
