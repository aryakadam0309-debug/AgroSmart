package com.agrosmart.activities.farmer;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.agrosmart.activities.BaseActivity;
import com.agrosmart.adapters.CropHarvestAdapter;
import com.agrosmart.databinding.ActivityCropInventoryBinding;
import com.agrosmart.models.CropHarvest;
import com.agrosmart.repository.CropHarvestRepository;
import com.agrosmart.viewmodels.CropHarvestViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CropInventoryActivity extends BaseActivity {

    private ActivityCropInventoryBinding binding;
    private CropHarvestViewModel viewModel;
    private CropHarvestAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCropInventoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        CropHarvestRepository.getInstance(this);
        viewModel = new ViewModelProvider(this).get(CropHarvestViewModel.class);

        binding.toolbarHarvest.setNavigationOnClickListener(v -> finish());

        setupRecyclerView();
        observeViewModel();

        binding.fabAddHarvest.setOnClickListener(v -> showAddHarvestDialog());
    }

    private void setupRecyclerView() {
        adapter = new CropHarvestAdapter(harvest -> {
            new AlertDialog.Builder(this)
                    .setTitle(harvest.getCropName())
                    .setMessage(String.format("Remaining Stock: %.0f Quintals\nStorage: %s\nTarget Price: ₹%,.0f/Qtl",
                            harvest.getStockRemainingQuintals(), harvest.getStorageLocation(), harvest.getExpectedPricePerQtl()))
                    .setPositiveButton("Record Sale (5 Qtl)", (dialog, which) -> {
                        if (harvest.getStockRemainingQuintals() >= 5) {
                            viewModel.sellStock(harvest.getId(), 5.0);
                            Toast.makeText(this, "Sold 5 Quintals! Remaining stock updated.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Not enough remaining stock", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNeutralButton("Delete", (dialog, which) -> {
                        viewModel.removeHarvest(harvest.getId());
                        Toast.makeText(this, "Harvest record removed", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Close", null)
                    .show();
        });

        binding.rvHarvests.setLayoutManager(new LinearLayoutManager(this));
        binding.rvHarvests.setAdapter(adapter);
    }

    private void observeViewModel() {
        viewModel.getHarvests().observe(this, list -> {
            adapter.setItems(list);
            if (list == null || list.isEmpty()) {
                binding.tvEmptyHarvests.setVisibility(View.VISIBLE);
                binding.rvHarvests.setVisibility(View.GONE);
            } else {
                binding.tvEmptyHarvests.setVisibility(View.GONE);
                binding.rvHarvests.setVisibility(View.VISIBLE);
            }
        });
    }

    private void showAddHarvestDialog() {
        EditText etName = new EditText(this);
        etName.setHint("Crop Name (e.g. Wheat, Soybean)");
        EditText etQty = new EditText(this);
        etQty.setHint("Harvested Yield in Quintals (e.g. 50)");
        etQty.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        EditText etStorage = new EditText(this);
        etStorage.setHint("Storage Location (e.g. Farm Shed, APMC)");
        EditText etPrice = new EditText(this);
        etPrice.setHint("Target Selling Price ₹ (e.g. 2800)");
        etPrice.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);

        android.widget.LinearLayout container = new android.widget.LinearLayout(this);
        container.setOrientation(android.widget.LinearLayout.VERTICAL);
        container.setPadding(48, 24, 48, 24);
        container.addView(etName);
        container.addView(etQty);
        container.addView(etStorage);
        container.addView(etPrice);

        new AlertDialog.Builder(this)
                .setTitle("Log New Crop Harvest")
                .setView(container)
                .setPositiveButton("Save Harvest", (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    if (name.isEmpty()) {
                        Toast.makeText(this, "Please enter a valid crop name.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    double qty = 20.0;
                    double price = 2500.0;
                    try { qty = Double.parseDouble(etQty.getText().toString().trim()); } catch (Exception ignored) {}
                    try { price = Double.parseDouble(etPrice.getText().toString().trim()); } catch (Exception ignored) {}

                    if (qty <= 0) {
                        Toast.makeText(this, "Yield quantity must be greater than 0 quintals.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (price < 0) {
                        Toast.makeText(this, "Target price cannot be negative.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String storage = etStorage.getText().toString().trim();
                    if (storage.isEmpty()) storage = "Farm Storage";

                    String id = "HV-" + (System.currentTimeMillis() % 10000);
                    String today = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new Date());

                    CropHarvest h = new CropHarvest(id, name, "Grade A", qty, 0.0, today, storage, price);
                    viewModel.addHarvest(h);
                    Toast.makeText(this, "Harvest record saved successfully!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
