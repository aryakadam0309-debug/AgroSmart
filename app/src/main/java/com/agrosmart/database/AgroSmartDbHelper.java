package com.agrosmart.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.agrosmart.models.AdminDashboardStats;
import com.agrosmart.models.CropHarvest;
import com.agrosmart.models.CropPrice;
import com.agrosmart.models.FertilizerItem;
import com.agrosmart.models.IotAlert;
import com.agrosmart.models.IotDevice;
import com.agrosmart.models.IrrigationSetting;
import com.agrosmart.models.NewsItem;
import com.agrosmart.models.PumpCommand;
import com.agrosmart.models.SensorReading;
import com.agrosmart.models.Tractor;
import com.agrosmart.models.TractorBooking;
import com.agrosmart.models.User;
import com.agrosmart.models.SoilHealthRecord;
import com.agrosmart.models.FarmExpense;
import com.agrosmart.models.DiseaseReport;
import com.agrosmart.models.CropAdvisory;
import com.agrosmart.models.FarmerFeedback;
import com.agrosmart.models.SavedCropPlanModel;


import java.util.ArrayList;
import java.util.List;

/**
 * AgroSmart SQLite Database Helper for reliable offline caching and persistence.
 * Zero external database dependency required on device; handles offline mode seamlessly.
 */
public class AgroSmartDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "agrosmart_local.db";
    private static final int DATABASE_VERSION = 9;

    private static AgroSmartDbHelper instance;

    public static synchronized AgroSmartDbHelper getInstance(Context context) {
        if (instance == null) {
            instance = new AgroSmartDbHelper(context.getApplicationContext());
        }
        return instance;
    }

    public AgroSmartDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 1. Users Table
        db.execSQL("CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "full_name TEXT NOT NULL, " +
                "phone TEXT UNIQUE NOT NULL, " +
                "email TEXT, " +
                "password TEXT NOT NULL, " +
                "role TEXT NOT NULL DEFAULT 'ROLE_FARMER', " +
                "location TEXT, " +
                "farm_size REAL DEFAULT 1.0, " +
                "profile_image_url TEXT, " +
                "is_synced INTEGER DEFAULT 0)");

        // 2. Tractors Table
        db.execSQL("CREATE TABLE IF NOT EXISTS tractors (" +
                "id TEXT PRIMARY KEY, " +
                "model_name TEXT NOT NULL, " +
                "brand TEXT NOT NULL, " +
                "hp TEXT NOT NULL, " +
                "hourly_rate REAL NOT NULL, " +
                "daily_rate REAL NOT NULL, " +
                "implements_included TEXT, " +
                "owner_name TEXT, " +
                "owner_phone TEXT, " +
                "location TEXT, " +
                "is_available INTEGER DEFAULT 1, " +
                "rating REAL DEFAULT 4.8)");

        // 3. Tractor Bookings Table
        db.execSQL("CREATE TABLE IF NOT EXISTS tractor_bookings (" +
                "id TEXT PRIMARY KEY, " +
                "tractor_id TEXT NOT NULL, " +
                "tractor_model TEXT NOT NULL, " +
                "farmer_name TEXT NOT NULL, " +
                "farmer_phone TEXT NOT NULL, " +
                "booking_date TEXT NOT NULL, " +
                "duration_hours INTEGER NOT NULL, " +
                "total_amount REAL NOT NULL, " +
                "farm_location TEXT NOT NULL, " +
                "notes TEXT, " +
                "status TEXT DEFAULT 'CONFIRMED', " +
                "created_at TEXT)");

        // 4. Fertilizer Inventory Table
        db.execSQL("CREATE TABLE IF NOT EXISTS fertilizer_inventory (" +
                "id TEXT PRIMARY KEY, " +
                "farmer_phone TEXT, " +
                "name TEXT NOT NULL, " +
                "category TEXT, " +
                "quantity_bags INTEGER NOT NULL DEFAULT 0, " +
                "bag_weight_kg REAL DEFAULT 50.0, " +
                "cost_per_bag REAL DEFAULT 0.0, " +
                "purchase_date TEXT, " +
                "low_stock_threshold INTEGER DEFAULT 3)");

        // 5. Crop Harvest Inventory Table
        db.execSQL("CREATE TABLE IF NOT EXISTS crop_harvest_inventory (" +
                "id TEXT PRIMARY KEY, " +
                "farmer_phone TEXT, " +
                "crop_name TEXT NOT NULL, " +
                "variety TEXT, " +
                "harvested_quintals REAL NOT NULL DEFAULT 0.0, " +
                "sold_quintals REAL NOT NULL DEFAULT 0.0, " +
                "stock_remaining_quintals REAL NOT NULL DEFAULT 0.0, " +
                "harvest_date TEXT, " +
                "storage_location TEXT, " +
                "expected_price_per_qtl REAL DEFAULT 0.0)");

        // 6. Crop Prices (Mandi Bhav) Table
        db.execSQL("CREATE TABLE IF NOT EXISTS crop_prices (" +
                "id TEXT PRIMARY KEY, " +
                "crop_name TEXT NOT NULL, " +
                "category TEXT NOT NULL, " +
                "variety TEXT, " +
                "market_name TEXT NOT NULL, " +
                "state TEXT NOT NULL, " +
                "district TEXT, " +
                "min_price REAL NOT NULL, " +
                "max_price REAL NOT NULL, " +
                "modal_price REAL NOT NULL, " +
                "price_unit TEXT DEFAULT '₹ / Qtl', " +
                "trend TEXT DEFAULT 'STABLE', " +
                "price_change REAL DEFAULT 0.0, " +
                "updated_date TEXT, " +
                "arrival_quantity TEXT)");

        // 7. IoT Devices Table
        db.execSQL("CREATE TABLE IF NOT EXISTS iot_devices (" +
                "device_id TEXT PRIMARY KEY, " +
                "device_name TEXT NOT NULL, " +
                "device_type TEXT NOT NULL, " +
                "owner_id INTEGER NOT NULL, " +
                "farmer_phone TEXT NOT NULL, " +
                "field_location TEXT, " +
                "api_key TEXT NOT NULL, " +
                "firmware_version TEXT, " +
                "is_active INTEGER DEFAULT 1, " +
                "last_seen TEXT, " +
                "created_at TEXT)");

        // 8. IoT Sensor Readings Table
        db.execSQL("CREATE TABLE IF NOT EXISTS sensor_readings (" +
                "id TEXT PRIMARY KEY, " +
                "device_id TEXT NOT NULL, " +
                "farmer_id INTEGER NOT NULL, " +
                "soil_moisture_pct REAL NOT NULL, " +
                "soil_moisture_raw INTEGER NOT NULL, " +
                "moisture_status TEXT NOT NULL, " +
                "temperature_c REAL, " +
                "humidity_pct REAL, " +
                "sensor_status TEXT DEFAULT 'VALID', " +
                "recorded_at TEXT NOT NULL)");

        // 9. IoT Irrigation Settings Table
        db.execSQL("CREATE TABLE IF NOT EXISTS irrigation_settings (" +
                "device_id TEXT PRIMARY KEY, " +
                "farmer_id INTEGER NOT NULL, " +
                "operating_mode TEXT DEFAULT 'MANUAL', " +
                "low_moisture_start_threshold REAL DEFAULT 30.0, " +
                "high_moisture_stop_threshold REAL DEFAULT 70.0, " +
                "max_continuous_pump_sec INTEGER DEFAULT 300, " +
                "cooldown_interval_sec INTEGER DEFAULT 600, " +
                "alert_threshold_pct REAL DEFAULT 25.0, " +
                "updated_at TEXT, " +
                "updated_by INTEGER)");

        // 10. IoT Pump Commands Table
        db.execSQL("CREATE TABLE IF NOT EXISTS pump_commands (" +
                "command_id TEXT PRIMARY KEY, " +
                "device_id TEXT NOT NULL, " +
                "command TEXT NOT NULL, " +
                "duration_seconds INTEGER, " +
                "trigger_source TEXT NOT NULL, " +
                "requested_state INTEGER NOT NULL, " +
                "confirmed_state INTEGER, " +
                "status TEXT DEFAULT 'PENDING', " +
                "failure_reason TEXT, " +
                "created_at TEXT, " +
                "executed_at TEXT)");

        // 11. IoT Alerts Table
        db.execSQL("CREATE TABLE IF NOT EXISTS iot_alerts (" +
                "id TEXT PRIMARY KEY, " +
                "device_id TEXT NOT NULL, " +
                "farmer_id INTEGER NOT NULL, " +
                "alert_type TEXT NOT NULL, " +
                "severity TEXT NOT NULL, " +
                "title TEXT, " +
                "message TEXT NOT NULL, " +
                "is_resolved INTEGER DEFAULT 0, " +
                "created_at TEXT)");

        // 12. Agri News Table
        db.execSQL("CREATE TABLE IF NOT EXISTS agri_news (" +
                "id INTEGER PRIMARY KEY, " +
                "title TEXT NOT NULL, " +
                "short_description TEXT NOT NULL, " +
                "full_content TEXT NOT NULL, " +
                "category TEXT, " +
                "image_url TEXT, " +
                "source_name TEXT, " +
                "published_date TEXT)");

        
        // 13. Soil Health Records Table
        db.execSQL("CREATE TABLE IF NOT EXISTS soil_health_records (" +
                "id TEXT PRIMARY KEY, " +
                "farmer_phone TEXT NOT NULL, " +
                "sample_date TEXT, " +
                "field_name TEXT, " +
                "ph REAL, " +
                "electrical_conductivity REAL, " +
                "organic_carbon REAL, " +
                "nitrogen_kg_ha REAL, " +
                "phosphorus_kg_ha REAL, " +
                "potassium_kg_ha REAL, " +
                "zinc_ppm REAL, " +
                "iron_ppm REAL, " +
                "fertility_rating TEXT, " +
                "advisory_notes TEXT)");

        // 14. Farm Expenses & Cultivation Profitability Table
        db.execSQL("CREATE TABLE IF NOT EXISTS farm_expenses (" +
                "id TEXT PRIMARY KEY, " +
                "farmer_phone TEXT NOT NULL, " +
                "entry_type TEXT NOT NULL, " +
                "category TEXT NOT NULL, " +
                "title TEXT NOT NULL, " +
                "amount REAL NOT NULL, " +
                "entry_date TEXT, " +
                "crop_season TEXT, " +
                "notes TEXT)");

        // 15. Crop Disease Reports Table
        db.execSQL("CREATE TABLE IF NOT EXISTS crop_disease_reports (" +
                "id TEXT PRIMARY KEY, " +
                "farmer_phone TEXT NOT NULL, " +
                "farmer_name TEXT, " +
                "crop_name TEXT NOT NULL, " +
                "variety TEXT, " +
                "affected_part TEXT, " +
                "symptoms TEXT NOT NULL, " +
                "severity TEXT, " +
                "village TEXT, " +
                "taluka TEXT, " +
                "district TEXT, " +
                "latitude REAL, " +
                "longitude REAL, " +
                "location_consent INTEGER DEFAULT 0, " +
                "is_duplicate INTEGER DEFAULT 0, " +
                "duplicate_of_report_id TEXT, " +
                "matched_advisory_id TEXT, " +
                "image_url TEXT, " +
                "reported_at TEXT, " +
                "status TEXT, " +
                "admin_advisory TEXT, " +
                "problem_category TEXT, " +
                "growth_stage TEXT, " +
                "review_note TEXT, " +
                "admin_guidance TEXT, " +
                "admin_source TEXT, " +
                "reviewed_by TEXT, " +
                "reviewed_at TEXT)");

        // 15B. Community Crop Alerts Table
        db.execSQL("CREATE TABLE IF NOT EXISTS crop_alerts (" +
                "id TEXT PRIMARY KEY, " +
                "crop_name TEXT NOT NULL, " +
                "district TEXT NOT NULL, " +
                "village TEXT, " +
                "alert_title TEXT NOT NULL, " +
                "severity TEXT, " +
                "summary TEXT NOT NULL, " +
                "report_count INTEGER, " +
                "authority TEXT, " +
                "recommended_actions TEXT, " +
                "status TEXT, " +
                "published_by TEXT, " +
                "published_at TEXT, " +
                "resolved_at TEXT)");

        // 15C. Official Agricultural Advisory Catalogue Table (Dr. PDKV Akola & CROPSAP)
        db.execSQL("CREATE TABLE IF NOT EXISTS crop_advisories (" +
                "id TEXT PRIMARY KEY, " +
                "title TEXT NOT NULL, " +
                "crop_name TEXT NOT NULL, " +
                "variety TEXT, " +
                "problem_category TEXT NOT NULL, " +
                "location_coverage TEXT, " +
                "advisory_text TEXT NOT NULL, " +
                "advisory_text_marathi TEXT, " +
                "language TEXT, " +
                "source_name TEXT NOT NULL, " +
                "source_url TEXT NOT NULL, " +
                "publication_date TEXT, " +
                "date_added TEXT, " +
                "validity_period TEXT, " +
                "verification_status TEXT, " +
                "reviewing_admin TEXT, " +
                "last_reviewed_at TEXT, " +
                "ingestion_type TEXT)");

        // 16. Farmer Field Feedback Table
        db.execSQL("CREATE TABLE IF NOT EXISTS farmer_feedback (" +
                "id TEXT PRIMARY KEY, " +
                "farmer_phone TEXT NOT NULL, " +
                "farmer_name TEXT, " +
                "rating INTEGER NOT NULL, " +
                "category TEXT NOT NULL, " +
                "comments TEXT NOT NULL, " +
                "app_version TEXT, " +
                "submitted_at TEXT)");

        // 17. Saved Crop Plans & Simulator Scenarios
        db.execSQL("CREATE TABLE IF NOT EXISTS saved_crop_plans (" +
                "id TEXT PRIMARY KEY, " +
                "farmer_id INTEGER, " +
                "farmer_phone TEXT NOT NULL, " +
                "farmer_name TEXT, " +
                "district TEXT NOT NULL, " +
                "taluka TEXT, " +
                "season TEXT NOT NULL, " +
                "crop_name TEXT NOT NULL, " +
                "variety TEXT, " +
                "plan_type TEXT NOT NULL, " +
                "water_availability TEXT, " +
                "land_type TEXT, " +
                "soil_type TEXT, " +
                "previous_crop TEXT, " +
                "investment_budget TEXT, " +
                "farmer_goal TEXT, " +
                "duration_days TEXT, " +
                "estimated_cost_per_acre REAL, " +
                "target_yield TEXT, " +
                "suitability_reason TEXT, " +
                "key_risks TEXT, " +
                "university_source TEXT, " +
                "is_planted INTEGER DEFAULT 0, " +
                "created_date TEXT)");

        // 18. Weather Forecast Snapshots & Offline Cache (PS 5.4)
        db.execSQL("CREATE TABLE IF NOT EXISTS weather_forecast_cache (" +
                "id TEXT PRIMARY KEY, " +
                "location_name TEXT NOT NULL, " +
                "current_temp REAL, " +
                "feels_like REAL, " +
                "condition TEXT, " +
                "description TEXT, " +
                "humidity INTEGER, " +
                "wind_speed REAL, " +
                "rain_chance INTEGER, " +
                "rainfall_mm REAL, " +
                "risk_level TEXT, " +
                "five_day_summary TEXT, " +
                "forecast_json TEXT, " +
                "advisories_json TEXT, " +
                "retrieved_at TEXT)");

        // 19. Weather Advisory Changes
        db.execSQL("CREATE TABLE IF NOT EXISTS weather_advisory_changes (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "farmer_phone TEXT NOT NULL, " +
                "location_name TEXT, " +
                "forecast_date TEXT, " +
                "day_name TEXT, " +
                "activity TEXT NOT NULL, " +
                "previous_status TEXT, " +
                "updated_status TEXT, " +
                "previous_summary TEXT, " +
                "updated_summary TEXT, " +
                "change_reason TEXT, " +
                "notification_status TEXT, " +
                "detected_at TEXT)");

        // Seed default initial data
        seedInitialData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS tractors");
        db.execSQL("DROP TABLE IF EXISTS tractor_bookings");
        db.execSQL("DROP TABLE IF EXISTS fertilizer_inventory");
        db.execSQL("DROP TABLE IF EXISTS crop_harvest_inventory");
        db.execSQL("DROP TABLE IF EXISTS crop_prices");
        db.execSQL("DROP TABLE IF EXISTS iot_devices");
        db.execSQL("DROP TABLE IF EXISTS sensor_readings");
        db.execSQL("DROP TABLE IF EXISTS irrigation_settings");
        db.execSQL("DROP TABLE IF EXISTS pump_commands");
        db.execSQL("DROP TABLE IF EXISTS iot_alerts");
        db.execSQL("DROP TABLE IF EXISTS agri_news");
        db.execSQL("DROP TABLE IF EXISTS soil_health_records");
        db.execSQL("DROP TABLE IF EXISTS farm_expenses");
        db.execSQL("DROP TABLE IF EXISTS crop_disease_reports");
        db.execSQL("DROP TABLE IF EXISTS crop_alerts");
        db.execSQL("DROP TABLE IF EXISTS crop_advisories");
        db.execSQL("DROP TABLE IF EXISTS farmer_feedback");
        db.execSQL("DROP TABLE IF EXISTS saved_crop_plans");
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        try {
            db.execSQL("ALTER TABLE users ADD COLUMN is_synced INTEGER DEFAULT 0");
        } catch (Exception ignored) {
            // Column already exists
        }
        try { db.execSQL("ALTER TABLE crop_disease_reports ADD COLUMN problem_category TEXT"); } catch (Exception ignored) {}
        try { db.execSQL("ALTER TABLE crop_disease_reports ADD COLUMN growth_stage TEXT"); } catch (Exception ignored) {}
        try { db.execSQL("ALTER TABLE crop_disease_reports ADD COLUMN review_note TEXT"); } catch (Exception ignored) {}
        try { db.execSQL("ALTER TABLE crop_disease_reports ADD COLUMN admin_guidance TEXT"); } catch (Exception ignored) {}
        try { db.execSQL("ALTER TABLE crop_disease_reports ADD COLUMN admin_source TEXT"); } catch (Exception ignored) {}
        try { db.execSQL("ALTER TABLE crop_disease_reports ADD COLUMN reviewed_by TEXT"); } catch (Exception ignored) {}
        try { db.execSQL("ALTER TABLE crop_disease_reports ADD COLUMN reviewed_at TEXT"); } catch (Exception ignored) {}
        try { db.execSQL("ALTER TABLE crop_disease_reports ADD COLUMN variety TEXT"); } catch (Exception ignored) {}
        try { db.execSQL("ALTER TABLE crop_disease_reports ADD COLUMN taluka TEXT"); } catch (Exception ignored) {}
        try { db.execSQL("ALTER TABLE crop_disease_reports ADD COLUMN latitude REAL"); } catch (Exception ignored) {}
        try { db.execSQL("ALTER TABLE crop_disease_reports ADD COLUMN longitude REAL"); } catch (Exception ignored) {}
        try { db.execSQL("ALTER TABLE crop_disease_reports ADD COLUMN location_consent INTEGER DEFAULT 0"); } catch (Exception ignored) {}
        try { db.execSQL("ALTER TABLE crop_disease_reports ADD COLUMN is_duplicate INTEGER DEFAULT 0"); } catch (Exception ignored) {}
        try { db.execSQL("ALTER TABLE crop_disease_reports ADD COLUMN duplicate_of_report_id TEXT"); } catch (Exception ignored) {}
        try { db.execSQL("ALTER TABLE crop_disease_reports ADD COLUMN matched_advisory_id TEXT"); } catch (Exception ignored) {}
        try {
            db.execSQL("CREATE TABLE IF NOT EXISTS crop_alerts (" +
                    "id TEXT PRIMARY KEY, " +
                    "crop_name TEXT NOT NULL, " +
                    "district TEXT NOT NULL, " +
                    "village TEXT, " +
                    "alert_title TEXT NOT NULL, " +
                    "severity TEXT, " +
                    "summary TEXT NOT NULL, " +
                    "report_count INTEGER, " +
                    "authority TEXT, " +
                    "recommended_actions TEXT, " +
                    "status TEXT, " +
                    "published_by TEXT, " +
                    "published_at TEXT, " +
                    "resolved_at TEXT)");
        } catch (Exception ignored) {}
        try {
            db.execSQL("CREATE TABLE IF NOT EXISTS crop_advisories (" +
                    "id TEXT PRIMARY KEY, " +
                    "title TEXT NOT NULL, " +
                    "crop_name TEXT NOT NULL, " +
                    "variety TEXT, " +
                    "problem_category TEXT NOT NULL, " +
                    "location_coverage TEXT, " +
                    "advisory_text TEXT NOT NULL, " +
                    "advisory_text_marathi TEXT, " +
                    "language TEXT, " +
                    "source_name TEXT NOT NULL, " +
                    "source_url TEXT NOT NULL, " +
                    "publication_date TEXT, " +
                    "date_added TEXT, " +
                    "validity_period TEXT, " +
                    "verification_status TEXT, " +
                    "reviewing_admin TEXT, " +
                    "last_reviewed_at TEXT, " +
                    "ingestion_type TEXT)");
        } catch (Exception ignored) {}
        try {
            db.execSQL("CREATE TABLE IF NOT EXISTS weather_forecast_cache (" +
                    "id TEXT PRIMARY KEY, " +
                    "location_name TEXT NOT NULL, " +
                    "current_temp REAL, " +
                    "feels_like REAL, " +
                    "condition TEXT, " +
                    "description TEXT, " +
                    "humidity INTEGER, " +
                    "wind_speed REAL, " +
                    "rain_chance INTEGER, " +
                    "rainfall_mm REAL, " +
                    "risk_level TEXT, " +
                    "five_day_summary TEXT, " +
                    "forecast_json TEXT, " +
                    "advisories_json TEXT, " +
                    "retrieved_at TEXT)");
            db.execSQL("CREATE TABLE IF NOT EXISTS weather_advisory_changes (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "farmer_phone TEXT NOT NULL, " +
                    "location_name TEXT, " +
                    "forecast_date TEXT, " +
                    "day_name TEXT, " +
                    "activity TEXT NOT NULL, " +
                    "previous_status TEXT, " +
                    "updated_status TEXT, " +
                    "previous_summary TEXT, " +
                    "updated_summary TEXT, " +
                    "change_reason TEXT, " +
                    "notification_status TEXT, " +
                    "detected_at TEXT)");
        } catch (Exception ignored) {}
        seedBaselineAdvisoriesIfEmpty(db);
    }

    private void seedInitialData(SQLiteDatabase db) {
        // Seed default farmer
        ContentValues userValues = new ContentValues();
        userValues.put("full_name", "Arya Farmer");
        userValues.put("phone", "9876543210");
        userValues.put("email", "farmer@agrosmart.in");
        userValues.put("password", "123456");
        userValues.put("role", "ROLE_FARMER");
        userValues.put("location", "Mumbai, Maharashtra");
        userValues.put("farm_size", 5.5);
        db.insertWithOnConflict("users", null, userValues, SQLiteDatabase.CONFLICT_IGNORE);

        // Seed default admin
        ContentValues adminValues = new ContentValues();
        adminValues.put("full_name", "AgroSmart Admin");
        adminValues.put("phone", "9999999999");
        adminValues.put("email", "admin@agrosmart.in");
        adminValues.put("password", "admin123");
        adminValues.put("role", "ROLE_ADMIN");
        adminValues.put("location", "Headquarters, Pune");
        adminValues.put("farm_size", 0.0);
        db.insertWithOnConflict("users", null, adminValues, SQLiteDatabase.CONFLICT_IGNORE);

        // Seed initial tractors
        insertTractorDirect(db, "TR01", "Mahindra 575 DI XP Plus", "Mahindra", "47 HP", 800.0, 5800.0,
                "Rotavator, 9-Tyne Cultivator, Trolley", "Ramesh Patil", "9822334455", "Nashik, Maharashtra", 1, 4.9);
        insertTractorDirect(db, "TR02", "John Deere 5050 D GearPro", "John Deere", "50 HP", 900.0, 6500.0,
                "Heavy-Duty Rotavator, MB Plough", "Suresh Jagtap", "9822445566", "Shirur, Pune", 1, 4.8);
        insertTractorDirect(db, "TR03", "Swaraj 855 FE 4WD", "Swaraj", "52 HP", 950.0, 6800.0,
                "Laser Land Leveler, Disk Harrow", "Vikas Deshmukh", "9822556677", "Daund, Pune", 1, 4.9);
        insertTractorDirect(db, "TR04", "Sonalika DI 745 III Sikander", "Sonalika", "50 HP", 850.0, 6000.0,
                "Cultivator, Seed Drill, Trolley", "Ganesh Shinde", "9822667788", "Niphad, Nashik", 1, 4.7);
        insertTractorDirect(db, "TR05", "Eicher 380 Super DI", "Eicher", "40 HP", 700.0, 5000.0,
                "Rigid Cultivator, Trolley", "Baburao Kadam", "9822778899", "Dindori, Nashik", 1, 4.6);

        // Seed initial fertilizers
        insertFertilizerDirect(db, "FER-01", "9876543210", "Neem Coated Urea (46% N)", "Chemical", 14, 45.0, 266.5, "12 Aug 2026", 5);
        insertFertilizerDirect(db, "FER-02", "9876543210", "DAP (Di-Ammonium Phosphate 18:46)", "Chemical", 2, 50.0, 1350.0, "05 Jul 2026", 4);
        insertFertilizerDirect(db, "FER-03", "9876543210", "NPK Suphala 10:26:26", "Chemical", 8, 50.0, 1470.0, "20 Aug 2026", 3);
        insertFertilizerDirect(db, "FER-04", "9876543210", "Muriate of Potash (MOP 60% K)", "Chemical", 1, 50.0, 1700.0, "15 Jun 2026", 3);
        insertFertilizerDirect(db, "FER-05", "9876543210", "Vermicompost Organic Compost", "Organic", 25, 40.0, 350.0, "01 Sep 2026", 6);
        insertFertilizerDirect(db, "FER-06", "9876543210", "Zinc Sulphate Heptahydrate (21% Zn)", "Micronutrient", 5, 25.0, 850.0, "10 Jul 2026", 2);

        // Seed initial harvest godown stock
        insertHarvestDirect(db, "HRV-01", "9876543210", "Soybean (सोयाबीन)", "JS-335 Yellow", 85.0, 35.0, 50.0, "15 Nov 2025", "Godown Room A, North Bay", 4850.0);
        insertHarvestDirect(db, "HRV-02", "9876543210", "Wheat (गेहूं)", "Sharbati Deluxe", 120.0, 40.0, 80.0, "28 Mar 2026", "Central Silo B, Pune", 2850.0);
        insertHarvestDirect(db, "HRV-03", "9876543210", "Chana / Gram (चना)", "Desi Bold Chana", 45.0, 15.0, 30.0, "10 Apr 2026", "Godown Room C, Pune", 5900.0);
        insertHarvestDirect(db, "HRV-04", "9876543210", "Onion (लाल प्याज)", "Nashik Red", 210.0, 160.0, 50.0, "20 May 2026", "Aerated Shade 1, Nashik", 2200.0);

        // Seed default IoT device (OFFLINE until physical ESP32 connects)
        ContentValues devCv = new ContentValues();
        devCv.put("device_id", "ESP32-AGRO-01");
        devCv.put("device_name", "ESP32 Smart Station #1");
        devCv.put("device_type", "ESP32_SMART_STATION");
        devCv.put("owner_id", 1);
        devCv.put("farmer_phone", "9876543210");
        devCv.put("field_location", "Farm Plot 1, Mumbai, Maharashtra");
        devCv.put("api_key", "AGRO-ESP32-SECURE-KEY-2026");
        devCv.put("firmware_version", "1.0.0");
        devCv.put("is_active", 0);
        devCv.put("last_seen", "Hardware Offline (Waiting for Connection)");
        devCv.put("created_at", "2026-10-13 10:00:00");
        db.insertWithOnConflict("iot_devices", null, devCv, SQLiteDatabase.CONFLICT_IGNORE);

        // Seed default irrigation settings
        ContentValues setCv = new ContentValues();
        setCv.put("device_id", "ESP32-AGRO-01");
        setCv.put("farmer_id", 1);
        setCv.put("operating_mode", "MANUAL");
        setCv.put("low_moisture_start_threshold", 30.0);
        setCv.put("high_moisture_stop_threshold", 70.0);
        setCv.put("max_continuous_pump_sec", 300);
        setCv.put("cooldown_interval_sec", 600);
        setCv.put("alert_threshold_pct", 25.0);
        setCv.put("updated_at", "13 Oct 2026");
        setCv.put("updated_by", 1);
        db.insertWithOnConflict("irrigation_settings", null, setCv, SQLiteDatabase.CONFLICT_IGNORE);

        // Seed initial APMC Mandi crop prices
        insertCropPriceDirect(db, "CP001", "Soybean (सोयाबीन)", "Oilseeds", "JS-335 Yellow", "Indore Mandi", "Madhya Pradesh", "Indore", 4400, 4950, 4820, "₹ / Qtl", "UP", 75.0, "Today, 11:30 AM", "3,400 Quintals");
        insertCropPriceDirect(db, "CP002", "Wheat (गेहूं)", "Cereals", "Sharbati Deluxe", "Pune APMC (Gultekdi)", "Maharashtra", "Pune", 2600, 3100, 2850, "₹ / Qtl", "UP", 40.0, "Today, 10:45 AM", "5,120 Quintals");
        insertCropPriceDirect(db, "CP003", "Cotton (कपास)", "Cash Crops", "BT Shankar-6", "Rajkot APMC Yard", "Gujarat", "Rajkot", 7100, 7850, 7550, "₹ / Qtl", "DOWN", -120.0, "Today, 12:15 PM", "1,890 Quintals");
        insertCropPriceDirect(db, "CP004", "Onion (लाल प्याज)", "Vegetables", "Nashik Red Premium", "Lasalgaon APMC", "Maharashtra", "Nashik", 1600, 2450, 2150, "₹ / Qtl", "UP", 150.0, "Today, 09:30 AM", "12,400 Quintals");
        insertCropPriceDirect(db, "CP005", "Gram / Chana (चना)", "Pulses", "Desi Bold Chana", "Akola APMC", "Maharashtra", "Akola", 5600, 6150, 5920, "₹ / Qtl", "STABLE", 0.0, "Today, 11:00 AM", "2,350 Quintals");

        // Seed initial Agriculture News
        insertNewsDirect(db, 1L,
                "New Subsidies Announced for Solar Agricultural Pumps in Maharashtra",
                "State government expands solar water pump scheme for smallholder farmers with up to 90% subsidy under the PM-KUSUM initiative.",
                "The Department of Agriculture has officially announced an expansion of the solar irrigation initiative. Farmers owning between 1 to 5 acres of agricultural land can apply for solar pumps ranging from 3 HP to 7.5 HP capacity with substantial subsidy support. Applications are being routed through the official Mahakrishi portal with simplified document requirements.",
                "Government",
                "https://images.unsplash.com/photo-1509391365360-2e959784a276?w=600",
                "Ministry of Agriculture",
                "10 Sep 2026");

        insertNewsDirect(db, 2L,
                "Soybean and Cotton Market Rates Surge in Western APMC Mandis",
                "Demand uptick in international edible oils and textile mills leads to higher farmgate prices across major trading centers.",
                "Mandi arrivals across Vidarbha and Marathwada recorded a 12% price appreciation for Grade-A non-GMO soybean and medium-staple cotton. Agricultural analysts advise farmers to stagger their market arrivals over the coming fortnights to capitalize on prevailing wholesale prices.",
                "Markets",
                "https://images.unsplash.com/photo-1595974482597-4b8da8879bc5?w=600",
                "Agri Market Watch",
                "09 Sep 2026");

        insertNewsDirect(db, 3L,
                "Integrated Pest Management Guidelines for Kharif Paddy",
                "Agricultural universities issue timely advisory for stem borer and blast prevention following recent erratic monsoon showers.",
                "Due to intermittent humidity and temperature spikes, paddy growers in coastal and plateau regions are advised to deploy pheromone traps and neem-based bio-pesticides at the active tillering stage. Excessive nitrogenous fertilizer application should be avoided during overcast conditions.",
                "Crops",
                "https://images.unsplash.com/photo-1536657464919-892534f60d6e?w=600",
                "ICAR Advisory",
                "08 Sep 2026");

        insertNewsDirect(db, 4L,
                "Micro-Irrigation Adoption Boosts Sugarcane Yields by 25%",
                "Field studies demonstrate that subsurface drip irrigation saves over 40% water while significantly improving sugar recovery ratios.",
                "Precision drip irrigation trials conducted in Western India demonstrate that scheduled fertigation directly to root zones reduces weed growth and maximizes fertilizer efficiency. Agronomists recommend adopting automated drip systems with state subsidy support.",
                "Agri-Tech",
                "https://images.unsplash.com/photo-1625246333195-78d9c38ad449?w=600",
                "Krishi Vigyan Kendra",
                "06 Sep 2026");
    }

    private void insertNewsDirect(SQLiteDatabase db, long id, String title, String summary, String content,
                                  String category, String imageUrl, String source, String date) {
        ContentValues cv = new ContentValues();
        cv.put("id", id);
        cv.put("title", title);
        cv.put("short_description", summary);
        cv.put("full_content", content);
        cv.put("category", category);
        cv.put("image_url", imageUrl);
        cv.put("source_name", source);
        cv.put("published_date", date);
        db.insertWithOnConflict("agri_news", null, cv, SQLiteDatabase.CONFLICT_IGNORE);
    }

    private void insertCropPriceDirect(SQLiteDatabase db, String id, String crop, String category, String variety,
                                      String market, String state, String district, double minP, double maxP, double modalP,
                                      String unit, String trend, double change, String updated, String arrivals) {
        ContentValues cv = new ContentValues();
        cv.put("id", id);
        cv.put("crop_name", crop);
        cv.put("category", category);
        cv.put("variety", variety);
        cv.put("market_name", market);
        cv.put("state", state);
        cv.put("district", district);
        cv.put("min_price", minP);
        cv.put("max_price", maxP);
        cv.put("modal_price", modalP);
        cv.put("price_unit", unit);
        cv.put("trend", trend);
        cv.put("price_change", change);
        cv.put("updated_date", updated);
        cv.put("arrival_quantity", arrivals);
        db.insertWithOnConflict("crop_prices", null, cv, SQLiteDatabase.CONFLICT_IGNORE);
    }

    private void insertReadingDirect(SQLiteDatabase db, String id, String devId, long farmerId,
                                     double moisture, int raw, String status, Double temp, Double hum,
                                     String sensorStatus, String recordedAt) {
        ContentValues cv = new ContentValues();
        cv.put("id", id);
        cv.put("device_id", devId);
        cv.put("farmer_id", farmerId);
        cv.put("soil_moisture_pct", moisture);
        cv.put("soil_moisture_raw", raw);
        cv.put("moisture_status", status);
        if (temp != null) cv.put("temperature_c", temp);
        if (hum != null) cv.put("humidity_pct", hum);
        cv.put("sensor_status", sensorStatus);
        cv.put("recorded_at", recordedAt);
        db.insertWithOnConflict("sensor_readings", null, cv, SQLiteDatabase.CONFLICT_IGNORE);
    }

    public void insertTractorDirect(SQLiteDatabase db, String id, String model, String brand, String hp,
                                     double hourly, double daily, String implementsList, String owner,
                                     String phone, String location, int available, double rating) {
        ContentValues cv = new ContentValues();
        cv.put("id", id);
        cv.put("model_name", model);
        cv.put("brand", brand);
        cv.put("hp", hp);
        cv.put("hourly_rate", hourly);
        cv.put("daily_rate", daily);
        cv.put("implements_included", implementsList);
        cv.put("owner_name", owner);
        cv.put("owner_phone", phone);
        cv.put("location", location);
        cv.put("is_available", available);
        cv.put("rating", rating);
        db.insertWithOnConflict("tractors", null, cv, SQLiteDatabase.CONFLICT_IGNORE);
    }

    private void insertFertilizerDirect(SQLiteDatabase db, String id, String phone, String name, String category,
                                        int qty, double weight, double cost, String purchaseDate, int threshold) {
        ContentValues cv = new ContentValues();
        cv.put("id", id);
        cv.put("farmer_phone", phone);
        cv.put("name", name);
        cv.put("category", category);
        cv.put("quantity_bags", qty);
        cv.put("bag_weight_kg", weight);
        cv.put("cost_per_bag", cost);
        cv.put("purchase_date", purchaseDate);
        cv.put("low_stock_threshold", threshold);
        db.insertWithOnConflict("fertilizer_inventory", null, cv, SQLiteDatabase.CONFLICT_IGNORE);
    }

    private void insertHarvestDirect(SQLiteDatabase db, String id, String phone, String crop, String variety,
                                     double harvested, double sold, double remaining, String date, String location, double price) {
        ContentValues cv = new ContentValues();
        cv.put("id", id);
        cv.put("farmer_phone", phone);
        cv.put("crop_name", crop);
        cv.put("variety", variety);
        cv.put("harvested_quintals", harvested);
        cv.put("sold_quintals", sold);
        cv.put("stock_remaining_quintals", remaining);
        cv.put("harvest_date", date);
        cv.put("storage_location", location);
        cv.put("expected_price_per_qtl", price);
        db.insertWithOnConflict("crop_harvest_inventory", null, cv, SQLiteDatabase.CONFLICT_IGNORE);
    }

    // ==========================================
    // USER CRUD
    // ==========================================
    public User authenticateUser(String phoneOrEmail, String password, String role) {
        SQLiteDatabase db = getReadableDatabase();
        // Allow flexible authentication by phone or email and password
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE (phone = ? OR email = ?) AND password = ?",
                new String[]{phoneOrEmail, phoneOrEmail, password});
        if (cursor != null && cursor.moveToFirst()) {
            User user = new User();
            user.setId(cursor.getLong(cursor.getColumnIndexOrThrow("id")));
            user.setFullName(cursor.getString(cursor.getColumnIndexOrThrow("full_name")));
            user.setPhone(cursor.getString(cursor.getColumnIndexOrThrow("phone")));
            user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow("email")));
            user.setRole(cursor.getString(cursor.getColumnIndexOrThrow("role")));
            user.setLocation(cursor.getString(cursor.getColumnIndexOrThrow("location")));
            user.setFarmSize(cursor.getDouble(cursor.getColumnIndexOrThrow("farm_size")));
            int imgIdx = cursor.getColumnIndex("profile_image_url");
            if (imgIdx != -1 && !cursor.isNull(imgIdx)) {
                user.setProfileImageUrl(cursor.getString(imgIdx));
            }
            cursor.close();
            return user;
        }
        if (cursor != null) cursor.close();
        return null;
    }

    public User getUserByPhone(String phone) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE phone = ?", new String[]{phone});
        if (cursor != null && cursor.moveToFirst()) {
            User user = new User();
            user.setId(cursor.getLong(cursor.getColumnIndexOrThrow("id")));
            user.setFullName(cursor.getString(cursor.getColumnIndexOrThrow("full_name")));
            user.setPhone(cursor.getString(cursor.getColumnIndexOrThrow("phone")));
            user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow("email")));
            user.setRole(cursor.getString(cursor.getColumnIndexOrThrow("role")));
            user.setLocation(cursor.getString(cursor.getColumnIndexOrThrow("location")));
            user.setFarmSize(cursor.getDouble(cursor.getColumnIndexOrThrow("farm_size")));
            int imgIdx = cursor.getColumnIndex("profile_image_url");
            if (imgIdx != -1 && !cursor.isNull(imgIdx)) {
                user.setProfileImageUrl(cursor.getString(imgIdx));
            }
            cursor.close();
            return user;
        }
        if (cursor != null) cursor.close();
        return null;
    }

    public User getUserById(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE id = ?", new String[]{String.valueOf(id)});
        if (cursor != null && cursor.moveToFirst()) {
            User user = new User();
            user.setId(cursor.getLong(cursor.getColumnIndexOrThrow("id")));
            user.setFullName(cursor.getString(cursor.getColumnIndexOrThrow("full_name")));
            user.setPhone(cursor.getString(cursor.getColumnIndexOrThrow("phone")));
            user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow("email")));
            user.setRole(cursor.getString(cursor.getColumnIndexOrThrow("role")));
            user.setLocation(cursor.getString(cursor.getColumnIndexOrThrow("location")));
            user.setFarmSize(cursor.getDouble(cursor.getColumnIndexOrThrow("farm_size")));
            int imgIdx = cursor.getColumnIndex("profile_image_url");
            if (imgIdx != -1 && !cursor.isNull(imgIdx)) {
                user.setProfileImageUrl(cursor.getString(imgIdx));
            }
            cursor.close();
            return user;
        }
        if (cursor != null) cursor.close();
        return null;
    }

    public boolean updateUserProfile(long id, String fullName, String email, String location, double farmSize, String profileImagePath) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("full_name", fullName);
        cv.put("email", email);
        cv.put("location", location);
        cv.put("farm_size", farmSize);
        if (profileImagePath != null) {
            cv.put("profile_image_url", profileImagePath);
        }
        int rows = db.update("users", cv, "id = ?", new String[]{String.valueOf(id)});
        return rows > 0;
    }

    public boolean updateUserLocation(long id, String location) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("location", location);
        int rows = db.update("users", cv, "id = ?", new String[]{String.valueOf(id)});
        return rows > 0;
    }

    public boolean registerUser(User user, String password) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("full_name", user.getFullName());
        cv.put("phone", user.getPhone());
        cv.put("email", user.getEmail());
        cv.put("password", password);
        cv.put("role", user.getRole() != null ? user.getRole() : "ROLE_FARMER");
        cv.put("location", user.getLocation());
        cv.put("farm_size", user.getFarmSize());
        cv.put("is_synced", 0);
        long id = db.insertWithOnConflict("users", null, cv, SQLiteDatabase.CONFLICT_REPLACE);
        if (id > 0) {
            user.setId(id);
            return true;
        } else {
            int rows = db.update("users", cv, "phone = ?", new String[]{user.getPhone()});
            return rows > 0;
        }
    }

    public void markUserSynced(String phone, boolean synced) {
        try {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put("is_synced", synced ? 1 : 0);
            db.update("users", cv, "phone = ?", new String[]{phone});
        } catch (Exception e) {
            android.util.Log.e("AgroSmartDbHelper", "Error updating is_synced: " + e.getMessage());
        }
    }

    public boolean isUserSynced(String phone) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT is_synced FROM users WHERE phone = ?", new String[]{phone});
        boolean synced = false;
        if (cursor != null && cursor.moveToFirst()) {
            synced = cursor.getInt(0) == 1;
            cursor.close();
        } else if (cursor != null) {
            cursor.close();
        }
        return synced;
    }

    public java.util.List<java.util.Map<String, String>> getPendingSyncUsers() {
        java.util.List<java.util.Map<String, String>> list = new java.util.ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE is_synced = 0 OR is_synced IS NULL", null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                java.util.Map<String, String> map = new java.util.HashMap<>();
                int nameIdx = cursor.getColumnIndex("full_name");
                int phoneIdx = cursor.getColumnIndex("phone");
                int emailIdx = cursor.getColumnIndex("email");
                int passIdx = cursor.getColumnIndex("password");
                int roleIdx = cursor.getColumnIndex("role");
                int locIdx = cursor.getColumnIndex("location");
                int farmIdx = cursor.getColumnIndex("farm_size");

                map.put("fullName", nameIdx != -1 ? cursor.getString(nameIdx) : "Farmer");
                map.put("phone", phoneIdx != -1 ? cursor.getString(phoneIdx) : "");
                map.put("email", emailIdx != -1 ? cursor.getString(emailIdx) : "");
                map.put("password", passIdx != -1 ? cursor.getString(passIdx) : "");
                map.put("role", roleIdx != -1 ? cursor.getString(roleIdx) : "ROLE_FARMER");
                map.put("location", locIdx != -1 ? cursor.getString(locIdx) : "Maharashtra");
                map.put("farmSize", farmIdx != -1 ? String.valueOf(cursor.getDouble(farmIdx)) : "1.0");
                list.add(map);
            } while (cursor.moveToNext());
            cursor.close();
        } else if (cursor != null) {
            cursor.close();
        }
        return list;
    }

    public boolean resetUserPassword(String phone, String newPassword) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("password", newPassword);
        int rows = db.update("users", cv, "phone = ?", new String[]{phone});
        return rows > 0;
    }

    public boolean checkUserExists(String phone) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM users WHERE phone = ?", new String[]{phone});
        boolean exists = (cursor != null && cursor.getCount() > 0);
        if (cursor != null) cursor.close();
        return exists;
    }

    // ==========================================
    // TRACTOR FLEET & LISTINGS CRUD
    // ==========================================
    public List<Tractor> getAllTractors(String brandFilter) {
        List<Tractor> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor;
        if (brandFilter == null || brandFilter.equalsIgnoreCase("All Brands")) {
            cursor = db.rawQuery("SELECT * FROM tractors ORDER BY id ASC", null);
        } else {
            cursor = db.rawQuery("SELECT * FROM tractors WHERE brand = ? COLLATE NOCASE ORDER BY id ASC", new String[]{brandFilter});
        }

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Tractor t = new Tractor();
                t.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
                t.setModelName(cursor.getString(cursor.getColumnIndexOrThrow("model_name")));
                t.setBrand(cursor.getString(cursor.getColumnIndexOrThrow("brand")));
                t.setHp(cursor.getString(cursor.getColumnIndexOrThrow("hp")));
                t.setHourlyRate(cursor.getDouble(cursor.getColumnIndexOrThrow("hourly_rate")));
                t.setDailyRate(cursor.getDouble(cursor.getColumnIndexOrThrow("daily_rate")));
                t.setImplementsIncluded(cursor.getString(cursor.getColumnIndexOrThrow("implements_included")));
                t.setOwnerName(cursor.getString(cursor.getColumnIndexOrThrow("owner_name")));
                t.setOwnerPhone(cursor.getString(cursor.getColumnIndexOrThrow("owner_phone")));
                t.setLocation(cursor.getString(cursor.getColumnIndexOrThrow("location")));
                t.setAvailable(cursor.getInt(cursor.getColumnIndexOrThrow("is_available")) == 1);
                t.setRating((float) cursor.getDouble(cursor.getColumnIndexOrThrow("rating")));
                list.add(t);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    public boolean addTractor(Tractor t) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("id", t.getId());
        cv.put("model_name", t.getModelName());
        cv.put("brand", t.getBrand());
        cv.put("hp", t.getHp());
        cv.put("hourly_rate", t.getHourlyRate());
        cv.put("daily_rate", t.getDailyRate());
        cv.put("implements_included", t.getImplementsIncluded());
        cv.put("owner_name", t.getOwnerName());
        cv.put("owner_phone", t.getOwnerPhone());
        cv.put("location", t.getLocation());
        cv.put("is_available", t.isAvailable() ? 1 : 0);
        cv.put("rating", (double) t.getRating());
        long result = db.insertWithOnConflict("tractors", null, cv, SQLiteDatabase.CONFLICT_REPLACE);
        return result != -1;
    }

    // ==========================================
    // TRACTOR BOOKINGS CRUD
    // ==========================================
    public List<TractorBooking> getAllBookings() {
        List<TractorBooking> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM tractor_bookings ORDER BY created_at DESC", null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                TractorBooking b = new TractorBooking(
                        cursor.getString(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("tractor_id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("tractor_model")),
                        cursor.getString(cursor.getColumnIndexOrThrow("farmer_name")),
                        cursor.getString(cursor.getColumnIndexOrThrow("farmer_phone")),
                        cursor.getString(cursor.getColumnIndexOrThrow("booking_date")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("duration_hours")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("total_amount")),
                        cursor.getString(cursor.getColumnIndexOrThrow("farm_location")),
                        cursor.getString(cursor.getColumnIndexOrThrow("notes")),
                        cursor.getString(cursor.getColumnIndexOrThrow("status")),
                        cursor.getString(cursor.getColumnIndexOrThrow("created_at"))
                );
                list.add(b);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    public boolean addBooking(TractorBooking b) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("id", b.getId());
        cv.put("tractor_id", b.getTractorId());
        cv.put("tractor_model", b.getTractorModel());
        cv.put("farmer_name", b.getFarmerName());
        cv.put("farmer_phone", b.getFarmerPhone());
        cv.put("booking_date", b.getBookingDate());
        cv.put("duration_hours", b.getDurationHours());
        cv.put("total_amount", b.getTotalAmount());
        cv.put("farm_location", b.getFarmLocation());
        cv.put("notes", b.getNotes());
        cv.put("status", b.getStatus());
        cv.put("created_at", b.getCreatedAt());
        long result = db.insertWithOnConflict("tractor_bookings", null, cv, SQLiteDatabase.CONFLICT_REPLACE);
        return result != -1;
    }

    public boolean updateBookingStatus(String bookingId, String status) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("status", status);
        int rows = db.update("tractor_bookings", cv, "id = ?", new String[]{bookingId});
        return rows > 0;
    }

    // ==========================================
    // FERTILIZER INVENTORY CRUD
    // ==========================================
    public List<FertilizerItem> getAllFertilizers() {
        List<FertilizerItem> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM fertilizer_inventory ORDER BY name ASC", null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                FertilizerItem item = new FertilizerItem(
                        cursor.getString(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        cursor.getString(cursor.getColumnIndexOrThrow("category")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("quantity_bags")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("bag_weight_kg")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("cost_per_bag")),
                        cursor.getString(cursor.getColumnIndexOrThrow("purchase_date")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("low_stock_threshold"))
                );
                list.add(item);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    public boolean addFertilizer(FertilizerItem item, String farmerPhone) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("id", item.getId());
        cv.put("farmer_phone", farmerPhone);
        cv.put("name", item.getName());
        cv.put("category", item.getCategory());
        cv.put("quantity_bags", item.getQuantityBags());
        cv.put("bag_weight_kg", item.getBagWeightKg());
        cv.put("cost_per_bag", item.getCostPerBag());
        cv.put("purchase_date", item.getPurchaseDate());
        cv.put("low_stock_threshold", item.getLowStockThreshold());
        long result = db.insertWithOnConflict("fertilizer_inventory", null, cv, SQLiteDatabase.CONFLICT_REPLACE);
        return result != -1;
    }

    public boolean updateFertilizerQuantity(String id, int newQty) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("quantity_bags", newQty);
        int rows = db.update("fertilizer_inventory", cv, "id = ?", new String[]{id});
        return rows > 0;
    }

    public boolean deleteFertilizer(String id) {
        SQLiteDatabase db = getWritableDatabase();
        int rows = db.delete("fertilizer_inventory", "id = ?", new String[]{id});
        return rows > 0;
    }

    // ==========================================
    // CROP HARVEST GODOWN CRUD
    // ==========================================
    public List<CropHarvest> getAllHarvests() {
        List<CropHarvest> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM crop_harvest_inventory ORDER BY crop_name ASC", null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                CropHarvest item = new CropHarvest(
                        cursor.getString(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("crop_name")),
                        cursor.getString(cursor.getColumnIndexOrThrow("variety")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("harvested_quintals")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("sold_quintals")),
                        cursor.getString(cursor.getColumnIndexOrThrow("harvest_date")),
                        cursor.getString(cursor.getColumnIndexOrThrow("storage_location")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("expected_price_per_qtl"))
                );
                list.add(item);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    public boolean addHarvest(CropHarvest item, String farmerPhone) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("id", item.getId());
        cv.put("farmer_phone", farmerPhone);
        cv.put("crop_name", item.getCropName());
        cv.put("variety", item.getVariety());
        cv.put("harvested_quintals", item.getHarvestedQuintals());
        cv.put("sold_quintals", item.getSoldQuintals());
        cv.put("stock_remaining_quintals", item.getStockRemainingQuintals());
        cv.put("harvest_date", item.getHarvestDate());
        cv.put("storage_location", item.getStorageLocation());
        cv.put("expected_price_per_qtl", item.getExpectedPricePerQtl());
        long result = db.insertWithOnConflict("crop_harvest_inventory", null, cv, SQLiteDatabase.CONFLICT_REPLACE);
        return result != -1;
    }

    public boolean updateHarvestSold(String id, double soldAdd) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT harvested_quintals, sold_quintals FROM crop_harvest_inventory WHERE id = ?", new String[]{id});
        if (cursor != null && cursor.moveToFirst()) {
            double harvested = cursor.getDouble(0);
            double currentSold = cursor.getDouble(1);
            cursor.close();
            double newSold = currentSold + soldAdd;
            double remaining = Math.max(0, harvested - newSold);
            ContentValues cv = new ContentValues();
            cv.put("sold_quintals", newSold);
            cv.put("stock_remaining_quintals", remaining);
            int rows = db.update("crop_harvest_inventory", cv, "id = ?", new String[]{id});
            return rows > 0;
        }
        if (cursor != null) cursor.close();
        return false;
    }

    public boolean deleteHarvest(String id) {
        SQLiteDatabase db = getWritableDatabase();
        int rows = db.delete("crop_harvest_inventory", "id = ?", new String[]{id});
        return rows > 0;
    }

    // ==========================================
    // IoT SMART FARMING & IRRIGATION CRUD
    // ==========================================

    public boolean insertOrUpdateIotDevice(IotDevice device) {
        if (device == null || device.getDeviceId() == null) return false;
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("device_id", device.getDeviceId());
        cv.put("device_name", device.getDeviceName());
        cv.put("device_type", device.getDeviceType());
        cv.put("owner_id", device.getOwnerId());
        cv.put("farmer_phone", device.getFarmerPhone());
        cv.put("field_location", device.getFieldLocation());
        cv.put("api_key", device.getApiKey());
        cv.put("firmware_version", device.getFirmwareVersion());
        cv.put("is_active", device.isActive() ? 1 : 0);
        cv.put("last_seen", device.getLastSeen());
        cv.put("created_at", device.getCreatedAt());
        long result = db.insertWithOnConflict("iot_devices", null, cv, SQLiteDatabase.CONFLICT_REPLACE);
        return result != -1;
    }

    public IotDevice getIotDevice(String deviceId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM iot_devices WHERE device_id = ?", new String[]{deviceId});
        IotDevice dev = null;
        if (c != null && c.moveToFirst()) {
            dev = new IotDevice();
            dev.setDeviceId(c.getString(c.getColumnIndexOrThrow("device_id")));
            dev.setDeviceName(c.getString(c.getColumnIndexOrThrow("device_name")));
            dev.setDeviceType(c.getString(c.getColumnIndexOrThrow("device_type")));
            dev.setOwnerId(c.getLong(c.getColumnIndexOrThrow("owner_id")));
            dev.setFarmerPhone(c.getString(c.getColumnIndexOrThrow("farmer_phone")));
            dev.setFieldLocation(c.getString(c.getColumnIndexOrThrow("field_location")));
            dev.setApiKey(c.getString(c.getColumnIndexOrThrow("api_key")));
            dev.setFirmwareVersion(c.getString(c.getColumnIndexOrThrow("firmware_version")));
            dev.setActive(c.getInt(c.getColumnIndexOrThrow("is_active")) == 1);
            dev.setLastSeen(c.getString(c.getColumnIndexOrThrow("last_seen")));
            dev.setCreatedAt(c.getString(c.getColumnIndexOrThrow("created_at")));
            c.close();
        } else if (c != null) {
            c.close();
        }
        return dev;
    }

    public boolean updateIotDeviceLocation(String deviceId, String location) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("field_location", location);
        int rows = db.update("iot_devices", cv, "device_id = ?", new String[]{deviceId});
        return rows > 0;
    }

    public boolean insertSensorReading(SensorReading reading) {
        if (reading == null || reading.getDeviceId() == null) return false;
        try {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put("id", reading.getId() != null ? reading.getId() : "SR-" + System.currentTimeMillis());
            cv.put("device_id", reading.getDeviceId());
            cv.put("farmer_id", reading.getFarmerId());
            cv.put("soil_moisture_pct", reading.getSoilMoisturePct());
            cv.put("soil_moisture_raw", reading.getSoilMoistureRaw());
            cv.put("moisture_status", reading.getMoistureStatus() != null ? reading.getMoistureStatus() : "OPTIMAL");
            if (reading.getTemperatureC() != null) cv.put("temperature_c", reading.getTemperatureC());
            if (reading.getHumidityPct() != null) cv.put("humidity_pct", reading.getHumidityPct());
            cv.put("sensor_status", reading.getSensorStatus() != null ? reading.getSensorStatus() : "VALID");
            cv.put("recorded_at", reading.getRecordedAt() != null ? reading.getRecordedAt() : "Just now");
            long result = db.insertWithOnConflict("sensor_readings", null, cv, SQLiteDatabase.CONFLICT_REPLACE);
            return result != -1;
        } catch (Exception e) {
            android.util.Log.e("AgroSmartDbHelper", "Error caching sensor reading: " + e.getMessage());
            return false;
        }
    }

    public SensorReading getLatestSensorReading(String deviceId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM sensor_readings WHERE device_id = ? ORDER BY rowid DESC LIMIT 1", new String[]{deviceId});
        SensorReading reading = null;
        if (c != null && c.moveToFirst()) {
            reading = cursorToSensorReading(c);
            c.close();
        } else if (c != null) {
            c.close();
        }
        return reading;
    }

    public List<SensorReading> getSensorReadingsHistory(String deviceId, int limit) {
        List<SensorReading> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM sensor_readings WHERE device_id = ? ORDER BY rowid DESC LIMIT ?",
                new String[]{deviceId, String.valueOf(limit)});
        if (c != null && c.moveToFirst()) {
            do {
                list.add(cursorToSensorReading(c));
            } while (c.moveToNext());
            c.close();
        } else if (c != null) {
            c.close();
        }
        return list;
    }

    private SensorReading cursorToSensorReading(Cursor c) {
        SensorReading r = new SensorReading();
        r.setId(c.getString(c.getColumnIndexOrThrow("id")));
        r.setDeviceId(c.getString(c.getColumnIndexOrThrow("device_id")));
        r.setFarmerId(c.getLong(c.getColumnIndexOrThrow("farmer_id")));
        r.setSoilMoisturePct(c.getDouble(c.getColumnIndexOrThrow("soil_moisture_pct")));
        r.setSoilMoistureRaw(c.getInt(c.getColumnIndexOrThrow("soil_moisture_raw")));
        r.setMoistureStatus(c.getString(c.getColumnIndexOrThrow("moisture_status")));
        int tempIdx = c.getColumnIndexOrThrow("temperature_c");
        if (!c.isNull(tempIdx)) r.setTemperatureC(c.getDouble(tempIdx));
        int humIdx = c.getColumnIndexOrThrow("humidity_pct");
        if (!c.isNull(humIdx)) r.setHumidityPct(c.getDouble(humIdx));
        r.setSensorStatus(c.getString(c.getColumnIndexOrThrow("sensor_status")));
        r.setRecordedAt(c.getString(c.getColumnIndexOrThrow("recorded_at")));
        return r;
    }

    public boolean saveIrrigationSetting(IrrigationSetting setting) {
        if (setting == null || setting.getDeviceId() == null) return false;
        try {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put("device_id", setting.getDeviceId());
            cv.put("farmer_id", setting.getFarmerId());
            cv.put("operating_mode", setting.getOperatingMode() != null ? setting.getOperatingMode() : "AUTO");
            cv.put("low_moisture_start_threshold", setting.getLowMoistureStartThreshold());
            cv.put("high_moisture_stop_threshold", setting.getHighMoistureStopThreshold());
            cv.put("max_continuous_pump_sec", setting.getMaxContinuousPumpSec());
            cv.put("cooldown_interval_sec", setting.getCooldownIntervalSec());
            cv.put("alert_threshold_pct", setting.getAlertThresholdPct());
            cv.put("updated_at", setting.getUpdatedAt() != null ? setting.getUpdatedAt() : "Just now");
            if (setting.getUpdatedBy() != null) cv.put("updated_by", setting.getUpdatedBy());
            long result = db.insertWithOnConflict("irrigation_settings", null, cv, SQLiteDatabase.CONFLICT_REPLACE);
            return result != -1;
        } catch (Exception e) {
            android.util.Log.e("AgroSmartDbHelper", "Error saving irrigation settings: " + e.getMessage());
            return false;
        }
    }

    public IrrigationSetting getIrrigationSetting(String deviceId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM irrigation_settings WHERE device_id = ?", new String[]{deviceId});
        IrrigationSetting s = null;
        if (c != null && c.moveToFirst()) {
            s = new IrrigationSetting();
            s.setDeviceId(c.getString(c.getColumnIndexOrThrow("device_id")));
            s.setFarmerId(c.getLong(c.getColumnIndexOrThrow("farmer_id")));
            s.setOperatingMode(c.getString(c.getColumnIndexOrThrow("operating_mode")));
            s.setLowMoistureStartThreshold(c.getDouble(c.getColumnIndexOrThrow("low_moisture_start_threshold")));
            s.setHighMoistureStopThreshold(c.getDouble(c.getColumnIndexOrThrow("high_moisture_stop_threshold")));
            s.setMaxContinuousPumpSec(c.getInt(c.getColumnIndexOrThrow("max_continuous_pump_sec")));
            s.setCooldownIntervalSec(c.getInt(c.getColumnIndexOrThrow("cooldown_interval_sec")));
            s.setAlertThresholdPct(c.getDouble(c.getColumnIndexOrThrow("alert_threshold_pct")));
            s.setUpdatedAt(c.getString(c.getColumnIndexOrThrow("updated_at")));
            int byIdx = c.getColumnIndexOrThrow("updated_by");
            if (!c.isNull(byIdx)) s.setUpdatedBy(c.getLong(byIdx));
            c.close();
        } else if (c != null) {
            c.close();
        }
        return s;
    }

    public boolean insertPumpCommand(PumpCommand cmd) {
        if (cmd == null || cmd.getCommandId() == null) return false;
        try {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put("command_id", cmd.getCommandId());
            cv.put("device_id", cmd.getDeviceId() != null ? cmd.getDeviceId() : "ESP32-AGRO-01");
            cv.put("command", cmd.getCommand() != null ? cmd.getCommand() : "PUMP_STATUS");
            if (cmd.getDurationSeconds() != null) cv.put("duration_seconds", cmd.getDurationSeconds());
            cv.put("trigger_source", cmd.getTriggerSource() != null ? cmd.getTriggerSource() : "MANUAL");
            cv.put("requested_state", cmd.isRequestedState() ? 1 : 0);
            if (cmd.getConfirmedState() != null) cv.put("confirmed_state", cmd.getConfirmedState() ? 1 : 0);
            cv.put("status", cmd.getStatus() != null ? cmd.getStatus() : "PENDING");
            cv.put("failure_reason", cmd.getFailureReason());
            cv.put("created_at", cmd.getCreatedAt() != null ? cmd.getCreatedAt() : "Just now");
            cv.put("executed_at", cmd.getExecutedAt());
            long result = db.insertWithOnConflict("pump_commands", null, cv, SQLiteDatabase.CONFLICT_REPLACE);
            return result != -1;
        } catch (Exception e) {
            android.util.Log.e("AgroSmartDbHelper", "Error caching pump command: " + e.getMessage());
            return false;
        }
    }

    public PumpCommand getLatestPumpCommand(String deviceId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM pump_commands WHERE device_id = ? ORDER BY rowid DESC LIMIT 1", new String[]{deviceId});
        PumpCommand cmd = null;
        if (c != null && c.moveToFirst()) {
            cmd = new PumpCommand();
            cmd.setCommandId(c.getString(c.getColumnIndexOrThrow("command_id")));
            cmd.setDeviceId(c.getString(c.getColumnIndexOrThrow("device_id")));
            cmd.setCommand(c.getString(c.getColumnIndexOrThrow("command")));
            int durIdx = c.getColumnIndexOrThrow("duration_seconds");
            if (!c.isNull(durIdx)) cmd.setDurationSeconds(c.getInt(durIdx));
            cmd.setTriggerSource(c.getString(c.getColumnIndexOrThrow("trigger_source")));
            cmd.setRequestedState(c.getInt(c.getColumnIndexOrThrow("requested_state")) == 1);
            int confIdx = c.getColumnIndexOrThrow("confirmed_state");
            if (!c.isNull(confIdx)) cmd.setConfirmedState(c.getInt(confIdx) == 1);
            cmd.setStatus(c.getString(c.getColumnIndexOrThrow("status")));
            cmd.setFailureReason(c.getString(c.getColumnIndexOrThrow("failure_reason")));
            cmd.setCreatedAt(c.getString(c.getColumnIndexOrThrow("created_at")));
            cmd.setExecutedAt(c.getString(c.getColumnIndexOrThrow("executed_at")));
            c.close();
        } else if (c != null) {
            c.close();
        }
        return cmd;
    }

    public boolean updatePumpCommandStatus(String commandId, String status, Boolean confirmedState) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("status", status);
        if (confirmedState != null) cv.put("confirmed_state", confirmedState ? 1 : 0);
        cv.put("executed_at", "Just now");
        int rows = db.update("pump_commands", cv, "command_id = ?", new String[]{commandId});
        return rows > 0;
    }

    public boolean insertIotAlert(IotAlert alert) {
        if (alert == null || alert.getId() == null) return false;
        try {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put("id", alert.getId());
            cv.put("device_id", alert.getDeviceId() != null ? alert.getDeviceId() : "ESP32-AGRO-01");
            cv.put("farmer_id", alert.getFarmerId());
            cv.put("alert_type", alert.getAlertType() != null ? alert.getAlertType() : "INFO");
            cv.put("severity", alert.getSeverity() != null ? alert.getSeverity() : "INFO");

            String title = alert.getTitle();
            if (title == null || title.trim().isEmpty()) {
                if (alert.getAlertType() != null && !alert.getAlertType().trim().isEmpty()) {
                    title = alert.getAlertType().replace('_', ' ') + " Alert";
                } else {
                    title = "IoT Alert";
                }
            }
            cv.put("title", title);
            cv.put("message", alert.getMessage() != null ? alert.getMessage() : "Sensor Alert Notification");
            cv.put("is_resolved", alert.isResolved() ? 1 : 0);
            cv.put("created_at", alert.getCreatedAt() != null ? alert.getCreatedAt() : "Just now");
            long result = db.insertWithOnConflict("iot_alerts", null, cv, SQLiteDatabase.CONFLICT_REPLACE);
            return result != -1;
        } catch (Exception e) {
            android.util.Log.e("AgroSmartDbHelper", "Error caching IoT alert: " + e.getMessage());
            return false;
        }
    }

    public List<IotAlert> getActiveAlerts(String deviceId) {
        List<IotAlert> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM iot_alerts WHERE device_id = ? AND is_resolved = 0 ORDER BY rowid DESC", new String[]{deviceId});
        if (c != null && c.moveToFirst()) {
            do {
                IotAlert a = new IotAlert();
                a.setId(c.getString(c.getColumnIndexOrThrow("id")));
                a.setDeviceId(c.getString(c.getColumnIndexOrThrow("device_id")));
                a.setFarmerId(c.getLong(c.getColumnIndexOrThrow("farmer_id")));
                a.setAlertType(c.getString(c.getColumnIndexOrThrow("alert_type")));
                a.setSeverity(c.getString(c.getColumnIndexOrThrow("severity")));
                a.setTitle(c.getString(c.getColumnIndexOrThrow("title")));
                a.setMessage(c.getString(c.getColumnIndexOrThrow("message")));
                a.setResolved(c.getInt(c.getColumnIndexOrThrow("is_resolved")) == 1);
                a.setCreatedAt(c.getString(c.getColumnIndexOrThrow("created_at")));
                list.add(a);
            } while (c.moveToNext());
            c.close();
        } else if (c != null) {
            c.close();
        }
        return list;
    }

    // ==========================================
    // ADMIN ANALYTICS & STATS
    // ==========================================
    public AdminDashboardStats getAdminDashboardStats() {
        SQLiteDatabase db = getReadableDatabase();
        int farmers = 0;
        int tractors = 0;
        int bookings = 0;

        Cursor c = db.rawQuery("SELECT COUNT(*) FROM users WHERE role = 'ROLE_FARMER'", null);
        if (c != null && c.moveToFirst()) {
            farmers = c.getInt(0);
            c.close();
        } else if (c != null) {
            c.close();
        }

        c = db.rawQuery("SELECT COUNT(*) FROM tractors", null);
        if (c != null && c.moveToFirst()) {
            tractors = c.getInt(0);
            c.close();
        } else if (c != null) {
            c.close();
        }

        c = db.rawQuery("SELECT COUNT(*) FROM tractor_bookings", null);
        if (c != null && c.moveToFirst()) {
            bookings = c.getInt(0);
            c.close();
        } else if (c != null) {
            c.close();
        }

        return new AdminDashboardStats(farmers, tractors, bookings, 0, 0, 0.0);
    }

    // ==========================================
    // CROP PRICES (MANDI BHAV) CRUD
    // ==========================================
    public List<CropPrice> getAllCropPricesFromDb() {
        List<CropPrice> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM crop_prices ORDER BY crop_name ASC", null);
        if (c != null && c.moveToFirst()) {
            do {
                CropPrice p = new CropPrice(
                        c.getString(c.getColumnIndexOrThrow("id")),
                        c.getString(c.getColumnIndexOrThrow("crop_name")),
                        c.getString(c.getColumnIndexOrThrow("category")),
                        c.getString(c.getColumnIndexOrThrow("variety")),
                        c.getString(c.getColumnIndexOrThrow("market_name")),
                        c.getString(c.getColumnIndexOrThrow("state")),
                        c.getString(c.getColumnIndexOrThrow("district")),
                        c.getDouble(c.getColumnIndexOrThrow("min_price")),
                        c.getDouble(c.getColumnIndexOrThrow("max_price")),
                        c.getDouble(c.getColumnIndexOrThrow("modal_price")),
                        c.getString(c.getColumnIndexOrThrow("price_unit")),
                        c.getString(c.getColumnIndexOrThrow("trend")),
                        c.getDouble(c.getColumnIndexOrThrow("price_change")),
                        c.getString(c.getColumnIndexOrThrow("updated_date")),
                        c.getString(c.getColumnIndexOrThrow("arrival_quantity"))
                );
                list.add(p);
            } while (c.moveToNext());
            c.close();
        } else if (c != null) {
            c.close();
        }
        return list;
    }

    public boolean insertOrUpdateCropPrice(CropPrice p) {
        if (p == null || p.getId() == null) return false;
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("id", p.getId());
        cv.put("crop_name", p.getCropName());
        cv.put("category", p.getCategory());
        cv.put("variety", p.getVariety());
        cv.put("market_name", p.getMarketName());
        cv.put("state", p.getState());
        cv.put("district", p.getDistrict());
        cv.put("min_price", p.getMinPrice());
        cv.put("max_price", p.getMaxPrice());
        cv.put("modal_price", p.getModalPrice());
        cv.put("price_unit", p.getPriceUnit());
        cv.put("trend", p.getTrend());
        cv.put("price_change", p.getPriceChange());
        cv.put("updated_date", p.getUpdatedDate());
        cv.put("arrival_quantity", p.getArrivalQuantity());
        long r = db.insertWithOnConflict("crop_prices", null, cv, SQLiteDatabase.CONFLICT_REPLACE);
        return r != -1;
    }

    public boolean deleteCropPrice(String id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete("crop_prices", "id = ?", new String[]{id}) > 0;
    }

    // ==========================================
    // AGRICULTURE NEWS CRUD
    // ==========================================
    public List<NewsItem> getAllNewsFromDb() {
        List<NewsItem> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM agri_news ORDER BY id DESC", null);
        if (c != null && c.moveToFirst()) {
            do {
                NewsItem item = new NewsItem(
                        c.getLong(c.getColumnIndexOrThrow("id")),
                        c.getString(c.getColumnIndexOrThrow("title")),
                        c.getString(c.getColumnIndexOrThrow("short_description")),
                        c.getString(c.getColumnIndexOrThrow("full_content")),
                        c.getString(c.getColumnIndexOrThrow("category")),
                        c.getString(c.getColumnIndexOrThrow("image_url")),
                        c.getString(c.getColumnIndexOrThrow("source_name")),
                        c.getString(c.getColumnIndexOrThrow("published_date"))
                );
                list.add(item);
            } while (c.moveToNext());
            c.close();
        } else if (c != null) {
            c.close();
        }
        return list;
    }

    public boolean insertNews(NewsItem item) {
        if (item == null) return false;
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("id", item.getId());
        cv.put("title", item.getTitle());
        cv.put("short_description", item.getShortDescription());
        cv.put("full_content", item.getFullContent());
        cv.put("category", item.getCategory());
        cv.put("image_url", item.getImageUrl());
        cv.put("source_name", item.getSourceName());
        cv.put("published_date", item.getPublishedDate());
        long r = db.insertWithOnConflict("agri_news", null, cv, SQLiteDatabase.CONFLICT_REPLACE);
        return r != -1;
    }

    public boolean deleteNews(long id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete("agri_news", "id = ?", new String[]{String.valueOf(id)}) > 0;
    }

    // =========================================================================
    // SOIL HEALTH RECORDS CRUD
    // =========================================================================

    public boolean insertSoilHealthRecord(SoilHealthRecord r) {
        if (r == null) return false;
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("id", r.getId());
        cv.put("farmer_phone", r.getFarmerPhone());
        cv.put("sample_date", r.getSampleDate());
        cv.put("field_name", r.getFieldName());
        if (r.getPh() != null) cv.put("ph", r.getPh());
        if (r.getElectricalConductivity() != null) cv.put("electrical_conductivity", r.getElectricalConductivity());
        if (r.getOrganicCarbon() != null) cv.put("organic_carbon", r.getOrganicCarbon());
        if (r.getNitrogenKgHa() != null) cv.put("nitrogen_kg_ha", r.getNitrogenKgHa());
        if (r.getPhosphorusKgHa() != null) cv.put("phosphorus_kg_ha", r.getPhosphorusKgHa());
        if (r.getPotassiumKgHa() != null) cv.put("potassium_kg_ha", r.getPotassiumKgHa());
        if (r.getZincPpm() != null) cv.put("zinc_ppm", r.getZincPpm());
        if (r.getIronPpm() != null) cv.put("iron_ppm", r.getIronPpm());
        cv.put("fertility_rating", r.getFertilityRating());
        cv.put("advisory_notes", r.getAdvisoryNotes());
        return db.insertWithOnConflict("soil_health_records", null, cv, SQLiteDatabase.CONFLICT_REPLACE) != -1;
    }

    public List<SoilHealthRecord> getSoilHealthRecords(String phone) {
        List<SoilHealthRecord> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = null;
        try {
            if (phone != null && !phone.trim().isEmpty()) {
                c = db.query("soil_health_records", null, "farmer_phone = ?", new String[]{phone}, null, null, "sample_date DESC");
            } else {
                c = db.query("soil_health_records", null, null, null, null, null, "sample_date DESC");
            }
            if (c != null && c.moveToFirst()) {
                do {
                    SoilHealthRecord r = new SoilHealthRecord();
                    r.setId(c.getString(c.getColumnIndexOrThrow("id")));
                    r.setFarmerPhone(c.getString(c.getColumnIndexOrThrow("farmer_phone")));
                    r.setSampleDate(c.getString(c.getColumnIndexOrThrow("sample_date")));
                    r.setFieldName(c.getString(c.getColumnIndexOrThrow("field_name")));
                    if (!c.isNull(c.getColumnIndexOrThrow("ph"))) r.setPh(c.getDouble(c.getColumnIndexOrThrow("ph")));
                    if (!c.isNull(c.getColumnIndexOrThrow("electrical_conductivity"))) r.setElectricalConductivity(c.getDouble(c.getColumnIndexOrThrow("electrical_conductivity")));
                    if (!c.isNull(c.getColumnIndexOrThrow("organic_carbon"))) r.setOrganicCarbon(c.getDouble(c.getColumnIndexOrThrow("organic_carbon")));
                    if (!c.isNull(c.getColumnIndexOrThrow("nitrogen_kg_ha"))) r.setNitrogenKgHa(c.getDouble(c.getColumnIndexOrThrow("nitrogen_kg_ha")));
                    if (!c.isNull(c.getColumnIndexOrThrow("phosphorus_kg_ha"))) r.setPhosphorusKgHa(c.getDouble(c.getColumnIndexOrThrow("phosphorus_kg_ha")));
                    if (!c.isNull(c.getColumnIndexOrThrow("potassium_kg_ha"))) r.setPotassiumKgHa(c.getDouble(c.getColumnIndexOrThrow("potassium_kg_ha")));
                    if (!c.isNull(c.getColumnIndexOrThrow("zinc_ppm"))) r.setZincPpm(c.getDouble(c.getColumnIndexOrThrow("zinc_ppm")));
                    if (!c.isNull(c.getColumnIndexOrThrow("iron_ppm"))) r.setIronPpm(c.getDouble(c.getColumnIndexOrThrow("iron_ppm")));
                    r.setFertilityRating(c.getString(c.getColumnIndexOrThrow("fertility_rating")));
                    r.setAdvisoryNotes(c.getString(c.getColumnIndexOrThrow("advisory_notes")));
                    list.add(r);
                } while (c.moveToNext());
            }
        } finally {
            if (c != null) c.close();
        }
        return list;
    }

    // =========================================================================
    // FARM EXPENSES & PROFITABILITY CRUD
    // =========================================================================

    public boolean insertFarmExpense(FarmExpense e) {
        if (e == null) return false;
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("id", e.getId());
        cv.put("farmer_phone", e.getFarmerPhone());
        cv.put("entry_type", e.getEntryType());
        cv.put("category", e.getCategory());
        cv.put("title", e.getTitle());
        cv.put("amount", e.getAmount() != null ? e.getAmount() : 0.0);
        cv.put("entry_date", e.getEntryDate());
        cv.put("crop_season", e.getCropSeason());
        cv.put("notes", e.getNotes());
        return db.insertWithOnConflict("farm_expenses", null, cv, SQLiteDatabase.CONFLICT_REPLACE) != -1;
    }

    public List<FarmExpense> getFarmExpenses(String phone) {
        List<FarmExpense> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = null;
        try {
            if (phone != null && !phone.trim().isEmpty()) {
                c = db.query("farm_expenses", null, "farmer_phone = ?", new String[]{phone}, null, null, "entry_date DESC");
            } else {
                c = db.query("farm_expenses", null, null, null, null, null, "entry_date DESC");
            }
            if (c != null && c.moveToFirst()) {
                do {
                    FarmExpense e = new FarmExpense();
                    e.setId(c.getString(c.getColumnIndexOrThrow("id")));
                    e.setFarmerPhone(c.getString(c.getColumnIndexOrThrow("farmer_phone")));
                    e.setEntryType(c.getString(c.getColumnIndexOrThrow("entry_type")));
                    e.setCategory(c.getString(c.getColumnIndexOrThrow("category")));
                    e.setTitle(c.getString(c.getColumnIndexOrThrow("title")));
                    e.setAmount(c.getDouble(c.getColumnIndexOrThrow("amount")));
                    e.setEntryDate(c.getString(c.getColumnIndexOrThrow("entry_date")));
                    e.setCropSeason(c.getString(c.getColumnIndexOrThrow("crop_season")));
                    e.setNotes(c.getString(c.getColumnIndexOrThrow("notes")));
                    list.add(e);
                } while (c.moveToNext());
            }
        } finally {
            if (c != null) c.close();
        }
        return list;
    }

    public boolean deleteFarmExpense(String id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete("farm_expenses", "id = ?", new String[]{id}) > 0;
    }

    // =========================================================================
    // CROP DISEASE REPORTS CRUD
    // =========================================================================

    public boolean insertDiseaseReport(DiseaseReport r) {
        if (r == null) return false;
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("id", r.getId());
        cv.put("farmer_phone", r.getFarmerPhone());
        cv.put("farmer_name", r.getFarmerName());
        cv.put("crop_name", r.getCropName());
        cv.put("variety", r.getVariety());
        cv.put("affected_part", r.getAffectedPart());
        cv.put("symptoms", r.getSymptoms());
        cv.put("severity", r.getSeverity());
        cv.put("village", r.getVillage());
        cv.put("taluka", r.getTaluka());
        cv.put("district", r.getDistrict());
        cv.put("latitude", r.getLatitude());
        cv.put("longitude", r.getLongitude());
        cv.put("location_consent", (r.getLocationConsent() != null && r.getLocationConsent()) ? 1 : 0);
        cv.put("is_duplicate", (r.getIsDuplicate() != null && r.getIsDuplicate()) ? 1 : 0);
        cv.put("duplicate_of_report_id", r.getDuplicateOfReportId());
        cv.put("matched_advisory_id", r.getMatchedAdvisoryId());
        cv.put("image_url", r.getImageUrl());
        cv.put("reported_at", r.getReportedAt());
        cv.put("status", r.getStatus());
        cv.put("admin_advisory", r.getAdminAdvisory());
        cv.put("problem_category", r.getProblemCategory());
        cv.put("growth_stage", r.getGrowthStage());
        cv.put("review_note", r.getReviewNote());
        cv.put("admin_guidance", r.getAdminGuidance());
        cv.put("admin_source", r.getAdminSource());
        cv.put("reviewed_by", r.getReviewedBy());
        cv.put("reviewed_at", r.getReviewedAt());
        return db.insertWithOnConflict("crop_disease_reports", null, cv, SQLiteDatabase.CONFLICT_REPLACE) != -1;
    }

    public List<DiseaseReport> getDiseaseReports(String phone) {
        List<DiseaseReport> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = null;
        try {
            if (phone != null && !phone.trim().isEmpty()) {
                c = db.query("crop_disease_reports", null, "farmer_phone = ?", new String[]{phone}, null, null, "reported_at DESC");
            } else {
                c = db.query("crop_disease_reports", null, null, null, null, null, "reported_at DESC");
            }
            if (c != null && c.moveToFirst()) {
                do {
                    DiseaseReport r = new DiseaseReport();
                    r.setId(c.getString(c.getColumnIndexOrThrow("id")));
                    r.setFarmerPhone(c.getString(c.getColumnIndexOrThrow("farmer_phone")));
                    r.setFarmerName(c.getString(c.getColumnIndexOrThrow("farmer_name")));
                    r.setCropName(c.getString(c.getColumnIndexOrThrow("crop_name")));
                    r.setAffectedPart(c.getString(c.getColumnIndexOrThrow("affected_part")));
                    r.setSymptoms(c.getString(c.getColumnIndexOrThrow("symptoms")));
                    r.setSeverity(c.getString(c.getColumnIndexOrThrow("severity")));
                    r.setVillage(c.getString(c.getColumnIndexOrThrow("village")));
                    r.setDistrict(c.getString(c.getColumnIndexOrThrow("district")));
                    r.setImageUrl(c.getString(c.getColumnIndexOrThrow("image_url")));
                    r.setReportedAt(c.getString(c.getColumnIndexOrThrow("reported_at")));
                    r.setStatus(c.getString(c.getColumnIndexOrThrow("status")));
                    r.setAdminAdvisory(c.getString(c.getColumnIndexOrThrow("admin_advisory")));

                    int idxVar = c.getColumnIndex("variety");
                    if (idxVar != -1) r.setVariety(c.getString(idxVar));
                    int idxTaluka = c.getColumnIndex("taluka");
                    if (idxTaluka != -1) r.setTaluka(c.getString(idxTaluka));
                    int idxLat = c.getColumnIndex("latitude");
                    if (idxLat != -1 && !c.isNull(idxLat)) r.setLatitude(c.getDouble(idxLat));
                    int idxLng = c.getColumnIndex("longitude");
                    if (idxLng != -1 && !c.isNull(idxLng)) r.setLongitude(c.getDouble(idxLng));
                    int idxConsent = c.getColumnIndex("location_consent");
                    if (idxConsent != -1) r.setLocationConsent(c.getInt(idxConsent) == 1);
                    int idxDup = c.getColumnIndex("is_duplicate");
                    if (idxDup != -1) r.setIsDuplicate(c.getInt(idxDup) == 1);
                    int idxDupOf = c.getColumnIndex("duplicate_of_report_id");
                    if (idxDupOf != -1) r.setDuplicateOfReportId(c.getString(idxDupOf));
                    int idxAdv = c.getColumnIndex("matched_advisory_id");
                    if (idxAdv != -1) r.setMatchedAdvisoryId(c.getString(idxAdv));

                    int idxCat = c.getColumnIndex("problem_category");
                    if (idxCat != -1) r.setProblemCategory(c.getString(idxCat));
                    int idxStage = c.getColumnIndex("growth_stage");
                    if (idxStage != -1) r.setGrowthStage(c.getString(idxStage));
                    int idxNote = c.getColumnIndex("review_note");
                    if (idxNote != -1) r.setReviewNote(c.getString(idxNote));
                    int idxGuidance = c.getColumnIndex("admin_guidance");
                    if (idxGuidance != -1) r.setAdminGuidance(c.getString(idxGuidance));
                    int idxSource = c.getColumnIndex("admin_source");
                    if (idxSource != -1) r.setAdminSource(c.getString(idxSource));
                    int idxReviewer = c.getColumnIndex("reviewed_by");
                    if (idxReviewer != -1) r.setReviewedBy(c.getString(idxReviewer));
                    int idxRevAt = c.getColumnIndex("reviewed_at");
                    if (idxRevAt != -1) r.setReviewedAt(c.getString(idxRevAt));

                    list.add(r);
                } while (c.moveToNext());
            }
        } finally {
            if (c != null) c.close();
        }
        return list;
    }

    // =========================================================================
    // COMMUNITY CROP ALERTS CRUD
    // =========================================================================

    public boolean insertCropAlert(com.agrosmart.models.CropAlert a) {
        if (a == null) return false;
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("id", a.getId());
        cv.put("crop_name", a.getCropName());
        cv.put("district", a.getDistrict());
        cv.put("village", a.getVillage());
        cv.put("alert_title", a.getAlertTitle());
        cv.put("severity", a.getSeverity());
        cv.put("summary", a.getSummary());
        cv.put("report_count", a.getReportCount());
        cv.put("authority", a.getAuthority());
        cv.put("recommended_actions", a.getRecommendedActions());
        cv.put("status", a.getStatus());
        cv.put("published_by", a.getPublishedBy());
        cv.put("published_at", a.getPublishedAt());
        cv.put("resolved_at", a.getResolvedAt());
        return db.insertWithOnConflict("crop_alerts", null, cv, SQLiteDatabase.CONFLICT_REPLACE) != -1;
    }

    public List<com.agrosmart.models.CropAlert> getCropAlerts(String district, String status) {
        List<com.agrosmart.models.CropAlert> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = null;
        try {
            String selection = null;
            List<String> argsList = new ArrayList<>();

            if (status != null && !status.trim().isEmpty() && !"ALL".equalsIgnoreCase(status)) {
                selection = "status = ?";
                argsList.add(status.trim().toUpperCase());
            }
            if (district != null && !district.trim().isEmpty()) {
                if (selection == null) {
                    selection = "district = ?";
                } else {
                    selection += " AND district = ?";
                }
                argsList.add(district.trim());
            }

            String[] args = argsList.isEmpty() ? null : argsList.toArray(new String[0]);
            c = db.query("crop_alerts", null, selection, args, null, null, "published_at DESC");

            if (c != null && c.moveToFirst()) {
                do {
                    com.agrosmart.models.CropAlert a = new com.agrosmart.models.CropAlert();
                    a.setId(c.getString(c.getColumnIndexOrThrow("id")));
                    a.setCropName(c.getString(c.getColumnIndexOrThrow("crop_name")));
                    a.setDistrict(c.getString(c.getColumnIndexOrThrow("district")));
                    a.setVillage(c.getString(c.getColumnIndexOrThrow("village")));
                    a.setAlertTitle(c.getString(c.getColumnIndexOrThrow("alert_title")));
                    a.setSeverity(c.getString(c.getColumnIndexOrThrow("severity")));
                    a.setSummary(c.getString(c.getColumnIndexOrThrow("summary")));
                    a.setReportCount(c.getInt(c.getColumnIndexOrThrow("report_count")));
                    a.setAuthority(c.getString(c.getColumnIndexOrThrow("authority")));
                    a.setRecommendedActions(c.getString(c.getColumnIndexOrThrow("recommended_actions")));
                    a.setStatus(c.getString(c.getColumnIndexOrThrow("status")));
                    a.setPublishedBy(c.getString(c.getColumnIndexOrThrow("published_by")));
                    a.setPublishedAt(c.getString(c.getColumnIndexOrThrow("published_at")));
                    a.setResolvedAt(c.getString(c.getColumnIndexOrThrow("resolved_at")));
                    list.add(a);
                } while (c.moveToNext());
            }
        } finally {
            if (c != null) c.close();
        }
        return list;
    }

    // =========================================================================
    // CROP ADVISORY CATALOGUE CRUD (Dr. PDKV Akola & CROPSAP)
    // =========================================================================

    public boolean insertCropAdvisory(CropAdvisory adv) {
        if (adv == null) return false;
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("id", adv.getId());
        cv.put("title", adv.getTitle());
        cv.put("crop_name", adv.getCropName());
        cv.put("variety", adv.getVariety());
        cv.put("problem_category", adv.getProblemCategory());
        cv.put("location_coverage", adv.getLocationCoverage());
        cv.put("advisory_text", adv.getAdvisoryText());
        cv.put("advisory_text_marathi", adv.getAdvisoryTextMarathi());
        cv.put("language", adv.getLanguage());
        cv.put("source_name", adv.getSourceName());
        cv.put("source_url", adv.getSourceUrl());
        cv.put("publication_date", adv.getPublicationDate());
        cv.put("date_added", adv.getDateAdded());
        cv.put("validity_period", adv.getValidityPeriod());
        cv.put("verification_status", adv.getVerificationStatus());
        cv.put("reviewing_admin", adv.getReviewingAdmin());
        cv.put("last_reviewed_at", adv.getLastReviewedAt());
        cv.put("ingestion_type", adv.getIngestionType());
        return db.insertWithOnConflict("crop_advisories", null, cv, SQLiteDatabase.CONFLICT_REPLACE) != -1;
    }

    public List<CropAdvisory> getCropAdvisories(String crop, String status) {
        List<CropAdvisory> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = null;
        try {
            String selection = null;
            List<String> argsList = new ArrayList<>();

            if (crop != null && !crop.trim().isEmpty() && !"All".equalsIgnoreCase(crop)) {
                selection = "crop_name = ?";
                argsList.add(crop.trim());
            }
            if (status != null && !status.trim().isEmpty() && !"ALL".equalsIgnoreCase(status)) {
                if (selection == null) {
                    selection = "verification_status = ?";
                } else {
                    selection += " AND verification_status = ?";
                }
                argsList.add(status.trim().toUpperCase());
            }

            String[] args = argsList.isEmpty() ? null : argsList.toArray(new String[0]);
            c = db.query("crop_advisories", null, selection, args, null, null, "publication_date DESC");

            if (c != null && c.moveToFirst()) {
                do {
                    CropAdvisory adv = new CropAdvisory();
                    adv.setId(c.getString(c.getColumnIndexOrThrow("id")));
                    adv.setTitle(c.getString(c.getColumnIndexOrThrow("title")));
                    adv.setCropName(c.getString(c.getColumnIndexOrThrow("crop_name")));
                    adv.setVariety(c.getString(c.getColumnIndexOrThrow("variety")));
                    adv.setProblemCategory(c.getString(c.getColumnIndexOrThrow("problem_category")));
                    adv.setLocationCoverage(c.getString(c.getColumnIndexOrThrow("location_coverage")));
                    adv.setAdvisoryText(c.getString(c.getColumnIndexOrThrow("advisory_text")));
                    adv.setAdvisoryTextMarathi(c.getString(c.getColumnIndexOrThrow("advisory_text_marathi")));
                    adv.setLanguage(c.getString(c.getColumnIndexOrThrow("language")));
                    adv.setSourceName(c.getString(c.getColumnIndexOrThrow("source_name")));
                    adv.setSourceUrl(c.getString(c.getColumnIndexOrThrow("source_url")));
                    adv.setPublicationDate(c.getString(c.getColumnIndexOrThrow("publication_date")));
                    adv.setDateAdded(c.getString(c.getColumnIndexOrThrow("date_added")));
                    adv.setValidityPeriod(c.getString(c.getColumnIndexOrThrow("validity_period")));
                    adv.setVerificationStatus(c.getString(c.getColumnIndexOrThrow("verification_status")));
                    adv.setReviewingAdmin(c.getString(c.getColumnIndexOrThrow("reviewing_admin")));
                    adv.setLastReviewedAt(c.getString(c.getColumnIndexOrThrow("last_reviewed_at")));
                    adv.setIngestionType(c.getString(c.getColumnIndexOrThrow("ingestion_type")));
                    list.add(adv);
                } while (c.moveToNext());
            }
        } finally {
            if (c != null) c.close();
        }
        return list;
    }

    private void seedBaselineAdvisoriesIfEmpty(SQLiteDatabase db) {
        try {
            Cursor c = db.rawQuery("SELECT COUNT(*) FROM crop_advisories", null);
            if (c != null && c.moveToFirst()) {
                int count = c.getInt(0);
                c.close();
                if (count > 0) return;
            }
        } catch (Exception ignored) {
            return;
        }

        ContentValues[] items = new ContentValues[5];

        items[0] = new ContentValues();
        items[0].put("id", "adv-pdkv-001");
        items[0].put("title", "Yellow Mosaic Virus & Whitefly Management in Soybean");
        items[0].put("crop_name", "Soybean");
        items[0].put("variety", "JS-335, JS-9305, NRC-37");
        items[0].put("problem_category", "Unusual Leaf Spots & Blights");
        items[0].put("location_coverage", "Vidarbha (Akola, Amravati, Yavatmal, Nagpur)");
        items[0].put("advisory_text", "Soybean yellow mosaic virus is transmitted by whiteflies (Bemisia tabaci). Observe leaves for yellow mottling and vein clearing. Action: Rogue out severely infected plants. Spray Thiamethoxam 25% WG @ 4 g/10 L water or Acetamiprid 20% SP @ 2 g/10 L water. Maintain weed-free bunds.");
        items[0].put("advisory_text_marathi", "सोयाबीनवरील पिवळा मोझॅक विषाणू पांढऱ्या माशीद्वारे पसरतो. पानांवर पिवळे चट्टे दिसल्यास बाधित झाडे उपटून नष्ट करा. थायामेथोक्साम २५% डब्ल्यूजी ४ ग्रॅम किंवा ॲसिटामिप्रिड २०% एसपी २ ग्रॅम प्रति १० लिटर पाण्यात मिसळून फवारणी करावी.");
        items[0].put("language", "BILINGUAL");
        items[0].put("source_name", "Dr. PDKV Akola E-Pest Surveillance Advisory");
        items[0].put("source_url", "https://www.pdkv.ac.in/?page_id=203");
        items[0].put("publication_date", "Kharif 2026");
        items[0].put("date_added", "2026-10-28");
        items[0].put("validity_period", "Valid throughout Kharif 2026");
        items[0].put("verification_status", "VERIFIED");
        items[0].put("reviewing_admin", "AgroSmart State Agronomy Cell");
        items[0].put("last_reviewed_at", "2026-10-28");
        items[0].put("ingestion_type", "MANUAL_ENTRY");

        items[1] = new ContentValues();
        items[1].put("id", "adv-cropsap-002");
        items[1].put("title", "Pink Bollworm & Sucking Pest Advisory for Bt Cotton");
        items[1].put("crop_name", "Cotton");
        items[1].put("variety", "Bt Cotton Hybrids");
        items[1].put("problem_category", "Pest & Insect Infestations");
        items[1].put("location_coverage", "Marathwada & Western Maharashtra");
        items[1].put("advisory_text", "Monitor cotton squares and flowers for rosette flowers caused by Pink Bollworm (Pectinophora gossypiella). Install 5 pheromone traps per ha. If ETL reaches 8 moths/trap/night for 3 consecutive days, spray Profenofos 50% EC @ 30 ml or Emamectin Benzoate 5% SG @ 5 g per 10 L water.");
        items[1].put("advisory_text_marathi", "गुलाबी बोंडअळीच्या व्यवस्थापनासाठी हेक्टरी ५ कामगंध सापळे लावावेत. आर्थिक नुकसान पातळी ओलांडल्यास प्रोफेनोफॉस ५०% ईसी ३० मिली किंवा इमामेक्टिन बेन्झोएट ५% एसजी ५ ग्रॅम प्रति १० लिटर पाण्यात मिसळून फवारावे.");
        items[1].put("language", "BILINGUAL");
        items[1].put("source_name", "Maharashtra Department of Agriculture - CROPSAP Pest Bulletin");
        items[1].put("source_url", "https://krishi.maharashtra.gov.in/");
        items[1].put("publication_date", "October 2026");
        items[1].put("date_added", "2026-10-28");
        items[1].put("validity_period", "Valid through Harvest 2026");
        items[1].put("verification_status", "VERIFIED");
        items[1].put("reviewing_admin", "State Plant Protection Officer");
        items[1].put("last_reviewed_at", "2026-10-28");
        items[1].put("ingestion_type", "MANUAL_ENTRY");

        items[2] = new ContentValues();
        items[2].put("id", "adv-pdkv-003");
        items[2].put("title", "Early Shoot Borer & Pokkah Boeng Advisory in Sugarcane");
        items[2].put("crop_name", "Sugarcane");
        items[2].put("variety", "Co-86032, Co-0265");
        items[2].put("problem_category", "Stem & Root Damage");
        items[2].put("location_coverage", "Western Maharashtra (Kolhapur, Pune, Sangli)");
        items[2].put("advisory_text", "Dead hearts observed in young shoots during early tillering. Ensure proper light earthing-up and trash mulching. For Pokkah Boeng leaf twisting, drench/spray Copper Oxychloride 50% WP @ 2.5 g/L or Carbendazim 50% WP @ 1 g/L.");
        items[2].put("advisory_text_marathi", "उसामधील खोडकिडीच्या नियंत्रणासाठी हलकी खांदणी करा व पाचटाचे आच्छादन ठेवा. पोक्का बोईंगच्या नियंत्रणासाठी कॉपर ऑक्सिक्लोराईड २.५ ग्रॅम किंवा कार्बेन्डाझिम १ ग्रॅम प्रति लिटर पाण्यात मिसळून फवारावे.");
        items[2].put("language", "BILINGUAL");
        items[2].put("source_name", "Dr. PDKV Akola Regional Sugarcane Research Advisory");
        items[2].put("source_url", "https://www.pdkv.ac.in/?page_id=203");
        items[2].put("publication_date", "Pre-Seasonal & Suru 2026");
        items[2].put("date_added", "2026-10-28");
        items[2].put("validity_period", "Valid through 2026");
        items[2].put("verification_status", "VERIFIED");
        items[2].put("reviewing_admin", "AgroSmart State Agronomy Cell");
        items[2].put("last_reviewed_at", "2026-10-28");
        items[2].put("ingestion_type", "MANUAL_ENTRY");

        items[3] = new ContentValues();
        items[3].put("id", "adv-cropsap-004");
        items[3].put("title", "Early & Late Blight Management in Tomato");
        items[3].put("crop_name", "Tomato");
        items[3].put("variety", "Abhinav, US-440, Local Hybrids");
        items[3].put("problem_category", "Unusual Leaf Spots & Blights");
        items[3].put("location_coverage", "Nashik, Ahmednagar, Pune");
        items[3].put("advisory_text", "Concentric target-board rings on leaves indicate Early Blight (Alternaria solani). Avoid overhead sprinkling and ensure proper plant staking. Apply prophylactic spray of Mancozeb 75% WP @ 2.5 g/L or Chlorothalonil 75% WP @ 2 g/L.");
        items[3].put("advisory_text_marathi", "टोमॅटोवरील करपा रोगाच्या नियंत्रणासाठी झाडांना आधार द्या व जास्त आर्द्रता टाळा. मॅन्कोझेब ७५% डब्ल्यूपी २.५ ग्रॅम किंवा क्लोरोथॅलोनिल २ ग्रॅम प्रति लिटर पाण्यात मिसळून फवारावे.");
        items[3].put("language", "BILINGUAL");
        items[3].put("source_name", "Maharashtra Department of Agriculture - CROPSAP Horticultural Advisory");
        items[3].put("source_url", "https://krishi.maharashtra.gov.in/");
        items[3].put("publication_date", "September-October 2026");
        items[3].put("date_added", "2026-10-28");
        items[3].put("validity_period", "Valid through Rabi 2026");
        items[3].put("verification_status", "VERIFIED");
        items[3].put("reviewing_admin", "State Plant Protection Officer");
        items[3].put("last_reviewed_at", "2026-10-28");
        items[3].put("ingestion_type", "MANUAL_ENTRY");

        items[4] = new ContentValues();
        items[4].put("id", "adv-pdkv-005");
        items[4].put("title", "Fusarium Wilt & Root Rot Alert in Chickpea (Gram)");
        items[4].put("crop_name", "Chickpea (Gram)");
        items[4].put("variety", "Vijay, Digvijay, BDNG-797");
        items[4].put("problem_category", "Wilting & Drying of Plants");
        items[4].put("location_coverage", "Central & Western Maharashtra");
        items[4].put("advisory_text", "Wilting and yellowing in seedling to early vegetative stage caused by Fusarium oxysporum f. sp. ciceris. Avoid water stagnation. Seed treatment with Trichoderma viride @ 5 g/kg seed or Carbendazim @ 2 g/kg seed before sowing is essential.");
        items[4].put("advisory_text_marathi", "हरभऱ्यावरील मर रोगाच्या नियंत्रणासाठी ट्रायकोडर्मा व्हिरिडी ५ ग्रॅम किंवा कार्बेन्डाझिम २ ग्रॅम प्रति किलो बियाण्यास चोळून बीजप्रक्रिया करावी. शेतात पाणी साचू देऊ नये.");
        items[4].put("language", "BILINGUAL");
        items[4].put("source_name", "Dr. PDKV Akola Pulses Research Advisory");
        items[4].put("source_url", "https://www.pdkv.ac.in/?page_id=203");
        items[4].put("publication_date", "Rabi 2026");
        items[4].put("date_added", "2026-10-28");
        items[4].put("validity_period", "Valid throughout Rabi 2026-27");
        items[4].put("verification_status", "VERIFIED");
        items[4].put("reviewing_admin", "AgroSmart State Agronomy Cell");
        items[4].put("last_reviewed_at", "2026-10-28");
        items[4].put("ingestion_type", "MANUAL_ENTRY");

        for (ContentValues cv : items) {
            db.insertWithOnConflict("crop_advisories", null, cv, SQLiteDatabase.CONFLICT_REPLACE);
        }
    }

    // =========================================================================
    // FARMER FIELD FEEDBACK CRUD
    // =========================================================================

    public boolean insertFeedback(FarmerFeedback f) {
        if (f == null) return false;
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("id", f.getId());
        cv.put("farmer_phone", f.getFarmerPhone());
        cv.put("farmer_name", f.getFarmerName());
        cv.put("rating", f.getRating());
        cv.put("category", f.getCategory());
        cv.put("comments", f.getComments());
        cv.put("app_version", f.getAppVersion());
        cv.put("submitted_at", f.getSubmittedAt());
        return db.insertWithOnConflict("farmer_feedback", null, cv, SQLiteDatabase.CONFLICT_REPLACE) != -1;
    }

    public List<FarmerFeedback> getAllFarmerFeedback() {
        List<FarmerFeedback> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = null;
        try {
            c = db.query("farmer_feedback", null, null, null, null, null, "submitted_at DESC");
            if (c != null && c.moveToFirst()) {
                do {
                    FarmerFeedback f = new FarmerFeedback();
                    f.setId(c.getString(c.getColumnIndexOrThrow("id")));
                    f.setFarmerPhone(c.getString(c.getColumnIndexOrThrow("farmer_phone")));
                    f.setFarmerName(c.getString(c.getColumnIndexOrThrow("farmer_name")));
                    f.setRating(c.getInt(c.getColumnIndexOrThrow("rating")));
                    f.setCategory(c.getString(c.getColumnIndexOrThrow("category")));
                    f.setComments(c.getString(c.getColumnIndexOrThrow("comments")));
                    f.setAppVersion(c.getString(c.getColumnIndexOrThrow("app_version")));
                    f.setSubmittedAt(c.getString(c.getColumnIndexOrThrow("submitted_at")));
                    list.add(f);
                } while (c.moveToNext());
            }
        } finally {
            if (c != null) c.close();
        }
        return list;
    }

    // =========================================================================
    // SAVED CROP PLANS & SIMULATOR PERSISTENCE CRUD
    // =========================================================================

    public boolean insertOrUpdateCropPlan(SavedCropPlanModel plan) {
        if (plan == null) return false;
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("id", plan.getId() != null ? plan.getId() : "PLAN-" + System.currentTimeMillis());
        if (plan.getFarmerId() != null) cv.put("farmer_id", plan.getFarmerId());
        cv.put("farmer_phone", plan.getFarmerPhone() != null ? plan.getFarmerPhone() : "");
        cv.put("farmer_name", plan.getFarmerName());
        cv.put("district", plan.getDistrict() != null ? plan.getDistrict() : "");
        cv.put("taluka", plan.getTaluka());
        cv.put("season", plan.getSeason() != null ? plan.getSeason() : "");
        cv.put("crop_name", plan.getCropName() != null ? plan.getCropName() : "");
        cv.put("variety", plan.getVariety());
        cv.put("plan_type", plan.getPlanType() != null ? plan.getPlanType() : "BALANCED_BEST_FIT");
        cv.put("water_availability", plan.getWaterAvailability());
        cv.put("land_type", plan.getLandType());
        cv.put("soil_type", plan.getSoilType());
        cv.put("previous_crop", plan.getPreviousCrop());
        cv.put("investment_budget", plan.getInvestmentBudget());
        cv.put("farmer_goal", plan.getFarmerGoal());
        cv.put("duration_days", plan.getDurationDays());
        if (plan.getEstimatedCostPerAcre() != null) {
            cv.put("estimated_cost_per_acre", plan.getEstimatedCostPerAcre());
        }
        cv.put("target_yield", plan.getTargetYield());
        cv.put("suitability_reason", plan.getSuitabilityReason());
        cv.put("key_risks", plan.getKeyRisks());
        cv.put("university_source", plan.getUniversitySource());
        cv.put("is_planted", plan.getIsPlanted() ? 1 : 0);
        cv.put("created_date", plan.getCreatedDate() != null ? plan.getCreatedDate() : "");

        long rowId = db.insertWithOnConflict("saved_crop_plans", null, cv, SQLiteDatabase.CONFLICT_REPLACE);
        return rowId != -1;
    }

    public List<SavedCropPlanModel> getSavedCropPlansByPhone(String phone) {
        List<SavedCropPlanModel> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = null;
        try {
            String selection = null;
            String[] selectionArgs = null;
            if (phone != null && !phone.trim().isEmpty()) {
                selection = "farmer_phone = ?";
                selectionArgs = new String[]{phone.trim()};
            }
            c = db.query("saved_crop_plans", null, selection, selectionArgs, null, null, "created_date DESC");
            if (c != null && c.moveToFirst()) {
                do {
                    SavedCropPlanModel p = new SavedCropPlanModel();
                    p.setId(c.getString(c.getColumnIndexOrThrow("id")));
                    if (!c.isNull(c.getColumnIndexOrThrow("farmer_id"))) {
                        p.setFarmerId(c.getLong(c.getColumnIndexOrThrow("farmer_id")));
                    }
                    p.setFarmerPhone(c.getString(c.getColumnIndexOrThrow("farmer_phone")));
                    p.setFarmerName(c.getString(c.getColumnIndexOrThrow("farmer_name")));
                    p.setDistrict(c.getString(c.getColumnIndexOrThrow("district")));
                    p.setTaluka(c.getString(c.getColumnIndexOrThrow("taluka")));
                    p.setSeason(c.getString(c.getColumnIndexOrThrow("season")));
                    p.setCropName(c.getString(c.getColumnIndexOrThrow("crop_name")));
                    p.setVariety(c.getString(c.getColumnIndexOrThrow("variety")));
                    p.setPlanType(c.getString(c.getColumnIndexOrThrow("plan_type")));
                    p.setWaterAvailability(c.getString(c.getColumnIndexOrThrow("water_availability")));
                    p.setLandType(c.getString(c.getColumnIndexOrThrow("land_type")));
                    p.setSoilType(c.getString(c.getColumnIndexOrThrow("soil_type")));
                    p.setPreviousCrop(c.getString(c.getColumnIndexOrThrow("previous_crop")));
                    p.setInvestmentBudget(c.getString(c.getColumnIndexOrThrow("investment_budget")));
                    p.setFarmerGoal(c.getString(c.getColumnIndexOrThrow("farmer_goal")));
                    p.setDurationDays(c.getString(c.getColumnIndexOrThrow("duration_days")));
                    if (!c.isNull(c.getColumnIndexOrThrow("estimated_cost_per_acre"))) {
                        p.setEstimatedCostPerAcre(c.getDouble(c.getColumnIndexOrThrow("estimated_cost_per_acre")));
                    }
                    p.setTargetYield(c.getString(c.getColumnIndexOrThrow("target_yield")));
                    p.setSuitabilityReason(c.getString(c.getColumnIndexOrThrow("suitability_reason")));
                    p.setKeyRisks(c.getString(c.getColumnIndexOrThrow("key_risks")));
                    p.setUniversitySource(c.getString(c.getColumnIndexOrThrow("university_source")));
                    p.setIsPlanted(c.getInt(c.getColumnIndexOrThrow("is_planted")) == 1);
                    p.setCreatedDate(c.getString(c.getColumnIndexOrThrow("created_date")));
                    list.add(p);
                } while (c.moveToNext());
            }
        } finally {
            if (c != null) c.close();
        }
        return list;
    }

    public boolean updateCropPlanPlantedStatus(String id, boolean isPlanted) {
        if (id == null) return false;
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("is_planted", isPlanted ? 1 : 0);
        return db.update("saved_crop_plans", cv, "id = ?", new String[]{id}) > 0;
    }

    public boolean deleteSavedCropPlan(String id) {
        if (id == null) return false;
        SQLiteDatabase db = getWritableDatabase();
        return db.delete("saved_crop_plans", "id = ?", new String[]{id}) > 0;
    }

    // --- WEATHER FORECAST & ADVISORY OFFLINE CACHING (PS 5.4) ---

    public synchronized void saveWeatherForecastCache(com.agrosmart.models.WeatherResponse weather) {
        if (weather == null || weather.getCityName() == null) return;
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        String id = weather.getCityName().toLowerCase().replaceAll("[^a-z0-9]", "_");
        cv.put("id", id);
        cv.put("location_name", weather.getCityName());
        cv.put("current_temp", weather.getTemp());
        cv.put("feels_like", weather.getFeelsLike());
        cv.put("condition", weather.getCondition());
        cv.put("description", weather.getDescription());
        cv.put("humidity", weather.getHumidity());
        cv.put("wind_speed", weather.getWindSpeed());
        cv.put("rain_chance", weather.getRainChance());
        cv.put("rainfall_mm", weather.getRainfallMm());
        cv.put("risk_level", weather.getOverallRiskLevel());
        cv.put("five_day_summary", weather.getFiveDaySummary());

        com.google.gson.Gson gson = new com.google.gson.Gson();
        cv.put("forecast_json", weather.getForecast() != null ? gson.toJson(weather.getForecast()) : "[]");
        cv.put("advisories_json", weather.getAdvisories() != null ? gson.toJson(weather.getAdvisories()) : "[]");
        cv.put("retrieved_at", String.valueOf(System.currentTimeMillis()));

        db.insertWithOnConflict("weather_forecast_cache", null, cv, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public synchronized com.agrosmart.models.WeatherResponse getCachedWeatherForecast(String location) {
        if (location == null) return null;
        SQLiteDatabase db = getReadableDatabase();
        String id = location.toLowerCase().replaceAll("[^a-z0-9]", "_");
        Cursor c = null;
        try {
            c = db.rawQuery("SELECT * FROM weather_forecast_cache WHERE id = ? OR location_name LIKE ? LIMIT 1",
                    new String[]{id, "%" + location + "%"});
            if (c != null && c.moveToFirst()) {
                com.agrosmart.models.WeatherResponse w = new com.agrosmart.models.WeatherResponse();
                w.setCityName(c.getString(c.getColumnIndexOrThrow("location_name")));
                w.setTemp(c.getDouble(c.getColumnIndexOrThrow("current_temp")));
                w.setFeelsLike(c.getDouble(c.getColumnIndexOrThrow("feels_like")));
                w.setCondition(c.getString(c.getColumnIndexOrThrow("condition")));
                w.setDescription(c.getString(c.getColumnIndexOrThrow("description")));
                w.setHumidity(c.getInt(c.getColumnIndexOrThrow("humidity")));
                w.setWindSpeed(c.getDouble(c.getColumnIndexOrThrow("wind_speed")));
                w.setRainChance(c.getInt(c.getColumnIndexOrThrow("rain_chance")));
                w.setRainfallMm(c.getDouble(c.getColumnIndexOrThrow("rainfall_mm")));
                w.setOverallRiskLevel(c.getString(c.getColumnIndexOrThrow("risk_level")));
                w.setFiveDaySummary(c.getString(c.getColumnIndexOrThrow("five_day_summary")));

                com.google.gson.Gson gson = new com.google.gson.Gson();
                String fJson = c.getString(c.getColumnIndexOrThrow("forecast_json"));
                if (fJson != null && !fJson.isEmpty()) {
                    java.lang.reflect.Type fType = new com.google.gson.reflect.TypeToken<java.util.List<com.agrosmart.models.ForecastItem>>(){}.getType();
                    w.setForecast(gson.fromJson(fJson, fType));
                }

                String aJson = c.getString(c.getColumnIndexOrThrow("advisories_json"));
                if (aJson != null && !aJson.isEmpty()) {
                    java.lang.reflect.Type aType = new com.google.gson.reflect.TypeToken<java.util.List<com.agrosmart.models.FarmingAdvisory>>(){}.getType();
                    w.setAdvisories(gson.fromJson(aJson, aType));
                }
                return w;
            }
        } catch (Exception ignored) {
        } finally {
            if (c != null) c.close();
        }
        return null;
    }

    public synchronized void saveAdvisoryChange(com.agrosmart.models.WeatherAdvisoryChange change) {
        if (change == null) return;
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("farmer_phone", change.getFarmerPhone() != null ? change.getFarmerPhone() : "9876543210");
        cv.put("location_name", change.getLocationName());
        cv.put("forecast_date", change.getForecastDate());
        cv.put("day_name", change.getDayName());
        cv.put("activity", change.getActivity());
        cv.put("previous_status", change.getPreviousStatus());
        cv.put("updated_status", change.getUpdatedStatus());
        cv.put("previous_summary", change.getPreviousForecastSummary());
        cv.put("updated_summary", change.getUpdatedForecastSummary());
        cv.put("change_reason", change.getChangeReason());
        cv.put("notification_status", change.getNotificationDeliveryStatus());
        cv.put("detected_at", change.getDetectedAt() != null ? change.getDetectedAt() : String.valueOf(System.currentTimeMillis()));
        db.insert("weather_advisory_changes", null, cv);
    }

    public synchronized java.util.List<com.agrosmart.models.WeatherAdvisoryChange> getLocalAdvisoryChanges(String phone) {
        java.util.List<com.agrosmart.models.WeatherAdvisoryChange> list = new java.util.ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = null;
        try {
            c = db.rawQuery("SELECT * FROM weather_advisory_changes WHERE farmer_phone = ? ORDER BY id DESC LIMIT 20",
                    new String[]{phone != null ? phone : "9876543210"});
            if (c != null && c.moveToFirst()) {
                do {
                    com.agrosmart.models.WeatherAdvisoryChange ch = new com.agrosmart.models.WeatherAdvisoryChange();
                    ch.setId(c.getLong(c.getColumnIndexOrThrow("id")));
                    ch.setFarmerPhone(c.getString(c.getColumnIndexOrThrow("farmer_phone")));
                    ch.setLocationName(c.getString(c.getColumnIndexOrThrow("location_name")));
                    ch.setForecastDate(c.getString(c.getColumnIndexOrThrow("forecast_date")));
                    ch.setDayName(c.getString(c.getColumnIndexOrThrow("day_name")));
                    ch.setActivity(c.getString(c.getColumnIndexOrThrow("activity")));
                    ch.setPreviousStatus(c.getString(c.getColumnIndexOrThrow("previous_status")));
                    ch.setUpdatedStatus(c.getString(c.getColumnIndexOrThrow("updated_status")));
                    ch.setPreviousForecastSummary(c.getString(c.getColumnIndexOrThrow("previous_summary")));
                    ch.setUpdatedForecastSummary(c.getString(c.getColumnIndexOrThrow("updated_summary")));
                    ch.setChangeReason(c.getString(c.getColumnIndexOrThrow("change_reason")));
                    ch.setNotificationDeliveryStatus(c.getString(c.getColumnIndexOrThrow("notification_status")));
                    ch.setDetectedAt(c.getString(c.getColumnIndexOrThrow("detected_at")));
                    list.add(ch);
                } while (c.moveToNext());
            }
        } catch (Exception ignored) {
        } finally {
            if (c != null) c.close();
        }
        return list;
    }
}
