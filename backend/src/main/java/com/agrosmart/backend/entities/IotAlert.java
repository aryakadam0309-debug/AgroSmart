package com.agrosmart.backend.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "iot_alerts", indexes = {
        @Index(name = "idx_alert_device", columnList = "device_id, created_at"),
        @Index(name = "idx_alert_farmer", columnList = "farmer_id, created_at")
})
public class IotAlert {

    @Id
    @Column(length = 50)
    private String id;

    @Column(name = "device_id", nullable = false, length = 50)
    private String deviceId;

    @Column(name = "farmer_id", nullable = false)
    private Long farmerId;

    @Column(nullable = false, length = 50)
    private String alertType; // 'LOW_MOISTURE', 'DEVICE_OFFLINE', 'SENSOR_FAULT', 'SAFETY_TIMEOUT', 'PUMP_OVERRUN'

    @Column(columnDefinition = "TEXT", nullable = false)
    private String message;

    @Column(nullable = false, length = 20)
    private String severity = "WARNING"; // 'INFO', 'WARNING', 'CRITICAL'

    private boolean isRead = false;

    @Column(length = 50)
    private String relatedReadingId;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public IotAlert() {}

    public IotAlert(String id, String deviceId, Long farmerId, String alertType, String message, String severity, String relatedReadingId) {
        this.id = id;
        this.deviceId = deviceId;
        this.farmerId = farmerId;
        this.alertType = alertType;
        this.message = message;
        this.severity = severity;
        this.relatedReadingId = relatedReadingId;
        this.isRead = false;
        this.createdAt = LocalDateTime.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }

    public Long getFarmerId() { return farmerId; }
    public void setFarmerId(Long farmerId) { this.farmerId = farmerId; }

    public String getAlertType() { return alertType; }
    public void setAlertType(String alertType) { this.alertType = alertType; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }

    public String getRelatedReadingId() { return relatedReadingId; }
    public void setRelatedReadingId(String relatedReadingId) { this.relatedReadingId = relatedReadingId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
