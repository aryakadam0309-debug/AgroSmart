package com.agrosmart.models;

import java.io.Serializable;

public class IotDevice implements Serializable {
    private String deviceId;
    private String deviceName;
    private String deviceType;
    private long ownerId;
    private String farmerPhone;
    private String fieldLocation;
    private String apiKey;
    private String firmwareVersion;
    private boolean active;
    private String lastSeen;
    private String createdAt;

    public IotDevice() {}

    public IotDevice(String deviceId, String deviceName, String deviceType,
                     long ownerId, String farmerPhone, String fieldLocation,
                     String apiKey, String firmwareVersion, boolean active,
                     String lastSeen, String createdAt) {
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.deviceType = deviceType;
        this.ownerId = ownerId;
        this.farmerPhone = farmerPhone;
        this.fieldLocation = fieldLocation;
        this.apiKey = apiKey;
        this.firmwareVersion = firmwareVersion;
        this.active = active;
        this.lastSeen = lastSeen;
        this.createdAt = createdAt;
    }

    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }

    public String getDeviceName() { return deviceName; }
    public void setDeviceName(String deviceName) { this.deviceName = deviceName; }

    public String getDeviceType() { return deviceType; }
    public void setDeviceType(String deviceType) { this.deviceType = deviceType; }

    public long getOwnerId() { return ownerId; }
    public void setOwnerId(long ownerId) { this.ownerId = ownerId; }

    public String getFarmerPhone() { return farmerPhone; }
    public void setFarmerPhone(String farmerPhone) { this.farmerPhone = farmerPhone; }

    public String getFieldLocation() { return fieldLocation; }
    public void setFieldLocation(String fieldLocation) { this.fieldLocation = fieldLocation; }

    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }

    public String getFirmwareVersion() { return firmwareVersion; }
    public void setFirmwareVersion(String firmwareVersion) { this.firmwareVersion = firmwareVersion; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public String getLastSeen() { return lastSeen; }
    public void setLastSeen(String lastSeen) { this.lastSeen = lastSeen; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
