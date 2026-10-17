package com.agrosmart.activities.admin;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.agrosmart.R;
import com.agrosmart.activities.BaseActivity;
import com.agrosmart.adapters.CropPriceAdapter;
import com.agrosmart.database.AgroSmartDbHelper;
import com.agrosmart.databinding.ActivityManageCropPricesBinding;
import com.agrosmart.models.CropPrice;
import com.agrosmart.repository.CropPriceRepository;
import com.agrosmart.utils.Constants;
import com.agrosmart.utils.SessionManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class ManageCropPricesActivity extends BaseActivity {

    private ActivityManageCropPricesBinding binding;
    private CropPriceAdapter adapter;
    private CropPriceRepository repository;
    private AgroSmartDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityManageCropPricesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SessionManager sessionManager = new SessionManager(this);
        if (!Constants.ROLE_ADMIN.equals(sessionManager.getUserRole())) {
            Toast.makeText(this, "Access Denied: Administrator role required.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        repository = CropPriceRepository.getInstance();
        dbHelper = AgroSmartDbHelper.getInstance(this);

        binding.toolbar.setNavigationOnClickListener(v -> finish());

        setupRecyclerView();
        loadPrices();

        binding.fabAddPrice.setOnClickListener(v -> showAddCropPriceDialog());
    }

    private void setupRecyclerView() {
        adapter = new CropPriceAdapter(price -> showOptionsDialog(price));
        binding.rvCropPrices.setLayoutManager(new LinearLayoutManager(this));
        binding.rvCropPrices.setAdapter(adapter);
    }

    private void loadPrices() {
        List<CropPrice> list = repository.getAllPrices();
        adapter.setItems(list);
        binding.tvCountHeader.setText("Active Market Commodities (" + list.size() + ")");
    }

    private void showOptionsDialog(CropPrice price) {
        String[] options = {"Update Modal Price", "Delete from Mandi", "Cancel"};
        new MaterialAlertDialogBuilder(this)
                .setTitle(price.getCropName() + " (" + price.getMarketName() + ")")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        showEditPriceDialog(price);
                    } else if (which == 1) {
                        deletePrice(price);
                    }
                })
                .show();
    }

    private void showEditPriceDialog(CropPrice price) {
        final EditText input = new EditText(this);
        input.setHint("Enter new modal price (₹ / Qtl)");
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        input.setText(String.valueOf(price.getModalPrice()));

        new MaterialAlertDialogBuilder(this)
                .setTitle("Update Modal Price")
                .setMessage("Update APMC mandi closing rate for " + price.getCropName())
                .setView(input)
                .setPositiveButton("Update", (d, w) -> {
                    String val = input.getText().toString().trim();
                    if (!val.isEmpty()) {
                        try {
                            double newModal = Double.parseDouble(val);
                            double change = newModal - price.getModalPrice();
                            price.setModalPrice(newModal);
                            price.setPriceChange(change);
                            price.setTrend(change > 0 ? "UP" : (change < 0 ? "DOWN" : "STABLE"));
                            price.setUpdatedDate("Today, " + new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date()));

                            dbHelper.insertOrUpdateCropPrice(price);
                            loadPrices();
                            Toast.makeText(this, "Crop rate updated successfully!", Toast.LENGTH_SHORT).show();
                        } catch (NumberFormatException e) {
                            Toast.makeText(this, "Invalid number", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deletePrice(CropPrice price) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Confirm Deletion")
                .setMessage("Are you sure you want to remove " + price.getCropName() + " from " + price.getMarketName() + "?")
                .setPositiveButton("Delete", (d, w) -> {
                    repository.removeCropPrice(price.getId());
                    dbHelper.deleteCropPrice(price.getId());
                    loadPrices();
                    Toast.makeText(this, "Commodity removed from Mandi", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showAddCropPriceDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_crop_price, null);

        EditText etCrop = dialogView.findViewById(R.id.etCropName);
        EditText etCategory = dialogView.findViewById(R.id.etCategory);
        EditText etVariety = dialogView.findViewById(R.id.etVariety);
        EditText etMarket = dialogView.findViewById(R.id.etMarketName);
        EditText etState = dialogView.findViewById(R.id.etState);
        EditText etDistrict = dialogView.findViewById(R.id.etDistrict);
        EditText etMin = dialogView.findViewById(R.id.etMinPrice);
        EditText etMax = dialogView.findViewById(R.id.etMaxPrice);
        EditText etModal = dialogView.findViewById(R.id.etModalPrice);
        EditText etArrivals = dialogView.findViewById(R.id.etArrivals);

        new MaterialAlertDialogBuilder(this)
                .setTitle("Add New Mandi Bhav")
                .setView(dialogView)
                .setPositiveButton("Publish", (dialog, which) -> {
                    String crop = etCrop.getText().toString().trim();
                    String cat = etCategory.getText().toString().trim();
                    String variety = etVariety.getText().toString().trim();
                    String market = etMarket.getText().toString().trim();
                    String state = etState.getText().toString().trim();
                    String district = etDistrict.getText().toString().trim();
                    String minStr = etMin.getText().toString().trim();
                    String maxStr = etMax.getText().toString().trim();
                    String modalStr = etModal.getText().toString().trim();
                    String arrivals = etArrivals.getText().toString().trim();

                    if (crop.isEmpty() || market.isEmpty() || modalStr.isEmpty()) {
                        Toast.makeText(this, "Crop name, Market, and Modal price are required", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    double min = minStr.isEmpty() ? Double.parseDouble(modalStr) * 0.9 : Double.parseDouble(minStr);
                    double max = maxStr.isEmpty() ? Double.parseDouble(modalStr) * 1.1 : Double.parseDouble(maxStr);
                    double modal = Double.parseDouble(modalStr);

                    String id = "CP" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
                    String time = "Today, " + new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());

                    CropPrice newPrice = new CropPrice(
                            id,
                            crop,
                            cat.isEmpty() ? "Vegetables" : cat,
                            variety.isEmpty() ? "Standard" : variety,
                            market,
                            state.isEmpty() ? "Maharashtra" : state,
                            district.isEmpty() ? market : district,
                            min, max, modal,
                            "₹ / Qtl",
                            "UP", 50.0,
                            time,
                            arrivals.isEmpty() ? "2,500 Quintals" : arrivals
                    );

                    repository.addCropPrice(newPrice);
                    dbHelper.insertOrUpdateCropPrice(newPrice);
                    loadPrices();
                    Toast.makeText(this, "New Mandi price published! Farmers can now view it.", Toast.LENGTH_LONG).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
