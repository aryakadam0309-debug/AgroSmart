#ifndef CONFIG_H
#define CONFIG_H

// ==============================================================================
// AGROSMART ESP32 SMART FARMING & IRRIGATION STATION CONFIGURATION
// ==============================================================================

// 1. Wi-Fi Credentials
// Replace with your local 2.4 GHz Wi-Fi hotspot or router credentials
#define WIFI_SSID           "AgroSmart_Farm_WiFi"
#define WIFI_PASSWORD       "KisanSmart2026"

// 2. AgroSmart Spring Boot Backend REST Endpoint
// For physical ESP32, replace with host PC's LAN IP (e.g., http://10.10.115.182:8080/api/iot)
#define BACKEND_BASE_URL    "http://10.10.115.182:8080/api/iot"

// 3. Device Station Credentials & Security
#define DEVICE_ID           "ESP32-AGRO-01"
#define DEVICE_API_KEY      "AGRO-ESP32-SECURE-KEY-2026"
#define FARMER_ID           1L
#define FIRMWARE_VERSION    "1.0.0"

// 4. Hardware Pin Allocations
#define PIN_SOIL_MOISTURE   34      // ADC1_CH6 - Capacitive Soil Moisture Sensor Analog Out
#define PIN_DHT22           4       // GPIO 4   - DHT22 Digital Temperature & Humidity Data Pin
#define PIN_RELAY_PUMP      18      // GPIO 18  - 5V Relay Module IN Pin (Controls Submersible Pump)
#define PIN_STATUS_LED      2       // GPIO 2   - ESP32 Onboard Blue LED (Network/Activity indicator)

// Relay Logic Configuration
// Most standard 5V relay modules are ACTIVE LOW (LOW = Relay ON / Energized)
#define RELAY_ACTIVE_LEVEL  LOW     // Set to LOW for Active-Low relay; HIGH for Active-High
#define RELAY_INACTIVE_LEVEL HIGH

// 5. Soil Moisture Sensor Calibration Values
// Capacitive sensor readings vary with soil salinity, depth, and voltage.
// Dry air reading (~3200 ADC @ 3.3V) -> 0% moisture
// Saturated wet soil/water reading (~1400 ADC @ 3.3V) -> 100% moisture
#define SOIL_DRY_ADC        3200
#define SOIL_WET_ADC        1400

// 6. Dual-Threshold Hysteresis & Safety Defaults
#define DEFAULT_START_MOISTURE_PCT   30.0   // Auto pump ON when moisture drops below this
#define DEFAULT_STOP_MOISTURE_PCT    70.0   // Auto pump OFF when target moisture achieved
#define DEFAULT_MAX_RUNTIME_SEC      300    // 5 minutes max continuous runtime safety auto-shutoff
#define DEFAULT_COOLDOWN_SEC         600    // 10 minutes safety cooldown between irrigation runs

// 7. Non-blocking Task Timing Intervals (in Milliseconds)
#define TELEMETRY_INTERVAL_MS        15000  // Transmit sensor data every 15 seconds
#define COMMAND_POLL_INTERVAL_MS     5000   // Poll backend for pump commands every 5 seconds
#define HEARTBEAT_INTERVAL_MS        30000  // Heartbeat ping every 30 seconds

#endif // CONFIG_H
