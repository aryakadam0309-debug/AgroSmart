package com.agrosmart.backend.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "crop_prices")
public class CropPrice {
    @Id
    private String id;
    private String cropName;
    private String category;
    private String variety;
    private String marketName;
    private String state;
    private String district;
    private double minPrice;
    private double maxPrice;
    private double modalPrice;
    private String priceUnit;
    private String trend;
    private double priceChange;
    private String updatedDate;
    private String arrivalQuantity;

    public CropPrice() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getCropName() { return cropName; }
    public void setCropName(String cropName) { this.cropName = cropName; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getVariety() { return variety; }
    public void setVariety(String variety) { this.variety = variety; }
    public String getMarketName() { return marketName; }
    public void setMarketName(String marketName) { this.marketName = marketName; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }
    public double getMinPrice() { return minPrice; }
    public void setMinPrice(double minPrice) { this.minPrice = minPrice; }
    public double getMaxPrice() { return maxPrice; }
    public void setMaxPrice(double maxPrice) { this.maxPrice = maxPrice; }
    public double getModalPrice() { return modalPrice; }
    public void setModalPrice(double modalPrice) { this.modalPrice = modalPrice; }
    public String getPriceUnit() { return priceUnit; }
    public void setPriceUnit(String priceUnit) { this.priceUnit = priceUnit; }
    public String getTrend() { return trend; }
    public void setTrend(String trend) { this.trend = trend; }
    public double getPriceChange() { return priceChange; }
    public void setPriceChange(double priceChange) { this.priceChange = priceChange; }
    public String getUpdatedDate() { return updatedDate; }
    public void setUpdatedDate(String updatedDate) { this.updatedDate = updatedDate; }
    public String getArrivalQuantity() { return arrivalQuantity; }
    public void setArrivalQuantity(String arrivalQuantity) { this.arrivalQuantity = arrivalQuantity; }
}
