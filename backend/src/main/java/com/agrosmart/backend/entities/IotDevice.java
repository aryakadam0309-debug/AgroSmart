package com.agrosmart.backend.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "iot_devices")
public class IotDevice {

    @Id
    @Column(name = "device_id", length = 50)
    private String deviceId;

    @Column(nullable = false, length = 100)
    private String deviceName;

    @Column(length = 50)
    private String deviceType = "ESP32_SMART_STATION";

    @Column(nullable = false)
    private Long ownerId;

    @Column(nullable = false, length = 15)
    private String farmerPhone;

    @Column(length = 150)
    private String fieldLocation;

    @Column(nullable = false, length = 100)
    private String apiKey;

    @Column(length = 20)
    private String firmwareVersion = "1.0.0";

    private boolean isActive = true;

    private LocalDateTime lastSeen = LocalDateTime.now();

    private LocalDateTime createdAt = LocalDateTime.now();

    public IotDevice() {}

    public IotDevice(String deviceId, String deviceName, String deviceType, Long ownerId, String farmerPhone, String fieldLocation, String apiKey) {
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.deviceType = deviceType;
        this.ownerId = ownerId;
        this.farmerPhone = farmerPhone;
        this.fieldLocation = fieldLocation;
        this.apiKey = apiKey;
        this.isActive = true;
        this.lastSeen = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
    }

    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }

    public String getDeviceName() { return deviceName; }
    public void setDeviceName(String deviceName) { this.deviceName = deviceName; }

    public String getDeviceType() { return deviceType; }
    public void setDeviceType(String deviceType) { this.deviceType = deviceType; }

    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }

    public String getFarmerPhone() { return farmerPhone; }
    public void setFarmerPhone(String farmerPhone) { this.farmerPhone = farmerPhone; }

    public String getFieldLocation() { return fieldLocation; }
    public void setFieldLocation(String fieldLocation) { this.fieldLocation = fieldLocation; }

    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }

    public String getFirmwareVersion() { return firmwareVersion; }
    public void setFirmwareVersion(String firmwareVersion) { this.firmwareVersion = firmwareVersion; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public LocalDateTime getLastSeen() { return lastSeen; }
    public void setLastSeen(LocalDateTime lastSeen) { this.lastSeen = lastSeen; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
