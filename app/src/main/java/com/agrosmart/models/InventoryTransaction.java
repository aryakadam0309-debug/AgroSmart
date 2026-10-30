package com.agrosmart.models;

import java.io.Serializable;

public class InventoryTransaction implements Serializable {
    private String id;
    private String itemId;
    private String farmerPhone;
    private String transactionType; // ADDITION, CONSUMPTION, CORRECTION
    private double quantity;
    private String unit;
    private double balanceAfter;
    private String transactionDate;
    private String note;
    private String actor;
    private String createdAt;
    private Long createdAtTimestamp;

    public InventoryTransaction() {}

    public InventoryTransaction(String id, String itemId, String farmerPhone, String transactionType,
                                double quantity, String unit, double balanceAfter,
                                String transactionDate, String note, String actor, String createdAt) {
        this.id = id;
        this.itemId = itemId;
        this.farmerPhone = farmerPhone;
        this.transactionType = transactionType;
        this.quantity = quantity;
        this.unit = unit;
        this.balanceAfter = balanceAfter;
        this.transactionDate = transactionDate;
        this.note = note;
        this.actor = actor;
        this.createdAt = createdAt;
        this.createdAtTimestamp = System.currentTimeMillis();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getItemId() { return itemId; }
    public void setItemId(String itemId) { this.itemId = itemId; }

    public String getFarmerPhone() { return farmerPhone; }
    public void setFarmerPhone(String farmerPhone) { this.farmerPhone = farmerPhone; }

    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }

    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public double getBalanceAfter() { return balanceAfter; }
    public void setBalanceAfter(double balanceAfter) { this.balanceAfter = balanceAfter; }

    public String getTransactionDate() { return transactionDate; }
    public void setTransactionDate(String transactionDate) { this.transactionDate = transactionDate; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public String getActor() { return actor; }
    public void setActor(String actor) { this.actor = actor; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public Long getCreatedAtTimestamp() { return createdAtTimestamp; }
    public void setCreatedAtTimestamp(Long createdAtTimestamp) { this.createdAtTimestamp = createdAtTimestamp; }
}
