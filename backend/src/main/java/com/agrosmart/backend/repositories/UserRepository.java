package com.agrosmart.backend.repositories;

import com.agrosmart.backend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByPhone(String phone);
    Optional<User> findByEmail(String email);
    Optional<User> findByGoogleId(String googleId);
    boolean existsByPhone(String phone);
    boolean existsByEmail(String email);
    boolean existsByGoogleId(String googleId);
}
