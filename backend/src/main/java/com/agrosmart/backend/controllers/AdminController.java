package com.agrosmart.backend.controllers;

import com.agrosmart.backend.repositories.CropPriceRepository;
import com.agrosmart.backend.repositories.TractorBookingRepository;
import com.agrosmart.backend.repositories.TractorRepository;
import com.agrosmart.backend.repositories.UserRepository;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    private final UserRepository userRepository;
    private final TractorRepository tractorRepository;
    private final TractorBookingRepository bookingRepository;
    private final CropPriceRepository cropPriceRepository;

    public AdminController(UserRepository userRepository, TractorRepository tractorRepository,
                           TractorBookingRepository bookingRepository, CropPriceRepository cropPriceRepository) {
        this.userRepository = userRepository;
        this.tractorRepository = tractorRepository;
        this.bookingRepository = bookingRepository;
        this.cropPriceRepository = cropPriceRepository;
    }

    @GetMapping("/stats")
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalFarmers", userRepository.count());
        stats.put("activeTractors", tractorRepository.count());
        stats.put("pendingBookings", bookingRepository.findByStatus("PENDING").size());
        stats.put("totalCropsMonitored", cropPriceRepository.count());
        stats.put("marketplaceRevenue", 48500.0);
        return stats;
    }
}
