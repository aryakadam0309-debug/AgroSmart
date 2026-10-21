package com.agrosmart.backend.payload;

public class AuthDto {
    public static class LoginRequest {
        public String phone;
        public String password;
    }

    public static class RegisterRequest {
        public String fullName;
        public String phone;
        public String email;
        public String password;
        public String role; // "ROLE_FARMER", "ROLE_ADMIN"
        public String village;
        public String district;
        public String state;
        public Double landAreaAcres;
    }

    public static class AuthResponse {
        public boolean success;
        public String token;
        public String role;
        public String fullName;
        public String phone;
        public String message;

        public AuthResponse(boolean success, String token, String role, String fullName, String phone, String message) {
            this.success = success;
            this.token = token;
            this.role = role;
            this.fullName = fullName;
            this.phone = phone;
            this.message = message;
        }
    }
}
