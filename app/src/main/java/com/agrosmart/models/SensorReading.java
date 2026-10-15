package com.agrosmart.models;

import java.io.Serializable;

public class SensorReading implements Serializable {
    private String id;
    private String deviceId;
    private long farmerId;
    private double soilMoisturePct;
    private int soilMoistureRaw;
    private String moistureStatus;
    private Double temperatureC;
    private Double humidityPct;
    private String sensorStatus;
    private String recordedAt;

    public SensorReading() {}

    public SensorReading(String id, String deviceId, long farmerId,
                         double soilMoisturePct, int soilMoistureRaw, String moistureStatus,
                         Double temperatureC, Double humidityPct, String sensorStatus,
                         String recordedAt) {
        this.id = id;
        this.deviceId = deviceId;
        this.farmerId = farmerId;
        this.soilMoisturePct = soilMoisturePct;
        this.soilMoistureRaw = soilMoistureRaw;
        this.moistureStatus = moistureStatus;
        this.temperatureC = temperatureC;
        this.humidityPct = humidityPct;
        this.sensorStatus = sensorStatus;
        this.recordedAt = recordedAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }

    public long getFarmerId() { return farmerId; }
    public void setFarmerId(long farmerId) { this.farmerId = farmerId; }

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

    public String getRecordedAt() { return recordedAt; }
    public void setRecordedAt(String recordedAt) { this.recordedAt = recordedAt; }
}
