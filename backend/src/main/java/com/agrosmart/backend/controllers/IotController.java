package com.agrosmart.backend.controllers;

import com.agrosmart.backend.entities.*;
import com.agrosmart.backend.repositories.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/iot")
@CrossOrigin(origins = "*")
public class IotController {

    private final IotDeviceRepository deviceRepository;
    private final SensorReadingRepository readingRepository;
    private final IrrigationSettingRepository settingRepository;
    private final PumpCommandRepository commandRepository;
    private final IotAlertRepository alertRepository;

    public IotController(IotDeviceRepository deviceRepository,
                         SensorReadingRepository readingRepository,
                         IrrigationSettingRepository settingRepository,
                         PumpCommandRepository commandRepository,
                         IotAlertRepository alertRepository) {
        this.deviceRepository = deviceRepository;
        this.readingRepository = readingRepository;
        this.settingRepository = settingRepository;
        this.commandRepository = commandRepository;
        this.alertRepository = alertRepository;
    }

    // ==========================================================
    // 1. DEVICE REGISTRATION & DISCOVERY
    // ==========================================================
    @GetMapping("/devices")
    public List<IotDevice> getDevices(@RequestParam(required = false) Long ownerId,
                                      @RequestParam(required = false) String phone) {
        if (ownerId != null) {
            return deviceRepository.findByOwnerId(ownerId);
        }
        if (phone != null && !phone.trim().isEmpty()) {
            return deviceRepository.findByFarmerPhone(phone.trim());
        }
        return deviceRepository.findAll();
    }

    @GetMapping("/devices/{deviceId}")
    public ResponseEntity<IotDevice> getDeviceById(@PathVariable String deviceId) {
        return deviceRepository.findById(deviceId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/devices/register")
    public ResponseEntity<IotDevice> registerDevice(@RequestBody IotDevice device) {
        if (device.getDeviceId() == null || device.getDeviceId().trim().isEmpty()) {
            device.setDeviceId("ESP32-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }
        if (device.getApiKey() == null || device.getApiKey().trim().isEmpty()) {
            device.setApiKey(UUID.randomUUID().toString().replace("-", ""));
        }
        device.setLastSeen(LocalDateTime.now());
        device.setCreatedAt(LocalDateTime.now());
        device.setActive(true);
        IotDevice saved = deviceRepository.save(device);

        // Ensure default settings exist for this device
        if (!settingRepository.existsById(saved.getDeviceId())) {
            IrrigationSetting defaultSetting = new IrrigationSetting(
                    saved.getDeviceId(), saved.getOwnerId(), "MANUAL", 30.0, 70.0, 300, 600, 25.0
            );
            settingRepository.save(defaultSetting);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // ==========================================================
    // 2. SENSOR READINGS (ESP32 INGESTION & APP MONITORING)
    // ==========================================================
    @PostMapping("/readings")
    public ResponseEntity<?> submitReading(@RequestBody SensorReading reading,
                                           @RequestHeader(value = "X-Device-ApiKey", required = false) String apiKeyHeader) {
        if (reading.getDeviceId() == null || reading.getDeviceId().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Device ID is required"));
        }

        Optional<IotDevice> deviceOpt = deviceRepository.findById(reading.getDeviceId());
        if (deviceOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Device not registered"));
        }

        IotDevice device = deviceOpt.get();

        // Optional API key check if header is provided
        if (apiKeyHeader != null && !apiKeyHeader.equals(device.getApiKey())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid Device API Key"));
        }

        // Validate ranges
        double moisture = Math.max(0.0, Math.min(100.0, reading.getSoilMoisturePct()));
        reading.setSoilMoisturePct(moisture);

        // Classify moisture status
        if (moisture < 30.0) {
            reading.setMoistureStatus("DRY");
        } else if (moisture <= 70.0) {
            reading.setMoistureStatus("MODERATE");
        } else {
            reading.setMoistureStatus("WET");
        }

        if (reading.getId() == null || reading.getId().trim().isEmpty()) {
            reading.setId("SR-" + UUID.randomUUID().toString().substring(0, 10));
        }
        reading.setFarmerId(device.getOwnerId());
        reading.setRecordedAt(LocalDateTime.now());

        SensorReading saved = readingRepository.save(reading);

        // Update device heartbeat
        device.setLastSeen(LocalDateTime.now());
        deviceRepository.save(device);

        // 1. Low Moisture Alert Check
        IrrigationSetting setting = settingRepository.findByDeviceId(device.getDeviceId())
                .orElse(new IrrigationSetting(device.getDeviceId(), device.getOwnerId(), "MANUAL", 30.0, 70.0, 300, 600, 25.0));

        if (moisture < setting.getAlertThresholdPct()) {
            if (!alertRepository.existsByDeviceIdAndAlertTypeAndIsReadFalse(device.getDeviceId(), "LOW_MOISTURE")) {
                IotAlert alert = new IotAlert(
                        "ALT-" + UUID.randomUUID().toString().substring(0, 8),
                        device.getDeviceId(), device.getOwnerId(),
                        "LOW_MOISTURE",
                        "Soil moisture dropped to " + String.format(Locale.US, "%.1f", moisture) + "% (Threshold: " + setting.getAlertThresholdPct() + "%). Irrigation recommended.",
                        "WARNING", saved.getId()
                );
                alertRepository.save(alert);
            }
        }

        // 2. Automatic Irrigation Hysteresis Evaluation
        if ("AUTOMATIC".equalsIgnoreCase(setting.getOperatingMode())) {
            evaluateAutomaticIrrigation(device, setting, moisture);
        }

        return ResponseEntity.ok(saved);
    }

    private void evaluateAutomaticIrrigation(IotDevice device, IrrigationSetting setting, double currentMoisture) {
        Optional<PumpCommand> latestCmdOpt = commandRepository.findFirstByDeviceIdOrderByCreatedAtDesc(device.getDeviceId());
        boolean isPumpCurrentlyOn = latestCmdOpt.isPresent() && "ON".equalsIgnoreCase(latestCmdOpt.get().getConfirmedActualState());

        if (!isPumpCurrentlyOn && currentMoisture < setting.getLowMoistureStartThreshold()) {
            // Check cooldown period
            if (latestCmdOpt.isPresent() && latestCmdOpt.get().getCompletedAt() != null) {
                long elapsedSeconds = Duration.between(latestCmdOpt.get().getCompletedAt(), LocalDateTime.now()).getSeconds();
                if (elapsedSeconds < setting.getCooldownIntervalSec()) {
                    return; // Cooldown active, protect pump
                }
            }

            // Create Auto-ON Command
            PumpCommand autoOnCmd = new PumpCommand(
                    "CMD-" + UUID.randomUUID().toString().substring(0, 8),
                    device.getDeviceId(), device.getOwnerId(), "ON", "AUTOMATIC_THRESHOLD"
            );
            commandRepository.save(autoOnCmd);

        } else if (isPumpCurrentlyOn) {
            // Stop condition A: Reached upper moisture target
            if (currentMoisture >= setting.getHighMoistureStopThreshold()) {
                PumpCommand autoOffCmd = new PumpCommand(
                        "CMD-" + UUID.randomUUID().toString().substring(0, 8),
                        device.getDeviceId(), device.getOwnerId(), "OFF", "AUTOMATIC_THRESHOLD"
                );
                commandRepository.save(autoOffCmd);
            }
            // Stop condition B: Maximum continuous runtime safety limit exceeded
            else if (latestCmdOpt.get().getAcknowledgedAt() != null) {
                long runtimeSeconds = Duration.between(latestCmdOpt.get().getAcknowledgedAt(), LocalDateTime.now()).getSeconds();
                if (runtimeSeconds >= setting.getMaxContinuousPumpSec()) {
                    PumpCommand safetyOffCmd = new PumpCommand(
                            "CMD-" + UUID.randomUUID().toString().substring(0, 8),
                            device.getDeviceId(), device.getOwnerId(), "OFF", "SAFETY_TIMEOUT"
                    );
                    commandRepository.save(safetyOffCmd);

                    // Create Safety Alert
                    IotAlert safetyAlert = new IotAlert(
                            "ALT-" + UUID.randomUUID().toString().substring(0, 8),
                            device.getDeviceId(), device.getOwnerId(),
                            "SAFETY_TIMEOUT",
                            "Pump auto-stopped: Maximum continuous run duration (" + (setting.getMaxContinuousPumpSec() / 60) + " mins) reached.",
                            "CRITICAL", null
                    );
                    alertRepository.save(safetyAlert);
                }
            }
        }
    }

    @GetMapping("/readings/latest/{deviceId}")
    public ResponseEntity<SensorReading> getLatestReading(@PathVariable String deviceId) {
        return readingRepository.findFirstByDeviceIdOrderByRecordedAtDesc(deviceId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/readings/history/{deviceId}")
    public List<SensorReading> getReadingHistory(@PathVariable String deviceId,
                                                @RequestParam(defaultValue = "20") int limit) {
        return readingRepository.findByDeviceIdOrderByRecordedAtDesc(deviceId, PageRequest.of(0, Math.min(limit, 100)));
    }

    // ==========================================================
    // 3. IRRIGATION SETTINGS
    // ==========================================================
    @GetMapping("/settings/{deviceId}")
    public ResponseEntity<IrrigationSetting> getSettings(@PathVariable String deviceId) {
        return settingRepository.findByDeviceId(deviceId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/settings/{deviceId}")
    public ResponseEntity<?> updateSettings(@PathVariable String deviceId, @RequestBody IrrigationSetting newSettings) {
        // Hysteresis Validation
        if (newSettings.getLowMoistureStartThreshold() >= newSettings.getHighMoistureStopThreshold()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Start threshold (" + newSettings.getLowMoistureStartThreshold() + "%) must be lower than stop threshold (" + newSettings.getHighMoistureStopThreshold() + "%) to prevent relay oscillation."));
        }

        if (newSettings.getMaxContinuousPumpSec() < 30 || newSettings.getMaxContinuousPumpSec() > 1800) {
            return ResponseEntity.badRequest().body(Map.of("error", "Maximum continuous pump duration must be between 30 and 1800 seconds."));
        }

        if (newSettings.getCooldownIntervalSec() < 60 || newSettings.getCooldownIntervalSec() > 3600) {
            return ResponseEntity.badRequest().body(Map.of("error", "Cooldown interval must be between 60 and 3600 seconds."));
        }

        newSettings.setDeviceId(deviceId);
        newSettings.setUpdatedAt(LocalDateTime.now());
        IrrigationSetting saved = settingRepository.save(newSettings);
        return ResponseEntity.ok(saved);
    }

    // ==========================================================
    // 4. PUMP CONTROL & COMMAND ACKNOWLEDGEMENT
    // ==========================================================
    @PostMapping("/commands")
    public ResponseEntity<?> issuePumpCommand(@RequestBody PumpCommand command) {
        if (command.getDeviceId() == null || command.getRequestedState() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Device ID and requestedState (ON/OFF) are required"));
        }

        Optional<IotDevice> deviceOpt = deviceRepository.findById(command.getDeviceId());
        if (deviceOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Device not found"));
        }

        IotDevice device = deviceOpt.get();

        // Check if device is offline (> 5 mins no heartbeat)
        boolean isDeviceOffline = Duration.between(device.getLastSeen(), LocalDateTime.now()).toMinutes() > 5;
        if (isDeviceOffline && "ON".equalsIgnoreCase(command.getRequestedState())) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("error", "Device appears to be OFFLINE. Cannot turn ON pump until connection is restored."));
        }

        // Cooldown check for manual ON
        if ("ON".equalsIgnoreCase(command.getRequestedState())) {
            Optional<IrrigationSetting> settingOpt = settingRepository.findByDeviceId(device.getDeviceId());
            int cooldown = settingOpt.map(IrrigationSetting::getCooldownIntervalSec).orElse(600);
            Optional<PumpCommand> latestCmd = commandRepository.findFirstByDeviceIdOrderByCreatedAtDesc(device.getDeviceId());

            if (latestCmd.isPresent() && latestCmd.get().getCompletedAt() != null) {
                long elapsed = Duration.between(latestCmd.get().getCompletedAt(), LocalDateTime.now()).getSeconds();
                if (elapsed < cooldown) {
                    long remaining = cooldown - elapsed;
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                            .body(Map.of("error", "Pump is in safety cooldown. Please wait " + remaining + " seconds before turning on."));
                }
            }
        }

        command.setId("CMD-" + UUID.randomUUID().toString().substring(0, 8));
        command.setRequestingUserId(device.getOwnerId());
        command.setCommandSource("MANUAL_APP");
        command.setStatus("PENDING");
        command.setCreatedAt(LocalDateTime.now());

        PumpCommand saved = commandRepository.save(command);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(saved);
    }

    @GetMapping("/devices/{deviceId}/commands/pending")
    public ResponseEntity<?> getPendingCommands(@PathVariable String deviceId) {
        List<PumpCommand> pending = commandRepository.findByDeviceIdAndStatusOrderByCreatedAtAsc(deviceId, "PENDING");
        if (pending.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        PumpCommand oldest = pending.get(0);
        oldest.setStatus("ACKNOWLEDGED");
        oldest.setAcknowledgedAt(LocalDateTime.now());
        commandRepository.save(oldest);
        return ResponseEntity.ok(List.of(oldest));
    }

    @PostMapping("/commands/{commandId}/acknowledge")
    public ResponseEntity<?> acknowledgeCommand(@PathVariable String commandId,
                                                @RequestBody Map<String, Object> ackPayload) {
        Optional<PumpCommand> cmdOpt = commandRepository.findById(commandId);
        if (cmdOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        PumpCommand cmd = cmdOpt.get();
        String confirmedState = (String) ackPayload.getOrDefault("confirmedActualState", cmd.getRequestedState());
        String status = (String) ackPayload.getOrDefault("status", "COMPLETED");
        String failureReason = (String) ackPayload.get("failureReason");

        cmd.setConfirmedActualState(confirmedState);
        cmd.setStatus(status);
        cmd.setFailureReason(failureReason);
        cmd.setCompletedAt(LocalDateTime.now());

        if (cmd.getAcknowledgedAt() != null) {
            cmd.setDurationSeconds((int) Duration.between(cmd.getAcknowledgedAt(), cmd.getCompletedAt()).getSeconds());
        }

        commandRepository.save(cmd);
        return ResponseEntity.ok(cmd);
    }

    @PostMapping("/devices/{deviceId}/heartbeat")
    public ResponseEntity<?> heartbeat(@PathVariable String deviceId) {
        return deviceRepository.findById(deviceId).map(device -> {
            device.setLastSeen(LocalDateTime.now());
            deviceRepository.save(device);
            return ResponseEntity.ok(Map.of("status", "HEARTBEAT_ACK", "timestamp", LocalDateTime.now().toString()));
        }).orElse(ResponseEntity.notFound().build());
    }

    // ==========================================================
    // 5. ALERTS & LOGS
    // ==========================================================
    @GetMapping("/alerts/{deviceId}")
    public List<IotAlert> getAlerts(@PathVariable String deviceId,
                                   @RequestParam(defaultValue = "15") int limit) {
        return alertRepository.findByDeviceIdOrderByCreatedAtDesc(deviceId, PageRequest.of(0, Math.min(limit, 50)));
    }

    @PutMapping("/alerts/{alertId}/read")
    public ResponseEntity<?> markAlertRead(@PathVariable String alertId) {
        return alertRepository.findById(alertId).map(alert -> {
            alert.setRead(true);
            alertRepository.save(alert);
            return ResponseEntity.ok(alert);
        }).orElse(ResponseEntity.notFound().build());
    }
}
