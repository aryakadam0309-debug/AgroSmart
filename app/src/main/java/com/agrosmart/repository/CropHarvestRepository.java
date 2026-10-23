package com.agrosmart.repository;

import android.content.Context;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.agrosmart.database.AgroSmartDbHelper;
import com.agrosmart.models.CropHarvest;
import com.agrosmart.network.ApiClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CropHarvestRepository {

    private static final String TAG = "CropHarvestRepo";
    private static CropHarvestRepository instance;
    private final List<CropHarvest> harvestList = new ArrayList<>();
    private final MutableLiveData<List<CropHarvest>> harvestLiveData = new MutableLiveData<>();
    private AgroSmartDbHelper dbHelper;

    public static synchronized CropHarvestRepository getInstance(Context context) {
        if (instance == null) {
            instance = new CropHarvestRepository(context != null ? context.getApplicationContext() : null);
        } else if (instance.dbHelper == null && context != null) {
            instance.initDb(context.getApplicationContext());
        }
        return instance;
    }

    public static synchronized CropHarvestRepository getInstance() {
        if (instance == null) {
            instance = new CropHarvestRepository(null);
        }
        return instance;
    }

    public CropHarvestRepository() {
        this(null);
    }

    public CropHarvestRepository(Context context) {
        initSampleData();
        if (context != null) {
            initDb(context);
        }
    }

    public synchronized void initDb(Context context) {
        if (context != null) {
            this.dbHelper = AgroSmartDbHelper.getInstance(context);
            List<CropHarvest> dbHarvests = dbHelper.getAllHarvests();
            if (dbHarvests != null && !dbHarvests.isEmpty()) {
                harvestList.clear();
                harvestList.addAll(dbHarvests);
                dispatchHarvests();
            }
        }
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
        dispatchHarvests();
    }

    private void dispatchHarvests() {
        try {
            if (Looper.getMainLooper() != null && Looper.myLooper() == Looper.getMainLooper()) {
                harvestLiveData.setValue(new ArrayList<>(harvestList));
            } else {
                harvestLiveData.postValue(new ArrayList<>(harvestList));
            }
        } catch (Exception ignored) {
            // JVM Unit test environment fallback
        }
    }

    public LiveData<List<CropHarvest>> getHarvests() {
        return harvestLiveData;
    }

    public void loadHarvests(String phone) {
        ApiClient.getInstance().getApi().getHarvests(phone)
                .enqueue(new Callback<List<CropHarvest>>() {
                    @Override
                    public void onResponse(Call<List<CropHarvest>> call, Response<List<CropHarvest>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            synchronized (CropHarvestRepository.this) {
                                harvestList.clear();
                                harvestList.addAll(response.body());
                                if (dbHelper != null) {
                                    for (CropHarvest ch : response.body()) {
                                        dbHelper.addHarvest(ch, phone);
                                    }
                                }
                            }
                            dispatchHarvests();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<CropHarvest>> call, Throwable t) {
                        Log.w(TAG, "Backend unreachable for harvest inventory: " + t.getMessage());
                    }
                });
    }

    public synchronized boolean addHarvest(CropHarvest harvest) {
        if (harvest == null) return false;
        if (harvest.getHarvestedQuintals() <= 0) return false;
        if (harvest.getSoldQuintals() < 0 || harvest.getSoldQuintals() > harvest.getHarvestedQuintals()) return false;

        harvestList.add(0, harvest);
        if (dbHelper != null) {
            dbHelper.addHarvest(harvest, harvest.getFarmerPhone() != null ? harvest.getFarmerPhone() : "9876543210");
        }
        dispatchHarvests();

        // Persist to MySQL
        ApiClient.getInstance().getApi().addHarvest(harvest)
                .enqueue(new Callback<CropHarvest>() {
                    @Override
                    public void onResponse(Call<CropHarvest> call, Response<CropHarvest> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Log.i(TAG, "Harvest saved to MySQL: " + response.body().getId());
                        }
                    }

                    @Override
                    public void onFailure(Call<CropHarvest> call, Throwable t) {
                        Log.w(TAG, "Failed to persist harvest to MySQL: " + t.getMessage());
                    }
                });

        return true;
    }

    public synchronized void removeHarvest(String id) {
        if (id == null) return;
        harvestList.removeIf(h -> h.getId().equals(id));
        if (dbHelper != null) {
            dbHelper.deleteHarvest(id);
        }
        dispatchHarvests();

        ApiClient.getInstance().getApi().deleteHarvest(id)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        Log.i(TAG, "Harvest deleted from MySQL: " + id);
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.w(TAG, "Failed to delete harvest on backend: " + t.getMessage());
                    }
                });
    }

    public synchronized boolean sellStock(String id, double quantitySold) {
        if (id == null || quantitySold <= 0) return false;
        CropHarvest target = null;
        for (CropHarvest h : harvestList) {
            if (h.getId().equals(id)) {
                if (quantitySold > h.getStockRemainingQuintals()) {
                    return false; // cannot sell more than remaining
                }
                h.setSoldQuintals(h.getSoldQuintals() + quantitySold);
                target = h;
                if (dbHelper != null) {
                    dbHelper.updateHarvestSold(id, quantitySold);
                }
                break;
            }
        }
        dispatchHarvests();

        if (target != null) {
            ApiClient.getInstance().getApi().addHarvest(target)
                    .enqueue(new Callback<CropHarvest>() {
                        @Override
                        public void onResponse(Call<CropHarvest> call, Response<CropHarvest> response) {
                            Log.i(TAG, "Stock sale synced to MySQL");
                        }

                        @Override
                        public void onFailure(Call<CropHarvest> call, Throwable t) {
                            Log.w(TAG, "Failed to sync stock sale to MySQL: " + t.getMessage());
                        }
                    });
        }
        return true;
    }
}
