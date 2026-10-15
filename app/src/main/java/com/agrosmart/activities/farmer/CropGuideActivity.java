package com.agrosmart.activities.farmer;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.agrosmart.R;
import com.agrosmart.adapters.CropGuideAdapter;
import com.agrosmart.databinding.ActivityCropGuideBinding;
import com.agrosmart.models.CropGuide;
import com.agrosmart.viewmodels.CropGuideViewModel;
import com.google.android.material.chip.Chip;

public class CropGuideActivity extends AppCompatActivity {

    private ActivityCropGuideBinding binding;
    private CropGuideViewModel viewModel;
    private CropGuideAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCropGuideBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(CropGuideViewModel.class);

        setupToolbar();
        setupRecyclerView();
        setupFilters();
        observeViewModel();
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new CropGuideAdapter(this::openGuideDetail);
        binding.rvCropGuides.setLayoutManager(new LinearLayoutManager(this));
        binding.rvCropGuides.setAdapter(adapter);

        binding.swipeRefreshGuide.setColorSchemeResources(R.color.forest_green_primary);
        binding.swipeRefreshGuide.setOnRefreshListener(() -> viewModel.refresh());
    }

    private void setupFilters() {
        binding.etSearchGuide.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.search(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        binding.chipGroupSeason.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                viewModel.filterBySeason("All Seasons");
                return;
            }
            int checkedId = checkedIds.get(0);
            Chip chip = findViewById(checkedId);
            if (chip != null) {
                viewModel.filterBySeason(chip.getText().toString());
            }
        });
    }

    private void observeViewModel() {
        viewModel.getGuides().observe(this, list -> {
            adapter.setItems(list);
            if (list == null || list.isEmpty()) {
                binding.tvEmptyGuide.setVisibility(View.VISIBLE);
                binding.rvCropGuides.setVisibility(View.GONE);
            } else {
                binding.tvEmptyGuide.setVisibility(View.GONE);
                binding.rvCropGuides.setVisibility(View.VISIBLE);
            }
        });

        viewModel.getIsLoading().observe(this, loading -> {
            binding.swipeRefreshGuide.setRefreshing(loading != null && loading);
        });
    }

    private void openGuideDetail(CropGuide guide) {
        Intent intent = new Intent(this, CropGuideDetailActivity.class);
        intent.putExtra("EXTRA_CROP_GUIDE", guide);
        startActivity(intent);
    }
}
