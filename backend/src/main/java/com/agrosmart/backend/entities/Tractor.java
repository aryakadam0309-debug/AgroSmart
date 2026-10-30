package com.agrosmart.backend.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "tractors")
public class Tractor {
    @Id
    @Column(length = 30)
    private String id;

    @Column(nullable = false, length = 150)
    private String modelName;

    @Column(nullable = false, length = 50)
    private String brand;

    @Column(nullable = false, length = 20)
    private String hp;

    @Column(nullable = false)
    private double hourlyRate;

    @Column(nullable = false)
    private double dailyRate;

    @Column(length = 255)
    private String implementsIncluded;

    @Column(length = 100)
    private String ownerName;

    @Column(length = 15)
    private String ownerPhone;

    @Column(length = 100)
    private String ownerUpiId;

    @Column(length = 150)
    private String ownerPaymentName;

    private Boolean ownerUpiVerified = false;

    @Column(length = 150)
    private String location;

    @Column(length = 100)
    private String village;

    @Column(length = 100)
    private String district;

    @Column(length = 100)
    private String state = "Maharashtra";

    private Double latitude;
    private Double longitude;

    private boolean isAvailable = true;

    private float rating = 4.8f;

    @Column(length = 30)
    private String approvalStatus = "APPROVED"; // APPROVED, PENDING_APPROVAL, REJECTED

    @Column(length = 50)
    private String equipmentType = "Tractor"; // Tractor, Harvester, Rotavator, Power Tiller, Drone Sprayer

    @Transient
    private Double distanceKm;

    public Tractor() {}

    public Tractor(String id, String modelName, String brand, String hp, double hourlyRate, double dailyRate,
                   String implementsIncluded, String ownerName, String ownerPhone, String location,
                   boolean isAvailable, float rating) {
        this.id = id;
        this.modelName = modelName;
        this.brand = brand;
        this.hp = hp;
        this.hourlyRate = hourlyRate;
        this.dailyRate = dailyRate;
        this.implementsIncluded = implementsIncluded;
        this.ownerName = ownerName;
        this.ownerPhone = ownerPhone;
        this.location = location;
        this.isAvailable = isAvailable;
        this.rating = rating;
        this.approvalStatus = "APPROVED";
        this.equipmentType = "Tractor";
    }

    public Tractor(String id, String modelName, String brand, String hp, double hourlyRate, double dailyRate,
                   String implementsIncluded, String ownerName, String ownerPhone, String location,
                   String village, String district, String state, Double latitude, Double longitude,
                   boolean isAvailable, float rating) {
        this(id, modelName, brand, hp, hourlyRate, dailyRate, implementsIncluded, ownerName, ownerPhone, location, isAvailable, rating);
        this.village = village;
        this.district = district;
        this.state = state;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getModelName() { return modelName; }
    public void setModelName(String modelName) { this.modelName = modelName; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public String getHp() { return hp; }
    public void setHp(String hp) { this.hp = hp; }
    public double getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(double hourlyRate) { this.hourlyRate = hourlyRate; }
    public double getDailyRate() { return dailyRate; }
    public void setDailyRate(double dailyRate) { this.dailyRate = dailyRate; }
    public String getImplementsIncluded() { return implementsIncluded; }
    public void setImplementsIncluded(String implementsIncluded) { this.implementsIncluded = implementsIncluded; }
    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
    public String getOwnerPhone() { return ownerPhone; }
    public void setOwnerPhone(String ownerPhone) { this.ownerPhone = ownerPhone; }
    public String getOwnerUpiId() { return ownerUpiId; }
    public void setOwnerUpiId(String ownerUpiId) { this.ownerUpiId = ownerUpiId; }
    public String getOwnerPaymentName() { return ownerPaymentName; }
    public void setOwnerPaymentName(String ownerPaymentName) { this.ownerPaymentName = ownerPaymentName; }
    public Boolean getOwnerUpiVerified() { return ownerUpiVerified; }
    public void setOwnerUpiVerified(Boolean ownerUpiVerified) { this.ownerUpiVerified = ownerUpiVerified; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getVillage() { return village; }
    public void setVillage(String village) { this.village = village; }
    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }
    public float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }
    public String getApprovalStatus() { return approvalStatus; }
    public void setApprovalStatus(String approvalStatus) { this.approvalStatus = approvalStatus; }
    public String getEquipmentType() { return equipmentType; }
    public void setEquipmentType(String equipmentType) { this.equipmentType = equipmentType; }

    public Double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(Double distanceKm) { this.distanceKm = distanceKm; }
}
