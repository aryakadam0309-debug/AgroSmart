package com.agrosmart.backend.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pump_commands", indexes = {
        @Index(name = "idx_cmd_device_status", columnList = "device_id, status")
})
public class PumpCommand {

    @Id
    @Column(length = 50)
    private String id;

    @Column(name = "device_id", nullable = false, length = 50)
    private String deviceId;

    @Column(nullable = false)
    private Long requestingUserId;

    @Column(nullable = false, length = 10)
    private String requestedState; // 'ON', 'OFF'

    @Column(nullable = false, length = 30)
    private String commandSource; // 'MANUAL_APP', 'AUTOMATIC_THRESHOLD', 'SAFETY_TIMEOUT'

    @Column(nullable = false, length = 20)
    private String status = "PENDING"; // 'PENDING', 'ACKNOWLEDGED', 'COMPLETED', 'EXPIRED', 'FAILED'

    @Column(length = 10)
    private String confirmedActualState = "OFF";

    @Column(length = 255)
    private String failureReason;

    private int durationSeconds = 0;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime acknowledgedAt;

    private LocalDateTime completedAt;

    public PumpCommand() {}

    public PumpCommand(String id, String deviceId, Long requestingUserId, String requestedState, String commandSource) {
        this.id = id;
        this.deviceId = deviceId;
        this.requestingUserId = requestingUserId;
        this.requestedState = requestedState;
        this.commandSource = commandSource;
        this.status = "PENDING";
        this.confirmedActualState = "OFF";
        this.createdAt = LocalDateTime.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }

    public Long getRequestingUserId() { return requestingUserId; }
    public void setRequestingUserId(Long requestingUserId) { this.requestingUserId = requestingUserId; }

    public String getRequestedState() { return requestedState; }
    public void setRequestedState(String requestedState) { this.requestedState = requestedState; }

    public String getCommandSource() { return commandSource; }
    public void setCommandSource(String commandSource) { this.commandSource = commandSource; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getConfirmedActualState() { return confirmedActualState; }
    public void setConfirmedActualState(String confirmedActualState) { this.confirmedActualState = confirmedActualState; }

    public String getFailureReason() { return failureReason; }
    public void setFailureReason(String failureReason) { this.failureReason = failureReason; }

    public int getDurationSeconds() { return durationSeconds; }
    public void setDurationSeconds(int durationSeconds) { this.durationSeconds = durationSeconds; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getAcknowledgedAt() { return acknowledgedAt; }
    public void setAcknowledgedAt(LocalDateTime acknowledgedAt) { this.acknowledgedAt = acknowledgedAt; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
}
