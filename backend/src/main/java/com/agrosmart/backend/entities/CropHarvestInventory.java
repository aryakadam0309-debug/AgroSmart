package com.agrosmart.backend.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "crop_harvest_inventory")
public class CropHarvestInventory {
    @Id
    private String id;

    @Column(nullable = false)
    private String farmerPhone;

    @Column(nullable = false)
    private String cropName;

    private String variety;
    private double harvestedQuintals = 0.0;
    private double soldQuintals = 0.0;
    private double stockRemainingQuintals = 0.0;
    private String harvestDate;
    private String storageLocation;
    private double expectedPricePerQtl = 0.0;

    public CropHarvestInventory() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFarmerPhone() { return farmerPhone; }
    public void setFarmerPhone(String farmerPhone) { this.farmerPhone = farmerPhone; }

    public String getCropName() { return cropName; }
    public void setCropName(String cropName) { this.cropName = cropName; }

    public String getVariety() { return variety; }
    public void setVariety(String variety) { this.variety = variety; }

    public double getHarvestedQuintals() { return harvestedQuintals; }
    public void setHarvestedQuintals(double harvestedQuintals) { this.harvestedQuintals = harvestedQuintals; }

    public double getSoldQuintals() { return soldQuintals; }
    public void setSoldQuintals(double soldQuintals) { this.soldQuintals = soldQuintals; }

    public double getStockRemainingQuintals() { return stockRemainingQuintals; }
    public void setStockRemainingQuintals(double stockRemainingQuintals) { this.stockRemainingQuintals = stockRemainingQuintals; }

    public String getHarvestDate() { return harvestDate; }
    public void setHarvestDate(String harvestDate) { this.harvestDate = harvestDate; }

    public String getStorageLocation() { return storageLocation; }
    public void setStorageLocation(String storageLocation) { this.storageLocation = storageLocation; }

    public double getExpectedPricePerQtl() { return expectedPricePerQtl; }
    public void setExpectedPricePerQtl(double expectedPricePerQtl) { this.expectedPricePerQtl = expectedPricePerQtl; }
}
