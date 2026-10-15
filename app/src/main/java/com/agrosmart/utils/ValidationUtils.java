package com.agrosmart.utils;

import java.util.regex.Pattern;

/**
 * AgroSmart Central Validation Utilities
 * Enforces strict input validation for Indian agricultural context:
 * - Indian mobile numbers (10 digits starting with 6, 7, 8, 9)
 * - Farmer and Admin names (alphabetic with spaces)
 * - Passwords (minimum 6 characters)
 * - Quantity, Price, and Machinery Registration validation
 */
public class ValidationUtils {

    // Indian mobile phone regex: 10 digits starting with 6, 7, 8, or 9
    private static final Pattern INDIAN_PHONE_PATTERN = Pattern.compile("^[6-9]\\d{9}$");

    // Full name: alphabetic characters and spaces only, 2 to 50 characters
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z\\s]{2,50}$");

    // Machinery / Tractor registration: e.g., MH12AB1234 or MH-12-AB-1234
    private static final Pattern REGISTRATION_PATTERN = Pattern.compile("^[A-Z]{2}[-\\s]?[0-9]{1,2}[-\\s]?[A-Z]{1,3}[-\\s]?[0-9]{4}$", Pattern.CASE_INSENSITIVE);

    // Standard RFC-compliant Email regex for pure Java JVM unit tests
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    public static boolean isValidText(String text) {
        return text != null && text.trim().length() >= 2;
    }

    public static boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) return false;
        return NAME_PATTERN.matcher(name.trim()).matches();
    }

    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) return false;
        String cleanPhone = normalizePhone(phone);
        return INDIAN_PHONE_PATTERN.matcher(cleanPhone).matches();
    }

    public static String normalizePhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) return "";
        String clean = phone.trim().replaceAll("[^0-9]", "");
        if (clean.startsWith("91") && clean.length() == 12) {
            clean = clean.substring(2);
        } else if (clean.startsWith("0") && clean.length() == 11) {
            clean = clean.substring(1);
        }
        return clean;
    }

    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) return false;
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.trim().length() >= 6;
    }

    public static boolean isValidPositiveNumber(String value) {
        if (value == null || value.trim().isEmpty()) return false;
        try {
            double num = Double.parseDouble(value.trim());
            return num > 0.0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidPositiveInteger(String value) {
        if (value == null || value.trim().isEmpty()) return false;
        try {
            int num = Integer.parseInt(value.trim());
            return num > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidRegistrationNumber(String regNo) {
        if (regNo == null || regNo.trim().isEmpty()) return false;
        return REGISTRATION_PATTERN.matcher(regNo.trim()).matches() || regNo.trim().length() >= 6;
    }
}
