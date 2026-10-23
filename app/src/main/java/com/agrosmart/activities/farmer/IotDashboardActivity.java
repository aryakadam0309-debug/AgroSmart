package com.agrosmart.activities.farmer;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.agrosmart.R;
import com.agrosmart.activities.BaseActivity;
import com.agrosmart.adapters.SensorReadingAdapter;
import com.agrosmart.databinding.ActivityIotDashboardBinding;
import com.agrosmart.databinding.DialogConfigureThresholdsBinding;
import com.agrosmart.models.IotAlert;
import com.agrosmart.models.IrrigationSetting;
import com.agrosmart.models.PumpCommand;
import com.agrosmart.models.SensorReading;
import com.agrosmart.repository.IotRepository;
import com.agrosmart.utils.SessionManager;
import com.agrosmart.viewmodels.IotViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class IotDashboardActivity extends BaseActivity {

    private ActivityIotDashboardBinding binding;
    private IotViewModel viewModel;
    private SensorReadingAdapter historyAdapter;
    private FusedLocationProviderClient fusedLocationClient;
    private ActivityResultLauncher<String[]> locationPermissionLauncher;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIotDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        viewModel = new ViewModelProvider(this).get(IotViewModel.class);

        locationPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {
                    Boolean fineGranted = result.get(Manifest.permission.ACCESS_FINE_LOCATION);
                    Boolean coarseGranted = result.get(Manifest.permission.ACCESS_COARSE_LOCATION);
                    if ((fineGranted != null && fineGranted) || (coarseGranted != null && coarseGranted)) {
                        detectAndSetGpsLocation();
                    } else {
                        Toast.makeText(this, "GPS permission denied. You can enter farm location manually.", Toast.LENGTH_LONG).show();
                        showManualLocationDialog();
                    }
                }
        );

        setupRecyclerView();
        setupObservers();
        setupListeners();
    }

    private void setupRecyclerView() {
        historyAdapter = new SensorReadingAdapter();
        binding.rvSensorHistory.setLayoutManager(new LinearLayoutManager(this));
        binding.rvSensorHistory.setAdapter(historyAdapter);
    }

    private void setupObservers() {
        // Device Station Details
        viewModel.getDevice().observe(this, device -> {
            if (device != null) {
                binding.tvDeviceName.setText(device.getDeviceName());
                if (device.getFieldLocation() != null) {
                    binding.tvFieldLocation.setText("📍 " + device.getFieldLocation());
                }
            }
        });

        // Online / Offline Status
        viewModel.getIsOnline().observe(this, isOnline -> {
            boolean online = Boolean.TRUE.equals(isOnline);
            if (online) {
                binding.viewStatusDot.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));
                binding.tvConnectionStatus.setText("Station Online (ESP32 Live)");
                binding.tvLastSeen.setText("Last sync: Live");
                binding.cardAlert.setVisibility(View.GONE);
            } else {
                binding.viewStatusDot.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E53935")));
                binding.tvConnectionStatus.setText("Hardware Offline (Waiting for ESP32)");
                SensorReading last = viewModel.getLatestReading().getValue();
                if (last != null && last.getRecordedAt() != null) {
                    binding.tvLastSeen.setText("Last Recorded: " + last.getRecordedAt());
                } else {
                    binding.tvLastSeen.setText("Hardware Offline (Waiting for ESP32)");
                }

                binding.cardAlert.setVisibility(View.VISIBLE);
                binding.tvAlertTitle.setText("⚠️ Station Hardware Offline");
                binding.tvAlertMessage.setText("ESP32 station is not actively connected to Wi-Fi. Showing latest saved telemetry from database.");
            }
        });

        // Latest Sensor Reading (Moisture + DHT22)
        viewModel.getLatestReading().observe(this, reading -> {
            if (reading != null) {
                boolean online = Boolean.TRUE.equals(viewModel.getIsOnline().getValue());
                binding.tvMoistureValue.setText(String.format(Locale.US, "%.1f", reading.getSoilMoisturePct()));
                binding.pbMoisture.setProgress((int) Math.round(reading.getSoilMoisturePct()));
                binding.tvRawSensorAdc.setText("Raw ADC: " + reading.getSoilMoistureRaw());

                String status = reading.getMoistureStatus() != null ? reading.getMoistureStatus().toUpperCase() : "NORMAL";
                binding.tvMoistureStatusChip.setText(online ? status : (status + " (CACHED)"));

                if ("DRY".equals(status)) {
                    binding.tvMoistureStatusChip.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFCDD2")));
                    binding.tvMoistureStatusChip.setTextColor(Color.parseColor("#C62828"));
                    binding.pbMoisture.setProgressTintList(ColorStateList.valueOf(Color.parseColor("#C62828")));
                } else if ("WET".equals(status)) {
                    binding.tvMoistureStatusChip.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#B3E5FC")));
                    binding.tvMoistureStatusChip.setTextColor(Color.parseColor("#0277BD"));
                    binding.pbMoisture.setProgressTintList(ColorStateList.valueOf(Color.parseColor("#0277BD")));
                } else {
                    binding.tvMoistureStatusChip.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#C8E6C9")));
                    binding.tvMoistureStatusChip.setTextColor(Color.parseColor("#2E7D32"));
                    binding.pbMoisture.setProgressTintList(ColorStateList.valueOf(Color.parseColor("#2E7D32")));
                }

                if (reading.getTemperatureC() != null) {
                    binding.tvLiveTemp.setText(String.format(Locale.US, "%.1f°C", reading.getTemperatureC()));
                } else {
                    binding.tvLiveTemp.setText("-- °C");
                }
                if (reading.getHumidityPct() != null) {
                    binding.tvLiveHumidity.setText(String.format(Locale.US, "%.1f%%", reading.getHumidityPct()));
                } else {
                    binding.tvLiveHumidity.setText("-- %");
                }
            }
        });

        // Active Alerts
        viewModel.getAlerts().observe(this, alerts -> {
            if (alerts != null && !alerts.isEmpty()) {
                IotAlert first = alerts.get(0);
                binding.cardAlert.setVisibility(View.VISIBLE);
                String title = first.getTitle() != null ? first.getTitle() : "⚠️ IoT Alert";
                String msg = first.getMessage() != null ? first.getMessage() : "Sensor threshold triggered.";
                binding.tvAlertTitle.setText(title);
                binding.tvAlertMessage.setText(msg);
            } else {
                Boolean isOnline = viewModel.getIsOnline().getValue();
                if (Boolean.TRUE.equals(isOnline)) {
                    binding.cardAlert.setVisibility(View.GONE);
                }
            }
        });

        // History
        viewModel.getHistory().observe(this, list -> {
            if (list != null) {
                historyAdapter.setReadings(list);
                binding.tvRecordCount.setText("Showing " + list.size() + " Readings");
            }
        });

        // Loading indicator
        viewModel.getIsLoading().observe(this, loading -> {
            binding.swipeRefresh.setRefreshing(Boolean.TRUE.equals(loading));
        });
    }

    private void setupListeners() {
        binding.btnBack.setOnClickListener(v -> finish());

        binding.layoutFieldLocation.setOnClickListener(v -> showLocationDialog());

        binding.btnHardwareSetup.setOnClickListener(v -> {
            Intent intent = new Intent(this, IotDeviceSetupActivity.class);
            startActivity(intent);
        });

        binding.swipeRefresh.setOnRefreshListener(() -> viewModel.refresh());

    }

    private void showLocationDialog() {
        String profileLoc = sessionManager.getUserLocation();
        String[] options = {
                "📡 Detect My Live GPS Location",
                "🌾 Use Profile Location (" + (profileLoc != null ? profileLoc : "Not set") + ")",
                "✏️ Enter Farm Location Manually"
        };

        new MaterialAlertDialogBuilder(this)
                .setTitle("Farm & Station Location")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        checkGpsPermissionAndDetect();
                    } else if (which == 1) {
                        if (profileLoc != null && !profileLoc.isEmpty()) {
                            saveFarmLocation(profileLoc);
                        } else {
                            Toast.makeText(this, "No saved profile location. Please enter manually.", Toast.LENGTH_SHORT).show();
                            showManualLocationDialog();
                        }
                    } else if (which == 2) {
                        showManualLocationDialog();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void checkGpsPermissionAndDetect() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            detectAndSetGpsLocation();
        } else {
            locationPermissionLauncher.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
        }
    }

    private void detectAndSetGpsLocation() {
        Toast.makeText(this, "Detecting GPS location...", Toast.LENGTH_SHORT).show();
        try {
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    double lat = location.getLatitude();
                    double lon = location.getLongitude();
                    String placeName = reverseGeocode(lat, lon);

                    new MaterialAlertDialogBuilder(this)
                            .setTitle("Confirm Farm Location")
                            .setMessage("Detected Location:\n\n📍 " + placeName + "\n(GPS: " + String.format(Locale.US, "%.4f, %.4f", lat, lon) + ")\n\nSet this as your farm station location?")
                            .setPositiveButton("Confirm & Save", (dialog, which) -> saveFarmLocation(placeName))
                            .setNegativeButton("Cancel", null)
                            .show();
                } else {
                    Toast.makeText(this, "GPS signal not available. Please ensure Location is enabled in phone settings.", Toast.LENGTH_LONG).show();
                    showManualLocationDialog();
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(this, "GPS detection failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                showManualLocationDialog();
            });
        } catch (SecurityException e) {
            Toast.makeText(this, "Location permission missing", Toast.LENGTH_SHORT).show();
        }
    }

    private String reverseGeocode(double lat, double lon) {
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address addr = addresses.get(0);
                StringBuilder sb = new StringBuilder();
                if (addr.getLocality() != null) {
                    sb.append(addr.getLocality());
                } else if (addr.getSubAdminArea() != null) {
                    sb.append(addr.getSubAdminArea());
                }
                if (addr.getAdminArea() != null) {
                    if (sb.length() > 0) sb.append(", ");
                    sb.append(addr.getAdminArea());
                }
                if (sb.length() > 0) {
                    return sb.toString();
                }
            }
        } catch (Exception ignored) {}
        return String.format(Locale.US, "Lat: %.4f, Lon: %.4f", lat, lon);
    }

    private void showManualLocationDialog() {
        EditText input = new EditText(this);
        input.setHint("e.g. Green Valley Farm, Plot 4, Nashik");
        input.setPadding(40, 30, 40, 30);
        String current = binding.tvFieldLocation.getText().toString().replace("📍 ", "").trim();
        input.setText(current);

        new MaterialAlertDialogBuilder(this)
                .setTitle("Enter Farm Location")
                .setView(input)
                .setPositiveButton("Save Location", (dialog, which) -> {
                    String loc = input.getText().toString().trim();
                    if (!loc.isEmpty()) {
                        saveFarmLocation(loc);
                    } else {
                        Toast.makeText(this, "Location cannot be blank", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void saveFarmLocation(String location) {
        viewModel.updateLocation(location, new IotRepository.OperationCallback() {
            @Override
            public void onSuccess(String message) {
                binding.tvFieldLocation.setText("📍 " + location);
                String timeStr = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());
                binding.tvLocationUpdatedAt.setText("Location updated: Today at " + timeStr);
                sessionManager.setUserLocation(location);
                Toast.makeText(IotDashboardActivity.this, "Farm station location set to: " + location, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(IotDashboardActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
