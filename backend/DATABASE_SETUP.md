# AgroSmart – Database & Backend Setup Guide

Welcome to the **AgroSmart – Smart Agriculture Management System** Database & Backend Developer Guide.
This document provides complete instructions for developers, evaluators, and administrators to configure, run, inspect, and test the MySQL database and Spring Boot backend.

---

## 1. System Requirements

- **Operating System**: Windows 10/11, Linux (Ubuntu/Debian), or macOS
- **Java**: JDK 17 or JDK 21 (Eclipse Temurin / Oracle / OpenJDK)
- **Database Engine**: MySQL Server 8.0+ or MariaDB 10.6+
- **Database Client**: MySQL Workbench, DBeaver, HeidiSQL, or MySQL CLI
- **Build Tool**: Gradle (wrapper included) or Maven
- **Port Availability**: `8080` (Backend API), `3306` (MySQL Server)

---

## 2. Database Connection Parameters

| Parameter | Default Local Value | Environment Variable |
|---|---|---|
| **Host** | `localhost` / `127.0.0.1` | `DB_HOST` |
| **Port** | `3306` | `DB_PORT` |
| **Database Name** | `agrosmart_db` | `DB_NAME` |
| **Username** | `root` | `DB_USERNAME` |
| **Password** | `root` (or your local MySQL password) | `DB_PASSWORD` |
| **JDBC Driver** | `com.mysql.cj.jdbc.Driver` | - |
| **JDBC URL** | `jdbc:mysql://localhost:3306/agrosmart_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Kolkata` | - |

> [!TIP]
> **No Hardcoded Secrets**: You can override any credential at runtime using Windows PowerShell environment variables:
> ```powershell
> $env:DB_PASSWORD="your_custom_password"
> $env:DB_USERNAME="root"
> $env:PORT="8080"
> ```

---

## 3. Database Initialization (Manual or Automatic)

### Option A: Automatic Initialization (Recommended)
Spring Boot will automatically execute `schema.sql` and `data.sql` located in `src/main/resources/` upon starting because `spring.sql.init.mode=always` is configured in `application.properties`.

### Option B: Manual Initialization via MySQL Workbench / CLI
1. Open MySQL Workbench or Windows Terminal:
   ```powershell
   mysql -u root -p
   ```
2. Enter your MySQL password.
3. Run the schema creation script:
   ```sql
   SOURCE C:/Users/Arya/.gemini/antigravity/scratch/AgroSmartBackend/src/main/resources/schema.sql;
   SOURCE C:/Users/Arya/.gemini/antigravity/scratch/AgroSmartBackend/src/main/resources/data.sql;
   ```
4. Verify created tables:
   ```sql
   USE agrosmart_db;
   SHOW TABLES;
   ```

---

## 4. All 14 Database Tables & Descriptions

1. `users`: Registered farmers and administrators with BCrypt-hashed passwords, role flags, phone numbers, and landholding acreage.
2. `crop_prices`: Mandi Bhav APMC market rate cache with minimum, maximum, modal price, arrival volume, and price change direction.
3. `crop_guides`: ICAR-compliant agronomic cultivation guides including sowing windows, watering stages, and fertilizer schedules.
4. `crop_diseases`: Encyclopedic plant disease directory (symptoms, organic remedies, chemical fungicides/insecticides) - **Strictly non-ML/no AI**.
5. `tractors`: Available tractor fleet & farm machinery listings with HP, owner contact, daily/hourly rates, and implements.
6. `tractor_bookings`: Farmer booking transactions with rental date, duration, total price, and fulfillment status.
7. `fertilizer_inventory`: On-farm fertilizer stock tracking (Urea, DAP, NPK, Vermicompost) with low-stock alerts.
8. `crop_harvest_inventory`: Post-harvest godown storage and sales log (quintals harvested, sold, and remaining in storage).
9. `products`: E-Commerce marketplace catalog (certified seeds, fertilizers, crop protection tools, sprayers).
10. `orders`: Marketplace checkout orders with delivery address, total price, and payment method (Razorpay / Cash on Delivery).
11. `order_items`: Line items linked to each marketplace order.
12. `government_schemes`: State and central agricultural schemes (PM-KISAN, PMFBY, SMAM) with eligibility rules and subsidy amounts.
13. `agri_news`: Real-time agricultural bulletin articles, policy updates, and weather advisories.
14. `notifications`: In-app advisory inbox, storm/frost weather warnings, and booking status alerts.

---

## 5. How to Start the Spring Boot Backend

### Method 1: Using Gradle Wrapper (Terminal)
Open PowerShell in the backend directory:
```powershell
cd C:\Users\Arya\.gemini\antigravity\scratch\AgroSmartBackend
.\gradlew.bat bootRun
```

### Method 2: In IntelliJ IDEA / Eclipse / VS Code
1. Open folder `C:\Users\Arya\.gemini\antigravity\scratch\AgroSmartBackend`.
2. Locate `src/main/java/com/agrosmart/backend/AgroSmartBackendApplication.java`.
3. Right click -> **Run 'AgroSmartBackendApplication'**.

The console will display:
```
Started AgroSmartBackendApplication in 3.2 seconds (process running for 3.8)
Tomcat started on port 8080 (http) with context path '/'
```

---

## 6. How Android Communicates with the Backend

- **Android Emulator**:
  The Android emulator connects to the host machine's backend via the special loopback IP:
  `http://10.0.2.2:8080/api/`
  (Configured in `com.agrosmart.network.ApiClient`).

- **Physical Android Device (via Wi-Fi / USB)**:
  1. Find your laptop/PC IP using `ipconfig` (e.g. `192.168.1.15`).
  2. In `com.agrosmart.network.ApiClient`, update `BASE_URL = "http://192.168.1.15:8080/api/";`.
  3. Ensure Windows Firewall allows incoming connections on port `8080`.

- **Offline Mode**:
  If the backend is not running, the mobile app operates seamlessly using local SQLite database (`AgroSmartDbHelper`) so farmers never face a blank screen or crash!

---

## 7. Pre-Seeded Test Credentials

| Role | Mobile Number | Email | Password | Dashboard |
|---|---|---|---|---|
| **Farmer (Default)** | `9876543210` | `farmer@agrosmart.in` | `123456` | Farmer Dashboard |
| **Admin (Official)** | `9999999999` | `admin@agrosmart.in` | `admin123` | Admin Dashboard |
| **Tractor Owner** | `9822334455` | `ramesh@patil.in` | `123456` | Farmer & Tractor Fleet |

---

## 8. API Verification with cURL / Postman

### A. Health & Mandi Prices
```bash
curl -X GET http://localhost:8080/api/crops/prices
```

### B. Tractor Fleet Listing
```bash
curl -X GET http://localhost:8080/api/tractors
```

### C. List a New Tractor for Rent
```bash
curl -X POST http://localhost:8080/api/tractors \
  -H "Content-Type: application/json" \
  -d '{
    "modelName": "Mahindra Yuvo Tech+ 585",
    "brand": "Mahindra",
    "hp": "49 HP",
    "hourlyRate": 850.0,
    "dailyRate": 6000.0,
    "implementsIncluded": "Rotavator, Reversible Plough",
    "ownerName": "Arya Farmer",
    "ownerPhone": "9876543210",
    "location": "Baramati, Pune",
    "isAvailable": true,
    "rating": 5.0
  }'
```

### D. User Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "identifier": "9876543210",
    "password": "123456",
    "role": "ROLE_FARMER"
  }'
```

---

## 9. Troubleshooting Guide

1. **`Communications link failure: Connection refused`**:
   - Check if MySQL service is running: Open Windows Services (`services.msc`) -> Ensure `MySQL80` service is **Running**.
   - Verify port `3306` is open.

2. **`Access denied for user 'root'@'localhost'`**:
   - Update `spring.datasource.password` in `application.properties` or set `$env:DB_PASSWORD="your_password"`.

3. **`Port 8080 already in use`**:
   - Find process: `netstat -ano | findstr :8080`
   - Kill process: `taskkill /PID <PID> /F` or change `server.port=8081` in `application.properties`.
