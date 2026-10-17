package com.agrosmart.repository;

import android.content.Context;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.agrosmart.database.AgroSmartDbHelper;
import com.agrosmart.models.FertilizerItem;

import java.util.ArrayList;
import java.util.List;

public class FertilizerRepository {

    private static FertilizerRepository instance;
    private final List<FertilizerItem> items = new ArrayList<>();
    private final MutableLiveData<List<FertilizerItem>> itemsLiveData = new MutableLiveData<>();
    private AgroSmartDbHelper dbHelper;

    public static synchronized FertilizerRepository getInstance(Context context) {
        if (instance == null) {
            instance = new FertilizerRepository(context != null ? context.getApplicationContext() : null);
        } else if (instance.dbHelper == null && context != null) {
            instance.initDb(context.getApplicationContext());
        }
        return instance;
    }

    public static synchronized FertilizerRepository getInstance() {
        if (instance == null) {
            instance = new FertilizerRepository(null);
        }
        return instance;
    }

    public FertilizerRepository() {
        this(null);
    }

    public FertilizerRepository(Context context) {
        initSampleData();
        if (context != null) {
            initDb(context);
        }
    }

    public synchronized void initDb(Context context) {
        if (context != null) {
            this.dbHelper = AgroSmartDbHelper.getInstance(context);
            List<FertilizerItem> dbItems = dbHelper.getAllFertilizers();
            if (dbItems != null && !dbItems.isEmpty()) {
                items.clear();
                items.addAll(dbItems);
                dispatchItems();
            }
        }
    }

    private void initSampleData() {
        items.clear();
        items.add(new FertilizerItem("FER-01", "Neem Coated Urea (46% N)", "Chemical", 14, 45.0, 266.5, "12 Aug 2026", 5));
        items.add(new FertilizerItem("FER-02", "DAP (Di-Ammonium Phosphate 18:46)", "Chemical", 2, 50.0, 1350.0, "05 Jul 2026", 4));
        items.add(new FertilizerItem("FER-03", "NPK Suphala 10:26:26", "Chemical", 8, 50.0, 1470.0, "20 Aug 2026", 3));
        items.add(new FertilizerItem("FER-04", "Muriate of Potash (MOP 60% K)", "Chemical", 1, 50.0, 1700.0, "15 Jun 2026", 3));
        items.add(new FertilizerItem("FER-05", "Vermicompost Organic Compost", "Organic", 25, 40.0, 350.0, "01 Sep 2026", 6));
        items.add(new FertilizerItem("FER-06", "Zinc Sulphate Heptahydrate (21% Zn)", "Micronutrient", 5, 25.0, 850.0, "10 Jul 2026", 2));
        dispatchItems();
    }

    private void dispatchItems() {
        try {
            if (Looper.getMainLooper() != null && Looper.myLooper() == Looper.getMainLooper()) {
                itemsLiveData.setValue(new ArrayList<>(items));
            } else {
                itemsLiveData.postValue(new ArrayList<>(items));
            }
        } catch (Exception ignored) {
            // JVM Unit test environment fallback
        }
    }

    public LiveData<List<FertilizerItem>> getFertilizers() {
        return itemsLiveData;
    }

    public synchronized boolean addFertilizer(FertilizerItem item) {
        if (item == null) return false;
        if (item.getQuantityBags() < 0 || item.getCostPerBag() < 0) return false;

        items.add(0, item);
        if (dbHelper != null) {
            dbHelper.addFertilizer(item, "9876543210");
        }
        dispatchItems();

        if (item.getQuantityBags() <= item.getLowStockThreshold()) {
            NotificationRepository.getInstance().addNotification(
                    "NOTIF-FERT-" + item.getId(),
                    "⚠️ Low Fertilizer Stock Alert",
                    item.getName() + " stock is low (" + item.getQuantityBags() + " bags remaining).",
                    "Just now",
                    "INVENTORY",
                    false
            );
        }
        return true;
    }

    public synchronized void removeFertilizer(String id) {
        if (id == null) return;
        items.removeIf(it -> it.getId().equals(id));
        if (dbHelper != null) {
            dbHelper.deleteFertilizer(id);
        }
        dispatchItems();
    }

    public synchronized boolean updateQuantity(String id, int newQty) {
        if (id == null || newQty < 0) return false;
        for (FertilizerItem it : items) {
            if (it.getId().equals(id)) {
                it.setQuantityBags(newQty);
                if (dbHelper != null) {
                    dbHelper.updateFertilizerQuantity(id, newQty);
                }
                if (newQty <= it.getLowStockThreshold()) {
                    NotificationRepository.getInstance().addNotification(
                            "NOTIF-FERT-LOW-" + id,
                            "⚠️ Low Fertilizer Stock Alert",
                            it.getName() + " stock is critically low (" + newQty + " bags remaining).",
                            "Just now",
                            "INVENTORY",
                            false
                    );
                }
                break;
            }
        }
        dispatchItems();
        return true;
    }
}
