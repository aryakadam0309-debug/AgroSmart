package com.agrosmart.backend.repositories;

import com.agrosmart.backend.entities.TractorBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TractorBookingRepository extends JpaRepository<TractorBooking, String> {

    List<TractorBooking> findByFarmerPhone(String farmerPhone);

    List<TractorBooking> findByOwnerPhone(String ownerPhone);

    List<TractorBooking> findByStatus(String status);

    List<TractorBooking> findByStatusIn(List<String> statuses);

    List<TractorBooking> findByTractorIdAndBookingDate(String tractorId, String bookingDate);

    Optional<TractorBooking> findByPaymentOrderId(String paymentOrderId);

    List<TractorBooking> findAllByOrderByCreatedAtDesc();

    @Query("SELECT b FROM TractorBooking b WHERE b.status IN ('PENDING_PAYMENT', 'AWAITING_PAYMENT', 'AWAITING_VERIFICATION', 'CONFIRMED', 'ACTIVE') ORDER BY b.createdAt DESC")
    List<TractorBooking> findActiveBookings();

    @Query("SELECT b FROM TractorBooking b WHERE b.status IN ('COMPLETED', 'CANCELLED', 'CANCELLED_BY_FARMER', 'CANCELLED_BY_OWNER', 'EXPIRED', 'FAILED', 'REJECTED') ORDER BY b.createdAt DESC")
    List<TractorBooking> findHistoricalBookings();

    @Query("SELECT b FROM TractorBooking b WHERE b.tractorId = :tractorId " +
           "AND (:bookingDate IS NULL OR b.bookingDate = :bookingDate OR " +
           "    (b.rentalStartEpochMs IS NOT NULL AND :reqEnd IS NOT NULL AND b.rentalStartEpochMs < :reqEnd AND b.rentalEndEpochMs > :reqStart)) " +
           "AND (b.status = 'CONFIRMED' OR b.status = 'ACTIVE' OR b.status = 'AWAITING_VERIFICATION' OR ((b.status = 'PENDING_PAYMENT' OR b.status = 'AWAITING_PAYMENT') AND b.holdExpiresAt > :now))")
    List<TractorBooking> findConflictingBookings(@Param("tractorId") String tractorId,
                                                @Param("bookingDate") String bookingDate,
                                                @Param("reqStart") Long reqStart,
                                                @Param("reqEnd") Long reqEnd,
                                                @Param("now") Long now);

    default List<TractorBooking> findConflictingBookings(String tractorId, String bookingDate, Long now) {
        return findConflictingBookings(tractorId, bookingDate, null, null, now);
    }
}
