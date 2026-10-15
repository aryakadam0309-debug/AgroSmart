package com.agrosmart.models;

import java.io.Serializable;

public class User implements Serializable {
    private Long id;
    private String fullName;
    private String phoneNumber;
    private String email;
    private String role;
    private String token;
    private String farmLocation;
    private Double farmSizeAcres;
    private String profileImageUrl;

    public User() {}

    public User(Long id, String fullName, String phoneNumber, String email, String role, String token) {
        this.id = id;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.role = role;
        this.token = token;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getPhone() { return phoneNumber; }
    public void setPhone(String phone) { this.phoneNumber = phone; }

    public String getLocation() { return farmLocation; }
    public void setLocation(String location) { this.farmLocation = location; }

    public Double getFarmSize() { return farmSizeAcres != null ? farmSizeAcres : 0.0; }
    public void setFarmSize(Double farmSize) { this.farmSizeAcres = farmSize; }

    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }
}

