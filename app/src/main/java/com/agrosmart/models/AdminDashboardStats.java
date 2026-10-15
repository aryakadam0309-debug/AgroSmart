package com.agrosmart.models;

import java.io.Serializable;

public class AdminDashboardStats implements Serializable {
    private int totalFarmers;
    private int totalTractors;
    private int totalBookings;
    private int totalProducts;
    private int totalOrders;
    private double totalRevenue;

    public AdminDashboardStats() {}

    public AdminDashboardStats(int totalFarmers, int totalTractors, int totalBookings, int totalProducts, int totalOrders, double totalRevenue) {
        this.totalFarmers = totalFarmers;
        this.totalTractors = totalTractors;
        this.totalBookings = totalBookings;
        this.totalProducts = totalProducts;
        this.totalOrders = totalOrders;
        this.totalRevenue = totalRevenue;
    }

    public int getTotalFarmers() { return totalFarmers; }
    public void setTotalFarmers(int totalFarmers) { this.totalFarmers = totalFarmers; }

    public int getTotalTractors() { return totalTractors; }
    public void setTotalTractors(int totalTractors) { this.totalTractors = totalTractors; }

    public int getTotalBookings() { return totalBookings; }
    public void setTotalBookings(int totalBookings) { this.totalBookings = totalBookings; }

    public int getTotalProducts() { return totalProducts; }
    public void setTotalProducts(int totalProducts) { this.totalProducts = totalProducts; }

    public int getTotalOrders() { return totalOrders; }
    public void setTotalOrders(int totalOrders) { this.totalOrders = totalOrders; }

    public double getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(double totalRevenue) { this.totalRevenue = totalRevenue; }
}
