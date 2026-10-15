package com.agrosmart.models;

import java.io.Serializable;

public class PumpCommand implements Serializable {
    private String commandId;
    private String deviceId;
    private String command; // PUMP_ON, PUMP_OFF
    private Integer durationSeconds;
    private String triggerSource; // MANUAL, AUTO_HYSTERESIS
    private boolean requestedState;
    private Boolean confirmedState;
    private String status; // PENDING, EXECUTED, REJECTED_COOLDOWN, REJECTED_ALREADY_ACTIVE, TIMEOUT
    private String failureReason;
    private String createdAt;
    private String executedAt;

    public PumpCommand() {}

    public PumpCommand(String commandId, String deviceId, String command,
                       Integer durationSeconds, String triggerSource,
                       boolean requestedState, Boolean confirmedState,
                       String status, String failureReason,
                       String createdAt, String executedAt) {
        this.commandId = commandId;
        this.deviceId = deviceId;
        this.command = command;
        this.durationSeconds = durationSeconds;
        this.triggerSource = triggerSource;
        this.requestedState = requestedState;
        this.confirmedState = confirmedState;
        this.status = status;
        this.failureReason = failureReason;
        this.createdAt = createdAt;
        this.executedAt = executedAt;
    }

    public String getCommandId() { return commandId; }
    public void setCommandId(String commandId) { this.commandId = commandId; }

    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }

    public String getCommand() { return command; }
    public void setCommand(String command) { this.command = command; }

    public Integer getDurationSeconds() { return durationSeconds; }
    public void setDurationSeconds(Integer durationSeconds) { this.durationSeconds = durationSeconds; }

    public String getTriggerSource() { return triggerSource; }
    public void setTriggerSource(String triggerSource) { this.triggerSource = triggerSource; }

    public boolean isRequestedState() { return requestedState; }
    public void setRequestedState(boolean requestedState) { this.requestedState = requestedState; }

    public Boolean getConfirmedState() { return confirmedState; }
    public void setConfirmedState(Boolean confirmedState) { this.confirmedState = confirmedState; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getFailureReason() { return failureReason; }
    public void setFailureReason(String failureReason) { this.failureReason = failureReason; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getExecutedAt() { return executedAt; }
    public void setExecutedAt(String executedAt) { this.executedAt = executedAt; }
}
