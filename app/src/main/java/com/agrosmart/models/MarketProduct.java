package com.agrosmart.models;

import java.io.Serializable;

public class MarketProduct implements Serializable {
    private String id;
    private String name;
    private String category; // "Seeds", "Fertilizers", "Pesticides", "Tools"
    private String brand;
    private double price;
    private double originalPrice;
    private String packageSize;
    private String description;
    private float rating;
    private int stockCount;

    public MarketProduct() {}

    public MarketProduct(String id, String name, String category, String brand,
                         double price, double originalPrice, String packageSize,
                         String description, float rating, int stockCount) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.brand = brand;
        this.price = price;
        this.originalPrice = originalPrice;
        this.packageSize = packageSize;
        this.description = description;
        this.rating = rating;
        this.stockCount = stockCount;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getBrand() { return brand; }
    public double getPrice() { return price; }
    public double getOriginalPrice() { return originalPrice; }
    public String getPackageSize() { return packageSize; }
    public String getDescription() { return description; }
    public float getRating() { return rating; }
    public int getStockCount() { return stockCount; }
}
