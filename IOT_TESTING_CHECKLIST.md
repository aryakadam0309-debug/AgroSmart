# AgroSmart — IoT Testing & Verification Checklist

This checklist provides a structured procedure for evaluating the **AgroSmart IoT Smart Farming & Irrigation Module**.

---

## Section 1: Pre-requisite Environment Verification

- [x] **Android Build Verification**:
  - Command: `.\gradlew.bat assembleDebug --no-daemon`
  - Result: `BUILD SUCCESSFUL`, `app-debug.apk` generated (8.12 MB).
- [x] **Spring Boot Backend Verification**:
  - Command: `.\gradlew.bat compileJava --no-daemon`
  - Result: `BUILD SUCCESSFUL`, zero compilation warnings/errors.
- [x] **Local SQLite Database Verification**:
  - Database helper: `AgroSmartDbHelper.java` (`DATABASE_VERSION = 2`).
  - Pre-seeded tables: `iot_devices`, `sensor_readings`, `irrigation_settings`, `pump_commands`, `iot_alerts`.
- [x] **ESP32 Firmware Verification**:
  - Firmware sketch: `AgroSmartFirmware/AgroSmart_ESP32_Firmware.ino`
  - Header: `AgroSmartFirmware/config.h`

---

## Section 2: Sensor Ingestion & Monitoring Test Cases

| Test ID | Test Description | Expected Result | Verified Status |
|---|---|---|---|
| **TC-SEN-01** | Dry Soil Sensor Reading | ADC $\approx 3200$, converted moisture $\approx 0 - 20\%$, category `DRY` | PASS |
| **TC-SEN-02** | Saturated Wet Soil Reading | Sensor in water; ADC $\approx 1400$, converted moisture $\approx 85 - 100\%$, category `WET` | PASS |
| **TC-SEN-03** | Range Clamping Test | Raw ADC $< 1400$ clamped to $100.0\%$; ADC $> 3200$ clamped to $0.0\%$ | PASS |
| **TC-SEN-04** | DHT22 Temperature & Humidity | Displays ambient temp (e.g. $28.5^\circ\text{C}$) and relative humidity (e.g. $62.0\%$) | PASS |
| **TC-SEN-05** | Sensor History Persistence | Android displays up to 20 historical readings with timestamp, moisture %, and status badge | PASS |
| **TC-SEN-06** | Stale / Offline Telemetry | When station is disconnected for $>90\text{s}$, status chip turns amber: "Offline Cache Mode" | PASS |

---

## Section 3: Smart Irrigation Pump Control & Safety Test Cases

| Test ID | Test Description | Expected Result | Verified Status |
|---|---|---|---|
| **TC-IRR-01** | Manual Pump ON Request | Tapping "Turn Pump ON" dispatches command; requested state shows `ON`; awaiting ACK until relay responds | PASS |
| **TC-IRR-02** | Confirmed State vs Requested State | Dashboard explicitly distinguishes "Requested: ON" from "Relay Confirmed: ON" | PASS |
| **TC-IRR-03** | Manual Pump OFF Request | Tapping "Turn Pump OFF" dispatches command; relay disengages; confirmed state updates to `CONFIRMED OFF` | PASS |
| **TC-IRR-04** | Automatic Hysteresis Start | In AUTO mode, when moisture $< 30.0\%$ (start threshold), pump engages automatically | PASS |
| **TC-IRR-05** | Automatic Hysteresis Stop | When moisture reaches $\ge 70.0\%$ (stop threshold), pump disengages automatically | PASS |
| **TC-IRR-06** | Anti-Chattering Constraint | System validates $\text{Start Threshold} < \text{Stop Threshold}$; rejects configuration if $\text{start} \ge \text{stop}$ | PASS |
| **TC-IRR-07** | Max Continuous Runtime Safety Cutoff | If pump runs continuously for $> 300\text{s}$ (5 min), firmware auto-cuts relay and logs safety alert | PASS |
| **TC-IRR-08** | Minimum Safety Cooldown | After pump stops, command requests within $600\text{s}$ (10 min) are rejected with `REJECTED_COOLDOWN` | PASS |

---

## Section 4: Offline Resilience & UI Test Cases

| Test ID | Test Description | Expected Result | Verified Status |
|---|---|---|---|
| **TC-OFF-01** | Zero-Network Launch | App opens IoT Dashboard with no Wi-Fi/mobile data; loads all 5 seed entities from local SQLite | PASS |
| **TC-OFF-02** | Offline Pump Toggle | In offline mode, user action updates local state optimistically with offline simulation acknowledgement | PASS |
| **TC-OFF-03** | Navigation from Home | Tapping the "IoT Smart Farming (ESP32 LIVE)" banner on `HomeFragment` opens `IotDashboardActivity` | PASS |
| **TC-OFF-04** | Navigation from Services | Tapping "IoT Smart Farming" on `ServicesFragment` opens `IotDashboardActivity` | PASS |
| **TC-OFF-05** | Hardware Setup Guide | Tapping the setup gear icon opens `IotDeviceSetupActivity` displaying schematic & calibration steps | PASS |
| **TC-OFF-06** | Multilingual Verification | UI strings correctly localize in English, हिन्दी (Hindi), and मराठी (Marathi) | PASS |
