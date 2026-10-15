package com.agrosmart.models;

import java.io.Serializable;

public class IrrigationSetting implements Serializable {
    private String deviceId;
    private long farmerId;
    private String operatingMode; // MANUAL or AUTO
    private double lowMoistureStartThreshold;
    private double highMoistureStopThreshold;
    private int maxContinuousPumpSec;
    private int cooldownIntervalSec;
    private double alertThresholdPct;
    private String updatedAt;
    private Long updatedBy;

    public IrrigationSetting() {
        this.operatingMode = "MANUAL";
        this.lowMoistureStartThreshold = 30.0;
        this.highMoistureStopThreshold = 70.0;
        this.maxContinuousPumpSec = 300;
        this.cooldownIntervalSec = 600;
        this.alertThresholdPct = 25.0;
    }

    public IrrigationSetting(String deviceId, long farmerId, String operatingMode,
                             double lowMoistureStartThreshold, double highMoistureStopThreshold,
                             int maxContinuousPumpSec, int cooldownIntervalSec,
                             double alertThresholdPct, String updatedAt, Long updatedBy) {
        this.deviceId = deviceId;
        this.farmerId = farmerId;
        this.operatingMode = operatingMode;
        this.lowMoistureStartThreshold = lowMoistureStartThreshold;
        this.highMoistureStopThreshold = highMoistureStopThreshold;
        this.maxContinuousPumpSec = maxContinuousPumpSec;
        this.cooldownIntervalSec = cooldownIntervalSec;
        this.alertThresholdPct = alertThresholdPct;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
    }

    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }

    public long getFarmerId() { return farmerId; }
    public void setFarmerId(long farmerId) { this.farmerId = farmerId; }

    public String getOperatingMode() { return operatingMode; }
    public void setOperatingMode(String operatingMode) { this.operatingMode = operatingMode; }

    public double getLowMoistureStartThreshold() { return lowMoistureStartThreshold; }
    public void setLowMoistureStartThreshold(double lowMoistureStartThreshold) { this.lowMoistureStartThreshold = lowMoistureStartThreshold; }

    public double getHighMoistureStopThreshold() { return highMoistureStopThreshold; }
    public void setHighMoistureStopThreshold(double highMoistureStopThreshold) { this.highMoistureStopThreshold = highMoistureStopThreshold; }

    public int getMaxContinuousPumpSec() { return maxContinuousPumpSec; }
    public void setMaxContinuousPumpSec(int maxContinuousPumpSec) { this.maxContinuousPumpSec = maxContinuousPumpSec; }

    public int getCooldownIntervalSec() { return cooldownIntervalSec; }
    public void setCooldownIntervalSec(int cooldownIntervalSec) { this.cooldownIntervalSec = cooldownIntervalSec; }

    public double getAlertThresholdPct() { return alertThresholdPct; }
    public void setAlertThresholdPct(double alertThresholdPct) { this.alertThresholdPct = alertThresholdPct; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    public Long getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(Long updatedBy) { this.updatedBy = updatedBy; }
}
