package com.agrosmart.backend.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sensor_readings", indexes = {
        @Index(name = "idx_device_time", columnList = "device_id, recorded_at"),
        @Index(name = "idx_farmer_time", columnList = "farmer_id, recorded_at")
})
public class SensorReading {

    @Id
    @Column(length = 50)
    private String id;

    @Column(name = "device_id", nullable = false, length = 50)
    private String deviceId;

    @Column(name = "farmer_id", nullable = false)
    private Long farmerId;

    @Column(nullable = false)
    private double soilMoisturePct;

    @Column(nullable = false)
    private int soilMoistureRaw;

    @Column(nullable = false, length = 20)
    private String moistureStatus; // 'DRY', 'MODERATE', 'WET'

    private Double temperatureC;

    private Double humidityPct;

    @Column(length = 30)
    private String sensorStatus = "VALID"; // 'VALID', 'DHT_DISCONNECTED', 'CALIBRATION_WARN'

    @Column(name = "recorded_at", nullable = false)
    private LocalDateTime recordedAt = LocalDateTime.now();

    public SensorReading() {}

    public SensorReading(String id, String deviceId, Long farmerId, double soilMoisturePct, int soilMoistureRaw,
                         String moistureStatus, Double temperatureC, Double humidityPct, String sensorStatus) {
        this.id = id;
        this.deviceId = deviceId;
        this.farmerId = farmerId;
        this.soilMoisturePct = soilMoisturePct;
        this.soilMoistureRaw = soilMoistureRaw;
        this.moistureStatus = moistureStatus;
        this.temperatureC = temperatureC;
        this.humidityPct = humidityPct;
        this.sensorStatus = sensorStatus != null ? sensorStatus : "VALID";
        this.recordedAt = LocalDateTime.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }

    public Long getFarmerId() { return farmerId; }
    public void setFarmerId(Long farmerId) { this.farmerId = farmerId; }

    public double getSoilMoisturePct() { return soilMoisturePct; }
    public void setSoilMoisturePct(double soilMoisturePct) { this.soilMoisturePct = soilMoisturePct; }

    public int getSoilMoistureRaw() { return soilMoistureRaw; }
    public void setSoilMoistureRaw(int soilMoistureRaw) { this.soilMoistureRaw = soilMoistureRaw; }

    public String getMoistureStatus() { return moistureStatus; }
    public void setMoistureStatus(String moistureStatus) { this.moistureStatus = moistureStatus; }

    public Double getTemperatureC() { return temperatureC; }
    public void setTemperatureC(Double temperatureC) { this.temperatureC = temperatureC; }

    public Double getHumidityPct() { return humidityPct; }
    public void setHumidityPct(Double humidityPct) { this.humidityPct = humidityPct; }

    public String getSensorStatus() { return sensorStatus; }
    public void setSensorStatus(String sensorStatus) { this.sensorStatus = sensorStatus; }

    public LocalDateTime getRecordedAt() { return recordedAt; }
    public void setRecordedAt(LocalDateTime recordedAt) { this.recordedAt = recordedAt; }
}
