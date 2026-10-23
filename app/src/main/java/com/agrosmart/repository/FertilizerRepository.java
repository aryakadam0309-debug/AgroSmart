package com.agrosmart.repository;

import android.content.Context;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.agrosmart.database.AgroSmartDbHelper;
import com.agrosmart.models.FertilizerItem;
import com.agrosmart.network.ApiClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FertilizerRepository {

    private static final String TAG = "FertilizerRepo";
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

    public void loadFertilizers(String phone) {
        ApiClient.getInstance().getApi().getFertilizers(phone)
                .enqueue(new Callback<List<FertilizerItem>>() {
                    @Override
                    public void onResponse(Call<List<FertilizerItem>> call, Response<List<FertilizerItem>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            synchronized (FertilizerRepository.this) {
                                items.clear();
                                items.addAll(response.body());
                                if (dbHelper != null) {
                                    for (FertilizerItem fi : response.body()) {
                                        dbHelper.addFertilizer(fi, phone);
                                    }
                                }
                            }
                            dispatchItems();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<FertilizerItem>> call, Throwable t) {
                        Log.w(TAG, "Backend unreachable for fertilizer inventory: " + t.getMessage());
                    }
                });
    }

    public synchronized boolean addFertilizer(FertilizerItem item) {
        if (item == null) return false;
        if (item.getQuantityBags() < 0 || item.getCostPerBag() < 0) return false;

        items.add(0, item);
        if (dbHelper != null) {
            dbHelper.addFertilizer(item, item.getFarmerPhone() != null ? item.getFarmerPhone() : "9876543210");
        }
        dispatchItems();

        // Persist to MySQL
        ApiClient.getInstance().getApi().addFertilizer(item)
                .enqueue(new Callback<FertilizerItem>() {
                    @Override
                    public void onResponse(Call<FertilizerItem> call, Response<FertilizerItem> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Log.i(TAG, "Fertilizer saved to MySQL: " + response.body().getId());
                        }
                    }

                    @Override
                    public void onFailure(Call<FertilizerItem> call, Throwable t) {
                        Log.w(TAG, "Failed to persist fertilizer to MySQL: " + t.getMessage());
                    }
                });

        return true;
    }

    public synchronized void removeFertilizer(String id) {
        if (id == null) return;
        items.removeIf(it -> it.getId().equals(id));
        if (dbHelper != null) {
            dbHelper.deleteFertilizer(id);
        }
        dispatchItems();

        ApiClient.getInstance().getApi().deleteFertilizer(id)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        Log.i(TAG, "Fertilizer deleted from MySQL: " + id);
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.w(TAG, "Failed to delete fertilizer on backend: " + t.getMessage());
                    }
                });
    }

    public synchronized boolean updateQuantity(String id, int newQty) {
        if (id == null || newQty < 0) return false;
        FertilizerItem target = null;
        for (FertilizerItem it : items) {
            if (it.getId().equals(id)) {
                it.setQuantityBags(newQty);
                target = it;
                if (dbHelper != null) {
                    dbHelper.updateFertilizerQuantity(id, newQty);
                }
                break;
            }
        }
        dispatchItems();

        if (target != null) {
            ApiClient.getInstance().getApi().addFertilizer(target)
                    .enqueue(new Callback<FertilizerItem>() {
                        @Override
                        public void onResponse(Call<FertilizerItem> call, Response<FertilizerItem> response) {
                            Log.i(TAG, "Fertilizer quantity update synced to MySQL");
                        }

                        @Override
                        public void onFailure(Call<FertilizerItem> call, Throwable t) {
                            Log.w(TAG, "Failed to sync fertilizer quantity: " + t.getMessage());
                        }
                    });
        }
        return true;
    }
}
