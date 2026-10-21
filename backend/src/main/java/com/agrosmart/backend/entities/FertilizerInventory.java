package com.agrosmart.backend.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "fertilizer_inventory")
public class FertilizerInventory {
    @Id
    private String id;

    @Column(nullable = false)
    private String farmerPhone;

    @Column(nullable = false)
    private String name;

    private String category;
    private int quantityBags = 0;
    private double bagWeightKg = 50.0;
    private double costPerBag = 0.0;
    private String purchaseDate;
    private int lowStockThreshold = 3;

    public FertilizerInventory() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFarmerPhone() { return farmerPhone; }
    public void setFarmerPhone(String farmerPhone) { this.farmerPhone = farmerPhone; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getQuantityBags() { return quantityBags; }
    public void setQuantityBags(int quantityBags) { this.quantityBags = quantityBags; }

    public double getBagWeightKg() { return bagWeightKg; }
    public void setBagWeightKg(double bagWeightKg) { this.bagWeightKg = bagWeightKg; }

    public double getCostPerBag() { return costPerBag; }
    public void setCostPerBag(double costPerBag) { this.costPerBag = costPerBag; }

    public String getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(String purchaseDate) { this.purchaseDate = purchaseDate; }

    public int getLowStockThreshold() { return lowStockThreshold; }
    public void setLowStockThreshold(int lowStockThreshold) { this.lowStockThreshold = lowStockThreshold; }
}
