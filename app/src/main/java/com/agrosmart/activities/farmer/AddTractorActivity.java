package com.agrosmart.activities.farmer;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.agrosmart.database.AgroSmartDbHelper;
import com.agrosmart.databinding.ActivityAddTractorBinding;
import com.agrosmart.models.Tractor;
import com.agrosmart.repository.TractorRepository;
import com.agrosmart.utils.LocaleHelper;
import com.agrosmart.utils.SessionManager;
import com.agrosmart.utils.ValidationUtils;

public class AddTractorActivity extends AppCompatActivity {

    private ActivityAddTractorBinding binding;
    private SessionManager sessionManager;
    private AgroSmartDbHelper dbHelper;

    @Override
    protected void attachBaseContext(Context newBase) {
        SessionManager session = new SessionManager(newBase);
        super.attachBaseContext(LocaleHelper.setLocale(newBase, session.getLanguage()));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddTractorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);
        dbHelper = AgroSmartDbHelper.getInstance(this);

        binding.toolbarAddTractor.setNavigationOnClickListener(v -> finish());

        // Pre-fill owner contact from session
        String userPhone = sessionManager.getUserPhone();
        if (!TextUtils.isEmpty(userPhone)) {
            binding.etOwnerPhone.setText(ValidationUtils.normalizePhone(userPhone));
        }

        binding.btnSubmitTractor.setOnClickListener(v -> submitTractorListing());
    }

    private void submitTractorListing() {
        String brand = binding.etBrand.getText() != null ? binding.etBrand.getText().toString().trim() : "";
        String modelName = binding.etModelName.getText() != null ? binding.etModelName.getText().toString().trim() : "";
        String hp = binding.etHp.getText() != null ? binding.etHp.getText().toString().trim() : "";
        String regNo = binding.etRegNo.getText() != null ? binding.etRegNo.getText().toString().trim().toUpperCase() : "";
        String hourlyRateStr = binding.etHourlyRate.getText() != null ? binding.etHourlyRate.getText().toString().trim() : "";
        String dailyRateStr = binding.etDailyRate.getText() != null ? binding.etDailyRate.getText().toString().trim() : "";
        String implementsInc = binding.etImplements.getText() != null ? binding.etImplements.getText().toString().trim() : "";
        String location = binding.etLocation.getText() != null ? binding.etLocation.getText().toString().trim() : "";
        String ownerPhone = binding.etOwnerPhone.getText() != null ? binding.etOwnerPhone.getText().toString().trim() : "";

        // 1. Validation
        if (!ValidationUtils.isValidText(brand)) {
            binding.tilBrand.setError("Please enter brand (e.g. Mahindra, Swaraj)");
            return;
        } else {
            binding.tilBrand.setError(null);
        }

        if (!ValidationUtils.isValidText(modelName)) {
            binding.tilModelName.setError("Please enter model name");
            return;
        } else {
            binding.tilModelName.setError(null);
        }

        if (TextUtils.isEmpty(hp)) {
            binding.tilHp.setError("Enter HP (e.g. 50 HP)");
            return;
        } else {
            binding.tilHp.setError(null);
        }

        if (!ValidationUtils.isValidPositiveNumber(hourlyRateStr)) {
            binding.tilHourlyRate.setError("Enter valid hourly rate (> ₹0)");
            return;
        } else {
            binding.tilHourlyRate.setError(null);
        }

        if (!ValidationUtils.isValidPositiveNumber(dailyRateStr)) {
            binding.tilDailyRate.setError("Enter valid daily rate (> ₹0)");
            return;
        } else {
            binding.tilDailyRate.setError(null);
        }

        if (!ValidationUtils.isValidText(location)) {
            binding.tilLocation.setError("Please enter equipment location/village");
            return;
        } else {
            binding.tilLocation.setError(null);
        }

        if (!ValidationUtils.isValidPhone(ownerPhone)) {
            binding.tilOwnerPhone.setError("Enter valid 10-digit Indian mobile number");
            return;
        } else {
            binding.tilOwnerPhone.setError(null);
        }

        double hourlyRate = Double.parseDouble(hourlyRateStr);
        double dailyRate = Double.parseDouble(dailyRateStr);

        String fullModel = brand + " " + modelName;
        String tractorId = "TR-" + (System.currentTimeMillis() % 10000);
        String ownerName = sessionManager.getUserName();
        if (TextUtils.isEmpty(ownerName)) {
            ownerName = "Farmer Owner";
        }

        if (TextUtils.isEmpty(implementsInc)) {
            implementsInc = "Rotavator, Cultivator, Trolley";
        }

        if (!hp.toLowerCase().contains("hp")) {
            hp = hp + " HP";
        }

        Tractor tractor = new Tractor(
                tractorId,
                fullModel,
                brand,
                hp,
                hourlyRate,
                dailyRate,
                implementsInc,
                ownerName,
                ownerPhone,
                location,
                true,
                5.0f
        );

        // Save to in-memory repository
        TractorRepository.getInstance().addTractor(tractor);

        // Save to SQLite local database
        dbHelper.addTractor(tractor);

        Toast.makeText(this, "Tractor listed successfully for rental!", Toast.LENGTH_LONG).show();
        finish();
    }
}
