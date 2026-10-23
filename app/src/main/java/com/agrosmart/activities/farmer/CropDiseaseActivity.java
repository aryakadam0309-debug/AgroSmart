package com.agrosmart.activities.farmer;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.agrosmart.R;
import com.agrosmart.activities.BaseActivity;
import com.agrosmart.adapters.CropDiseaseAdapter;
import com.agrosmart.databinding.ActivityCropDiseaseBinding;
import com.agrosmart.models.CropDisease;
import com.agrosmart.viewmodels.CropDiseaseViewModel;
import com.google.android.material.chip.Chip;

public class CropDiseaseActivity extends BaseActivity {

    private ActivityCropDiseaseBinding binding;
    private CropDiseaseViewModel viewModel;
    private CropDiseaseAdapter adapter;

    private final String[] regions = {
            "All Maharashtra (All Prevalent Diseases)",
            "Western Maharashtra (Pune, Satara, Solapur, Nashik, Kolhapur)",
            "Marathwada (Chhatrapati Sambhajinagar, Latur, Nanded, Jalna)",
            "Vidarbha (Nagpur, Akola, Amravati, Yavatmal, Wardha)",
            "Konkan (Ratnagiri, Sindhudurg, Raigad, Palghar)"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCropDiseaseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(CropDiseaseViewModel.class);

        setupToolbar();
        setupRegionSpinner();
        setupRecyclerView();
        setupFilters();
        observeViewModel();
    }

    private void setupToolbar() {
        binding.toolbarDisease.setNavigationOnClickListener(v -> finish());
    }

    private void setupRegionSpinner() {
        ArrayAdapter<String> regionAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, regions);
        binding.spnDiseaseRegion.setAdapter(regionAdapter);

        binding.spnDiseaseRegion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedRegion = (String) binding.spnDiseaseRegion.getSelectedItem();
                viewModel.filterByRegion(selectedRegion);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupRecyclerView() {
        adapter = new CropDiseaseAdapter(this::openDiseaseDetail);
        binding.rvCropDiseases.setLayoutManager(new LinearLayoutManager(this));
        binding.rvCropDiseases.setAdapter(adapter);

        binding.swipeRefreshDisease.setColorSchemeResources(R.color.forest_green_primary);
        binding.swipeRefreshDisease.setOnRefreshListener(() -> viewModel.refresh());
    }

    private void setupFilters() {
        binding.etSearchDisease.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.search(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        binding.chipGroupCropFilter.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                viewModel.filterByCrop("All Crops");
                return;
            }
            int checkedId = checkedIds.get(0);
            Chip chip = findViewById(checkedId);
            if (chip != null) {
                viewModel.filterByCrop(chip.getText().toString());
            }
        });
    }

    private void observeViewModel() {
        viewModel.getDiseases().observe(this, list -> {
            adapter.setItems(list);
            if (list == null || list.isEmpty()) {
                binding.tvEmptyDisease.setVisibility(View.VISIBLE);
                binding.rvCropDiseases.setVisibility(View.GONE);
            } else {
                binding.tvEmptyDisease.setVisibility(View.GONE);
                binding.rvCropDiseases.setVisibility(View.VISIBLE);
            }
        });

        viewModel.getIsLoading().observe(this, loading -> {
            binding.swipeRefreshDisease.setRefreshing(loading != null && loading);
        });
    }

    private void openDiseaseDetail(CropDisease disease) {
        Intent intent = new Intent(this, CropDiseaseDetailActivity.class);
        intent.putExtra("EXTRA_DISEASE", disease);
        startActivity(intent);
    }
}
