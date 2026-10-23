package com.agrosmart.backend.services;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CropAdvisorService {

    public static class CropProfile {
        public String cropName;
        public String variety;
        public String zone; // KONKAN, WESTERN_MAHARASHTRA, MARATHWADA, VIDARBHA
        public List<String> districts;
        public List<String> seasons; // Kharif, Rabi, Zaid, Summer, Perennial
        public String minWaterRequirement; // RAINFED, LIMITED, ASSURED
        public String landSuitability; // LOWLAND, UPLAND, SLOPING, PLAINS, ANY
        public String soilTypeFit; // ALLUVIAL, BLACK_COTTON, RED_LATERITIC, SANDY_LOAM, ANY
        public int durationMinDays;
        public int durationMaxDays;
        public String inputCostLevel; // LOW, MEDIUM, HIGH
        public String laborDemand; // LOW, MEDIUM, HIGH
        public double estimatedCostPerAcre;
        public String typicalYieldPerAcre;
        public List<String> rotationSynergyWith; // e.g. ["Cotton", "Sugarcane", "Wheat"]
        public List<String> rotationHarmfulWith; // e.g. ["Soybean", "Tur"]
        public String universitySource;
        public String suitabilityReason;
        public String risksAndPrecautions;
        public List<String> timelineMilestones;

        public CropProfile(String cropName, String variety, String zone, List<String> districts,
                           List<String> seasons, String minWaterRequirement, String landSuitability,
                           String soilTypeFit, int durationMinDays, int durationMaxDays,
                           String inputCostLevel, String laborDemand, double estimatedCostPerAcre,
                           String typicalYieldPerAcre, List<String> rotationSynergyWith,
                           List<String> rotationHarmfulWith, String universitySource,
                           String suitabilityReason, String risksAndPrecautions,
                           List<String> timelineMilestones) {
            this.cropName = cropName;
            this.variety = variety;
            this.zone = zone;
            this.districts = districts;
            this.seasons = seasons;
            this.minWaterRequirement = minWaterRequirement;
            this.landSuitability = landSuitability;
            this.soilTypeFit = soilTypeFit;
            this.durationMinDays = durationMinDays;
            this.durationMaxDays = durationMaxDays;
            this.inputCostLevel = inputCostLevel;
            this.laborDemand = laborDemand;
            this.estimatedCostPerAcre = estimatedCostPerAcre;
            this.typicalYieldPerAcre = typicalYieldPerAcre;
            this.rotationSynergyWith = rotationSynergyWith;
            this.rotationHarmfulWith = rotationHarmfulWith;
            this.universitySource = universitySource;
            this.suitabilityReason = suitabilityReason;
            this.risksAndPrecautions = risksAndPrecautions;
            this.timelineMilestones = timelineMilestones;
        }
    }

    private final List<CropProfile> knowledgeBase = new ArrayList<>();

    public CropAdvisorService() {
        initKnowledgeBase();
    }

    private void initKnowledgeBase() {
        // ==========================================
        // 1. KONKAN AGRO-CLIMATIC ZONE (DBSKKV DAPOLI)
        // ==========================================
        List<String> konkanDistricts = Arrays.asList("Ratnagiri", "Sindhudurg", "Raigad", "Thane", "Palghar");

        // Konkan Rice (Paddy) - Lowland / Assured Water
        knowledgeBase.add(new CropProfile(
                "Paddy (Rice)", "Karjat-3 / Ratnagiri-24 / Phule Radha",
                "KONKAN", konkanDistricts,
                Collections.singletonList("Kharif"), "ASSURED", "LOWLAND", "RED_LATERITIC",
                115, 125, "MEDIUM", "HIGH", 18000.0, "18 - 22 Qtl/Acre",
                Arrays.asList("Wal", "Cowpea", "Mustard", "Fallow"),
                Collections.singletonList("Paddy (Rice)"),
                "Dr. Balasaheb Sawant Konkan Krishi Vidyapeeth (DBSKKV) Dapoli - Crop Research Station",
                "Specially bred for coastal acidic soils with heavy monsoon precipitation; sturdy culms prevent lodging.",
                "Maintain 5cm standing water during tillering; monitor for stem borer and blast in cloudy weather.",
                Arrays.asList("Day 0: Nursery sowing", "Day 25: Puddling & Transplanting", "Day 45: Tillering N top-dressing", "Day 85: Panicle emergence water check", "Day 120: Harvest & Sun drying")
        ));

        // Konkan Finger Millet (Nagli / Ragi) - Upland / Sloping / Low Water
        knowledgeBase.add(new CropProfile(
                "Finger Millet (Nagli / Ragi)", "Dapoli Nagli-1 / Phule Nachani",
                "KONKAN", konkanDistricts,
                Arrays.asList("Kharif", "Rabi"), "RAINFED", "SLOPING", "ANY",
                95, 105, "LOW", "MEDIUM", 8500.0, "8 - 12 Qtl/Acre",
                Arrays.asList("Groundnut", "Wal", "Sesame"),
                Collections.emptyList(),
                "DBSKKV Dapoli - Millet Improvement Project",
                "Excellent drought resilience on sloppy lateritic varkas lands; high calcium and iron grain content with minimal fertilizer need.",
                "Susceptible to finger blast if continuous waterlogging occurs on flat lands; ensure slope drainage.",
                Arrays.asList("Day 0: Direct drilling/transplant on slopes", "Day 20: First weeding", "Day 50: Heading stage", "Day 100: Earhead harvesting")
        ));

        // Konkan Wal / Lablab Bean - Rabi Residual Moisture Pulse
        knowledgeBase.add(new CropProfile(
                "Wal (Field Bean / Lablab)", "Konkan Wal-1 / Konkan Wal-2",
                "KONKAN", konkanDistricts,
                Collections.singletonList("Rabi"), "LIMITED", "LOWLAND", "ANY",
                85, 95, "LOW", "LOW", 7000.0, "5 - 7 Qtl/Acre",
                Arrays.asList("Paddy (Rice)", "Maize"),
                Collections.singletonList("Wal (Field Bean / Lablab)"),
                "DBSKKV Dapoli - Pulse Research Division",
                "Thrives on residual moisture in rice fallow soils; fixes 35 kg atmospheric N/acre and drastically restores soil fertility.",
                "Sow immediately behind the plow while residual soil moisture exists after paddy harvest.",
                Arrays.asList("Day 0: Relay sowing into moist paddy stubble", "Day 30: Foliar bloom spray", "Day 70: Green pod picking", "Day 90: Dry seed harvest")
        ));

        // Konkan Cowpea - Low Input / Quick Cashflow
        knowledgeBase.add(new CropProfile(
                "Cowpea (Chawli)", "Konkan Safed / Pusa Komal",
                "KONKAN", konkanDistricts,
                Arrays.asList("Kharif", "Rabi", "Summer"), "LIMITED", "UPLAND", "ANY",
                65, 75, "LOW", "LOW", 6500.0, "6 - 8 Qtl/Acre",
                Arrays.asList("Paddy (Rice)", "Nagli", "Millets"),
                Collections.emptyList(),
                "DBSKKV Dapoli - Pulses Unit",
                "Ultra-short duration leguminous crop; exceptional for drought contingency or sudden delayed sowing situations.",
                "Watch for aphid infestation during flowering; spray neem seed kernel extract (NSKE 5%).",
                Arrays.asList("Day 0: Line sowing with Rhizobium inoculant", "Day 20: Interculturing", "Day 50: Pod development", "Day 70: Harvest")
        ));

        // Konkan Cashew / Spices - High-Value Diversification
        knowledgeBase.add(new CropProfile(
                "Cashewnut + Spice Intercrop", "Vengurla-4 / Vengurla-7 + Black Pepper",
                "KONKAN", konkanDistricts,
                Collections.singletonList("Perennial"), "LIMITED", "SLOPING", "RED_LATERITIC",
                180, 365, "HIGH", "MEDIUM", 22000.0, "6 - 10 Qtl/Acre (Nuts)",
                Arrays.asList("Legume cover crops", "Stylosanthes"),
                Collections.emptyList(),
                "DBSKKV Regional Fruit Research Station, Vengurla",
                "High commercial export potential on coastal hill terrains; stabilizes soil against erosion and generates long-term compounding cashflow.",
                "Requires tea mosquito bug control during flowering and flush periods (Nov-Jan).",
                Arrays.asList("Monsoon: Graft planting & mulching", "Oct: Basin ring weeding", "Dec: Flowering protection", "Mar-May: Nut collection")
        ));

        // ==============================================================
        // 2. WESTERN MAHARASHTRA & KHANDESH (MPKV RAHURI)
        // ==========================================
        List<String> westDistricts = Arrays.asList("Pune", "Ahmednagar", "Satara", "Solapur", "Sangli", "Kolhapur", "Nashik", "Dhule", "Jalgaon");

        // Soybean - Balanced Best Fit Kharif
        knowledgeBase.add(new CropProfile(
                "Soybean", "Phule Sangam (KDS-726) / Phule Kimaya",
                "WESTERN_MAHARASHTRA", westDistricts,
                Collections.singletonList("Kharif"), "LIMITED", "PLAINS", "BLACK_COTTON",
                100, 105, "MEDIUM", "MEDIUM", 14000.0, "10 - 14 Qtl/Acre",
                Arrays.asList("Sugarcane", "Wheat", "Gram", "Jowar"),
                Collections.singletonList("Soybean"),
                "Mahatma Phule Krishi Vidyapeeth (MPKV) Rahuri - Soybean Research Scheme",
                "High resistance to Asian Soybean Rust and stem fly; ideal high-protein cash crop that leaves behind organic N for succeeding Rabi crops.",
                "Avoid deep sowing (> 3-4 cm); treat seed with Bradyrhizobium and Trichoderma.",
                Arrays.asList("Day 0: Broad bed furrow (BBF) sowing", "Day 20: Pre-emergence weed management", "Day 50: Pod initiation check", "Day 102: Harvest at 14% moisture")
        ));

        // Chickpea (Harbara) - High Value / Soil Restoring Rabi
        knowledgeBase.add(new CropProfile(
                "Chickpea / Gram (Harbara)", "Vijay / Digvijay / Phule Vikram",
                "WESTERN_MAHARASHTRA", westDistricts,
                Collections.singletonList("Rabi"), "LIMITED", "PLAINS", "BLACK_COTTON",
                95, 105, "LOW", "LOW", 9500.0, "8 - 11 Qtl/Acre",
                Arrays.asList("Soybean", "Bajra", "Cotton", "Sugarcane Fallow"),
                Collections.singletonList("Chickpea / Gram (Harbara)"),
                "MPKV Rahuri - Pulse Improvement Project",
                "Exceptional drought tolerance in black soils; Phule Vikram is specifically adapted for mechanical combine harvesting.",
                "Avoid sowing in waterlogged soil; one protective irrigation at pod formation increases yield by 35%.",
                Arrays.asList("Day 0: Seed inoculation with PSB & Rhizobium", "Day 30: Nipping of apical buds to promote branching", "Day 60: Pod borer pheromone trap installation", "Day 100: Harvesting")
        ));

        // Rabi Sorghum (Jowar) - Low Water / Food & Fodder Security
        knowledgeBase.add(new CropProfile(
                "Rabi Sorghum (Jowar)", "Maldandi M-35-1 / Phule Revati / Phule Anuradha",
                "WESTERN_MAHARASHTRA", westDistricts,
                Collections.singletonList("Rabi"), "RAINFED", "PLAINS", "BLACK_COTTON",
                115, 125, "LOW", "LOW", 7500.0, "10 - 14 Qtl/Acre grain + 3T Fodder",
                Arrays.asList("Soybean", "Green Gram", "Black Gram"),
                Collections.emptyList(),
                "MPKV Rahuri - Dryland Farming Research Station, Solapur",
                "Benchmark drought-hardy crop for scarcity zone; deep root penetration utilizes subsoil moisture with high quality sweet stover fodder.",
                "Ensure sowing between Sept 15 - Oct 15 to avoid shoot fly incidence.",
                Arrays.asList("Day 0: Compartment bunding & seed drill sowing", "Day 21: Thinning to maintain plant population", "Day 75: Milk stage bird scaring", "Day 120: Harvest")
        ));

        // Pearl Millet (Bajra) - Drought Contingency / Short Duration
        knowledgeBase.add(new CropProfile(
                "Pearl Millet (Bajra)", "Shraddha / Saburi / Phule Aadishakti",
                "WESTERN_MAHARASHTRA", westDistricts,
                Collections.singletonList("Kharif"), "RAINFED", "UPLAND", "ANY",
                75, 85, "LOW", "LOW", 6000.0, "10 - 13 Qtl/Acre",
                Arrays.asList("Sugarcane Fallow", "Wheat", "Gram"),
                Collections.emptyList(),
                "MPKV Rahuri - Bajra Research Station, Dhule",
                "Matures in just 75-80 days; survives prolonged dry spells where soybean fails; excellent food and fodder security.",
                "Downy mildew resistant; avoid excess nitrogen under rainfed conditions to prevent lodging.",
                Arrays.asList("Day 0: Sowing with 45x15 cm spacing", "Day 15: Weeding and thinning", "Day 45: Earhead emergence", "Day 80: Harvesting")
        ));

        // Onion - High Commercial Diversification Plan
        knowledgeBase.add(new CropProfile(
                "Onion", "Bhima Super (Kharif) / Bhima Kiran (Rabi)",
                "WESTERN_MAHARASHTRA", westDistricts,
                Arrays.asList("Kharif", "Rabi", "Summer"), "ASSURED", "PLAINS", "ALLUVIAL",
                110, 125, "HIGH", "HIGH", 28000.0, "100 - 140 Qtl/Acre",
                Arrays.asList("Soybean", "Maize", "Wheat"),
                Collections.singletonList("Onion"),
                "ICAR-Directorate of Onion and Garlic Research (DOGR), Rajgurunagar, Pune",
                "Very high commercial cashflow potential in Pune, Nashik, and Ahmednagar belts with well-drained fertile soils.",
                "Requires assured micro-irrigation; thrips and purple blotch management needed in high humidity.",
                Arrays.asList("Day 0: Raised bed seedling transplanting", "Day 30: Micro-sprinkler/drip fertigation", "Day 60: Bulb development stage", "Day 115: Neck fall & Curing")
        ));

        // Wheat - Assured Irrigated Rabi Plan
        knowledgeBase.add(new CropProfile(
                "Wheat", "Trimbak (NIAW-301) / Lokwan / Samadhan",
                "WESTERN_MAHARASHTRA", westDistricts,
                Collections.singletonList("Rabi"), "ASSURED", "PLAINS", "BLACK_COTTON",
                110, 120, "MEDIUM", "MEDIUM", 15000.0, "18 - 22 Qtl/Acre",
                Arrays.asList("Soybean", "Groundnut", "Udad", "Moong"),
                Collections.emptyList(),
                "MPKV Rahuri - Wheat Specialist Station, Niphad",
                "Prime staple crop under assured irrigation; excellent chapati quality and responsive to balanced NPK application.",
                "Critical irrigation stages: Crown Root Initiation (21 days), Tillering, and Grain Filling. Don't miss CRI stage.",
                Arrays.asList("Day 0: Pre-sowing irrigation & drilling", "Day 21: CRI stage first irrigation (crucial)", "Day 45: Tillering stage irrigation", "Day 85: Milk stage irrigation", "Day 115: Harvest")
        ));

        // ==============================================================
        // 3. MARATHWADA AGRO-CLIMATIC ZONE (VNMKV PARBHANI)
        // ==========================================
        List<String> marathwadaDistricts = Arrays.asList("Chhatrapati Sambhajinagar", "Aurangabad", "Jalna", "Beed", "Parbhani", "Nanded", "Latur", "Dharashiv", "Osmanabad", "Hingoli");

        // Pigeonpea (Tur) + Soybean Intercrop - Best Fit
        knowledgeBase.add(new CropProfile(
                "Soybean + Pigeonpea (Tur) Intercrop", "MAUS-158 + BDN-711 (4:2 Row Pattern)",
                "MARATHWADA", marathwadaDistricts,
                Collections.singletonList("Kharif"), "LIMITED", "PLAINS", "BLACK_COTTON",
                100, 160, "MEDIUM", "MEDIUM", 16000.0, "Soybean 8 Qtl + Tur 5 Qtl/Acre",
                Arrays.asList("Cotton", "Sorghum", "Wheat"),
                Collections.emptyList(),
                "Vasantrao Naik Marathwada Krishi Vidyapeeth (VNMKV) Parbhani - Intercropping Research",
                "4:2 intercropping acts as weather insurance; early soybean harvest provides prompt cashflow while deep-rooted Tur thrives into winter.",
                "Tur requires protection against Helicoverpa pod borer at flowering with pheromone traps and biopesticides.",
                Arrays.asList("Day 0: Paired row sowing with BBF", "Day 40: Integrated weed management", "Day 100: Soybean harvest & Tur interculturing", "Day 160: Tur harvesting")
        ));

        // Safflower (Kardi) - Ultra Drought-Tolerant Rabi Oilseed
        knowledgeBase.add(new CropProfile(
                "Safflower (Kardi)", "Sharda / PBNS-12 / Phule Kusuma",
                "MARATHWADA", marathwadaDistricts,
                Collections.singletonList("Rabi"), "RAINFED", "PLAINS", "BLACK_COTTON",
                125, 135, "LOW", "LOW", 7000.0, "6 - 9 Qtl/Acre",
                Arrays.asList("Soybean", "Green Gram", "Black Gram"),
                Collections.emptyList(),
                "VNMKV Parbhani - Oilseeds Research Station",
                "Deep taproot reaches over 2 meters into subsoil, making it the most drought-hardy Rabi oilseed; high polyunsaturated oil value.",
                "Spiny variety requires harvesting in early morning hours when spines are supple due to dew.",
                Arrays.asList("Day 0: Seed treatment with Azotobacter", "Day 30: Thinning to 20cm plant spacing", "Day 65: Rosette to branching phase", "Day 130: Harvesting")
        ));

        // Green Gram (Moong) - Super Short Duration (Catch Crop)
        knowledgeBase.add(new CropProfile(
                "Green Gram (Moong)", "BM-2003-2 / BM-4",
                "MARATHWADA", marathwadaDistricts,
                Arrays.asList("Kharif", "Summer"), "RAINFED", "UPLAND", "ANY",
                65, 70, "LOW", "LOW", 5500.0, "4 - 6 Qtl/Acre",
                Arrays.asList("Cotton", "Jowar", "Sugarcane"),
                Collections.emptyList(),
                "VNMKV Parbhani - Pulses Breeding Scheme",
                "Completes life cycle in only 65 days; leaves 25 kg N/acre for the succeeding Rabi crop; excellent short-window risk hedge.",
                "Yellow Mosaic Virus (YMV) resistant; pick pods as they mature to avoid shattering in sudden rains.",
                Arrays.asList("Day 0: Sowing at 30x10 cm", "Day 20: Hand weeding", "Day 45: First pod picking", "Day 65: Final plant harvest")
        ));

        // Sweet Orange (Mosambi) / Fruit Crop - Diversification
        knowledgeBase.add(new CropProfile(
                "Sweet Orange (Mosambi)", "Phule Mosambi / Katol Gold",
                "MARATHWADA", marathwadaDistricts,
                Collections.singletonList("Perennial"), "LIMITED", "PLAINS", "ALLUVIAL",
                180, 365, "HIGH", "MEDIUM", 25000.0, "8 - 12 Tonnes/Acre",
                Arrays.asList("Leguminous intercrops (Gram, Soybean)"),
                Collections.emptyList(),
                "VNMKV Fruit Research Station, Aurangabad",
                "Signature high-value horticulture crop of Marathwada; deep drip fertigation yields superior brix and commercial fruit weight.",
                "Ensure Phytophthora root rot control using Trichoderma drenching before monsoon onset.",
                Arrays.asList("Monsoon: Basin cleaning & organic manuring", "Nov: Ambe bahar water stress", "Jan: Regular drip irrigation", "Aug-Oct: Fruit harvesting")
        ));

        // ==============================================================
        // 4. VIDARBHA AGRO-CLIMATIC ZONE (PDKV AKOLA)
        // ==========================================
        List<String> vidarbhaDistricts = Arrays.asList("Nagpur", "Amravati", "Akola", "Yavatmal", "Wardha", "Chandrapur", "Buldhana", "Bhandara", "Gondia", "Gadchiroli");

        // Cotton + Greengram Strip Cropping - Best Fit
        knowledgeBase.add(new CropProfile(
                "Cotton + Greengram Strip Crop", "Bt Cotton + Moong (PDKV AKM-4) 1:1 Strip",
                "VIDARBHA", vidarbhaDistricts,
                Collections.singletonList("Kharif"), "LIMITED", "PLAINS", "BLACK_COTTON",
                150, 165, "MEDIUM", "HIGH", 19000.0, "Cotton 10-12 Qtl + Moong 3 Qtl/Acre",
                Arrays.asList("Gram", "Wheat", "Fallow"),
                Collections.singletonList("Cotton + Greengram Strip Crop"),
                "Dr. Panjabrao Deshmukh Krishi Vidyapeeth (PDKV) Akola - Cotton Research Unit",
                "Strip cropping with moong acts as a trap crop for pests, fixes nitrogen, and provides cash in 65 days while cotton matures.",
                "Strict pink bollworm surveillance with pheromone traps; avoid late-season pesticide overuse.",
                Arrays.asList("Day 0: Strip planting with 90x45cm cotton spacing", "Day 30: Square formation nutrient spray", "Day 65: Moong harvest", "Day 120-160: Cotton picking")
        ));

        // Blackgram (Udad) - Low Input / Drought Hardy Kharif
        knowledgeBase.add(new CropProfile(
                "Blackgram (Udad)", "TAU-1 / AKU-15",
                "VIDARBHA", vidarbhaDistricts,
                Collections.singletonList("Kharif"), "RAINFED", "UPLAND", "BLACK_COTTON",
                70, 75, "LOW", "LOW", 6000.0, "5 - 7 Qtl/Acre",
                Arrays.asList("Cotton", "Jowar", "Wheat"),
                Collections.emptyList(),
                "PDKV Akola - Pulses Improvement Project",
                "Benchmark pulse variety developed by PDKV Akola; widely popular for high bold seed yield and tolerance to powdery mildew.",
                "Avoid saturated soils during seedling emergence; requires good surface drainage.",
                Arrays.asList("Day 0: Line drilling with 30cm row width", "Day 20: Single hand weeding", "Day 50: Pod filling stage", "Day 72: Harvesting")
        ));

        // Mustard (Mohari) - Low Water Rabi Alternative
        knowledgeBase.add(new CropProfile(
                "Mustard (Mohari)", "Pusa Bold / Shatabdi",
                "VIDARBHA", vidarbhaDistricts,
                Collections.singletonList("Rabi"), "LIMITED", "PLAINS", "ALLUVIAL",
                100, 110, "LOW", "LOW", 7500.0, "6 - 9 Qtl/Acre",
                Arrays.asList("Soybean", "Paddy (Rice)", "Maize"),
                Collections.emptyList(),
                "PDKV Akola - Oilseeds Scheme",
                "Requires only 2 light irrigations compared to 5 for wheat; high seed oil content and lower disease risk in eastern Vidarbha.",
                "Aphid management needed during cloudy December-January periods.",
                Arrays.asList("Day 0: Sowing in moist soil (Oct-Nov)", "Day 25: First irrigation at rosette stage", "Day 60: Flowering stage irrigation", "Day 105: Harvesting")
        ));

        // Nagpur Mandarin (Orange) - Diversification
        knowledgeBase.add(new CropProfile(
                "Nagpur Mandarin (Orange)", "Nagpur Santra (Selected N-182)",
                "VIDARBHA", vidarbhaDistricts,
                Collections.singletonList("Perennial"), "LIMITED", "PLAINS", "BLACK_COTTON",
                180, 365, "HIGH", "MEDIUM", 26000.0, "7 - 10 Tonnes/Acre",
                Arrays.asList("Gram intercrop", "Moong intercrop"),
                Collections.emptyList(),
                "ICAR - Central Citrus Research Institute (CCRI), Nagpur",
                "World-renowned GI-tagged fruit crop; high profitability when managed with drip irrigation and timely bahar treatment.",
                "Citrus psylla and fruit sucking moth surveillance during fruit enlargement.",
                Arrays.asList("May: Ambia bahar initiation", "July: Micronutrient spray", "Oct: Fruit thinning", "Jan-Mar: Fruit harvesting")
        ));
    }

    // =========================================================================
    // MULTI-FACTOR RANKING & BEFORE-YOU-SOW SIMULATOR ENGINE
    // =========================================================================

    public Map<String, Object> generateRecommendationsAndPlans(
            String district, String season, String waterAvailability, String landType,
            String previousCrop, String budgetLevel, String farmerGoal, String soilType,
            boolean scenarioWaterDeficit, boolean scenarioBudgetCut,
            boolean scenarioEarlyHarvest, boolean scenarioDelayedSowing) {

        String safeDistrict = (district != null && !district.trim().isEmpty()) ? district.trim() : "Pune";
        String safeSeason = (season != null && !season.trim().isEmpty()) ? season.trim() : "Kharif";
        String safeWater = (waterAvailability != null && !waterAvailability.trim().isEmpty()) ? waterAvailability.trim() : "Limited Irrigation";
        String safeLand = (landType != null && !landType.trim().isEmpty()) ? landType.trim() : "Plains";
        String safePrevCrop = (previousCrop != null && !previousCrop.trim().isEmpty()) ? previousCrop.trim() : "Fallow";
        String safeBudget = (budgetLevel != null && !budgetLevel.trim().isEmpty()) ? budgetLevel.trim() : "Medium Investment";
        String safeGoal = (farmerGoal != null && !farmerGoal.trim().isEmpty()) ? farmerGoal.trim() : "Balanced Return";

        // Effective conditions after applying "What-If" scenarios
        String effectiveWater = safeWater;
        if (scenarioWaterDeficit) {
            effectiveWater = "Rainfed";
        }
        String effectiveBudget = safeBudget;
        if (scenarioBudgetCut) {
            effectiveBudget = "Low Investment";
        }

        String normSeason = safeSeason.toLowerCase();
        boolean isSummer = normSeason.contains("summer") || normSeason.contains("zaid");
        boolean isKharif = normSeason.contains("kharif") || normSeason.contains("monsoon");
        boolean isRabi = normSeason.contains("rabi") || normSeason.contains("winter");

        // Filter and score crops
        List<ScoredCrop> scoredList = new ArrayList<>();

        for (CropProfile crop : knowledgeBase) {
            // 1. Season constraint
            boolean seasonMatch = false;
            for (String s : crop.seasons) {
                String cs = s.toLowerCase();
                if (cs.equals("perennial")) {
                    seasonMatch = true;
                    break;
                }
                if (isSummer && (cs.contains("summer") || cs.contains("zaid"))) {
                    seasonMatch = true;
                    break;
                }
                if (isKharif && cs.contains("kharif")) {
                    seasonMatch = true;
                    break;
                }
                if (isRabi && cs.contains("rabi")) {
                    seasonMatch = true;
                    break;
                }
                if (s.equalsIgnoreCase(safeSeason)) {
                    seasonMatch = true;
                    break;
                }
            }
            if (!seasonMatch) continue;

            // 2. District / Zone match
            boolean districtMatch = false;
            for (String d : crop.districts) {
                if (d.equalsIgnoreCase(safeDistrict) || safeDistrict.toLowerCase().contains(d.toLowerCase()) || d.toLowerCase().contains(safeDistrict.toLowerCase())) {
                    districtMatch = true;
                    break;
                }
            }
            // Broad zone match fallback (Konkan, Western, Marathwada, Vidarbha)
            if (!districtMatch) {
                String zoneOfDistrict = determineZoneForDistrict(safeDistrict);
                if (crop.zone != null && crop.zone.equalsIgnoreCase(zoneOfDistrict)) {
                    districtMatch = true;
                }
            }
            if (!districtMatch) {
                continue;
            }

            // 3. Multi-factor scoring
            double score = 50.0;
            StringBuilder reasons = new StringBuilder();
            StringBuilder risks = new StringBuilder();

            // Water score
            if (effectiveWater.toLowerCase().contains("rainfed")) {
                if ("RAINFED".equalsIgnoreCase(crop.minWaterRequirement)) {
                    score += 25;
                    reasons.append("• Proven drought hardiness under rainfed conditions.\n");
                } else if ("LIMITED".equalsIgnoreCase(crop.minWaterRequirement)) {
                    score += 10;
                    reasons.append("• Tolerates dry periods with deep root system.\n");
                } else {
                    score -= 30; // High water demand penalised in rainfed
                    risks.append("• Water intensive crop; high drought risk under rainfed conditions.\n");
                }
            } else if (effectiveWater.toLowerCase().contains("assured") || effectiveWater.toLowerCase().contains("drip")) {
                score += 20;
                reasons.append("• Assured irrigation maximizes genetic yield potential.\n");
            } else { // Limited
                if (!"ASSURED".equalsIgnoreCase(crop.minWaterRequirement)) {
                    score += 15;
                    reasons.append("• Well-adapted to moderate or limited irrigation.\n");
                } else {
                    score -= 10;
                    risks.append("• May face yield penalty if water runs out during reproductive stage.\n");
                }
            }

            // Budget score
            if (effectiveBudget.toLowerCase().contains("low")) {
                if ("LOW".equalsIgnoreCase(crop.inputCostLevel)) {
                    score += 20;
                    reasons.append("• Low cash outlay per acre; low financial downside.\n");
                } else if ("HIGH".equalsIgnoreCase(crop.inputCostLevel)) {
                    score -= 25;
                    risks.append("• High initial investment in seeds/fertilizers exceeds current budget.\n");
                }
            } else if (effectiveBudget.toLowerCase().contains("higher")) {
                if ("HIGH".equalsIgnoreCase(crop.inputCostLevel)) {
                    score += 15;
                    reasons.append("• High-input responsiveness yields superior market returns.\n");
                }
            }

            // Land & Soil fit
            if (safeLand.toLowerCase().contains("slop") && "SLOPING".equalsIgnoreCase(crop.landSuitability)) {
                score += 15;
                reasons.append("• Ideal for sloping topography; binds soil against monsoon erosion.\n");
            }
            if (safeLand.toLowerCase().contains("lowland") && "LOWLAND".equalsIgnoreCase(crop.landSuitability)) {
                score += 15;
                reasons.append("• Thrives in fertile lowlands with high water holding capacity.\n");
            }

            // Rotation synergy vs penalty
            if (!safePrevCrop.equalsIgnoreCase("Fallow") && !safePrevCrop.equalsIgnoreCase("Other")) {
                boolean hasSynergy = false;
                for (String syn : crop.rotationSynergyWith) {
                    if (syn.toLowerCase().contains(safePrevCrop.toLowerCase()) || safePrevCrop.toLowerCase().contains(syn.toLowerCase())) {
                        hasSynergy = true;
                        break;
                    }
                }
                if (hasSynergy) {
                    score += 20;
                    reasons.append("• Excellent rotation synergy following ").append(safePrevCrop).append(" (restores nitrogen & breaks disease cycles).\n");
                }

                boolean hasHarm = false;
                for (String harm : crop.rotationHarmfulWith) {
                    if (harm.toLowerCase().contains(safePrevCrop.toLowerCase()) || safePrevCrop.toLowerCase().contains(harm.toLowerCase())) {
                        hasHarm = true;
                        break;
                    }
                }
                if (hasHarm) {
                    score -= 25;
                    risks.append("• Mono-cropping hazard: Planting after ").append(safePrevCrop).append(" increases soil depletion and pathogen buildup.\n");
                }
            }

            // What-If Scenario dynamic impacts
            if (scenarioEarlyHarvest) {
                if (crop.durationMaxDays <= 85) {
                    score += 25;
                    reasons.append("• What-If Scenario Fit: Early maturity (").append(crop.durationMaxDays).append(" days) vacates field quickly.\n");
                } else {
                    score -= 20;
                }
            }

            if (scenarioDelayedSowing) {
                if (crop.durationMaxDays <= 95 && ("LOW".equalsIgnoreCase(crop.inputCostLevel) || "RAINFED".equalsIgnoreCase(crop.minWaterRequirement))) {
                    score += 20;
                    reasons.append("• What-If Scenario Fit: Suitable for late-sown contingency planning.\n");
                } else {
                    score -= 15;
                    risks.append("• Late sowing drastically reduces vegetative period and yield.\n");
                }
            }

            if (crop.risksAndPrecautions != null && !crop.risksAndPrecautions.isEmpty()) {
                risks.append("• ").append(crop.risksAndPrecautions).append("\n");
            }

            scoredList.add(new ScoredCrop(crop, score, reasons.toString().trim(), risks.toString().trim()));
        }

        // Sort descending by score
        scoredList.sort((a, b) -> Double.compare(b.score, a.score));

        // Formulate 3 distinct evidence-backed plans
        Map<String, Object> planBestFit = null;
        Map<String, Object> planLowWater = null;
        Map<String, Object> planDiversification = null;

        for (ScoredCrop item : scoredList) {
            CropProfile c = item.profile;
            // Best fit candidate
            if (planBestFit == null) {
                planBestFit = buildPlanMap("BALANCED_BEST_FIT", "Plan A: Balanced Best Fit", item, safeDistrict, safeSeason);
                continue;
            }

            // Low-water candidate
            if (planLowWater == null && ("RAINFED".equalsIgnoreCase(c.minWaterRequirement) || "LOW".equalsIgnoreCase(c.inputCostLevel) || c.durationMaxDays <= 90)) {
                if (!c.cropName.equals(planBestFit.get("cropName"))) {
                    planLowWater = buildPlanMap("LOW_WATER_RESILIENT", "Plan B: Low-Water & Climate-Resilient Alternative", item, safeDistrict, safeSeason);
                    continue;
                }
            }

            // Diversification candidate
            if (planDiversification == null && ("HIGH".equalsIgnoreCase(c.inputCostLevel) || "Perennial".equalsIgnoreCase(c.seasons.get(0)) || c.cropName.contains("Onion") || c.cropName.contains("Cashew") || c.cropName.contains("Orange") || c.cropName.contains("Mosambi") || c.cropName.contains("Turmeric"))) {
                if (!c.cropName.equals(planBestFit.get("cropName")) && (planLowWater == null || !c.cropName.equals(planLowWater.get("cropName")))) {
                    planDiversification = buildPlanMap("DIVERSIFICATION", "Plan C: High-Value Market Diversification Plan", item, safeDistrict, safeSeason);
                }
            }
        }

        // Fallbacks if fewer candidates
        if (scoredList.isEmpty()) {
            // Guarantee at least 3 fallback candidates from knowledge base so simulation NEVER returns 0 plans
            for (CropProfile cp : knowledgeBase) {
                scoredList.add(new ScoredCrop(cp, 75.0, "• Recommended staple adaptation for Maharashtra microclimates.", "• Follow standard university package of practices."));
                if (scoredList.size() >= 3) break;
            }
        }

        if (planBestFit == null && !scoredList.isEmpty()) {
            planBestFit = buildPlanMap("BALANCED_BEST_FIT", "Plan A: Recommended Seasonal Plan", scoredList.get(0), safeDistrict, safeSeason);
        }
        if (planLowWater == null && scoredList.size() > 1) {
            planLowWater = buildPlanMap("LOW_WATER_RESILIENT", "Plan B: Resilient Secondary Fit", scoredList.get(1), safeDistrict, safeSeason);
        }
        if (planDiversification == null && scoredList.size() > 2) {
            planDiversification = buildPlanMap("DIVERSIFICATION", "Plan C: Alternative Cropping Fit", scoredList.get(2), safeDistrict, safeSeason);
        }

        List<Map<String, Object>> plans = new ArrayList<>();
        if (planBestFit != null) plans.add(planBestFit);
        if (planLowWater != null) plans.add(planLowWater);
        if (planDiversification != null) plans.add(planDiversification);

        Map<String, Object> response = new HashMap<>();
        response.put("district", safeDistrict);
        response.put("season", safeSeason);
        response.put("waterAvailability", safeWater);
        response.put("landType", safeLand);
        response.put("previousCrop", safePrevCrop);
        response.put("budgetLevel", safeBudget);
        response.put("farmerGoal", safeGoal);
        response.put("scenariosApplied", Map.of(
                "waterDeficit", scenarioWaterDeficit,
                "budgetCut", scenarioBudgetCut,
                "earlyHarvest", scenarioEarlyHarvest,
                "delayedSowing", scenarioDelayedSowing
        ));
        response.put("generatedPlans", plans);
        response.put("totalCandidatesEvaluated", scoredList.size());

        return response;
    }

    private Map<String, Object> buildPlanMap(String planTypeKey, String planTitle, ScoredCrop scored, String district, String season) {
        CropProfile c = scored.profile;
        Map<String, Object> map = new HashMap<>();
        map.put("planType", planTypeKey);
        map.put("planTitle", planTitle);
        map.put("cropName", c.cropName);
        map.put("variety", c.variety);
        map.put("durationDays", c.durationMinDays + " - " + c.durationMaxDays + " Days");
        map.put("waterDemand", c.minWaterRequirement);
        map.put("inputCostCategory", c.inputCostLevel);
        map.put("estimatedCostPerAcre", c.estimatedCostPerAcre);
        map.put("targetYield", c.typicalYieldPerAcre);
        map.put("suitabilityScore", (int) Math.round(Math.min(99, Math.max(55, scored.score))));
        map.put("suitabilityReason", scored.reasons != null && !scored.reasons.isEmpty() ? scored.reasons : c.suitabilityReason);
        map.put("keyRisks", scored.risks != null && !scored.risks.isEmpty() ? scored.risks : c.risksAndPrecautions);
        map.put("universitySource", c.universitySource);
        map.put("timelineMilestones", c.timelineMilestones);
        return map;
    }

    public String determineZoneForDistrict(String district) {
        if (district == null) return "WESTERN_MAHARASHTRA";
        String d = district.toLowerCase();
        if (d.contains("ratnagiri") || d.contains("sindhudurg") || d.contains("raigad") || d.contains("thane") || d.contains("palghar")) {
            return "KONKAN";
        }
        if (d.contains("sambhajinagar") || d.contains("aurangabad") || d.contains("jalna") || d.contains("beed") || d.contains("parbhani") || d.contains("nanded") || d.contains("latur") || d.contains("dharashiv") || d.contains("osmanabad") || d.contains("hingoli")) {
            return "MARATHWADA";
        }
        if (d.contains("nagpur") || d.contains("amravati") || d.contains("akola") || d.contains("yavatmal") || d.contains("wardha") || d.contains("chandrapur") || d.contains("buldhana") || d.contains("bhandara") || d.contains("gondia") || d.contains("gadchiroli")) {
            return "VIDARBHA";
        }
        return "WESTERN_MAHARASHTRA";
    }

    private static class ScoredCrop {
        CropProfile profile;
        double score;
        String reasons;
        String risks;

        ScoredCrop(CropProfile profile, double score, String reasons, String risks) {
            this.profile = profile;
            this.score = score;
            this.reasons = reasons;
            this.risks = risks;
        }
    }
}
