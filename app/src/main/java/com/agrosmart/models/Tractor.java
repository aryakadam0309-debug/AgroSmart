package com.agrosmart.models;

import java.io.Serializable;

public class Tractor implements Serializable {
    private String id;
    private String modelName;
    private String brand;
    private String hp;
    private double hourlyRate;
    private double dailyRate;
    private String implementsIncluded;
    private String ownerName;
    private String ownerPhone;
    private String ownerUpiId;
    private String ownerPaymentName;
    private Boolean ownerUpiVerified = false;
    private String location;
    private boolean isAvailable;
    private float rating;
    private String approvalStatus = "APPROVED";
    private String equipmentType = "Standard Tractor";
    private String village;
    private String district;
    private String state;
    private Double latitude;
    private Double longitude;
    private Double distanceKm;

    public Tractor() {}

    public Tractor(String id, String modelName, String brand, String hp,
                   double hourlyRate, double dailyRate, String implementsIncluded,
                   String ownerName, String ownerPhone, String location,
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
    public boolean isOwnerUpiVerified() { return Boolean.TRUE.equals(ownerUpiVerified); }
    public void setOwnerUpiVerified(Boolean ownerUpiVerified) { this.ownerUpiVerified = ownerUpiVerified; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }

    public float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }

    public String getApprovalStatus() { return approvalStatus; }
    public void setApprovalStatus(String approvalStatus) { this.approvalStatus = approvalStatus; }

    public String getEquipmentType() { return equipmentType; }
    public void setEquipmentType(String equipmentType) { this.equipmentType = equipmentType; }

    public String getModel() { return modelName; }
    public void setModel(String model) { this.modelName = model; }

    public int getHorsePower() {
        try {
            if (hp != null) {
                String num = hp.replaceAll("[^0-9]", "");
                if (!num.isEmpty()) return Integer.parseInt(num);
            }
        } catch (Exception ignored) {}
        return 45;
    }

    public String getImplementsList() { return implementsIncluded; }
    public void setImplementsList(String implementsList) { this.implementsIncluded = implementsList; }

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

    public Double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(Double distanceKm) { this.distanceKm = distanceKm; }

    public String getFormattedDistance() {
        if (distanceKm == null) return null;
        return String.format(java.util.Locale.US, "%.1f km away", distanceKm);
    }
}
