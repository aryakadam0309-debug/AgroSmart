package com.agrosmart.activities.farmer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.agrosmart.R;
import com.agrosmart.activities.BaseActivity;
import com.agrosmart.adapters.CropAlertAdapter;
import com.agrosmart.adapters.MyCropReportAdapter;
import com.agrosmart.databinding.ActivityCropWatchBinding;
import com.agrosmart.models.CropAlert;
import com.agrosmart.models.DiseaseReport;
import com.agrosmart.repository.DiseaseReportRepository;
import com.agrosmart.utils.SessionManager;
import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CropWatchActivity extends BaseActivity {

    private ActivityCropWatchBinding binding;
    private DiseaseReportRepository repository;
    private SessionManager sessionManager;
    private MyCropReportAdapter myReportAdapter;
    private CropAlertAdapter alertAdapter;

    private Uri selectedPhotoUri = null;
    private File selectedPhotoFile = null;
    private String uploadedImageUrl = null;

    private ActivityResultLauncher<String> photoPickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCropWatchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);
        repository = DiseaseReportRepository.getInstance(this);

        setupToolbar();
        setupTabs();
        setupSpinners();
        setupLocationDefaults();
        setupPhotoPicker();
        setupRecyclerViews();

        binding.btnSubmitProblemReport.setOnClickListener(v -> submitProblemReport());

        // Default tab: Report Form
        showTab(0);
        loadLocalAlerts();
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupTabs() {
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                showTab(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void showTab(int position) {
        binding.scrollReportForm.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
        binding.layoutMyReports.setVisibility(position == 1 ? View.VISIBLE : View.GONE);
        binding.layoutLocalAlerts.setVisibility(position == 2 ? View.VISIBLE : View.GONE);

        if (position == 1) {
            loadMyReports();
        } else if (position == 2) {
            loadLocalAlerts();
        }
    }

    private void setupSpinners() {
        // Crops Spinner
        String[] crops = {
                "Soybean (सोयाबीन)",
                "Cotton (कापूस / कपास)",
                "Sugarcane (ऊस / गन्ना)",
                "Wheat (गहू / गेहूं)",
                "Tomato (टोमॅटो / टमाटर)",
                "Onion (कांदा / प्याज)",
                "Rice (भात / धान)",
                "Maize (मका / मक्का)",
                "Gram / Chickpea (हरभरा / चना)",
                "Grapes (द्राक्षे / अंगूर)",
                "Pomegranate (डाळिंब / अनार)",
                "Groundnut (भुईमूग / मूंगफली)",
                "Other Crop (इतर पीक)"
        };
        ArrayAdapter<String> cropAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, crops);
        binding.spnCrops.setAdapter(cropAdapter);

        // Problem Categories
        String[] categories = {
                "Unusual Leaf Spots (पानावरील ठिपके / पत्तियों पर धब्बे)",
                "Wilting & Drying (रोप कोमेजणे / पौधा सूखना)",
                "Pest Sightings (कीड / कीड़ों का प्रकोप)",
                "Leaf / Stem Damage (पाने किंवा खोडाचे नुकसान)",
                "Fruit / Pod Rot (फळ किंवा शेंगा कुजणे)",
                "Stunted Growth (रोपाची खुंटलेली वाढ)",
                "Root Issues / Damping Off (मुळांची कुज / जड़ सड़न)",
                "Other Symptoms (इतर लक्षणे)"
        };
        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        binding.spnProblemCategory.setAdapter(catAdapter);

        // Growth Stages
        String[] stages = {
                "Seedling / Germination (उगवण / अंकुरण)",
                "Vegetative Growth (शाकीय वाढ / वानस्पतिक वृद्धि)",
                "Flowering (फुलोरा / फूल आना)",
                "Fruiting / Pod Formation (फळधारणा / फली बनना)",
                "Maturity / Harvest (पक्वता / कटाई)"
        };
        ArrayAdapter<String> stageAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, stages);
        binding.spnGrowthStage.setAdapter(stageAdapter);
    }

    private void setupLocationDefaults() {
        String savedLocation = sessionManager.getUserLocation();
        String village = "Baramati";
        String district = "Pune";

        if (savedLocation != null && !savedLocation.trim().isEmpty()) {
            String[] parts = savedLocation.split(",");
            if (parts.length > 0) village = parts[0].trim();
            if (parts.length > 1) district = parts[1].trim();
        }

        binding.etDistrict.setText(district);
        binding.etVillage.setText(village);
    }

    private void setupPhotoPicker() {
        photoPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        selectedPhotoUri = uri;
                        try {
                            // Copy to cache file for upload
                            InputStream is = getContentResolver().openInputStream(uri);
                            File tempFile = new File(getCacheDir(), "crop_report_" + System.currentTimeMillis() + ".jpg");
                            FileOutputStream fos = new FileOutputStream(tempFile);
                            byte[] buffer = new byte[4096];
                            int read;
                            while ((read = is.read(buffer)) != -1) {
                                fos.write(buffer, 0, read);
                            }
                            fos.close();
                            is.close();

                            selectedPhotoFile = tempFile;

                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                            binding.imgPhotoPreview.setImageBitmap(bitmap);
                            binding.imgPhotoPreview.setVisibility(View.VISIBLE);
                            binding.tvPhotoFileName.setText(tempFile.getName());
                            binding.tvPhotoFileName.setVisibility(View.VISIBLE);
                            binding.btnRemovePhoto.setVisibility(View.VISIBLE);
                        } catch (Exception e) {
                            Toast.makeText(this, "Could not load photo: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        binding.btnPickPhoto.setOnClickListener(v -> photoPickerLauncher.launch("image/*"));

        binding.btnRemovePhoto.setOnClickListener(v -> {
            selectedPhotoUri = null;
            selectedPhotoFile = null;
            uploadedImageUrl = null;
            binding.imgPhotoPreview.setImageDrawable(null);
            binding.imgPhotoPreview.setVisibility(View.GONE);
            binding.tvPhotoFileName.setVisibility(View.GONE);
            binding.btnRemovePhoto.setVisibility(View.GONE);
        });
    }

    private void setupRecyclerViews() {
        // My Reports
        myReportAdapter = new MyCropReportAdapter(report -> {
            if (report.getAdminGuidance() != null && !report.getAdminGuidance().isEmpty()) {
                new AlertDialog.Builder(this)
                        .setTitle("🔬 Official Agronomy Guidance")
                        .setMessage(report.getAdminGuidance() + "\n\nSource: " +
                                (report.getAdminSource() != null ? report.getAdminSource() : "Official Extension Advisory"))
                        .setPositiveButton("OK", null)
                        .show();
            }
        });
        binding.rvMyReports.setLayoutManager(new LinearLayoutManager(this));
        binding.rvMyReports.setAdapter(myReportAdapter);

        // Community Alerts
        alertAdapter = new CropAlertAdapter(alert -> {
            new AlertDialog.Builder(this)
                    .setTitle(alert.getAlertTitle())
                    .setMessage(alert.getSummary() + "\n\n" +
                            "Recommended Action:\n" + alert.getRecommendedActions() + "\n\n" +
                            "Authority: " + alert.getAuthority())
                    .setPositiveButton("Close", null)
                    .show();
        });
        binding.rvLocalAlerts.setLayoutManager(new LinearLayoutManager(this));
        binding.rvLocalAlerts.setAdapter(alertAdapter);
    }

    private void submitProblemReport() {
        String cropRaw = binding.spnCrops.getSelectedItem() != null ?
                binding.spnCrops.getSelectedItem().toString() : "Soybean";
        // Clean crop name (extract english title before parenthesis)
        String cropName = cropRaw.contains("(") ? cropRaw.substring(0, cropRaw.indexOf("(")).trim() : cropRaw.trim();

        String categoryRaw = binding.spnProblemCategory.getSelectedItem() != null ?
                binding.spnProblemCategory.getSelectedItem().toString() : "Unusual Leaf Spots";
        String problemCategory = categoryRaw.contains("(") ? categoryRaw.substring(0, categoryRaw.indexOf("(")).trim() : categoryRaw.trim();

        String stageRaw = binding.spnGrowthStage.getSelectedItem() != null ?
                binding.spnGrowthStage.getSelectedItem().toString() : "Vegetative";
        String growthStage = stageRaw.contains("(") ? stageRaw.substring(0, stageRaw.indexOf("(")).trim() : stageRaw.trim();

        String district = binding.etDistrict.getText() != null ? binding.etDistrict.getText().toString().trim() : "";
        String village = binding.etVillage.getText() != null ? binding.etVillage.getText().toString().trim() : "";
        String description = binding.etDescription.getText() != null ? binding.etDescription.getText().toString().trim() : "";

        if (district.isEmpty()) {
            binding.etDistrict.setError(getString(R.string.error_district_required));
            binding.etDistrict.requestFocus();
            return;
        }

        // Prevent accidental duplicate submission
        binding.btnSubmitProblemReport.setEnabled(false);
        binding.progressSubmit.setVisibility(View.VISIBLE);

        // If photo attached, attempt upload; otherwise submit directly
        if (selectedPhotoFile != null) {
            repository.uploadCropImage(selectedPhotoFile, new DiseaseReportRepository.UploadCallback() {
                @Override
                public void onSuccess(String imageUrl) {
                    uploadedImageUrl = imageUrl;
                    completeReportSubmission(cropName, problemCategory, growthStage, district, village, description, uploadedImageUrl);
                }

                @Override
                public void onError(String error) {
                    // Proceed even if photo upload fails to avoid blocking the report
                    completeReportSubmission(cropName, problemCategory, growthStage, district, village, description, null);
                }
            });
        } else {
            completeReportSubmission(cropName, problemCategory, growthStage, district, village, description, null);
        }
    }

    private void completeReportSubmission(String cropName, String category, String stage, String district, String village, String desc, String photoUrl) {
        String reportId = "CW-REP-" + System.currentTimeMillis();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a"));

        DiseaseReport report = new DiseaseReport();
        report.setId(reportId);
        report.setFarmerPhone(sessionManager.getUserPhone());
        report.setFarmerName(sessionManager.getUserName() != null ? sessionManager.getUserName() : "Farmer");
        report.setCropName(cropName);
        report.setProblemCategory(category);
        report.setGrowthStage(stage);
        report.setDistrict(district);
        report.setVillage(village.isEmpty() ? "Local Farm" : village);
        report.setSymptoms(!desc.isEmpty() ? desc : category + " observed in " + cropName);
        report.setImageUrl(photoUrl);
        report.setReportedAt(timestamp);
        report.setStatus("PENDING_REVIEW");

        repository.submitReport(report, new DiseaseReportRepository.SingleReportCallback() {
            @Override
            public void onSuccess(DiseaseReport saved) {
                binding.progressSubmit.setVisibility(View.GONE);
                binding.btnSubmitProblemReport.setEnabled(true);

                // Show confirmation dialog with report ID
                new AlertDialog.Builder(CropWatchActivity.this)
                        .setTitle("✅ " + getString(R.string.crop_report_submitted_title))
                        .setMessage(getString(R.string.crop_report_submitted_msg) + "\n\n" +
                                getString(R.string.label_report_id) + ": " + saved.getId() + "\n" +
                                getString(R.string.label_crop) + ": " + saved.getCropName() + "\n" +
                                getString(R.string.label_status) + ": Pending Review\n\n" +
                                getString(R.string.crop_report_clustering_notice))
                        .setPositiveButton(getString(R.string.btn_view_my_reports), (dialog, which) -> {
                            // Switch to My Reports tab
                            TabLayout.Tab tab = binding.tabLayout.getTabAt(1);
                            if (tab != null) tab.select();
                        })
                        .setNegativeButton(getString(R.string.btn_ok), null)
                        .setCancelable(false)
                        .show();

                // Clear input form
                binding.etDescription.setText("");
                binding.btnRemovePhoto.performClick();
            }

            @Override
            public void onError(String error) {
                binding.progressSubmit.setVisibility(View.GONE);
                binding.btnSubmitProblemReport.setEnabled(true);
                Toast.makeText(CropWatchActivity.this, "Submission saved locally. Will sync when connected.", Toast.LENGTH_LONG).show();

                TabLayout.Tab tab = binding.tabLayout.getTabAt(1);
                if (tab != null) tab.select();
            }
        });
    }

    private void loadMyReports() {
        String phone = sessionManager.getUserPhone();
        repository.getReports(phone, new DiseaseReportRepository.ReportCallback() {
            @Override
            public void onLoaded(List<DiseaseReport> list) {
                myReportAdapter.setItems(list);
                if (list == null || list.isEmpty()) {
                    binding.layoutEmptyMyReports.setVisibility(View.VISIBLE);
                    binding.rvMyReports.setVisibility(View.GONE);
                } else {
                    binding.layoutEmptyMyReports.setVisibility(View.GONE);
                    binding.rvMyReports.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(String error) {
                binding.layoutEmptyMyReports.setVisibility(View.VISIBLE);
            }
        });
    }

    private void loadLocalAlerts() {
        String district = binding.etDistrict.getText() != null ? binding.etDistrict.getText().toString().trim() : null;
        repository.getCropAlerts(district, "ACTIVE", new DiseaseReportRepository.AlertsCallback() {
            @Override
            public void onLoaded(List<CropAlert> list) {
                alertAdapter.setItems(list);
                if (list == null || list.isEmpty()) {
                    binding.layoutEmptyAlerts.setVisibility(View.VISIBLE);
                    binding.rvLocalAlerts.setVisibility(View.GONE);
                } else {
                    binding.layoutEmptyAlerts.setVisibility(View.GONE);
                    binding.rvLocalAlerts.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(String error) {
                binding.layoutEmptyAlerts.setVisibility(View.VISIBLE);
            }
        });
    }
}
