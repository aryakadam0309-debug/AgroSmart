package com.agrosmart.backend.repositories;

import com.agrosmart.backend.entities.NotificationItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationItem, String> {
    List<NotificationItem> findByFarmerPhoneOrFarmerPhoneIsNullOrderByTimestampDesc(String farmerPhone);
}
