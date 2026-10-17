package com.agrosmart.activities.farmer;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.agrosmart.R;
import com.agrosmart.activities.BaseActivity;
import com.agrosmart.adapters.CropPriceAdapter;
import com.agrosmart.databinding.ActivityCropPriceBinding;
import com.agrosmart.models.CropPrice;
import com.agrosmart.viewmodels.CropPriceViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;

public class CropPriceActivity extends BaseActivity {

    private ActivityCropPriceBinding binding;
    private CropPriceViewModel viewModel;
    private CropPriceAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCropPriceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(CropPriceViewModel.class);

        setupToolbar();
        setupRecyclerView();
        setupSearchAndFilters();
        observeViewModel();
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new CropPriceAdapter(this::showPriceDetailDialog);
        binding.rvCropPrices.setLayoutManager(new LinearLayoutManager(this));
        binding.rvCropPrices.setAdapter(adapter);

        binding.swipeRefresh.setColorSchemeResources(R.color.forest_green_primary);
        binding.swipeRefresh.setOnRefreshListener(() -> viewModel.refresh());
    }

    private void setupSearchAndFilters() {
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.search(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        binding.chipGroupStates.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                viewModel.filterByState("All States");
                return;
            }
            int checkedId = checkedIds.get(0);
            Chip chip = findViewById(checkedId);
            if (chip != null) {
                viewModel.filterByState(chip.getText().toString());
            }
        });
    }

    private void observeViewModel() {
        viewModel.getCropPrices().observe(this, prices -> {
            adapter.setItems(prices);
            if (prices == null || prices.isEmpty()) {
                binding.tvEmptyState.setVisibility(View.VISIBLE);
                binding.rvCropPrices.setVisibility(View.GONE);
            } else {
                binding.tvEmptyState.setVisibility(View.GONE);
                binding.rvCropPrices.setVisibility(View.VISIBLE);
            }
        });

        viewModel.getIsLoading().observe(this, loading -> {
            binding.swipeRefresh.setRefreshing(loading != null && loading);
        });
    }

    private void showPriceDetailDialog(CropPrice price) {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_crop_price_detail, null);
        dialog.setContentView(dialogView);

        TextView tvName = dialogView.findViewById(R.id.dialogCropName);
        TextView tvMarket = dialogView.findViewById(R.id.dialogMarketLocation);
        TextView tvMin = dialogView.findViewById(R.id.dialogMinPrice);
        TextView tvModal = dialogView.findViewById(R.id.dialogModalPrice);
        TextView tvMax = dialogView.findViewById(R.id.dialogMaxPrice);
        TextView tvAdvisory = dialogView.findViewById(R.id.dialogAdvisory);
        View btnClose = dialogView.findViewById(R.id.btnCloseDialog);

        tvName.setText(price.getCropName() + " (" + price.getVariety() + ")");
        tvMarket.setText("📍 " + price.getMarketName() + ", " + price.getState() + " • " + price.getArrivalQuantity());
        tvMin.setText(String.format("₹%,.0f", price.getMinPrice()));
        tvModal.setText(String.format("₹%,.0f", price.getModalPrice()));
        tvMax.setText(String.format("₹%,.0f", price.getMaxPrice()));

        if ("UP".equalsIgnoreCase(price.getTrend())) {
            tvAdvisory.setText("📈 Bullish Demand: Prices increased by ₹" + price.getPriceChange() + " today. Good window to liquidate stock at peak APMC rates.");
        } else if ("DOWN".equalsIgnoreCase(price.getTrend())) {
            tvAdvisory.setText("📉 High Arrivals: Heavy influx of stock resulted in slight price softening of ₹" + Math.abs(price.getPriceChange()) + ". Consider holding if storage is available.");
        } else {
            tvAdvisory.setText("⚖️ Stable Market: Balanced demand and supply across APMC yards. Prices holding steady around ₹" + price.getModalPrice() + ".");
        }

        btnClose.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}
