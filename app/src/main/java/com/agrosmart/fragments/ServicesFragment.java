package com.agrosmart.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.agrosmart.databinding.FragmentServicesBinding;

public class ServicesFragment extends Fragment {

    private FragmentServicesBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentServicesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.cardIotService.setOnClickListener(v -> openTarget("IoT Smart Farming", "com.agrosmart.activities.farmer.IotDashboardActivity"));
        binding.cardSoilHealthService.setOnClickListener(v -> openTarget("Soil Health Advisor", "com.agrosmart.activities.farmer.SoilHealthActivity"));
        binding.cardFarmExpensesService.setOnClickListener(v -> openTarget("Farm Expenses & Profitability", "com.agrosmart.activities.farmer.FarmExpensesActivity"));
        binding.cardCropRecommendationService.setOnClickListener(v -> openTarget("Crop Recommendations", "com.agrosmart.activities.farmer.CropRecommendationActivity"));
        binding.cardFertilizerService.setOnClickListener(v -> openTarget("Fertilizer Inventory", "com.agrosmart.activities.farmer.FertilizerInventoryActivity"));
        binding.cardCropInventoryService.setOnClickListener(v -> openTarget("Crop Inventory", "com.agrosmart.activities.farmer.CropInventoryActivity"));
        binding.cardReportDiseaseService.setOnClickListener(v -> openTarget("Report Disease Outbreak", "com.agrosmart.activities.farmer.DiseaseReportActivity"));
        binding.cardFeedbackService.setOnClickListener(v -> openTarget("Share App Feedback", "com.agrosmart.activities.farmer.FarmerFeedbackActivity"));
    }

    private void openTarget(String title, String className) {
        try {
            Class<?> target = Class.forName(className);
            startActivity(new Intent(requireContext(), target));
        } catch (ClassNotFoundException e) {
            Toast.makeText(requireContext(), title + " module ready in upcoming phase", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
