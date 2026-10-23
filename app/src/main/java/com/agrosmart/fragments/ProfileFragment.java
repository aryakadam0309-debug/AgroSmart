package com.agrosmart.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.agrosmart.R;
import com.agrosmart.activities.admin.AdminDashboardActivity;
import com.agrosmart.activities.auth.LanguageSelectionActivity;
import com.agrosmart.activities.auth.LoginActivity;
import com.agrosmart.database.AgroSmartDbHelper;
import com.agrosmart.databinding.FragmentProfileBinding;
import com.agrosmart.models.User;
import com.agrosmart.network.ApiClient;
import com.agrosmart.network.AgroSmartApiService;
import com.agrosmart.utils.Constants;
import com.agrosmart.utils.SessionManager;
import com.agrosmart.utils.ValidationUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private SessionManager sessionManager;
    private AgroSmartDbHelper dbHelper;
    private FusedLocationProviderClient fusedLocationClient;

    private ActivityResultLauncher<String> avatarPickerLauncher;
    private ActivityResultLauncher<String[]> locationPermissionLauncher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Context context = requireContext();
        sessionManager = new SessionManager(context);
        dbHelper = AgroSmartDbHelper.getInstance(context);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

        setupLaunchers();
        populateProfileUI();
        setupListeners();
    }

    private void setupLaunchers() {
        // Avatar Photo Picker Launcher
        avatarPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        handleAvatarSelection(uri);
                    }
                }
        );

        // Location Permission Launcher
        locationPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {
                    Boolean fineGranted = result.get(Manifest.permission.ACCESS_FINE_LOCATION);
                    Boolean coarseGranted = result.get(Manifest.permission.ACCESS_COARSE_LOCATION);
                    if ((fineGranted != null && fineGranted) || (coarseGranted != null && coarseGranted)) {
                        detectGpsForProfile();
                    } else {
                        Toast.makeText(requireContext(), "Location permission not granted. You can type farm location manually.", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void populateProfileUI() {
        long userId = sessionManager.getUserId();
        // Load latest user record from SQLite if available
        User dbUser = dbHelper.getUserById(userId);

        String name = dbUser != null ? dbUser.getFullName() : sessionManager.getUserName();
        String phone = dbUser != null ? dbUser.getPhone() : sessionManager.getUserPhone();
        String email = dbUser != null ? dbUser.getEmail() : sessionManager.getUserEmail();
        String location = dbUser != null && dbUser.getLocation() != null ? dbUser.getLocation() : sessionManager.getUserLocation();
        double farmSize = dbUser != null && dbUser.getFarmSize() != null ? dbUser.getFarmSize() : sessionManager.getUserFarmSize();
        String profileImage = dbUser != null && dbUser.getProfileImageUrl() != null ? dbUser.getProfileImageUrl() : sessionManager.getProfileImage();

        binding.tvProfileNameHeader.setText(name != null && !name.isEmpty() ? name : "Farmer Profile");

        String role = sessionManager.getUserRole();
        if (Constants.ROLE_ADMIN.equals(role)) {
            binding.tvProfileRoleBadge.setText("🛡️ SYSTEM ADMINISTRATOR");
            binding.tvProfileRoleBadge.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#33FFFFFF")));
            binding.btnAdminDashboard.setVisibility(View.VISIBLE);
        } else {
            binding.tvProfileRoleBadge.setText("🌱 REGISTERED FARMER");
            binding.btnAdminDashboard.setVisibility(View.GONE);
        }

        binding.tvProfilePhone.setText("📱 Mobile: +91 " + (phone != null ? phone : "9876543210"));
        binding.tvProfileEmail.setText("✉️ Email: " + (email != null && !email.isEmpty() ? email : "Not specified"));
        binding.tvProfileLocation.setText("📍 Farm Location: " + (location != null && !location.isEmpty() ? location : "Mumbai, Maharashtra"));
        binding.tvProfileFarmSize.setText("🌾 Farm Size: " + farmSize + " Acres");

        // Load avatar image
        loadAvatarImage(profileImage);
    }

    private void loadAvatarImage(String imagePath) {
        if (imagePath != null && !imagePath.isEmpty()) {
            File file = new File(imagePath);
            if (file.exists()) {
                Glide.with(this)
                        .load(file)
                        .placeholder(R.drawable.bg_rounded_card)
                        .error(R.drawable.bg_rounded_card)
                        .into(binding.ivProfileAvatar);
                return;
            } else if (imagePath.startsWith("http")) {
                Glide.with(this)
                        .load(imagePath)
                        .placeholder(R.drawable.bg_rounded_card)
                        .error(R.drawable.bg_rounded_card)
                        .into(binding.ivProfileAvatar);
                return;
            }
        }
        // Default avatar
        binding.ivProfileAvatar.setImageResource(R.drawable.bg_rounded_card);
    }

    private void setupListeners() {
        // Photo picker trigger
        binding.btnChangeAvatar.setOnClickListener(v -> avatarPickerLauncher.launch("image/*"));
        binding.ivProfileAvatar.setOnClickListener(v -> avatarPickerLauncher.launch("image/*"));

        // Toggle Edit Mode
        binding.btnEditProfile.setOnClickListener(v -> enterEditMode());

        binding.btnCancelEditProfile.setOnClickListener(v -> exitEditMode());

        // GPS location detection in edit mode
        binding.btnDetectGpsProfile.setOnClickListener(v -> checkLocationPermissionAndDetect());

        // Save Profile Changes
        binding.btnSaveProfile.setOnClickListener(v -> saveProfileChanges());

        // Admin Dashboard Shortcut
        binding.btnAdminDashboard.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), AdminDashboardActivity.class);
            startActivity(intent);
        });

        // Change Language
        binding.btnChangeLanguage.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), LanguageSelectionActivity.class);
            startActivity(intent);
        });

        // Logout with confirmation
        binding.btnLogout.setOnClickListener(v -> showLogoutConfirmation());
    }

    private void enterEditMode() {
        long userId = sessionManager.getUserId();
        User dbUser = dbHelper.getUserById(userId);

        String name = dbUser != null ? dbUser.getFullName() : sessionManager.getUserName();
        String email = dbUser != null ? dbUser.getEmail() : sessionManager.getUserEmail();
        String location = dbUser != null && dbUser.getLocation() != null ? dbUser.getLocation() : sessionManager.getUserLocation();
        double farmSize = dbUser != null && dbUser.getFarmSize() != null ? dbUser.getFarmSize() : sessionManager.getUserFarmSize();

        binding.etEditFullName.setText(name);
        binding.etEditEmail.setText(email);
        binding.etEditLocation.setText(location);
        binding.etEditFarmSize.setText(String.valueOf(farmSize));

        binding.cardProfileView.setVisibility(View.GONE);
        binding.cardProfileEdit.setVisibility(View.VISIBLE);
    }

    private void exitEditMode() {
        binding.cardProfileEdit.setVisibility(View.GONE);
        binding.cardProfileView.setVisibility(View.VISIBLE);
    }

    private void saveProfileChanges() {
        String name = binding.etEditFullName.getText() != null ? binding.etEditFullName.getText().toString().trim() : "";
        String email = binding.etEditEmail.getText() != null ? binding.etEditEmail.getText().toString().trim() : "";
        String location = binding.etEditLocation.getText() != null ? binding.etEditLocation.getText().toString().trim() : "";
        String sizeStr = binding.etEditFarmSize.getText() != null ? binding.etEditFarmSize.getText().toString().trim() : "";

        if (name.isEmpty()) {
            binding.etEditFullName.setError("Full name is required");
            binding.etEditFullName.requestFocus();
            return;
        }

        if (!email.isEmpty() && !ValidationUtils.isValidEmail(email)) {
            binding.etEditEmail.setError("Please enter a valid email address");
            binding.etEditEmail.requestFocus();
            return;
        }

        if (location.isEmpty()) {
            binding.etEditLocation.setError("Farm location is required");
            binding.etEditLocation.requestFocus();
            return;
        }

        double farmSize = 1.0;
        try {
            farmSize = Double.parseDouble(sizeStr);
            if (farmSize <= 0) {
                binding.etEditFarmSize.setError("Farm size must be greater than 0");
                binding.etEditFarmSize.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            binding.etEditFarmSize.setError("Please enter a valid numeric acreage");
            binding.etEditFarmSize.requestFocus();
            return;
        }

        long userId = sessionManager.getUserId();
        String currentAvatar = sessionManager.getProfileImage();

        // 1. Update SQLite database
        boolean updated = dbHelper.updateUserProfile(userId, name, email, location, farmSize, currentAvatar);

        // 2. Update SessionManager so changes immediately reflect across the app
        sessionManager.setUserName(name);
        sessionManager.setUserEmail(email);
        sessionManager.setUserLocation(location);
        sessionManager.setUserFarmSize(farmSize);

        // 3. Persist update directly into MySQL backend database
        Map<String, Object> req = new HashMap<>();
        req.put("phone", sessionManager.getUserPhone());
        req.put("email", email);
        req.put("fullName", name);
        req.put("location", location);
        req.put("farmSize", farmSize);

        AgroSmartApiService apiService = ApiClient.getBackendClient().create(AgroSmartApiService.class);
        apiService.updateProfile(req).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful()) {
                    android.util.Log.i("ProfileFragment", "Profile synced with MySQL successfully");
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                android.util.Log.w("ProfileFragment", "MySQL sync deferred (offline): " + t.getMessage());
            }
        });

        Toast.makeText(requireContext(), "Profile details updated successfully!", Toast.LENGTH_SHORT).show();

        // 3. Refresh UI & switch back to View Mode
        populateProfileUI();
        exitEditMode();
    }

    private void handleAvatarSelection(Uri uri) {
        try {
            // Check file size (<= 5 MB)
            long size = 0;
            try (AssetFileDescriptor afd = requireContext().getContentResolver().openAssetFileDescriptor(uri, "r")) {
                if (afd != null) {
                    size = afd.getLength();
                }
            }
            if (size > 5 * 1024 * 1024) {
                Toast.makeText(requireContext(), "Selected photo exceeds 5 MB. Please choose a smaller image.", Toast.LENGTH_LONG).show();
                return;
            }

            File dir = new File(requireContext().getFilesDir(), "profile_images");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            long userId = sessionManager.getUserId();
            File target = new File(dir, "avatar_" + userId + ".jpg");

            try (InputStream in = requireContext().getContentResolver().openInputStream(uri);
                 FileOutputStream out = new FileOutputStream(target)) {
                byte[] buffer = new byte[8192];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
                out.flush();
            }

            String savedPath = target.getAbsolutePath();
            sessionManager.setProfileImage(savedPath);

            // Persist to database
            User user = dbHelper.getUserById(userId);
            if (user != null) {
                dbHelper.updateUserProfile(userId, user.getFullName(), user.getEmail(), user.getLocation(), user.getFarmSize(), savedPath);
            }

            loadAvatarImage(savedPath);
            Toast.makeText(requireContext(), "Profile picture updated!", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Failed to save profile picture: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void checkLocationPermissionAndDetect() {
        Context context = requireContext();
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            detectGpsForProfile();
        } else {
            locationPermissionLauncher.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
        }
    }

    private void detectGpsForProfile() {
        Toast.makeText(requireContext(), "Detecting GPS location...", Toast.LENGTH_SHORT).show();
        try {
            fusedLocationClient.getLastLocation().addOnSuccessListener(requireActivity(), location -> {
                if (location != null) {
                    String place = reverseGeocode(location.getLatitude(), location.getLongitude());
                    binding.etEditLocation.setText(place);
                    Toast.makeText(requireContext(), "Detected location: " + place, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "GPS location unavailable. Please verify GPS is enabled in settings.", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> Toast.makeText(requireContext(), "GPS detection failed", Toast.LENGTH_SHORT).show());
        } catch (SecurityException ignored) {}
    }

    private String reverseGeocode(double lat, double lon) {
        try {
            Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
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
                if (sb.length() > 0) return sb.toString();
            }
        } catch (Exception ignored) {}
        return "Mumbai, Maharashtra";
    }

    private void showLogoutConfirmation() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Log Out")
                .setMessage("Are you sure you want to log out of AgroSmart?")
                .setPositiveButton("Log Out", (dialog, which) -> {
                    sessionManager.logout();
                    Intent intent = new Intent(requireContext(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    requireActivity().finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
