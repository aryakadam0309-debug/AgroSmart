package com.agrosmart.models;

import java.io.Serializable;

public class TractorBooking implements Serializable {
    private String id;
    private String tractorId;
    private String tractorModel;
    private String farmerName;
    private String farmerPhone;
    private String ownerName;
    private String ownerPhone;
    private String ownerUpiId;
    private String ownerPaymentName;
    private String bookingDate;
    private int durationHours;
    private double totalAmount;
    private String currency = "INR";
    private String farmLocation;
    private String notes;
    private String status = "PENDING_PAYMENT"; // PENDING_PAYMENT, CONFIRMED, COMPLETED, CANCELLED, FAILED
    private String paymentOrderId;
    private String paymentId;
    private String paymentMethod;
    private String paymentStatus = "PENDING";
    private String upiTransactionRef;
    private String verifiedBy;
    private Long verifiedAtEpochMs;
    private String verificationNotes;
    private Long holdExpiresAt;
    private Long rentalStartEpochMs;
    private Long rentalEndEpochMs;
    private String createdAt;
    private String updatedAt;

    public TractorBooking() {}

    public TractorBooking(String id, String tractorId, String tractorModel,
                          String farmerName, String farmerPhone, String bookingDate,
                          int durationHours, double totalAmount, String farmLocation,
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
    }

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
