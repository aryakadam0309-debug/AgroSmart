package com.agrosmart.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.agrosmart.models.FarmerProfile;

public class SessionManager {
    private final SharedPreferences pref;
    private final SharedPreferences.Editor editor;
    private final Context context;

    public SessionManager(Context context) {
        this.context = context;
        this.pref = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        this.editor = pref.edit();
    }

    public void saveAuthSession(String token, Long userId, String fullName, String email, String phone, String role) {
        editor.putBoolean(Constants.KEY_IS_LOGGED_IN, true);
        editor.putString(Constants.KEY_USER_TOKEN, token);
        if (userId != null) {
            editor.putLong(Constants.KEY_USER_ID, userId);
        }
        editor.putString(Constants.KEY_USER_NAME, fullName);
        editor.putString(Constants.KEY_USER_EMAIL, email);
        editor.putString(Constants.KEY_USER_PHONE, phone);
        editor.putString(Constants.KEY_USER_ROLE, role);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(Constants.KEY_IS_LOGGED_IN, false);
    }

    public String getUserToken() {
        return pref.getString(Constants.KEY_USER_TOKEN, null);
    }

    public String getUserRole() {
        return pref.getString(Constants.KEY_USER_ROLE, Constants.ROLE_FARMER);
    }

    public String getUserName() {
        return pref.getString(Constants.KEY_USER_NAME, "Farmer");
    }

    public void setLanguage(String langCode) {
        editor.putString(Constants.KEY_LANGUAGE, langCode);
        editor.putBoolean(Constants.KEY_IS_LANGUAGE_SET, true);
        editor.apply();
    }

    public String getLanguage() {
        return pref.getString(Constants.KEY_LANGUAGE, Constants.LANG_ENGLISH);
    }

    public boolean isLanguageSelected() {
        return pref.getBoolean(Constants.KEY_IS_LANGUAGE_SET, false);
    }

    public void logout() {
        String lang = getLanguage();
        boolean langSet = isLanguageSelected();
        editor.clear();
        editor.putString(Constants.KEY_LANGUAGE, lang);
        editor.putBoolean(Constants.KEY_IS_LANGUAGE_SET, langSet);
        editor.apply();
    }

    public String getUserPhone() {
        return pref.getString(Constants.KEY_USER_PHONE, "9876543210");
    }

    public String getUserEmail() {
        return pref.getString(Constants.KEY_USER_EMAIL, "");
    }

    public long getUserId() {
        return pref.getLong(Constants.KEY_USER_ID, 1L);
    }

    public void setSelectedCity(String city) {
        editor.putString("selected_city", city);
        editor.apply();
    }

    public String getSelectedCity() {
        return pref.getString("selected_city", "Mumbai, Maharashtra");
    }

    public void setUserName(String name) {
        editor.putString(Constants.KEY_USER_NAME, name);
        editor.apply();
    }

    public void setUserEmail(String email) {
        editor.putString(Constants.KEY_USER_EMAIL, email);
        editor.apply();
    }

    public void setUserPhone(String phone) {
        editor.putString(Constants.KEY_USER_PHONE, phone);
        editor.apply();
    }

    public void setUserLocation(String location) {
        editor.putString("user_farm_location", location);
        editor.apply();
    }

    public String getUserLocation() {
        return pref.getString("user_farm_location", "Mumbai, Maharashtra");
    }

    public void setUserFarmSize(double farmSize) {
        editor.putFloat("user_farm_size", (float) farmSize);
        editor.apply();
    }

    public double getUserFarmSize() {
        return pref.getFloat("user_farm_size", 2.5f);
    }

    public void setProfileImage(String path) {
        editor.putString("user_profile_image", path);
        editor.apply();
    }

    public String getProfileImage() {
        return pref.getString("user_profile_image", null);
    }

    public void saveFarmerProfile(FarmerProfile profile) {
        if (profile == null) return;
        editor.putString("profile_state", profile.getState());
        editor.putString("profile_district", profile.getDistrict());
        if (profile.getLandAreaAcres() != null) {
            editor.putFloat("user_farm_size", profile.getLandAreaAcres().floatValue());
        }
        editor.putString("profile_category", profile.getFarmerCategory());
        editor.putString("profile_crops", profile.getPrimaryCrops());
        editor.putString("profile_ownership", profile.getOwnershipStatus());
        if (profile.getAnnualIncome() != null) {
            editor.putFloat("profile_income", profile.getAnnualIncome().floatValue());
        }
        editor.apply();
    }

    public FarmerProfile getFarmerProfile() {
        FarmerProfile p = new FarmerProfile();
        p.setFarmerPhone(getUserPhone());
        p.setFullName(getUserName());
        p.setState(pref.getString("profile_state", "Maharashtra"));
        p.setDistrict(pref.getString("profile_district", "Baramati"));
        p.setLandAreaAcres((double) pref.getFloat("user_farm_size", 2.5f));
        p.setFarmerCategory(pref.getString("profile_category", "SMALL_MARGINAL"));
        p.setPrimaryCrops(pref.getString("profile_crops", "Soybean, Cotton"));
        p.setOwnershipStatus(pref.getString("profile_ownership", "OWNER"));
        p.setAnnualIncome((double) pref.getFloat("profile_income", 120000.0f));
        return p;
    }

    public void saveTractorSearchLocation(String areaName, double lat, double lon, boolean isGps) {
        editor.putString("tractor_search_area", areaName);
        editor.putFloat("tractor_search_lat", (float) lat);
        editor.putFloat("tractor_search_lon", (float) lon);
        editor.putBoolean("tractor_use_gps", isGps);
        editor.apply();
    }

    public String getTractorSearchArea() {
        return pref.getString("tractor_search_area", "Karad, Satara");
    }

    public double getTractorSearchLat() {
        return pref.getFloat("tractor_search_lat", 17.2892f);
    }

    public double getTractorSearchLon() {
        return pref.getFloat("tractor_search_lon", 74.1818f);
    }

    public boolean isTractorSearchUsingGps() {
        return pref.getBoolean("tractor_use_gps", false);
    }
}
