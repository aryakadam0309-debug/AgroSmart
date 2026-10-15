# AgroSmart — IoT Hardware & Software Setup Guide

This guide walks you step-by-step through setting up, configuring, flashing, and running the physical ESP32 station and the AgroSmart Android application.

---

## 1. Hardware Requirements & Bill of Materials (BOM)

| Component | Specification | Quantity | Purpose |
|---|---|---|---|
| **ESP32 DevKit V1** | 30 or 38 Pin (Dual-Core, 2.4GHz Wi-Fi + BLE) | 1 | IoT Station Microcontroller |
| **Capacitive Soil Moisture Sensor** | v1.2 Corrosion Resistant Analog Sensor | 1 | Measures volumetric water content |
| **DHT22 (AM2302) Sensor** | Digital Temperature & Humidity Sensor | 1 | Microclimate monitoring |
| **5V Relay Module** | 1-Channel Optocoupler Isolated (Active LOW) | 1 | Controls submersible pump motor |
| **Submersible Water Pump** | 5V - 12V DC Mini Submersible Pump + Tubing | 1 | Irrigation water delivery |
| **Pull-up Resistor** | $10\text{k}\Omega$ Resistor | 1 | Pull-up for DHT22 Data pin |
| **Jumper Wires & Breadboard** | Male-to-Female / Male-to-Male | 1 set | Circuit interconnects |
| **USB Cable** | Micro-USB or Type-C (Data capable) | 1 | Flashing and Serial Monitor |

---

## 2. Wiring & Pinout Connection Table

| Sensor / Module Pin | ESP32 Pin | Voltage / Type | Notes |
|---|---|---|---|
| **Capacitive Moisture VCC** | `3V3` | 3.3V DC Power | Stable reference voltage |
| **Capacitive Moisture GND** | `GND` | Ground | Common ground |
| **Capacitive Moisture AOUT**| `GPIO 34` | Analog Input (ADC1_CH6)| 12-bit ADC ($0 - 4095$) |
| **DHT22 VCC** | `3V3` or `VIN` | 3.3V or 5V DC | - |
| **DHT22 GND** | `GND` | Ground | Common ground |
| **DHT22 DATA** | `GPIO 4` | Digital I/O | Connect $10\text{k}\Omega$ between DATA and VCC |
| **Relay Module VCC** | `VIN` | 5V DC Power | Power for optocoupler and coil |
| **Relay Module GND** | `GND` | Ground | Common ground |
| **Relay Module IN** | `GPIO 18` | Digital Output | Active LOW control signal |
| **Relay Module COM & NO** | Pump Circuit | Switched Loop | Connect pump in series with power supply |
| **Onboard Blue LED** | `GPIO 2` | Digital Output | Network status / activity blinker |

---

## 3. ESP32 Firmware Configuration & Flashing

### Step 3.1: Open Arduino IDE
1. Install the **ESP32 Board Package** in Arduino IDE:
   - File $\rightarrow$ Preferences $\rightarrow$ Additional Board Manager URLs:
     `https://raw.githubusercontent.com/espressif/arduino-esp32/gh-pages/package_esp32_index.json`
   - Tools $\rightarrow$ Board $\rightarrow$ Boards Manager $\rightarrow$ Search `esp32` $\rightarrow$ Install.
2. Select Board: **Tools $\rightarrow$ Board $\rightarrow$ ESP32 Arduino $\rightarrow$ DOIT ESP32 DEVKIT V1**.
3. Select Port: **Tools $\rightarrow$ Port $\rightarrow$ COMx** (the Silicon Labs / CH340 serial port).

### Step 3.2: Configure `config.h`
Open `AgroSmartFirmware/config.h`:
```c
// Set your 2.4GHz Wi-Fi credentials
#define WIFI_SSID           "Your_WiFi_Hotspot"
#define WIFI_PASSWORD       "Your_WiFi_Password"

// Set your computer's LAN IP address running the Spring Boot backend
// (Find via 'ipconfig' in Windows terminal, e.g. 192.168.1.15)
#define BACKEND_BASE_URL    "http://192.168.1.15:8080/api/iot"
```

### Step 3.3: Flash Firmware
1. Open `AgroSmartFirmware/AgroSmart_ESP32_Firmware.ino`.
2. Click **Upload** (Ctrl + U).
3. If necessary, hold the **BOOT** button on the ESP32 while "Connecting..." appears in the console.
4. Open **Serial Monitor** at **115200 baud** to view real-time initialization and Wi-Fi connection.

---

## 4. Running the Spring Boot Backend

1. Navigate to the backend directory:
   ```cmd
   cd C:\Users\Arya\.gemini\antigravity\scratch\AgroSmartBackend
   ```
2. Start the backend with Java 17:
   ```cmd
   set JAVA_HOME=C:\Program Files\Android\Android Studio\jbr
   .\gradlew.bat bootRun
   ```
3. Verify backend health in browser:
   `http://localhost:8080/api/iot/status/ESP32-AGRO-01`

---

## 5. Running the AgroSmart Android App

1. Launch Android Studio and open the project at `C:\Users\Arya\.gemini\antigravity\scratch\AgroSmart`.
2. Ensure your Android emulator or physical device is connected.
   - **Emulator Note**: The app automatically talks to `http://10.0.2.2:8080/api/` (emulator loopback to host).
   - **Physical Phone Note**: Ensure the phone and computer are on the same Wi-Fi network.
3. Click **Run 'app'** or install the pre-compiled APK:
   ```cmd
   adb install -r C:\Users\Arya\.gemini\antigravity\scratch\AgroSmart\app\build\outputs\apk\debug\app-debug.apk
   ```
4. On the Home screen or Services tab, tap **IoT Smart Farming (ESP32 LIVE)**.
5. The IoT Dashboard will display real-time soil moisture, microclimate stats, pump controls, and recent readings!
