package com.agrosmart.repository;

import android.content.Context;
import com.agrosmart.database.AgroSmartDbHelper;
import com.agrosmart.models.AdminDashboardStats;
import com.agrosmart.network.ApiClient;
import com.agrosmart.network.AgroSmartApiService;

import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminRepository {

    private final AgroSmartApiService apiService;

    public AdminRepository() {
        this.apiService = ApiClient.getBackendClient().create(AgroSmartApiService.class);
    }

    public interface StatsCallback {
        void onSuccess(AdminDashboardStats stats);
        void onError(String message);
    }

    public void getDashboardStats(StatsCallback callback) {
        getDashboardStats(null, callback);
    }

    public void getDashboardStats(Context context, StatsCallback callback) {
        // Query Spring Boot backend (MySQL database)
        apiService.getAdminStats().enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> body = response.body();
                    int totalFarmers = getIntValue(body.get("totalFarmers"));
                    int totalTractors = getIntValue(body.get("totalTractors"));
                    int totalBookings = getIntValue(body.get("totalBookings"));
                    int totalOrders = getIntValue(body.get("totalOrders"));
                    int totalProducts = getIntValue(body.get("totalProducts"));
                    double totalRevenue = getDoubleValue(body.get("totalRevenue"));

                    AdminDashboardStats stats = new AdminDashboardStats(
                            totalFarmers, totalTractors, totalBookings, totalProducts, totalOrders, totalRevenue
                    );
                    callback.onSuccess(stats);
                } else {
                    fallbackToLocalDb(context, callback);
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                fallbackToLocalDb(context, callback);
            }
        });
    }

    private void fallbackToLocalDb(Context context, StatsCallback callback) {
        if (context != null) {
            AdminDashboardStats stats = AgroSmartDbHelper.getInstance(context).getAdminDashboardStats();
            callback.onSuccess(stats);
        } else {
            callback.onError("Failed to fetch admin stats from database");
        }
    }

    private int getIntValue(Object obj) {
        if (obj instanceof Number) {
            return ((Number) obj).intValue();
        }
        return 0;
    }

    private double getDoubleValue(Object obj) {
        if (obj instanceof Number) {
            return ((Number) obj).doubleValue();
        }
        return 0.0;
    }
}
