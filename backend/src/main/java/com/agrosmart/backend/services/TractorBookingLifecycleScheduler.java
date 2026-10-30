package com.agrosmart.backend.services;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Background Scheduler for Tractor Booking Lifecycle:
 * - Runs automatically every 30 seconds.
 * - Idempotently cleans up expired holds, promotes active rentals, and moves finished bookings to history.
 * - Preserves all audit history, payment IDs, and financial records.
 */
@Component
public class TractorBookingLifecycleScheduler {

    private final TractorBookingService bookingService;

    public TractorBookingLifecycleScheduler(TractorBookingService bookingService) {
        this.bookingService = bookingService;
    }

    @Scheduled(fixedRate = 30000)
    public void scheduleLifecycleMaintenance() {
        try {
            bookingService.processLifecycleTransitions();
        } catch (Exception e) {
            // Silently catch to avoid crashing scheduler thread
        }
    }
}
