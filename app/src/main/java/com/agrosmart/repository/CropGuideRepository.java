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
                "120 - 135 Days", "Well-drained Fertile Loam", "450 - 550 mm (4-6 irrigations)",
                "15°C - 25°C", "18 - 24 Quintals / Acre", "November 1 - 25",
                "March - April", "N:P:K = 120:60:40 kg/ha",
                "1. Germination (0-15d)\n2. Crown Root Initiation (20-25d)\n3. Tillering (35-45d)\n4. Flowering & Milk Stage (70-85d)\n5. Maturity (110-130d)",
                "First irrigation at CRI stage (21 days) is critical. Use certified seeds treated with Thiram/Carbendazim."
        ));
        cropGuides.add(new CropGuide(
                "CG02", "Soybean (सोयाबीन)", "Glycine max", "Kharif",
                "95 - 110 Days", "Well-drained Black Cotton Soil", "500 - 650 mm (Rainfed)",
                "22°C - 32°C", "10 - 14 Quintals / Acre", "June 20 - July 10",
                "October - November", "N:P:K = 25:60:40 kg/ha + Rhizobium",
                "1. Emergence (4-7d)\n2. Vegetative Growth (15-30d)\n3. Flowering (35-50d)\n4. Pod Filling (55-80d)\n5. Senescence & Harvest (95-105d)",
                "Avoid waterlogging. Apply seed treatment with Bradyrhizobium culture for optimal nitrogen fixation."
        ));
        cropGuides.add(new CropGuide(
                "CG03", "Cotton (कपास)", "Gossypium hirsutum", "Kharif",
                "150 - 180 Days", "Deep Black or Alluvial Loam", "600 - 850 mm",
                "25°C - 35°C", "12 - 16 Quintals / Acre", "May 15 - June 15",
                "November - January", "N:P:K = 120:60:60 kg/ha",
                "1. Seedling (0-20d)\n2. Squaring (40-50d)\n3. Flowering & Boll Formation (70-110d)\n4. Boll Maturation & Bursting (120-160d)",
                "Maintain 90x60 cm plant spacing. Install pheromone traps for pink bollworm monitoring at 45 days."
        ));
        cropGuides.add(new CropGuide(
                "CG04", "Rice / Paddy (धान)", "Oryza sativa", "Kharif",
                "125 - 145 Days", "Heavy Clayey & Silty Loam", "1100 - 1400 mm",
                "24°C - 35°C", "22 - 28 Quintals / Acre", "June 1 - June 30",
                "October - November", "N:P:K = 100:50:50 kg/ha + Zinc 25kg",
                "1. Nursery (0-25d)\n2. Transplanting (25-30d)\n3. Tillering (40-60d)\n4. Panicle Initiation (70-85d)\n5. Ripening (110-135d)",
                "Transplant 2-3 seedlings per hill at 20x15 cm spacing. Maintain shallow standing water (2-5 cm)."
        ));
        cropGuides.add(new CropGuide(
                "CG05", "Gram / Chana (चना)", "Cicer arietinum", "Rabi",
                "100 - 115 Days", "Sandy Loam to Clay Loam", "250 - 350 mm (Low water)",
                "18°C - 28°C", "8 - 12 Quintals / Acre", "October 15 - November 10",
                "February - March", "N:P:K = 20:50:20 kg/ha + PSB Culture",
                "1. Germination (0-10d)\n2. Branching & Nipping (30-40d)\n3. Flowering (50-65d)\n4. Pod Development (70-90d)\n5. Harvest (105-115d)",
                "Perform nipping of top shoots at 35-40 days to encourage extensive lateral branching and higher pod count."
        ));
        cropGuides.add(new CropGuide(
                "CG06", "Tomato (टमाटर)", "Solanum lycopersicum", "All Season",
                "90 - 120 Days", "Sandy Loam with organic matter", "400 - 600 mm (Drip)",
                "20°C - 30°C", "150 - 220 Quintals / Acre", "June-July / Oct-Nov",
                "Starting 75 days after transplant", "N:P:K = 150:100:100 kg/ha",
                "1. Nursery (0-25d)\n2. Transplanting & Staking (30-40d)\n3. Flowering (45-60d)\n4. Fruit Setting (60-80d)\n5. Harvest Flushes (80-120d)",
                "Adopt trellis staking to prevent soil contact diseases and improve fruit quality and uniform grading."
        ));
    }

    public LiveData<List<CropGuide>> getGuides(String query, String season) {
        MutableLiveData<List<CropGuide>> liveData = new MutableLiveData<>();
        List<CropGuide> filtered = new ArrayList<>();

        for (CropGuide guide : cropGuides) {
            boolean matchQuery = query == null || query.trim().isEmpty()
                    || guide.getCropName().toLowerCase().contains(query.toLowerCase())
                    || guide.getScientificName().toLowerCase().contains(query.toLowerCase());

            boolean matchSeason = season == null || season.equals("All Seasons")
                    || guide.getSeason().equalsIgnoreCase(season);

            if (matchQuery && matchSeason) {
                filtered.add(guide);
            }
        }
        liveData.setValue(filtered);
        return liveData;
    }
}
