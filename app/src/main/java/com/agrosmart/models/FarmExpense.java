package com.agrosmart.models;

import java.io.Serializable;

public class FarmExpense implements Serializable {
    private String id;
    private String farmerPhone;
    private String entryType; // EXPENSE or REVENUE
    private String category;  // SEEDS, FERTILIZER, LABOR, IRRIGATION, MACHINERY, CROP_SALE
    private String title;
    private Double amount;
    private String entryDate;
    private String cropSeason;
    private String notes;

    public FarmExpense() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFarmerPhone() { return farmerPhone; }
    public void setFarmerPhone(String farmerPhone) { this.farmerPhone = farmerPhone; }

    public String getEntryType() { return entryType; }
    public void setEntryType(String entryType) { this.entryType = entryType; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getEntryDate() { return entryDate; }
    public void setEntryDate(String entryDate) { this.entryDate = entryDate; }

    public String getCropSeason() { return cropSeason; }
    public void setCropSeason(String cropSeason) { this.cropSeason = cropSeason; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
