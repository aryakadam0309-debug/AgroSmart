package com.agrosmart.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.agrosmart.models.CropGuide;

import java.util.ArrayList;
import java.util.List;

public class CropGuideRepository {

    private static CropGuideRepository instance;
    private final List<CropGuide> cropGuides = new ArrayList<>();

    public static synchronized CropGuideRepository getInstance() {
        if (instance == null) {
            instance = new CropGuideRepository();
        }
        return instance;
    }

    private CropGuideRepository() {
        initCropGuides();
    }

    private void initCropGuides() {
        cropGuides.clear();

        cropGuides.add(new CropGuide(
                "CG01", "Wheat (गेहूं)", "Triticum aestivum", "Rabi",
                "120 - 135 Days", "Fertile Medium to Deep Black / Loam Soil", "450 - 550 mm (4-6 irrigations)",
                "15°C - 25°C", "18 - 24 Quintals / Acre", "November 1 - 25",
                "March - April", "N:P:K = 120:60:40 kg/ha",
                "1. Germination (0-15d)\n2. Crown Root Initiation (20-25d)\n3. Tillering (35-45d)\n4. Flowering & Milk Stage (70-85d)\n5. Maturity (110-130d)",
                "First irrigation at CRI stage (21 days) is critical. Use certified seeds treated with Thiram/Carbendazim.",
                "Western Maharashtra, Marathwada, Vidarbha", "BLACK_COTTON, ALLUVIAL, LOAM"
        ));

        cropGuides.add(new CropGuide(
                "CG02", "Soybean (सोयाबीन)", "Glycine max", "Kharif",
                "95 - 110 Days", "Well-drained Medium to Deep Black Cotton Soil", "500 - 650 mm (Rainfed)",
                "22°C - 32°C", "10 - 14 Quintals / Acre", "June 20 - July 10",
                "October - November", "N:P:K = 25:60:40 kg/ha + Rhizobium",
                "1. Emergence (4-7d)\n2. Vegetative Growth (15-30d)\n3. Flowering (35-50d)\n4. Pod Filling (55-80d)\n5. Senescence & Harvest (95-105d)",
                "Avoid waterlogging. Apply seed treatment with Bradyrhizobium culture for optimal nitrogen fixation.",
                "Western Maharashtra, Marathwada, Vidarbha", "BLACK_COTTON, LOAM, ALLUVIAL"
        ));

        cropGuides.add(new CropGuide(
                "CG03", "Cotton (कपास)", "Gossypium hirsutum", "Kharif",
                "150 - 180 Days", "Deep Black Regur Soil & Clayey Loam", "600 - 850 mm",
                "25°C - 35°C", "12 - 16 Quintals / Acre", "May 15 - June 15",
                "November - January", "N:P:K = 120:60:60 kg/ha",
                "1. Seedling (0-20d)\n2. Squaring (40-50d)\n3. Flowering & Boll Formation (70-110d)\n4. Boll Maturation & Bursting (120-160d)",
                "Maintain 90x60 cm plant spacing. Install pheromone traps for pink bollworm monitoring at 45 days.",
                "Vidarbha, Marathwada, Western Maharashtra", "BLACK_COTTON, CLAYEY"
        ));

        cropGuides.add(new CropGuide(
                "CG04", "Rice / Paddy (धान)", "Oryza sativa", "Kharif",
                "125 - 145 Days", "Clayey Lowland, Coastal Alluvial & Red Lateritic Soil", "1100 - 1400 mm",
                "24°C - 35°C", "22 - 28 Quintals / Acre", "June 1 - June 30",
                "October - November", "N:P:K = 100:50:50 kg/ha + Zinc 25kg",
                "1. Nursery (0-25d)\n2. Transplanting (25-30d)\n3. Tillering (40-60d)\n4. Panicle Initiation (70-85d)\n5. Ripening (110-135d)",
                "Transplant 2-3 seedlings per hill at 20x15 cm spacing. Maintain shallow standing water (2-5 cm).",
                "Konkan, Vidarbha, Western Maharashtra", "CLAYEY, RED_LATERITIC, ALLUVIAL"
        ));

        cropGuides.add(new CropGuide(
                "CG05", "Gram / Chana (चना)", "Cicer arietinum", "Rabi",
                "100 - 115 Days", "Medium Black Cotton Soil & Clay Loam", "250 - 350 mm (Low water)",
                "18°C - 28°C", "8 - 12 Quintals / Acre", "October 15 - November 10",
                "February - March", "N:P:K = 20:50:20 kg/ha + PSB Culture",
                "1. Germination (0-10d)\n2. Branching & Nipping (30-40d)\n3. Flowering (50-65d)\n4. Pod Development (70-90d)\n5. Harvest (105-115d)",
                "Perform nipping of top shoots at 35-40 days to encourage extensive lateral branching and higher pod count.",
                "Marathwada, Western Maharashtra, Vidarbha", "BLACK_COTTON, ALLUVIAL, LOAM"
        ));

        cropGuides.add(new CropGuide(
                "CG06", "Tomato (टमाटर)", "Solanum lycopersicum", "All Season",
                "90 - 120 Days", "Well-drained Sandy Loam with Organic Matter", "400 - 600 mm (Drip)",
                "20°C - 30°C", "150 - 220 Quintals / Acre", "June-July / Oct-Nov",
                "Starting 75 days after transplant", "N:P:K = 150:100:100 kg/ha",
                "1. Nursery (0-25d)\n2. Transplanting & Staking (30-40d)\n3. Flowering (45-60d)\n4. Fruit Setting (60-80d)\n5. Harvest Flushes (80-120d)",
                "Adopt trellis staking to prevent soil contact diseases and improve fruit quality and uniform grading.",
                "Western Maharashtra, Marathwada, Vidarbha, Konkan", "SANDY_LOAM, ALLUVIAL, RED_LATERITIC"
        ));

        cropGuides.add(new CropGuide(
                "CG07", "Onion (प्याज)", "Allium cepa", "Rabi",
                "115 - 130 Days", "Well-drained Fertile Alluvial / Medium Black Loam", "550 - 650 mm (Micro-sprinkler)",
                "18°C - 30°C", "120 - 160 Quintals / Acre", "October - November",
                "March - April", "N:P:K = 100:50:50 kg/ha + Sulphur 30kg",
                "1. Nursery (0-40d)\n2. Transplanting on Raised Beds (45-50d)\n3. Vegetative Bulb Initiation (65-80d)\n4. Bulb Enlargement (80-110d)\n5. Neck Fall & Harvest (120d)",
                "Ensure sulphur application at planting for pungency and storage firmness. Stop irrigation 10-12 days before harvest.",
                "Western Maharashtra, Marathwada", "ALLUVIAL, BLACK_COTTON, SANDY_LOAM"
        ));

        cropGuides.add(new CropGuide(
                "CG08", "Pigeonpea / Tur (अरहर)", "Cajanus cajan", "Kharif",
                "160 - 180 Days", "Medium to Deep Black Cotton Soil", "600 - 700 mm (Rainfed / Limited)",
                "22°C - 35°C", "7 - 10 Quintals / Acre", "June 15 - July 10",
                "December - January", "N:P:K = 25:50:0 kg/ha + Rhizobium",
                "1. Seedling (0-30d)\n2. Branching (45-75d)\n3. Flowering Flush (90-120d)\n4. Pod Formation (120-150d)\n5. Harvest (160-180d)",
                "Ideal as 4:2 intercrop with soybean in Marathwada & Vidarbha; deep taproots survive extended dry spells.",
                "Marathwada, Vidarbha, Western Maharashtra", "BLACK_COTTON, LOAM"
        ));

        cropGuides.add(new CropGuide(
                "CG09", "Finger Millet / Nagli (नाचणी)", "Eleusine coracana", "Kharif",
                "95 - 110 Days", "Sloping Red Lateritic Hill Soil & Poor Nutrient Soils", "800 - 1200 mm (Tolerates poor soil)",
                "20°C - 32°C", "9 - 13 Quintals / Acre", "June - July",
                "October - November", "N:P:K = 60:30:30 kg/ha",
                "1. Nursery/Direct Drill (0-20d)\n2. Tillering (30-45d)\n3. Heading & Flowering (60-75d)\n4. Grain Hardening (80-95d)\n5. Harvesting (100-110d)",
                "Grows on acidic, low-fertility sloping soils in Konkan and Ghats; exceptional drought resilience and high calcium grain.",
                "Konkan, Western Maharashtra", "RED_LATERITIC, SANDY_LOAM"
        ));

        cropGuides.add(new CropGuide(
                "CG10", "Sugarcane (गन्ना)", "Saccharum officinarum", "Annual",
                "360 - 420 Days", "Heavy Clayey Deep Black Cotton Soil with Good Drainage", "1500 - 2000 mm (Assured)",
                "20°C - 35°C", "45 - 65 Tonnes / Acre", "July-August (Adsali) or Jan-Feb (Suru)",
                "12 - 14 months after planting", "N:P:K = 250:115:115 kg/ha + Micronutrients",
                "1. Sprouting (0-30d)\n2. Formative Tillering (30-100d)\n3. Grand Growth Elongation (100-270d)\n4. Ripening & Sucrose Accumulation (270-360d)",
                "Pair with subsurface drip irrigation for 40% water saving and uniform cane girth in Western Maharashtra.",
                "Western Maharashtra, Marathwada", "BLACK_COTTON, CLAYEY, ALLUVIAL"
        ));

        cropGuides.add(new CropGuide(
                "CG11", "Maize / Corn (मक्का)", "Zea mays", "Kharif",
                "100 - 115 Days", "Well-drained Fertile Alluvial / Loam / Medium Black Soil", "500 - 700 mm",
                "20°C - 32°C", "24 - 32 Quintals / Acre", "June 15 - July 15",
                "September - October", "N:P:K = 120:60:40 kg/ha + Zinc Sulphate",
                "1. Emergence (0-10d)\n2. Knee-high (25-35d)\n3. Tasseling & Silking (50-65d)\n4. Grain Filling (70-90d)\n5. Physiological Maturity (100-115d)",
                "Keep soil well-aerated; maize cannot tolerate standing water for more than 24 hours at young stage.",
                "Western Maharashtra, Marathwada, Vidarbha", "ALLUVIAL, BLACK_COTTON, SANDY_LOAM"
        ));

        cropGuides.add(new CropGuide(
                "CG12", "Groundnut (मूंगफली)", "Arachis hypogaea", "Kharif",
                "105 - 120 Days", "Loose Friable Sandy Loam & Reddish Well-drained Soil", "450 - 600 mm",
                "22°C - 32°C", "12 - 16 Quintals / Acre", "June 20 - July 10",
                "October - November", "N:P:K = 25:50:0 kg/ha + Gypsum 200kg",
                "1. Vegetative (0-25d)\n2. Flowering (25-40d)\n3. Pegging & Soil Penetration (40-60d)\n4. Pod Development (60-90d)\n5. Harvest (105-120d)",
                "Apply gypsum at 45 days (flowering/pegging) to provide essential calcium for pod development and prevent empty shells.",
                "Western Maharashtra, Vidarbha, Konkan", "SANDY_LOAM, RED_LATERITIC, ALLUVIAL"
        ));

        cropGuides.add(new CropGuide(
                "CG13", "Pomegranate (अनार)", "Punica granatum", "Annual",
                "150 - 180 Days (Mrig / Hasta Bahar)", "Light to Medium Loam, Well-drained Medium Black Soil", "600 - 800 mm (Drip)",
                "20°C - 38°C", "50 - 70 Quintals / Acre", "Bahar Treatment: May-June or Sept-Oct",
                "5 - 6 months after flowering", "N:P:K = 600:250:250 g/plant/year + FYM",
                "1. Defoliation & Rest (0-30d)\n2. Sprouting & Flowering (30-60d)\n3. Fruit Set & Thinning (60-90d)\n4. Fruit Enlargement & Bagging (90-140d)\n5. Harvest (150-180d)",
                "Use fruit bagging to protect against fruit borer and bacterial blight (Telya). Maintain strict drainage.",
                "Western Maharashtra, Marathwada", "SANDY_LOAM, ALLUVIAL, BLACK_COTTON"
        ));

        cropGuides.add(new CropGuide(
                "CG14", "Cashew Nut (काजू)", "Anacardium occidentale", "Annual",
                "Perennial (Fruit setting 60-70d)", "Porous Red Lateritic Hardpan & Coastal Sandy Soils", "1200 - 2500 mm",
                "22°C - 36°C", "8 - 12 Quintals / Acre", "Flowering: Dec-Jan",
                "Harvest: March - May", "N:P:K = 500:125:125 g/tree/year",
                "1. Vegetative Flush (Aug-Oct)\n2. Panicle Emergence (Nov-Dec)\n3. Flowering & Fruit Set (Dec-Feb)\n4. Nut Development & Apple Ripening (Feb-May)",
                "Tolerates porous, nutrient-poor soils in high rainfall Konkan coastal belt. Prune water shoots after monsoon.",
                "Konkan", "RED_LATERITIC, SANDY_LOAM"
        ));
    }

    public LiveData<List<CropGuide>> getGuides(String query, String season) {
        return getGuides(query, season, "All Maharashtra", "All Soil Types");
    }

    public LiveData<List<CropGuide>> getGuides(String query, String season, String region, String soilType) {
        MutableLiveData<List<CropGuide>> liveData = new MutableLiveData<>();
        List<CropGuide> filtered = new ArrayList<>();

        for (CropGuide guide : cropGuides) {
            boolean matchQuery = query == null || query.trim().isEmpty()
                    || guide.getCropName().toLowerCase().contains(query.toLowerCase())
                    || guide.getScientificName().toLowerCase().contains(query.toLowerCase());

            boolean matchSeason = season == null || season.equals("All Seasons")
                    || guide.getSeason().equalsIgnoreCase(season)
                    || guide.getSeason().equalsIgnoreCase("All Season")
                    || guide.getSeason().equalsIgnoreCase("Annual");

            boolean matchRegion = matchesRegion(guide, region);
            boolean matchSoil = matchesSoil(guide, soilType);

            if (matchQuery && matchSeason && matchRegion && matchSoil) {
                filtered.add(guide);
            }
        }
        liveData.setValue(filtered);
        return liveData;
    }

    private boolean matchesRegion(CropGuide guide, String selectedRegion) {
        if (selectedRegion == null || selectedRegion.trim().isEmpty()
                || selectedRegion.equalsIgnoreCase("All Maharashtra")) {
            return true;
        }

        String guideRegion = guide.getRegion().toLowerCase();
        if (guideRegion.contains("all maharashtra") || guideRegion.contains("all") || guideRegion.contains("any")) {
            return true;
        }

        String sel = selectedRegion.toLowerCase();

        // 1. Check primary agro-climatic zones
        if (sel.contains("western") && guideRegion.contains("western")) return true;
        if (sel.contains("marathwada") && guideRegion.contains("marathwada")) return true;
        if (sel.contains("vidarbha") && guideRegion.contains("vidarbha")) return true;
        if (sel.contains("konkan") && guideRegion.contains("konkan")) return true;

        // 2. Direct bidirectional containment
        if (sel.contains(guideRegion) || guideRegion.contains(sel)) {
            return true;
        }

        // 3. District name verification
        String[] districts = {
                "pune", "satara", "solapur", "nashik", "kolhapur", "ahmednagar", "sangli",
                "chhatrapati sambhajinagar", "aurangabad", "latur", "nanded", "jalna", "parbhani", "beed", "dharashiv", "osmanabad",
                "nagpur", "akola", "amravati", "yavatmal", "wardha", "chandrapur", "bhandara", "gondia", "buldhana",
                "ratnagiri", "sindhudurg", "raigad", "thane", "palghar"
        };
        for (String d : districts) {
            if (sel.contains(d) && (guideRegion.contains(d) || guide.getBestPractices().toLowerCase().contains(d))) {
                return true;
            }
        }

        return false;
    }

    private boolean matchesSoil(CropGuide guide, String selectedSoil) {
        if (selectedSoil == null || selectedSoil.trim().isEmpty()
                || selectedSoil.equalsIgnoreCase("All Soil Types")
                || selectedSoil.equalsIgnoreCase("Any Soil (Optional)")) {
            return true;
        }

        String guideSoilCat = guide.getSuitableSoilCategory() != null ? guide.getSuitableSoilCategory().toUpperCase() : "ANY";
        if (guideSoilCat.contains("ANY")) {
            return true;
        }

        String guideSoilDesc = guide.getSoilType() != null ? guide.getSoilType().toLowerCase() : "";
        String sel = selectedSoil.toLowerCase();

        if (sel.contains("black") || sel.contains("regur")) {
            return guideSoilCat.contains("BLACK") || guideSoilDesc.contains("black") || guideSoilDesc.contains("regur");
        }
        if (sel.contains("lateritic") || sel.contains("red")) {
            return guideSoilCat.contains("RED") || guideSoilCat.contains("LATERITIC") || guideSoilDesc.contains("lateritic") || guideSoilDesc.contains("red");
        }
        if (sel.contains("sandy")) {
            return guideSoilCat.contains("SANDY") || guideSoilDesc.contains("sandy") || guideSoilDesc.contains("loam");
        }
        if (sel.contains("alluvial") || sel.contains("loam")) {
            return guideSoilCat.contains("ALLUVIAL") || guideSoilCat.contains("LOAM") || guideSoilDesc.contains("alluvial") || guideSoilDesc.contains("loam");
        }
        if (sel.contains("clay")) {
            return guideSoilCat.contains("CLAY") || guideSoilCat.contains("BLACK") || guideSoilDesc.contains("clay");
        }

        return true;
    }
}
