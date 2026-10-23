package com.agrosmart.models;

import java.io.Serializable;

public class NotificationItem implements Serializable {
    private String id;
    private String farmerPhone;
    private String title;
    private String message;
    private String timestamp;
    private String type; // "WEATHER", "PRICE", "SCHEME", "BOOKING"
    private boolean isRead;

    public NotificationItem() {}

    public NotificationItem(String id, String title, String message, String timestamp, String type, boolean isRead) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.timestamp = timestamp;
        this.type = type;
        this.isRead = isRead;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getFarmerPhone() { return farmerPhone; }
    public void setFarmerPhone(String farmerPhone) { this.farmerPhone = farmerPhone; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public String getTimestamp() { return timestamp; }
    public String getType() { return type; }
    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
}
