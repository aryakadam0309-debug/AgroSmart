package com.agrosmart.models;

import java.io.Serializable;

public class InventoryItem implements Serializable {
    private String id;
    private String farmerPhone;
    private String name;
    private String category; // FERTILIZER, SEED, PESTICIDE, OTHER
    private String variety;
    private double quantity;
    private String unit; // kg, bags, liters, quintals
    private double bagWeightKg = 50.0;
    private double minStockThreshold;
    private double costPerUnit;
    private String status = "ACTIVE"; // ACTIVE, ARCHIVED
    private String createdAt;
    private String updatedAt;

    public InventoryItem() {}

    public InventoryItem(String id, String farmerPhone, String name, String category, String variety,
                         double quantity, String unit, double bagWeightKg, double minStockThreshold,
                         double costPerUnit, String status, String createdAt, String updatedAt) {
        this.id = id;
        this.farmerPhone = farmerPhone;
        this.name = name;
        this.category = category;
        this.variety = variety;
        this.quantity = quantity;
        this.unit = unit;
        this.bagWeightKg = bagWeightKg > 0 ? bagWeightKg : 50.0;
        this.minStockThreshold = minStockThreshold;
        this.costPerUnit = costPerUnit;
        this.status = status != null ? status : "ACTIVE";
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public boolean isLowStock() {
        return this.quantity <= this.minStockThreshold;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFarmerPhone() { return farmerPhone; }
    public void setFarmerPhone(String farmerPhone) { this.farmerPhone = farmerPhone; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getVariety() { return variety; }
    public void setVariety(String variety) { this.variety = variety; }

    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public double getBagWeightKg() { return bagWeightKg; }
    public void setBagWeightKg(double bagWeightKg) { this.bagWeightKg = bagWeightKg; }

    public double getMinStockThreshold() { return minStockThreshold; }
    public void setMinStockThreshold(double minStockThreshold) { this.minStockThreshold = minStockThreshold; }

    public double getCostPerUnit() { return costPerUnit; }
    public void setCostPerUnit(double costPerUnit) { this.costPerUnit = costPerUnit; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}
