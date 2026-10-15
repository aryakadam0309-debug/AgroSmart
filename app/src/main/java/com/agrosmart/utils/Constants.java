package com.agrosmart.utils;

public final class Constants {
    private Constants() {} // Prevent instantiation

    public static final String PREF_NAME = "AgroSmartPrefs";
    public static final String KEY_IS_LOGGED_IN = "is_logged_in";
    public static final String KEY_USER_TOKEN = "user_jwt_token";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_USER_NAME = "user_full_name";
    public static final String KEY_USER_EMAIL = "user_email";
    public static final String KEY_USER_PHONE = "user_phone";
    public static final String KEY_USER_ROLE = "user_role";
    public static final String KEY_LANGUAGE = "selected_language";
    public static final String KEY_IS_LANGUAGE_SET = "is_language_set";

    // Roles
    public static final String ROLE_FARMER = "ROLE_FARMER";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";

    // Languages
    public static final String LANG_ENGLISH = "en";
    public static final String LANG_HINDI = "hi";
    public static final String LANG_MARATHI = "mr";

    // Intent Extra Keys
    public static final String EXTRA_ITEM_ID = "extra_item_id";
    public static final String EXTRA_TITLE = "extra_title";
}
