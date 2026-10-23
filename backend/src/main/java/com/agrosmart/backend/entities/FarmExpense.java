package com.agrosmart.backend.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "farm_expenses")
public class FarmExpense {

    @Id
    @Column(name = "id", length = 50)
    private String id;

    @Column(name = "farmer_phone", length = 20, nullable = false)
    private String farmerPhone;

    @Column(name = "entry_type", length = 20, nullable = false) // EXPENSE or REVENUE
    private String entryType;

    @Column(name = "category", length = 50, nullable = false) // SEEDS, FERTILIZER, LABOR, IRRIGATION, MACHINERY, CROP_SALE
    private String category;

    @Column(name = "title", length = 150, nullable = false)
    private String title;

    @Column(name = "amount", nullable = false)
    private Double amount;

    @Column(name = "entry_date", length = 30)
    private String entryDate;

    @Column(name = "crop_season", length = 50)
    private String cropSeason;

    @Column(name = "notes", columnDefinition = "TEXT")
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
