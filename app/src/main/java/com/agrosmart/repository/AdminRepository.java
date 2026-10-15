package com.agrosmart.repository;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.agrosmart.database.AgroSmartDbHelper;
import com.agrosmart.models.AdminDashboardStats;

public class AdminRepository {

    public interface StatsCallback {
        void onSuccess(AdminDashboardStats stats);
        void onError(String message);
    }

    public void getDashboardStats(StatsCallback callback) {
        getDashboardStats(null, callback);
    }

    public void getDashboardStats(Context context, StatsCallback callback) {
        if (context != null) {
            AdminDashboardStats stats = AgroSmartDbHelper.getInstance(context).getAdminDashboardStats();
            callback.onSuccess(stats);
        } else {
            AdminDashboardStats stats = new AdminDashboardStats(
                    1248,  // Total Farmers
                    84,    // Total Tractors
                    342,   // Total Bookings
                    120,   // Total Marketplace Products
                    530,   // Total Orders
                    485200.0 // Revenue
            );
            callback.onSuccess(stats);
        }
    }
}
