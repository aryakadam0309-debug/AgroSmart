package com.agrosmart.backend.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tractor_bookings")
public class TractorBooking {
    @Id
    @Column(length = 50)
    private String id;

    @Column(nullable = false, length = 50)
    private String tractorId;

    @Column(nullable = false, length = 150)
    private String tractorModel;

    @Column(nullable = false, length = 100)
    private String farmerName;

    @Column(nullable = false, length = 20)
    private String farmerPhone;

    @Column(length = 100)
    private String ownerName;

    @Column(length = 20)
    private String ownerPhone;

    @Column(length = 100)
    private String ownerUpiId;

    @Column(length = 150)
    private String ownerPaymentName;

    @Column(nullable = false, length = 50)
    private String bookingDate; // e.g. "30 Oct 2026"

    @Column(nullable = false)
    private int durationHours;

    @Column(nullable = false)
    private double totalAmount;

    @Column(length = 10)
    private String currency = "INR";

    @Column(nullable = false, length = 255)
    private String farmLocation;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(length = 30)
    private String status = "PENDING_PAYMENT"; // PENDING_PAYMENT, CONFIRMED, CANCELLED, COMPLETED, EXPIRED, FAILED

    @Column(length = 100)
    private String paymentOrderId;

    @Column(length = 100)
    private String paymentId;

    @Column(length = 50)
    private String paymentMethod; // Razorpay (Online), Cash on Delivery

    @Column(length = 30)
    private String paymentStatus = "PENDING"; // PENDING, SUCCESS, FAILED, REFUNDED, AWAITING_VERIFICATION, VERIFIED

    @Column(length = 100)
    private String upiTransactionRef; // 12-digit UPI UTR submitted by farmer

    @Column(length = 50)
    private String verifiedBy; // Owner phone or Admin who verified the receipt

    private Long verifiedAtEpochMs;

    @Column(length = 255)
    private String verificationNotes;

    private Long holdExpiresAt; // Epoch timestamp in ms for temporary hold expiration (e.g. 10 min)

    private Long rentalStartEpochMs; // Epoch timestamp in ms for start of rental period (Asia/Kolkata)
    private Long rentalEndEpochMs;   // Epoch timestamp in ms for end of rental period (Asia/Kolkata)

    @Column(length = 50)
    private String createdAt;

    @Column(length = 50)
    private String updatedAt;

    public TractorBooking() {}

    public TractorBooking(String id, String tractorId, String tractorModel, String farmerName, String farmerPhone,
                          String bookingDate, int durationHours, double totalAmount, String farmLocation,
                          String notes, String status, String createdAt) {
        this.id = id;
        this.tractorId = tractorId;
        this.tractorModel = tractorModel;
        this.farmerName = farmerName;
        this.farmerPhone = farmerPhone;
        this.bookingDate = bookingDate;
        this.durationHours = durationHours;
        this.totalAmount = totalAmount;
        this.farmLocation = farmLocation;
        this.notes = notes;
        this.status = status != null ? status : "PENDING_PAYMENT";
        this.createdAt = createdAt;
        this.currency = "INR";
        this.paymentStatus = "PENDING";
    }

    public boolean isHoldExpired() {
        if (("PENDING_PAYMENT".equalsIgnoreCase(this.status) || "AWAITING_PAYMENT".equalsIgnoreCase(this.status)) && this.holdExpiresAt != null) {
            return System.currentTimeMillis() > this.holdExpiresAt;
        }
        return false;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTractorId() { return tractorId; }
    public void setTractorId(String tractorId) { this.tractorId = tractorId; }

    public String getTractorModel() { return tractorModel; }
    public void setTractorModel(String tractorModel) { this.tractorModel = tractorModel; }

    public String getFarmerName() { return farmerName; }
    public void setFarmerName(String farmerName) { this.farmerName = farmerName; }

    public String getFarmerPhone() { return farmerPhone; }
    public void setFarmerPhone(String farmerPhone) { this.farmerPhone = farmerPhone; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    public String getOwnerPhone() { return ownerPhone; }
    public void setOwnerPhone(String ownerPhone) { this.ownerPhone = ownerPhone; }

    public String getOwnerUpiId() { return ownerUpiId; }
    public void setOwnerUpiId(String ownerUpiId) { this.ownerUpiId = ownerUpiId; }

    public String getOwnerPaymentName() { return ownerPaymentName; }
    public void setOwnerPaymentName(String ownerPaymentName) { this.ownerPaymentName = ownerPaymentName; }

    public String getUpiTransactionRef() { return upiTransactionRef; }
    public void setUpiTransactionRef(String upiTransactionRef) { this.upiTransactionRef = upiTransactionRef; }

    public String getVerifiedBy() { return verifiedBy; }
    public void setVerifiedBy(String verifiedBy) { this.verifiedBy = verifiedBy; }

    public Long getVerifiedAtEpochMs() { return verifiedAtEpochMs; }
    public void setVerifiedAtEpochMs(Long verifiedAtEpochMs) { this.verifiedAtEpochMs = verifiedAtEpochMs; }

    public String getVerificationNotes() { return verificationNotes; }
    public void setVerificationNotes(String verificationNotes) { this.verificationNotes = verificationNotes; }

    public String getBookingDate() { return bookingDate; }
    public void setBookingDate(String bookingDate) { this.bookingDate = bookingDate; }

    public int getDurationHours() { return durationHours; }
    public void setDurationHours(int durationHours) { this.durationHours = durationHours; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getFarmLocation() { return farmLocation; }
    public void setFarmLocation(String farmLocation) { this.farmLocation = farmLocation; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPaymentOrderId() { return paymentOrderId; }
    public void setPaymentOrderId(String paymentOrderId) { this.paymentOrderId = paymentOrderId; }

    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public Long getHoldExpiresAt() { return holdExpiresAt; }
    public void setHoldExpiresAt(Long holdExpiresAt) { this.holdExpiresAt = holdExpiresAt; }

    public Long getRentalStartEpochMs() { return rentalStartEpochMs; }
    public void setRentalStartEpochMs(Long rentalStartEpochMs) { this.rentalStartEpochMs = rentalStartEpochMs; }

    public Long getRentalEndEpochMs() { return rentalEndEpochMs; }
    public void setRentalEndEpochMs(Long rentalEndEpochMs) { this.rentalEndEpochMs = rentalEndEpochMs; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}
