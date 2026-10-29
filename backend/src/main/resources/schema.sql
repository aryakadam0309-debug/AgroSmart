-- ==============================================================================
-- AGROSMART DATABASE DDL SCHEMA (MySQL 8.x)
-- ==============================================================================

CREATE DATABASE IF NOT EXISTS `agrosmart_db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `agrosmart_db`;

-- 1. Users Table (Farmers & Admins)
CREATE TABLE IF NOT EXISTS `users` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `full_name` VARCHAR(100) NOT NULL,
    `phone` VARCHAR(15) NOT NULL UNIQUE,
    `email` VARCHAR(100) UNIQUE,
    `password` VARCHAR(255) NOT NULL,
    `role` VARCHAR(20) NOT NULL DEFAULT 'ROLE_FARMER',
    `village` VARCHAR(100),
    `district` VARCHAR(100),
    `state` VARCHAR(100) DEFAULT 'Maharashtra',
    `land_area_acres` DOUBLE DEFAULT 0.0,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 2. Mandi Bhav / Crop Prices Table
CREATE TABLE IF NOT EXISTS `crop_prices` (
    `id` VARCHAR(30) PRIMARY KEY,
    `crop_name` VARCHAR(100) NOT NULL,
    `category` VARCHAR(50) NOT NULL,
    `variety` VARCHAR(100),
    `market_name` VARCHAR(150) NOT NULL,
    `state` VARCHAR(100) NOT NULL,
    `district` VARCHAR(100),
    `min_price` DOUBLE NOT NULL,
    `max_price` DOUBLE NOT NULL,
    `modal_price` DOUBLE NOT NULL,
    `price_unit` VARCHAR(20) DEFAULT '₹ / Qtl',
    `trend` VARCHAR(10) DEFAULT 'STABLE',
    `price_change` DOUBLE DEFAULT 0.0,
    `updated_date` VARCHAR(50),
    `arrival_quantity` VARCHAR(50)
) ENGINE=InnoDB;

-- 3. Crop Cultivation Guides Table
CREATE TABLE IF NOT EXISTS `crop_guides` (
    `id` VARCHAR(30) PRIMARY KEY,
    `crop_name` VARCHAR(100) NOT NULL,
    `scientific_name` VARCHAR(150),
    `season` VARCHAR(30) NOT NULL,
    `duration` VARCHAR(50),
    `soil_type` VARCHAR(150),
    `water_requirement` VARCHAR(150),
    `temperature` VARCHAR(50),
    `expected_yield` VARCHAR(100),
    `sowing_window` VARCHAR(100),
    `harvesting_window` VARCHAR(100),
    `npk_advisory` TEXT,
    `growth_stages` TEXT,
    `best_practices` TEXT
) ENGINE=InnoDB;

-- 4. Crop Disease Directory Table (Strictly NO AI/ML)
CREATE TABLE IF NOT EXISTS `crop_diseases` (
    `id` VARCHAR(30) PRIMARY KEY,
    `crop_name` VARCHAR(100) NOT NULL,
    `disease_name` VARCHAR(150) NOT NULL,
    `causal_organism` VARCHAR(150),
    `severity` VARCHAR(20) DEFAULT 'MEDIUM',
    `symptoms` TEXT,
    `favorable_conditions` TEXT,
    `preventive_measures` TEXT,
    `organic_remedy` TEXT,
    `chemical_control` TEXT
) ENGINE=InnoDB;

-- 5. Tractors & Equipment Fleet Table
CREATE TABLE IF NOT EXISTS `tractors` (
    `id` VARCHAR(30) PRIMARY KEY,
    `model_name` VARCHAR(150) NOT NULL,
    `brand` VARCHAR(50) NOT NULL,
    `hp` VARCHAR(20) NOT NULL,
    `hourly_rate` DOUBLE NOT NULL,
    `daily_rate` DOUBLE NOT NULL,
    `implements_included` VARCHAR(255),
    `owner_name` VARCHAR(100),
    `owner_phone` VARCHAR(15),
    `location` VARCHAR(150),
    `is_available` BOOLEAN DEFAULT TRUE,
    `rating` FLOAT DEFAULT 4.8
) ENGINE=InnoDB;

-- 6. Tractor Bookings Table
CREATE TABLE IF NOT EXISTS `tractor_bookings` (
    `id` VARCHAR(40) PRIMARY KEY,
    `tractor_id` VARCHAR(30) NOT NULL,
    `tractor_model` VARCHAR(150) NOT NULL,
    `farmer_name` VARCHAR(100) NOT NULL,
    `farmer_phone` VARCHAR(15) NOT NULL,
    `booking_date` VARCHAR(50) NOT NULL,
    `duration_hours` INT NOT NULL,
    `total_amount` DOUBLE NOT NULL,
    `farm_location` VARCHAR(255) NOT NULL,
    `notes` TEXT,
    `status` VARCHAR(20) DEFAULT 'PENDING',
    `created_at` VARCHAR(50)
) ENGINE=InnoDB;

-- 7. Fertilizer Inventory Table
CREATE TABLE IF NOT EXISTS `fertilizer_inventory` (
    `id` VARCHAR(40) PRIMARY KEY,
    `farmer_phone` VARCHAR(15) NOT NULL,
    `name` VARCHAR(150) NOT NULL,
    `category` VARCHAR(50),
    `quantity_bags` INT NOT NULL DEFAULT 0,
    `bag_weight_kg` DOUBLE DEFAULT 50.0,
    `cost_per_bag` DOUBLE DEFAULT 0.0,
    `purchase_date` VARCHAR(50),
    `low_stock_threshold` INT DEFAULT 3
) ENGINE=InnoDB;

-- 8. Crop Harvest Inventory Table
CREATE TABLE IF NOT EXISTS `crop_harvest_inventory` (
    `id` VARCHAR(40) PRIMARY KEY,
    `farmer_phone` VARCHAR(15) NOT NULL,
    `crop_name` VARCHAR(100) NOT NULL,
    `variety` VARCHAR(100),
    `harvested_quintals` DOUBLE NOT NULL DEFAULT 0.0,
    `sold_quintals` DOUBLE NOT NULL DEFAULT 0.0,
    `stock_remaining_quintals` DOUBLE NOT NULL DEFAULT 0.0,
    `harvest_date` VARCHAR(50),
    `storage_location` VARCHAR(150),
    `expected_price_per_qtl` DOUBLE DEFAULT 0.0
) ENGINE=InnoDB;

-- 9. Marketplace Products Table
CREATE TABLE IF NOT EXISTS `products` (
    `id` VARCHAR(30) PRIMARY KEY,
    `name` VARCHAR(150) NOT NULL,
    `category` VARCHAR(50) NOT NULL,
    `brand` VARCHAR(100),
    `price` DOUBLE NOT NULL,
    `original_price` DOUBLE NOT NULL,
    `package_size` VARCHAR(50),
    `description` TEXT,
    `rating` FLOAT DEFAULT 4.8,
    `stock_count` INT DEFAULT 100
) ENGINE=InnoDB;

-- 10. Orders Table
CREATE TABLE IF NOT EXISTS `orders` (
    `order_id` VARCHAR(50) PRIMARY KEY,
    `farmer_phone` VARCHAR(15) NOT NULL,
    `total_amount` DOUBLE NOT NULL,
    `payment_method` VARCHAR(50) NOT NULL,
    `payment_id` VARCHAR(100),
    `delivery_address` TEXT NOT NULL,
    `order_date` VARCHAR(50) NOT NULL,
    `status` VARCHAR(20) DEFAULT 'PLACED'
) ENGINE=InnoDB;

-- 11. Order Items Table
CREATE TABLE IF NOT EXISTS `order_items` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `order_id` VARCHAR(50) NOT NULL,
    `product_id` VARCHAR(30) NOT NULL,
    `product_name` VARCHAR(150) NOT NULL,
    `quantity` INT NOT NULL,
    `unit_price` DOUBLE NOT NULL,
    `subtotal` DOUBLE NOT NULL,
    CONSTRAINT `fk_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 12. Government Schemes Table
CREATE TABLE IF NOT EXISTS `government_schemes` (
    `id` VARCHAR(30) PRIMARY KEY,
    `title` VARCHAR(200) NOT NULL,
    `ministry` VARCHAR(150),
    `subsidy_amount` VARCHAR(100),
    `eligibility` TEXT,
    `description` TEXT,
    `official_url` VARCHAR(255),
    `category` VARCHAR(50),
    `deadline` VARCHAR(50)
) ENGINE=InnoDB;

-- 13. Agriculture News Table
CREATE TABLE IF NOT EXISTS `agri_news` (
    `id` VARCHAR(30) PRIMARY KEY,
    `title` VARCHAR(255) NOT NULL,
    `summary` TEXT,
    `content` TEXT,
    `source` VARCHAR(100),
    `category` VARCHAR(50),
    `published_date` VARCHAR(50),
    `read_time` VARCHAR(20)
) ENGINE=InnoDB;

-- 14. Notifications Table
CREATE TABLE IF NOT EXISTS `notifications` (
    `id` VARCHAR(40) PRIMARY KEY,
    `farmer_phone` VARCHAR(15),
    `title` VARCHAR(200) NOT NULL,
    `message` TEXT NOT NULL,
    `timestamp` VARCHAR(50),
    `type` VARCHAR(30),
    `is_read` BOOLEAN DEFAULT FALSE
) ENGINE=InnoDB;

-- 15. IoT Devices Table
CREATE TABLE IF NOT EXISTS `iot_devices` (
    `device_id` VARCHAR(50) PRIMARY KEY,
    `device_name` VARCHAR(100) NOT NULL,
    `device_type` VARCHAR(50) DEFAULT 'ESP32_SMART_STATION',
    `owner_id` BIGINT NOT NULL,
    `farmer_phone` VARCHAR(15) NOT NULL,
    `field_location` VARCHAR(150),
    `api_key` VARCHAR(100) NOT NULL,
    `firmware_version` VARCHAR(20) DEFAULT '1.0.0',
    `is_active` BOOLEAN DEFAULT TRUE,
    `last_seen` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 16. Sensor Readings Table (Capacitive Moisture + DHT22 Temp/Humidity)
CREATE TABLE IF NOT EXISTS `sensor_readings` (
    `id` VARCHAR(50) PRIMARY KEY,
    `device_id` VARCHAR(50) NOT NULL,
    `farmer_id` BIGINT NOT NULL,
    `soil_moisture_pct` DOUBLE NOT NULL,
    `soil_moisture_raw` INT NOT NULL,
    `moisture_status` VARCHAR(20) NOT NULL,
    `temperature_c` DOUBLE,
    `humidity_pct` DOUBLE,
    `sensor_status` VARCHAR(30) DEFAULT 'VALID',
    `recorded_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_reading_device_time` (`device_id`, `recorded_at`),
    INDEX `idx_reading_farmer_time` (`farmer_id`, `recorded_at`)
) ENGINE=InnoDB;

-- 17. Irrigation Settings Table
CREATE TABLE IF NOT EXISTS `irrigation_settings` (
    `device_id` VARCHAR(50) PRIMARY KEY,
    `farmer_id` BIGINT NOT NULL,
    `operating_mode` VARCHAR(20) DEFAULT 'MANUAL',
    `low_moisture_start_threshold` DOUBLE DEFAULT 30.0,
    `high_moisture_stop_threshold` DOUBLE DEFAULT 70.0,
    `max_continuous_pump_sec` INT DEFAULT 300,
    `cooldown_interval_sec` INT DEFAULT 600,
    `alert_threshold_pct` DOUBLE DEFAULT 25.0,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `updated_by` BIGINT
) ENGINE=InnoDB;

-- 18. Pump Commands and Operation History Table
CREATE TABLE IF NOT EXISTS `pump_commands` (
    `id` VARCHAR(50) PRIMARY KEY,
    `device_id` VARCHAR(50) NOT NULL,
    `requesting_user_id` BIGINT NOT NULL,
    `requested_state` VARCHAR(10) NOT NULL,
    `command_source` VARCHAR(30) NOT NULL,
    `status` VARCHAR(20) DEFAULT 'PENDING',
    `confirmed_actual_state` VARCHAR(10) DEFAULT 'OFF',
    `failure_reason` VARCHAR(255),
    `duration_seconds` INT DEFAULT 0,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `acknowledged_at` TIMESTAMP NULL,
    `completed_at` TIMESTAMP NULL,
    INDEX `idx_cmd_device_status` (`device_id`, `status`)
) ENGINE=InnoDB;

-- 19. IoT Alerts and Events Table
CREATE TABLE IF NOT EXISTS `iot_alerts` (
    `id` VARCHAR(50) PRIMARY KEY,
    `device_id` VARCHAR(50) NOT NULL,
    `farmer_id` BIGINT NOT NULL,
    `alert_type` VARCHAR(50) NOT NULL,
    `message` TEXT NOT NULL,
    `severity` VARCHAR(20) DEFAULT 'WARNING',
    `is_read` BOOLEAN DEFAULT FALSE,
    `related_reading_id` VARCHAR(50),
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_alert_device` (`device_id`, `created_at`),
    INDEX `idx_alert_farmer` (`farmer_id`, `created_at`)
) ENGINE=InnoDB;

-- 20. Soil Health Records Table
CREATE TABLE IF NOT EXISTS `soil_health_records` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `farmer_id` BIGINT NOT NULL,
    `farmer_phone` VARCHAR(15),
    `sample_date` VARCHAR(50),
    `field_name` VARCHAR(100),
    `soil_type` VARCHAR(50),
    `nitrogen_n` DOUBLE,
    `phosphorus_p` DOUBLE,
    `potassium_k` DOUBLE,
    `ph_level` DOUBLE,
    `organic_carbon` DOUBLE,
    `health_status` VARCHAR(50),
    `fertilizer_advice` TEXT,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_soil_farmer` (`farmer_id`)
) ENGINE=InnoDB;

-- 21. Farm Expenses & Financial Ledger Table
CREATE TABLE IF NOT EXISTS `farm_expenses` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `farmer_id` BIGINT NOT NULL,
    `farmer_phone` VARCHAR(15),
    `crop_name` VARCHAR(100),
    `category` VARCHAR(50),
    `amount` DOUBLE,
    `description` VARCHAR(255),
    `expense_date` VARCHAR(50),
    `receipt_url` VARCHAR(255),
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_expense_farmer` (`farmer_id`)
) ENGINE=InnoDB;

-- 22. Crop Disease Reports & Field Triage Table
CREATE TABLE IF NOT EXISTS `crop_disease_reports` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `farmer_id` BIGINT NOT NULL,
    `farmer_phone` VARCHAR(15),
    `crop_name` VARCHAR(100),
    `symptoms` TEXT,
    `suspected_disease` VARCHAR(100),
    `image_url` VARCHAR(255),
    `status` VARCHAR(30) DEFAULT 'SUBMITTED',
    `expert_notes` TEXT,
    `reported_at` VARCHAR(50),
    `resolved_at` VARCHAR(50),
    INDEX `idx_disease_farmer` (`farmer_id`),
    INDEX `idx_disease_status` (`status`)
) ENGINE=InnoDB;

-- 23. Farmer Feedback & Platform Reviews Table
CREATE TABLE IF NOT EXISTS `farmer_feedback` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `farmer_id` BIGINT NOT NULL,
    `farmer_phone` VARCHAR(15),
    `farmer_name` VARCHAR(100),
    `module_name` VARCHAR(50),
    `rating` INT,
    `comments` TEXT,
    `submitted_at` VARCHAR(50),
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_feedback_farmer` (`farmer_id`)
) ENGINE=InnoDB;

-- 24. Saved Crop Plans & Simulator Scenarios
CREATE TABLE IF NOT EXISTS `saved_crop_plans` (
    `id` VARCHAR(50) PRIMARY KEY,
    `farmer_id` BIGINT,
    `farmer_phone` VARCHAR(15) NOT NULL,
    `farmer_name` VARCHAR(100),
    `district` VARCHAR(50) NOT NULL,
    `taluka` VARCHAR(50),
    `season` VARCHAR(30) NOT NULL,
    `crop_name` VARCHAR(100) NOT NULL,
    `variety` VARCHAR(100),
    `plan_type` VARCHAR(50) NOT NULL,
    `water_availability` VARCHAR(50),
    `land_type` VARCHAR(50),
    `soil_type` VARCHAR(50),
    `previous_crop` VARCHAR(100),
    `investment_budget` VARCHAR(50),
    `farmer_goal` VARCHAR(50),
    `duration_days` VARCHAR(50),
    `estimated_cost_per_acre` DOUBLE,
    `target_yield` VARCHAR(100),
    `suitability_reason` TEXT,
    `key_risks` TEXT,
    `university_source` VARCHAR(200),
    `is_planted` BOOLEAN DEFAULT FALSE,
    `created_date` VARCHAR(50),
    INDEX `idx_crop_plan_farmer` (`farmer_phone`),
    INDEX `idx_crop_plan_district` (`district`)
) ENGINE=InnoDB;




-- 25. Weather Forecast Snapshots (PS 5.4)
CREATE TABLE IF NOT EXISTS `weather_forecast_snapshots` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `farmer_phone` VARCHAR(15),
    `location_name` VARCHAR(100),
    `latitude` DOUBLE,
    `longitude` DOUBLE,
    `forecast_date` VARCHAR(20) NOT NULL,
    `day_name` VARCHAR(20),
    `min_temp` DOUBLE,
    `max_temp` DOUBLE,
    `temp` DOUBLE,
    `humidity` INT,
    `wind_speed` DOUBLE,
    `rain_probability` INT,
    `expected_rainfall_mm` DOUBLE,
    `condition` VARCHAR(50),
    `condition_description` VARCHAR(200),
    `risk_level` VARCHAR(20),
    `is_heavy_rain` BOOLEAN DEFAULT FALSE,
    `provider` VARCHAR(100),
    `retrieved_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_snap_phone_date` (`farmer_phone`, `forecast_date`),
    INDEX `idx_snap_location` (`location_name`)
) ENGINE=InnoDB;

-- 26. Weather Advisory Changes (Previous vs Updated Tracking)
CREATE TABLE IF NOT EXISTS `weather_advisory_changes` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `farmer_phone` VARCHAR(15) NOT NULL,
    `location_name` VARCHAR(100),
    `forecast_date` VARCHAR(20),
    `day_name` VARCHAR(20),
    `activity` VARCHAR(50) NOT NULL,
    `previous_status` VARCHAR(30),
    `updated_status` VARCHAR(30),
    `previous_forecast_summary` TEXT,
    `updated_forecast_summary` TEXT,
    `change_reason` TEXT,
    `notification_sent` BOOLEAN DEFAULT FALSE,
    `notification_delivery_status` VARCHAR(30),
    `dedup_key` VARCHAR(128),
    `previous_retrieved_at` TIMESTAMP NULL,
    `updated_retrieved_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `detected_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_advisory_farmer` (`farmer_phone`),
    INDEX `idx_advisory_dedup` (`dedup_key`)
) ENGINE=InnoDB;

-- 27. Device Push Notification Tokens
CREATE TABLE IF NOT EXISTS `device_tokens` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `farmer_phone` VARCHAR(15) NOT NULL,
    `token` VARCHAR(512) NOT NULL UNIQUE,
    `device_model` VARCHAR(100),
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_device_phone` (`farmer_phone`)
) ENGINE=InnoDB;
