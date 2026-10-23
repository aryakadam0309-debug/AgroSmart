package com.agrosmart.backend.controllers;

import com.agrosmart.backend.entities.NotificationItem;
import com.agrosmart.backend.entities.Tractor;
import com.agrosmart.backend.entities.TractorBooking;
import com.agrosmart.backend.repositories.NotificationRepository;
import com.agrosmart.backend.repositories.TractorBookingRepository;
import com.agrosmart.backend.repositories.TractorRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tractors")
@CrossOrigin(origins = "*")
public class TractorController {

    private final TractorRepository tractorRepository;
    private final TractorBookingRepository bookingRepository;
    private final NotificationRepository notificationRepository;

    public TractorController(TractorRepository tractorRepository, 
                             TractorBookingRepository bookingRepository,
                             NotificationRepository notificationRepository) {
        this.tractorRepository = tractorRepository;
        this.bookingRepository = bookingRepository;
        this.notificationRepository = notificationRepository;
    }

    @GetMapping
    public List<Tractor> getAllTractors(@RequestParam(required = false) String brand) {
        if (brand != null && !brand.equalsIgnoreCase("All Brands")) {
            return tractorRepository.findByBrandIgnoreCase(brand);
        }
        return tractorRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> addTractor(@RequestBody Tractor tractor) {
        if (tractor.getId() == null || tractor.getId().trim().isEmpty()) {
            tractor.setId("TR-" + (System.currentTimeMillis() % 10000));
        }
        tractorRepository.save(tractor);
        return ResponseEntity.ok(tractor);
    }

    @PostMapping("/book")
    public ResponseEntity<?> createBooking(@RequestBody TractorBooking booking) {
        if (booking.getId() == null || booking.getId().trim().isEmpty()) {
            booking.setId("TB-" + System.currentTimeMillis());
        }
        if (booking.getCreatedAt() == null || booking.getCreatedAt().trim().isEmpty()) {
            booking.setCreatedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
        }
        if (booking.getStatus() == null || booking.getStatus().trim().isEmpty()) {
            booking.setStatus("CONFIRMED");
        }

        // Prevent overlapping bookings for the same tractor on the same date
        if (booking.getTractorId() != null && booking.getBookingDate() != null) {
            List<TractorBooking> conflicts = bookingRepository.findByTractorIdAndBookingDate(
                    booking.getTractorId(), booking.getBookingDate());
            boolean hasConflict = conflicts.stream().anyMatch(b -> 
                    !b.getId().equals(booking.getId()) &&
                    !"CANCELLED".equalsIgnoreCase(b.getStatus()) &&
                    !"REJECTED".equalsIgnoreCase(b.getStatus())
            );
            if (hasConflict) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("error", "Tractor is already booked on " + booking.getBookingDate()));
            }
        }

        TractorBooking saved = bookingRepository.save(booking);

        // Notify farmer
        try {
            NotificationItem notif = new NotificationItem();
            notif.setId("NOTIF-" + System.currentTimeMillis());
            notif.setFarmerPhone(booking.getFarmerPhone());
            notif.setTitle("🚜 Tractor Booking Confirmed");
            notif.setMessage("Booking #" + saved.getId() + " for " + saved.getTractorModel() + " on " + saved.getBookingDate() + " is confirmed.");
            notif.setTimestamp("Just now");
            notif.setType("BOOKING");
            notif.setRead(false);
            notificationRepository.save(notif);
        } catch (Exception ignored) {}

        return ResponseEntity.ok(saved);
    }

    @GetMapping("/bookings")
    public List<TractorBooking> getFarmerBookings(@RequestParam(required = false) String phone) {
        if (phone != null && !phone.trim().isEmpty()) {
            return bookingRepository.findByFarmerPhone(phone);
        }
        return bookingRepository.findAll();
    }

    @PutMapping("/bookings/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable String id, @RequestParam String status) {
        return bookingRepository.findById(id).map(b -> {
            b.setStatus(status);
            bookingRepository.save(b);

            // Notify farmer of status change
            try {
                NotificationItem notif = new NotificationItem();
                notif.setId("NOTIF-" + System.currentTimeMillis());
                notif.setFarmerPhone(b.getFarmerPhone());
                notif.setTitle("🚜 Booking Status: " + status);
                notif.setMessage("Your tractor booking #" + b.getId() + " status is now " + status + ".");
                notif.setTimestamp("Just now");
                notif.setType("BOOKING");
                notif.setRead(false);
                notificationRepository.save(notif);
            } catch (Exception ignored) {}

            return ResponseEntity.ok(b);
        }).orElse(ResponseEntity.notFound().build());
    }
}
