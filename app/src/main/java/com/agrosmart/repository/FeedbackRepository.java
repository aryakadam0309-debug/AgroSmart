package com.agrosmart.repository;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.agrosmart.database.AgroSmartDbHelper;
import com.agrosmart.models.FarmerFeedback;
import com.agrosmart.network.ApiClient;
import com.agrosmart.network.AgroSmartApiService;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Response;

public class FeedbackRepository {

    private static FeedbackRepository instance;
    private final AgroSmartDbHelper dbHelper;
    private final AgroSmartApiService apiService;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public interface FeedbackCallback {
        void onLoaded(List<FarmerFeedback> list);
        void onError(String error);
    }

    public interface SingleFeedbackCallback {
        void onSuccess(FarmerFeedback feedback);
        void onError(String error);
    }

    public static synchronized FeedbackRepository getInstance(Context context) {
        if (instance == null) {
            instance = new FeedbackRepository(context.getApplicationContext());
        }
        return instance;
    }

    private FeedbackRepository(Context context) {
        dbHelper = AgroSmartDbHelper.getInstance(context);
        apiService = ApiClient.getApiService(context);
    }

    public void submitFeedback(FarmerFeedback feedback, SingleFeedbackCallback callback) {
        executor.execute(() -> {
            dbHelper.insertFeedback(feedback);
            mainHandler.post(() -> callback.onSuccess(feedback));

            try {
                Call<FarmerFeedback> call = apiService.submitFeedback(feedback);
                Response<FarmerFeedback> resp = call.execute();
                if (resp.isSuccessful() && resp.body() != null) {
                    dbHelper.insertFeedback(resp.body());
                }
            } catch (Exception ignored) {}
        });
    }

    public void getAllFeedback(FeedbackCallback callback) {
        executor.execute(() -> {
            List<FarmerFeedback> local = dbHelper.getAllFarmerFeedback();
            mainHandler.post(() -> callback.onLoaded(local));

            try {
                Call<List<FarmerFeedback>> call = apiService.getAllFeedback();
                Response<List<FarmerFeedback>> resp = call.execute();
                if (resp.isSuccessful() && resp.body() != null) {
                    for (FarmerFeedback f : resp.body()) {
                        dbHelper.insertFeedback(f);
                    }
                    List<FarmerFeedback> updated = dbHelper.getAllFarmerFeedback();
                    mainHandler.post(() -> callback.onLoaded(updated));
                }
            } catch (Exception ignored) {}
        });
    }
}
