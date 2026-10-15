package com.agrosmart;

import com.agrosmart.utils.ValidationUtils;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for AgroSmart ValidationUtils.
 * Covers Indian phone numbers, names, emails, passwords, positive numbers, and registration numbers.
 */
public class ValidationUtilsTest {

    @Test
    public void testIndianPhoneValidation_validCases() {
        assertTrue("Standard 10-digit starting with 9", ValidationUtils.isValidPhone("9876543210"));
        assertTrue("Standard 10-digit starting with 8", ValidationUtils.isValidPhone("8123456789"));
        assertTrue("Standard 10-digit starting with 7", ValidationUtils.isValidPhone("7987654321"));
        assertTrue("Standard 10-digit starting with 6", ValidationUtils.isValidPhone("6234567890"));
        assertTrue("+91 prefixed number", ValidationUtils.isValidPhone("+91 9876543210"));
        assertTrue("0 prefixed number", ValidationUtils.isValidPhone("09876543210"));
    }

    @Test
    public void testIndianPhoneValidation_invalidCases() {
        assertFalse("Starts with 5", ValidationUtils.isValidPhone("5876543210"));
        assertFalse("Too short (9 digits)", ValidationUtils.isValidPhone("987654321"));
        assertFalse("Too long (11 digits)", ValidationUtils.isValidPhone("98765432101"));
        assertFalse("Contains letters", ValidationUtils.isValidPhone("98765abcde"));
        assertFalse("Empty string", ValidationUtils.isValidPhone(""));
        assertFalse("Null string", ValidationUtils.isValidPhone(null));
    }

    @Test
    public void testPhoneNormalization() {
        assertEquals("9876543210", ValidationUtils.normalizePhone("+91 98765 43210"));
        assertEquals("9876543210", ValidationUtils.normalizePhone("09876543210"));
        assertEquals("9876543210", ValidationUtils.normalizePhone("9876543210"));
        assertEquals("", ValidationUtils.normalizePhone(""));
        assertEquals("", ValidationUtils.normalizePhone(null));
    }

    @Test
    public void testNameValidation() {
        assertTrue("Single name", ValidationUtils.isValidName("Ramesh"));
        assertTrue("Full name with space", ValidationUtils.isValidName("Ramesh Pawar"));
        assertTrue("Multi-part Indian name", ValidationUtils.isValidName("Sardar Vallabhbhai Patel"));
        assertFalse("Name with numbers", ValidationUtils.isValidName("Ramesh123"));
        assertFalse("Special characters", ValidationUtils.isValidName("Ramesh@Pawar"));
        assertFalse("Too short", ValidationUtils.isValidName("R"));
        assertFalse("Empty string", ValidationUtils.isValidName(""));
        assertFalse("Null string", ValidationUtils.isValidName(null));
    }

    @Test
    public void testEmailValidation() {
        assertTrue("Valid gmail", ValidationUtils.isValidEmail("farmer@gmail.com"));
        assertTrue("Valid domain", ValidationUtils.isValidEmail("arya@agrosmart.in"));
        assertTrue("Valid with dot", ValidationUtils.isValidEmail("kisan.mitra@gov.in"));
        assertFalse("Missing @", ValidationUtils.isValidEmail("farmergmail.com"));
        assertFalse("Missing domain", ValidationUtils.isValidEmail("farmer@"));
        assertFalse("Missing tld", ValidationUtils.isValidEmail("farmer@gmail"));
        assertFalse("Empty string", ValidationUtils.isValidEmail(""));
        assertFalse("Null string", ValidationUtils.isValidEmail(null));
    }

    @Test
    public void testPasswordValidation() {
        assertTrue("6 chars minimum", ValidationUtils.isValidPassword("123456"));
        assertTrue("Strong password", ValidationUtils.isValidPassword("Kisan@2026"));
        assertFalse("Too short (5 chars)", ValidationUtils.isValidPassword("12345"));
        assertFalse("Empty string", ValidationUtils.isValidPassword(""));
        assertFalse("Null string", ValidationUtils.isValidPassword(null));
    }

    @Test
    public void testPositiveNumberValidation() {
        assertTrue("Valid decimal", ValidationUtils.isValidPositiveNumber("5.5"));
        assertTrue("Valid integer string", ValidationUtils.isValidPositiveNumber("100"));
        assertFalse("Zero value", ValidationUtils.isValidPositiveNumber("0"));
        assertFalse("Negative value", ValidationUtils.isValidPositiveNumber("-12.5"));
        assertFalse("Non-numeric", ValidationUtils.isValidPositiveNumber("abc"));
        assertFalse("Empty string", ValidationUtils.isValidPositiveNumber(""));
        assertFalse("Null string", ValidationUtils.isValidPositiveNumber(null));
    }

    @Test
    public void testPositiveIntegerValidation() {
        assertTrue("Valid positive integer", ValidationUtils.isValidPositiveInteger("10"));
        assertFalse("Zero value", ValidationUtils.isValidPositiveInteger("0"));
        assertFalse("Negative integer", ValidationUtils.isValidPositiveInteger("-5"));
        assertFalse("Float number", ValidationUtils.isValidPositiveInteger("5.5"));
        assertFalse("Empty string", ValidationUtils.isValidPositiveInteger(""));
        assertFalse("Null string", ValidationUtils.isValidPositiveInteger(null));
    }

    @Test
    public void testRegistrationNumberValidation() {
        assertTrue("Standard Indian vehicle reg MH12AB1234", ValidationUtils.isValidRegistrationNumber("MH12AB1234"));
        assertTrue("Hyphenated vehicle reg MH-14-CD-5678", ValidationUtils.isValidRegistrationNumber("MH-14-CD-5678"));
        assertFalse("Too short registration", ValidationUtils.isValidRegistrationNumber("MH1"));
        assertFalse("Empty string", ValidationUtils.isValidRegistrationNumber(""));
        assertFalse("Null string", ValidationUtils.isValidRegistrationNumber(null));
    }
}
