package com.agrosmart.repository;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.agrosmart.database.AgroSmartDbHelper;
import com.agrosmart.models.DiseaseReport;
import com.agrosmart.network.ApiClient;
import com.agrosmart.network.AgroSmartApiService;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Response;

public class DiseaseReportRepository {

    private static DiseaseReportRepository instance;
    private final AgroSmartDbHelper dbHelper;
    private final AgroSmartApiService apiService;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public interface ReportCallback {
        void onLoaded(List<DiseaseReport> list);
        void onError(String error);
    }

    public interface SingleReportCallback {
        void onSuccess(DiseaseReport report);
        void onError(String error);
    }

    public static synchronized DiseaseReportRepository getInstance(Context context) {
        if (instance == null) {
            instance = new DiseaseReportRepository(context.getApplicationContext());
        }
        return instance;
    }

    private DiseaseReportRepository(Context context) {
        dbHelper = AgroSmartDbHelper.getInstance(context);
        apiService = ApiClient.getApiService(context);
    }

    public void getReports(String phone, ReportCallback callback) {
        executor.execute(() -> {
            List<DiseaseReport> local = dbHelper.getDiseaseReports(phone);
            mainHandler.post(() -> callback.onLoaded(local));

            try {
                Call<List<DiseaseReport>> call = apiService.getDiseaseReports(phone, null, null);
                Response<List<DiseaseReport>> resp = call.execute();
                if (resp.isSuccessful() && resp.body() != null) {
                    for (DiseaseReport r : resp.body()) {
                        dbHelper.insertDiseaseReport(r);
                    }
                    List<DiseaseReport> updated = dbHelper.getDiseaseReports(phone);
                    mainHandler.post(() -> callback.onLoaded(updated));
                }
            } catch (Exception ignored) {}
        });
    }

    public void submitReport(DiseaseReport report, SingleReportCallback callback) {
        executor.execute(() -> {
            dbHelper.insertDiseaseReport(report);
            mainHandler.post(() -> callback.onSuccess(report));

            try {
                Call<DiseaseReport> call = apiService.submitDiseaseReport(report);
                Response<DiseaseReport> resp = call.execute();
                if (resp.isSuccessful() && resp.body() != null) {
                    dbHelper.insertDiseaseReport(resp.body());
                }
            } catch (Exception ignored) {}
        });
    }

    public void updateReportStatus(String id, String newStatus, String advisory, SingleReportCallback callback) {
        executor.execute(() -> {
            try {
                Call<DiseaseReport> call = apiService.updateDiseaseReportStatus(id, Map.of("status", newStatus, "adminAdvisory", advisory != null ? advisory : ""));
                Response<DiseaseReport> resp = call.execute();
                if (resp.isSuccessful() && resp.body() != null) {
                    dbHelper.insertDiseaseReport(resp.body());
                    mainHandler.post(() -> callback.onSuccess(resp.body()));
                    return;
                }
            } catch (Exception ignored) {}
            mainHandler.post(() -> callback.onError("Failed to update status on server"));
        });
    }
}
