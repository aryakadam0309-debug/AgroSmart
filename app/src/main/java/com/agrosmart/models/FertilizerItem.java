package com.agrosmart.models;

import java.io.Serializable;

public class FertilizerItem implements Serializable {
    private String id;
    private String farmerPhone;
    private String name;
    private String category; // "Chemical", "Organic", "Micronutrient", "Bio-fertilizer"
    private int quantityBags;
    private double bagWeightKg;
    private double costPerBag;
    private String purchaseDate;
    private int lowStockThreshold;

    public FertilizerItem() {}

    public FertilizerItem(String id, String name, String category, int quantityBags,
                          double bagWeightKg, double costPerBag, String purchaseDate,
                          int lowStockThreshold) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.quantityBags = quantityBags;
        this.bagWeightKg = bagWeightKg;
        this.costPerBag = costPerBag;
        this.purchaseDate = purchaseDate;
        this.lowStockThreshold = lowStockThreshold;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getFarmerPhone() { return farmerPhone; }
    public void setFarmerPhone(String farmerPhone) { this.farmerPhone = farmerPhone; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public int getQuantityBags() { return quantityBags; }
    public void setQuantityBags(int quantityBags) { this.quantityBags = quantityBags; }
    public double getBagWeightKg() { return bagWeightKg; }
    public double getCostPerBag() { return costPerBag; }
    public String getPurchaseDate() { return purchaseDate; }
    public int getLowStockThreshold() { return lowStockThreshold; }
    public boolean isLowStock() { return quantityBags <= lowStockThreshold; }
}
