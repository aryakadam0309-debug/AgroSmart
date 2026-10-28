package com.agrosmart.repository;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.agrosmart.database.AgroSmartDbHelper;
import com.agrosmart.models.CropAlert;
import com.agrosmart.models.CropCluster;
import com.agrosmart.models.DiseaseReport;
import com.agrosmart.network.ApiClient;
import com.agrosmart.network.AgroSmartApiService;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
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

    public interface AlertsCallback {
        void onLoaded(List<CropAlert> list);
        void onError(String error);
    }

    public interface SingleAlertCallback {
        void onSuccess(CropAlert alert);
        void onError(String error);
    }

    public interface ClustersCallback {
        void onLoaded(List<CropCluster> list);
        void onError(String error);
    }

    public interface UploadCallback {
        void onSuccess(String imageUrl);
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

    public void getAllReportsForAdmin(ReportCallback callback) {
        executor.execute(() -> {
            List<DiseaseReport> local = dbHelper.getDiseaseReports(null);
            mainHandler.post(() -> callback.onLoaded(local));

            try {
                Call<List<DiseaseReport>> call = apiService.getDiseaseReports(null, null, null);
                Response<List<DiseaseReport>> resp = call.execute();
                if (resp.isSuccessful() && resp.body() != null) {
                    for (DiseaseReport r : resp.body()) {
                        dbHelper.insertDiseaseReport(r);
                    }
                    List<DiseaseReport> updated = dbHelper.getDiseaseReports(null);
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

    public void reviewReport(String id, String newStatus, String reviewNote, String guidance, String source, String adminPhone, SingleReportCallback callback) {
        executor.execute(() -> {
            Map<String, String> payload = new HashMap<>();
            payload.put("status", newStatus);
            if (reviewNote != null) payload.put("reviewNote", reviewNote);
            if (guidance != null) payload.put("adminGuidance", guidance);
            if (source != null) payload.put("adminSource", source);
            if (adminPhone != null) payload.put("reviewedBy", adminPhone);

            try {
                Call<DiseaseReport> call = apiService.reviewDiseaseReport(id, payload);
                Response<DiseaseReport> resp = call.execute();
                if (resp.isSuccessful() && resp.body() != null) {
                    dbHelper.insertDiseaseReport(resp.body());
                    mainHandler.post(() -> callback.onSuccess(resp.body()));
                    return;
                }
            } catch (Exception ignored) {}

            // Local fallback
            List<DiseaseReport> list = dbHelper.getDiseaseReports(null);
            for (DiseaseReport r : list) {
                if (id.equals(r.getId())) {
                    r.setStatus(newStatus);
                    r.setReviewNote(reviewNote);
                    r.setAdminGuidance(guidance);
                    r.setAdminSource(source);
                    dbHelper.insertDiseaseReport(r);
                    mainHandler.post(() -> callback.onSuccess(r));
                    return;
                }
            }
            mainHandler.post(() -> callback.onError("Failed to update status on server"));
        });
    }

    public void updateReportStatus(String id, String newStatus, String advisory, SingleReportCallback callback) {
        reviewReport(id, newStatus, null, advisory, "Agronomy Advisory", null, callback);
    }

    // ==========================================
    // CROP CLUSTERS
    // ==========================================

    public void getCropClusters(ClustersCallback callback) {
        executor.execute(() -> {
            try {
                Call<List<CropCluster>> call = apiService.getCropClusters();
                Response<List<CropCluster>> resp = call.execute();
                if (resp.isSuccessful() && resp.body() != null) {
                    mainHandler.post(() -> callback.onLoaded(resp.body()));
                    return;
                }
            } catch (Exception ignored) {}
            mainHandler.post(() -> callback.onError("Unable to connect to clustering service"));
        });
    }

    // ==========================================
    // CROP ALERTS
    // ==========================================

    public void getCropAlerts(String district, String status, AlertsCallback callback) {
        executor.execute(() -> {
            List<CropAlert> local = dbHelper.getCropAlerts(district, status);
            mainHandler.post(() -> callback.onLoaded(local));

            try {
                Call<List<CropAlert>> call = apiService.getCropAlerts(district, status);
                Response<List<CropAlert>> resp = call.execute();
                if (resp.isSuccessful() && resp.body() != null) {
                    for (CropAlert a : resp.body()) {
                        dbHelper.insertCropAlert(a);
                    }
                    List<CropAlert> updated = dbHelper.getCropAlerts(district, status);
                    mainHandler.post(() -> callback.onLoaded(updated));
                }
            } catch (Exception ignored) {}
        });
    }

    public void publishCropAlert(CropAlert alert, SingleAlertCallback callback) {
        executor.execute(() -> {
            dbHelper.insertCropAlert(alert);
            mainHandler.post(() -> callback.onSuccess(alert));

            try {
                Call<CropAlert> call = apiService.publishCropAlert(alert);
                Response<CropAlert> resp = call.execute();
                if (resp.isSuccessful() && resp.body() != null) {
                    dbHelper.insertCropAlert(resp.body());
                }
            } catch (Exception ignored) {}
        });
    }

    public void updateAlertStatus(String id, String status, SingleAlertCallback callback) {
        executor.execute(() -> {
            Map<String, String> payload = new HashMap<>();
            payload.put("status", status);

            try {
                Call<CropAlert> call = apiService.updateAlertStatus(id, payload);
                Response<CropAlert> resp = call.execute();
                if (resp.isSuccessful() && resp.body() != null) {
                    dbHelper.insertCropAlert(resp.body());
                    mainHandler.post(() -> callback.onSuccess(resp.body()));
                    return;
                }
            } catch (Exception ignored) {}

            List<CropAlert> list = dbHelper.getCropAlerts(null, null);
            for (CropAlert a : list) {
                if (id.equals(a.getId())) {
                    a.setStatus(status);
                    dbHelper.insertCropAlert(a);
                    mainHandler.post(() -> callback.onSuccess(a));
                    return;
                }
            }
            mainHandler.post(() -> callback.onError("Failed to update alert"));
        });
    }

    // ==========================================
    // SAFE IMAGE UPLOAD
    // ==========================================

    public void uploadCropImage(File file, UploadCallback callback) {
        executor.execute(() -> {
            try {
                RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
                MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), reqFile);

                Call<Map<String, Object>> call = apiService.uploadCropImage(body);
                Response<Map<String, Object>> resp = call.execute();
                if (resp.isSuccessful() && resp.body() != null && resp.body().containsKey("imageUrl")) {
                    String url = (String) resp.body().get("imageUrl");
                    mainHandler.post(() -> callback.onSuccess(url));
                    return;
                }
            } catch (Exception ignored) {}
            mainHandler.post(() -> callback.onError("Failed to upload image to server"));
        });
    }
}
