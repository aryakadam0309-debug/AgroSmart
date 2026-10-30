package com.agrosmart.backend.controllers;

import com.agrosmart.backend.entities.Tractor;
import com.agrosmart.backend.entities.TractorBooking;
import com.agrosmart.backend.exceptions.BookingConflictException;
import com.agrosmart.backend.repositories.TractorRepository;
import com.agrosmart.backend.security.AuthHelper;
import com.agrosmart.backend.services.TractorBookingService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tractors")
@CrossOrigin(origins = "*")
public class TractorController {

    private final TractorBookingService bookingService;
    private final TractorRepository tractorRepository;
    private final AuthHelper authHelper;

    public TractorController(TractorBookingService bookingService,
                             TractorRepository tractorRepository,
                             AuthHelper authHelper) {
        this.bookingService = bookingService;
        this.tractorRepository = tractorRepository;
        this.authHelper = authHelper;
    }

    @GetMapping
    public List<Tractor> getAllTractors(@RequestParam(required = false) String brand) {
        return bookingService.getApprovedTractors(brand);
    }

    /**
     * Area-Wise Nearby Tractor Search within 20 km (or custom radius).
     * Excludes tractors exceeding 20 km, inactive machines, unapproved listings,
     * and tractors with overlapping bookings for the selected date.
     * Returns matching tractors sorted from nearest to farthest.
     */
    @GetMapping("/nearby")
    public ResponseEntity<?> getNearbyTractors(@RequestParam double lat,
                                               @RequestParam double lon,
                                               @RequestParam(required = false, defaultValue = "20.0") Double radiusKm,
                                               @RequestParam(required = false) String bookingDate,
                                               @RequestParam(required = false) String brand) {
        try {
            List<Tractor> nearby = bookingService.searchNearbyTractors(lat, lon, radiusKm, bookingDate, brand);
            return ResponseEntity.ok(nearby);
        } catch (IllegalArgumentException iae) {
            return ResponseEntity.badRequest().body(Map.of("error", iae.getMessage()));
        }
    }

    @GetMapping("/resolve-location")
    public ResponseEntity<Map<String, Object>> resolveLocation(@RequestParam String query) {
        String q = query.toLowerCase().trim();
        double lat = 17.2892;
        double lon = 74.1818;
        String resolvedName = "Karad, Satara";

        if (q.contains("karad")) {
            lat = 17.2892; lon = 74.1818; resolvedName = "Karad, Satara";
        } else if (q.contains("baramati")) {
            lat = 18.1519; lon = 74.5771; resolvedName = "Baramati, Pune";
        } else if (q.contains("satara")) {
            lat = 17.6805; lon = 74.0183; resolvedName = "Satara City, Satara";
        } else if (q.contains("pune")) {
            lat = 18.5204; lon = 73.8567; resolvedName = "Pune, Maharashtra";
        } else if (q.contains("nashik") || q.contains("nasik")) {
            lat = 19.9975; lon = 73.7898; resolvedName = "Nashik, Maharashtra";
        } else if (q.contains("kolhapur")) {
            lat = 16.7050; lon = 74.2433; resolvedName = "Kolhapur, Maharashtra";
        } else if (q.contains("sangli")) {
            lat = 16.8524; lon = 74.5815; resolvedName = "Sangli, Maharashtra";
        } else if (q.contains("shirur")) {
            lat = 18.8272; lon = 74.3792; resolvedName = "Shirur, Pune";
        } else if (q.contains("daund")) {
            lat = 18.4655; lon = 74.5828; resolvedName = "Daund, Pune";
        }

        return ResponseEntity.ok(Map.of(
                "query", query,
                "resolvedName", resolvedName,
                "latitude", lat,
                "longitude", lon
        ));
    }

    @GetMapping("/all")
    public List<Tractor> getAllTractorsForAdmin() {
        return tractorRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> addTractor(@RequestBody Tractor tractor,
                                        HttpServletRequest request) {
        if (tractor.getId() == null || tractor.getId().trim().isEmpty()) {
            tractor.setId("TR-" + (System.currentTimeMillis() % 100000));
        }
        if (tractor.getApprovalStatus() == null || tractor.getApprovalStatus().isEmpty()) {
            tractor.setApprovalStatus("APPROVED"); // Defaults to auto-approved in development/hackathon
        }
        tractor.setAvailable(true);
        Tractor saved = tractorRepository.save(tractor);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/book")
    public ResponseEntity<?> createBooking(@RequestBody TractorBooking booking,
                                           @RequestParam(required = false) String phone,
                                           @RequestParam(required = false) String name,
                                           HttpServletRequest request) {
        try {
            String candidatePhone = (booking.getFarmerPhone() != null && !booking.getFarmerPhone().trim().isEmpty())
                    ? booking.getFarmerPhone().trim()
                    : (phone != null && !phone.trim().isEmpty() ? phone.trim() : null);

            String renterPhone = candidatePhone;
            if (renterPhone == null && authHelper != null) {
                try {
                    renterPhone = authHelper.resolveAuthenticatedPhone(request, candidatePhone);
                } catch (Exception ignored) {}
            }
            if (renterPhone == null || renterPhone.trim().isEmpty()) {
                renterPhone = "9876543210";
            }

            String candidateName = (booking.getFarmerName() != null && !booking.getFarmerName().trim().isEmpty())
                    ? booking.getFarmerName().trim()
                    : (name != null && !name.trim().isEmpty() ? name.trim() : "Farmer");

            TractorBooking created = bookingService.confirmDirectBooking(booking, renterPhone, candidateName);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (BookingConflictException bce) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", bce.getMessage(), "code", "TRACTOR_UNAVAILABLE", "type", "DATE_CONFLICT"));
        } catch (IllegalStateException ise) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", ise.getMessage()));
        } catch (IllegalArgumentException iae) {
            return ResponseEntity.badRequest().body(Map.of("error", iae.getMessage()));
        } catch (SecurityException se) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", se.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/book/create-order")
    public ResponseEntity<?> createPaymentOrder(@RequestBody Map<String, String> payload,
                                                HttpServletRequest request) {
        try {
            String bookingId = payload.get("bookingId");
            String fallbackPhone = payload.get("phone");
            String renterPhone = authHelper.resolveAuthenticatedPhone(request, fallbackPhone);

            Map<String, Object> orderDetails = bookingService.createPaymentOrder(bookingId, renterPhone);
            return ResponseEntity.ok(orderDetails);
        } catch (IllegalStateException ise) {
            return ResponseEntity.status(HttpStatus.GONE).body(Map.of("error", ise.getMessage()));
        } catch (SecurityException se) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", se.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/book/verify-payment")
    public ResponseEntity<?> verifyPayment(@RequestBody Map<String, String> payload,
                                           HttpServletRequest request) {
        try {
            String bookingId = payload.get("bookingId");
            String paymentId = payload.get("paymentId");
            String orderId = payload.get("orderId");
            String signature = payload.get("signature");
            String fallbackPhone = payload.get("phone");

            String renterPhone = authHelper.resolveAuthenticatedPhone(request, fallbackPhone);
            TractorBooking confirmed = bookingService.verifyAndConfirmPayment(
                    bookingId, paymentId, orderId, signature, renterPhone);
            return ResponseEntity.ok(confirmed);
        } catch (IllegalStateException ise) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", ise.getMessage()));
        } catch (SecurityException se) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", se.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/bookings")
    public ResponseEntity<?> getFarmerBookings(@RequestParam(required = false) String phone,
                                               HttpServletRequest request) {
        try {
            String candidatePhone = (phone != null && !phone.trim().isEmpty()) ? phone.trim() : null;
            String farmerPhone = candidatePhone;
            if (farmerPhone == null && authHelper != null) {
                try {
                    farmerPhone = authHelper.resolveAuthenticatedPhone(request, candidatePhone);
                } catch (Exception ignored) {}
            }
            if (farmerPhone == null || farmerPhone.trim().isEmpty()) {
                farmerPhone = "9876543210";
            }
            List<TractorBooking> bookings = bookingService.getBookingsForRenter(farmerPhone);
            return ResponseEntity.ok(bookings);
        } catch (SecurityException se) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", se.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/owner-bookings")
    public ResponseEntity<?> getOwnerBookings(@RequestParam(required = false) String phone,
                                              HttpServletRequest request) {
        try {
            String ownerPhone = authHelper.resolveAuthenticatedPhone(request, phone);
            List<TractorBooking> bookings = bookingService.getBookingsForOwner(ownerPhone);
            return ResponseEntity.ok(bookings);
        } catch (SecurityException se) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", se.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/bookings/{id}/cancel")
    public ResponseEntity<?> cancelBooking(@PathVariable String id,
                                           @RequestBody(required = false) Map<String, String> body,
                                           @RequestParam(required = false) String phone,
                                           HttpServletRequest request) {
        try {
            String fallbackPhone = body != null ? body.get("phone") : phone;
            String reason = body != null ? body.getOrDefault("reason", "Cancelled by farmer") : "Cancelled by farmer";
            String renterPhone = fallbackPhone;
            if (renterPhone == null && authHelper != null) {
                try {
                    renterPhone = authHelper.resolveAuthenticatedPhone(request, fallbackPhone);
                } catch (Exception ignored) {}
            }
            if (renterPhone == null || renterPhone.trim().isEmpty()) {
                renterPhone = "9876543210";
            }

            TractorBooking cancelled = bookingService.cancelBooking(id, renterPhone, reason);
            return ResponseEntity.ok(cancelled);
        } catch (SecurityException se) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", se.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/bookings/{id}/submit-upi")
    public ResponseEntity<?> submitUpiPayment(@PathVariable String id,
                                              @RequestBody Map<String, String> payload,
                                              HttpServletRequest request) {
        try {
            String upiTxnRef = payload.get("upiTransactionRef");
            String paymentMethod = payload.getOrDefault("paymentMethod", "DIRECT_UPI");
            String fallbackPhone = payload.get("phone");
            String renterPhone = authHelper.resolveAuthenticatedPhone(request, fallbackPhone);

            TractorBooking submitted = bookingService.submitUpiPayment(id, upiTxnRef, paymentMethod, renterPhone);
            return ResponseEntity.ok(submitted);
        } catch (IllegalStateException ise) {
            return ResponseEntity.status(HttpStatus.GONE).body(Map.of("error", ise.getMessage()));
        } catch (SecurityException se) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", se.getMessage()));
        } catch (IllegalArgumentException iae) {
            return ResponseEntity.badRequest().body(Map.of("error", iae.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/bookings/{id}/verify-direct-payment")
    public ResponseEntity<?> verifyDirectPayment(@PathVariable String id,
                                                 @RequestBody Map<String, Object> payload,
                                                 HttpServletRequest request) {
        try {
            boolean approved = Boolean.TRUE.equals(payload.get("approved"));
            String notes = payload.containsKey("notes") ? String.valueOf(payload.get("notes")) : "";
            String fallbackPhone = payload.containsKey("phone") ? String.valueOf(payload.get("phone")) : null;

            String verifierPhone = authHelper.resolveAuthenticatedPhone(request, fallbackPhone);
            boolean isAdmin = false;
            try {
                authHelper.requireAdmin(request);
                isAdmin = true;
            } catch (Exception ignored) {
            }

            TractorBooking verified = bookingService.verifyDirectPayment(id, approved, notes, verifierPhone, isAdmin);
            return ResponseEntity.ok(verified);
        } catch (IllegalStateException ise) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", ise.getMessage()));
        } catch (SecurityException se) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", se.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/bookings/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable String id, @RequestParam String status) {
        return tractorRepository.findById(id).map(t -> ResponseEntity.ok(t))
                .orElse(ResponseEntity.ok().build());
    }

    @PutMapping("/{id}/approval")
    public ResponseEntity<?> approveTractor(@PathVariable String id, @RequestParam boolean approve) {
        return tractorRepository.findById(id).map(t -> {
            t.setApprovalStatus(approve ? "APPROVED" : "REJECTED");
            return ResponseEntity.ok(tractorRepository.save(t));
        }).orElse(ResponseEntity.notFound().build());
    }

    // --- Admin Endpoints (Restricted to Authorized Admins) ---

    @GetMapping("/admin/bookings/active")
    public ResponseEntity<?> getActiveBookingsAdmin(HttpServletRequest request) {
        try {
            authHelper.requireAdmin(request);
            List<TractorBooking> active = bookingService.getActiveBookingsAdmin();
            return ResponseEntity.ok(active);
        } catch (SecurityException se) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", se.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/admin/bookings/history")
    public ResponseEntity<?> getHistoricalBookingsAdmin(HttpServletRequest request) {
        try {
            authHelper.requireAdmin(request);
            List<TractorBooking> history = bookingService.getHistoricalBookingsAdmin();
            return ResponseEntity.ok(history);
        } catch (SecurityException se) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", se.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/admin/bookings")
    public ResponseEntity<?> getAllBookingsAdmin(@RequestParam(required = false) String status,
                                                HttpServletRequest request) {
        try {
            authHelper.requireAdmin(request);
            List<TractorBooking> list = bookingService.getAllBookingsAdmin(status);
            return ResponseEntity.ok(list);
        } catch (SecurityException se) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", se.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/admin/scheduler/run")
    public ResponseEntity<?> triggerLifecycleScheduler(HttpServletRequest request) {
        try {
            authHelper.requireAdmin(request);
            bookingService.processLifecycleTransitions();
            return ResponseEntity.ok(Map.of("status", "SUCCESS", "message", "Lifecycle transitions processed successfully"));
        } catch (SecurityException se) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", se.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
