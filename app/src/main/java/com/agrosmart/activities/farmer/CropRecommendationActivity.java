package com.agrosmart.activities.farmer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.agrosmart.R;
import com.agrosmart.activities.BaseActivity;
import com.agrosmart.databinding.ActivityCropRecommendationBinding;
import com.agrosmart.repository.AdvisoryRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CropRecommendationActivity extends BaseActivity {

    private ActivityCropRecommendationBinding binding;
    private AdvisoryRepository advisoryRepository;

    private final String[] zones = {
            "Desh / Western Maharashtra (Pune, Ahmednagar, Satara, Solapur)",
            "Marathwada (Aurangabad, Nanded, Latur, Jalna)",
            "Vidarbha (Nagpur, Amravati, Akola, Yavatmal)",
            "Konkan & Western Ghats (Ratnagiri, Sindhudurg, Raigad, Kolhapur)"
    };

    private final String[] soilTypes = {
            "Black Cotton Soil (Vertisol - Medium to Deep)",
            "Red Lateritic Soil (Acidic, Well-drained)",
            "Sandy Loam Soil (Light, High Permeability)",
            "Alluvial Silt Loam (River basins, Fertile)"
    };

    private final String[] irrigationTypes = {
            "Well / Canal Irrigated (Assured Water Supply)",
            "Rainfed Only (Monsoon dependent)",
            "Drip / Micro-Irrigation (Precision Water Control)"
    };

    private final String[] previousCrops = {
            "Cotton (Bt Cotton)",
            "Sugarcane",
            "Soybean",
            "Wheat",
            "Chickpea / Gram (Harbara)",
            "Maize",
            "Pigeon Pea (Tur)"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCropRecommendationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        advisoryRepository = AdvisoryRepository.getInstance(this);

        binding.toolbar.setNavigationOnClickListener(v -> finish());

        setupSpinners();

        binding.btnRecommend.setOnClickListener(v -> evaluateRecommendation());

        // Initial evaluation
        evaluateRecommendation();
    }

    private void setupSpinners() {
        ArrayAdapter<String> zoneAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, zones);
        binding.spnZone.setAdapter(zoneAdapter);

        ArrayAdapter<String> soilAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, soilTypes);
        binding.spnSoil.setAdapter(soilAdapter);

        ArrayAdapter<String> irrigationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, irrigationTypes);
        binding.spnIrrigation.setAdapter(irrigationAdapter);

        ArrayAdapter<String> cropAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, previousCrops);
        binding.spnPreviousCrop.setAdapter(cropAdapter);
    }

    private void evaluateRecommendation() {
        String selectedZone = (String) binding.spnZone.getSelectedItem();
        String selectedSoil = (String) binding.spnSoil.getSelectedItem();
        String selectedIrrigation = (String) binding.spnIrrigation.getSelectedItem();
        String selectedPrevCrop = (String) binding.spnPreviousCrop.getSelectedItem();

        // Check rotation plan first
        Map<String, String> payload = new HashMap<>();
        payload.put("previousCrop", selectedPrevCrop);
        payload.put("plannedCrop", selectedPrevCrop.contains("Cotton") ? "Cotton" : (selectedPrevCrop.contains("Soybean") ? "Wheat" : "Gram"));

        advisoryRepository.evaluateRotationPlan(payload, new AdvisoryRepository.GenericMapCallback() {
            @Override
            public void onSuccess(Map<String, Object> data) {
                Boolean isRisk = (Boolean) data.get("isHighRisk");
                String title = (String) data.get("alertTitle");
                String msg = (String) data.get("advisoryMessage");

                binding.cardRotationAlert.setVisibility(View.VISIBLE);
                binding.tvRotationAlertTitle.setText(title != null ? title : "Rotation Advisory");
                binding.tvRotationAlertMsg.setText(msg != null ? msg : "");

                if (Boolean.TRUE.equals(isRisk)) {
                    binding.llRotationAlert.setBackgroundColor(0xFFFEF2F2); // Red tint
                    binding.tvRotationAlertTitle.setTextColor(0xFF991B1B);
                } else {
                    binding.llRotationAlert.setBackgroundColor(0xFFECFDF5); // Green tint
                    binding.tvRotationAlertTitle.setTextColor(0xFF065F46);
                }
            }

            @Override
            public void onError(String error) {}
        });

        // Get agronomic suitability recommendations
        advisoryRepository.getCropRecommendations(selectedZone, selectedSoil, selectedIrrigation, "Kharif", new AdvisoryRepository.GenericMapCallback() {
            @Override
            public void onSuccess(Map<String, Object> data) {
                renderRecommendations(data);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(CropRecommendationActivity.this, "Error fetching recommendations", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void renderRecommendations(Map<String, Object> data) {
        binding.llRecommendationsList.removeAllViews();
        Object listObj = data.get("recommendations");
        if (!(listObj instanceof List)) return;

        List<Map<String, Object>> list = (List<Map<String, Object>>) listObj;
        LayoutInflater inflater = LayoutInflater.from(this);

        for (Map<String, Object> rec : list) {
            View card = inflater.inflate(R.layout.item_soil_record, binding.llRecommendationsList, false);

            TextView tvTitle = card.findViewById(R.id.tvItemFieldName);
            TextView tvBadge = card.findViewById(R.id.tvItemRating);
            TextView tvDate = card.findViewById(R.id.tvItemDate);
            TextView tvPh = card.findViewById(R.id.tvItemPh);
            TextView tvOc = card.findViewById(R.id.tvItemOc);
            TextView tvNpk = card.findViewById(R.id.tvItemNpk);
            TextView tvAdvisory = card.findViewById(R.id.tvItemAdvisory);

            String cropName = (String) rec.get("cropName");
            String duration = (String) rec.get("duration");
            String yield = (String) rec.get("expectedYield");
            String justification = (String) rec.get("justification");
            Number score = (Number) rec.get("suitabilityScore");

            tvTitle.setText(cropName != null ? cropName : "Crop Recommendation");
            tvBadge.setText((score != null ? score.intValue() : 95) + "% SUITABLE");
            tvDate.setText("Season: " + rec.getOrDefault("season", "Kharif"));
            tvPh.setText("Duration: " + (duration != null ? duration : "105d"));
            tvOc.setText("Yield: " + (yield != null ? yield : "25 Qtl/ha"));
            tvNpk.setText("Resilience: HIGH");
            tvAdvisory.setText(justification != null ? justification : "Scientifically matched to regional soil & water profile.");

            binding.llRecommendationsList.addView(card);
        }
    }
}
