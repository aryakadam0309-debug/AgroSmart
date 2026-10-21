package com.agrosmart.backend.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tractor_bookings")
public class TractorBooking {
    @Id
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
    private String status = "PENDING";
    private String createdAt;

    public TractorBooking() {}

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
    public String getBookingDate() { return bookingDate; }
    public void setBookingDate(String bookingDate) { this.bookingDate = bookingDate; }
    public int getDurationHours() { return durationHours; }
    public void setDurationHours(int durationHours) { this.durationHours = durationHours; }
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public String getFarmLocation() { return farmLocation; }
    public void setFarmLocation(String farmLocation) { this.farmLocation = farmLocation; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
