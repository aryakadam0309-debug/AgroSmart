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
        diseases.add(new CropDisease(
                "DIS01", "Wheat (गेहूं)", "Yellow / Stripe Rust (पीला रतुआ)", "Fungal (Puccinia striiformis)",
                "HIGH",
                "Bright yellow pustules arranged in linear stripes on upper leaf surface. Dust of yellow spores rubs off on fingers.",
                "High humidity (above 85%), cool temperatures (10°C - 20°C), dense foliage.",
                "Use rust-resistant varieties like HD-2967, DBW-187. Avoid late sowing and excessive nitrogen fertilizer.",
                "Spray sour buttermilk (5%) or Neem kernel extract (5%) at initial symptom detection.",
                "Propiconazole 25% EC (Tilt) @ 1 ml/liter of water or Tebuconazole 25.9% EC @ 1 ml/liter."
        ));
        diseases.add(new CropDisease(
                "DIS02", "Soybean (सोयाबीन)", "Anthracnose & Pod Blight", "Fungal (Colletotrichum truncatum)",
                "HIGH",
                "Irregular brown to black spots on leaves, veins, stems, and pods. Infected pods produce shriveled, moldy seeds.",
                "Warm (28°C - 32°C), humid rainy conditions with prolonged leaf wetness.",
                "Certified seed treatment with Thiram + Carbendazim (2:1) @ 3g/kg seed. Crop rotation with non-legumes.",
                "Spray Trichoderma viride @ 5g/liter or fermented cow urine spray (10%) at 30 & 45 DAS.",
                "Hexaconazole 5% EC @ 2 ml/liter or Pyraclostrobin 20% WG @ 1g/liter at pod formation."
        ));
        diseases.add(new CropDisease(
                "DIS03", "Cotton (कपास)", "Pink Bollworm (गुलाबी सुंडी)", "Pest Larvae (Pectinophora gossypiella)",
                "HIGH",
                "Rosetted flowers that fail to open properly. Larvae bore into developing bolls, eating seeds and staining lint.",
                "Late season high humidity, cloudy days, continuous monoculture.",
                "Install 5-8 pheromone traps per acre at 45 DAS for ETL monitoring. Grow refuge non-Bt border rows.",
                "Release Trichogramma egg parasitoids @ 60,000/acre at weekly intervals. Spray Neem seed kernel extract (5%).",
                "Profenofos 50% EC @ 2 ml/liter or Emamectin Benzoate 5% SG @ 0.5g/liter when 10% rosette flowers appear."
        ));
        diseases.add(new CropDisease(
                "DIS04", "Rice (धान)", "Bacterial Leaf Blight (BLB)", "Bacterial (Xanthomonas oryzae)",
                "HIGH",
                "Water-soaked yellowish lesions starting from leaf tips and margins, progressing downwards in wavy margins.",
                "Warm weather (25°C - 34°C), torrential rains, typhoons, and excess nitrogen top-dressing.",
                "Maintain field drainage. Avoid clipping seedlings during transplanting. Use balanced potassium fertilization.",
                "Spray fresh cow dung filtrate (20%) mixed with turmeric powder (2g/liter).",
                "Streptocycline (100 ppm) @ 1g in 10 liters + Copper Oxychloride 50% WP @ 25g in 10 liters."
        ));
        diseases.add(new CropDisease(
                "DIS05", "Tomato (टमाटर)", "Early & Late Blight (झुलसा रोग)", "Fungal (Alternaria & Phytophthora)",
                "MEDIUM",
                "Concentric dark brown target-board rings on older leaves leading to defoliation. Dark water-soaked fruit lesions.",
                "Cool wet nights followed by warm humid days.",
                "Trellis staking, wide spacing (60x45 cm), drip irrigation to avoid splashing soil on leaves.",
                "Spray Panchagavya (3%) or Bordeaux mixture (1%) at 15-day intervals.",
                "Mancozeb 75% WP @ 2.5g/liter or Metalaxyl 8% + Mancozeb 64% WP @ 2g/liter."
        ));
        diseases.add(new CropDisease(
                "DIS06", "Onion (प्याज)", "Purple Blotch (बैंगनी धब्बा)", "Fungal (Alternaria porri)",
                "MEDIUM",
                "Small water-soaked lesions that enlarge into sunken purple spots with faint concentric rings and yellow halos.",
                "Relative humidity 80-90%, frequent winter drizzle or morning dew.",
                "Ensure raised bed nursery. Dip seedlings in Carbendazim solution before transplanting.",
                "Foliar spray of wood ash suspension or garlic-chilli extract (3%).",
                "Difenoconazole 25% EC @ 1 ml/liter or Chlorothalonil 75% WP @ 2g/liter."
        ));
    }

    public LiveData<List<CropDisease>> getDiseases(String query, String cropFilter) {
        MutableLiveData<List<CropDisease>> liveData = new MutableLiveData<>();
        List<CropDisease> filtered = new ArrayList<>();

        for (CropDisease d : diseases) {
            boolean matchQuery = query == null || query.trim().isEmpty()
                    || d.getDiseaseName().toLowerCase().contains(query.toLowerCase())
                    || d.getCropName().toLowerCase().contains(query.toLowerCase())
                    || d.getSymptoms().toLowerCase().contains(query.toLowerCase());

            boolean matchCrop = cropFilter == null || cropFilter.equals("All Crops")
                    || d.getCropName().toLowerCase().contains(cropFilter.toLowerCase());

            if (matchQuery && matchCrop) {
                filtered.add(d);
            }
        }
        liveData.setValue(filtered);
        return liveData;
    }
}
