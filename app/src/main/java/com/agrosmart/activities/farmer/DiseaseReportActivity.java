package com.agrosmart.activities.farmer;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.agrosmart.activities.BaseActivity;
import com.agrosmart.adapters.DiseaseReportAdapter;
import com.agrosmart.databinding.ActivityDiseaseReportBinding;
import com.agrosmart.models.DiseaseReport;
import com.agrosmart.repository.DiseaseReportRepository;
import com.agrosmart.utils.SessionManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DiseaseReportActivity extends BaseActivity {

    private ActivityDiseaseReportBinding binding;
    private DiseaseReportRepository repository;
    private SessionManager sessionManager;
    private DiseaseReportAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDiseaseReportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);
        repository = DiseaseReportRepository.getInstance(this);

        binding.toolbar.setNavigationOnClickListener(v -> finish());

        // Pre-fill location
        binding.etVillage.setText(sessionManager.getUserLocation() != null ? sessionManager.getUserLocation() : "Baramati");
        binding.etDistrict.setText("Pune");

        adapter = new DiseaseReportAdapter(item -> {
            if (item.getAdminAdvisory() != null && !item.getAdminAdvisory().isEmpty()) {
                Toast.makeText(this, "Officer Advisory: " + item.getAdminAdvisory(), Toast.LENGTH_LONG).show();
            }
        });

        binding.rvDiseaseReports.setLayoutManager(new LinearLayoutManager(this));
        binding.rvDiseaseReports.setAdapter(adapter);

        binding.btnSubmitReport.setOnClickListener(v -> submitReport());

        loadMyReports();
    }

    private void loadMyReports() {
        String phone = sessionManager.getUserPhone();
        repository.getReports(phone, new DiseaseReportRepository.ReportCallback() {
            @Override
            public void onLoaded(List<DiseaseReport> list) {
                adapter.setItems(list);
                if (list == null || list.isEmpty()) {
                    binding.tvEmptyReports.setVisibility(View.VISIBLE);
                    binding.rvDiseaseReports.setVisibility(View.GONE);
                } else {
                    binding.tvEmptyReports.setVisibility(View.GONE);
                    binding.rvDiseaseReports.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(String error) {
                binding.tvEmptyReports.setVisibility(View.VISIBLE);
            }
        });
    }

    private void submitReport() {
        String cropName = binding.etReportCropName.getText().toString().trim();
        String symptoms = binding.etSymptoms.getText().toString().trim();
        String village = binding.etVillage.getText().toString().trim();
        String district = binding.etDistrict.getText().toString().trim();
        String plantPart = binding.spnPlantPart.getSelectedItem() != null ? binding.spnPlantPart.getSelectedItem().toString() : "Leaves";
        String severity = binding.spnSeverity.getSelectedItem() != null ? binding.spnSeverity.getSelectedItem().toString() : "Moderate";

        if (cropName.isEmpty() || symptoms.isEmpty()) {
            Toast.makeText(this, "Crop name and observed symptoms are required", Toast.LENGTH_SHORT).show();
            return;
        }

        DiseaseReport report = new DiseaseReport();
        report.setId("REP-" + System.currentTimeMillis());
        report.setFarmerPhone(sessionManager.getUserPhone());
        report.setFarmerName(sessionManager.getUserName());
        report.setCropName(cropName);
        report.setAffectedPart(plantPart);
        report.setSeverity(severity);
        report.setSymptoms(symptoms);
        report.setVillage(village.isEmpty() ? "Local Farm" : village);
        report.setDistrict(district.isEmpty() ? "District" : district);
        report.setReportedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")));
        report.setStatus("REPORTED");

        repository.submitReport(report, new DiseaseReportRepository.SingleReportCallback() {
            @Override
            public void onSuccess(DiseaseReport submitted) {
                Toast.makeText(DiseaseReportActivity.this, "Outbreak report submitted for agronomic verification!", Toast.LENGTH_SHORT).show();
                binding.etReportCropName.setText("");
                binding.etSymptoms.setText("");
                loadMyReports();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(DiseaseReportActivity.this, "Failed to submit: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
