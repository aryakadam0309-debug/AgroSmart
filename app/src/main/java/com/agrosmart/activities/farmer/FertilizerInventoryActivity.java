package com.agrosmart.activities.farmer;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.agrosmart.R;
import com.agrosmart.adapters.FertilizerAdapter;
import com.agrosmart.databinding.ActivityFertilizerInventoryBinding;
import com.agrosmart.models.FertilizerItem;
import com.agrosmart.viewmodels.FertilizerViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FertilizerInventoryActivity extends AppCompatActivity {

    private ActivityFertilizerInventoryBinding binding;
    private FertilizerViewModel viewModel;
    private FertilizerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFertilizerInventoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(FertilizerViewModel.class);

        binding.toolbarFertilizer.setNavigationOnClickListener(v -> finish());

        setupRecyclerView();
        observeViewModel();

        binding.fabAddFertilizer.setOnClickListener(v -> showAddDialog());
    }

    private void setupRecyclerView() {
        adapter = new FertilizerAdapter(item -> {
            // Options: Update quantity or delete
            new AlertDialog.Builder(this)
                    .setTitle(item.getName())
                    .setMessage(String.format("Current Stock: %d Bags\nUnit Cost: ₹%,.1f", item.getQuantityBags(), item.getCostPerBag()))
                    .setPositiveButton("Use 1 Bag", (dialog, which) -> {
                        if (item.getQuantityBags() > 0) {
                            viewModel.updateQuantity(item.getId(), item.getQuantityBags() - 1);
                            Toast.makeText(this, "Stock deducted (1 bag used)", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNeutralButton("Delete", (dialog, which) -> {
                        viewModel.removeFertilizer(item.getId());
                        Toast.makeText(this, "Fertilizer removed", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Close", null)
                    .show();
        });

        binding.rvFertilizers.setLayoutManager(new LinearLayoutManager(this));
        binding.rvFertilizers.setAdapter(adapter);
    }

    private void observeViewModel() {
        viewModel.getFertilizers().observe(this, list -> {
            adapter.setItems(list);
            if (list == null || list.isEmpty()) {
                binding.tvEmptyFertilizers.setVisibility(View.VISIBLE);
                binding.rvFertilizers.setVisibility(View.GONE);
            } else {
                binding.tvEmptyFertilizers.setVisibility(View.GONE);
                binding.rvFertilizers.setVisibility(View.VISIBLE);
            }
        });
    }

    private void showAddDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_crop_price_detail, null); // Reuse styled dialog layout base or build alert
        EditText etName = new EditText(this);
        etName.setHint("Fertilizer Name (e.g. Urea 46%, Potash)");
        EditText etBags = new EditText(this);
        etBags.setHint("Number of Bags (e.g. 10)");
        etBags.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        EditText etCost = new EditText(this);
        etCost.setHint("Cost per Bag ₹ (e.g. 350)");
        etCost.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);

        android.widget.LinearLayout container = new android.widget.LinearLayout(this);
        container.setOrientation(android.widget.LinearLayout.VERTICAL);
        container.setPadding(48, 24, 48, 24);
        container.addView(etName);
        container.addView(etBags);
        container.addView(etCost);

        new AlertDialog.Builder(this)
                .setTitle("Add Fertilizer Stock")
                .setView(container)
                .setPositiveButton("Save Stock", (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    if (name.isEmpty()) return;
                    int bags = 1;
                    double cost = 300.0;
                    try { bags = Integer.parseInt(etBags.getText().toString().trim()); } catch (Exception ignored) {}
                    try { cost = Double.parseDouble(etCost.getText().toString().trim()); } catch (Exception ignored) {}

                    String id = "FER-" + (System.currentTimeMillis() % 10000);
                    String today = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new Date());
                    FertilizerItem newItem = new FertilizerItem(id, name, "Chemical", bags, 50.0, cost, today, 3);
                    viewModel.addFertilizer(newItem);
                    Toast.makeText(this, "Fertilizer stock added!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
