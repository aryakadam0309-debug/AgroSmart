package com.agrosmart.activities.farmer;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.agrosmart.R;
import com.agrosmart.activities.BaseActivity;
import com.agrosmart.adapters.SensorReadingAdapter;
import com.agrosmart.databinding.ActivityIotDashboardBinding;
import com.agrosmart.databinding.DialogConfigureThresholdsBinding;
import com.agrosmart.models.IrrigationSetting;
import com.agrosmart.models.PumpCommand;
import com.agrosmart.models.SensorReading;
import com.agrosmart.repository.IotRepository;
import com.agrosmart.utils.SessionManager;
import com.agrosmart.viewmodels.IotViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class IotDashboardActivity extends BaseActivity {

    private ActivityIotDashboardBinding binding;
    private IotViewModel viewModel;
    private SensorReadingAdapter historyAdapter;
    private IrrigationSetting currentSettings;
    private boolean isPumpActive = false;
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
                binding.viewStatusDot.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#4CAF50")));
                binding.tvConnectionStatus.setText("Station Online (ESP32 Live)");
                binding.tvLastSeen.setText("Last sync: Live");
                binding.cardAlert.setVisibility(View.GONE);
            } else {
                binding.viewStatusDot.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#E53935")));
                binding.tvConnectionStatus.setText("No Physical Sensor Connected");
                binding.tvLastSeen.setText("Hardware Offline (Waiting for ESP32)");

                // Reset display when no hardware is streaming live
                binding.tvMoistureValue.setText("--");
                binding.pbMoisture.setProgress(0);
                binding.tvRawSensorAdc.setText("Raw ADC: No Signal");
                binding.tvMoistureStatusChip.setText("HARDWARE OFFLINE");
                binding.tvMoistureStatusChip.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#EEEEEE")));
                binding.tvMoistureStatusChip.setTextColor(Color.parseColor("#616161"));

                binding.tvLiveTemp.setText("-- °C");
                binding.tvLiveHumidity.setText("-- %");

                binding.cardAlert.setVisibility(View.VISIBLE);
                binding.tvAlertTitle.setText("⚠️ Hardware Disconnected");
                binding.tvAlertMessage.setText("No physical ESP32 or sensors are currently streaming data. Power on your ESP32 station and connect to Wi-Fi to view live telemetry.");
            }
        });

        // Latest Sensor Reading (Moisture + DHT22)
        viewModel.getLatestReading().observe(this, reading -> {
            boolean online = Boolean.TRUE.equals(viewModel.getIsOnline().getValue());
            if (reading != null && online) {
                binding.tvMoistureValue.setText(String.format(java.util.Locale.US, "%.1f", reading.getSoilMoisturePct()));
                binding.pbMoisture.setProgress((int) Math.round(reading.getSoilMoisturePct()));
                binding.tvRawSensorAdc.setText("Raw ADC: " + reading.getSoilMoistureRaw());

                String status = reading.getMoistureStatus() != null ? reading.getMoistureStatus().toUpperCase() : "MODERATE";
                binding.tvMoistureStatusChip.setText(status);

                if ("DRY".equals(status)) {
                    binding.tvMoistureStatusChip.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#FFCDD2")));
                    binding.tvMoistureStatusChip.setTextColor(Color.parseColor("#C62828"));
                    binding.pbMoisture.setProgressTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#C62828")));
                } else if ("WET".equals(status)) {
                    binding.tvMoistureStatusChip.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#B3E5FC")));
                    binding.tvMoistureStatusChip.setTextColor(Color.parseColor("#0277BD"));
                    binding.pbMoisture.setProgressTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#0277BD")));
                } else {
                    binding.tvMoistureStatusChip.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#C8E6C9")));
                    binding.tvMoistureStatusChip.setTextColor(Color.parseColor("#2E7D32"));
                    binding.pbMoisture.setProgressTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#2E7D32")));
                }

                if (reading.getTemperatureC() != null) {
                    binding.tvLiveTemp.setText(String.format(java.util.Locale.US, "%.1f°C", reading.getTemperatureC()));
                }
                if (reading.getHumidityPct() != null) {
                    binding.tvLiveHumidity.setText(String.format(java.util.Locale.US, "%.1f%%", reading.getHumidityPct()));
                }
            }
        });

        // Irrigation Settings
        viewModel.getSettings().observe(this, settings -> {
            if (settings != null) {
                this.currentSettings = settings;
                boolean isAuto = "AUTO".equalsIgnoreCase(settings.getOperatingMode());
                binding.switchAutoMode.setChecked(isAuto);

                String summary = String.format(java.util.Locale.US,
                        "Auto thresholds: Start < %.0f%% • Stop > %.0f%% • Max: %ds • Cooldown: %ds",
                        settings.getLowMoistureStartThreshold(),
                        settings.getHighMoistureStopThreshold(),
                        settings.getMaxContinuousPumpSec(),
                        settings.getCooldownIntervalSec());
                binding.tvThresholdSummary.setText(summary);
            }
        });

        // Pump Command & Confirmed State
        viewModel.getLatestPumpCommand().observe(this, cmd -> {
            if (cmd != null) {
                boolean req = cmd.isRequestedState();
                binding.tvRequestedState.setText(req ? "ON" : "OFF");
                binding.tvRequestedState.setTextColor(req ? Color.parseColor("#2E7D32") : Color.parseColor("#757575"));

                Boolean conf = cmd.getConfirmedState();
                if (conf != null) {
                    isPumpActive = conf;
                    binding.tvConfirmedState.setText(conf ? "CONFIRMED ON (Relay Energized)" : "CONFIRMED OFF (Relay Disengaged)");
                    binding.tvConfirmedState.setTextColor(conf ? Color.parseColor("#2E7D32") : Color.parseColor("#757575"));
                } else {
                    binding.tvConfirmedState.setText("AWAITING ACK FROM ESP32 (" + cmd.getStatus() + ")");
                    binding.tvConfirmedState.setTextColor(Color.parseColor("#ED6C02"));
                }

                if (isPumpActive) {
                    binding.btnTogglePump.setText(R.string.iot_turn_off);
                    binding.btnTogglePump.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#BA1A1A")));
                } else {
                    binding.btnTogglePump.setText(R.string.iot_turn_on);
                    binding.btnTogglePump.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#2D6A4F")));
                }
            }
        });

        // Active Alerts
        viewModel.getAlerts().observe(this, alerts -> {
            if (alerts != null && !alerts.isEmpty()) {
                binding.cardAlert.setVisibility(View.VISIBLE);
                binding.tvAlertTitle.setText(alerts.get(0).getTitle());
                binding.tvAlertMessage.setText(alerts.get(0).getMessage());
            } else {
                binding.cardAlert.setVisibility(View.GONE);
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

        // Mode switch listener
        binding.switchAutoMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (currentSettings != null) {
                String newMode = isChecked ? "AUTO" : "MANUAL";
                if (!newMode.equalsIgnoreCase(currentSettings.getOperatingMode())) {
                    currentSettings.setOperatingMode(newMode);
                    viewModel.saveSettings(currentSettings, new IotRepository.OperationCallback() {
                        @Override
                        public void onSuccess(String message) {
                            Toast.makeText(IotDashboardActivity.this, "Mode updated: " + newMode, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(String error) {
                            Toast.makeText(IotDashboardActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        // Manual Pump Button
        binding.btnTogglePump.setOnClickListener(v -> {
            boolean turnOn = !isPumpActive;
            String prompt = turnOn ? "Turn irrigation pump ON?" : "Turn irrigation pump OFF?";
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Pump Operation Confirmation")
                    .setMessage(prompt)
                    .setPositiveButton("Confirm", (dialog, which) -> {
                        viewModel.togglePump(turnOn, 300, new IotRepository.OperationCallback() {
                            @Override
                            public void onSuccess(String message) {
                                Toast.makeText(IotDashboardActivity.this, message, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(String error) {
                                Toast.makeText(IotDashboardActivity.this, error, Toast.LENGTH_LONG).show();
                            }
                        });
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        // Threshold Configuration Dialog
        binding.btnConfigThresholds.setOnClickListener(v -> showConfigureThresholdsDialog());
    }

    private void showConfigureThresholdsDialog() {
        DialogConfigureThresholdsBinding dialogBinding = DialogConfigureThresholdsBinding.inflate(
                LayoutInflater.from(this)
        );

        if (currentSettings != null) {
            dialogBinding.etStartThreshold.setText(String.valueOf(currentSettings.getLowMoistureStartThreshold()));
            dialogBinding.etStopThreshold.setText(String.valueOf(currentSettings.getHighMoistureStopThreshold()));
            dialogBinding.etMaxRuntime.setText(String.valueOf(currentSettings.getMaxContinuousPumpSec()));
            dialogBinding.etCooldown.setText(String.valueOf(currentSettings.getCooldownIntervalSec()));
        }

        AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setView(dialogBinding.getRoot())
                .setPositiveButton("Save Settings", (d, which) -> {
                    try {
                        double startThresh = Double.parseDouble(dialogBinding.etStartThreshold.getText().toString().trim());
                        double stopThresh = Double.parseDouble(dialogBinding.etStopThreshold.getText().toString().trim());
                        int maxRuntime = Integer.parseInt(dialogBinding.etMaxRuntime.getText().toString().trim());
                        int cooldown = Integer.parseInt(dialogBinding.etCooldown.getText().toString().trim());

                        if (startThresh >= stopThresh) {
                            Toast.makeText(this, "Validation Error: Start threshold must be lower than stop threshold to prevent relay chattering!", Toast.LENGTH_LONG).show();
                            return;
                        }

                        if (currentSettings == null) {
                            currentSettings = new IrrigationSetting();
                            currentSettings.setDeviceId(viewModel.getCurrentDeviceId());
                            currentSettings.setFarmerId(1L);
                        }

                        currentSettings.setLowMoistureStartThreshold(startThresh);
                        currentSettings.setHighMoistureStopThreshold(stopThresh);
                        currentSettings.setMaxContinuousPumpSec(maxRuntime);
                        currentSettings.setCooldownIntervalSec(cooldown);

                        viewModel.saveSettings(currentSettings, new IotRepository.OperationCallback() {
                            @Override
                            public void onSuccess(String message) {
                                Toast.makeText(IotDashboardActivity.this, message, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(String error) {
                                Toast.makeText(IotDashboardActivity.this, error, Toast.LENGTH_LONG).show();
                            }
                        });
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "Please enter valid numeric values", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();

        dialog.show();
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
