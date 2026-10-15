package com.agrosmart.activities.farmer;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.agrosmart.R;
import com.agrosmart.adapters.TractorAdapter;
import com.agrosmart.databinding.ActivityTractorListBinding;
import com.agrosmart.models.Tractor;
import com.agrosmart.viewmodels.TractorViewModel;
import com.google.android.material.chip.Chip;

public class TractorListActivity extends AppCompatActivity {

    private ActivityTractorListBinding binding;
    private TractorViewModel viewModel;
    private TractorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTractorListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(TractorViewModel.class);

        binding.toolbarTractors.setNavigationOnClickListener(v -> finish());
        binding.fabAddTractor.setOnClickListener(v -> {
            startActivity(new Intent(TractorListActivity.this, AddTractorActivity.class));
        });

        setupRecyclerView();
        setupFilters();
        observeViewModel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (viewModel != null) {
            viewModel.loadTractors();
        }
    }

    private void setupRecyclerView() {
        adapter = new TractorAdapter(this::openBookingScreen);
        binding.rvTractors.setLayoutManager(new LinearLayoutManager(this));
        binding.rvTractors.setAdapter(adapter);

        binding.swipeRefreshTractors.setColorSchemeResources(R.color.forest_green_primary);
        binding.swipeRefreshTractors.setOnRefreshListener(() -> viewModel.loadTractors());
    }

    private void setupFilters() {
        binding.chipGroupBrand.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                viewModel.filterByBrand("All Brands");
                return;
            }
            int id = checkedIds.get(0);
            Chip chip = findViewById(id);
            if (chip != null) {
                viewModel.filterByBrand(chip.getText().toString());
            }
        });
    }

    private void observeViewModel() {
        viewModel.getTractors().observe(this, list -> {
            adapter.setItems(list);
        });

        viewModel.getIsLoading().observe(this, loading -> {
            binding.swipeRefreshTractors.setRefreshing(loading != null && loading);
        });
    }

    private void openBookingScreen(Tractor tractor) {
        Intent intent = new Intent(this, BookTractorActivity.class);
        intent.putExtra("EXTRA_TRACTOR", tractor);
        startActivity(intent);
    }
}
