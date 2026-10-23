package com.agrosmart.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.agrosmart.models.CropDisease;

import java.util.ArrayList;
import java.util.List;

public class CropDiseaseRepository {

    private static CropDiseaseRepository instance;
    private final List<CropDisease> diseases = new ArrayList<>();

    public static synchronized CropDiseaseRepository getInstance() {
        if (instance == null) {
            instance = new CropDiseaseRepository();
        }
        return instance;
    }

    private CropDiseaseRepository() {
        initDiseases();
    }

    private void initDiseases() {
        diseases.clear();

        // 1. Vidarbha & Marathwada Cotton
        diseases.add(new CropDisease(
                "DIS01", "Cotton (कपास)", "Pink Bollworm (गुलाबी बोंडअळी)", "Pest Larvae (Pectinophora gossypiella)",
                "HIGH",
                "Rosetted flowers that fail to open properly. Larvae bore into developing bolls, eating seeds, ruining locules, and staining lint.",
                "Late monsoon high humidity, continuous Bt-cotton monoculture, cloudy overcast days.",
                "Install 5-8 pheromone traps per acre at 45 DAS for ETL monitoring (8 moths/trap/night). Maintain non-Bt refuge border rows.",
                "Release Trichogramma egg parasitoids @ 60,000/acre at weekly intervals. Spray Neem seed kernel extract (5% NSKE).",
                "Profenofos 50% EC @ 2 ml/liter or Emamectin Benzoate 5% SG @ 0.5g/liter when 10% rosette flowers appear.",
                "Vidarbha, Marathwada, Western Maharashtra"
        ));

        diseases.add(new CropDisease(
                "DIS02", "Cotton (कपास)", "Fusarium Wilt & Root Rot (मर व मूळकुज)", "Fungal (Fusarium oxysporum / Rhizoctonia)",
                "HIGH",
                "Yellowing and browning of leaves from bottom upwards. Sudden drooping and complete wilting of plant. Dark brown vascular discoloration inside stem.",
                "Heavy black cotton soils with poor drainage followed by sudden dry spells and high soil temperature (30°C - 35°C).",
                "Deep summer ploughing, crop rotation with sorghum or maize, avoid repeated cotton on infected fields.",
                "Soil drenching with Trichoderma viride @ 2.5 kg/acre mixed with 100 kg farmyard manure at sowing.",
                "Copper Oxychloride 50% WP @ 3g/liter or Carbendazim 50% WP @ 1g/liter drenched at base of affected plants.",
                "Vidarbha, Marathwada"
        ));

        // 2. Soybean - Vidarbha, Marathwada, Western Maharashtra
        diseases.add(new CropDisease(
                "DIS03", "Soybean (सोयाबीन)", "Anthracnose & Pod Blight (शेंगा करपा)", "Fungal (Colletotrichum truncatum)",
                "HIGH",
                "Irregular brown to black spots on leaves, veins, stems, and pods. Infected pods turn prematurely black, produce shriveled, moldy seeds.",
                "Warm (28°C - 32°C), humid rainy conditions with prolonged leaf wetness in July-August.",
                "Certified seed treatment with Thiram + Carbendazim (2:1) @ 3g/kg seed. Maintain optimal plant population.",
                "Spray Trichoderma viride @ 5g/liter or fermented cow urine spray (10%) at 30 & 45 DAS.",
                "Hexaconazole 5% EC @ 2 ml/liter or Pyraclostrobin 20% WG @ 1g/liter at early pod formation.",
                "Vidarbha, Marathwada, Western Maharashtra"
        ));

        diseases.add(new CropDisease(
                "DIS04", "Soybean (सोयाबीन)", "Yellow Mosaic Virus / YMV (पिवळा मोझॅक)", "Viral (Whitefly vector - Bemisia tabaci)",
                "HIGH",
                "Alternating bright yellow and green patches on young leaves. Plants remain stunted with few pods containing small seeds.",
                "High whitefly population under dry rain breaks in Kharif season.",
                "Rogue out and destroy initial infected plants. Grow resistant varieties like JS 335, Phule Purva.",
                "Install yellow sticky traps (10-12 per acre). Spray 5% Neem oil at 15-day intervals.",
                "Spray Thiamethoxam 25% WG @ 0.3g/liter or Acetamiprid 20% SP @ 0.2g/liter to suppress vector whiteflies.",
                "Vidarbha, Marathwada"
        ));

        // 3. Western Maharashtra & Marathwada - Sugarcane
        diseases.add(new CropDisease(
                "DIS05", "Sugarcane (गन्ना / ऊस)", "Red Rot (तांबेरा / लाल सडन)", "Fungal (Colletotrichum falcatum)",
                "HIGH",
                "Third or fourth leaf from top starts withering. Split canes show blood-red internal tissues with characteristic white transverse patches and alcoholic odor.",
                "Waterlogging in heavy clay soils, high humidity (>90%), planting infected seed sets.",
                "Use healthy seed canes from certified nurseries. Practice hot water treatment (52°C for 30 mins) before setts planting.",
                "Apply Trichoderma harzianum @ 5 kg/ha enriched in well-decomposed FYM at furrow opening.",
                "Drench setts in Carbendazim 50% WP (1g/liter) before planting. Rogue out infected clumps immediately.",
                "Western Maharashtra, Marathwada"
        ));

        diseases.add(new CropDisease(
                "DIS06", "Sugarcane (गन्ना / ऊस)", "Whip Smut (चाबूक काणी)", "Fungal (Sporisorium scitamineum)",
                "MEDIUM",
                "Central shoot gets modified into a long, curved black whip-like structure (up to 1-1.5 meters) covered with millions of black sooty spores.",
                "Ratoon cropping in warm and dry climate, infected planting material.",
                "Avoid continuous ratooning of infected plots. Disinfect cutting knives with Lysol.",
                "Carefully cut and bag smut whips in plastic without shedding spores, then burn them outside field.",
                "Treat setts with Propiconazole 25% EC @ 1 ml/liter of water before planting.",
                "Western Maharashtra, Marathwada"
        ));

        // 4. Western Maharashtra & Marathwada - Onion
        diseases.add(new CropDisease(
                "DIS07", "Onion (प्याज / कांदा)", "Purple Blotch & Stemphylium Blight (जांभळा करपा)", "Fungal (Alternaria porri / Stemphylium)",
                "HIGH",
                "Small sunken water-soaked spots that quickly turn purple in the center with yellow halos. Severe blighting causes foliage collapse and poor bulb size.",
                "Relative humidity 80-90%, frequent winter drizzle, morning dew, temperatures 20°C - 28°C.",
                "Raised bed cultivation, drip or micro-sprinkler irrigation instead of flooding. Crop rotation with cereals.",
                "Foliar spray of wood ash suspension or garlic-chilli-neem extract (3%).",
                "Difenoconazole 25% EC @ 1 ml/liter or Mancozeb 75% WP @ 2.5g/liter with sticker.",
                "Western Maharashtra, Marathwada"
        ));

        // 5. Western Maharashtra & Marathwada - Pomegranate
        diseases.add(new CropDisease(
                "DIS08", "Pomegranate (अनार / डाळिंब)", "Bacterial Blight / Telya (तेल्या रोग)", "Bacterial (Xanthomonas axonopodis pv. punicae)",
                "HIGH",
                "Water-soaked black oily spots with 'Y' or 'L' shaped cracks on fruit skin. Oily brown lesions on leaves and black nodal cankers causing stem breakage.",
                "Monsoon Mrig bahar rains, cloudy weather, temperatures 25°C - 35°C, high wind causing thorns friction.",
                "Adopt Hasta Bahar (Sept-Oct flowering) to escape heavy rains. Disinfect secateurs with sodium hypochlorite.",
                "Spray Bordeaux mixture (0.5% - 1%) during rest and flush periods. Apply cow dung slurry + copper sulfate.",
                "Streptocycline (500 ppm) @ 0.5g/liter + Copper Oxychloride 50% WP @ 2g/liter with good wetting agent.",
                "Western Maharashtra, Marathwada"
        ));

        // 6. Konkan & Vidarbha - Rice
        diseases.add(new CropDisease(
                "DIS09", "Rice / Paddy (धान)", "Blast & Neck Blast (करपा रोग)", "Fungal (Magnaporthe oryzae)",
                "HIGH",
                "Spindle-shaped eye lesions with greyish center and brown margins on leaves. Black lesions at neck of panicle causing panicles to snap and produce chaffy grains.",
                "High relative humidity (>90%), cool nights (20°C - 24°C), intermittent drizzles, excessive nitrogen fertilizer.",
                "Avoid excessive urea; split nitrogen in 3 doses. Grow blast tolerant varieties like Karjat, Ratnagiri, Sahyadri.",
                "Spray fermented butter-milk (5%) or leaf extract of Adhatoda vasica.",
                "Tricyclazole 75% WP @ 0.6g/liter or Isoprothiolane 40% EC @ 1.5 ml/liter at panicle emergence.",
                "Konkan, Vidarbha"
        ));

        diseases.add(new CropDisease(
                "DIS10", "Rice / Paddy (धान)", "Bacterial Leaf Blight / BLB (पानावरील करपा)", "Bacterial (Xanthomonas oryzae)",
                "HIGH",
                "Water-soaked yellowish-green wavy lesions along leaf margins progressing from tip downwards. Bacterial ooze beads visible in early morning.",
                "Warm weather (25°C - 34°C), heavy torrential rains in Konkan, strong winds, standing water.",
                "Clip seedlings with sanitized tools. Apply potassium fertilizer to improve leaf epidermal strength.",
                "Spray fresh cow dung supernatant (20%) mixed with fine turmeric powder (2g/liter).",
                "Streptocycline @ 1g in 10 liters + Copper Hydroxide 77% WP @ 20g in 10 liters.",
                "Konkan, Vidarbha"
        ));

        // 7. Marathwada & Vidarbha - Pigeonpea / Tur
        diseases.add(new CropDisease(
                "DIS11", "Pigeonpea / Tur (अरहर / तूर)", "Fusarium Wilt & Sterility Mosaic (उकटा व वांझ रोग)", "Fungal (Fusarium) / SMD Virus (Eriophyid Mite)",
                "HIGH",
                "Sudden wilting of green plants during flowering. SMD causes bushy growth, light green mottling, and complete lack of flowering and pod formation.",
                "Continuous monocropping in deep black soils, mite vector multiplication in humid dry spells.",
                "Crop rotation with sorghum, bajra, or soybean (4:2 intercrop). Grow wilt-resistant varieties like BSMR-736, BDN-711.",
                "Seed treatment with Trichoderma harzianum @ 10g/kg seed + Rhizobium culture.",
                "Spray Fenazaquin 10% EC @ 2 ml/liter or Propargite 57% EC @ 2 ml/liter to control vector mites.",
                "Marathwada, Vidarbha"
        ));

        // 8. Marathwada, Vidarbha & Western Maharashtra - Gram / Chana
        diseases.add(new CropDisease(
                "DIS12", "Gram / Chana (चना)", "Helicoverpa Pod Borer (घाटे अळी)", "Pest Larvae (Helicoverpa armigera)",
                "HIGH",
                "Caterpillars feed on tender leaves, then bore round holes into developing pods, thrusting their anterior body inside to eat seeds.",
                "Cloudy, warm weather during flowering and pod development in December-January.",
                "Install bird perches (T-shaped sticks, 20/acre) for predatory birds. Plant marigold as trap crop on borders.",
                "Spray HaNPV (Helicoverpa nuclear polyhedrosis virus) @ 250 LE/acre or Neem seed kernel extract (5%).",
                "Chlorantraniliprole 18.5% SC @ 0.3 ml/liter or Emamectin Benzoate 5% SG @ 0.4g/liter at ETL (1 larva/meter row).",
                "Marathwada, Western Maharashtra, Vidarbha"
        ));

        // 9. Western Maharashtra, Vidarbha & Marathwada - Wheat
        diseases.add(new CropDisease(
                "DIS13", "Wheat (गेहूं)", "Yellow & Brown Leaf Rust (तांबेरा)", "Fungal (Puccinia triticina / striiformis)",
                "HIGH",
                "Bright orange-yellow to reddish-brown pustules arranged on leaves. Spores rub off like rusty powder on hands. Premature leaf death reduces grain weight.",
                "Cool night temperatures (12°C - 20°C) with morning dew and high humidity (>85%).",
                "Timely sowing before Nov 20. Use certified rust-resistant varieties like HD-2967, NIAW-301 (Trimbak), Phule Samadhan.",
                "Spray sour buttermilk (5%) or Neem extract at first appearance of orange specks.",
                "Propiconazole 25% EC (Tilt) @ 1 ml/liter or Tebuconazole 25.9% EC @ 1 ml/liter.",
                "Western Maharashtra, Vidarbha, Marathwada"
        ));

        // 10. Western Maharashtra & Marathwada - Tomato
        diseases.add(new CropDisease(
                "DIS14", "Tomato (टमाटर)", "Early & Late Blight (झुलसा / करपा)", "Fungal (Alternaria solani / Phytophthora)",
                "MEDIUM",
                "Concentric dark brown 'target board' rings on older leaves. Leaves turn brown and drop. Fruits develop large sunken black leathery rot spots.",
                "Cool, foggy mornings followed by warm days with intermittent showers.",
                "Staking plants on trellises, mulching with silver-black plastic, drip irrigation to prevent soil splash on leaves.",
                "Spray Panchagavya (3%) or Bordeaux mixture (1%) at 15-day intervals.",
                "Mancozeb 75% WP @ 2.5g/liter or Metalaxyl 8% + Mancozeb 64% WP @ 2g/liter.",
                "Western Maharashtra, Marathwada"
        ));

        // 11. Konkan - Cashew & Mango
        diseases.add(new CropDisease(
                "DIS15", "Cashew (काजू)", "Tea Mosquito Bug & Anthracnose (ढेकूण व करपा)", "Insect Pest (Helopeltis) & Fungal (Colletotrichum)",
                "HIGH",
                "Nymphs and adults suck sap from tender shoots, panicles, and developing apples/nuts, producing angular water-soaked black resinous lesions causing shoot dieback.",
                "Foggy humid weather in coastal Konkan during cashew flushing and flowering (December to March).",
                "Prune water shoots and criss-cross branches after monsoon for ample sunlight penetration.",
                "Spray Beauveria bassiana @ 5g/liter or 5% Neem extract at vegetative flush.",
                "Lambda Cyhalothrin 5% EC @ 0.6 ml/liter + Carbendazim 50% WP @ 1g/liter at flowering stage.",
                "Konkan"
        ));

        diseases.add(new CropDisease(
                "DIS16", "Mango (आम)", "Powdery Mildew & Anthracnose (भुरी व करपा)", "Fungal (Oidium mangiferae / Colletotrichum)",
                "HIGH",
                "White powdery fungal coating on panicles, flowers, and tender fruits causing flower drop and fruitlet mummification. Black tear-stain streaks on mature fruits.",
                "Cloudy mornings with high humidity and morning dew during flowering (January-February).",
                "Clean orchard floor, prune dead and infected twigs after monsoon harvest.",
                "Foliar spray of wettable sulfur 80% WP @ 2g/liter at pre-flowering stage.",
                "Hexaconazole 5% SC @ 1 ml/liter or Azoxystrobin 23% SC @ 1 ml/liter when flowers are at pea size.",
                "Konkan, Western Maharashtra"
        ));
    }

    public LiveData<List<CropDisease>> getDiseases(String query, String cropFilter) {
        return getDiseases(query, cropFilter, "All Maharashtra");
    }

    public LiveData<List<CropDisease>> getDiseases(String query, String cropFilter, String region) {
        MutableLiveData<List<CropDisease>> liveData = new MutableLiveData<>();
        List<CropDisease> filtered = new ArrayList<>();

        for (CropDisease d : diseases) {
            boolean matchQuery = query == null || query.trim().isEmpty()
                    || d.getDiseaseName().toLowerCase().contains(query.toLowerCase())
                    || d.getCropName().toLowerCase().contains(query.toLowerCase())
                    || d.getSymptoms().toLowerCase().contains(query.toLowerCase());

            boolean matchCrop = cropFilter == null || cropFilter.equals("All Crops")
                    || d.getCropName().toLowerCase().contains(cropFilter.toLowerCase());

            boolean matchRegion = matchesRegion(d, region);

            if (matchQuery && matchCrop && matchRegion) {
                filtered.add(d);
            }
        }
        liveData.setValue(filtered);
        return liveData;
    }

    private boolean matchesRegion(CropDisease disease, String selectedRegion) {
        if (selectedRegion == null || selectedRegion.trim().isEmpty()
                || selectedRegion.contains("All Maharashtra")) {
            return true;
        }

        String dRegion = disease.getRegion().toLowerCase();
        if (dRegion.contains("all maharashtra") || dRegion.contains("all") || dRegion.contains("any")) {
            return true;
        }

        String sel = selectedRegion.toLowerCase();

        // 1. Zone checks
        if (sel.contains("western") && dRegion.contains("western")) return true;
        if (sel.contains("marathwada") && dRegion.contains("marathwada")) return true;
        if (sel.contains("vidarbha") && dRegion.contains("vidarbha")) return true;
        if (sel.contains("konkan") && dRegion.contains("konkan")) return true;

        // 2. Bidirectional containment
        if (sel.contains(dRegion) || dRegion.contains(sel)) {
            return true;
        }

        // 3. District checks
        String[] districts = {
                "pune", "satara", "solapur", "nashik", "kolhapur", "ahmednagar", "sangli",
                "chhatrapati sambhajinagar", "aurangabad", "latur", "nanded", "jalna", "parbhani", "beed", "dharashiv", "osmanabad",
                "nagpur", "akola", "amravati", "yavatmal", "wardha", "chandrapur", "bhandara", "gondia", "buldhana",
                "ratnagiri", "sindhudurg", "raigad", "thane", "palghar"
        };
        for (String d : districts) {
            if (sel.contains(d) && dRegion.contains(d)) {
                return true;
            }
        }

        return false;
    }
}
