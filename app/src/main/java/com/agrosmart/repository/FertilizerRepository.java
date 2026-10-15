package com.agrosmart.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.agrosmart.models.FertilizerItem;

import java.util.ArrayList;
import java.util.List;

public class FertilizerRepository {

    private static FertilizerRepository instance;
    private final List<FertilizerItem> items = new ArrayList<>();
    private final MutableLiveData<List<FertilizerItem>> itemsLiveData = new MutableLiveData<>();

    public static synchronized FertilizerRepository getInstance() {
        if (instance == null) {
            instance = new FertilizerRepository();
        }
        return instance;
    }

    private FertilizerRepository() {
        initSampleData();
    }

    private void initSampleData() {
        items.clear();
        items.add(new FertilizerItem("FER-01", "Neem Coated Urea (46% N)", "Chemical", 14, 45.0, 266.5, "12 Aug 2026", 5));
        items.add(new FertilizerItem("FER-02", "DAP (Di-Ammonium Phosphate 18:46)", "Chemical", 2, 50.0, 1350.0, "05 Jul 2026", 4));
        items.add(new FertilizerItem("FER-03", "NPK Suphala 10:26:26", "Chemical", 8, 50.0, 1470.0, "20 Aug 2026", 3));
        items.add(new FertilizerItem("FER-04", "Muriate of Potash (MOP 60% K)", "Chemical", 1, 50.0, 1700.0, "15 Jun 2026", 3));
        items.add(new FertilizerItem("FER-05", "Vermicompost Organic Compost", "Organic", 25, 40.0, 350.0, "01 Sep 2026", 6));
        items.add(new FertilizerItem("FER-06", "Zinc Sulphate Heptahydrate (21% Zn)", "Micronutrient", 5, 25.0, 850.0, "10 Jul 2026", 2));
        itemsLiveData.setValue(new ArrayList<>(items));
    }

    public LiveData<List<FertilizerItem>> getFertilizers() {
        return itemsLiveData;
    }

    public void addFertilizer(FertilizerItem item) {
        items.add(0, item);
        itemsLiveData.setValue(new ArrayList<>(items));
    }

    public void removeFertilizer(String id) {
        items.removeIf(it -> it.getId().equals(id));
        itemsLiveData.setValue(new ArrayList<>(items));
    }

    public void updateQuantity(String id, int newQty) {
        for (FertilizerItem it : items) {
            if (it.getId().equals(id)) {
                it.setQuantityBags(newQty);
                break;
            }
        }
        itemsLiveData.setValue(new ArrayList<>(items));
    }
}
