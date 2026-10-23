package com.agrosmart.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.agrosmart.R;
import com.agrosmart.adapters.SavedCropPlanAdapter;
import com.agrosmart.databinding.FragmentCropPlannerBinding;
import com.agrosmart.models.SavedCropPlanModel;
import com.agrosmart.repository.CropPlanRepository;
import com.agrosmart.utils.SessionManager;
import com.google.android.material.button.MaterialButton;

import java.util.*;

public class CropPlannerFragment extends Fragment implements SavedCropPlanAdapter.OnPlanActionListener {

    private FragmentCropPlannerBinding binding;
    private CropPlanRepository repository;
    private SessionManager sessionManager;
    private SavedCropPlanAdapter savedPlansAdapter;

    private final String[] districts = {
            "Pune", "Nashik", "Ahmednagar", "Satara", "Solapur",
            "Chhatrapati Sambhajinagar", "Jalna", "Nanded", "Latur", "Parbhani",
            "Nagpur", "Amravati", "Akola", "Yavatmal",
            "Ratnagiri", "Sindhudurg", "Raigad", "Kolhapur"
    };

    private final String[] seasons = {"Kharif", "Rabi", "Summer / Zaid"};
    private final String[] waterLevels = {"Limited / Borewell", "Assured Irrigation", "Rainfed Monsoon"};
    private final String[] landTypes = {"Plains / Valley", "Lowland", "Sloping / Hilly"};
    private final String[] previousCrops = {"Cotton", "Soybean", "Sugarcane", "Wheat", "Chickpea / Gram", "None"};
    private final String[] budgetLevels = {"Medium", "Low", "High"};
    private final String[] farmerGoals = {"Balanced Yield & Profit", "Maximum Profit", "Minimum Water Risk"};
    private final String[] soilTypes = {"Any Soil (Optional)", "Medium Black", "Deep Black Cotton", "Red Lateritic", "Sandy Loam", "Alluvial"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCropPlannerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Context ctx = requireContext();
        repository = CropPlanRepository.getInstance(ctx);
        sessionManager = new SessionManager(ctx);

        setupSpinners();
        setupToggleGroup();
        setupRecyclerView();

        binding.btnRunSimulation.setOnClickListener(v -> executeSimulation());

        // Default to Simulator
        binding.toggleViewMode.check(R.id.btnTabSimulator);
    }

    private void setupSpinners() {
        Context ctx = requireContext();
        binding.spnDistrict.setAdapter(new ArrayAdapter<>(ctx, android.R.layout.simple_spinner_dropdown_item, districts));
        binding.spnSeason.setAdapter(new ArrayAdapter<>(ctx, android.R.layout.simple_spinner_dropdown_item, seasons));
        binding.spnWater.setAdapter(new ArrayAdapter<>(ctx, android.R.layout.simple_spinner_dropdown_item, waterLevels));
        binding.spnLandType.setAdapter(new ArrayAdapter<>(ctx, android.R.layout.simple_spinner_dropdown_item, landTypes));
        binding.spnPreviousCrop.setAdapter(new ArrayAdapter<>(ctx, android.R.layout.simple_spinner_dropdown_item, previousCrops));
        binding.spnBudget.setAdapter(new ArrayAdapter<>(ctx, android.R.layout.simple_spinner_dropdown_item, budgetLevels));
        binding.spnGoal.setAdapter(new ArrayAdapter<>(ctx, android.R.layout.simple_spinner_dropdown_item, farmerGoals));
        binding.spnSoilType.setAdapter(new ArrayAdapter<>(ctx, android.R.layout.simple_spinner_dropdown_item, soilTypes));
    }

    private void setupToggleGroup() {
        binding.toggleViewMode.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (!isChecked) return;
            if (checkedId == R.id.btnTabSimulator) {
                binding.containerSimulatorView.setVisibility(View.VISIBLE);
                binding.containerSavedPlansView.setVisibility(View.GONE);
            } else if (checkedId == R.id.btnTabSavedPlans) {
                binding.containerSimulatorView.setVisibility(View.GONE);
                binding.containerSavedPlansView.setVisibility(View.VISIBLE);
                loadSavedPlans();
            }
        });
    }

    private void setupRecyclerView() {
        savedPlansAdapter = new SavedCropPlanAdapter(this);
        binding.rvSavedPlans.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvSavedPlans.setAdapter(savedPlansAdapter);
    }

    private void executeSimulation() {
        binding.progressBarSimulator.setVisibility(View.VISIBLE);
        binding.btnRunSimulation.setEnabled(false);

        String district = (String) binding.spnDistrict.getSelectedItem();
        String season = (String) binding.spnSeason.getSelectedItem();
        String waterStr = (String) binding.spnWater.getSelectedItem();
        String water = waterStr.contains("Assured") ? "ASSURED" : (waterStr.contains("Rainfed") ? "RAINFED" : "LIMITED");

        String landStr = (String) binding.spnLandType.getSelectedItem();
        String land = landStr.contains("Lowland") ? "LOWLAND" : (landStr.contains("Sloping") ? "SLOPING" : "PLAINS");

        String prevCrop = (String) binding.spnPreviousCrop.getSelectedItem();
        String budget = ((String) binding.spnBudget.getSelectedItem()).toUpperCase();
        String goalStr = (String) binding.spnGoal.getSelectedItem();
        String goal = goalStr.contains("Profit") && !goalStr.contains("Balanced") ? "MAX_PROFIT" : (goalStr.contains("Water") ? "MIN_RISK" : "BALANCED");

        String soilStr = (String) binding.spnSoilType.getSelectedItem();
        String soil = soilStr.contains("Black Cotton") ? "BLACK_COTTON" :
                (soilStr.contains("Lateritic") ? "RED_LATERITIC" :
                        (soilStr.contains("Sandy") ? "SANDY_LOAM" :
                                (soilStr.contains("Alluvial") ? "ALLUVIAL" : "ANY")));

        boolean scenarioWater = binding.cbScenarioWaterDeficit.isChecked();
        boolean scenarioBudget = binding.cbScenarioBudgetCut.isChecked();
        boolean scenarioEarly = binding.cbScenarioEarlyHarvest.isChecked();
        boolean scenarioDelayed = binding.cbScenarioDelayedSowing.isChecked();

        Map<String, Object> req = new HashMap<>();
        req.put("district", district);
        req.put("season", season);
        req.put("waterAvailability", water);
        req.put("landType", land);
        req.put("previousCrop", prevCrop);
        req.put("budgetLevel", budget);
        req.put("farmerGoal", goal);
        req.put("soilType", soil);
        req.put("scenarioWaterDeficit", scenarioWater);
        req.put("scenarioBudgetCut", scenarioBudget);
        req.put("scenarioEarlyHarvest", scenarioEarly);
        req.put("scenarioDelayedSowing", scenarioDelayed);

        repository.recommendAndSimulate(req, new CropPlanRepository.SimulationCallback() {
            @Override
            public void onSuccess(Map<String, Object> simulationData) {
                if (!isAdded()) return;
                binding.progressBarSimulator.setVisibility(View.GONE);
                binding.btnRunSimulation.setEnabled(true);
                renderSimulationResults(simulationData, district, season, water, land, soil, prevCrop, budget, goal);
            }

            @Override
            public void onError(String error) {
                if (!isAdded()) return;
                binding.progressBarSimulator.setVisibility(View.GONE);
                binding.btnRunSimulation.setEnabled(true);
                Toast.makeText(requireContext(), "Simulation error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void renderSimulationResults(Map<String, Object> data,
                                         String district, String season, String water, String land,
                                         String soil, String prevCrop, String budget, String goal) {
        binding.layoutPlansContainer.removeAllViews();
        binding.tvResultsHeader.setVisibility(View.VISIBLE);

        Object plansObj = data.get("generatedPlans");
        if (!(plansObj instanceof List)) {
            Toast.makeText(requireContext(), "No suitable plan candidates found for these criteria.", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Map<String, Object>> plans = (List<Map<String, Object>>) plansObj;
        LayoutInflater inflater = LayoutInflater.from(requireContext());

        for (Map<String, Object> planMap : plans) {
            View card = inflater.inflate(R.layout.item_crop_plan_card, binding.layoutPlansContainer, false);

            TextView tvBadge = card.findViewById(R.id.tvPlanBadge);
            TextView tvScore = card.findViewById(R.id.tvSuitabilityScore);
            TextView tvCropName = card.findViewById(R.id.tvCropName);
            TextView tvSource = card.findViewById(R.id.tvUniversitySource);
            TextView tvDuration = card.findViewById(R.id.tvDuration);
            TextView tvWater = card.findViewById(R.id.tvWaterDemand);
            TextView tvYield = card.findViewById(R.id.tvTargetYield);
            TextView tvCost = card.findViewById(R.id.tvEstimatedCost);
            TextView tvReasons = card.findViewById(R.id.tvSuitabilityReasons);
            TextView tvRisks = card.findViewById(R.id.tvKeyRisks);
            MaterialButton btnSave = card.findViewById(R.id.btnSavePlan);

            String planTitle = (String) planMap.getOrDefault("planTitle", "Seasonal Plan");
            String planType = (String) planMap.getOrDefault("planType", "BALANCED_BEST_FIT");
            String cropName = (String) planMap.getOrDefault("cropName", "Crop");
            String variety = (String) planMap.getOrDefault("variety", "");
            Number scoreNum = (Number) planMap.getOrDefault("suitabilityScore", 90);
            String duration = (String) planMap.getOrDefault("durationDays", "90-120 Days");
            String waterDemand = (String) planMap.getOrDefault("waterDemand", water);
            String targetYield = (String) planMap.getOrDefault("targetYield", "N/A");
            Number costNum = (Number) planMap.getOrDefault("estimatedCostPerAcre", 15000);
            String reasons = (String) planMap.getOrDefault("suitabilityReason", "");
            String risks = (String) planMap.getOrDefault("keyRisks", "");
            String university = (String) planMap.getOrDefault("universitySource", "ICAR / Maharashtra Agricultural Universities");

            tvBadge.setText(planTitle);
            tvScore.setText(scoreNum.intValue() + "% Fit");
            tvCropName.setText(cropName + (!variety.isEmpty() ? " (" + variety + ")" : ""));
            tvSource.setText(university);
            tvDuration.setText(duration);
            tvWater.setText(waterDemand);
            tvYield.setText(targetYield);
            tvCost.setText("Estimated Production Cost: ₹" + Math.round(costNum.doubleValue()) + " / acre");
            tvReasons.setText(reasons);
            tvRisks.setText(risks);

            btnSave.setOnClickListener(v -> {
                btnSave.setEnabled(false);
                btnSave.setText("Saving Plan...");

                SavedCropPlanModel model = new SavedCropPlanModel();
                model.setId("PLAN-" + System.currentTimeMillis());
                model.setFarmerPhone(sessionManager.getUserPhone());
                model.setFarmerName(sessionManager.getUserName());
                model.setDistrict(district);
                model.setSeason(season);
                model.setCropName(cropName);
                model.setVariety(variety);
                model.setPlanType(planType);
                model.setWaterAvailability(water);
                model.setLandType(land);
                model.setSoilType(soil);
                model.setPreviousCrop(prevCrop);
                model.setInvestmentBudget(budget);
                model.setFarmerGoal(goal);
                model.setDurationDays(duration);
                model.setEstimatedCostPerAcre(costNum.doubleValue());
                model.setTargetYield(targetYield);
                model.setSuitabilityReason(reasons);
                model.setKeyRisks(risks);
                model.setUniversitySource(university);
                model.setIsPlanted(false);

                repository.saveCropPlan(model, new CropPlanRepository.SavePlanCallback() {
                    @Override
                    public void onSuccess(SavedCropPlanModel saved) {
                        if (!isAdded()) return;
                        btnSave.setText("Saved to My Plans ✓");
                        btnSave.setBackgroundColor(0xFF059669);
                        Toast.makeText(requireContext(), "Plan saved successfully! View in 'My Saved Plans'", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(String error) {
                        if (!isAdded()) return;
                        btnSave.setEnabled(true);
                        btnSave.setText("Save This Seasonal Plan");
                        Toast.makeText(requireContext(), "Save failed: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
            });

            binding.layoutPlansContainer.addView(card);
        }

        // Scroll down smoothly to see results
        binding.containerSimulatorView.post(() -> binding.containerSimulatorView.smoothScrollTo(0, binding.tvResultsHeader.getTop()));
    }

    private void loadSavedPlans() {
        String phone = sessionManager.getUserPhone();
        repository.getMyPlans(phone, null, new CropPlanRepository.PlansListCallback() {
            @Override
            public void onLoaded(List<SavedCropPlanModel> plans) {
                if (!isAdded()) return;
                savedPlansAdapter.setPlans(plans);
                binding.tvEmptySavedPlans.setVisibility(plans == null || plans.isEmpty() ? View.VISIBLE : View.GONE);
                binding.rvSavedPlans.setVisibility(plans != null && !plans.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onError(String error) {
                if (!isAdded()) return;
                Toast.makeText(requireContext(), "Error loading plans: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onPlantedToggle(SavedCropPlanModel plan, boolean isPlanted) {
        repository.updatePlantedStatus(plan.getId(), isPlanted, new CropPlanRepository.ActionCallback() {
            @Override
            public void onSuccess() {
                if (!isAdded()) return;
                Toast.makeText(requireContext(), isPlanted ? "Marked as Planted in Field" : "Status updated", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error) {}
        });
    }

    @Override
    public void onDelete(SavedCropPlanModel plan) {
        repository.deletePlan(plan.getId(), new CropPlanRepository.ActionCallback() {
            @Override
            public void onSuccess() {
                if (!isAdded()) return;
                Toast.makeText(requireContext(), "Plan deleted", Toast.LENGTH_SHORT).show();
                loadSavedPlans();
            }

            @Override
            public void onError(String error) {}
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
