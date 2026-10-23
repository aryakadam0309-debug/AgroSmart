package com.agrosmart.backend.controllers;

import com.agrosmart.backend.entities.DiseaseReport;
import com.agrosmart.backend.entities.NotificationItem;
import com.agrosmart.backend.repositories.DiseaseReportRepository;
import com.agrosmart.backend.repositories.NotificationRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/crops/disease-reports")
@CrossOrigin(origins = "*")
public class DiseaseReportController {

    private final DiseaseReportRepository diseaseReportRepository;
    private final NotificationRepository notificationRepository;

    public DiseaseReportController(DiseaseReportRepository diseaseReportRepository,
                                   NotificationRepository notificationRepository) {
        this.diseaseReportRepository = diseaseReportRepository;
        this.notificationRepository = notificationRepository;
    }

    @GetMapping
    public List<DiseaseReport> getReports(
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String district,
            @RequestParam(required = false) String status) {

        if (phone != null && !phone.trim().isEmpty()) {
            return diseaseReportRepository.findByFarmerPhoneOrderByReportedAtDesc(phone);
        }
        if (district != null && !district.trim().isEmpty()) {
            return diseaseReportRepository.findByDistrictOrderByReportedAtDesc(district);
        }
        if (status != null && !status.trim().isEmpty()) {
            return diseaseReportRepository.findByStatusOrderByReportedAtDesc(status);
        }
        return diseaseReportRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<DiseaseReport> submitReport(@RequestBody DiseaseReport report) {
        if (report.getId() == null || report.getId().trim().isEmpty()) {
            report.setId("REP-" + System.currentTimeMillis());
        }
        if (report.getReportedAt() == null || report.getReportedAt().trim().isEmpty()) {
            report.setReportedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")));
        }
        if (report.getStatus() == null || report.getStatus().trim().isEmpty()) {
            report.setStatus("REPORTED");
        }

        DiseaseReport saved = diseaseReportRepository.save(report);

        // Acknowledge farmer via notification
        try {
            NotificationItem notif = new NotificationItem();
            notif.setId("NOTIF-" + System.currentTimeMillis());
            notif.setFarmerPhone(saved.getFarmerPhone());
            notif.setTitle("🛡️ Crop Disease Report Submitted");
            notif.setMessage("Your report for " + saved.getCropName() + " symptoms has been forwarded to agricultural officers for triage.");
            notif.setTimestamp("Just now");
            notif.setType("DISEASE_REPORT");
            notif.setRead(false);
            notificationRepository.save(notif);
        } catch (Exception ignored) {}

        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateReportStatus(
            @PathVariable String id,
            @RequestBody Map<String, String> payload) {

        Optional<DiseaseReport> optional = diseaseReportRepository.findById(id);
        if (optional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        DiseaseReport report = optional.get();
        String newStatus = payload.getOrDefault("status", report.getStatus());
        String advisory = payload.get("adminAdvisory");

        report.setStatus(newStatus);
        if (advisory != null && !advisory.trim().isEmpty()) {
            report.setAdminAdvisory(advisory.trim());
        }

        DiseaseReport updated = diseaseReportRepository.save(report);

        // If verified, issue a regional advisory notification to the reporting farmer
        if ("VERIFIED_ALERT".equalsIgnoreCase(newStatus)) {
            try {
                NotificationItem notif = new NotificationItem();
                notif.setId("NOTIF-" + System.currentTimeMillis());
                notif.setFarmerPhone(report.getFarmerPhone());
                notif.setTitle("🚨 VERIFIED OUTBREAK ALERT: " + report.getCropName());
                notif.setMessage("Official Agronomist advisory for " + report.getDistrict() + ": " +
                        (advisory != null ? advisory : "Preventive fungicide spray recommended immediately."));
                notif.setTimestamp("Just now");
                notif.setType("DISEASE_ALERT");
                notif.setRead(false);
                notificationRepository.save(notif);
            } catch (Exception ignored) {}
        }

        return ResponseEntity.ok(updated);
    }
}
