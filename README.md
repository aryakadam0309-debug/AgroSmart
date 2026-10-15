# AgroSmart – Smart Agriculture Management System & IoT Module

**AgroSmart** is an enterprise-grade, native Android application paired with a Spring Boot 3 REST backend, MySQL 8 database, local offline SQLite engine, and ESP32 physical IoT farming station. Designed specifically for Indian farmers and agricultural communities, it provides complete crop lifecycle management, market intelligence, machinery booking, and automated precision irrigation.

---

## 🌟 System Features Overview

### 1. IoT Smart Farming & Precision Irrigation (New Module)
- **Soil Moisture Monitoring**: Continuous capacitive sensor sampling on ESP32 analog `GPIO 34`, calibrated mapping ($0 - 100\%$), noise filtering, and status categorization (`DRY`, `MODERATE`, `WET`).
- **Microclimate Tracking**: DHT22 digital ambient temperature (°C) and relative humidity (%) monitoring on `GPIO 4`.
- **Smart Pump Control**: Optocoupler-isolated relay actuator on `GPIO 18`. Supports manual power toggling and automatic dual-threshold hysteresis control (Start at $<30\%$, Stop at $\ge 70\%$).
- **Fail-Safe Safety Systems**: Maximum continuous runtime auto-shutoff (5 minutes) and mandatory rest cooldown (10 minutes).
- **Dual-State Integrity**: Explicitly reports **Requested State** vs. **Confirmed Relay State**.
- **Hardware Integration**: Full Arduino C++ firmware sketch (`AgroSmartFirmware/AgroSmart_ESP32_Firmware.ino`) with configurable network and pin headers (`config.h`).

### 2. Core Agricultural Management (22 Existing Features)
1. **Weather Forecast**: Real-time OpenWeatherMap API integration, 5-day forecasts, agricultural advisories.
2. **Crop Prices (Mandi Bhav)**: Live APMC modal prices, min/max rates, price trends (UP, DOWN, STABLE).
3. **Government Schemes Directory**: Central/state agricultural subsidies, eligibility criteria, application links.
4. **Agriculture News & Bulletins**: Real-time market trends, monsoon updates, agricultural policy alerts.
5. **Crop Cultivation Guides**: Encyclopedic agronomic guidance (sowing windows, soil types, NPK advisories).
6. **Crop Disease Directory**: Non-ML symptom checklists, preventive cultural practices, and chemical/organic remedies.
7. **Tractor & Machinery Rental Fleet**: Browse local tractors, view implement configurations, hourly/daily rates.
8. **Tractor Booking Engine**: Reserve machinery, compute rental fees, confirm bookings.
9. **Tractor Owner Listing**: Allows tractor owners to list implements and machinery for hire.
10. **Fertilizer Inventory Tracker**: Manage godown stock, bags, weights, purchase costs, and low-stock alerts.
11. **Harvest Godown Inventory**: Log harvested crops, sold quantities, remaining quintals, and market valuations.
12. **Agri Marketplace**: E-commerce catalog of certified seeds, organic fertilizers, tools, and sprayers.
13. **Shopping Cart & Checkout**: Add products, adjust quantities, review order totals.
14. **Razorpay Payment Integration**: Secure digital checkout with dummy test sandbox verification.
15. **Order Tracking & History**: Track order confirmations and transaction records.
16. **Advisory Notifications**: Alerts for weather extremes, mandi price shifts, and irrigation requirements.
17. **Farmer Profile Management**: Farm acreage, location, contact information, and role management.
18. **Admin Dashboard**: System telemetry, total farmers, registered tractors, orders, and station counts.
19. **Role-Based Authentication**: Secure BCrypt authentication (`ROLE_FARMER` and `ROLE_ADMIN`).
20. **Offline SQLite Engine**: 100% functionality without internet connection via `AgroSmartDbHelper`.
21. **Multilingual Localization**: Native support for **English**, **हिन्दी (Hindi)**, and **मराठी (Marathi)**.
22. **Material 3 UI Design**: Custom agricultural color scheme (Deep Forest Green, Harvest Gold, Earth Brown).

---

## 🛠️ Technology Stack

| Layer | Technologies |
|---|---|
| **Mobile App (Android)** | Java 17, XML Layouts, MVVM, ViewBinding, Retrofit 2, OkHttp 3, Material Components 3 |
| **Backend Server** | Java 17, Spring Boot 3.3.3, Spring Data JPA, Spring Security, Hibernate |
| **Database Tier** | Dual-Tier: MySQL 8.x (Remote Backend) + SQLite 3 (Android Local Offline Cache) |
| **IoT Firmware** | Arduino C++, ESP32 DevKit V1, HTTPClient, Capacitive Sensor v1.2, DHT22, 5V Relay |

---

## 🚀 Quick Start Guide

### 1. Compile & Install Android App
```cmd
cd C:\Users\Arya\.gemini\antigravity\scratch\AgroSmart
set JAVA_HOME=C:\Program Files\Android\Android Studio\jbr
.\gradlew.bat assembleDebug --no-daemon
```
The output APK is generated at: `app\build\outputs\apk\debug\app-debug.apk` (8.12 MB).

### 2. Run Spring Boot Backend
```cmd
cd C:\Users\Arya\.gemini\antigravity\scratch\AgroSmartBackend
set JAVA_HOME=C:\Program Files\Android\Android Studio\jbr
.\gradlew.bat bootRun
```
Endpoints live at: `http://localhost:8080/api/`

### 3. Flash ESP32 Firmware
1. Open `C:\Users\Arya\.gemini\antigravity\scratch\AgroSmartFirmware\AgroSmart_ESP32_Firmware.ino` in Arduino IDE.
2. Edit Wi-Fi credentials and PC LAN IP in `config.h`.
3. Select board `DOIT ESP32 DEVKIT V1` and flash to device.

---

## 📚 Complete Project Documentation Files
- **`IOT_MODULE_DOCUMENTATION.md`**: Complete technical and architectural specification of the IoT module.
- **`IOT_DATABASE_DOCUMENTATION.md`**: Schemas, data dictionaries, indexes, and dual-database sync details.
- **`IOT_TESTING_CHECKLIST.md`**: End-to-end verification checklist and pass/fail matrix.
- **`IOT_SETUP_GUIDE.md`**: Wiring diagrams, calibration instructions, and run instructions.
- **`DATABASE_SETUP.md`**: Backend MySQL configuration and developer access setup.
