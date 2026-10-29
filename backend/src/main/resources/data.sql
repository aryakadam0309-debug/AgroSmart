-- ==============================================================================
-- AGROSMART SEED DATA (Authentic Agricultural Dataset for Demo & Evaluation)
-- ==============================================================================

USE `agrosmart_db`;

-- 1. Default Users (Password: '123456' / 'admin123' hashed via BCrypt)
INSERT INTO `users` (`id`, `full_name`, `phone`, `email`, `password`, `role`, `village`, `district`, `state`, `land_area_acres`)
VALUES 
(1, 'Arya Farmer', '9876543210', 'farmer@agrosmart.in', '$2a$10$Eqm17oJz4zY6BwG7hY9vpeO3wS8.n0XvL3rV0u5H7sB9m2P5tQ6k2', 'ROLE_FARMER', 'Mumbai', 'Mumbai Suburban', 'Maharashtra', 5.5),
(2, 'AgroSmart Admin', '9999999999', 'admin@agrosmart.in', '$2a$10$Eqm17oJz4zY6BwG7hY9vpeO3wS8.n0XvL3rV0u5H7sB9m2P5tQ6k2', 'ROLE_ADMIN', 'Headquarters', 'Pune', 'Maharashtra', 0.0)
ON DUPLICATE KEY UPDATE `full_name` = VALUES(`full_name`);

-- 2. Seed Mandi Bhav / Crop Prices
INSERT INTO `crop_prices` (`id`, `crop_name`, `category`, `variety`, `market_name`, `state`, `district`, `min_price`, `max_price`, `modal_price`, `price_unit`, `trend`, `price_change`, `updated_date`, `arrival_quantity`)
VALUES
('CP001', 'Soybean (सोयाबीन)', 'Oilseeds', 'JS-335 Yellow', 'Indore Mandi', 'Madhya Pradesh', 'Indore', 4400, 4950, 4820, '₹ / Qtl', 'UP', 75.0, 'Today, 11:30 AM', '3,400 Quintals'),
('CP002', 'Wheat (गेहूं)', 'Cereals', 'Sharbati Deluxe', 'Pune APMC (Gultekdi)', 'Maharashtra', 'Pune', 2600, 3100, 2850, '₹ / Qtl', 'UP', 40.0, 'Today, 10:45 AM', '5,120 Quintals'),
('CP003', 'Cotton (कपास)', 'Cash Crops', 'BT Shankar-6', 'Rajkot APMC Yard', 'Gujarat', 'Rajkot', 7100, 7850, 7550, '₹ / Qtl', 'DOWN', -120.0, 'Today, 12:15 PM', '1,890 Quintals'),
('CP004', 'Onion (लाल प्याज)', 'Vegetables', 'Nashik Red Premium', 'Lasalgaon APMC', 'Maharashtra', 'Nashik', 1600, 2450, 2150, '₹ / Qtl', 'UP', 150.0, 'Today, 09:30 AM', '12,400 Quintals'),
('CP005', 'Gram / Chana (चना)', 'Pulses', 'Desi Bold Chana', 'Akola APMC', 'Maharashtra', 'Akola', 5600, 6150, 5920, '₹ / Qtl', 'STABLE', 0.0, 'Today, 11:00 AM', '2,350 Quintals'),
('CP006', 'Basmati Rice (चावल)', 'Cereals', '1121 Pusa Basmati', 'Khanna Grain Market', 'Punjab', 'Ludhiana', 3800, 4400, 4200, '₹ / Qtl', 'UP', 60.0, 'Today, 10:15 AM', '4,800 Quintals')
ON DUPLICATE KEY UPDATE `modal_price` = VALUES(`modal_price`);

-- 3. Seed Tractors Fleet
INSERT INTO `tractors` (`id`, `model_name`, `brand`, `hp`, `hourly_rate`, `daily_rate`, `implements_included`, `owner_name`, `owner_phone`, `location`, `is_available`, `rating`)
VALUES
('TR01', 'Mahindra 575 DI XP Plus', 'Mahindra', '47 HP', 800.0, 5800.0, 'Rotavator, 9-Tyne Cultivator, Trolley', 'Ramesh Patil', '9822334455', 'Nashik, Maharashtra', TRUE, 4.9),
('TR02', 'John Deere 5050 D GearPro', 'John Deere', '50 HP', 900.0, 6500.0, 'Heavy-Duty Rotavator, MB Plough', 'Suresh Jagtap', '9822445566', 'Shirur, Pune', TRUE, 4.8),
('TR03', 'Swaraj 855 FE 4WD', 'Swaraj', '52 HP', 950.0, 6800.0, 'Laser Land Leveler, Disk Harrow', 'Vikas Deshmukh', '9822556677', 'Daund, Pune', TRUE, 4.9),
('TR04', 'Sonalika DI 745 III Sikander', 'Sonalika', '50 HP', 850.0, 6000.0, 'Cultivator, Seed Drill, Trolley', 'Ganesh Shinde', '9822667788', 'Niphad, Nashik', TRUE, 4.7),
('TR05', 'Eicher 380 Super DI', 'Eicher', '40 HP', 700.0, 5000.0, 'Rigid Cultivator, Trolley', 'Baburao Kadam', '9822778899', 'Dindori, Nashik', TRUE, 4.6)
ON DUPLICATE KEY UPDATE `hourly_rate` = VALUES(`hourly_rate`);

-- 4. Seed Crop Cultivation Guides
INSERT INTO `crop_guides` (`id`, `crop_name`, `scientific_name`, `season`, `duration`, `soil_type`, `water_requirement`, `temperature`, `expected_yield`, `sowing_window`, `harvesting_window`, `npk_advisory`, `growth_stages`, `best_practices`)
VALUES
('CG01', 'Wheat (गेहूं)', 'Triticum aestivum', 'Rabi', '110-120 Days', 'Well-drained Loamy to Clay Loam', '4-5 Irrigations (Critical: CRI stage at 21 days)', '15°C to 25°C', '45-55 Qtl/Ha', 'Nov 1 - Nov 25', 'Mar 15 - Apr 15', 'N:120, P:60, K:40 kg/ha. Apply full P&K with half N at sowing.', 'Crown Root (21d) -> Tillering (45d) -> Heading (65d) -> Grain Filling (90d)', 'Ensure seed treatment with Trichoderma viride @ 4g/kg seed. Maintain optimal row spacing of 20-22 cm.'),
('CG02', 'Soybean (सोयाबीन)', 'Glycine max', 'Kharif', '95-105 Days', 'Well-drained deep black cotton soil', 'Rainfed (3-4 protective irrigations if dry spell)', '25°C to 32°C', '22-28 Qtl/Ha', 'June 20 - July 10 (after 75-100mm rain)', 'Oct 1 - Oct 20', 'N:30, P:60, K:30, S:20 kg/ha. Inoculate seeds with Rhizobium.', 'Germination (5d) -> Vegetative (25d) -> Flowering (45d) -> Pod development (70d)', 'Broad Bed Furrow (BBF) planting helps prevent waterlogging during heavy monsoon rains.'),
('CG03', 'Cotton (कपास)', 'Gossypium hirsutum', 'Kharif', '160-180 Days', 'Deep black soil (Regur) with good drainage', '6-8 Irrigations (Drip irrigation recommended)', '21°C to 35°C', '25-35 Qtl/Ha', 'May 25 - June 20', 'Nov 15 - Jan 30', 'N:120, P:60, K:60 kg/ha in 3 split doses.', 'Square formation (45d) -> Flowering (75d) -> Boll development (110d) -> Bursting (150d)', 'Install pheromone traps @ 5/ha for early detection of pink bollworm. Nipping at 90 days.')
ON DUPLICATE KEY UPDATE `crop_name` = VALUES(`crop_name`);

-- 5. Seed Crop Diseases Directory (Strictly Non-ML Encyclopedic)
INSERT INTO `crop_diseases` (`id`, `crop_name`, `disease_name`, `causal_organism`, `severity`, `symptoms`, `favorable_conditions`, `preventive_measures`, `organic_remedy`, `chemical_control`)
VALUES
('DIS01', 'Wheat', 'Yellow Rust (Stripe Rust)', 'Puccinia striiformis', 'HIGH', 'Yellow pustules arranged in parallel narrow stripes on leaf blades, powdery spores easily rubbing onto fingers.', 'Cool temperatures (10-15°C) with high humidity and morning dew.', 'Grow resistant varieties like HD-2967, DBW-187. Timely sowing before Nov 15.', 'Spray Neem Oil @ 5ml/liter or Cow urine extract (10%).', 'Spray Propiconazole 25% EC (Tilt) @ 1 ml/liter of water at first symptom appearance.'),
('DIS02', 'Soybean', 'Charcoal Rot', 'Macrophomina phaseolina', 'MEDIUM', 'Ashy discoloration on stem base, premature leaf drying, black microsclerotia under epidermis.', 'Soil temperature > 30°C accompanied by prolonged drought stress during flowering.', 'Deep summer ploughing, crop rotation with sorghum or corn, avoid moisture stress.', 'Trichoderma viride seed treatment @ 5-10g/kg seed.', 'Carbendazim 50% WP @ 2g/kg seed coating prior to sowing.'),
('DIS03', 'Cotton', 'Bacterial Blight (Angular Leaf Spot)', 'Xanthomonas citri pv. malvacearum', 'HIGH', 'Angular water-soaked spots on leaves bounded by veinlets, turning black; black lesion streaks on twigs (blackarm).', 'Warm humid weather (28-32°C), frequent rains with windy conditions.', 'Use acid-delinted certified seeds. Field sanitation and crop residue burning.', 'Spray Pseudomonas fluorescens @ 10g/liter.', 'Spray Copper Oxychloride 50% WP @ 2.5g/L + Streptocycline @ 0.1g/L.')
ON DUPLICATE KEY UPDATE `disease_name` = VALUES(`disease_name`);

-- 6. Seed Marketplace Products
INSERT INTO `products` (`id`, `name`, `category`, `brand`, `price`, `original_price`, `package_size`, `description`, `rating`, `stock_count`)
VALUES
('PR01', 'Certified Sharbati Wheat Seeds (HD-2967)', 'Seeds', 'Mahyco Seeds', 950.0, 1100.0, '40 Kg Bag', 'High tillering, disease resistant, certified germination above 95%.', 4.9, 50),
('PR02', 'JS-335 Certified Soybean Seeds', 'Seeds', 'National Seeds Corp', 1850.0, 2100.0, '30 Kg Bag', 'Early maturing (95 days), drought tolerant, high oil content.', 4.8, 35),
('PR03', 'Bio-Organic Vermicompost Enriched', 'Fertilizers', 'Krishi Organic', 320.0, 400.0, '50 Kg Bag', '100% pure organic humus enriched with beneficial soil microbes.', 4.9, 120),
('PR04', 'Water Soluble NPK 19:19:19', 'Fertilizers', 'IFFCO', 195.0, 240.0, '1 Kg Pack', '100% water-soluble foliar spray fertilizer for vegetative booster.', 4.8, 200),
('PR05', 'Battery Powered Knapsack Sprayer (16L)', 'Tools', 'KisanKraft', 2850.0, 3500.0, '16 Liter Tank', '12V rechargeable battery, dual nozzles, comfortable padded shoulder straps.', 4.9, 25)
ON DUPLICATE KEY UPDATE `price` = VALUES(`price`);

-- 7. Seed Government Schemes (PS 5.3 Verified Data)
DELETE FROM government_schemes;
INSERT INTO government_schemes (
    id, 	itle, ministry, subsidy_amount, eligibility, description, enefits, 
equired_documents,
    pplication_process, official_url, source_url, helpline, category, level, pplicable_state,
    deadline, pplication_status, last_verified_date, min_land_acres, max_land_acres,
    llowed_farmer_categories, llowed_crops, max_annual_income, 
equires_land_ownership
) VALUES
('SCH01', 'PM-KISAN Samman Nidhi', 'Ministry of Agriculture & Farmers Welfare', '₹6,000 / Year', 
 'All landholding farmer families with cultivable land in their names across India',
 'Direct income support of ₹6,000 per year paid in three equal installments of ₹2,000 directly into bank accounts via DBT.',
 'Direct financial grant of ₹6,000 per annum paid in 3 four-monthly cycles. Transferred via Aadhaar-linked DBT directly without middlemen.',
 '1. Aadhaar Card\n2. Land ownership record (7/12 & 8-A extract)\n3. Bank passbook linked with Aadhaar\n4. Active mobile number',
 '1. Visit pmkisan.gov.in -> Farmers Corner -> New Farmer Registration.\n2. Enter Aadhaar and mobile number, verify OTP.\n3. Upload land survey number and 7/12 details.\n4. Self-registration is validated by State Nodal Officer.',
 'https://pmkisan.gov.in', 'https://pmkisan.gov.in/Guidelines.aspx', '155261 / 011-24300606',
 'Financial Assistance', 'CENTRAL', 'ALL_INDIA', 'Ongoing (Continuous)', 'OPEN', '2026-10-25',
 0.1, NULL, 'ALL', 'ALL', NULL, TRUE),

('SCH02', 'Pradhan Mantri Fasal Bima Yojana (PMFBY)', 'Ministry of Agriculture & Farmers Welfare', 'Up to 90% Premium Subsidy',
 'All farmers growing notified food crops, oilseeds, or annual horticultural crops in notified areas',
 'Comprehensive crop insurance covering yield loss from non-preventable natural risks (drought, flood, unseasonal storm, pest attack).',
 'Full sum insured coverage for yield loss, localized disasters, and post-harvest cyclone damage. Farmer pays only nominal premium: 2% for Kharif, 1.5% for Rabi, 5% for commercial/horticultural crops.',
 '1. Sowing Certificate / Crop Sowing Self-Declaration\n2. Land revenue record (7/12) or Tenancy/Sharecropper Agreement\n3. Bank Passbook copy\n4. Aadhaar Card',
 '1. Apply through National Crop Insurance Portal (pmfby.gov.in) before cut-off date.\n2. Non-loanee farmers apply via CSC or authorized bank branch.\n3. Loanee farmers are enrolled automatically by credit-issuing banks.',
 'https://pmfby.gov.in', 'https://pmfby.gov.in/pdf/Revised_Operational_Guidelines.pdf', '14447',
 'Crop Insurance', 'CENTRAL', 'ALL_INDIA', 'Kharif: 31 July / Rabi: 31 Dec', 'OPEN', '2026-10-25',
 NULL, NULL, 'ALL', 'Soybean,Cotton,Wheat,Rice,Maize,Pulses,ALL', NULL, FALSE),

('SCH03', 'Sub-Mission on Agricultural Mechanization (SMAM)', 'Ministry of Agriculture & Farmers Welfare', '40% - 50% Subsidy on Farm Equipment',
 'Small and marginal farmers (<= 5 acres), women, and SC/ST farmers purchasing certified modern machinery',
 'Financial assistance to purchase tractors, power tillers, rotavators, multi-crop threshers, and laser land levelers to promote farm mechanization.',
 '40% subsidy for General category and 50% subsidy for SC/ST, Women, and Small/Marginal farmers on certified agricultural implements from authorized manufacturers.',
 '1. Aadhaar Card\n2. Land holding record showing <= 5.0 acres (7/12 & 8-A extract)\n3. Bank account passbook\n4. Proforma invoice / quotation from authorized machinery dealer\n5. Caste Certificate (for affirmative subsidy tier)',
 '1. Register on DBT Agricultural Mechanization portal (agrimachinery.nic.in).\n2. Select machine type and authorized dealer.\n3. Upload land records and dealer quotation.\n4. Lottery/sanction letter issued by District Agriculture Officer.\n5. Purchase machine and upload tax invoice for DBT subsidy credit.',
 'https://agrimachinery.nic.in', 'https://agrimachinery.nic.in/Guidelines/SMAM_Guidelines.pdf', '011-23382012',
 'Farm Machinery', 'CENTRAL', 'ALL_INDIA', 'Annual Quota Window', 'OPEN', '2026-10-25',
 0.1, 5.0, 'SMALL_MARGINAL,SC_ST,WOMEN', 'ALL', NULL, TRUE),

('SCH04', 'Kisan Credit Card (KCC) Scheme', 'Ministry of Finance & RBI', '4% Effective Concessional Loan Interest',
 'All farmers - individuals/joint cultivators, tenant farmers, oral lessees, sharecroppers, and SHGs',
 'Adequate and timely credit support from the banking system for agricultural crop production and investment needs.',
 'Short term crop loans up to ₹3 Lakh at standard 7% interest rate, with 3% prompt repayment incentive bringing effective interest down to only 4% per annum. No collateral required for loans up to ₹1.6 Lakh.',
 '1. Duly filled KCC application form\n2. Land record documents (7/12) or crop tenancy agreement\n3. Identity Proof (Aadhaar / Voter ID)\n4. Address Proof\n5. 2 Passport size photos',
 '1. Submit KCC one-page form at any Commercial Bank, Regional Rural Bank (RRB), or Cooperative Bank branch.\n2. Digital KCC available through state portals with pre-populated land records.\n3. Card issued within 14 business days.',
 'https://myscheme.gov.in/schemes/kcc', 'https://rbi.org.in/Scripts/NotificationUser.aspx?Id=11488', '1800-180-1551',
 'Agricultural Credit', 'CENTRAL', 'ALL_INDIA', 'Ongoing (Continuous)', 'OPEN', '2026-10-25',
 NULL, NULL, 'ALL', 'ALL', NULL, FALSE),

('SCH05', 'PMKSY - Per Drop More Crop (Micro-Irrigation)', 'Ministry of Agriculture & Farmers Welfare', 'Up to 55% Subsidy for Drip / Sprinkler',
 'All landholding farmers with assured water source (well, tube-well, farm pond)',
 'Promotes water-use efficiency through micro-irrigation technologies (drip and sprinkler systems), saving up to 50% irrigation water and enhancing yields.',
 '55% subsidy of total benchmark cost for Small & Marginal farmers; 45% subsidy for other farmers for installing ISI-marked drip and sprinkler systems.',
 '1. Aadhaar Card\n2. Land ownership record (7/12 extract)\n3. Electricity bill or water source certificate\n4. Drip layout design and quotation from registered micro-irrigation vendor\n5. Bank passbook',
 '1. Apply online via State Agriculture Portal (e.g. Mahadbt in Maharashtra or state PMKSY portal).\n2. Select empanelled micro-irrigation company.\n3. Pre-sanction inspection by Taluka Agriculture Officer.\n4. Installation, geo-tagging, and post-verification before DBT subsidy release.',
 'https://pmksy.gov.in', 'https://pmksy.gov.in/pdmc/Guidelines.aspx', '011-23383370',
 'Irrigation & Water', 'CENTRAL', 'ALL_INDIA', 'Annual Budget Allocation', 'OPEN', '2026-10-25',
 0.5, NULL, 'ALL', 'ALL', NULL, TRUE),

('SCH06', 'Soil Health Card Scheme', 'Ministry of Agriculture & Farmers Welfare', '100% Free Soil Nutrient Testing',
 'All farmers cultivating agricultural land in India',
 'Provides comprehensive soil fertility status report every two years to optimize fertilizer usage and restore soil health.',
 'Free laboratory testing of 12 crucial parameters: Macro-nutrients (N, P, K), Secondary nutrients (S), Micro-nutrients (Zn, Fe, Cu, Mn, Bo), and Physical properties (pH, Electrical Conductivity, Organic Carbon). Includes customized dosage recommendations for 6 major crops.',
 '1. Aadhaar Card or Farmer ID\n2. Land Survey / Gut Number details',
 '1. Soil samples collected from farmer fields (grid basis) by agriculture department staff.\n2. Tested at Soil Testing Laboratories (STLs).\n3. Soil Health Card printed and distributed directly or downloaded from soilhealth.dac.gov.in.',
 'https://soilhealth.dac.gov.in', 'https://soilhealth.dac.gov.in/about-scheme', '011-24305948',
 'Soil Health', 'CENTRAL', 'ALL_INDIA', 'Continuous (Biennial)', 'CONTINUOUS', '2026-10-25',
 NULL, NULL, 'ALL', 'ALL', NULL, FALSE),

('SCH07', 'Dr. Babasaheb Ambedkar Krishi Swavalamban Yojana', 'Department of Agriculture, Govt. of Maharashtra', '100% Grant up to ₹2.5 Lakh for Irrigation',
 'Scheduled Caste (SC) / Nav-Bouddha farmers owning cultivable land between 0.40 to 6.0 hectares with annual income <= ₹1.5 Lakh',
 'State-sponsored scheme for SC farmers in Maharashtra to establish assured irrigation through new wells, solar pumps, and micro-irrigation.',
 '100% financial assistance: New Well up to ₹2,50,000, In-well boring up to ₹20,000, Drip irrigation up to ₹50,000, Sprinkler up to ₹25,000.',
 '1. Caste Certificate issued by competent authority\n2. Land 7/12 and 8-A records\n3. Tahsildar Income Certificate (<= ₹1,50,000/year)\n4. Ground Water Survey Agency (GSDA) feasibility certificate\n5. Bank Passbook and Aadhaar',
 '1. Register and submit application on Mahadbt Farmer Portal (mahadbt.maharashtra.gov.in).\n2. Select Scheme: Dr. Babasaheb Ambedkar Krishi Swavalamban Yojana.\n3. Scrutiny by Taluka Agriculture Officer and online lottery selection.',
 'https://mahadbt.maharashtra.gov.in', 'https://krishi.maharashtra.gov.in', '022-49150800',
 'Irrigation & Water', 'STATE', 'Maharashtra', 'Annual State Quota', 'OPEN', '2026-10-25',
 1.0, 15.0, 'SC_ST', 'ALL', 150000.0, TRUE),

('SCH08', 'Kharif Special Emergency Drought Relief Grant 2025', 'State Disaster Management Authority', '₹6,800 / Hectare Input Subsidy',
 'Farmers in declared drought-affected revenue circles who suffered > 33% crop loss during Kharif 2025',
 'Emergency financial relief compensation for rainfall deficit crop damage.',
 'Ex-gratia DBT compensation of ₹6,800 per hectare for rainfed crops, directly deposited to eligible farmers bank accounts.',
 '1. Crop Damage Panchnama Certificate\n2. Land 7/12 record\n3. Aadhaar linked bank account',
 'Application cycle closed on 15 September 2025 following final district audit disbursements. No new applications are accepted for this fiscal cycle.',
 'https://mahadbt.maharashtra.gov.in', 'https://maharashtra.gov.in/gr/drought_relief_archive_2025.pdf', '1800-120-8040',
 'Disaster Relief', 'STATE', 'Maharashtra', 'Closed on 15 Sep 2025', 'CLOSED', '2026-10-25',
 NULL, NULL, 'ALL', 'ALL', NULL, FALSE);

-- 8. Seed Agri News
INSERT INTO `agri_news` (`id`, `title`, `summary`, `content`, `source`, `category`, `published_date`, `read_time`)
VALUES
('NW01', 'Kharif Sowing Hits Record 109 Million Hectares Nationwide', 'Paddy, pulses, and oilseeds register substantial gains backed by good monsoon distribution.', 'The Ministry of Agriculture reported that total Kharif sowing reached 109.2 million hectares, marked by healthy growth in soybean, maize, and pulse acreages across central and western belts.', 'AgriWatch News', 'Market & Crops', 'Today, 08:30 AM', '3 min read'),
('NW02', 'Government Announces Minimum Support Price (MSP) Hike for Rabi Crops', 'Wheat MSP increased by ₹150 per quintal, mustard and lentil get attractive revisions.', 'The Cabinet Committee on Economic Affairs approved higher MSP for six Rabi crops to ensure remunerative prices to growers and encourage crop diversification.', 'PIB Krishi', 'Policy & Schemes', 'Yesterday, 05:00 PM', '4 min read')
ON DUPLICATE KEY UPDATE `title` = VALUES(`title`);

-- 9. Seed Advisory Notifications
INSERT INTO `notifications` (`id`, `farmer_phone`, `title`, `message`, `timestamp`, `type`, `is_read`)
VALUES
('NT01', '9876543210', 'Weather Advisory: Moderate Rain Alert', 'Forecast predicts 25-35mm rain in Pune/Baramati in next 36 hours. Postpone chemical spraying on open crops.', 'Today, 07:00 AM', 'WEATHER_ALERT', FALSE),
('NT02', '9876543210', 'Mandi Update: Soybean Prices Up ₹75/Qtl', 'Indore and Lasalgaon mandis reported steady upward price trends for yellow soybean JS-335.', 'Yesterday, 02:30 PM', 'PRICE_ALERT', TRUE)
ON DUPLICATE KEY UPDATE `title` = VALUES(`title`);

-- 10. Seed IoT Smart Farming Station (ESP32)
INSERT INTO `iot_devices` (`device_id`, `device_name`, `device_type`, `owner_id`, `farmer_phone`, `field_location`, `api_key`, `firmware_version`, `is_active`, `last_seen`, `created_at`)
VALUES
('ESP32-AGRO-01', 'Smart Station Baramati Plot A', 'ESP32_SMART_STATION', 1, '9876543210', 'Baramati North Field (Soybean Block)', 'AGRO-ESP32-SECURE-KEY-2026', '1.0.0', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE `device_name` = VALUES(`device_name`);

-- 11. Seed Irrigation Settings
INSERT INTO `irrigation_settings` (`device_id`, `farmer_id`, `operating_mode`, `low_moisture_start_threshold`, `high_moisture_stop_threshold`, `max_continuous_pump_sec`, `cooldown_interval_sec`, `alert_threshold_pct`, `updated_at`, `updated_by`)
VALUES
('ESP32-AGRO-01', 1, 'MANUAL', 30.0, 70.0, 300, 600, 25.0, CURRENT_TIMESTAMP, 1)
ON DUPLICATE KEY UPDATE `operating_mode` = VALUES(`operating_mode`);

-- 12. Seed Sensor Readings
INSERT INTO `sensor_readings` (`id`, `device_id`, `farmer_id`, `soil_moisture_pct`, `soil_moisture_raw`, `moisture_status`, `temperature_c`, `humidity_pct`, `sensor_status`, `recorded_at`)
VALUES
('SR-1001', 'ESP32-AGRO-01', 1, 42.5, 2350, 'MODERATE', 28.4, 62.1, 'VALID', DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 45 MINUTE)),
('SR-1002', 'ESP32-AGRO-01', 1, 38.0, 2480, 'MODERATE', 29.1, 59.8, 'VALID', DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 30 MINUTE)),
('SR-1003', 'ESP32-AGRO-01', 1, 33.2, 2620, 'MODERATE', 29.8, 57.2, 'VALID', DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 15 MINUTE)),
('SR-1004', 'ESP32-AGRO-01', 1, 28.5, 2780, 'DRY', 30.5, 54.0, 'VALID', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE `soil_moisture_pct` = VALUES(`soil_moisture_pct`);

-- 13. Seed IoT Alerts
INSERT INTO `iot_alerts` (`id`, `device_id`, `farmer_id`, `alert_type`, `severity`, `title`, `message`, `is_resolved`, `created_at`)
VALUES
('ALT-1001', 'ESP32-AGRO-01', 1, 'LOW_SOIL_MOISTURE', 'MEDIUM', 'Low Soil Moisture Warning', 'Soil moisture at Baramati Plot A has dropped to 28.5%. Consider running irrigation pump.', FALSE, CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE `title` = VALUES(`title`);

