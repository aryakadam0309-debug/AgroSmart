package com.agrosmart.backend.controllers;

import com.agrosmart.backend.entities.CropAlert;
import com.agrosmart.backend.entities.DiseaseReport;
import com.agrosmart.backend.entities.NotificationItem;
import com.agrosmart.backend.entities.User;
import com.agrosmart.backend.payload.CropClusterDto;
import com.agrosmart.backend.repositories.CropAlertRepository;
import com.agrosmart.backend.repositories.DiseaseReportRepository;
import com.agrosmart.backend.repositories.NotificationRepository;
import com.agrosmart.backend.repositories.UserRepository;
import com.agrosmart.backend.services.CropClusterService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/crops/disease-reports")
@CrossOrigin(origins = "*")
public class DiseaseReportController {

    private static final String UPLOAD_DIR = "uploads/crop-reports/";
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 MB

    private final DiseaseReportRepository diseaseReportRepository;
    private final NotificationRepository notificationRepository;
    private final CropAlertRepository cropAlertRepository;
    private final UserRepository userRepository;
    private final CropClusterService cropClusterService;

    public DiseaseReportController(DiseaseReportRepository diseaseReportRepository,
                                   NotificationRepository notificationRepository,
                                   CropAlertRepository cropAlertRepository,
                                   UserRepository userRepository,
                                   CropClusterService cropClusterService) {
        this.diseaseReportRepository = diseaseReportRepository;
        this.notificationRepository = notificationRepository;
        this.cropAlertRepository = cropAlertRepository;
        this.userRepository = userRepository;
        this.cropClusterService = cropClusterService;

        // Ensure upload directory exists
        File uploadFolder = new File(UPLOAD_DIR);
        if (!uploadFolder.exists()) {
            uploadFolder.mkdirs();
        }
    }

    // ==========================================
    // 1. FARMER & COMMUNITY REPORTS
    // ==========================================

    @GetMapping
    public List<DiseaseReport> getReports(
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String district,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String crop) {

        if (phone != null && !phone.trim().isEmpty()) {
            return diseaseReportRepository.findByFarmerPhoneOrderByReportedAtDesc(phone.trim());
        }
        if (district != null && !district.trim().isEmpty()) {
            return diseaseReportRepository.findByDistrictOrderByReportedAtDesc(district.trim());
        }
        if (crop != null && !crop.trim().isEmpty()) {
            return diseaseReportRepository.findByCropNameOrderByReportedAtDesc(crop.trim());
        }
        if (status != null && !status.trim().isEmpty()) {
            return diseaseReportRepository.findByStatusOrderByReportedAtDesc(status.trim());
        }
        return diseaseReportRepository.findAllByOrderByReportedAtDesc();
    }

    @PostMapping
    public ResponseEntity<?> submitReport(@RequestBody DiseaseReport report) {
        if (report.getCropName() == null || report.getCropName().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Crop name is required"));
        }
        if ((report.getSymptoms() == null || report.getSymptoms().trim().isEmpty()) &&
                (report.getProblemCategory() == null || report.getProblemCategory().trim().isEmpty())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Observed symptoms or problem category is required"));
        }

        if (report.getId() == null || report.getId().trim().isEmpty()) {
            report.setId("CW-REP-" + System.currentTimeMillis());
        }
        if (report.getReportedAt() == null || report.getReportedAt().trim().isEmpty()) {
            report.setReportedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")));
        }
        if (report.getStatus() == null || report.getStatus().trim().isEmpty()) {
            report.setStatus("PENDING_REVIEW");
        }
        if (report.getSymptoms() == null || report.getSymptoms().trim().isEmpty()) {
            report.setSymptoms(report.getProblemCategory() != null ? report.getProblemCategory() : "Reported crop problem");
        }

        DiseaseReport saved = diseaseReportRepository.save(report);

        // Acknowledge farmer via persistent in-app notification
        try {
            NotificationItem notif = new NotificationItem();
            notif.setId("NOTIF-" + System.currentTimeMillis());
            notif.setFarmerPhone(saved.getFarmerPhone());
            notif.setTitle("🛡️ Crop Watch: Report Submitted");
            notif.setMessage("Your problem report for " + saved.getCropName() + " (" +
                    (saved.getProblemCategory() != null ? saved.getProblemCategory() : "Symptoms") +
                    ") in " + (saved.getVillage() != null ? saved.getVillage() : "your farm") +
                    " has been recorded. Reference ID: " + saved.getId() + ". Our agronomy team will review it.");
            notif.setTimestamp("Just now");
            notif.setType("CROP_WATCH_REPORT");
            notif.setRead(false);
            notificationRepository.save(notif);
        } catch (Exception ignored) {}

        return ResponseEntity.ok(saved);
    }

    // ==========================================
    // 2. ADMINISTRATOR REPORT REVIEW & TRIAGE
    // ==========================================

    @PutMapping("/{id}/review")
    public ResponseEntity<?> reviewReport(
            @PathVariable String id,
            @RequestBody Map<String, String> payload) {

        Optional<DiseaseReport> optional = diseaseReportRepository.findById(id);
        if (optional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        DiseaseReport report = optional.get();
        String newStatus = payload.get("status");
        String reviewNote = payload.get("reviewNote");
        String adminGuidance = payload.get("adminGuidance");
        String adminSource = payload.get("adminSource");
        String reviewedBy = payload.get("reviewedBy");

        if (newStatus != null && !newStatus.trim().isEmpty()) {
            report.setStatus(newStatus.trim().toUpperCase());
        }
        if (reviewNote != null) {
            report.setReviewNote(reviewNote.trim());
        }
        if (adminGuidance != null && !adminGuidance.trim().isEmpty()) {
            report.setAdminGuidance(adminGuidance.trim());
            report.setAdminAdvisory(adminGuidance.trim()); // backward compatibility
        }
        if (adminSource != null) {
            report.setAdminSource(adminSource.trim());
        }
        if (reviewedBy != null) {
            report.setReviewedBy(reviewedBy.trim());
        }
        report.setReviewedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")));

        DiseaseReport updated = diseaseReportRepository.save(report);

        // Notify reporting farmer of status update
        try {
            NotificationItem notif = new NotificationItem();
            notif.setId("NOTIF-" + System.currentTimeMillis());
            notif.setFarmerPhone(report.getFarmerPhone());
            notif.setTitle("🌾 Crop Watch: Report Update — " + report.getCropName());

            String msg = "Your report (Ref: " + report.getId() + ") status is now: " + report.getStatus() + ".";
            if (report.getAdminGuidance() != null && !report.getAdminGuidance().isEmpty()) {
                msg += " Advisory: " + report.getAdminGuidance();
                if (report.getAdminSource() != null && !report.getAdminSource().isEmpty()) {
                    msg += " [Source: " + report.getAdminSource() + "]";
                }
            }
            notif.setMessage(msg);
            notif.setTimestamp("Just now");
            notif.setType("CROP_WATCH_UPDATE");
            notif.setRead(false);
            notificationRepository.save(notif);
        } catch (Exception ignored) {}

        return ResponseEntity.ok(updated);
    }

    // Backward compatibility with older status endpoint
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateReportStatus(
            @PathVariable String id,
            @RequestBody Map<String, String> payload) {
        return reviewReport(id, payload);
    }

    // ==========================================
    // 3. COMMUNITY CLUSTER DETECTION
    // ==========================================

    @GetMapping("/clusters")
    public ResponseEntity<List<CropClusterDto>> getClusters() {
        List<CropClusterDto> clusters = cropClusterService.detectClusters();
        return ResponseEntity.ok(clusters);
    }

    // ==========================================
    // 4. REGIONAL CROP ALERTS
    // ==========================================

    @GetMapping("/alerts")
    public ResponseEntity<List<CropAlert>> getAlerts(
            @RequestParam(required = false) String district,
            @RequestParam(required = false) String status) {

        String queryStatus = (status != null && !status.trim().isEmpty()) ? status.trim().toUpperCase() : "ACTIVE";

        if ("ALL".equalsIgnoreCase(queryStatus)) {
            return ResponseEntity.ok(cropAlertRepository.findAllByOrderByPublishedAtDesc());
        }

        if (district != null && !district.trim().isEmpty()) {
            return ResponseEntity.ok(cropAlertRepository.findByDistrictAndStatusOrderByPublishedAtDesc(district.trim(), queryStatus));
        }

        return ResponseEntity.ok(cropAlertRepository.findByStatusOrderByPublishedAtDesc(queryStatus));
    }

    @PostMapping("/alerts")
    public ResponseEntity<?> publishAlert(@RequestBody CropAlert alert) {
        if (alert.getCropName() == null || alert.getCropName().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Crop name is required"));
        }
        if (alert.getDistrict() == null || alert.getDistrict().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "District is required"));
        }
        if (alert.getAlertTitle() == null || alert.getAlertTitle().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Alert title is required"));
        }
        if (alert.getSummary() == null || alert.getSummary().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Alert summary is required"));
        }

        if (alert.getId() == null || alert.getId().trim().isEmpty()) {
            alert.setId("CW-ALERT-" + System.currentTimeMillis());
        }
        if (alert.getStatus() == null || alert.getStatus().trim().isEmpty()) {
            alert.setStatus("ACTIVE");
        }
        if (alert.getPublishedAt() == null || alert.getPublishedAt().trim().isEmpty()) {
            alert.setPublishedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")));
        }
        if (alert.getAuthority() == null || alert.getAuthority().trim().isEmpty()) {
            alert.setAuthority("District Agricultural Extension & Agronomy Advisory");
        }

        CropAlert saved = cropAlertRepository.save(alert);

        // Targeted Persistent In-App Notifications for farmers in this district / crop
        try {
            List<User> targetFarmers = userRepository.findAll();
            for (User u : targetFarmers) {
                // If farmer is in the same district or has no strict location filter
                boolean districtMatch = (u.getDistrict() != null &&
                        (u.getDistrict().toLowerCase().contains(saved.getDistrict().toLowerCase()) ||
                                saved.getDistrict().toLowerCase().contains(u.getDistrict().toLowerCase())));

                if (districtMatch || "farmer".equalsIgnoreCase(u.getRole()) || "ROLE_FARMER".equalsIgnoreCase(u.getRole())) {
                    NotificationItem notif = new NotificationItem();
                    notif.setId("NOTIF-ALERT-" + System.currentTimeMillis() + "-" + (u.getPhone() != null ? u.getPhone() : UUID.randomUUID().toString().substring(0, 5)));
                    notif.setFarmerPhone(u.getPhone());
                    notif.setTitle("📢 Crop Watch Alert: " + saved.getCropName() + " (" + saved.getDistrict() + ")");
                    String notifBody = saved.getAlertTitle() + "\n" + saved.getSummary();
                    if (saved.getRecommendedActions() != null && !saved.getRecommendedActions().isEmpty()) {
                        notifBody += "\nRecommended Action: " + saved.getRecommendedActions();
                    }
                    notif.setMessage(notifBody);
                    notif.setTimestamp("Just now");
                    notif.setType("COMMUNITY_CROP_ALERT");
                    notif.setRead(false);
                    notificationRepository.save(notif);
                }
            }
        } catch (Exception ignored) {}

        return ResponseEntity.ok(saved);
    }

    @PutMapping("/alerts/{id}/status")
    public ResponseEntity<?> updateAlertStatus(
            @PathVariable String id,
            @RequestBody Map<String, String> payload) {

        Optional<CropAlert> optional = cropAlertRepository.findById(id);
        if (optional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        CropAlert alert = optional.get();
        String newStatus = payload.get("status");
        if (newStatus != null && !newStatus.trim().isEmpty()) {
            alert.setStatus(newStatus.trim().toUpperCase());
            if ("RESOLVED".equalsIgnoreCase(newStatus) || "WITHDRAWN".equalsIgnoreCase(newStatus)) {
                alert.setResolvedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")));
            }
        }

        CropAlert updated = cropAlertRepository.save(alert);
        return ResponseEntity.ok(updated);
    }

    // ==========================================
    // 5. SAFE PHOTOGRAPH UPLOAD & SERVING
    // ==========================================

    @PostMapping("/upload-image")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "No file selected"));
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                    .body(Map.of("error", "Image file exceeds maximum 5MB size limit"));
        }

        String contentType = file.getContentType();
        if (contentType == null || (!contentType.equals("image/jpeg") &&
                !contentType.equals("image/png") &&
                !contentType.equals("image/webp"))) {
            return ResponseEntity.badRequest().body(Map.of("error", "Only JPEG, PNG, and WebP images are allowed"));
        }

        try {
            String originalFilename = file.getOriginalFilename();
            String extension = ".jpg";
            if (originalFilename != null && originalFilename.lastIndexOf(".") != -1) {
                extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
            }

            String filename = "report_" + UUID.randomUUID().toString() + extension;
            Path targetPath = Paths.get(UPLOAD_DIR).resolve(filename);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            String relativeUrl = "/api/crops/disease-reports/images/" + filename;
            return ResponseEntity.ok(Map.of(
                    "imageUrl", relativeUrl,
                    "filename", filename,
                    "size", file.getSize()
            ));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to store image: " + e.getMessage()));
        }
    }

    @GetMapping("/images/{filename:.+}")
    public ResponseEntity<Resource> serveImage(@PathVariable String filename) {
        try {
            Path file = Paths.get(UPLOAD_DIR).resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                MediaType mediaType = MediaType.IMAGE_JPEG;
                if (filename.toLowerCase().endsWith(".png")) {
                    mediaType = MediaType.IMAGE_PNG;
                } else if (filename.toLowerCase().endsWith(".webp")) {
                    mediaType = MediaType.parseMediaType("image/webp");
                }

                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .contentType(mediaType)
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
