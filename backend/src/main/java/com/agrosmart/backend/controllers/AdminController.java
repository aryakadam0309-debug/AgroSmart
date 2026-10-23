package com.agrosmart.backend.controllers;

import com.agrosmart.backend.repositories.*;
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
    private final OrderRepository orderRepository;
    private final CropPriceRepository cropPriceRepository;
    private final SoilHealthRecordRepository soilHealthRepository;
    private final FarmerFeedbackRepository feedbackRepository;
    private final DiseaseReportRepository diseaseReportRepository;

    public AdminController(UserRepository userRepository,
                           OrderRepository orderRepository,
                           CropPriceRepository cropPriceRepository,
                           SoilHealthRecordRepository soilHealthRepository,
                           FarmerFeedbackRepository feedbackRepository,
                           DiseaseReportRepository diseaseReportRepository) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.cropPriceRepository = cropPriceRepository;
        this.soilHealthRepository = soilHealthRepository;
        this.feedbackRepository = feedbackRepository;
        this.diseaseReportRepository = diseaseReportRepository;
    }

    @GetMapping("/stats")
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        long farmers = userRepository.count();
        long soilTests = soilHealthRepository.count();
        long feedbackCount = feedbackRepository.count();
        long orders = orderRepository.count();
        long cropPrices = cropPriceRepository.count();
        long diseaseAlerts = diseaseReportRepository.count();

        stats.put("totalFarmers", farmers);
        stats.put("totalAdvisories", soilTests);
        stats.put("totalFeedback", feedbackCount);
        stats.put("totalOrders", orders);
        stats.put("totalProducts", cropPrices);
        stats.put("totalDiseaseReports", diseaseAlerts);

        // Backward compatibility mappings
        stats.put("totalTractors", soilTests);
        stats.put("totalBookings", feedbackCount);
        stats.put("totalRevenue", orders * 1500.0);

        return stats;
    }
}
