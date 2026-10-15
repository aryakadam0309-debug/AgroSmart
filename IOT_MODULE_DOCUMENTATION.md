# AgroSmart — IoT Smart Farming & Irrigation Module Documentation

## 1. Executive Summary

The **IoT Smart Farming and Irrigation Module** extends the **AgroSmart – Smart Agriculture Management System** with real-time agronomic telemetry and safety-hardened automation. It integrates physical hardware (ESP32 microcontrollers, capacitive soil moisture sensors, DHT22 microclimate sensors, and relay-driven irrigation pumps) with an enterprise Spring Boot 3 REST backend, MySQL 8 database, local offline SQLite caching, and native Android (Java + XML, MVVM, ViewBinding) user interface.

---

## 2. System Architecture & Topology

```
+-----------------------------------------------------------------------------------+
|                           PHYSICAL HARDWARE TIER                                  |
|                                                                                   |
|  +--------------------+         +------------------+         +------------------+ |
|  | Capacitive Soil    |         | DHT22 Temp &     |         | 5V Relay Module  | |
|  | Moisture v1.2      |         | Humidity Sensor  |         | (Submersible     | |
|  | (Analog GPIO 34)   |         | (Digital GPIO 4) |         |  Pump on GPIO 18)| |
|  +---------+----------+         +--------+---------+         +--------+---------+ |
|            |                             |                            |           |
|            +-----------------------+     |     +----------------------+           |
|                                    |     |     |                                  |
|                              +-----v-----v-----v-----+                            |
|                              |      ESP32 DevKit     |                            |
|                              | (Station: ESP32-AGRO) |                            |
|                              +-----------+-----------+                            |
+------------------------------------------|----------------------------------------+
                                           | Wi-Fi 2.4GHz
                                           | REST JSON (API Key Authenticated)
                                           v
+-----------------------------------------------------------------------------------+
|                           APPLICATION SERVER TIER                                 |
|                                                                                   |
|                 +------------------------------------------------+                |
|                 |   Spring Boot 3.3.3 Backend (:8080/api/iot)     |                |
|                 |   - IotController (Telemetry, Polling, Control)|                |
|                 |   - Dual-Threshold Hysteresis Evaluation Engine|                |
|                 |   - Safety Auto-Shutoff & Cooldown Enforcement |                |
|                 |   - SecurityConfig (Permitted /api/iot/**)     |                |
|                 +-----------------------+------------------------+                |
|                                         |                                         |
|                                         v                                         |
|                 +------------------------------------------------+                |
|                 |            MySQL 8.x Enterprise DB             |                |
|                 |  (iot_devices, sensor_readings, settings,      |                |
|                 |   pump_commands, iot_alerts)                   |                |
|                 +------------------------------------------------+                |
+------------------------------------------^----------------------------------------+
                                           | HTTP REST (Retrofit 2 / Gson)
                                           | Emulator: 10.0.2.2:8080
                                           | Physical Device: Host LAN IP:8080
+------------------------------------------v----------------------------------------+
|                            MOBILE CLIENT TIER                                     |
|                                                                                   |
|  +-----------------------------------------------------------------------------+  |
|  |                   Native Android App (Java 17, XML, MVVM)                   |  |
|  |                                                                             |  |
|  |   [ IotDashboardActivity ] <---> [ IotViewModel ] <---> [ IotRepository ]  |  |
|  |                                                                |            |  |
|  |                                                                +---> Local  |  |
|  |   - Moisture Gauge & Category (Dry/Mod/Wet)                          SQLite |  |
|  |   - Microclimate (Temp °C / Humidity %)                             Cache   |  |
|  |   - Requested vs Confirmed State                                            |  |
|  |   - Auto Mode Switch & Threshold Dialog                                     |  |
|  |   - Historical Readings Recycler (20 records)                               |  |
|  +-----------------------------------------------------------------------------+  |
+-----------------------------------------------------------------------------------+
```

---

## 3. Core Feature Details

### Feature A: Smart Soil Moisture Monitoring
- **Sensor**: Capacitive Soil Moisture Sensor v1.2 (corrosion-resistant).
- **Interface**: ESP32 ADC1 Channel 6 (`GPIO 34`), 12-bit resolution ($0 - 4095$).
- **Filtering**: 10-sample running average with noise suppression.
- **Calibration Mapping**:
  $$\text{Moisture \%} = \text{constrain}\left(\frac{\text{DRY\_ADC} - \text{ADC}}{\text{DRY\_ADC} - \text{WET\_ADC}} \times 100,\, 0,\, 100\right)$$
  - Default Dry Air Calibration: $3200\text{ ADC} \rightarrow 0\%$
  - Default Saturated Water Calibration: $1400\text{ ADC} \rightarrow 100\%$
- **Agronomic Classification**:
  - `DRY`: $< 35.0\%$ (Triggers low moisture advisory; initiates auto-irrigation)
  - `MODERATE`: $35.0\% - 70.0\%$ (Optimal vegetative zone)
  - `WET`: $> 70.0\%$ (Saturated; halts irrigation)

### Feature B: Temperature and Humidity Monitoring
- **Sensor**: DHT22 (AM2302) digital capacitive humidity and NTC thermistor.
- **Interface**: ESP32 `GPIO 4` with $10\text{k}\Omega$ pull-up resistor.
- **Validation**: Checks range ($-40^\circ\text{C}$ to $+80^\circ\text{C}$, $0 - 100\%$ RH) and checksum integrity. Rejects NaN or faulty readings with status code `SENSOR_ERROR`.

### Feature C: Smart Irrigation Pump Control
- **Actuator**: 5V Optocoupler-isolated Relay Module driving submersible pump on `GPIO 18`.
- **Operating Modes**:
  1. **Manual Mode**: Direct farmer control via dashboard ON/OFF button.
  2. **Automatic Hysteresis Mode**: Dual-threshold control without relay chattering:
     - **Start Threshold** ($30\%$): Activates pump when soil moisture falls below threshold.
     - **Stop Threshold** ($70\%$): Deactivates pump once target moisture is achieved.
     - Enforces constraint: $\text{Start Threshold} < \text{Stop Threshold}$.
- **Safety Auto-Shutoff**: Maximum continuous runtime cutoff (default 300 seconds / 5 minutes) prevents pump burnout, well depletion, or field flooding if sensors become dislodged.
- **Safety Cooldown**: Minimum rest interval (default 600 seconds / 10 minutes) between successive irrigation runs to allow water infiltration and motor cooling.
- **State Integrity**: Distinguishes **Requested State** from **Physical Confirmed State** (relay contact acknowledgement).

---

## 4. REST API Endpoint Catalog

All endpoints reside under `/api/iot`:

| Method | Endpoint | Description | Security |
|---|---|---|---|
| `POST` | `/api/iot/register` | Register new ESP32 station | Bearer Token / Role Farmer |
| `POST` | `/api/iot/telemetry` | Ingest sensor readings from ESP32 | `X-Device-ApiKey` / Body Key |
| `GET` | `/api/iot/status/{deviceId}` | Get live connection status & latest data | Public / Farmer Session |
| `GET` | `/api/iot/reading/latest/{deviceId}` | Get most recent sensor reading | Public / Farmer Session |
| `GET` | `/api/iot/reading/history/{deviceId}` | Get historical readings list (with `limit`) | Public / Farmer Session |
| `GET` | `/api/iot/irrigation/settings/{deviceId}` | Get current irrigation thresholds | Public / Farmer Session |
| `POST` | `/api/iot/irrigation/settings` | Update thresholds, mode, runtime limits | Farmer Session |
| `POST` | `/api/iot/pump/command` | Dispatch pump ON/OFF command | Farmer Session |
| `GET` | `/api/iot/command/poll/{deviceId}` | Poll next pending command for ESP32 | ESP32 Polling |
| `POST` | `/api/iot/command/ack` | Acknowledge command execution from ESP32 | ESP32 Station |
| `POST` | `/api/iot/heartbeat/{deviceId}` | Liveness heartbeat ping from ESP32 | ESP32 Station |
| `GET` | `/api/iot/alerts/{deviceId}` | Get active unresolved alerts | Farmer Session |

---

## 5. Offline Resiliency & Fault Tolerance

1. **Dual-Tier Persistence**:
   - The Android app never crashes or presents blank screens without internet.
   - Initial render reads from `AgroSmartDbHelper` (`agrosmart_local.db`).
   - Network responses from Spring Boot seamlessly refresh the local SQLite database.
2. **Station Offline Detection**:
   - Device heartbeat timeout (90 seconds).
   - Android dashboard displays "Station Offline (Cache Mode)" with amber indicator.
   - Prevents stale telemetry from being misinterpreted as real-time field data.
