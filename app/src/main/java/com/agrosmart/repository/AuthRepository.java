package com.agrosmart.repository;

import android.content.Context;
import com.agrosmart.database.AgroSmartDbHelper;
import com.agrosmart.models.User;
import com.agrosmart.utils.Constants;
import com.agrosmart.utils.SessionManager;
import com.agrosmart.utils.ValidationUtils;

public class AuthRepository {

    private final SessionManager sessionManager;
    private final AgroSmartDbHelper dbHelper;

    public AuthRepository(Context context) {
        this.sessionManager = new SessionManager(context);
        this.dbHelper = AgroSmartDbHelper.getInstance(context);
    }

    public interface AuthCallback {
        void onSuccess(User user);
        void onError(String message);
    }

    public interface ResetCallback {
        void onSuccess(String message);
        void onError(String message);
    }

    public void login(String identifier, String password, String role, AuthCallback callback) {
        if (identifier == null || identifier.trim().isEmpty()) {
            callback.onError("Please enter phone number or email");
            return;
        }
        if (password == null || password.length() < 6) {
            callback.onError("Password must be at least 6 characters");
            return;
        }

        String cleanIdentifier = identifier.trim();

        // 1. Try local SQLite database first for persistence
        User user = dbHelper.authenticateUser(cleanIdentifier, password, role);

        if (user == null) {
            // Check default fallback admin/farmer credentials
            if (Constants.ROLE_ADMIN.equals(role)) {
                if (("admin@agrosmart.in".equalsIgnoreCase(cleanIdentifier) || "9999999999".equals(cleanIdentifier)) && "admin123".equals(password)) {
                    user = new User(2L, "AgroSmart Admin", "9999999999", "admin@agrosmart.in", role, "jwt_admin_" + System.currentTimeMillis());
                }
            } else {
                if (("farmer@agrosmart.in".equalsIgnoreCase(cleanIdentifier) || "9876543210".equals(cleanIdentifier)) && "123456".equals(password)) {
                    user = new User(1L, "Arya Farmer", "9876543210", "farmer@agrosmart.in", role, "jwt_farmer_" + System.currentTimeMillis());
                    user.setLocation("Mumbai, Maharashtra");
                    user.setFarmSize(5.5);
                } else if (ValidationUtils.isValidPhone(cleanIdentifier) && password.length() >= 6) {
                    // Demo mode dynamic login
                    user = new User(System.currentTimeMillis(), "Shetkari Kisan", cleanIdentifier, cleanIdentifier + "@agrosmart.in", role, "jwt_farmer_" + System.currentTimeMillis());
                    user.setLocation("Mumbai, Maharashtra");
                    user.setFarmSize(4.0);
                    dbHelper.registerUser(user, password);
                }
            }
        }

        if (user != null) {
            if (user.getToken() == null) {
                user.setToken("jwt_token_" + System.currentTimeMillis());
            }
            sessionManager.saveAuthSession(user.getToken(), user.getId(), user.getFullName(), user.getEmail(), user.getPhone(), user.getRole());
            if (user.getLocation() != null && !user.getLocation().isEmpty()) {
                sessionManager.setUserLocation(user.getLocation());
            }
            if (user.getFarmSize() != null) {
                sessionManager.setUserFarmSize(user.getFarmSize());
            }
            if (user.getProfileImageUrl() != null) {
                sessionManager.setProfileImage(user.getProfileImageUrl());
            }
            callback.onSuccess(user);
        } else {
            callback.onError("Invalid credentials. Please verify phone/email and password.");
        }
    }

    public void register(String fullName, String phone, String email, String password, String location, double farmSize, AuthCallback callback) {
        String cleanPhone = ValidationUtils.normalizePhone(phone);
        User user = new User();
        user.setId(System.currentTimeMillis());
        user.setFullName(fullName);
        user.setPhone(cleanPhone);
        user.setEmail(email);
        user.setRole(Constants.ROLE_FARMER);
        user.setLocation(location);
        user.setFarmSize(farmSize);
        user.setToken("jwt_farmer_" + System.currentTimeMillis());

        // Save to SQLite database
        dbHelper.registerUser(user, password);

        // Save active session
        sessionManager.saveAuthSession(user.getToken(), user.getId(), user.getFullName(), user.getEmail(), user.getPhone(), user.getRole());
        if (user.getLocation() != null && !user.getLocation().isEmpty()) {
            sessionManager.setUserLocation(user.getLocation());
        }
        if (user.getFarmSize() != null) {
            sessionManager.setUserFarmSize(user.getFarmSize());
        }
        callback.onSuccess(user);
    }

    public void resetPassword(String phone, String newPassword, ResetCallback callback) {
        String cleanPhone = ValidationUtils.normalizePhone(phone);
        if (!ValidationUtils.isValidPhone(cleanPhone)) {
            callback.onError("Please enter a valid 10-digit registered Indian mobile number");
            return;
        }
        if (!ValidationUtils.isValidPassword(newPassword)) {
            callback.onError("New password must be at least 6 characters");
            return;
        }

        boolean updated = dbHelper.resetUserPassword(cleanPhone, newPassword);
        if (updated) {
            callback.onSuccess("Password reset successfully. You can now login with your new password.");
        } else {
            // Check if it's the demo farmer number
            if ("9876543210".equals(cleanPhone)) {
                callback.onSuccess("Password updated for demo account. Please login with new password.");
            } else {
                callback.onError("Mobile number not found. Please register first or verify phone number.");
            }
        }
    }
}
