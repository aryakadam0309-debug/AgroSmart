package com.agrosmart.backend.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tractors")
public class Tractor {
    @Id
    private String id;
    private String modelName;
    private String brand;
    private String hp;
    private double hourlyRate;
    private double dailyRate;
    private String implementsIncluded;
    private String ownerName;
    private String ownerPhone;
    private String location;
    private boolean isAvailable = true;
    private float rating = 4.8f;

    public Tractor() {}

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
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }
    public float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }
}
