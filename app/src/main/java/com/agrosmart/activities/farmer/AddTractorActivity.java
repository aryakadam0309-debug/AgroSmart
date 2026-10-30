package com.agrosmart.activities.farmer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;

import com.agrosmart.activities.BaseActivity;
import com.agrosmart.databinding.ActivityAddTractorBinding;
import com.agrosmart.models.Tractor;
import com.agrosmart.repository.TractorRepository;
import com.agrosmart.utils.SessionManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

public class AddTractorActivity extends BaseActivity {

    private ActivityAddTractorBinding binding;
    private TractorRepository repository;
    private SessionManager sessionManager;
    private FusedLocationProviderClient fusedLocationClient;

    private ActivityResultLauncher<String> locationPermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddTractorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = new TractorRepository(this);
        sessionManager = new SessionManager(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Populate owner contact info from session
        String sessionName = sessionManager.getUserName();
        String sessionPhone = sessionManager.getUserPhone();
        if (sessionName != null && !sessionName.isEmpty()) {
            binding.etOwnerName.setText(sessionName);
        }
        if (sessionPhone != null && !sessionPhone.isEmpty()) {
            binding.etOwnerPhone.setText(sessionPhone);
        }

        initLocationPermissionLauncher();

        binding.toolbarAddTractor.setNavigationOnClickListener(v -> finish());
        binding.btnSubmitListing.setOnClickListener(v -> submitTractorListing());
        binding.btnDetectLocation.setOnClickListener(v -> checkAndDetectLiveLocation());

        // Auto-detect live GPS location on opening screen
        checkAndDetectLiveLocation();
    }

    private void initLocationPermissionLauncher() {
        locationPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        fetchLiveGpsCoordinates();
                    } else {
                        Toast.makeText(this, "Location permission not granted. You can type village & district manually.", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void checkAndDetectLiveLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fetchLiveGpsCoordinates();
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private void fetchLiveGpsCoordinates() {
        Toast.makeText(this, "Detecting your live GPS coordinates...", Toast.LENGTH_SHORT).show();

        try {
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    applyGpsToForm(location);
                }
            });

            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            applyGpsToForm(location);
                        }
                    });
        } catch (SecurityException ignored) {}
    }

    private void applyGpsToForm(Location location) {
        double lat = location.getLatitude();
        double lon = location.getLongitude();

        binding.etLatitude.setText(String.format(Locale.US, "%.4f", lat));
        binding.etLongitude.setText(String.format(Locale.US, "%.4f", lon));

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                Geocoder geocoder = new Geocoder(AddTractorActivity.this, Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
                if (addresses != null && !addresses.isEmpty()) {
                    Address a = addresses.get(0);
                    String locality = a.getLocality() != null ? a.getLocality() : a.getSubLocality();
                    String subAdmin = a.getSubAdminArea() != null ? a.getSubAdminArea() : a.getAdminArea();
                    String state = a.getAdminArea() != null ? a.getAdminArea() : "Maharashtra";

                    runOnUiThread(() -> {
                        if (locality != null && !locality.isEmpty()) {
                            binding.etVillage.setText(locality);
                        }
                        if (subAdmin != null && !subAdmin.isEmpty()) {
                            binding.etDistrict.setText(subAdmin);
                        }
                        if (state != null && !state.isEmpty()) {
                            binding.etState.setText(state);
                        }
                        Toast.makeText(AddTractorActivity.this,
                                "Live location applied: " + (locality != null ? locality : "") + " (" + String.format(Locale.US, "%.4f, %.4f", lat, lon) + ")",
                                Toast.LENGTH_SHORT).show();
                    });
                }
            } catch (Exception ignored) {}
        });
    }

    private void submitTractorListing() {
        String model = binding.etModelName.getText() != null ? binding.etModelName.getText().toString().trim() : "";
        if (model.isEmpty()) {
            binding.etModelName.setError("Model name is required");
            return;
        }

        String brand = binding.etBrand.getText() != null ? binding.etBrand.getText().toString().trim() : "Mahindra";
        String hp = binding.etHp.getText() != null ? binding.etHp.getText().toString().trim() : "45 HP";
        String implementsInc = binding.etImplements.getText() != null ? binding.etImplements.getText().toString().trim() : "Standard Hitch";
        String village = binding.etVillage.getText() != null ? binding.etVillage.getText().toString().trim() : "Local Farm";
        String district = binding.etDistrict.getText() != null ? binding.etDistrict.getText().toString().trim() : "Maharashtra";
        String state = binding.etState.getText() != null ? binding.etState.getText().toString().trim() : "Maharashtra";
        String location = village + (district.isEmpty() ? "" : ", " + district);

        Double latitude = null;
        Double longitude = null;
        try {
            if (binding.etLatitude.getText() != null && !binding.etLatitude.getText().toString().trim().isEmpty()) {
                latitude = Double.parseDouble(binding.etLatitude.getText().toString().trim());
            }
            if (binding.etLongitude.getText() != null && !binding.etLongitude.getText().toString().trim().isEmpty()) {
                longitude = Double.parseDouble(binding.etLongitude.getText().toString().trim());
            }
        } catch (Exception ignored) {}

        if (latitude == null || longitude == null) {
            latitude = 17.2892;
            longitude = 74.1818;
        }

        double hourlyRate = 600.0;
        try {
            hourlyRate = Double.parseDouble(binding.etHourlyRate.getText().toString().trim());
        } catch (Exception ignored) {}

        double dailyRate = hourlyRate * 8.0;
        try {
            if (binding.etDailyRate.getText() != null && !binding.etDailyRate.getText().toString().trim().isEmpty()) {
                dailyRate = Double.parseDouble(binding.etDailyRate.getText().toString().trim());
            }
        } catch (Exception ignored) {}

        String id = "TR-" + (System.currentTimeMillis() % 100000);
        String ownerName = binding.etOwnerName.getText() != null ? binding.etOwnerName.getText().toString().trim() : "";
        if (ownerName.isEmpty()) {
            ownerName = sessionManager.getUserName() != null ? sessionManager.getUserName() : "Tractor Owner";
        }

        String ownerPhone = binding.etOwnerPhone.getText() != null ? binding.etOwnerPhone.getText().toString().trim() : "";
        if (ownerPhone.isEmpty()) {
            ownerPhone = sessionManager.getUserPhone() != null ? sessionManager.getUserPhone() : "9822000000";
        }

        Tractor tractor = new Tractor(
                id, model, brand, hp, hourlyRate, dailyRate, implementsInc,
                ownerName, ownerPhone, location, true, 4.9f
        );
        tractor.setVillage(village);
        tractor.setDistrict(district);
        tractor.setState(state);
        tractor.setLatitude(latitude);
        tractor.setLongitude(longitude);
        tractor.setOwnerPaymentName(ownerName);
        tractor.setOwnerUpiId(null);
        tractor.setOwnerUpiVerified(false);
        tractor.setApprovalStatus("APPROVED");

        repository.registerTractor(tractor, new TractorRepository.RepositoryCallback<Tractor>() {
            @Override
            public void onSuccess(Tractor result) {
                Toast.makeText(AddTractorActivity.this,
                        "Equipment listing published successfully! Visible in rental fleet.", Toast.LENGTH_LONG).show();
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onError(String message, int statusCode) {
                Toast.makeText(AddTractorActivity.this, "Listing added locally: " + message, Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            }
        });
    }
}
