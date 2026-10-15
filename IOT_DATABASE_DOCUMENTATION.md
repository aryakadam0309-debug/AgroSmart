# AgroSmart — IoT Database Documentation

## 1. Overview & Dual-Database Strategy

AgroSmart utilizes a dual-tier database architecture designed for resilient agricultural field conditions:
1. **Server-Side MySQL 8.x**: Central relational store behind the Spring Boot 3 REST API. Stores historical telemetry across all stations, audit logs, command history, and user relationships.
2. **Client-Side Android SQLite (`agrosmart_local.db`)**: Local on-device embedded database managed via `AgroSmartDbHelper`. Provides offline caching, immediate screen rendering, and offline pump control simulation.

---

## 2. Table Schemas & Data Dictionaries

### 2.1 Table: `iot_devices`
Stores registered ESP32 field stations and hardware metadata.

| Field Name | MySQL Type | SQLite Type | Nullable | Key / Constraint | Description |
|---|---|---|---|---|---|
| `device_id` | `VARCHAR(50)` | `TEXT` | NO | PRIMARY KEY | Unique hardware serial (e.g. `ESP32-AGRO-01`) |
| `device_name` | `VARCHAR(100)` | `TEXT` | NO | - | Descriptive station label |
| `device_type` | `VARCHAR(50)` | `TEXT` | YES | DEFAULT 'ESP32_SMART_STATION' | Hardware category |
| `owner_id` | `BIGINT` | `INTEGER` | NO | FOREIGN KEY (`users.id`) | Registered farmer ID |
| `farmer_phone`| `VARCHAR(15)` | `TEXT` | NO | - | Farmer mobile contact |
| `field_location` | `VARCHAR(150)` | `TEXT` | YES | - | Geographic field / plot block |
| `api_key` | `VARCHAR(100)` | `TEXT` | NO | - | Shared secret for ESP32 HTTP authentication |
| `firmware_version`| `VARCHAR(20)` | `TEXT` | YES | DEFAULT '1.0.0' | Firmware version |
| `is_active` | `BOOLEAN` | `INTEGER` | YES | DEFAULT 1 | Active station toggle |
| `last_seen` | `TIMESTAMP` | `TEXT` | YES | - | Timestamp of last heartbeat/telemetry ping |
| `created_at` | `TIMESTAMP` | `TEXT` | YES | DEFAULT CURRENT_TIMESTAMP | Registration date |

---

### 2.2 Table: `sensor_readings`
Time-series sensor readings from capacitive soil moisture and DHT22 sensors.

| Field Name | MySQL Type | SQLite Type | Nullable | Key / Constraint | Description |
|---|---|---|---|---|---|
| `id` | `VARCHAR(50)` | `TEXT` | NO | PRIMARY KEY | Unique UUID / reading ID |
| `device_id` | `VARCHAR(50)` | `TEXT` | NO | INDEX (`idx_reading_device_time`) | Associated station |
| `farmer_id` | `BIGINT` | `INTEGER` | NO | INDEX (`idx_reading_farmer_time`) | Associated farmer |
| `soil_moisture_pct` | `DOUBLE` | `REAL` | NO | - | Calibrated soil moisture percentage ($0.0 - 100.0\%$) |
| `soil_moisture_raw` | `INT` | `INTEGER` | NO | - | Raw 12-bit ADC reading ($0 - 4095$) |
| `moisture_status` | `VARCHAR(20)` | `TEXT` | NO | - | `DRY`, `MODERATE`, or `WET` |
| `temperature_c` | `DOUBLE` | `REAL` | YES | - | Ambient temperature from DHT22 in °C |
| `humidity_pct` | `DOUBLE` | `REAL` | YES | - | Relative humidity from DHT22 in % |
| `sensor_status` | `VARCHAR(30)` | `TEXT` | YES | DEFAULT 'VALID' | Sensor health (`VALID`, `STALE`, `SENSOR_ERROR`) |
| `recorded_at` | `TIMESTAMP` | `TEXT` | NO | - | Telemetry timestamp |

---

### 2.3 Table: `irrigation_settings`
Configurable operational parameters and dual-threshold hysteresis controls.

| Field Name | MySQL Type | SQLite Type | Nullable | Key / Constraint | Description |
|---|---|---|---|---|---|
| `device_id` | `VARCHAR(50)` | `TEXT` | NO | PRIMARY KEY | Associated station |
| `farmer_id` | `BIGINT` | `INTEGER` | NO | - | Owner farmer ID |
| `operating_mode` | `VARCHAR(20)` | `TEXT` | YES | DEFAULT 'MANUAL' | `MANUAL` or `AUTO` |
| `low_moisture_start_threshold` | `DOUBLE` | `REAL` | YES | DEFAULT 30.0 | Moisture threshold below which irrigation starts |
| `high_moisture_stop_threshold` | `DOUBLE` | `REAL` | YES | DEFAULT 70.0 | Moisture threshold at which irrigation stops |
| `max_continuous_pump_sec` | `INT` | `INTEGER` | YES | DEFAULT 300 | Max continuous runtime before auto cutoff (sec) |
| `cooldown_interval_sec` | `INT` | `INTEGER` | YES | DEFAULT 600 | Mandatory cooldown interval between cycles (sec) |
| `alert_threshold_pct` | `DOUBLE` | `REAL` | YES | DEFAULT 25.0 | Alert trigger threshold for farmer notification |
| `updated_at` | `TIMESTAMP` | `TEXT` | YES | - | Last modification timestamp |
| `updated_by` | `BIGINT` | `INTEGER` | YES | - | User ID who modified settings |

---

### 2.4 Table: `pump_commands`
Audit log of all manual and automated pump operations with state confirmation.

| Field Name | MySQL Type | SQLite Type | Nullable | Key / Constraint | Description |
|---|---|---|---|---|---|
| `command_id` | `VARCHAR(50)` | `TEXT` | NO | PRIMARY KEY | Unique command UUID |
| `device_id` | `VARCHAR(50)` | `TEXT` | NO | INDEX (`idx_cmd_device_status`) | Target station ID |
| `command` | `VARCHAR(20)` | `TEXT` | NO | - | `PUMP_ON` or `PUMP_OFF` |
| `duration_seconds` | `INT` | `INTEGER` | YES | - | Scheduled runtime (seconds) |
| `trigger_source` | `VARCHAR(30)` | `TEXT` | NO | - | `MANUAL` or `AUTO_HYSTERESIS` |
| `requested_state` | `BOOLEAN` | `INTEGER` | NO | - | Intended state (`true` = ON, `false` = OFF) |
| `confirmed_state` | `BOOLEAN` | `INTEGER` | YES | - | Relay feedback confirmed by ESP32 |
| `status` | `VARCHAR(30)` | `TEXT` | NO | DEFAULT 'PENDING' | `PENDING`, `EXECUTED`, `REJECTED_COOLDOWN`, `TIMEOUT` |
| `failure_reason` | `VARCHAR(255)` | `TEXT` | YES | - | Failure explanation if rejected |
| `created_at` | `TIMESTAMP` | `TEXT` | YES | DEFAULT CURRENT_TIMESTAMP | Dispatch timestamp |
| `executed_at` | `TIMESTAMP` | `TEXT` | YES | - | Acknowledged execution timestamp |

---

### 2.5 Table: `iot_alerts`
Active and resolved alarms generated by thresholds or system failures.

| Field Name | MySQL Type | SQLite Type | Nullable | Key / Constraint | Description |
|---|---|---|---|---|---|
| `id` | `VARCHAR(50)` | `TEXT` | NO | PRIMARY KEY | Unique alert ID |
| `device_id` | `VARCHAR(50)` | `TEXT` | NO | - | Station ID |
| `farmer_id` | `BIGINT` | `INTEGER` | NO | - | Farmer recipient ID |
| `alert_type` | `VARCHAR(50)` | `TEXT` | NO | - | `LOW_SOIL_MOISTURE`, `SENSOR_FAULT`, etc. |
| `severity` | `VARCHAR(20)` | `TEXT` | NO | - | `LOW`, `MEDIUM`, `HIGH`, `CRITICAL` |
| `title` | `VARCHAR(150)` | `TEXT` | NO | - | Alert heading |
| `message` | `TEXT` | `TEXT` | NO | - | Advisory text |
| `is_resolved` | `BOOLEAN` | `INTEGER` | YES | DEFAULT 0 | Resolution status |
| `created_at` | `TIMESTAMP` | `TEXT` | YES | DEFAULT CURRENT_TIMESTAMP | Occurrence timestamp |

---

## 3. Seed Data

Default test station pre-seeded in both MySQL `data.sql` and SQLite `AgroSmartDbHelper.java`:
- **Station**: `ESP32-AGRO-01`
- **Station Name**: `Smart Station Baramati Plot A`
- **Location**: `Baramati North Field (Soybean Block)`
- **Owner ID**: `1` (`Arya Farmer`, `9876543210`)
- **API Key**: `AGRO-ESP32-SECURE-KEY-2026`
- **Operating Mode**: `MANUAL`
- **Start Threshold**: $30.0\%$
- **Stop Threshold**: $70.0\%$
- **Max Runtime**: $300\text{ sec}$ ($5\text{ min}$)
- **Cooldown**: $600\text{ sec}$ ($10\text{ min}$)
- **Sample Readings**: 4 historical readings ($42.5\%$, $38.0\%$, $33.2\%$, $28.5\%$) demonstrating soil dry-down trend.
