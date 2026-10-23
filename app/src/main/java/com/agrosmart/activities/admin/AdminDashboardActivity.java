package com.agrosmart.activities.admin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.agrosmart.activities.BaseActivity;
import com.agrosmart.activities.auth.LoginActivity;
import com.agrosmart.activities.farmer.CropGuideActivity;
import com.agrosmart.activities.farmer.DiseaseReportActivity;
import com.agrosmart.activities.farmer.NotificationsActivity;
import com.agrosmart.databinding.ActivityAdminDashboardBinding;
import com.agrosmart.utils.Constants;
import com.agrosmart.utils.SessionManager;
import com.agrosmart.viewmodels.AdminViewModel;

public class AdminDashboardActivity extends BaseActivity {

    private ActivityAdminDashboardBinding binding;
    private AdminViewModel adminViewModel;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);
        if (!Constants.ROLE_ADMIN.equals(sessionManager.getUserRole())) {
            Toast.makeText(this, "Access Denied: Administrator role required.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        adminViewModel = new ViewModelProvider(this).get(AdminViewModel.class);

        setupObservers();
        setupClickListeners();

        adminViewModel.fetchStats();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adminViewModel != null) {
            adminViewModel.fetchStats();
        }
    }

    private void setupObservers() {
        adminViewModel.getDashboardStats().observe(this, stats -> {
            if (stats != null) {
                binding.tvTotalFarmers.setText(String.format("%,d", stats.getTotalFarmers()));
                binding.tvTotalAdvisories.setText(String.format("%,d", stats.getTotalTractors()));
                binding.tvTotalFeedback.setText(String.format("%,d", stats.getTotalBookings()));
                binding.tvTotalOrders.setText(String.format("%,d", stats.getTotalOrders()));
            }
        });
    }

    private void setupClickListeners() {
        binding.btnLogout.setOnClickListener(v -> {
            sessionManager.logout();
            Intent intent = new Intent(AdminDashboardActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        binding.cardManageSchemes.setOnClickListener(v ->
                startActivity(new Intent(this, ManageSchemesActivity.class)));

        binding.cardManageNews.setOnClickListener(v ->
                startActivity(new Intent(this, ManageNewsActivity.class)));

        binding.cardManageCropPrices.setOnClickListener(v ->
                startActivity(new Intent(this, ManageCropPricesActivity.class)));

        binding.cardManageCropGuides.setOnClickListener(v ->
                startActivity(new Intent(this, CropGuideActivity.class)));

        binding.cardManageFeedback.setOnClickListener(v ->
                startActivity(new Intent(this, AdminFeedbackActivity.class)));

        binding.cardTriageDisease.setOnClickListener(v ->
                startActivity(new Intent(this, DiseaseReportActivity.class)));

        binding.cardBroadcastAlerts.setOnClickListener(v ->
                startActivity(new Intent(this, NotificationsActivity.class)));
    }
}
