package com.agrosmart;

import com.agrosmart.models.User;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for AgroSmart User model.
 */
public class UserModelTest {

    @Test
    public void testUserCreationAndGetters() {
        User user = new User(1L, "Arya Farmer", "9876543210", "farmer@agrosmart.in", "ROLE_FARMER", "mock_jwt_token");
        user.setLocation("Mumbai, Maharashtra");
        user.setFarmSize(5.5);

        assertEquals(Long.valueOf(1L), user.getId());
        assertEquals("Arya Farmer", user.getFullName());
        assertEquals("9876543210", user.getPhoneNumber());
        assertEquals("9876543210", user.getPhone());
        assertEquals("farmer@agrosmart.in", user.getEmail());
        assertEquals("ROLE_FARMER", user.getRole());
        assertEquals("mock_jwt_token", user.getToken());
        assertEquals("Mumbai, Maharashtra", user.getLocation());
        assertEquals(5.5, user.getFarmSize(), 0.001);
    }

    @Test
    public void testUserProfileImageProperty() {
        User user = new User(2L, "Admin User", "9999999999", "admin@agrosmart.in", "ROLE_ADMIN", "admin_token");
        assertNull(user.getProfileImageUrl());

        String avatarPath = "/data/data/com.agrosmart.debug/files/profile_images/avatar_2.jpg";
        user.setProfileImageUrl(avatarPath);
        assertEquals(avatarPath, user.getProfileImageUrl());
    }

    @Test
    public void testUserRoleChecks() {
        User farmer = new User(10L, "Kisan", "9811122233", "kisan@agrosmart.in", "ROLE_FARMER", "token_1");
        User admin = new User(11L, "SuperAdmin", "9844455566", "admin@agrosmart.in", "ROLE_ADMIN", "token_2");

        assertEquals("ROLE_FARMER", farmer.getRole());
        assertEquals("ROLE_ADMIN", admin.getRole());
    }
}
