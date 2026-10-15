package com.agrosmart.models;

import java.io.Serializable;

public class TractorBooking implements Serializable {
    private String id;
    private String tractorId;
    private String tractorModel;
    private String farmerName;
    private String farmerPhone;
    private String bookingDate;
    private int durationHours;
    private double totalAmount;
    private String farmLocation;
    private String notes;
    private String status; // "PENDING", "CONFIRMED", "COMPLETED", "CANCELLED"
    private String createdAt;

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
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public String getTractorId() { return tractorId; }
    public String getTractorModel() { return tractorModel; }
    public String getFarmerName() { return farmerName; }
    public String getFarmerPhone() { return farmerPhone; }
    public String getBookingDate() { return bookingDate; }
    public int getDurationHours() { return durationHours; }
    public double getTotalAmount() { return totalAmount; }
    public String getFarmLocation() { return farmLocation; }
    public String getNotes() { return notes; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCreatedAt() { return createdAt; }
}
