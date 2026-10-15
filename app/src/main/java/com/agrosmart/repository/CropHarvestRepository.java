package com.agrosmart.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.agrosmart.models.CropHarvest;

import java.util.ArrayList;
import java.util.List;

public class CropHarvestRepository {

    private static CropHarvestRepository instance;
    private final List<CropHarvest> harvestList = new ArrayList<>();
    private final MutableLiveData<List<CropHarvest>> harvestLiveData = new MutableLiveData<>();

    public static synchronized CropHarvestRepository getInstance() {
        if (instance == null) {
            instance = new CropHarvestRepository();
        }
        return instance;
    }

    private CropHarvestRepository() {
        initSampleData();
    }

    private void initSampleData() {
        harvestList.clear();
        harvestList.add(new CropHarvest(
                "HV-01", "Soybean (सोयाबीन)", "JS-335",
                48.0, 30.0, "25 Oct 2026", "Farm Warehouse, Shed #2", 4850.0
        ));
        harvestList.add(new CropHarvest(
                "HV-02", "Wheat (गेहूं)", "Sharbati Deluxe",
                85.0, 40.0, "15 Apr 2026", "APMC Grain Storage Godown", 2900.0
        ));
        harvestList.add(new CropHarvest(
                "HV-03", "Cotton (कपास)", "BT Shankar-6",
                28.0, 28.0, "10 Jan 2026", "Cotton Ginning Mill Yard", 7600.0
        ));
        harvestList.add(new CropHarvest(
                "HV-04", "Onion (लाल प्याज)", "Nashik Red Premium",
                120.0, 60.0, "18 May 2026", "Naturally Ventilated Chawl", 2200.0
        ));
        harvestLiveData.setValue(new ArrayList<>(harvestList));
    }

    public LiveData<List<CropHarvest>> getHarvests() {
        return harvestLiveData;
    }

    public void addHarvest(CropHarvest harvest) {
        harvestList.add(0, harvest);
        harvestLiveData.setValue(new ArrayList<>(harvestList));
    }

    public void removeHarvest(String id) {
        harvestList.removeIf(h -> h.getId().equals(id));
        harvestLiveData.setValue(new ArrayList<>(harvestList));
    }

    public void sellStock(String id, double quantitySold) {
        for (CropHarvest h : harvestList) {
            if (h.getId().equals(id)) {
                h.setSoldQuintals(h.getSoldQuintals() + quantitySold);
                break;
            }
        }
        harvestLiveData.setValue(new ArrayList<>(harvestList));
    }
}
