package com.agrosmart.utils;

import android.content.Context;
import android.content.SharedPreferences;

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
        return pref.getFloat("user_farm_size", 5.5f);
    }

    public void setProfileImage(String path) {
        editor.putString("user_profile_image", path);
        editor.apply();
    }

    public String getProfileImage() {
        return pref.getString("user_profile_image", null);
    }
}
