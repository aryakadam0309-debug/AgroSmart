package com.agrosmart.activities.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.agrosmart.R;
import com.agrosmart.activities.BaseActivity;
import com.agrosmart.adapters.AdminCropReportAdapter;
import com.agrosmart.adapters.CropAlertAdapter;
import com.agrosmart.adapters.CropClusterAdapter;
import com.agrosmart.databinding.ActivityAdminCropWatchBinding;
import com.agrosmart.models.CropAlert;
import com.agrosmart.models.CropCluster;
import com.agrosmart.models.DiseaseReport;
import com.agrosmart.repository.DiseaseReportRepository;
import com.agrosmart.utils.Constants;
import com.agrosmart.utils.SessionManager;
import com.google.android.material.tabs.TabLayout;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AdminCropWatchActivity extends BaseActivity {

    private ActivityAdminCropWatchBinding binding;
    private DiseaseReportRepository repository;
    private SessionManager sessionManager;

    private AdminCropReportAdapter reportAdapter;
    private CropClusterAdapter clusterAdapter;
    private CropAlertAdapter alertAdapter;

    private List<DiseaseReport> allReportsList = new ArrayList<>();
    private String currentStatusFilter = "ALL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminCropWatchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);
        if (!Constants.ROLE_ADMIN.equals(sessionManager.getUserRole())) {
            Toast.makeText(this, "Access Denied: Administrator role required.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        repository = DiseaseReportRepository.getInstance(this);

        setupToolbar();
        setupTabs();
        setupFilterChips();
        setupRecyclerViews();

        binding.btnCreateNewAlert.setOnClickListener(v -> showCreateAlertDialog(null));

        showTab(0);
        loadReports();
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupTabs() {
        binding.adminTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
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
        binding.layoutAdminReports.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
        binding.layoutAdminClusters.setVisibility(position == 1 ? View.VISIBLE : View.GONE);
        binding.layoutAdminAlerts.setVisibility(position == 2 ? View.VISIBLE : View.GONE);

        if (position == 0) {
            loadReports();
        } else if (position == 1) {
            loadClusters();
        } else if (position == 2) {
            loadAlerts();
        }
    }

    private void setupFilterChips() {
        binding.chipGroupStatus.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int id = checkedIds.get(0);
            if (id == R.id.chipFilterAll) {
                currentStatusFilter = "ALL";
            } else if (id == R.id.chipFilterPending) {
                currentStatusFilter = "PENDING_REVIEW";
            } else if (id == R.id.chipFilterVerified) {
                currentStatusFilter = "VERIFIED";
            } else if (id == R.id.chipFilterResolved) {
                currentStatusFilter = "RESOLVED";
            }
            filterReports();
        });
    }

    private void setupRecyclerViews() {
        reportAdapter = new AdminCropReportAdapter(this::showTriageDialog);
        binding.rvAdminReports.setLayoutManager(new LinearLayoutManager(this));
        binding.rvAdminReports.setAdapter(reportAdapter);

        clusterAdapter = new CropClusterAdapter(this::showCreateAlertDialog);
        binding.rvAdminClusters.setLayoutManager(new LinearLayoutManager(this));
        binding.rvAdminClusters.setAdapter(clusterAdapter);

        alertAdapter = new CropAlertAdapter(this::showAlertActionDialog);
        binding.rvAdminAlerts.setLayoutManager(new LinearLayoutManager(this));
        binding.rvAdminAlerts.setAdapter(alertAdapter);
    }

    private void loadReports() {
        repository.getAllReportsForAdmin(new DiseaseReportRepository.ReportCallback() {
            @Override
            public void onLoaded(List<DiseaseReport> list) {
                allReportsList = list != null ? list : new ArrayList<>();
                filterReports();
            }

            @Override
            public void onError(String error) {
                binding.tvEmptyAdminReports.setVisibility(View.VISIBLE);
            }
        });
    }

    private void filterReports() {
        if (allReportsList.isEmpty()) {
            reportAdapter.setItems(new ArrayList<>());
            binding.tvEmptyAdminReports.setVisibility(View.VISIBLE);
            binding.rvAdminReports.setVisibility(View.GONE);
            return;
        }

        List<DiseaseReport> filtered = new ArrayList<>();
        for (DiseaseReport r : allReportsList) {
            if ("ALL".equalsIgnoreCase(currentStatusFilter)) {
                filtered.add(r);
            } else if (currentStatusFilter.equalsIgnoreCase(r.getStatus())) {
                filtered.add(r);
            }
        }

        reportAdapter.setItems(filtered);
        if (filtered.isEmpty()) {
            binding.tvEmptyAdminReports.setVisibility(View.VISIBLE);
            binding.rvAdminReports.setVisibility(View.GONE);
        } else {
            binding.tvEmptyAdminReports.setVisibility(View.GONE);
            binding.rvAdminReports.setVisibility(View.VISIBLE);
        }
    }

    private void loadClusters() {
        repository.getCropClusters(new DiseaseReportRepository.ClustersCallback() {
            @Override
            public void onLoaded(List<CropCluster> list) {
                clusterAdapter.setItems(list);
                if (list == null || list.isEmpty()) {
                    binding.tvEmptyAdminClusters.setVisibility(View.VISIBLE);
                    binding.rvAdminClusters.setVisibility(View.GONE);
                } else {
                    binding.tvEmptyAdminClusters.setVisibility(View.GONE);
                    binding.rvAdminClusters.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(String error) {
                binding.tvEmptyAdminClusters.setVisibility(View.VISIBLE);
                binding.rvAdminClusters.setVisibility(View.GONE);
            }
        });
    }

    private void loadAlerts() {
        repository.getCropAlerts(null, "ALL", new DiseaseReportRepository.AlertsCallback() {
            @Override
            public void onLoaded(List<CropAlert> list) {
                alertAdapter.setItems(list);
                if (list == null || list.isEmpty()) {
                    binding.tvEmptyAdminAlerts.setVisibility(View.VISIBLE);
                    binding.rvAdminAlerts.setVisibility(View.GONE);
                } else {
                    binding.tvEmptyAdminAlerts.setVisibility(View.GONE);
                    binding.rvAdminAlerts.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(String error) {
                binding.tvEmptyAdminAlerts.setVisibility(View.VISIBLE);
                binding.rvAdminAlerts.setVisibility(View.GONE);
            }
        });
    }

    private void showTriageDialog(DiseaseReport report) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_admin_triage_report, null);

        TextView tvTitle = dialogView.findViewById(R.id.tvDialogReportTitle);
        TextView tvDetails = dialogView.findViewById(R.id.tvDialogReportDetails);
        Spinner spnStatus = dialogView.findViewById(R.id.spnDialogStatus);
        EditText etNote = dialogView.findViewById(R.id.etDialogReviewNote);
        EditText etDuplicateOf = dialogView.findViewById(R.id.etDialogDuplicateOfId);
        Spinner spnGuidance = dialogView.findViewById(R.id.spnDialogGuidancePreset);
        EditText etCustomGuidance = dialogView.findViewById(R.id.etDialogCustomGuidance);

        tvTitle.setText("Review: " + report.getCropName() + " (" + report.getId() + ")");
        String details = "Farmer: " + (report.getFarmerName() != null ? report.getFarmerName() : "Farmer") +
                " (" + (report.getFarmerPhone() != null ? report.getFarmerPhone() : "N/A") + ")\n" +
                "Location: " + (report.getVillage() != null ? report.getVillage() : "") + ", " + (report.getDistrict() != null ? report.getDistrict() : "") + "\n" +
                "Problem Category: " + (report.getProblemCategory() != null ? report.getProblemCategory() : "General") + "\n" +
                "Growth Stage: " + (report.getGrowthStage() != null ? report.getGrowthStage() : "N/A") + "\n" +
                "Symptoms: " + (report.getSymptoms() != null ? report.getSymptoms() : "None");
        if (report.getDuplicateOfReportId() != null) {
            details += "\n⚠️ Marked Duplicate of: " + report.getDuplicateOfReportId();
        }
        tvDetails.setText(details);

        // Status Spinner
        String[] statuses = {"VERIFIED", "UNDER_REVIEW", "NOT_VERIFIED", "DUPLICATE", "RESOLVED"};
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, statuses);
        spnStatus.setAdapter(statusAdapter);
        for (int i = 0; i < statuses.length; i++) {
            if (statuses[i].equalsIgnoreCase(report.getStatus())) {
                spnStatus.setSelection(i);
                break;
            }
        }

        // Guidance Presets from Authoritative Agricultural Sources
        String[] presets = {
                "Select Official Advisory Preset...",
                "Neem Oil 1500 ppm @ 5ml/L preventive spray [ICAR-CRIDA]",
                "Trichoderma viride seed/soil treatment @ 5g/L [MPKV Rahuri]",
                "Install Yellow/Blue Sticky Traps (15/acre) [KVK Baramati]",
                "Mancozeb 75% WP @ 2.5g/L for leaf spots [ICAR-IARI]",
                "Chlorantraniliprole 18.5% SC @ 0.3ml/L for pod borers [MPKV]",
                "Copper Oxychloride 50% WP @ 3g/L for bacterial blight [ICAR]",
                "Field investigation scheduled. Avoid broad-spectrum chemical sprays [KVK]"
        };
        ArrayAdapter<String> guideAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, presets);
        spnGuidance.setAdapter(guideAdapter);

        if (report.getReviewNote() != null) etNote.setText(report.getReviewNote());
        if (report.getDuplicateOfReportId() != null && etDuplicateOf != null) etDuplicateOf.setText(report.getDuplicateOfReportId());
        if (report.getAdminGuidance() != null) etCustomGuidance.setText(report.getAdminGuidance());

        new AlertDialog.Builder(this)
                .setTitle("🛡️ Crop Watch Triage")
                .setView(dialogView)
                .setPositiveButton("Save Review", (dialog, which) -> {
                    String selectedStatus = spnStatus.getSelectedItem().toString();
                    String reviewNote = etNote.getText().toString().trim();
                    String duplicateOfId = etDuplicateOf != null ? etDuplicateOf.getText().toString().trim() : null;
                    String chosenPreset = spnGuidance.getSelectedItemPosition() > 0 ?
                            spnGuidance.getSelectedItem().toString() : "";
                    String customGuide = etCustomGuidance.getText().toString().trim();

                    String finalGuidance = !customGuide.isEmpty() ? customGuide : chosenPreset;
                    String source = chosenPreset.contains("[") ?
                            chosenPreset.substring(chosenPreset.indexOf("[") + 1, chosenPreset.indexOf("]")) : "KVK Baramati Advisory";

                    repository.reviewReport(report.getId(), selectedStatus, reviewNote, finalGuidance, source, sessionManager.getUserPhone(), duplicateOfId,
                            new DiseaseReportRepository.SingleReportCallback() {
                                @Override
                                public void onSuccess(DiseaseReport updated) {
                                    Toast.makeText(AdminCropWatchActivity.this, "Report triaged successfully & farmer notified!", Toast.LENGTH_SHORT).show();
                                    loadReports();
                                }

                                @Override
                                public void onError(String error) {
                                    Toast.makeText(AdminCropWatchActivity.this, "Triage saved locally.", Toast.LENGTH_SHORT).show();
                                    loadReports();
                                }
                            });
                })
                .setNeutralButton("Publish Alert", (dialog, which) -> {
                    // Prepopulate alert creation
                    CropCluster tempCluster = new CropCluster();
                    tempCluster.setCropName(report.getCropName());
                    tempCluster.setDistrict(report.getDistrict());
                    tempCluster.setVillage(report.getVillage());
                    tempCluster.setProblemCategory(report.getProblemCategory());
                    showCreateAlertDialog(tempCluster);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showCreateAlertDialog(CropCluster cluster) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_admin_create_alert, null);

        EditText etTitle = dialogView.findViewById(R.id.etAlertTitle);
        EditText etCrop = dialogView.findViewById(R.id.etAlertCrop);
        EditText etDistrict = dialogView.findViewById(R.id.etAlertDistrict);
        EditText etVillage = dialogView.findViewById(R.id.etAlertVillage);
        Spinner spnSeverity = dialogView.findViewById(R.id.spnAlertSeverity);
        EditText etSummary = dialogView.findViewById(R.id.etAlertSummary);
        EditText etAuthority = dialogView.findViewById(R.id.etAlertAuthority);
        EditText etActions = dialogView.findViewById(R.id.etAlertActions);

        String[] severities = {"ADVISORY", "MODERATE", "HIGH", "SEVERE"};
        spnSeverity.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, severities));

        etAuthority.setText("KVK Baramati & MPKV Extension Wing");

        if (cluster != null) {
            etCrop.setText(cluster.getCropName());
            etDistrict.setText(cluster.getDistrict());
            if (cluster.getVillage() != null) etVillage.setText(cluster.getVillage());
            etTitle.setText("Community Alert: " + cluster.getCropName() + " " + cluster.getProblemCategory());
            etSummary.setText("Multiple farmers (" + cluster.getDistinctFarmerCount() +
                    " verified reports) in " + cluster.getDistrict() + " reported similar symptoms. Agronomic advisory issued.");

            if (cluster.isHasMatchedAdvisory() && cluster.getMatchedAdvisoryText() != null) {
                etActions.setText(cluster.getMatchedAdvisoryText() + "\n\nOfficial Source Reference:\n" +
                        cluster.getMatchedAdvisorySourceName() + " (" + cluster.getMatchedAdvisorySourceUrl() + ")");
                etAuthority.setText(cluster.getMatchedAdvisorySourceName());
            } else {
                etActions.setText("1. Inspect crops for early leaf chlorosis and pests.\n2. Apply recommended bio-formulations.\n3. Report severe spread to nearest agricultural extension centre.");
            }
            spnSeverity.setSelection(cluster.isPotentialCluster() ? 2 : 1);
        }

        new AlertDialog.Builder(this)
                .setTitle("📢 Publish Verified Regional Crop Alert")
                .setView(dialogView)
                .setPositiveButton("Publish & Broadcast", (dialog, which) -> {
                    String title = etTitle.getText().toString().trim();
                    String crop = etCrop.getText().toString().trim();
                    String district = etDistrict.getText().toString().trim();
                    String village = etVillage.getText().toString().trim();
                    String severity = spnSeverity.getSelectedItem().toString();
                    String summary = etSummary.getText().toString().trim();
                    String authority = etAuthority.getText().toString().trim();
                    String actions = etActions.getText().toString().trim();

                    if (title.isEmpty() || crop.isEmpty() || district.isEmpty() || summary.isEmpty()) {
                        Toast.makeText(this, "Title, Crop, District, and Summary are required.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    CropAlert alert = new CropAlert();
                    alert.setId("CW-ALERT-" + System.currentTimeMillis());
                    alert.setCropName(crop);
                    alert.setDistrict(district);
                    alert.setVillage(village);
                    alert.setAlertTitle(title);
                    alert.setSeverity(severity);
                    alert.setSummary(summary);
                    alert.setReportCount(cluster != null ? cluster.getDistinctFarmerCount() : 1);
                    alert.setAuthority(authority);
                    alert.setRecommendedActions(actions);
                    alert.setStatus("ACTIVE");
                    alert.setPublishedBy(sessionManager.getUserPhone());
                    alert.setPublishedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")));

                    repository.publishCropAlert(alert, new DiseaseReportRepository.SingleAlertCallback() {
                        @Override
                        public void onSuccess(CropAlert saved) {
                            Toast.makeText(AdminCropWatchActivity.this, "Verified Alert published & broadcast to farmers!", Toast.LENGTH_LONG).show();
                            // Switch to Alerts tab
                            TabLayout.Tab tab = binding.adminTabLayout.getTabAt(2);
                            if (tab != null) tab.select();
                        }

                        @Override
                        public void onError(String error) {
                            Toast.makeText(AdminCropWatchActivity.this, "Alert saved locally.", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showAlertActionDialog(CropAlert alert) {
        String[] options = {"Mark as RESOLVED", "WITHDRAW Alert", "View Details"};
        new AlertDialog.Builder(this)
                .setTitle(alert.getAlertTitle())
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        repository.updateAlertStatus(alert.getId(), "RESOLVED", new DiseaseReportRepository.SingleAlertCallback() {
                            @Override
                            public void onSuccess(CropAlert updated) {
                                Toast.makeText(AdminCropWatchActivity.this, "Alert marked as RESOLVED", Toast.LENGTH_SHORT).show();
                                loadAlerts();
                            }

                            @Override
                            public void onError(String error) {
                                Toast.makeText(AdminCropWatchActivity.this, "Updated locally.", Toast.LENGTH_SHORT).show();
                                loadAlerts();
                            }
                        });
                    } else if (which == 1) {
                        repository.updateAlertStatus(alert.getId(), "WITHDRAWN", new DiseaseReportRepository.SingleAlertCallback() {
                            @Override
                            public void onSuccess(CropAlert updated) {
                                Toast.makeText(AdminCropWatchActivity.this, "Alert WITHDRAWN", Toast.LENGTH_SHORT).show();
                                loadAlerts();
                            }

                            @Override
                            public void onError(String error) {
                                Toast.makeText(AdminCropWatchActivity.this, "Updated locally.", Toast.LENGTH_SHORT).show();
                                loadAlerts();
                            }
                        });
                    } else {
                        new AlertDialog.Builder(this)
                                .setTitle(alert.getAlertTitle())
                                .setMessage(alert.getSummary() + "\n\nActions:\n" + alert.getRecommendedActions() +
                                        "\n\nAuthority: " + alert.getAuthority())
                                .setPositiveButton("Close", null)
                                .show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
