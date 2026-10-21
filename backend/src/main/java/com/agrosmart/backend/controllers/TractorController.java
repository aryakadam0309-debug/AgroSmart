package com.agrosmart.backend.controllers;

import com.agrosmart.backend.entities.Tractor;
import com.agrosmart.backend.entities.TractorBooking;
import com.agrosmart.backend.repositories.TractorBookingRepository;
import com.agrosmart.backend.repositories.TractorRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tractors")
@CrossOrigin(origins = "*")
public class TractorController {

    private final TractorRepository tractorRepository;
    private final TractorBookingRepository bookingRepository;

    public TractorController(TractorRepository tractorRepository, TractorBookingRepository bookingRepository) {
        this.tractorRepository = tractorRepository;
        this.bookingRepository = bookingRepository;
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
        bookingRepository.save(booking);
        return ResponseEntity.ok(booking);
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
            return ResponseEntity.ok(b);
        }).orElse(ResponseEntity.notFound().build());
    }
}
