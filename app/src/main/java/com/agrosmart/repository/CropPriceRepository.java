package com.agrosmart.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.agrosmart.models.CropPrice;

import java.util.ArrayList;
import java.util.List;

public class CropPriceRepository {

    private static CropPriceRepository instance;
    private final List<CropPrice> mockPrices = new ArrayList<>();

    public static synchronized CropPriceRepository getInstance() {
        if (instance == null) {
            instance = new CropPriceRepository();
        }
        return instance;
    }

    private CropPriceRepository() {
        initMockData();
    }

    private void initMockData() {
        mockPrices.clear();
        mockPrices.add(new CropPrice(
                "CP001", "Soybean (सोयाबीन)", "Oilseeds", "JS-335 Yellow",
                "Indore Mandi", "Madhya Pradesh", "Indore",
                4400, 4950, 4820, "₹ / Qtl", "UP", 75.0,
                "Today, 11:30 AM", "3,400 Quintals"
        ));
        mockPrices.add(new CropPrice(
                "CP002", "Wheat (गेहूं)", "Cereals", "Sharbati Deluxe",
                "Pune APMC (Gultekdi)", "Maharashtra", "Pune",
                2600, 3100, 2850, "₹ / Qtl", "UP", 40.0,
                "Today, 10:45 AM", "5,120 Quintals"
        ));
        mockPrices.add(new CropPrice(
                "CP003", "Cotton (कपास)", "Cash Crops", "BT Shankar-6",
                "Rajkot APMC Yard", "Gujarat", "Rajkot",
                7100, 7850, 7550, "₹ / Qtl", "DOWN", -120.0,
                "Today, 12:15 PM", "1,890 Quintals"
        ));
        mockPrices.add(new CropPrice(
                "CP004", "Onion (लाल प्याज)", "Vegetables", "Nashik Red Premium",
                "Lasalgaon APMC", "Maharashtra", "Nashik",
                1600, 2450, 2150, "₹ / Qtl", "UP", 150.0,
                "Today, 09:30 AM", "12,400 Quintals"
        ));
        mockPrices.add(new CropPrice(
                "CP005", "Gram / Chana (चना)", "Pulses", "Desi Bold Chana",
                "Akola APMC", "Maharashtra", "Akola",
                5600, 6150, 5920, "₹ / Qtl", "STABLE", 0.0,
                "Today, 11:00 AM", "2,350 Quintals"
        ));
        mockPrices.add(new CropPrice(
                "CP006", "Basmati Rice (चावल)", "Cereals", "1121 Pusa Basmati",
                "Khanna Grain Market", "Punjab", "Ludhiana",
                3800, 4400, 4200, "₹ / Qtl", "UP", 60.0,
                "Today, 10:15 AM", "4,800 Quintals"
        ));
        mockPrices.add(new CropPrice(
                "CP007", "Mustard (सरसों)", "Oilseeds", "Yellow Gold",
                "Kota Mandi", "Rajasthan", "Kota",
                5100, 5650, 5450, "₹ / Qtl", "DOWN", -35.0,
                "Today, 01:00 PM", "3,100 Quintals"
        ));
        mockPrices.add(new CropPrice(
                "CP008", "Tomato (टमाटर)", "Vegetables", "Hybrid Vaishnavi",
                "Narayangaon APMC", "Maharashtra", "Pune",
                1200, 2100, 1800, "₹ / Qtl", "DOWN", -90.0,
                "Today, 08:45 AM", "8,900 Quintals"
        ));
        mockPrices.add(new CropPrice(
                "CP009", "Maize (मक्का)", "Cereals", "Yellow Feed Corn",
                "Chhindwara Mandi", "Madhya Pradesh", "Chhindwara",
                2050, 2400, 2280, "₹ / Qtl", "UP", 30.0,
                "Today, 11:50 AM", "6,200 Quintals"
        ));
        mockPrices.add(new CropPrice(
                "CP010", "Potato (आलू)", "Vegetables", "Kufri Jyoti",
                "Agra Mandi", "Uttar Pradesh", "Agra",
                1300, 1850, 1650, "₹ / Qtl", "STABLE", 0.0,
                "Today, 12:00 PM", "15,200 Quintals"
        ));
    }

    public LiveData<List<CropPrice>> getCropPrices(String searchQuery, String selectedState, String selectedCategory) {
        MutableLiveData<List<CropPrice>> liveData = new MutableLiveData<>();
        List<CropPrice> filtered = new ArrayList<>();

        for (CropPrice price : mockPrices) {
            boolean matchesSearch = searchQuery == null || searchQuery.trim().isEmpty()
                    || price.getCropName().toLowerCase().contains(searchQuery.toLowerCase())
                    || price.getMarketName().toLowerCase().contains(searchQuery.toLowerCase())
                    || price.getVariety().toLowerCase().contains(searchQuery.toLowerCase());

            boolean matchesState = selectedState == null || selectedState.equals("All States")
                    || price.getState().equalsIgnoreCase(selectedState);

            boolean matchesCategory = selectedCategory == null || selectedCategory.equals("All Crops")
                    || price.getCategory().equalsIgnoreCase(selectedCategory);

            if (matchesSearch && matchesState && matchesCategory) {
                filtered.add(price);
            }
        }

        liveData.setValue(filtered);
        return liveData;
    }

    public void addCropPrice(CropPrice price) {
        mockPrices.add(0, price);
    }

    public boolean removeCropPrice(String id) {
        for (int i = 0; i < mockPrices.size(); i++) {
            if (mockPrices.get(i).getId().equals(id)) {
                mockPrices.remove(i);
                return true;
            }
        }
        return false;
    }

    public List<CropPrice> getAllPrices() {
        return new ArrayList<>(mockPrices);
    }
}
