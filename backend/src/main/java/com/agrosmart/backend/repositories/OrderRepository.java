package com.agrosmart.backend.repositories;

import com.agrosmart.backend.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
    List<Order> findByFarmerPhoneOrderByOrderDateDesc(String farmerPhone);
}
