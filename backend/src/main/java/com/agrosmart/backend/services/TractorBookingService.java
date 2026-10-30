package com.agrosmart.backend.services;

import com.agrosmart.backend.entities.NotificationItem;
import com.agrosmart.backend.entities.Tractor;
import com.agrosmart.backend.entities.TractorBooking;
import com.agrosmart.backend.exceptions.BookingConflictException;
import com.agrosmart.backend.repositories.NotificationRepository;
import com.agrosmart.backend.repositories.TractorBookingRepository;
import com.agrosmart.backend.repositories.TractorRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class TractorBookingService {

    private final TractorRepository tractorRepository;
    private final TractorBookingRepository bookingRepository;
    private final NotificationRepository notificationRepository;

    @Value("${app.razorpay.key-id:rzp_test_AgroSmartDemo123}")
    private String razorpayKeyId;

    @Value("${app.razorpay.key-secret:AgroSmartRazorpaySecret2026TestKey}")
    private String razorpayKeySecret;

    @Value("${app.tractor.hold-duration-minutes:10}")
    private int holdDurationMinutes;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");
    private static final ZoneId IST_ZONE = ZoneId.of("Asia/Kolkata");
    public static final String RAZORPAY_TEST_KEY = "rzp_test_AgroSmartDemo123";

    public TractorBookingService(TractorRepository tractorRepository,
                                 TractorBookingRepository bookingRepository,
                                 NotificationRepository notificationRepository) {
        this.tractorRepository = tractorRepository;
        this.bookingRepository = bookingRepository;
        this.notificationRepository = notificationRepository;
    }

    public List<Tractor> getApprovedTractors(String brand) {
        if (brand != null && !brand.trim().isEmpty() && !brand.equalsIgnoreCase("All Brands") && !brand.equalsIgnoreCase("All")) {
            return tractorRepository.findByBrandIgnoreCase(brand.trim());
        }
        return tractorRepository.findAll();
    }

    /**
     * Calculates start and end timestamps in milliseconds for the rental period in Indian Standard Time (IST).
     */
    public static long[] calculateRentalTimeRange(String bookingDate, int durationHours) {
        long nowMs = System.currentTimeMillis();
        long startMs = nowMs;
        try {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.US);
            LocalDate date = LocalDate.parse(bookingDate.trim(), fmt);
            LocalDate today = LocalDate.now(IST_ZONE);

            if (date.isEqual(today)) {
                startMs = nowMs;
            } else {
                startMs = date.atTime(8, 0).atZone(IST_ZONE).toInstant().toEpochMilli();
            }
        } catch (Exception e) {
            startMs = nowMs;
        }
        long endMs = startMs + (Math.max(1, durationHours) * 3600 * 1000L);
        return new long[]{startMs, endMs};
    }

    /**
     * Area-Wise Nearby Tractor Search (Default 20 km radius)
     * Calculates precise straight-line geographic distance using Haversine formula.
     * Excludes tractors exceeding radius, unapproved, inactive, or having overlapping bookings (CONFIRMED, ACTIVE, or unexpired PENDING_PAYMENT hold).
     * Returns matching tractors sorted from nearest to farthest.
     */
    public List<Tractor> searchNearbyTractors(double targetLat, double targetLon, Double radiusKm, String bookingDate, String brand) {
        if (targetLat < -90.0 || targetLat > 90.0 || targetLon < -180.0 || targetLon > 180.0) {
            throw new IllegalArgumentException("Invalid geographic coordinates: Latitude [-90, 90], Longitude [-180, 180].");
        }

        final boolean isAllMaharashtra = (radiusKm == null) || (radiusKm > 500.0);
        final double searchRadius = isAllMaharashtra ? 10000.0 : radiusKm;
        List<Tractor> allTractors = tractorRepository.findAll();
        List<Tractor> matchingTractors = new java.util.ArrayList<>();
        long nowMs = System.currentTimeMillis();

        long reqStart = nowMs;
        long reqEnd = nowMs + (8 * 3600 * 1000L);
        if (bookingDate != null && !bookingDate.trim().isEmpty()) {
            long[] range = calculateRentalTimeRange(bookingDate, 8);
            reqStart = range[0];
            reqEnd = range[1];
        }

        for (Tractor t : allTractors) {
            if (t.getLatitude() == null || t.getLongitude() == null) {
                continue;
            }

            if (!t.isAvailable() || !"APPROVED".equalsIgnoreCase(t.getApprovalStatus())) {
                continue;
            }

            if (brand != null && !brand.trim().isEmpty() && !brand.equalsIgnoreCase("All Brands") && !brand.equalsIgnoreCase("All")) {
                if (!brand.trim().equalsIgnoreCase(t.getBrand())) {
                    continue;
                }
            }

            // Exclude booked / active / reserved machines for overlapping rental dates
            if (bookingDate != null && !bookingDate.trim().isEmpty()) {
                List<TractorBooking> conflicts = bookingRepository.findConflictingBookings(
                        t.getId(), bookingDate.trim(), reqStart, reqEnd, nowMs);
                boolean hasActiveConflict = conflicts.stream().anyMatch(b ->
                        "CONFIRMED".equalsIgnoreCase(b.getStatus()) ||
                        "ACTIVE".equalsIgnoreCase(b.getStatus()) ||
                        ("PENDING_PAYMENT".equalsIgnoreCase(b.getStatus()) && b.getHoldExpiresAt() != null && b.getHoldExpiresAt() > nowMs)
                );
                if (hasActiveConflict) {
                    continue; // Exclude tractor from available search results
                }
            }

            double distance = calculateHaversineDistanceKm(targetLat, targetLon, t.getLatitude(), t.getLongitude());
            if (isAllMaharashtra || distance <= searchRadius) {
                t.setDistanceKm(Math.round(distance * 10.0) / 10.0);

                // Privacy: Mask owner private contact on public search results
                if (t.getOwnerPhone() != null && t.getOwnerPhone().length() >= 7) {
                    t.setOwnerPhone(t.getOwnerPhone().substring(0, 4) + "***" +
                            t.getOwnerPhone().substring(t.getOwnerPhone().length() - 3));
                }

                matchingTractors.add(t);
            }
        }

        matchingTractors.sort(java.util.Comparator.comparingDouble(Tractor::getDistanceKm));
        return matchingTractors;
    }

    public static double calculateHaversineDistanceKm(double lat1, double lon1, double lat2, double lon2) {
        final double EARTH_RADIUS_KM = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c;
    }

    public List<TractorBooking> getBookingsForRenter(String farmerPhone) {
        if (farmerPhone == null || farmerPhone.trim().isEmpty()) {
            return bookingRepository.findAll();
        }
        String trimmed = farmerPhone.trim();
        List<TractorBooking> exact = bookingRepository.findByFarmerPhone(trimmed);
        if (!exact.isEmpty()) {
            return exact;
        }

        String digitsOnly = trimmed.replaceAll("[^0-9]", "");
        String last10 = digitsOnly.length() >= 10 ? digitsOnly.substring(digitsOnly.length() - 10) : digitsOnly;

        if (!last10.isEmpty()) {
            List<TractorBooking> withPlus = bookingRepository.findByFarmerPhone("+91" + last10);
            if (!withPlus.isEmpty()) return withPlus;

            List<TractorBooking> raw10 = bookingRepository.findByFarmerPhone(last10);
            if (!raw10.isEmpty()) return raw10;

            List<TractorBooking> tailMatch = bookingRepository.findAll().stream()
                    .filter(b -> b.getFarmerPhone() != null &&
                            b.getFarmerPhone().replaceAll("[^0-9]", "").endsWith(last10))
                    .collect(java.util.stream.Collectors.toList());
            if (!tailMatch.isEmpty()) return tailMatch;
        }

        return exact;
    }

    public List<TractorBooking> getBookingsForOwner(String ownerPhone) {
        return bookingRepository.findByOwnerPhone(ownerPhone.trim());
    }

    /**
     * Confirm-Only Direct Reservation:
     * 1. Acquires pessimistic write lock on the Tractor row to prevent race conditions.
     * 2. Checks overlapping confirmed bookings, active rentals, and unexpired holds.
     * 3. Atomically creates and confirms the reservation directly with zero in-app payment.
     * 4. Rental rates and payment settlements are handled directly between farmer and tractor owner/driver.
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public TractorBooking confirmDirectBooking(TractorBooking request, String renterPhone, String renterName) {
        if (request.getTractorId() == null || request.getTractorId().trim().isEmpty()) {
            throw new IllegalArgumentException("Tractor ID is required.");
        }
        if (request.getBookingDate() == null || request.getBookingDate().trim().isEmpty()) {
            throw new IllegalArgumentException("Booking date is required.");
        }
        if (request.getFarmLocation() == null || request.getFarmLocation().trim().isEmpty()) {
            throw new IllegalArgumentException("Farm location / survey number is required.");
        }

        // Concurrency Control: Lock tractor row in database
        Tractor tractor = tractorRepository.findByIdForUpdate(request.getTractorId())
                .orElseThrow(() -> new IllegalArgumentException("Tractor not found: " + request.getTractorId()));

        if (!tractor.isAvailable()) {
            throw new IllegalStateException("Tractor is currently inactive or marked unavailable by owner.");
        }
        if ("REJECTED".equalsIgnoreCase(tractor.getApprovalStatus())) {
            throw new IllegalStateException("Tractor listing has been rejected by administration.");
        }

        int durationHours = Math.max(1, request.getDurationHours());
        long[] timeRange = calculateRentalTimeRange(request.getBookingDate(), durationHours);
        long startMs = timeRange[0];
        long endMs = timeRange[1];

        // Check for conflicting confirmed bookings, active rentals, or unexpired holds
        long nowMs = System.currentTimeMillis();
        List<TractorBooking> conflicts = bookingRepository.findConflictingBookings(
                tractor.getId(), request.getBookingDate().trim(), startMs, endMs, nowMs);

        boolean hasConflict = conflicts.stream().anyMatch(b ->
                !b.getId().equals(request.getId()) &&
                ("CONFIRMED".equalsIgnoreCase(b.getStatus()) ||
                 "ACTIVE".equalsIgnoreCase(b.getStatus()) ||
                 "AWAITING_VERIFICATION".equalsIgnoreCase(b.getStatus()) ||
                 (("PENDING_PAYMENT".equalsIgnoreCase(b.getStatus()) || "AWAITING_PAYMENT".equalsIgnoreCase(b.getStatus()))
                         && b.getHoldExpiresAt() != null && b.getHoldExpiresAt() > nowMs))
        );

        if (hasConflict) {
            throw new BookingConflictException("Sorry, this tractor has just been booked for your selected dates. Please refresh availability or choose another tractor or date range.");
        }

        double calculatedTotal = durationHours * tractor.getHourlyRate();

        String bookingId = (request.getId() != null && !request.getId().trim().isEmpty()) ?
                request.getId() : "TB-" + System.currentTimeMillis();

        String nowStr = LocalDateTime.now().format(DATE_FMT);

        TractorBooking booking = new TractorBooking();
        booking.setId(bookingId);
        booking.setTractorId(tractor.getId());
        booking.setTractorModel(tractor.getModelName());
        booking.setFarmerName(renterName != null ? renterName : "Farmer");
        booking.setFarmerPhone(renterPhone.trim());
        booking.setOwnerName(tractor.getOwnerName());
        booking.setOwnerPhone(tractor.getOwnerPhone()); // Verified driver/owner phone
        booking.setOwnerUpiId(tractor.getOwnerUpiId());
        booking.setOwnerPaymentName(tractor.getOwnerPaymentName() != null ? tractor.getOwnerPaymentName() : tractor.getOwnerName());
        booking.setBookingDate(request.getBookingDate().trim());
        booking.setDurationHours(durationHours);
        booking.setTotalAmount(calculatedTotal);
        booking.setCurrency("INR");
        booking.setFarmLocation(request.getFarmLocation().trim());
        booking.setNotes(request.getNotes());
        booking.setStatus("CONFIRMED");
        booking.setPaymentMethod("DIRECT_WITH_OWNER");
        booking.setPaymentStatus("DIRECT_WITH_OWNER");
        booking.setRentalStartEpochMs(startMs);
        booking.setRentalEndEpochMs(endMs);
        booking.setCreatedAt(nowStr);
        booking.setUpdatedAt(nowStr);

        TractorBooking saved = bookingRepository.save(booking);

        // Notify Renter
        try {
            NotificationItem notif = new NotificationItem();
            notif.setId("NOTIF-BK-FARMER-" + System.currentTimeMillis());
            notif.setFarmerPhone(saved.getFarmerPhone());
            notif.setTitle("🚜 Tractor Reservation Confirmed (#" + saved.getId() + ")");
            notif.setMessage("Your reservation for " + saved.getTractorModel() + " on " +
                    saved.getBookingDate() + " (" + saved.getDurationHours() + " hrs, ₹" +
                    Math.round(saved.getTotalAmount()) + ") is confirmed. Please coordinate directly with driver " +
                    saved.getOwnerName() + " (" + saved.getOwnerPhone() + ").");
            notif.setTimestamp("Just now");
            notif.setType("BOOKING");
            notif.setRead(false);
            notificationRepository.save(notif);
        } catch (Exception ignored) {}

        // Notify Equipment Owner / Driver
        try {
            if (saved.getOwnerPhone() != null && !saved.getOwnerPhone().isEmpty()) {
                NotificationItem ownerNotif = new NotificationItem();
                ownerNotif.setId("NOTIF-BK-OWNER-" + System.currentTimeMillis());
                ownerNotif.setFarmerPhone(saved.getOwnerPhone());
                ownerNotif.setTitle("🚜 New Tractor Reservation for Your " + saved.getTractorModel());
                ownerNotif.setMessage("Farmer " + saved.getFarmerName() + " (" + saved.getFarmerPhone() +
                        ") reserved your tractor for " + saved.getBookingDate() + " at " + saved.getFarmLocation() + ".");
                ownerNotif.setTimestamp("Just now");
                ownerNotif.setType("BOOKING");
                ownerNotif.setRead(false);
                notificationRepository.save(ownerNotif);
            }
        } catch (Exception ignored) {}

        return saved;
    }

    /**
     * Race-Safe Booking:
     * 1. Acquires pessimistic write lock on the Tractor row.
     * 2. Checks overlapping confirmed bookings, active rentals, and unexpired holds.
     * 3. Creates temporary hold (10 mins) and commits before payment checkout.
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public TractorBooking holdAndCreateBooking(TractorBooking request, String renterPhone, String renterName) {
        if (request.getTractorId() == null || request.getTractorId().trim().isEmpty()) {
            throw new IllegalArgumentException("Tractor ID is required.");
        }
        if (request.getBookingDate() == null || request.getBookingDate().trim().isEmpty()) {
            throw new IllegalArgumentException("Booking date is required.");
        }
        if (request.getFarmLocation() == null || request.getFarmLocation().trim().isEmpty()) {
            throw new IllegalArgumentException("Farm location / survey number is required.");
        }

        // Concurrency Control: Lock tractor row in database
        Tractor tractor = tractorRepository.findByIdForUpdate(request.getTractorId())
                .orElseThrow(() -> new IllegalArgumentException("Tractor not found: " + request.getTractorId()));

        if (!tractor.isAvailable()) {
            throw new IllegalStateException("Tractor is currently inactive or marked unavailable by owner.");
        }
        if ("REJECTED".equalsIgnoreCase(tractor.getApprovalStatus())) {
            throw new IllegalStateException("Tractor listing has been rejected by administration.");
        }

        int durationHours = Math.max(1, request.getDurationHours());
        long[] timeRange = calculateRentalTimeRange(request.getBookingDate(), durationHours);
        long startMs = timeRange[0];
        long endMs = timeRange[1];

        // Check for conflicting confirmed bookings, active rentals, or unexpired holds
        long nowMs = System.currentTimeMillis();
        List<TractorBooking> conflicts = bookingRepository.findConflictingBookings(
                tractor.getId(), request.getBookingDate().trim(), startMs, endMs, nowMs);

        boolean hasConflict = conflicts.stream().anyMatch(b ->
                !b.getId().equals(request.getId()) &&
                ("CONFIRMED".equalsIgnoreCase(b.getStatus()) ||
                 "ACTIVE".equalsIgnoreCase(b.getStatus()) ||
                 ("PENDING_PAYMENT".equalsIgnoreCase(b.getStatus()) && b.getHoldExpiresAt() != null && b.getHoldExpiresAt() > nowMs))
        );

        if (hasConflict) {
            throw new BookingConflictException("Tractor is already reserved or booked for " +
                    request.getBookingDate() + ". Please select another date or machine.");
        }

        double calculatedTotal = durationHours * tractor.getHourlyRate();

        String bookingId = (request.getId() != null && !request.getId().trim().isEmpty()) ?
                request.getId() : "TB-" + System.currentTimeMillis();

        String nowStr = LocalDateTime.now().format(DATE_FMT);
        long holdDurationMs = holdDurationMinutes * 60 * 1000L;

        TractorBooking booking = new TractorBooking();
        booking.setId(bookingId);
        booking.setTractorId(tractor.getId());
        booking.setTractorModel(tractor.getModelName());
        booking.setFarmerName(renterName != null ? renterName : "Farmer");
        booking.setFarmerPhone(renterPhone.trim());
        booking.setOwnerName(tractor.getOwnerName());
        booking.setOwnerPhone(tractor.getOwnerPhone()); // Trusted verified owner/driver phone preserved for confirmed renter
        booking.setOwnerUpiId(tractor.getOwnerUpiId());
        booking.setOwnerPaymentName(tractor.getOwnerPaymentName() != null ? tractor.getOwnerPaymentName() : tractor.getOwnerName());
        booking.setBookingDate(request.getBookingDate().trim());
        booking.setDurationHours(durationHours);
        booking.setTotalAmount(calculatedTotal);
        booking.setCurrency("INR");
        booking.setFarmLocation(request.getFarmLocation().trim());
        booking.setNotes(request.getNotes());
        booking.setStatus("PENDING_PAYMENT");
        booking.setPaymentOrderId("order_TRK_" + bookingId);
        booking.setPaymentStatus("AWAITING_PAYMENT");
        booking.setHoldExpiresAt(nowMs + holdDurationMs); // Configurable hold duration (default 10 minutes)
        booking.setRentalStartEpochMs(startMs);
        booking.setRentalEndEpochMs(endMs);
        booking.setCreatedAt(nowStr);
        booking.setUpdatedAt(nowStr);

        return bookingRepository.save(booking);
    }

    public Map<String, Object> createPaymentOrder(String bookingId, String renterPhone) {
        TractorBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + bookingId));

        if (!booking.getFarmerPhone().equalsIgnoreCase(renterPhone.trim())) {
            throw new SecurityException("Unauthorized: Booking belongs to another farmer.");
        }

        if (booking.isHoldExpired()) {
            booking.setStatus("EXPIRED");
            booking.setPaymentStatus("EXPIRED");
            bookingRepository.save(booking);
            throw new IllegalStateException("Your " + holdDurationMinutes + "-minute reservation hold on this tractor has expired. Please rebook.");
        }

        Map<String, Object> orderDetails = new HashMap<>();
        orderDetails.put("bookingId", booking.getId());
        orderDetails.put("orderId", booking.getPaymentOrderId());
        orderDetails.put("totalAmount", booking.getTotalAmount());
        orderDetails.put("amountPaise", Math.round(booking.getTotalAmount() * 100));
        orderDetails.put("currency", "INR");
        orderDetails.put("keyId", razorpayKeyId); // Key ID preserved for marketplace compatibility
        orderDetails.put("tractorModel", booking.getTractorModel());
        orderDetails.put("bookingDate", booking.getBookingDate());
        orderDetails.put("farmerName", booking.getFarmerName());
        orderDetails.put("farmerPhone", booking.getFarmerPhone());
        orderDetails.put("ownerName", booking.getOwnerName());
        orderDetails.put("ownerPhone", booking.getOwnerPhone());
        orderDetails.put("ownerUpiId", booking.getOwnerUpiId());
        orderDetails.put("ownerPaymentName", booking.getOwnerPaymentName());
        orderDetails.put("holdExpiresAt", booking.getHoldExpiresAt());
        orderDetails.put("status", booking.getStatus());
        return orderDetails;
    }

    /**
     * Direct UPI Payment: Renter submits UPI transaction reference (UTR) from GPay, PhonePe, etc.
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public TractorBooking submitUpiPayment(String bookingId, String upiTxnRef, String paymentMethod, String renterPhone) {
        TractorBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + bookingId));

        if (!booking.getFarmerPhone().equalsIgnoreCase(renterPhone.trim())) {
            throw new SecurityException("Unauthorized: Booking belongs to another farmer.");
        }

        if (booking.isHoldExpired()) {
            booking.setStatus("EXPIRED");
            booking.setPaymentStatus("EXPIRED");
            bookingRepository.save(booking);
            throw new IllegalStateException("Your reservation hold has expired. Please rebook.");
        }

        if (upiTxnRef == null || upiTxnRef.trim().isEmpty()) {
            throw new IllegalArgumentException("UPI Transaction Reference / UTR number is required for verification.");
        }

        booking.setUpiTransactionRef(upiTxnRef.trim());
        booking.setPaymentMethod(paymentMethod != null && !paymentMethod.trim().isEmpty() ? paymentMethod.trim() : "DIRECT_UPI");
        booking.setStatus("AWAITING_VERIFICATION");
        booking.setPaymentStatus("PAYMENT_SUBMITTED");
        booking.setUpdatedAt(LocalDateTime.now().format(DATE_FMT));
        return bookingRepository.save(booking);
    }

    /**
     * Direct UPI Payment Verification: Equipment owner or administrator verifies receipt against bank/UPI app.
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public TractorBooking verifyDirectPayment(String bookingId, boolean approved, String notes, String verifierPhone, boolean isAdmin) {
        TractorBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + bookingId));

        boolean isOwner = booking.getOwnerPhone() != null && booking.getOwnerPhone().equalsIgnoreCase(verifierPhone.trim());
        if (!isOwner && !isAdmin) {
            throw new SecurityException("Unauthorized: Only the equipment owner or an administrator can verify payments.");
        }

        long nowMs = System.currentTimeMillis();
        String nowStr = LocalDateTime.now().format(DATE_FMT);

        if (approved) {
            // Pessimistic write lock tractor to verify availability inside the transaction
            Tractor tractor = tractorRepository.findByIdForUpdate(booking.getTractorId())
                    .orElseThrow(() -> new IllegalArgumentException("Tractor not found: " + booking.getTractorId()));

            List<TractorBooking> conflicts = bookingRepository.findConflictingBookings(
                    booking.getTractorId(), booking.getBookingDate(),
                    booking.getRentalStartEpochMs(), booking.getRentalEndEpochMs(), nowMs);

            boolean slotTakenByOther = conflicts.stream().anyMatch(b ->
                    !b.getId().equals(booking.getId()) &&
                    ("CONFIRMED".equalsIgnoreCase(b.getStatus()) || "ACTIVE".equalsIgnoreCase(b.getStatus())));

            if (slotTakenByOther) {
                booking.setStatus("CONFLICT_REJECTED");
                booking.setPaymentStatus("CONFLICT_SLOT_LOST");
                booking.setVerifiedBy(verifierPhone);
                booking.setVerifiedAtEpochMs(nowMs);
                bookingRepository.save(booking);
                throw new IllegalStateException("Slot was secured by another confirmed booking during verification window.");
            }

            booking.setStatus("CONFIRMED");
            booking.setPaymentStatus("VERIFIED");
            booking.setVerifiedBy(verifierPhone);
            booking.setVerifiedAtEpochMs(nowMs);
            booking.setVerificationNotes(notes != null && !notes.trim().isEmpty() ? notes.trim() : "Direct payment verified by owner/admin");
            booking.setUpdatedAt(nowStr);
        } else {
            // Rejected by owner - payment not received
            booking.setStatus("CANCELLED");
            booking.setPaymentStatus("REJECTED");
            booking.setVerifiedBy(verifierPhone);
            booking.setVerifiedAtEpochMs(nowMs);
            booking.setVerificationNotes(notes != null && !notes.trim().isEmpty() ? notes.trim() : "Payment rejected: Funds not received in account");
            booking.setUpdatedAt(nowStr);
        }

        return bookingRepository.save(booking);
    }

    /**
     * Cryptographically verifies Razorpay payment signature using HMAC-SHA256 with Key Secret.
     */
    public static boolean verifyRazorpaySignature(String orderId, String paymentId, String signature, String secret) {
        if (orderId == null || paymentId == null || signature == null || secret == null) {
            return false;
        }
        try {
            String payload = orderId + "|" + paymentId;
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKey);
            byte[] rawHmac = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : rawHmac) {
                hex.append(String.format("%02x", b));
            }
            return MessageDigest.isEqual(hex.toString().getBytes(StandardCharsets.UTF_8), signature.toLowerCase().trim().getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            return false;
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public TractorBooking verifyAndConfirmPayment(String bookingId, String razorpayPaymentId,
                                                  String razorpayOrderId, String signature,
                                                  String renterPhone) {
        TractorBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + bookingId));

        if (!booking.getFarmerPhone().equalsIgnoreCase(renterPhone.trim())) {
            throw new SecurityException("Unauthorized: Booking belongs to another farmer.");
        }

        // Idempotency: if already confirmed with this transaction, return safely
        if ("CONFIRMED".equalsIgnoreCase(booking.getStatus()) && razorpayPaymentId.equals(booking.getPaymentId())) {
            return booking;
        }

        if (razorpayPaymentId == null || razorpayPaymentId.trim().isEmpty()) {
            booking.setStatus("FAILED");
            booking.setPaymentStatus("FAILED");
            bookingRepository.save(booking);
            throw new IllegalArgumentException("Invalid payment: Missing transaction reference.");
        }

        // Row lock tractor to ensure safe concurrency during confirmation
        Tractor tractor = tractorRepository.findByIdForUpdate(booking.getTractorId())
                .orElseThrow(() -> new IllegalArgumentException("Tractor not found: " + booking.getTractorId()));

        // Delayed callback check: if hold expired, verify slot has not been taken by another confirmed booking
        long nowMs = System.currentTimeMillis();
        if (booking.isHoldExpired()) {
            List<TractorBooking> conflicts = bookingRepository.findConflictingBookings(
                    booking.getTractorId(), booking.getBookingDate(),
                    booking.getRentalStartEpochMs(), booking.getRentalEndEpochMs(), nowMs);
            boolean slotTakenByOther = conflicts.stream().anyMatch(b ->
                    !b.getId().equals(booking.getId()) &&
                    ("CONFIRMED".equalsIgnoreCase(b.getStatus()) || "ACTIVE".equalsIgnoreCase(b.getStatus())));
            if (slotTakenByOther) {
                booking.setStatus("FAILED");
                booking.setPaymentStatus("REFUND_REQUIRED");
                bookingRepository.save(booking);
                throw new IllegalStateException("Hold expired and slot was secured by another farmer. Payment marked for automatic refund.");
            }
        }

        // Support Pay on Service (Cash on Delivery) vs Online Razorpay Payment
        boolean isCod = "COD_SIG".equalsIgnoreCase(signature)
                || (razorpayPaymentId != null && (razorpayPaymentId.equalsIgnoreCase("COD")
                || razorpayPaymentId.startsWith("COD-")
                || razorpayPaymentId.equalsIgnoreCase("CASH_ON_DELIVERY")));

        if (isCod) {
            booking.setPaymentMethod("Pay on Service (Cash on Delivery)");
            booking.setPaymentStatus("PENDING_CASH_COLLECTION");
        } else {
            // Cryptographic HMAC SHA-256 signature verification for online Razorpay payments
            boolean isValidSig = false;
            if (signature != null && !signature.trim().isEmpty() && !"sig_rzp".equals(signature)) {
                String effectiveOrderId = (razorpayOrderId != null && !razorpayOrderId.trim().isEmpty())
                        ? razorpayOrderId : booking.getPaymentOrderId();
                isValidSig = verifyRazorpaySignature(effectiveOrderId, razorpayPaymentId, signature, razorpayKeySecret);
            }

            // Allow valid cryptographic HMAC signature OR Razorpay direct test checkout (where no server order was signed)
            boolean isTestPass = isValidSig
                    || (razorpayKeyId != null && razorpayKeyId.startsWith("rzp_test_") && (
                            "sig_rzp".equals(signature)
                            || signature == null
                            || signature.trim().isEmpty()
                       ));

            if (!isTestPass) {
                booking.setStatus("FAILED");
                booking.setPaymentStatus("FAILED");
                bookingRepository.save(booking);
                throw new IllegalArgumentException("Payment verification failed: Invalid HMAC-SHA256 signature.");
            }

            booking.setPaymentMethod("Razorpay Online (UPI/Card)");
            booking.setPaymentStatus("SUCCESS");
        }

        String nowStr = LocalDateTime.now().format(DATE_FMT);
        booking.setStatus("CONFIRMED");
        booking.setPaymentId(razorpayPaymentId);
        booking.setPaymentOrderId(razorpayOrderId != null ? razorpayOrderId : booking.getPaymentOrderId());
        booking.setUpdatedAt(nowStr);

        TractorBooking confirmed = bookingRepository.save(booking);

        // Notify Renter
        try {
            NotificationItem notif = new NotificationItem();
            notif.setId("NOTIF-BK-FARMER-" + System.currentTimeMillis());
            notif.setFarmerPhone(confirmed.getFarmerPhone());
            notif.setTitle("🚜 Tractor Booking Confirmed (#" + confirmed.getId() + ")");
            notif.setMessage("Your booking for " + confirmed.getTractorModel() + " on " +
                    confirmed.getBookingDate() + " (" + confirmed.getDurationHours() + " hrs, ₹" +
                    Math.round(confirmed.getTotalAmount()) + ") is confirmed via " + confirmed.getPaymentMethod() + ".");
            notif.setTimestamp("Just now");
            notif.setType("BOOKING");
            notif.setRead(false);
            notificationRepository.save(notif);
        } catch (Exception ignored) {}

        // Notify Equipment Owner
        try {
            if (confirmed.getOwnerPhone() != null && !confirmed.getOwnerPhone().isEmpty()) {
                NotificationItem ownerNotif = new NotificationItem();
                ownerNotif.setId("NOTIF-BK-OWNER-" + System.currentTimeMillis());
                ownerNotif.setFarmerPhone(confirmed.getOwnerPhone());
                ownerNotif.setTitle("🚜 New Booking for Your " + confirmed.getTractorModel());
                ownerNotif.setMessage("Farmer " + confirmed.getFarmerName() + " (" + confirmed.getFarmerPhone() +
                        ") booked your tractor for " + confirmed.getBookingDate() + " at " + confirmed.getFarmLocation() + ".");
                ownerNotif.setTimestamp("Just now");
                ownerNotif.setType("BOOKING");
                ownerNotif.setRead(false);
                notificationRepository.save(ownerNotif);
            }
        } catch (Exception ignored) {}

        return confirmed;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public TractorBooking cancelBooking(String bookingId, String callerPhone, String reason) {
        TractorBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + bookingId));

        boolean isFarmer = booking.getFarmerPhone() != null && booking.getFarmerPhone().equalsIgnoreCase(callerPhone.trim());
        boolean isOwner = booking.getOwnerPhone() != null && booking.getOwnerPhone().equalsIgnoreCase(callerPhone.trim());

        if (!isFarmer && !isOwner) {
            throw new SecurityException("Unauthorized: Caller is neither the booking farmer nor the equipment owner.");
        }

        String cancellationStatus = isFarmer ? "CANCELLED_BY_FARMER" : "CANCELLED_BY_OWNER";
        booking.setStatus(cancellationStatus);
        String auditReason = "Cancelled by " + (isFarmer ? "farmer (" : "owner (") + callerPhone.trim() + "): " + reason;
        booking.setNotes(booking.getNotes() != null ? booking.getNotes() + " | " + auditReason : auditReason);
        booking.setUpdatedAt(LocalDateTime.now().format(DATE_FMT));
        return bookingRepository.save(booking);
    }

    /**
     * Automated Lifecycle Scheduler:
     * Idempotently transitions bookings:
     * 1. Expired holds (PENDING_PAYMENT with holdExpiresAt < now) -> EXPIRED.
     * 2. Active rentals (CONFIRMED within rental window) -> ACTIVE.
     * 3. Completed rentals (ACTIVE/CONFIRMED past rentalEndEpochMs) -> COMPLETED.
     * All historical data, payment records, and audit details are permanently preserved.
     */
    @Transactional
    public void processLifecycleTransitions() {
        long nowMs = System.currentTimeMillis();

        // 1. Expire uncompleted holds
        List<TractorBooking> pendingHolds = bookingRepository.findByStatusIn(List.of("PENDING_PAYMENT", "AWAITING_PAYMENT"));
        for (TractorBooking b : pendingHolds) {
            if (b.getHoldExpiresAt() != null && nowMs > b.getHoldExpiresAt()) {
                b.setStatus("EXPIRED");
                b.setPaymentStatus("EXPIRED");
                b.setUpdatedAt(LocalDateTime.now().format(DATE_FMT));
                bookingRepository.save(b);
            }
        }

        // 2. Transition CONFIRMED -> ACTIVE if within rental window
        List<TractorBooking> confirmedBookings = bookingRepository.findByStatus("CONFIRMED");
        for (TractorBooking b : confirmedBookings) {
            if (b.getRentalStartEpochMs() != null && b.getRentalEndEpochMs() != null) {
                if (nowMs >= b.getRentalStartEpochMs() && nowMs < b.getRentalEndEpochMs()) {
                    b.setStatus("ACTIVE");
                    b.setUpdatedAt(LocalDateTime.now().format(DATE_FMT));
                    bookingRepository.save(b);
                } else if (nowMs >= b.getRentalEndEpochMs()) {
                    b.setStatus("COMPLETED");
                    b.setUpdatedAt(LocalDateTime.now().format(DATE_FMT));
                    bookingRepository.save(b);
                }
            }
        }

        // 3. Transition ACTIVE -> COMPLETED once rental period is finished
        List<TractorBooking> activeBookings = bookingRepository.findByStatus("ACTIVE");
        for (TractorBooking b : activeBookings) {
            if (b.getRentalEndEpochMs() != null && nowMs >= b.getRentalEndEpochMs()) {
                b.setStatus("COMPLETED");
                b.setUpdatedAt(LocalDateTime.now().format(DATE_FMT));
                bookingRepository.save(b);
            }
        }
    }

    // --- Admin Views ---
    public List<TractorBooking> getActiveBookingsAdmin() {
        return bookingRepository.findActiveBookings();
    }

    public List<TractorBooking> getHistoricalBookingsAdmin() {
        return bookingRepository.findHistoricalBookings();
    }

    public List<TractorBooking> getAllBookingsAdmin(String status) {
        if (status != null && !status.trim().isEmpty() && !"ALL".equalsIgnoreCase(status)) {
            return bookingRepository.findByStatus(status.trim().toUpperCase());
        }
        return bookingRepository.findAllByOrderByCreatedAtDesc();
    }
}
