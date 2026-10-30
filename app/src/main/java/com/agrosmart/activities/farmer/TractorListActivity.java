package com.agrosmart.activities.farmer;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.agrosmart.R;
import com.agrosmart.activities.BaseActivity;
import com.agrosmart.adapters.TractorAdapter;
import com.agrosmart.databinding.ActivityTractorListBinding;
import com.agrosmart.models.Tractor;
import com.agrosmart.repository.TractorRepository;
import com.agrosmart.utils.SessionManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

public class TractorListActivity extends BaseActivity {

    private ActivityTractorListBinding binding;
    private TractorRepository repository;
    private SessionManager sessionManager;
    private TractorAdapter adapter;
    private FusedLocationProviderClient fusedLocationClient;

    private String selectedBrand = "All Brands";
    private String selectedAreaName = "Detecting live GPS...";
    private double searchLat = 17.2892;
    private double searchLon = 74.1818;
    private Double searchRadiusKm = 20.0;
    private String selectedBookingDate = null; // e.g. "15 Nov 2026"

    private ActivityResultLauncher<String> locationPermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTractorListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = new TractorRepository(this);
        sessionManager = new SessionManager(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Load cached coordinates if available
        searchLat = sessionManager.getTractorSearchLat();
        searchLon = sessionManager.getTractorSearchLon();
        String savedArea = sessionManager.getTractorSearchArea();
        if (savedArea != null && !savedArea.isEmpty()) {
            selectedAreaName = savedArea;
        }

        initLocationPermissionLauncher();

        binding.toolbarTractors.setNavigationOnClickListener(v -> finish());

        setupLocationHeader();
        setupDatePicker();
        setupScopeFilter();
        setupRecyclerView();
        setupBrandFilter();
        observeTractors();

        // Automatically detect live location on launch
        checkAndRequestGpsLocation();

        binding.swipeRefreshTractors.setOnRefreshListener(() -> {
            checkAndRequestGpsLocation();
            binding.swipeRefreshTractors.setRefreshing(false);
        });

        binding.btnViewMyBookings.setOnClickListener(v -> {
            startActivity(new Intent(this, MyBookingsActivity.class));
        });

        binding.fabAddTractor.setOnClickListener(v -> {
            startActivity(new Intent(this, AddTractorActivity.class));
        });

        binding.btnEmptyChangeLocation.setOnClickListener(v -> checkAndRequestGpsLocation());

        binding.btnSearchAllMaharashtra.setOnClickListener(v -> {
            searchRadiusKm = null;
            binding.chipAllTractors.setChecked(true);
            binding.tvRadiusBadge.setText("All Maharashtra");
            loadTractors();
        });
    }

    private void initLocationPermissionLauncher() {
        locationPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        fetchCurrentGpsLocationAndSort();
                    } else {
                        Toast.makeText(this, "Location permission not granted. Using approximate area • Nearest fleets sorted.", Toast.LENGTH_SHORT).show();
                        selectedAreaName = "Approximate Location";
                        updateLocationHeaderUi();
                        loadTractors();
                    }
                }
        );
    }

    private void checkAndRequestGpsLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fetchCurrentGpsLocationAndSort();
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private void fetchCurrentGpsLocationAndSort() {
        binding.tvSelectedLocation.setText("📍 Acquiring live GPS location...");
        binding.tvLiveLocationCoords.setText("Connecting to satellite positioning...");

        try {
            // First check last known location for fast response
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    applyLiveLocation(location);
                }
            });

            // Then request fresh high accuracy current location
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            applyLiveLocation(location);
                        } else if (binding.tvSelectedLocation.getText().toString().contains("Acquiring")) {
                            selectedAreaName = "Live Area (" + String.format(Locale.US, "%.3f, %.3f", searchLat, searchLon) + ")";
                            updateLocationHeaderUi();
                            loadTractors();
                        }
                    })
                    .addOnFailureListener(e -> {
                        if (binding.tvSelectedLocation.getText().toString().contains("Acquiring")) {
                            updateLocationHeaderUi();
                            loadTractors();
                        }
                    });
        } catch (SecurityException e) {
            updateLocationHeaderUi();
            loadTractors();
        }
    }

    private void applyLiveLocation(Location location) {
        searchLat = location.getLatitude();
        searchLon = location.getLongitude();

        // Reverse geocode locality in background
        Executors.newSingleThreadExecutor().execute(() -> {
            String localityName = null;
            try {
                Geocoder geocoder = new Geocoder(TractorListActivity.this, Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(searchLat, searchLon, 1);
                if (addresses != null && !addresses.isEmpty()) {
                    Address a = addresses.get(0);
                    localityName = a.getLocality() != null ? a.getLocality() : a.getSubAdminArea();
                    if (localityName != null && a.getAdminArea() != null && !localityName.contains(a.getAdminArea())) {
                        localityName += ", " + a.getAdminArea();
                    }
                }
            } catch (Exception ignored) {}

            final String displayName = (localityName != null) ? localityName
                    : String.format(Locale.US, "GPS (%.3f, %.3f)", searchLat, searchLon);

            runOnUiThread(() -> {
                selectedAreaName = displayName;
                sessionManager.saveTractorSearchLocation(displayName, searchLat, searchLon, true);
                updateLocationHeaderUi();
            });
        });

        loadTractors();
    }

    private void setupLocationHeader() {
        updateLocationHeaderUi();

        binding.btnRefreshGps.setOnClickListener(v -> {
            Toast.makeText(this, "Refreshing live GPS location...", Toast.LENGTH_SHORT).show();
            checkAndRequestGpsLocation();
        });

        binding.tvRadiusBadge.setOnClickListener(v -> {
            String[] radiusOptions = {"Within 20 km (Default)", "Within 50 km", "All Maharashtra"};
            new AlertDialog.Builder(this)
                    .setTitle("Select Search Radius")
                    .setItems(radiusOptions, (dialog, which) -> {
                        if (which == 0) {
                            searchRadiusKm = 20.0;
                            binding.tvRadiusBadge.setText("Within 20 km");
                        } else if (which == 1) {
                            searchRadiusKm = 50.0;
                            binding.tvRadiusBadge.setText("Within 50 km");
                        } else {
                            searchRadiusKm = null;
                            binding.tvRadiusBadge.setText("All Maharashtra");
                        }
                        loadTractors();
                    })
                    .show();
        });
    }

    private void updateLocationHeaderUi() {
        binding.tvSelectedLocation.setText("📍 " + selectedAreaName + " (Live GPS)");
        binding.tvLiveLocationCoords.setText(String.format(Locale.US,
                "Lat: %.3f, Lon: %.3f • Nearest tractors sorted first", searchLat, searchLon));

        if (searchRadiusKm != null) {
            binding.tvRadiusBadge.setText(String.format(Locale.US, "Within %.0f km", searchRadiusKm));
        } else {
            binding.tvRadiusBadge.setText("All Maharashtra");
        }
    }

    private void setupDatePicker() {
        binding.chipSelectDate.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            DatePickerDialog dialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                Calendar chosen = Calendar.getInstance();
                chosen.set(year, month, dayOfMonth);
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.US);
                selectedBookingDate = sdf.format(chosen.getTime());
                binding.chipSelectDate.setText("📅 " + selectedBookingDate);
                binding.tvClearDate.setVisibility(View.VISIBLE);
                loadTractors();
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

            dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            dialog.show();
        });

        binding.tvClearDate.setOnClickListener(v -> {
            selectedBookingDate = null;
            binding.chipSelectDate.setText("📅 Any Date");
            binding.tvClearDate.setVisibility(View.GONE);
            loadTractors();
        });
    }

    private void setupScopeFilter() {
        binding.chipGroupScope.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty() || checkedIds.contains(R.id.chipNearbyOnly)) {
                searchRadiusKm = 20.0;
                binding.tvRadiusBadge.setText("Within 20 km");
            } else {
                searchRadiusKm = null;
                binding.tvRadiusBadge.setText("All Maharashtra");
            }
            loadTractors();
        });
    }

    private void loadTractors() {
        if (searchRadiusKm != null) {
            repository.searchNearbyTractors(searchLat, searchLon, searchRadiusKm, selectedBookingDate, selectedBrand, null);
        } else {
            repository.loadTractors(selectedBrand);
        }
    }

    private void setupRecyclerView() {
        adapter = new TractorAdapter(tractor -> {
            Intent intent = new Intent(this, BookTractorActivity.class);
            intent.putExtra("EXTRA_TRACTOR", tractor);
            if (selectedBookingDate != null) {
                intent.putExtra("EXTRA_DEFAULT_DATE", selectedBookingDate);
            }
            startActivity(intent);
        });

        binding.rvTractors.setLayoutManager(new LinearLayoutManager(this));
        binding.rvTractors.setAdapter(adapter);
    }

    private void setupBrandFilter() {
        binding.chipGroupBrand.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                selectedBrand = "All Brands";
            } else {
                int checkedId = checkedIds.get(0);
                if (checkedId == R.id.chipMahindra) {
                    selectedBrand = "Mahindra";
                } else if (checkedId == R.id.chipJohnDeere) {
                    selectedBrand = "John Deere";
                } else if (checkedId == R.id.chipSwaraj) {
                    selectedBrand = "Swaraj";
                } else if (checkedId == R.id.chipSonalika) {
                    selectedBrand = "Sonalika";
                } else if (checkedId == R.id.chipEicher) {
                    selectedBrand = "Eicher";
                } else {
                    selectedBrand = "All Brands";
                }
            }
            loadTractors();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTractors();
    }

    private void observeTractors() {
        repository.getTractorsLiveData().observe(this, list -> {
            if (list == null || list.isEmpty()) {
                showEmptyState();
                return;
            }

            // 1. Calculate distance from live GPS for all tractors
            List<Tractor> processedList = new ArrayList<>();
            for (Tractor t : list) {
                resolveTractorCoordinates(t);
                if (t.getLatitude() != null && t.getLongitude() != null) {
                    double dist = calculateHaversineDistanceKm(searchLat, searchLon, t.getLatitude(), t.getLongitude());
                    t.setDistanceKm(Math.round(dist * 10.0) / 10.0);
                }

                // If within radius filter is applied, only include tractors within that radius
                if (searchRadiusKm != null) {
                    if (t.getDistanceKm() != null && t.getDistanceKm() <= searchRadiusKm) {
                        processedList.add(t);
                    }
                } else {
                    processedList.add(t);
                }
            }

            // 2. Sort strictly from nearest to farthest (ascending distance)
            Collections.sort(processedList, Comparator.comparingDouble(t ->
                    t.getDistanceKm() != null ? t.getDistanceKm() : 999999.0));

            if (processedList.isEmpty()) {
                showEmptyState();
            } else {
                binding.layoutEmptyState.setVisibility(View.GONE);
                binding.rvTractors.setVisibility(View.VISIBLE);

                if (searchRadiusKm != null) {
                    binding.tvLiveLocationCoords.setText(String.format(Locale.US,
                            "Lat: %.3f, Lon: %.3f • %d tractors within %.0f km (nearest first)",
                            searchLat, searchLon, processedList.size(), searchRadiusKm));
                } else {
                    binding.tvLiveLocationCoords.setText(String.format(Locale.US,
                            "Lat: %.3f, Lon: %.3f • All %d tractors sorted nearest first",
                            searchLat, searchLon, processedList.size()));
                }

                adapter.submitList(processedList);
            }
        });
    }

    private void showEmptyState() {
        binding.rvTractors.setVisibility(View.GONE);
        binding.layoutEmptyState.setVisibility(View.VISIBLE);

        String dateStr = selectedBookingDate != null ? " on " + selectedBookingDate : "";
        String brandStr = !"All Brands".equalsIgnoreCase(selectedBrand) ? " (" + selectedBrand + ")" : "";
        String radiusStr = searchRadiusKm != null ? " within " + String.format(Locale.US, "%.0f km", searchRadiusKm) : "";

        binding.tvEmptyTitle.setText("No Tractors Found" + radiusStr + brandStr);
        binding.tvEmptyMessage.setText("No available tractors found in this range" + dateStr
                + ". Tap 'View All Across Maharashtra' to view the entire fleet sorted by distance from your location.");
    }

    public static void resolveTractorCoordinates(Tractor t) {
        if (t.getLatitude() != null && t.getLongitude() != null && (t.getLatitude() != 0.0 || t.getLongitude() != 0.0)) {
            return;
        }
        String loc = ((t.getLocation() != null ? t.getLocation() : "") + " " +
                (t.getVillage() != null ? t.getVillage() : "") + " " +
                (t.getDistrict() != null ? t.getDistrict() : "")).toLowerCase();

        if (loc.contains("mumbai") || loc.contains("goregaon") || loc.contains("aarey") || loc.contains("bhandup")) {
            t.setLatitude(19.0760); t.setLongitude(72.8777);
        } else if (loc.contains("thane")) {
            t.setLatitude(19.2183); t.setLongitude(72.9781);
        } else if (loc.contains("panvel") || loc.contains("raigad")) {
            t.setLatitude(18.9894); t.setLongitude(73.1175);
        } else if (loc.contains("karad") || loc.contains("satara") || loc.contains("malkapur") || loc.contains("ogalewadi")) {
            t.setLatitude(17.2892); t.setLongitude(74.1818);
        } else if (loc.contains("baramati")) {
            t.setLatitude(18.1519); t.setLongitude(74.5771);
        } else if (loc.contains("shirur")) {
            t.setLatitude(18.8272); t.setLongitude(74.3792);
        } else if (loc.contains("daund")) {
            t.setLatitude(18.4655); t.setLongitude(74.5828);
        } else if (loc.contains("pune")) {
            t.setLatitude(18.5204); t.setLongitude(73.8567);
        } else if (loc.contains("nashik") || loc.contains("niphad") || loc.contains("dindori") || loc.contains("sinnar")) {
            t.setLatitude(19.9975); t.setLongitude(73.7898);
        } else if (loc.contains("kolhapur") || loc.contains("shirol") || loc.contains("ichalkaranji")) {
            t.setLatitude(16.7050); t.setLongitude(74.2433);
        } else if (loc.contains("sangli") || loc.contains("ashta") || loc.contains("tasgaon") || loc.contains("islampur")) {
            t.setLatitude(16.8524); t.setLongitude(74.5815);
        } else {
            t.setLatitude(18.5204); t.setLongitude(73.8567);
        }
    }

    public static double calculateHaversineDistanceKm(double lat1, double lon1, double lat2, double lon2) {
        final double EARTH_RADIUS_KM = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c;
    }
}
