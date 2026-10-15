package com.agrosmart.models;

import java.io.Serializable;

public class IotAlert implements Serializable {
    private String id;
    private String deviceId;
    private long farmerId;
    private String alertType; // LOW_SOIL_MOISTURE, HIGH_TEMPERATURE, SENSOR_FAULT, PUMP_COOLDOWN_ACTIVE, PUMP_MAX_RUNTIME_REACHED
    private String severity; // LOW, MEDIUM, HIGH, CRITICAL
    private String title;
    private String message;
    private boolean resolved;
    private String createdAt;

    public IotAlert() {}

    public IotAlert(String id, String deviceId, long farmerId,
                    String alertType, String severity, String title,
                    String message, boolean resolved, String createdAt) {
        this.id = id;
        this.deviceId = deviceId;
        this.farmerId = farmerId;
        this.alertType = alertType;
        this.severity = severity;
        this.title = title;
        this.message = message;
        this.resolved = resolved;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }

    public long getFarmerId() { return farmerId; }
    public void setFarmerId(long farmerId) { this.farmerId = farmerId; }

    public String getAlertType() { return alertType; }
    public void setAlertType(String alertType) { this.alertType = alertType; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public boolean isResolved() { return resolved; }
    public void setResolved(boolean resolved) { this.resolved = resolved; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
