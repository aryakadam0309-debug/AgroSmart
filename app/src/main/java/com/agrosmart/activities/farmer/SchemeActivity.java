package com.agrosmart.activities.farmer;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.agrosmart.R;
import com.agrosmart.activities.BaseActivity;
import com.agrosmart.adapters.SchemeAdapter;
import com.agrosmart.database.AgroSmartDbHelper;
import com.agrosmart.databinding.ActivitySchemeBinding;
import com.agrosmart.models.FarmerProfile;
import com.agrosmart.models.GovernmentScheme;
import com.agrosmart.models.SchemeEligibilityResult;
import com.agrosmart.network.ApiClient;
import com.agrosmart.repository.SchemeRepository;
import com.agrosmart.utils.SchemeEligibilityHelper;
import com.agrosmart.utils.SessionManager;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SchemeActivity extends BaseActivity {

    private ActivitySchemeBinding binding;
    private SchemeAdapter schemeAdapter;
    private AgroSmartDbHelper dbHelper;
    private SessionManager sessionManager;
    private FarmerProfile farmerProfile;

    private int activeTab = 0; // 0 = Recommended, 1 = All, 2 = Saved
    private String selectedCategory = "All";
    private String searchQuery = "";
    private boolean closedTestToggle = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySchemeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new AgroSmartDbHelper(this);
        sessionManager = new SessionManager(this);

        initFarmerProfile();
        setupRecyclerView();
        setupListeners();
        setupSearchAndFilters();

        // Default: Open Recommended tab
        switchTab(0);
    }

    private void initFarmerProfile() {
        farmerProfile = sessionManager.getFarmerProfile();
        updateProfileBannerText();
    }

    private void updateProfileBannerText() {
        String catLabel = "SMALL_MARGINAL".equalsIgnoreCase(farmerProfile.getFarmerCategory()) ? "Small/Marginal"
                : ("LARGE".equalsIgnoreCase(farmerProfile.getFarmerCategory()) ? "Large" : farmerProfile.getFarmerCategory());

        binding.tvProfileSummary.setText(String.format("Profile: %s • %.1f Acres (%s) • %s",
                farmerProfile.getState(),
                farmerProfile.getLandAreaAcres(),
                catLabel,
                farmerProfile.getOwnershipStatus()));
    }

    private void setupRecyclerView() {
        schemeAdapter = new SchemeAdapter(this);
        binding.rvSchemes.setLayoutManager(new LinearLayoutManager(this));
        binding.rvSchemes.setAdapter(schemeAdapter);

        schemeAdapter.setOnSchemeBookmarkListener((scheme, isSaved) -> {
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
        });
    }

    private void setupListeners() {
        binding.btnBack.setOnClickListener(v -> finish());
        binding.cardFarmerProfileBanner.setOnClickListener(v -> showProfileCompletionDialog());

        binding.tabRecommended.setOnClickListener(v -> switchTab(0));
        binding.tabAllSchemes.setOnClickListener(v -> switchTab(1));
        binding.tabSavedSchemes.setOnClickListener(v -> switchTab(2));

        // Hackathon Scenario 3: Toggle closed scheme status to demonstrate live reflection to judges
        binding.btnToggleClosedDemo.setOnClickListener(v -> toggleClosedSchemeDemo());
    }

    private void switchTab(int tabIndex) {
        activeTab = tabIndex;

        // Reset tab backgrounds
        binding.tabRecommended.setBackgroundColor(Color.TRANSPARENT);
        binding.tabRecommended.setTextColor(Color.parseColor("#424242"));
        binding.tabAllSchemes.setBackgroundColor(Color.TRANSPARENT);
        binding.tabAllSchemes.setTextColor(Color.parseColor("#424242"));
        binding.tabSavedSchemes.setBackgroundColor(Color.TRANSPARENT);
        binding.tabSavedSchemes.setTextColor(Color.parseColor("#424242"));

        if (tabIndex == 0) {
            binding.tabRecommended.setBackgroundResource(R.drawable.bg_rounded_card);
            binding.tabRecommended.getBackground().setTint(getColor(R.color.forest_green_primary));
            binding.tabRecommended.setTextColor(Color.WHITE);
            loadRecommendedSchemes();
        } else if (tabIndex == 1) {
            binding.tabAllSchemes.setBackgroundResource(R.drawable.bg_rounded_card);
            binding.tabAllSchemes.getBackground().setTint(getColor(R.color.forest_green_primary));
            binding.tabAllSchemes.setTextColor(Color.WHITE);
            loadAllSchemes();
        } else {
            binding.tabSavedSchemes.setBackgroundResource(R.drawable.bg_rounded_card);
            binding.tabSavedSchemes.getBackground().setTint(getColor(R.color.forest_green_primary));
            binding.tabSavedSchemes.setTextColor(Color.WHITE);
            loadSavedSchemes();
        }
    }

    private void setupSearchAndFilters() {
        binding.etSearchScheme.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchQuery = s != null ? s.toString().trim() : "";
                if (activeTab == 0) {
                    loadRecommendedSchemes();
                } else if (activeTab == 1) {
                    loadAllSchemes();
                }
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        binding.chipGroupSchemeCategories.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int id = checkedIds.get(0);
            if (id == R.id.chipFinancial) {
                selectedCategory = "Financial Assistance";
            } else if (id == R.id.chipInsurance) {
                selectedCategory = "Crop Insurance";
            } else if (id == R.id.chipMachinery) {
                selectedCategory = "Farm Machinery";
            } else if (id == R.id.chipIrrigation) {
                selectedCategory = "Irrigation & Water";
            } else if (id == R.id.chipCredit) {
                selectedCategory = "Agricultural Credit";
            } else if (id == R.id.chipSoilHealth) {
                selectedCategory = "Soil Health";
            } else {
                selectedCategory = "All";
            }

            if (activeTab == 0) {
                loadRecommendedSchemes();
            } else if (activeTab == 1) {
                loadAllSchemes();
            }
        });
    }

    private void loadRecommendedSchemes() {
        ApiClient.getInstance().getApi().getRecommendedSchemes(sessionManager.getUserPhone(), farmerProfile)
                .enqueue(new Callback<List<SchemeEligibilityResult>>() {
                    @Override
                    public void onResponse(Call<List<SchemeEligibilityResult>> call, Response<List<SchemeEligibilityResult>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<SchemeEligibilityResult> results = response.body();
                            filterAndDisplayRecommendations(results);
                        } else {
                            loadOfflineRecommendationsFallback();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<SchemeEligibilityResult>> call, Throwable t) {
                        loadOfflineRecommendationsFallback();
                    }
                });
    }

    private void filterAndDisplayRecommendations(List<SchemeEligibilityResult> list) {
        List<SchemeEligibilityResult> filtered = new ArrayList<>();
        for (SchemeEligibilityResult r : list) {
            GovernmentScheme s = r.getScheme();
            if (s == null) continue;

            boolean catMatch = "All".equalsIgnoreCase(selectedCategory) ||
                    (s.getCategory() != null && s.getCategory().toLowerCase().contains(selectedCategory.toLowerCase()));
            boolean queryMatch = searchQuery.isEmpty() ||
                    s.getSchemeName().toLowerCase().contains(searchQuery.toLowerCase()) ||
                    (s.getDescription() != null && s.getDescription().toLowerCase().contains(searchQuery.toLowerCase()));

            if (catMatch && queryMatch) {
                filtered.add(r);
            }
        }

        binding.layoutEmptyState.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
        schemeAdapter.setEligibilityResults(filtered);
    }

    private void loadOfflineRecommendationsFallback() {
        SchemeRepository.getInstance().getSchemes(selectedCategory, searchQuery, new SchemeRepository.SchemeCallback() {
            @Override
            public void onSuccess(List<GovernmentScheme> schemes) {
                List<SchemeEligibilityResult> results = new ArrayList<>();
                for (GovernmentScheme s : schemes) {
                    SchemeEligibilityResult r = SchemeEligibilityHelper.evaluate(s, farmerProfile);
                    if (r != null) {
                        r.setSaved(dbHelper.isSchemeSaved(s.getSchemeId(), sessionManager.getUserPhone()));
                        results.add(r);
                    }
                }

                // Rank order: Likely Eligible (1) -> Possibly Eligible (2) -> Not Eligible (3) -> Application Closed (4) -> Status Unverified (5)
                results.sort((a, b) -> Integer.compare(getRankScore(a.getEligibilityStatus()), getRankScore(b.getEligibilityStatus())));

                binding.layoutEmptyState.setVisibility(results.isEmpty() ? View.VISIBLE : View.GONE);
                schemeAdapter.setEligibilityResults(results);
            }

            @Override
            public void onError(String message) {
                binding.layoutEmptyState.setVisibility(View.VISIBLE);
            }
        });
    }

    private int getRankScore(String status) {
        if ("Likely Eligible".equalsIgnoreCase(status)) return 1;
        if ("Possibly Eligible – More Information Required".equalsIgnoreCase(status)) return 2;
        if ("Not Eligible".equalsIgnoreCase(status)) return 3;
        if ("Application Closed".equalsIgnoreCase(status)) return 4;
        return 5;
    }

    private void loadAllSchemes() {
        ApiClient.getInstance().getApi().filterSchemes(selectedCategory, "All", "ALL_INDIA", "All", searchQuery)
                .enqueue(new Callback<List<GovernmentScheme>>() {
                    @Override
                    public void onResponse(Call<List<GovernmentScheme>> call, Response<List<GovernmentScheme>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<GovernmentScheme> list = response.body();
                            displayAllSchemesWithEligibility(list);
                        } else {
                            loadOfflineAllSchemes();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<GovernmentScheme>> call, Throwable t) {
                        loadOfflineAllSchemes();
                    }
                });
    }

    private void displayAllSchemesWithEligibility(List<GovernmentScheme> list) {
        List<SchemeEligibilityResult> results = new ArrayList<>();
        for (GovernmentScheme s : list) {
            SchemeEligibilityResult r = SchemeEligibilityHelper.evaluate(s, farmerProfile);
            if (r != null) {
                r.setSaved(dbHelper.isSchemeSaved(s.getSchemeId(), sessionManager.getUserPhone()));
                results.add(r);
            }
        }
        binding.layoutEmptyState.setVisibility(results.isEmpty() ? View.VISIBLE : View.GONE);
        schemeAdapter.setEligibilityResults(results);
    }

    private void loadOfflineAllSchemes() {
        SchemeRepository.getInstance().getSchemes(selectedCategory, searchQuery, new SchemeRepository.SchemeCallback() {
            @Override
            public void onSuccess(List<GovernmentScheme> schemes) {
                displayAllSchemesWithEligibility(schemes);
            }

            @Override
            public void onError(String message) {
                binding.layoutEmptyState.setVisibility(View.VISIBLE);
            }
        });
    }

    private void loadSavedSchemes() {
        // Load from local SQLite cache first (offline instant render)
        List<GovernmentScheme> localSaved = dbHelper.getLocalSavedSchemes(sessionManager.getUserPhone());
        if (!localSaved.isEmpty()) {
            displayAllSchemesWithEligibility(localSaved);
        }

        // Sync with backend
        ApiClient.getInstance().getApi().getSavedSchemes(sessionManager.getUserPhone())
                .enqueue(new Callback<List<GovernmentScheme>>() {
                    @Override
                    public void onResponse(Call<List<GovernmentScheme>> call, Response<List<GovernmentScheme>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<GovernmentScheme> list = response.body();
                            displayAllSchemesWithEligibility(list);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<GovernmentScheme>> call, Throwable t) {}
                });
    }

    private void toggleClosedSchemeDemo() {
        closedTestToggle = !closedTestToggle;
        String targetScheme = "SCH03"; // SMAM
        String newStatus = closedTestToggle ? "CLOSED" : "OPEN";
        String deadline = closedTestToggle ? "Closed for Fiscal Year" : "Annual Quota Window";

        ApiClient.getInstance().getApi().toggleSchemeStatus(targetScheme, newStatus, deadline)
                .enqueue(new Callback<GovernmentScheme>() {
                    @Override
                    public void onResponse(Call<GovernmentScheme> call, Response<GovernmentScheme> response) {
                        String msg = closedTestToggle ? "⚡ Demo: SMAM Mechanization is now CLOSED (Application disabled)"
                                                      : "⚡ Demo: SMAM Mechanization is now OPEN";
                        Toast.makeText(SchemeActivity.this, msg, Toast.LENGTH_LONG).show();
                        switchTab(activeTab); // refresh list
                    }

                    @Override
                    public void onFailure(Call<GovernmentScheme> call, Throwable t) {
                        // Offline local toggle demo
                        for (GovernmentScheme s : SchemeRepository.getInstance().getAllSchemes()) {
                            if (targetScheme.equalsIgnoreCase(s.getSchemeId())) {
                                s.setApplicationStatus(newStatus);
                                s.setDeadline(deadline);
                                break;
                            }
                        }
                        String msg = closedTestToggle ? "⚡ Demo: SMAM Mechanization is now CLOSED (Application disabled)"
                                : "⚡ Demo: SMAM Mechanization is now OPEN";
                        Toast.makeText(SchemeActivity.this, msg, Toast.LENGTH_LONG).show();
                        switchTab(activeTab);
                    }
                });
    }

    private void showProfileCompletionDialog() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_farmer_profile_completion, null);
        dialog.setContentView(view);

        Spinner spState = view.findViewById(R.id.spinnerProfileState);
        EditText etLand = view.findViewById(R.id.etProfileLandAcres);
        Spinner spCategory = view.findViewById(R.id.spinnerProfileCategory);
        EditText etCrops = view.findViewById(R.id.etProfileCrops);
        Spinner spOwnership = view.findViewById(R.id.spinnerProfileOwnership);
        MaterialButton btnSave = view.findViewById(R.id.btnSaveProfile);
        MaterialButton btnCancel = view.findViewById(R.id.btnCancelProfile);

        String[] states = {"Maharashtra", "Punjab", "Gujarat", "Madhya Pradesh", "Karnataka", "Uttar Pradesh"};
        ArrayAdapter<String> stateAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, states);
        spState.setAdapter(stateAdapter);

        String[] categories = {"SMALL_MARGINAL", "LARGE", "SC_ST", "WOMEN", "GENERAL"};
        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        spCategory.setAdapter(catAdapter);

        String[] ownerships = {"OWNER", "TENANT", "SHARECROPPER"};
        ArrayAdapter<String> ownAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, ownerships);
        spOwnership.setAdapter(ownAdapter);

        // Pre-fill values
        etLand.setText(String.valueOf(farmerProfile.getLandAreaAcres()));
        etCrops.setText(farmerProfile.getPrimaryCrops());

        for (int i = 0; i < states.length; i++) {
            if (states[i].equalsIgnoreCase(farmerProfile.getState())) {
                spState.setSelection(i);
                break;
            }
        }
        for (int i = 0; i < categories.length; i++) {
            if (categories[i].equalsIgnoreCase(farmerProfile.getFarmerCategory())) {
                spCategory.setSelection(i);
                break;
            }
        }
        for (int i = 0; i < ownerships.length; i++) {
            if (ownerships[i].equalsIgnoreCase(farmerProfile.getOwnershipStatus())) {
                spOwnership.setSelection(i);
                break;
            }
        }

        // Automatic category switching based on agricultural acreage rules:
        // Land > 5.0 acres automatically categorizes as LARGE (Small/Marginal ceiling is 5.0 acres / 2 ha)
        etLand.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    String str = s != null ? s.toString().trim() : "";
                    if (!str.isEmpty()) {
                        double acres = Double.parseDouble(str);
                        if (acres > 5.0 && spCategory.getSelectedItemPosition() == 0) {
                            for (int i = 0; i < categories.length; i++) {
                                if ("LARGE".equalsIgnoreCase(categories[i])) {
                                    spCategory.setSelection(i);
                                    break;
                                }
                            }
                        } else if (acres <= 5.0 && "LARGE".equalsIgnoreCase(spCategory.getSelectedItem().toString())) {
                            spCategory.setSelection(0); // SMALL_MARGINAL
                        }
                    }
                } catch (Exception ignored) {}
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnSave.setOnClickListener(v -> {
            try {
                String landStr = etLand.getText().toString().trim();
                double land = landStr.isEmpty() ? 2.5 : Double.parseDouble(landStr);
                String selectedCat = spCategory.getSelectedItem().toString();

                // Enforce agricultural categorization consistency
                if (land > 5.0 && "SMALL_MARGINAL".equalsIgnoreCase(selectedCat)) {
                    selectedCat = "LARGE";
                }

                farmerProfile.setLandAreaAcres(land);
                farmerProfile.setState(spState.getSelectedItem().toString());
                farmerProfile.setFarmerCategory(selectedCat);
                farmerProfile.setOwnershipStatus(spOwnership.getSelectedItem().toString());
                farmerProfile.setPrimaryCrops(etCrops.getText().toString().trim());

                sessionManager.saveFarmerProfile(farmerProfile);
                updateProfileBannerText();
                dialog.dismiss();
                Toast.makeText(this, "Profile updated. Refreshing eligibility ranking...", Toast.LENGTH_SHORT).show();
                switchTab(activeTab);
            } catch (Exception e) {
                Toast.makeText(this, "Please enter valid numeric land acreage", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }
}
