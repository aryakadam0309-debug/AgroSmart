package com.agrosmart.models;

import java.io.Serializable;

public class CropHarvest implements Serializable {
    private String id;
    private String farmerPhone;
    private String cropName;
    private String variety;
    private double harvestedQuintals;
    private double soldQuintals;
    private double stockRemainingQuintals;
    private String harvestDate;
    private String storageLocation;
    private double expectedPricePerQtl;

    public CropHarvest() {}

    public CropHarvest(String id, String cropName, String variety,
                       double harvestedQuintals, double soldQuintals,
                       String harvestDate, String storageLocation,
                       double expectedPricePerQtl) {
        this.id = id;
        this.cropName = cropName;
        this.variety = variety;
        this.harvestedQuintals = harvestedQuintals;
        this.soldQuintals = soldQuintals;
        this.stockRemainingQuintals = Math.max(0, harvestedQuintals - soldQuintals);
        this.harvestDate = harvestDate;
        this.storageLocation = storageLocation;
        this.expectedPricePerQtl = expectedPricePerQtl;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getFarmerPhone() { return farmerPhone; }
    public void setFarmerPhone(String farmerPhone) { this.farmerPhone = farmerPhone; }
    public String getCropName() { return cropName; }
    public String getVariety() { return variety; }
    public double getHarvestedQuintals() { return harvestedQuintals; }
    public double getSoldQuintals() { return soldQuintals; }
    public void setSoldQuintals(double soldQuintals) {
        this.soldQuintals = soldQuintals;
        this.stockRemainingQuintals = Math.max(0, harvestedQuintals - soldQuintals);
    }
    public double getStockRemainingQuintals() { return stockRemainingQuintals; }
    public String getHarvestDate() { return harvestDate; }
    public String getStorageLocation() { return storageLocation; }
    public double getExpectedPricePerQtl() { return expectedPricePerQtl; }
}
