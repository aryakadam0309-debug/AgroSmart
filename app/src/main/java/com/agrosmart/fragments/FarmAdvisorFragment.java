package com.agrosmart.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.agrosmart.activities.farmer.CropRecommendationActivity;
import com.agrosmart.activities.farmer.DiseaseReportActivity;
import com.agrosmart.activities.farmer.FarmExpensesActivity;
import com.agrosmart.activities.farmer.SoilHealthActivity;
import com.agrosmart.databinding.FragmentFarmAdvisorBinding;

public class FarmAdvisorFragment extends Fragment {

    private FragmentFarmAdvisorBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFarmAdvisorBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.cardAdvisorySoil.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), SoilHealthActivity.class)));

        binding.cardAdvisoryRotation.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), CropRecommendationActivity.class)));

        binding.cardAdvisoryExpenses.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), FarmExpensesActivity.class)));

        binding.cardAdvisoryDisease.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), DiseaseReportActivity.class)));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
