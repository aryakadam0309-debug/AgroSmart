package com.agrosmart.backend.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "device_tokens")
public class DeviceToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String farmerPhone;

    @Column(nullable = false, length = 512)
    private String token;

    private String deviceModel;
    private LocalDateTime updatedAt = LocalDateTime.now();

    public DeviceToken() {}

    public DeviceToken(String farmerPhone, String token, String deviceModel) {
        this.farmerPhone = farmerPhone;
        this.token = token;
        this.deviceModel = deviceModel;
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFarmerPhone() { return farmerPhone; }
    public void setFarmerPhone(String farmerPhone) { this.farmerPhone = farmerPhone; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getDeviceModel() { return deviceModel; }
    public void setDeviceModel(String deviceModel) { this.deviceModel = deviceModel; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
