package com.agrosmart.backend.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "irrigation_settings")
public class IrrigationSetting {

    @Id
    @Column(name = "device_id", length = 50)
    private String deviceId;

    @Column(nullable = false)
    private Long farmerId;

    @Column(length = 20)
    private String operatingMode = "MANUAL"; // 'MANUAL', 'AUTOMATIC'

    private double lowMoistureStartThreshold = 30.0;

    private double highMoistureStopThreshold = 70.0;

    private int maxContinuousPumpSec = 300; // 5 mins

    private int cooldownIntervalSec = 600; // 10 mins

    private double alertThresholdPct = 25.0;

    private LocalDateTime updatedAt = LocalDateTime.now();

    private Long updatedBy;

    public IrrigationSetting() {}

    public IrrigationSetting(String deviceId, Long farmerId, String operatingMode, double lowStart, double highStop, int maxSec, int cooldownSec, double alertPct) {
        this.deviceId = deviceId;
        this.farmerId = farmerId;
        this.operatingMode = operatingMode;
        this.lowMoistureStartThreshold = lowStart;
        this.highMoistureStopThreshold = highStop;
        this.maxContinuousPumpSec = maxSec;
        this.cooldownIntervalSec = cooldownSec;
        this.alertThresholdPct = alertPct;
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = farmerId;
    }

    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }

    public Long getFarmerId() { return farmerId; }
    public void setFarmerId(Long farmerId) { this.farmerId = farmerId; }

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

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Long getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(Long updatedBy) { this.updatedBy = updatedBy; }
}
