package com.agrosmart.activities.farmer;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.agrosmart.activities.BaseActivity;
import com.agrosmart.database.AgroSmartDbHelper;
import com.agrosmart.databinding.ActivitySchemeDetailBinding;
import com.agrosmart.models.CriterionCheck;
import com.agrosmart.models.FarmerProfile;
import com.agrosmart.models.GovernmentScheme;
import com.agrosmart.models.SchemeEligibilityResult;
import com.agrosmart.network.ApiClient;
import com.agrosmart.utils.SessionManager;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SchemeDetailActivity extends BaseActivity {

    private ActivitySchemeDetailBinding binding;
    private GovernmentScheme scheme;
    private SchemeEligibilityResult eligibilityResult;
    private AgroSmartDbHelper dbHelper;
    private SessionManager sessionManager;
    private FarmerProfile farmerProfile;
    private boolean isSaved = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySchemeDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new AgroSmartDbHelper(this);
        sessionManager = new SessionManager(this);
        loadLocalProfile();

        scheme = (GovernmentScheme) getIntent().getSerializableExtra("extra_scheme_item");
        eligibilityResult = (SchemeEligibilityResult) getIntent().getSerializableExtra("extra_eligibility_result");

        binding.btnSchemeDetailBack.setOnClickListener(v -> finish());

        if (scheme != null) {
            renderSchemeDetails();
            checkSavedBookmarkStatus();
            if (eligibilityResult != null) {
                renderEligibilityReport(eligibilityResult);
            } else {
                fetchOnlineEligibility();
            }
        }

        binding.btnDetailBookmark.setOnClickListener(v -> toggleBookmark());
        binding.btnRecheckProfile.setOnClickListener(v -> showProfileCompletionDialog());
    }

    private void loadLocalProfile() {
        farmerProfile = new FarmerProfile();
        farmerProfile.setFarmerPhone(sessionManager.getUserPhone());
        farmerProfile.setFullName(sessionManager.getUserName());
        farmerProfile.setState("Maharashtra");
        farmerProfile.setDistrict("Baramati");
        farmerProfile.setLandAreaAcres(2.5);
        farmerProfile.setFarmerCategory("SMALL_MARGINAL");
        farmerProfile.setPrimaryCrops("Soybean, Cotton");
        farmerProfile.setAnnualIncome(120000.0);
        farmerProfile.setOwnershipStatus("OWNER");
    }

    private void renderSchemeDetails() {
        binding.tvDetailSchemeName.setText(scheme.getSchemeName());
        binding.tvDetailMinistry.setText(scheme.getMinistry());
        binding.tvDetailCategoryBadge.setText(scheme.getCategory());
        binding.tvDetailLevelBadge.setText(scheme.getLevel() != null ? scheme.getLevel().toUpperCase() : "CENTRAL");
        binding.tvDetailBenefits.setText(scheme.getBenefits());
        binding.tvDetailEligibility.setText(scheme.getEligibility());
        binding.tvDetailDocuments.setText(scheme.getRequiredDocuments());
        binding.tvDetailProcess.setText(scheme.getApplicationProcess());
        binding.tvDetailDates.setText("🗓️ Timeline / Deadline: " + scheme.getDeadline());
        binding.tvDetailHelpline.setText("📞 Official Helpline: " + scheme.getHelpline());

        String verText = "🛡️ Verified Source: " + scheme.getSourceUrl();
        if (scheme.getLastVerifiedDate() != null) {
            verText += " • Last Verified: " + scheme.getLastVerifiedDate();
        }
        binding.tvVerifiedSourceInfo.setText(verText);

        // Application Status & Action Handling (PS 5.3 Requirement 7: Closed Schemes)
        boolean isClosed = "CLOSED".equalsIgnoreCase(scheme.getApplicationStatus()) ||
                           (scheme.getDeadline() != null && scheme.getDeadline().toLowerCase().contains("closed"));

        if (isClosed) {
            binding.btnOpenPortal.setText("Applications Closed on Official Portal 🔒");
            binding.btnOpenPortal.setBackgroundColor(Color.parseColor("#78909C"));
            binding.tvClosedNotice.setVisibility(View.VISIBLE);
            binding.tvClosedNotice.setText("🔒 The official application window for this scheme is closed. You may inspect guidelines on the portal.");

            binding.btnOpenPortal.setOnClickListener(v -> {
                Toast.makeText(this, "Notice: Application window is closed on the government portal.", Toast.LENGTH_SHORT).show();
                openOfficialPortal();
            });
        } else {
            binding.btnOpenPortal.setText("Apply on Official Website 🌐");
            binding.btnOpenPortal.setBackgroundColor(Color.parseColor("#2E7D32"));
            binding.tvClosedNotice.setVisibility(View.GONE);
            binding.btnOpenPortal.setOnClickListener(v -> openOfficialPortal());
        }
    }

    private void openOfficialPortal() {
        String url = scheme.getOfficialPortalUrl();
        if (url != null && !url.trim().isEmpty()) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        } else {
            Toast.makeText(this, "Official portal URL not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkSavedBookmarkStatus() {
        isSaved = dbHelper.isSchemeSaved(scheme.getSchemeId(), sessionManager.getUserPhone());
        updateBookmarkIcon();
    }

    private void updateBookmarkIcon() {
        binding.btnDetailBookmark.setColorFilter(isSaved ? Color.parseColor("#FFCDD2") : Color.parseColor("#FFFFFF"));
    }

    private void toggleBookmark() {
        isSaved = !isSaved;
        updateBookmarkIcon();

        String phone = sessionManager.getUserPhone();
        if (isSaved) {
            dbHelper.saveSchemeBookmark(scheme, phone);
            ApiClient.getInstance().getApi().saveScheme(phone, scheme.getSchemeId()).enqueue(new Callback<Map<String, Object>>() {
                @Override public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {}
                @Override public void onFailure(Call<Map<String, Object>> call, Throwable t) {}
            });
            Toast.makeText(this, "❤️ Scheme saved to My Bookmarks", Toast.LENGTH_SHORT).show();
        } else {
            dbHelper.removeSchemeBookmark(scheme.getSchemeId(), phone);
            ApiClient.getInstance().getApi().unsaveScheme(phone, scheme.getSchemeId()).enqueue(new Callback<Map<String, Object>>() {
                @Override public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {}
                @Override public void onFailure(Call<Map<String, Object>> call, Throwable t) {}
            });
            Toast.makeText(this, "Scheme removed from bookmarks", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchOnlineEligibility() {
        ApiClient.getInstance().getApi().evaluateScheme(scheme.getSchemeId(), farmerProfile)
                .enqueue(new Callback<SchemeEligibilityResult>() {
                    @Override
                    public void onResponse(Call<SchemeEligibilityResult> call, Response<SchemeEligibilityResult> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            eligibilityResult = response.body();
                            renderEligibilityReport(eligibilityResult);
                        }
                    }

                    @Override
                    public void onFailure(Call<SchemeEligibilityResult> call, Throwable t) {
                        // Offline or backend unreachable: render basic offline check
                        renderOfflineEligibility();
                    }
                });
    }

    private void renderOfflineEligibility() {
        // Simple offline fallback evaluation
        boolean isClosed = "CLOSED".equalsIgnoreCase(scheme.getApplicationStatus());
        boolean landOk = scheme.getMaxLandAcres() == null || (farmerProfile.getLandAreaAcres() <= scheme.getMaxLandAcres());

        SchemeEligibilityResult offlineRes = new SchemeEligibilityResult();
        offlineRes.setSchemeId(scheme.getSchemeId());
        if (isClosed) {
            offlineRes.setEligibilityStatus("Application Closed");
            offlineRes.setSummaryExplanation("Official application window is closed according to published timeline.");
        } else if (!landOk) {
            offlineRes.setEligibilityStatus("Not Eligible");
            offlineRes.setSummaryExplanation("Your landholding (" + farmerProfile.getLandAreaAcres() + " acres) exceeds the designated threshold (" + scheme.getMaxLandAcres() + " acres).");
        } else {
            offlineRes.setEligibilityStatus("Likely Eligible");
            offlineRes.setSummaryExplanation("Your registered farm profile satisfies known eligibility conditions.");
        }
        renderEligibilityReport(offlineRes);
    }

    private void renderEligibilityReport(SchemeEligibilityResult res) {
        if (res == null) return;

        binding.tvDetailEligibilityStatusBadge.setText(res.getEligibilityStatus());
        binding.tvDetailEligibilityExplanation.setText(res.getSummaryExplanation());

        String status = res.getEligibilityStatus();
        if ("Likely Eligible".equalsIgnoreCase(status)) {
            binding.tvDetailEligibilityStatusBadge.setBackgroundColor(Color.parseColor("#E8F5E9"));
            binding.tvDetailEligibilityStatusBadge.setTextColor(Color.parseColor("#2E7D32"));
        } else if ("Possibly Eligible – More Information Required".equalsIgnoreCase(status)) {
            binding.tvDetailEligibilityStatusBadge.setBackgroundColor(Color.parseColor("#FFF3E0"));
            binding.tvDetailEligibilityStatusBadge.setTextColor(Color.parseColor("#E65100"));
        } else if ("Not Eligible".equalsIgnoreCase(status)) {
            binding.tvDetailEligibilityStatusBadge.setBackgroundColor(Color.parseColor("#FFEBEE"));
            binding.tvDetailEligibilityStatusBadge.setTextColor(Color.parseColor("#C62828"));
        } else if ("Application Closed".equalsIgnoreCase(status)) {
            binding.tvDetailEligibilityStatusBadge.setBackgroundColor(Color.parseColor("#ECEFF1"));
            binding.tvDetailEligibilityStatusBadge.setTextColor(Color.parseColor("#455A64"));
        } else {
            binding.tvDetailEligibilityStatusBadge.setBackgroundColor(Color.parseColor("#F5F5F5"));
            binding.tvDetailEligibilityStatusBadge.setTextColor(Color.parseColor("#757575"));
        }

        // Render Criteria Checks
        binding.layoutCriteriaList.removeAllViews();
        List<CriterionCheck> checks = res.getCriteriaChecks();
        if (checks != null && !checks.isEmpty()) {
            for (CriterionCheck c : checks) {
                TextView tv = new TextView(this);
                tv.setTextSize(13);
                tv.setPadding(0, 4, 0, 4);

                if (Boolean.TRUE.equals(c.getIsSatisfied())) {
                    tv.setText("✅ " + c.getCriterionName() + ": " + c.getFarmerValue() + " (Satisfied)");
                    tv.setTextColor(Color.parseColor("#2E7D32"));
                } else if (Boolean.FALSE.equals(c.getIsSatisfied())) {
                    tv.setText("❌ " + c.getCriterionName() + ": " + c.getFarmerValue() + " (Unmet: " + c.getExplanation() + ")");
                    tv.setTextColor(Color.parseColor("#C62828"));
                } else {
                    tv.setText("⚠️ " + c.getCriterionName() + ": " + c.getExplanation());
                    tv.setTextColor(Color.parseColor("#E65100"));
                }
                binding.layoutCriteriaList.addView(tv);
            }
        }
    }

    private void showProfileCompletionDialog() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(com.agrosmart.R.layout.dialog_farmer_profile_completion, null);
        dialog.setContentView(view);

        Spinner spState = view.findViewById(com.agrosmart.R.id.spinnerProfileState);
        EditText etLand = view.findViewById(com.agrosmart.R.id.etProfileLandAcres);
        Spinner spCategory = view.findViewById(com.agrosmart.R.id.spinnerProfileCategory);
        EditText etCrops = view.findViewById(com.agrosmart.R.id.etProfileCrops);
        Spinner spOwnership = view.findViewById(com.agrosmart.R.id.spinnerProfileOwnership);
        MaterialButton btnSave = view.findViewById(com.agrosmart.R.id.btnSaveProfile);
        MaterialButton btnCancel = view.findViewById(com.agrosmart.R.id.btnCancelProfile);

        String[] states = {"Maharashtra", "Punjab", "Gujarat", "Madhya Pradesh", "Karnataka", "Uttar Pradesh"};
        ArrayAdapter<String> stateAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, states);
        spState.setAdapter(stateAdapter);

        String[] categories = {"SMALL_MARGINAL", "LARGE", "SC_ST", "WOMEN", "GENERAL"};
        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        spCategory.setAdapter(catAdapter);

        String[] ownerships = {"OWNER", "TENANT", "SHARECROPPER"};
        ArrayAdapter<String> ownAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, ownerships);
        spOwnership.setAdapter(ownAdapter);

        // Pre-fill current values
        etLand.setText(String.valueOf(farmerProfile.getLandAreaAcres()));
        etCrops.setText(farmerProfile.getPrimaryCrops());

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnSave.setOnClickListener(v -> {
            try {
                String landStr = etLand.getText().toString().trim();
                double land = landStr.isEmpty() ? 2.5 : Double.parseDouble(landStr);
                farmerProfile.setLandAreaAcres(land);
                farmerProfile.setState(spState.getSelectedItem().toString());
                farmerProfile.setFarmerCategory(spCategory.getSelectedItem().toString());
                farmerProfile.setOwnershipStatus(spOwnership.getSelectedItem().toString());
                farmerProfile.setPrimaryCrops(etCrops.getText().toString().trim());

                dialog.dismiss();
                Toast.makeText(this, "Profile updated. Re-evaluating eligibility...", Toast.LENGTH_SHORT).show();
                fetchOnlineEligibility();
            } catch (Exception e) {
                Toast.makeText(this, "Please enter valid numeric land size", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }
}
