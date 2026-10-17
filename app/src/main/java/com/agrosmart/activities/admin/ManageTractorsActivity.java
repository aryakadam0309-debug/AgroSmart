package com.agrosmart.activities.admin;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.agrosmart.R;
import com.agrosmart.activities.BaseActivity;
import com.agrosmart.adapters.TractorAdapter;
import com.agrosmart.database.AgroSmartDbHelper;
import com.agrosmart.databinding.ActivityManageTractorsBinding;
import com.agrosmart.models.Tractor;
import com.agrosmart.repository.TractorRepository;
import com.agrosmart.utils.Constants;
import com.agrosmart.utils.SessionManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.UUID;

public class ManageTractorsActivity extends BaseActivity {

    private ActivityManageTractorsBinding binding;
    private TractorAdapter adapter;
    private TractorRepository repository;
    private AgroSmartDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityManageTractorsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SessionManager sessionManager = new SessionManager(this);
        if (!Constants.ROLE_ADMIN.equals(sessionManager.getUserRole())) {
            Toast.makeText(this, "Access Denied: Administrator role required.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        repository = TractorRepository.getInstance(this);
        dbHelper = AgroSmartDbHelper.getInstance(this);

        binding.toolbar.setNavigationOnClickListener(v -> finish());

        setupRecyclerView();
        loadFleet();

        binding.fabAddTractor.setOnClickListener(v -> showAddTractorDialog());
    }

    private void setupRecyclerView() {
        adapter = new TractorAdapter(tractor -> {
            new MaterialAlertDialogBuilder(this)
                    .setTitle(tractor.getModelName())
                    .setMessage("Owner: " + tractor.getOwnerName() + "\nPhone: " + tractor.getOwnerPhone() + "\nRate: ₹" + tractor.getHourlyRate() + "/hr\nLocation: " + tractor.getLocation())
                    .setPositiveButton("Close", null)
                    .show();
        });
        binding.rvTractors.setLayoutManager(new LinearLayoutManager(this));
        binding.rvTractors.setAdapter(adapter);
    }

    private void loadFleet() {
        repository.getTractors("All Brands").observe(this, list -> {
            if (list != null) {
                adapter.setItems(list);
                binding.tvCountHeader.setText("Registered Tractor Fleet (" + list.size() + ")");
            }
        });
    }

    private void showAddTractorDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_tractor, null);

        EditText etModel = dialogView.findViewById(R.id.etModelName);
        EditText etBrand = dialogView.findViewById(R.id.etBrand);
        EditText etHp = dialogView.findViewById(R.id.etHp);
        EditText etHourly = dialogView.findViewById(R.id.etHourlyRate);
        EditText etDaily = dialogView.findViewById(R.id.etDailyRate);
        EditText etImplements = dialogView.findViewById(R.id.etImplements);
        EditText etOwner = dialogView.findViewById(R.id.etOwnerName);
        EditText etPhone = dialogView.findViewById(R.id.etOwnerPhone);
        EditText etLoc = dialogView.findViewById(R.id.etLocation);

        new MaterialAlertDialogBuilder(this)
                .setTitle("Register New Tractor")
                .setView(dialogView)
                .setPositiveButton("Register", (dialog, which) -> {
                    String model = etModel.getText().toString().trim();
                    String brand = etBrand.getText().toString().trim();
                    String hp = etHp.getText().toString().trim();
                    String hourlyStr = etHourly.getText().toString().trim();
                    String dailyStr = etDaily.getText().toString().trim();
                    String impl = etImplements.getText().toString().trim();
                    String owner = etOwner.getText().toString().trim();
                    String phone = etPhone.getText().toString().trim();
                    String loc = etLoc.getText().toString().trim();

                    if (model.isEmpty() || brand.isEmpty() || hourlyStr.isEmpty() || owner.isEmpty() || phone.isEmpty()) {
                        Toast.makeText(this, "Model, Brand, Hourly Rate, Owner, and Phone are required", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    double hourly = Double.parseDouble(hourlyStr);
                    double daily = dailyStr.isEmpty() ? hourly * 7.0 : Double.parseDouble(dailyStr);

                    String id = "TR" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
                    Tractor t = new Tractor(
                            id,
                            model,
                            brand,
                            hp.isEmpty() ? "50 HP" : hp,
                            hourly,
                            daily,
                            impl.isEmpty() ? "Rotavator, Cultivator, Trolley" : impl,
                            owner,
                            phone,
                            loc.isEmpty() ? "Pune, Maharashtra" : loc,
                            true,
                            5.0f
                    );

                    repository.addTractor(t);
                    dbHelper.insertTractorDirect(dbHelper.getWritableDatabase(), id, model, brand, hp, hourly, daily, impl, owner, phone, loc, 1, 5.0);
                    loadFleet();
                    Toast.makeText(this, "Tractor successfully added to rental fleet!", Toast.LENGTH_LONG).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
