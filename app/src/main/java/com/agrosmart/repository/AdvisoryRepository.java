package com.agrosmart.repository;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.agrosmart.database.AgroSmartDbHelper;
import com.agrosmart.models.SoilHealthRecord;
import com.agrosmart.network.ApiClient;
import com.agrosmart.network.AgroSmartApiService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Response;

public class AdvisoryRepository {

    private static AdvisoryRepository instance;
    private final AgroSmartDbHelper dbHelper;
    private final AgroSmartApiService apiService;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public interface SoilCallback {
        void onLoaded(List<SoilHealthRecord> list);
        void onError(String error);
    }

    public interface SingleSoilCallback {
        void onSuccess(SoilHealthRecord record);
        void onError(String error);
    }

    public interface GenericMapCallback {
        void onSuccess(Map<String, Object> data);
        void onError(String error);
    }

    public static synchronized AdvisoryRepository getInstance(Context context) {
        if (instance == null) {
            instance = new AdvisoryRepository(context.getApplicationContext());
        }
        return instance;
    }

    private AdvisoryRepository(Context context) {
        dbHelper = AgroSmartDbHelper.getInstance(context);
        apiService = ApiClient.getApiService(context);
    }

    public void getSoilRecords(String phone, SoilCallback callback) {
        executor.execute(() -> {
            List<SoilHealthRecord> local = dbHelper.getSoilHealthRecords(phone);
            mainHandler.post(() -> callback.onLoaded(local));

            // Sync with backend
            try {
                Call<List<SoilHealthRecord>> call = apiService.getSoilRecords(phone);
                Response<List<SoilHealthRecord>> resp = call.execute();
                if (resp.isSuccessful() && resp.body() != null) {
                    for (SoilHealthRecord r : resp.body()) {
                        dbHelper.insertSoilHealthRecord(r);
                    }
                    List<SoilHealthRecord> updated = dbHelper.getSoilHealthRecords(phone);
                    mainHandler.post(() -> callback.onLoaded(updated));
                }
            } catch (Exception ignored) {}
        });
    }

    public void saveSoilRecord(SoilHealthRecord record, SingleSoilCallback callback) {
        executor.execute(() -> {
            // Local calculation fallback if offline
            if (record.getAdvisoryNotes() == null || record.getAdvisoryNotes().trim().isEmpty()) {
                double ph = record.getPh() != null ? record.getPh() : 7.0;
                double oc = record.getOrganicCarbon() != null ? record.getOrganicCarbon() : 0.6;
                StringBuilder sb = new StringBuilder();
                if (ph < 6.5) sb.append("• Acidic Soil: Apply lime/dolomite (1.5 t/ha) before sowing.\n");
                else if (ph > 8.0) sb.append("• Alkaline Soil: Apply gypsum (2.5 t/ha) with green manuring.\n");
                else sb.append("• Optimal pH: Balanced nutrient assimilation.\n");
                if (oc < 0.5) sb.append("• Low Organic Matter: Apply 8-10 tons FYM/compost per acre.\n");
                record.setAdvisoryNotes(sb.toString().trim());
                record.setFertilityRating(ph < 6.5 ? "ACIDIC" : (ph > 8.0 ? "ALKALINE" : "BALANCED"));
            }

            dbHelper.insertSoilHealthRecord(record);
            mainHandler.post(() -> callback.onSuccess(record));

            // Push to backend
            try {
                Call<SoilHealthRecord> call = apiService.saveSoilRecord(record);
                Response<SoilHealthRecord> resp = call.execute();
                if (resp.isSuccessful() && resp.body() != null) {
                    dbHelper.insertSoilHealthRecord(resp.body());
                    mainHandler.post(() -> callback.onSuccess(resp.body()));
                }
            } catch (Exception ignored) {}
        });
    }

    public void getCropRecommendations(String zone, String soil, String irrigation, String season, GenericMapCallback callback) {
        executor.execute(() -> {
            try {
                Call<Map<String, Object>> call = apiService.getCropRecommendations(zone, soil, irrigation, season);
                Response<Map<String, Object>> resp = call.execute();
                if (resp.isSuccessful() && resp.body() != null) {
                    mainHandler.post(() -> callback.onSuccess(resp.body()));
                    return;
                }
            } catch (Exception ignored) {}

            // Offline Agronomic Decision Fallback
            Map<String, Object> fallback = Map.of(
                    "zone", zone,
                    "soil", soil,
                    "recommendations", List.of(
                            Map.of("cropName", "Soybean (Phule Sangam - KDS 726) + Tur Intercrop", "season", "Kharif", "duration", "105 days", "expectedYield", "24 Qtl/ha", "suitabilityScore", 95, "justification", "Recommended by MPKV Rahuri for high nodulation and rust resistance."),
                            Map.of("cropName", "Chickpea / Gram (Digvijay)", "season", "Rabi", "duration", "95 - 105 days", "expectedYield", "20 - 22 Qtl/ha", "suitabilityScore", 92, "justification", "Deep rooting depth utilizes subsoil moisture effectively.")
                    )
            );
            mainHandler.post(() -> callback.onSuccess(fallback));
        });
    }

    public void evaluateRotationPlan(Map<String, String> payload, GenericMapCallback callback) {
        executor.execute(() -> {
            try {
                Call<Map<String, Object>> call = apiService.evaluateRotationPlan(payload);
                Response<Map<String, Object>> resp = call.execute();
                if (resp.isSuccessful() && resp.body() != null) {
                    mainHandler.post(() -> callback.onSuccess(resp.body()));
                    return;
                }
            } catch (Exception ignored) {}

            String prev = payload.getOrDefault("previousCrop", "Cotton");
            String plan = payload.getOrDefault("plannedCrop", "Soybean");
            boolean highRisk = prev.equalsIgnoreCase(plan);

            Map<String, Object> fallback = Map.of(
                    "previousCrop", prev,
                    "plannedCrop", plan,
                    "isHighRisk", highRisk,
                    "alertTitle", highRisk ? "⚠️ Soil Exhaustion Risk" : "✅ Balanced Crop Rotation",
                    "advisoryMessage", highRisk ? "Consecutive mono-cropping multiplies soil-borne pests. Rotate with pulse crops." : "Alternating crops enhances soil structure and balances nutrient extraction.",
                    "soilHealthImpactScore", highRisk ? 35 : 90
            );
            mainHandler.post(() -> callback.onSuccess(fallback));
        });
    }
}
