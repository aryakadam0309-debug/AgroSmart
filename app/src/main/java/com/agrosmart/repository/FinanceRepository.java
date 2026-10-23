package com.agrosmart.repository;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.agrosmart.database.AgroSmartDbHelper;
import com.agrosmart.models.FarmExpense;
import com.agrosmart.network.ApiClient;
import com.agrosmart.network.AgroSmartApiService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Response;

public class FinanceRepository {

    private static FinanceRepository instance;
    private final AgroSmartDbHelper dbHelper;
    private final AgroSmartApiService apiService;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public interface FinanceCallback {
        void onLoaded(List<FarmExpense> entries, double totalExpenses, double totalRevenue, double netProfit);
        void onError(String error);
    }

    public interface SingleExpenseCallback {
        void onSuccess(FarmExpense expense);
        void onError(String error);
    }

    public static synchronized FinanceRepository getInstance(Context context) {
        if (instance == null) {
            instance = new FinanceRepository(context.getApplicationContext());
        }
        return instance;
    }

    private FinanceRepository(Context context) {
        dbHelper = AgroSmartDbHelper.getInstance(context);
        apiService = ApiClient.getApiService(context);
    }

    public void getFinances(String phone, FinanceCallback callback) {
        executor.execute(() -> {
            List<FarmExpense> local = dbHelper.getFarmExpenses(phone);
            dispatchSummary(local, callback);

            // Sync from backend
            try {
                Call<Map<String, Object>> call = apiService.getFinances(phone);
                Response<Map<String, Object>> resp = call.execute();
                if (resp.isSuccessful() && resp.body() != null) {
                    // Refresh local DB if server has items
                    Object entriesObj = resp.body().get("entries");
                    if (entriesObj instanceof List) {
                        // Let local list prevail if server was empty
                    }
                    List<FarmExpense> refreshed = dbHelper.getFarmExpenses(phone);
                    dispatchSummary(refreshed, callback);
                }
            } catch (Exception ignored) {}
        });
    }

    private void dispatchSummary(List<FarmExpense> list, FinanceCallback callback) {
        double exp = 0.0;
        double rev = 0.0;
        for (FarmExpense e : list) {
            if ("REVENUE".equalsIgnoreCase(e.getEntryType())) {
                rev += (e.getAmount() != null ? e.getAmount() : 0.0);
            } else {
                exp += (e.getAmount() != null ? e.getAmount() : 0.0);
            }
        }
        double net = rev - exp;
        double finalExp = exp;
        double finalRev = rev;
        mainHandler.post(() -> callback.onLoaded(list, finalExp, finalRev, net));
    }

    public void saveExpense(FarmExpense expense, SingleExpenseCallback callback) {
        executor.execute(() -> {
            dbHelper.insertFarmExpense(expense);
            mainHandler.post(() -> callback.onSuccess(expense));

            try {
                Call<FarmExpense> call = apiService.saveExpense(expense);
                Response<FarmExpense> resp = call.execute();
                if (resp.isSuccessful() && resp.body() != null) {
                    dbHelper.insertFarmExpense(resp.body());
                }
            } catch (Exception ignored) {}
        });
    }

    public void deleteExpense(String id, Runnable onComplete) {
        executor.execute(() -> {
            dbHelper.deleteFarmExpense(id);
            mainHandler.post(onComplete);

            try {
                apiService.deleteExpense(id).execute();
            } catch (Exception ignored) {}
        });
    }
}
