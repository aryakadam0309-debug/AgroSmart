# AgroSmart – Comprehensive QA & Evaluation Testing Checklist

This checklist provides a structured, end-to-end verification protocol for **AgroSmart – Smart Agriculture Management System** (Android App + Spring Boot REST Backend + MySQL Database).

---

## 1. Authentication & Session Validation

- [ ] **Test Case 1.1: Registration with Empty Fields**
  - **Action**: Tap "Register", leave fields blank, tap "Register".
  - **Expected Result**: Validation errors appear under Full Name ("letters only, min 2 chars") and Mobile ("Enter valid 10-digit Indian mobile number"). No record is created.

- [ ] **Test Case 1.2: Invalid Indian Mobile Number**
  - **Action**: Enter `12345` or `0123456789` in Mobile field.
  - **Expected Result**: Validation rejects input with clear helper text. Indian numbers must start with 6, 7, 8, or 9 and contain exactly 10 digits.

- [ ] **Test Case 1.3: Valid Farmer Registration**
  - **Action**: Enter Full Name "Ramesh Shinde", Phone "9822112233", Password "pass123", Location "Shirur, Pune", Farm Size "4.5".
  - **Expected Result**: Account registers successfully, session stores credentials in `SessionManager` & SQLite `users` table, redirects directly to `FarmerDashboardActivity`.

- [ ] **Test Case 1.4: Farmer Login with Pre-Seeded Account**
  - **Action**: In Login screen, select "Farmer" role. Enter Phone `9876543210` and Password `123456`.
  - **Expected Result**: Toast "Welcome Arya Farmer", opens Farmer Dashboard with bottom navigation.

- [ ] **Test Case 1.5: Role Isolation Enforcement**
  - **Action**: Select "Admin" role on login screen and attempt to enter farmer credentials.
  - **Expected Result**: Login rejected with "Invalid credentials". Farmer cannot enter Admin dashboard; Admin cannot enter Farmer dashboard.

- [ ] **Test Case 1.6: Forgot Password Flow**
  - **Action**: On Login screen, tap "Forgot Password?". Enter registered mobile `9876543210` and new password `newpass123`.
  - **Expected Result**: Success alert dialog appears; password updates in database; user can now login with `newpass123`.

---

## 2. Multilingual Support (English, Hindi, Marathi)

- [ ] **Test Case 2.1: Language Switcher from Profile**
  - **Action**: Navigate to Profile tab -> tap "Change Language (भाषा बदला)".
  - **Expected Result**: Language selection screen opens. Selecting "मराठी (Marathi)" immediately changes all navigation labels, headers, and buttons to Marathi.
- [ ] **Test Case 2.2: Hindi Language Verification**
  - **Action**: Select "हिन्दी (Hindi)".
  - **Expected Result**: Headers update to "एग्रोस्मार्ट", "मौसम का पूर्वानुमान", "मंडी भाव", "ट्रैक्टर बुकिंग".

---

## 3. Tractor Rental & "Rent Out My Tractor" Feature

- [ ] **Test Case 3.1: Browse Fleet with Brand Chips**
  - **Action**: From Dashboard or Services, open "Tractor Booking". Tap "Mahindra", "John Deere", "Swaraj".
  - **Expected Result**: Fleet list filters dynamically according to selected brand chip.

- [ ] **Test Case 3.2: Book a Tractor**
  - **Action**: Select "Mahindra 575 DI XP Plus", tap "Book Now". Select 4 hours duration, enter farm location "Plot 12, Baramati".
  - **Expected Result**: Estimated cost calculates automatically (`4 * ₹800 = ₹3,200`). Booking is saved and appears in "Bookings" tab with `CONFIRMED` status.

- [ ] **Test Case 3.3: Rent Out My Tractor (Owner Listing Flow)**
  - **Action**: In Tractor Rental screen, tap bottom Floating Action Button `+ Rent Out Tractor` (or from Services tab `Rent Out My Tractor`).
  - **Expected Result**: Opens `AddTractorActivity`.
  - **Form Validation**:
    - Enter Brand: "Sonalika"
    - Model: "DI 750 III Sikander"
    - Power: "55 HP"
    - Reg No: "MH12XY4567"
    - Hourly Rate: "900"
    - Daily Rate: "6500"
    - Implements: "Rotavator, Heavy Cultivator"
    - Location: "Baramati, Pune"
  - **Submit**: Tap "Publish Tractor Listing".
  - **Expected Result**: Success message appears, tractor listing is saved to local SQLite & in-memory fleet, and immediately shows up in the fleet browser!

---

## 4. Farm Operations & Offline Inventory

- [ ] **Test Case 4.1: Fertilizer Inventory Management**
  - **Action**: Open Services -> "Fertilizer Inventory".
  - **Verify**: Current stock for Urea, DAP, NPK, Vermicompost with low-stock badges.
  - **Action**: Tap "+" to add new batch: "Zinc Sulphate 21%", 10 bags, ₹850/bag.
  - **Expected Result**: Item added to inventory list and stored in SQLite database.

- [ ] **Test Case 4.2: Crop Harvest Godown Storage**
  - **Action**: Open Services -> "Crop Inventory".
  - **Verify**: Harvested batches for Soybean, Wheat, Onion with remaining quintals.
  - **Action**: Tap "Record Sale" on Soybean lot -> enter 10 Quintals sold.
  - **Expected Result**: Remaining stock updates immediately (`50 - 10 = 40 Qtl`).

---

## 5. Agronomic Advisory & Knowledge Modules (Strictly Non-ML)

- [ ] **Test Case 5.1: Mandi Bhav (Live APMC Prices)**
  - **Action**: Open "Crop Prices" from dashboard.
  - **Verify**: Real-time cards for Soybean, Wheat, Cotton, Onion, Chana with price trends (`UP`, `DOWN`, `STABLE`).
  - **Action**: Type "Onion" in search bar.
  - **Expected Result**: Filtered results show Lasalgaon Mandi with modal price ₹2,150/Qtl.

- [ ] **Test Case 5.2: Crop Cultivation Guide**
  - **Action**: Open "Crop Guide" -> select "Wheat (गेहूं)".
  - **Expected Result**: Agronomic parameters (sowing window Nov 1-25, critical CRI irrigation stage at 21 days, NPK dosage 120:60:40 kg/ha).

- [ ] **Test Case 5.3: Plant Pathology Directory (Strictly Non-AI)**
  - **Action**: Open "Crop Diseases".
  - **Verify**: Scientific search for Yellow Rust, Charcoal Rot, Bacterial Blight.
  - **Expected Result**: Full ICAR-compliant recommendations: causal organism, visual symptoms, organic remedies, and approved chemical controls. **No camera permissions requested; strictly encyclopedic**.

---

## 6. Agri Marketplace & Razorpay Payment Integration

- [ ] **Test Case 6.1: Product Catalog & Add to Cart**
  - **Action**: Open "Agri Marketplace" -> Add "Certified Sharbati Wheat Seeds (40 Kg Bag)" to cart.
  - **Expected Result**: Cart badge increments.

- [ ] **Test Case 6.2: Cart & Checkout**
  - **Action**: Open Cart -> Tap "Proceed to Checkout".
  - **Expected Result**: Checkout summary calculates subtotal, GST, and shipping.
  - **Action**: Select "Razorpay Online Payment" or "Cash on Delivery" -> Confirm Order.
  - **Expected Result**: Launches Razorpay checkout interface / confirms order, and opens `OrderSuccessActivity` with order ID `ORD-...`.

---

## 7. Offline Resilience & Reliability

- [ ] **Test Case 7.1: Zero-Network Simulation**
  - **Action**: Put phone/emulator in Airplane Mode. Launch AgroSmart.
  - **Expected Result**: App opens instantly without crashing. Local SQLite database (`AgroSmartDbHelper`) populates tractors, inventory, guides, and diseases offline.
